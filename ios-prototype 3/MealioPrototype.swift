import SwiftUI

@main
struct MealioPrototypeApp: App {
    var body: some Scene { WindowGroup { MealioRootView() } }
}

struct Food: Identifiable, Hashable {
    let id = UUID(); let name: String; let brand: String; let kcal: Int
    let protein: Double; let carbs: Double; let fat: Double; let basis: String
}
struct DiaryEntry: Identifiable { let id = UUID(); var food: Food; var amount: Double; var meal: String }

@MainActor final class MealioModel: ObservableObject {
    @Published var entries = [DiaryEntry(food: Food(name:"Owsianka z bananem", brand:"Domowa", kcal:245, protein:8.2, carbs:42, fat:5.1, basis:"100 g"), amount:250, meal:"Śniadanie"), DiaryEntry(food: Food(name:"Jogurt naturalny", brand:"Piątnica", kcal:62, protein:4.3, carbs:4.7, fat:3.0, basis:"100 g"), amount:150, meal:"Drugie śniadanie")]
    @Published var selectedDate = Date.now
    var kcal: Int { entries.reduce(0) { $0 + Int(Double($1.food.kcal) * $1.amount / 100) } }
    var protein: Double { entries.reduce(0) { $0 + $1.food.protein * $1.amount / 100 } }
    var carbs: Double { entries.reduce(0) { $0 + $1.food.carbs * $1.amount / 100 } }
    var fat: Double { entries.reduce(0) { $0 + $1.food.fat * $1.amount / 100 } }
}

struct MealioRootView: View {
    @StateObject private var model = MealioModel()
    @State private var tab = 0
    var body: some View {
        TabView(selection:$tab) {
            DiaryView(model:model).tabItem { Label("Dziennik",systemImage:"calendar") }.tag(0)
            ProductSearchView(model:model).tabItem { Label("Produkty",systemImage:"magnifyingglass") }.tag(1)
            ProfileView().tabItem { Label("Więcej",systemImage:"line.3.horizontal") }.tag(2)
        }.tint(Color(red:0.12,green:0.43,blue:0.37))
    }
}

struct DiaryView: View {
    @ObservedObject var model: MealioModel
    @State private var route: AddRoute?
    @State private var showingManual = false
    private let meals = ["Śniadanie","Drugie śniadanie","Obiad","Podwieczorek","Kolacja","Inne"]
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(alignment:.leading,spacing:18) {
                    HStack { VStack(alignment:.leading,spacing:4) { Text("Twój dzień").font(.largeTitle.bold()); Text("Jedz świadomie, bez presji").foregroundStyle(.secondary) }; Spacer(); Image(systemName:"person.crop.circle").font(.title2).foregroundStyle(.secondary) }
                    DatePicker("Dzień",selection:$model.selectedDate,displayedComponents:.date).datePickerStyle(.compact).padding(12).background(.white,in:RoundedRectangle(cornerRadius:16))
                    NutritionCard(model:model)
                    HStack { Text("Posiłki").font(.title2.bold()); Spacer(); Button { route = .text } label: { Label("Opisz posiłek",systemImage:"text.bubble").font(.subheadline.weight(.semibold)) } }
                    ForEach(meals,id:\.self) { meal in
                        let items = model.entries.filter{$0.meal == meal}
                        VStack(alignment:.leading,spacing:10) {
                            HStack { Text(meal).font(.headline); Spacer(); Text("\(items.reduce(0){$0 + Int(Double($1.food.kcal)*$1.amount/100)}) kcal").font(.subheadline).foregroundStyle(.secondary); Button { showingManual = true } label: { Image(systemName:"plus.circle.fill").font(.title3) } }
                            if items.isEmpty { Text("Dodaj pierwszy produkt do tego posiłku").font(.subheadline).foregroundStyle(.secondary).padding(.vertical,8) }
                            ForEach(items) { entry in EntryRow(entry:entry) }
                        }.padding(14).background(.white,in:RoundedRectangle(cornerRadius:18))
                    }
                    HStack(spacing:12) {
                        QuickAction(title:"Skanuj kod",icon:"barcode.viewfinder",color:.green){ route = .scanner }
                        QuickAction(title:"Dodaj produkt",icon:"plus",color:.teal){ showingManual = true }
                    }
                }.padding(16)
            }.background(Color(red:0.95,green:0.96,blue:0.94)).toolbar(.hidden,for:.navigationBar)
            .sheet(isPresented:$showingManual) { ProductSearchView(model:model,asSheet:true) }
            .sheet(item:$route) { route in switch route { case .scanner: ScannerFlow(model:model); case .text: TextMealFlow(model:model) } }
        }
    }
}
enum AddRoute: String, Identifiable { case scanner,text; var id:String { rawValue } }

