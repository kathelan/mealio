# Mealio — osobisty dziennik i przepisy MVP

- Data: 2026-10-20.
- Status: zatwierdzony zakres produktu; wymaga specyfikacji technicznych przed implementacją.
- Właściciel: właściciel produktu prowadzący rozmowę.
- Źródło: sesja `grill-me`, potwierdzenie podsumowania Q35 oraz dalszych prac nad briefem i backlogiem.
- Zapis decyzji: [kierunek MVP](research/decisions/2026-10-20-personal-mvp.md).

## Cel i granice

Ułatwić właścicielowi codzienne rejestrowanie jedzenia bez ponownego skanowania i odtwarzania
potraw. Docelowo aplikacja służy również drugiej osobie w gospodarstwie domowym; MVP ma jeden
dziennik, bez użytkowników i współdzielenia. Nie jest projektem opartym na monetyzacji ani
walidacji popytu szerokiego rynku. Relacja właściciela nie stanowi niezależnego badania drugiej osoby.

Dokument opisuje docelowe zachowanie, nie stan istniejącego backendu. Nie zatwierdza endpointów,
migracji, algorytmu zapotrzebowania ani implementacji. Rozdzielenie na wdrażalne specyfikacje
techniczne następuje przed zmianami kodu.

## Dziennik

- Osobna strona/zakładka, domyślnie wybrany dzień; przełączenie na tydzień pokazuje wykres dziennych kcal. Brak wpisów nie oznacza potwierdzonego braku spożycia, a niekompletne dane są jawne.
- Dowolna liczba i nazwy sekcji posiłków. Domyślnie: śniadanie, drugie śniadanie, obiad,
  lunch, kolacja i przekąski. Nazwy i liczba nie są zamkniętą listą domenową.
- Do sekcji można dodawać produkty ręcznie lub skanerem oraz zapisane przepisy. Zmiana nazwy obowiązuje także w historii. Usunięcie usuwa sekcję z wyboru, zachowując wcześniejsze wpisy pod jej ostatnią nazwą; brak przywracania. Nowa o tej samej nazwie jest odrębna. Zmiana kolejności pozostaje propozycją.
- Przepis widnieje jako jeden wpis: nazwa, kcal i makroskładniki. Nie rozwija się na osobne
  wpisy produktów. Historia zachowuje wartości użyte w chwili zapisu.
- Domyślnie dodawana jest jedna porcja; można wybrać np. pół albo dwie porcje.

## Przepisy i etykiety

- Osobna zakładka do tworzenia i wyszukiwania przepisów. Składniki wybiera się z produktów
  albo skanuje, podając ilości. Przepis ma nazwę i obowiązkową dodatnią liczbę porcji, także 1.
- Wiele opcjonalnych własnych etykiet wybiera się z listy; można nie przypisać żadnej. Startowe Śniadanie, Obiad, Kolacja, Przekąska można zmieniać i uzupełniać. Filtry działają w bibliotece i przy wybieraniu przepisu, z opcją wszystkich przepisów. Etykiety są niezależne od sekcji dziennika,
  bez obowiązkowej hierarchii i podkategorii posiłków. Jajecznica może trafić do dowolnej sekcji.
- Ragu, bolognese i carbonara mogą być oddzielnymi przepisami.
- Własna baza sosu może być składnikiem innego przepisu w g/ml z jawnym przelicznikiem. Jego źródło pozostaje otwarte (Q06). Bulion odkładamy do kolejnej fazy. Zmiana/usunięcie bazy nie zmienia wersji użytej w istniejącym przepisie; nową wersję wybiera się świadomie. SDD określi ochronę przed cyklami.
- Wskazane pozycje dziennika można zapisać jako przepis bez ponownego dodania spożycia.
  Przepis można również utworzyć niezależnie od dziennika.
- W MVP brak publikacji, moderacji i obowiązkowych zdjęć.

## Ilości, parametry i obliczenia

Wartości całego przepisu pochodzą ze składników i ich ilości, np. suchego makaronu lub surowego
mięsa zgodnie z danymi produktu. Dzielimy je przez liczbę porcji. Końcowa waga potrawy jest
opcjonalna: dodana woda zmienia masę i wynik na 100 g, ale nie sumę kalorii garnka.
Nie zastępujemy danych konkretnego produktu ogólnym założeniem, że wszystko ważone jest na sucho.

Przy składniku można zaznaczyć checkbox zmiennego inputu i wybrać gramaturę albo liczbę sztuk. Podczas dodawania przepisu podaje się ilość
na jedną porcję, np. liczbę jajek. Pozostałe składniki korzystają z ilości na porcję wynikających
z zapisanego przepisu. Liczba zjadanych porcji mnoży te ilości; nie zmienia bazowego przepisu.
Nie ma osobnego systemu formuł ani ekranów definiowania parametrów.

Przed zapisem spożycia można zwyczajnie zmienić ilość, zamienić produkt, dodać lub usunąć
składnik. Zmiany dotyczą tego dodania. Osobna świadoma akcja aktualizuje przepis albo zapisuje
nowy. Checkbox usprawnia częste zmiany ilości, podmiana produktu jest zwykłą edycją.
Po zapisaniu można zmieniać ilość produktu, porcje i skład konkretnego spożycia przepisu, także w starszych dniach. Zmiana dotyczy tylko tego wpisu i jego bilansu. Wpis można przenieść do innej sekcji/daty bez zmiany wartości oraz usunąć z Cofnij. Wybór wersji danych przy świadomej korekcie starego wpisu wymaga SDD (Q05).

Przykłady akceptacyjne:

1. Przepis na 4 porcje ma 2000 kcal: jedna porcja daje 500 kcal bez końcowej masy potrawy.
2. Zmiana samej ilości wody nie dodaje kalorii ani nie zmienia wartości równej porcji.
3. Jajko ma jawny przelicznik 1 szt. = 55 g: 3 szt. odpowiadają 165 g danych tego produktu.
4. Przy parametrze 3 jajka na porcję, dodanie 2 porcji daje 6 jajek w obliczeniu jednego wpisu.
5. Aktualizacja przepisu lub produktu nie zmienia kcal i makro wcześniejszego wpisu.

## Produkty

Raz rozpoznany i zatwierdzony skan jest zapamiętywany do następnego użycia. Nie wymaga ciągłego
szukania tego samego produktu. Własny produkt może mieć jawny przelicznik sztuk na g, np.
jajko z indywidualnie ustawioną masą. Dane odżywcze pochodzą z katalogu lub jawnie wprowadzonych
danych; AI nie ustala ich wartości. Nie zakładamy 1 ml = 1 g; brak danych nie oznacza zera.

Docelowo własny produkt należy do właściciela i nie jest automatycznie udostępniany.
MVP bez tożsamości ma jedną lokalną bibliotekę, zgodnie z ADR-0003; nie zapewnia izolacji osób.
Zewnętrzny produkt może wymagać potwierdzenia przez korzystającego zgodnie z ADR-0007:
to odrębne od niepotrzebnej moderacji przepisów przez administratora.

## Cel kalorii

Na początek wyliczenie ze wzoru i ręczna korekta. W MVP można aktualizować masę ciała w ustawieniach jednego dziennika i ponownie wyliczać propozycję celu. Nowa propozycja wymaga zatwierdzenia, z możliwością zachowania ręcznej korekty; wcześniejsze dni zachowują wcześniejszy cel. Nie dodajemy kont ani userId. Obecna potrzeba to redukcja, następnie
utrzymanie lub rekompozycja. Samo określenie rekompozycji nie ustala nowego algorytmu.
Wybór wzoru, danych wejściowych i modelu aktywności pozostaje otwarty. Można planować
podstawowy przepływ jedzenia bez tych decyzji; implementacja kalkulatora wymaga ich rozstrzygnięcia.

## Poza MVP

Konta, auth, zaproszenia, wybór osoby do współdzielenia, wiele dzienników, Garmin oraz AI.
Nie usuwamy istniejącego ASR ani parsera w ramach dokumentacji. Nie dodajemy userId,
technicznego użytkownika ani fikcyjnej autoryzacji.

## Otwarte kwestie przed implementacją odpowiedniego zakresu

- Kalkulator: wzór, dane wejściowe, model aktywności; właściciel zatwierdza propozycję opartą na źródłach.
- Nierozpoznany kod: ręczny fallback jest potwierdzony przez E01-C1/D13; szczegóły implementacji opisują SDD katalogu/skanera.
- Bazy sosów: źródło jawnego przelicznika g/ml i zachowanie przy jego braku (Q06), cykle; zasady wersji/usunięcia są potwierdzone. Bulion później.
- Korekta starych wpisów: wybór wersji danych i uzupełnianie braków (Q05).
- Projekt UI, błędy, walidacje, API, migracje i zaokrąglenia należą do poszczególnych SDD.

## Etapy przygotowania wdrożenia

1. Produkty i obliczenia: dopracować katalog, zapamiętane skany i własne przeliczniki.
2. Przepisy: porcje, płaskie etykiety, parametry ilości i bazy przepisów.
3. Dziennik i UI: własne sekcje, pojedyncze wpisy przepisu, dzień/tydzień, zapis jako przepis.
4. Cele: osobna SDD po wyborze wzoru; ręczna korekta jest wymagana.

To grupy prac do specyfikacji, nie narzucona kolejność implementacji. Dawna kolejność
D01–D06 nie jest nowym harmonogramem; szczegóły przydziela odświeżony backlog.
ADR-0004 uzgodniono z zakresem; przed kodem trzeba uzgodnić ADR-0005 dla PIECE, zachować ADR-0003,
OpenAPI i granice modułów. Weryfikacja kodu obejmuje 100% linii jednostkowych,
integracje PostgreSQL, testy architektury i pełne `mvn clean verify`.

Uzupełnienie zakresu: [decyzje sesji grilling](research/decisions/2026-10-20-diary-and-recipe-details.md), brief D18–D24/R12–R18.