struct NutritionCard: View {
    @ObservedObject var model: MealioModel
    var body: some View {
        VStack(alignment:.leading,spacing:14) {
            HStack(alignment:.firstTextBaseline) { Text("Dzisiaj").font(.headline); Spacer(); Text("\(model.kcal) / 2 000 kcal").font(.title3.bold()) }
            ProgressView(value:min(Double(model.kcal)/2000,1)).tint(Color(red:0.12,green:0.43,blue:0.37))
            HStack { MacroPill(name:"Białko",value:model.protein,goal:100,color:.orange); MacroPill(name:"Węglowodany",value:model.carbs,goal:250,color:.blue); MacroPill(name:"Tłuszcze",value:model.fat,goal:70,color:.purple) }
        }.padding(16).background(.white,in:RoundedRectangle(cornerRadius:20))
    }
}
struct MacroPill: View { let name:String; let value:Double; let goal:Double; let color:Color; var body: some View { VStack(alignment:.leading,spacing:5) { Text(name).font(.caption).foregroundStyle(.secondary); Text("\(Int(value)) / \(Int(goal)) g").font(.caption.bold()); ProgressView(value:min(value/goal,1)).tint(color) }.frame(maxWidth:.infinity,alignment:.leading) } }
struct EntryRow: View { let entry:DiaryEntry; var body: some View { HStack(spacing:12) { RoundedRectangle(cornerRadius:10).fill(Color.green.opacity(0.12)).frame(width:42,height:42).overlay(Image(systemName:"leaf.fill").foregroundStyle(.green)); VStack(alignment:.leading) { Text(entry.food.name).font(.subheadline.weight(.semibold)); Text("\(Int(entry.amount)) g · \(entry.food.brand)").font(.caption).foregroundStyle(.secondary) }; Spacer(); Text("\(Int(Double(entry.food.kcal)*entry.amount/100)) kcal").font(.caption.weight(.semibold)) } } }
struct QuickAction: View { let title:String; let icon:String; let color:Color; let action:()->Void; var body: some View { Button(action:action) { Label(title,systemImage:icon).font(.subheadline.weight(.semibold)).frame(maxWidth:.infinity).padding(14).background(color.opacity(0.1),in:RoundedRectangle(cornerRadius:14)) }.foregroundStyle(color) } }

struct ProductSearchView: View {
    @ObservedObject var model:MealioModel; var asSheet=false
    @Environment(\.dismiss) private var dismiss
    @State private var query=""; @State private var showManual=false
    private let foods=[Food(name:"Banan",brand:"Produkt ogólny",kcal:89,protein:1.1,carbs:22.8,fat:0.3,basis:"100 g"),Food(name:"Pierś z kurczaka",brand:"Produkt ogólny",kcal:165,protein:31,carbs:0,fat:3.6,basis:"100 g"),Food(name:"Chleb żytni",brand:"Piekarnia",kcal:259,protein:8.5,carbs:48,fat:3.3,basis:"100 g"),Food(name:"Skyr naturalny",brand:"Piątnica",kcal:63,protein:12,carbs:4,fat:0.2,basis:"100 g"),Food(name:"Jajko kurze",brand:"Produkt ogólny",kcal:143,protein:13,carbs:1.1,fat:9.5,basis:"100 g")]
    var results:[Food] { query.isEmpty ? foods : foods.filter{$0.name.localizedCaseInsensitiveContains(query)} }
    var body: some View {
        NavigationStack { VStack(spacing:0) {
            HStack { Image(systemName:"magnifyingglass").foregroundStyle(.secondary); TextField("Nazwa produktu lub kod kreskowy",text:$query).textInputAutocapitalization(.never); if !query.isEmpty { Button { query="" } label:{Image(systemName:"xmark.circle.fill").foregroundStyle(.secondary)} } }.padding(12).background(Color(.secondarySystemBackground),in:RoundedRectangle(cornerRadius:14)).padding()
            if results.isEmpty { ContentUnavailableView("Brak wyników",systemImage:"magnifyingglass",description:Text("Sprawdź nazwę lub dodaj produkt ręcznie.")); Button("Dodaj produkt ręcznie") { showManual=true }.padding() }
            else { List(results) { food in Button { model.entries.append(DiaryEntry(food:food,amount:100,meal:"Obiad")); dismiss() } label: { HStack { VStack(alignment:.leading,spacing:4){Text(food.name).foregroundStyle(.primary);Text(food.brand).font(.caption).foregroundStyle(.secondary)};Spacer();Text("\(food.kcal) kcal / 100 g").font(.caption).foregroundStyle(.secondary);Image(systemName:"plus.circle.fill").foregroundStyle(.teal) } }.listRowSeparator(.hidden) }.listStyle(.plain) }
            Button { showManual=true } label: { Label("Utwórz produkt ręcznie",systemImage:"square.and.pencil").frame(maxWidth:.infinity).padding() }.buttonStyle(.bordered).padding()
        }.navigationTitle("Dodaj produkt").navigationBarTitleDisplayMode(.inline).toolbar { ToolbarItem(placement:.topBarTrailing){Button("Zamknij"){dismiss()}} }.sheet(isPresented:$showManual){ ManualProductFlow(model:model) } }
    }
}

struct ManualProductFlow: View {
    @ObservedObject var model:MealioModel; @Environment(\.dismiss) private var dismiss
    @State private var name=""; @State private var kcal=""; @State private var protein=""; @State private var fat=""; @State private var carbs=""; @State private var basis="Na 100 g"; @State private var amount="100"
    var body: some View { NavigationStack { Form { Section("Produkt") { TextField("Nazwa produktu",text:$name); Picker("Wartości na",selection:$basis){Text("Na 100 g").tag("Na 100 g");Text("Na 100 ml").tag("Na 100 ml")}; TextField("Ilość (g lub ml)",text:$amount).keyboardType(.decimalPad) }; Section("Wartości odżywcze") { numeric("Energia · kcal",text:$kcal);numeric("Białko · g",text:$protein);numeric("Tłuszcz · g",text:$fat);numeric("Węglowodany · g",text:$carbs) }; Section { Text("Wpisz dane z etykiety. Brakujące wartości pozostaną nieznane.").font(.caption).foregroundStyle(.secondary) } }.navigationTitle("Nowy produkt").toolbar { ToolbarItem(placement:.topBarLeading){Button("Anuluj"){dismiss()}};ToolbarItem(placement:.topBarTrailing){Button("Zapisz"){save()}.disabled(name.trimmingCharacters(in:.whitespaces).isEmpty || Double(kcal)==nil)} } } }
    func numeric(_ title:String,text:Binding<String>)->some View { TextField(title,text:text).keyboardType(.decimalPad) }
    func save(){ let food=Food(name:name,brand:"Wpis ręczny · dane użytkownika",kcal:Int(Double(kcal) ?? 0),protein:Double(protein) ?? 0,carbs:Double(carbs) ?? 0,fat:Double(fat) ?? 0,basis:basis); model.entries.append(DiaryEntry(food:food,amount:Double(amount) ?? 100,meal:"Obiad"));dismiss() }
}

struct ScannerFlow: View {
    @ObservedObject var model:MealioModel; @Environment(\.dismiss) private var dismiss; @State private var stage=0; @State private var amount="100"
    let found=Food(name:"Płatki owsiane górskie",brand:"Przykładowy produkt",kcal:366,protein:13.5,carbs:60,fat:7,basis:"100 g")
    var body: some View { NavigationStack { VStack(spacing:22) { if stage==0 { Spacer(); RoundedRectangle(cornerRadius:28).fill(Color.black).frame(height:280).overlay { VStack(spacing:12){Image(systemName:"barcode.viewfinder").font(.system(size:90,weight:.light)).foregroundStyle(.white);Text("Umieść kod w ramce").foregroundStyle(.white)} }.padding(); Text("Skanowanie działa w prototypie jako symulacja.").font(.footnote).foregroundStyle(.secondary); Button("Zeskanuj przykładowy produkt"){stage=1}.buttonStyle(.borderedProminent); Button("Kod nie został rozpoznany"){stage=2};Spacer() } else if stage==1 { ProductConfirmation(food:found,amount:$amount); Button("Potwierdź i dodaj"){model.entries.append(DiaryEntry(food:found,amount:Double(amount) ?? 100,meal:"Obiad"));dismiss()}.buttonStyle(.borderedProminent); Button("To nie ten produkt"){stage=2} } else { ContentUnavailableView("Nie znaleziono produktu",systemImage:"barcode.viewfinder",description:Text("Możesz uzupełnić dane z etykiety.")); Button("Dodaj produkt z etykiety"){dismiss()} .buttonStyle(.borderedProminent) } }.navigationTitle("Skanuj kod").navigationBarTitleDisplayMode(.inline).toolbar { ToolbarItem(placement:.topBarLeading){Button("Zamknij"){dismiss()}} } } }
}
struct ProductConfirmation: View { let food:Food; @Binding var amount:String; var body:some View { VStack(alignment:.leading,spacing:14){Text("Sprawdź produkt").font(.title2.bold());Text(food.name).font(.headline);Text("\(food.brand) · wartości na 100 g").foregroundStyle(.secondary);TextField("Ilość w gramach",text:$amount).keyboardType(.decimalPad).textFieldStyle(.roundedBorder);HStack{Text("\(food.kcal) kcal");Spacer();Text("B \(food.protein, specifier:"%.1f") g · W \(food.carbs, specifier:"%.1f") g · T \(food.fat, specifier:"%.1f") g")}.font(.caption);Label("Źródło zewnętrzne — potwierdź dane",systemImage:"info.circle").font(.caption).foregroundStyle(.orange)}.padding(18).background(.white,in:RoundedRectangle(cornerRadius:18)).padding() } }

struct TextMealFlow: View {
    @ObservedObject var model:MealioModel; @Environment(\.dismiss) private var dismiss; @State private var text=""; @State private var parsed=false; @State private var selected=[true,true]
    let suggestions=[Food(name:"Jajko kurze",brand:"Produkt ogólny",kcal:143,protein:13,carbs:1,fat:10,basis:"100 g"),Food(name:"Chleb żytni",brand:"Produkt ogólny",kcal:259,protein:8.5,carbs:48,fat:3.3,basis:"100 g")]
    var body: some View { NavigationStack { ScrollView { VStack(alignment:.leading,spacing:18) { if !parsed { Text("Opowiedz, co zjadłeś").font(.title2.bold());Text("Wpisz posiłek własnymi słowami. Sprawdzisz propozycje przed zapisaniem.").foregroundStyle(.secondary);TextEditor(text:$text).frame(height:140).padding(8).background(Color(.secondarySystemBackground),in:RoundedRectangle(cornerRadius:14));Button("Pokaż propozycje"){parsed=true}.buttonStyle(.borderedProminent).disabled(text.trimmingCharacters(in:.whitespaces).isEmpty);Text("Przykład: 2 jajka i 2 kromki chleba").font(.caption).foregroundStyle(.secondary) } else { Text("Sprawdź propozycje").font(.title2.bold());Text("Dopasowania i porcje są szacunkowe. Popraw je przed dodaniem.").font(.subheadline).foregroundStyle(.secondary);ForEach(Array(suggestions.enumerated()),id:\.offset){index,food in Toggle(isOn:$selected[index]){VStack(alignment:.leading,spacing:5){Text(food.name).font(.headline);Text(index==0 ? "2 szt. · przyjęto 100 g" : "2 kromki · przyjęto 70 g").font(.caption).foregroundStyle(.secondary);Text("Dane: katalog Mealio · \(food.kcal) kcal / 100 g").font(.caption2).foregroundStyle(.secondary)}}.padding(14).background(.white,in:RoundedRectangle(cornerRadius:16))};Button("Potwierdź i dodaj wybrane"){for (i,food) in suggestions.enumerated() where selected[i]{model.entries.append(DiaryEntry(food:food,amount:i==0 ? 100:70,meal:"Obiad"))};dismiss()}.buttonStyle(.borderedProminent).disabled(!selected.contains(true));Button("Edytuj opis"){parsed=false} } }.padding() }.background(Color(.systemGroupedBackground)).navigationTitle("Dodaj z opisu").navigationBarTitleDisplayMode(.inline).toolbar { ToolbarItem(placement:.topBarLeading){Button("Zamknij"){dismiss()}} } } }
}
struct ProfileView: View { var body: some View { NavigationStack { List { Section("Preferencje") {Label("Jednostki · g i ml",systemImage:"scalemass");Label("Język · polski",systemImage:"globe")};Section("Informacje") {Label("Mealio · prototyp",systemImage:"leaf");Text("Dane w tym prototypie są przykładowe i pozostają w pamięci aplikacji.").font(.caption).foregroundStyle(.secondary)} }.navigationTitle("Więcej") } } }
