# Referencje: Mealio

Data: 2026-10-21. Referencje i decyzje wybierał oraz zatwierdzał właściciel
produktu w rozmowie; agent porządkował materiał i opisał obserwacje.

**Słowa charakteru — potwierdzone:** kolorowy, osobisty, praktyczny.
**Antycharakter — potwierdzony:** nudny, reklamowy, przeładowany.

Nic w tym pliku nie jest jeszcze regułą implementacji. Zasady, tokeny i komponenty
powstają w następnym kroku, z odwołaniem do numerów cech; każdą z nich właściciel
zatwierdza osobno. Nie wybrano wartości kolorów, fontów, odstępów ani animacji.

## Kontekst i źródła

Mealio jest aplikacją iOS do prowadzenia dziennika jedzenia, zapisywania produktów
i ponownego używania własnych przepisów. Właściciel chce ograniczyć powtarzane
wyszukiwanie i skanowanie składników. Współdzielenie pozostaje poza MVP.

- [Brief Mealio](../.ai/specs/product-brief.md).
- [Relacja właściciela o dzienniku](../.ai/specs/research/interviews/2026-10-20-owner-food-diary.md).
- [Doprecyzowanie osobistego MVP](../.ai/specs/research/interviews/2026-10-20-owner-personal-mvp.md).
- [Decyzje dziennika i przepisów](../.ai/specs/research/decisions/2026-10-20-diary-and-recipe-details.md).
- Rozmowa prowadząca do tego dokumentu: źródło potwierdzeń wyglądu, nawigacji,
  personalizacji, braku reklam i bezpłatnych funkcji. Nie jest pomiarem użyteczności.

R01 to dwa dostarczone przez właściciela zrzuty Fitatu: dziennik oraz dodawanie
do posiłku. Oba zostały otwarte i obejrzane. Pokazują jeden stan aplikacji;
wersja Fitatu i data wykonania zdjęć nie zostały ustalone. Zdjęcia są prywatne,
lokalne i wyłączone z Git. Nie wolno ich dołączać do commitów, PR-ów ani publikować
na GitHubie. Dokument nie przepisuje prywatnych wartości z dziennika.

Źródła publiczne, otwarte podczas porównania, dostęp 2026-10-21:

- F1: [Posiłki w Fitatu](https://www.fitatu.com/pl/help/czym-sa-posilki-w-fitatu/).
- F2: [Dodawanie produktu lub potrawy](https://www.fitatu.com/pl/help/jak-dodac-produkt-lub-potrawe-do-jadlospisu).
- F3: [Własny produkt lub potrawa](https://www.fitatu.com/pl/help/jak-dodac-wlasny-produkt-lub-potrawe).
- F4: [Zapisanie posiłku jako potrawy](https://www.fitatu.com/pl/help/czy-moge-zapisac-na-przyszlosc-caly-posilek).

Dokumentacja potwierdza przepływy, a nie dokładny wygląd aktualnej wersji iOS.
GitHub i Slack zostały zastąpione Fitatu za zgodą właściciela; nie są referencjami
Mealio. Zachowano antyreferencję generycznego wyglądu AI.

## Referencje

Waga „musi być” oznacza jawny wymóg właściciela. „Nieustalona” oznacza brak
ustalenia priorytetu, a nie niską ważność. Obserwacje oznaczone jako „opis
właściciela” nie są obserwacjami ekranu. Każdy wiersz obejmuje jedną cechę;
odrzucenia odnoszą się tylko do niej. Nie dopisano niezatwierdzonych odrzuceń.

| # | Referencja | Źródło | Kto wybrał | Obserwacja | Bierzemy | Nie bierzemy | Robimy zamiast tego | Dlaczego pasuje do produktu | Rodzina | Waga | Status (propozycja / przyjęte) |
|---|---|---|---|---|---|---|---|---|---|---|---|
| R01.1 | Fitatu — dziennik | Zrzut dziennika; F1; rozmowa | Właściciel | Na jednym ekranie są sekcje posiłków oraz podsumowanie energii i makr; na zrzucie podsumowanie jest nad dolną nawigacją. | Dziennik i bilans dnia jako punkt startowy. | Umieszczenia bilansu dopiero za pełną listą posiłków, jak w dotychczasowej makiecie Mealio. | Bilans dnia przed sekcjami posiłków. | Pierwsze zadanie to sprawdzenie zapisanego jedzenia i wyniku dnia. | kompozycja i gęstość | musi być | przyjęte |
| R01.2 | Fitatu — sekcje posiłków | Zrzut dziennika; F1; rozmowa | Właściciel | Posiłki tworzą pionową listę nazw; przy pustym posiłku widoczna jest zerowa kaloryczność. | Podział dziennika na własne sekcje. | Narzuconego, niezmiennego podziału dnia. | Domyślne: Śniadanie, Drugie śniadanie, Lunch, Obiad, Kolacja, Przekąski; użytkownik może zmieniać nazwy i liczbę sekcji. | Właściciel chce dopasować dziennik do własnego sposobu jedzenia. | komponenty | musi być | przyjęte |
| R01.3 | Fitatu — dodawanie do sekcji | Oba zrzuty; F2; rozmowa | Właściciel | Po prawej stronie nagłówka posiłku jest okrągła kontrolka z plusem; drugi zrzut zachowuje nazwę wybranego posiłku w nagłówku. | Plus przypisany do konkretnego posiłku. | Dodawania wymagającego ponownego wskazania sekcji, z której rozpoczęto operację. | Dodawanie zachowuje wybrany posiłek i wraca do jego kontekstu. Nie ustalono kształtu ani koloru kontrolki. | Ogranicza zbędne wybory przy częstym zapisywaniu jedzenia. | komponenty | musi być | przyjęte |
| R01.4 | Fitatu — metody znalezienia produktu | F2; rozmowa | Właściciel | Dokumentacja opisuje wejście plusem, wyszukiwanie lub skan kodu, wybór ilości i zapis. Zrzut pokazuje wyszukiwarkę, ale nie cały przebieg skanowania. | Wyszukiwanie i skanowanie dostępne w przepływie dodawania. | Ponownego skanowania jako koniecznego warunku użycia zapisanego przepisu. | Zapisany przepis można wybrać z biblioteki. | Składniki powtarzających się dań nie powinny wymagać odtwarzania za każdym razem. | ruch i reakcja | musi być | przyjęte |
| R01.5 | Mealio — zawartość posiłku wobec Fitatu | Opis właściciela; brief | Właściciel | Zrzut Fitatu pokazuje pusty dziennik, więc nie pozwala zaobserwować wierszy zjedzonych pozycji. Właściciel opisał oczekiwany układ Mealio. | Lista produktów i dań/przepisów z ilością, kaloriami i makrami każdej pozycji. | Zastąpienia listy samą sumą posiłku. | Widoczne pozycje oraz suma energii i wszystkich makr całego posiłku. | Pozwala sprawdzić zarówno źródło bilansu, jak i wynik posiłku. | komponenty | musi być | przyjęte |
| R01.6 | Fitatu — dolna nawigacja | Zrzut dziennika; rozmowa | Właściciel | Przy dolnej krawędzi znajduje się pasek ikon z etykietami; obejmuje m.in. jadłospis i przepisy, ale też obszary wykraczające poza MVP Mealio. | Dolny pasek nawigacji iOS z nazwami obszarów. | Kopiowania pełnego zestawu zakładek Fitatu. | Dziennik, Przepisy, Produkty. | To uzgodnione główne obszary osobistego MVP. | komponenty | musi być | przyjęte |
| R01.7 | Fitatu — kolor i czytelność danych | Oba zrzuty; opis właściciela | Właściciel | Na zrzutach jasnozielony nagłówek i kontrolki kontrastują z ciemnym obszarem treści. Właściciel chce koloru głównie dla makroskładników i posiłków. | Kolorowy charakter z kolorem przypisanym do informacji. | Kopiowania palety Fitatu ani traktowania ciemnego tła jako zatwierdzonego wyboru Mealio. | Paleta do osobnej decyzji; kolory mają przede wszystkim pomagać rozróżniać makra i posiłki. | Wspiera orientację w danych i odpowiada potwierdzonemu oczekiwaniu wyglądu. | kolor | musi być | przyjęte |
| R01.8 | Mealio — obrazy przepisów | Opis właściciela | Właściciel | Na dostarczonych zrzutach nie ma biblioteki przepisów. Właściciel dopuścił ilustracje przy przepisach dodawanych przez użytkowników. | Możliwość zdjęcia lub ilustracji własnego przepisu. | Dekoracyjnych obrazów sugerujących dane, których użytkownik nie dodał. | Sposób prezentacji przepisu bez obrazu pozostaje do ustalenia. | Obraz może ułatwiać rozpoznanie własnego dania; skuteczności nie zmierzono. | komponenty | nieustalona | przyjęte |
| R01.9 | Fitatu — ponowne użycie własnych dań | F3, F4; brief; relacja właściciela | Właściciel | Dokumentacja Fitatu pozwala zapisać własną potrawę i ponownie ją dodawać; cały posiłek można zachować przez „Zapisz jako”. To nie jest brak funkcji Fitatu. | Ponowne używanie zapisanych przepisów i zapis pozycji dziennika jako przepisu. | Konieczności ponownego wprowadzania tych samych składników. | Wybór własnego wariantu przepisu, następnie spożytej ilości. | Odpowiada opisanej trudności powtarzania pracy. | ruch i reakcja | musi być | przyjęte |
| R01.10 | Mealio — własna klasyfikacja przepisów | Opis właściciela; rozmowa | Właściciel | Właściciel podał przykład „Śniadania → Jajecznice” z kilkoma zapisanymi wariantami; sekcje dziennika i klasyfikacja przepisów mają być deklarowane osobno przez użytkownika. | Własna klasyfikacja pomagająca wybrać zapisany wariant przepisu. | Utożsamiania kategorii przepisu z sekcją konkretnego dnia. | Niezależna organizacja biblioteki i dziennika; model hierarchii pozostaje w lukach. | Użytkownik ma znajdować własne dania, zamiast ponownie szukać składników w katalogu. | komponenty | musi być | przyjęte |
| R01.11 | Fitatu — reklama w dzienniku | Zrzut dziennika; rozmowa | Właściciel | Między kalendarzem a listą posiłków jest kontrastowy baner oferty Premium z odliczaniem. | Bezpośredni dostęp do zadania dziennika. | Reklam i banerów sprzedażowych. | Dziennik bez reklam; funkcje Mealio mają być bezpłatne. | Jawna preferencja właściciela osobistego produktu; nie ustalono modelu finansowania. | kompozycja i gęstość | musi być | przyjęte |
| R01.12 | Fitatu — ekran dodawania | Zrzut dodawania; zaakceptowana propozycja agenta | Agent; zatwierdził właściciel | Nad listą produktów znajdują się zakładki, wyszukiwarka, wejścia głos/zdjęcie/tekst oraz kolejny rząd filtrów; pod listą są dodatkowe akcje. | Dostęp do zadania wyboru jedzenia. | Wielu równorzędnych kontrolek konkurujących o uwagę. | Praktyczny ekran wyboru; dokładne rozmieszczenie metod i filtrów wymaga projektu. | Właściciel wskazał długie dodawanie jako problem. Sama liczba kontrolek nie dowodzi zmierzonego spowolnienia. | kompozycja i gęstość | nieustalona | przyjęte |
| R01.13 | Mealio — trafność wyszukiwania | Relacja właściciela w rozmowie | Właściciel | Właściciel zgłasza niepożądane produkty w wynikach Fitatu. Zrzut nie pokazuje zapytania, które pozwala ocenić trafność. | Ułatwienie dotarcia do zapisanych własnych dań. | Traktowania powtarzanego wyszukiwania składników jako jedynej drogi. | Biblioteka własnych przepisów dostępna w dodawaniu; ranking wyników katalogu pozostaje do ustalenia. | Odnosi się do konkretnej opisanej przeszkody, bez nieudowodnionej obietnicy lepszego wyszukiwania. | ruch i reakcja | musi być | przyjęte |

## Antyreferencje

| # | Wygląd | Dlaczego nie | Podpis do uniknięcia | Źródło (gust / badania / propozycja) |
|---|---|---|---|---|
| A01.1 | Generyczny wygląd AI — fioletowy gradient | Właściciel odrzucił wygląd typowej strony startupu AI. | Ekran: fioletowe przejścia barw jako dominująca dekoracja. Kod: gradienty użyte jako motyw tła lub akcji bez powiązania z danymi Mealio. Zamiast tego: kolor przede wszystkim rozróżnia makra i posiłki, R01.7. | gust — karta A01, potwierdzona |
| A01.2 | Generyczny wygląd AI — szklane karty | Ten sam odrzucony motyw startupowy. | Ekran: powtarzalne półprzezroczyste panele z rozmyciem. Kod: rozmycie i przezroczystość stosowane jako obudowa większości treści. Zamiast tego: czytelna lista pozycji, R01.5. Nie jest to zakaz natywnego materiału systemowego w nawigacji iOS. | gust — karta A01, potwierdzona |
| A01.3 | Generyczny wygląd AI — iskierki | Ten sam odrzucony motyw; AI pozostaje poza MVP. | Ekran: iskierki i dekoracyjne oznaczenia AI przy głównych akcjach. Kod: powtarzany symbol lub asset iskierki jako wyróżnik dodawania. Zamiast tego: kontekstowy plus, R01.3. | gust oraz zakres briefu — potwierdzone |
| A02 | Reklamowy dziennik | Właściciel chce aplikacji bez reklam i bezpłatnych funkcji. | Ekran: baner oferty z odliczaniem pomiędzy bilansem, datą i jedzeniem. Kod: slot reklamowy lub komponent oferty w podstawowym przepływie dziennika. Zamiast tego: przestrzeń przeznaczona na jedzenie i bilans, R01.11. | gust — potwierdzone; obserwacja zrzutu |
| A03 | Przeładowany ekran dodawania | Właściciel zaakceptował antycharakter „przeładowany”. | Ekran: kilka rzędów równorzędnych zakładek, filtrów i metod wejścia przed wynikami. Kod: wiele stale widocznych grup kontrolek przed listą wyboru. Zamiast tego: hierarchia wyboru do zaprojektowania, R01.12. | propozycja agenta — przyjęta przez właściciela |
| A04 | Nudny, monotonny wygląd | Właściciel tak ocenia Fitatu; to preferencja, nie wynik testu innych osób. | Do uniknięcia: brak wizualnego rozróżnienia makr i posiłków, gdy prezentowane są razem. Nie wyciągamy z tego zakazu szarości ani obowiązku kolorowania każdej powierzchni. Zamiast tego: R01.7; dokładny podpis palety wymaga projektu. | gust — potwierdzone; interpretacja operacyjna do dalszej weryfikacji |
| A05 | Dodawanie wymagające odtwarzania własnego dania | Relacje opisują powtarzane wyszukiwanie i skanowanie. Nie ustalono, czy przyczyną są ograniczenia Fitatu, konfiguracja czy sposób korzystania. | Ekran: przy ponownym spożyciu brak wejścia do własnych zapisanych wariantów. Kod: dodawanie obsługuje wyłącznie nowe wyszukiwanie/skan, bez wyboru zapisanego przepisu. Zamiast tego: R01.9–R01.10. | badania — relacja jednego właściciela; kierunek potwierdzony w rozmowie |
| A06 | Wygląd rozbudowanego portalu funkcji dodatkowych | Właściciel potwierdził skupienie MVP na dzienniku, produktach i przepisach. | Ekran: zakładki i promocje postu, wyzwań, nawyków lub funkcji AI konkurują z zadaniem wpisania jedzenia. Kod: te obszary dodane do głównej nawigacji MVP. Zamiast tego: R01.6. | propozycja agenta oparta na zakresie — przyjęta przez właściciela |

## Konflikty i rozstrzygnięcia

| Konflikt | Referencje | Rozstrzygnięcie | Kto zdecydował |
|---|---|---|---|
| Brief wskazany w szablonie a produkt w repozytorium | Pierwotny prompt DevMentor; brief Mealio | Dokument dotyczy Mealio. Sesje tekstowe mentoringu nie są kontekstem produktu. | Właściciel |
| GitHub/Slack a zadania użytkownika Mealio | Pierwotne R01/R02; obecne R01 | Referencją jest Fitatu; GitHub i Slack usunięto z zestawu. | Właściciel |
| Bilans na dole zrzutu Fitatu a priorytet ekranu Mealio | R01.1 | Mealio zaczyna od dziennika z bilansem dnia przed posiłkami. | Właściciel |
| Różnorodność wizualna a rozbudowa zakresu | R01.7, R01.12; A06 | „Nie monotematyczny” oznacza wygląd; nie rozszerza MVP. | Właściciel |
| Brak współdzielenia jako problem a MVP bez kont | Relacje; brief; rozmowa | Współdzielenie jest potrzebą późniejszego etapu, nie funkcją MVP ani dowodem braku funkcji Fitatu. | Właściciel |
| Personalizacja sekcji dnia a klasyfikacja przepisów | R01.2, R01.10 | Dwa niezależne podziały definiowane przez użytkownika. | Właściciel |
| Płaskie etykiety w briefie a przykład „Śniadania → Jajecznice” | Brief R15; R01.10 | Potwierdzono własną klasyfikację, lecz nie rozstrzygnięto jednoznacznie modelu: etykiety, hierarchia czy oba. Nie zmieniono briefu ani modelu danych. Propozycja: rozstrzygnąć w projekcie biblioteki na przykładach właściciela. | Otwarte; wymaga decyzji właściciela |
| Potrzeba ponownego użycia a przekonanie, że Fitatu go nie ma | Relacje; F3, F4 | Dokumentacja Fitatu potwierdza własne potrawy i „Zapisz jako”. Problem właściciela pozostaje relacją o doświadczeniu, nie porównawczym brakiem funkcji. | Ustalenie ze źródeł; potrzeba Mealio potwierdzona przez właściciela |
| Kolorowa aplikacja a nieznana paleta | R01.7; A01 | Potwierdzono role koloru; nie zatwierdzono palety Fitatu, ciemnego motywu ani konkretnych wartości. | Właściciel określił role; wartości pozostają otwarte |

## Luki

- **Typografia:** brak zaakceptowanej wizualnej referencji. Wcześniej przyjęto
  kierunek natywnego iOS i czytelnej typografii, ale nie wybrano krojów, stylów,
  rozmiarów ani wag. Nie wyprowadzamy ich ze skompresowanych zdjęć.
- **Kolor:** otwarte są konkretne wartości, przypisanie kolorów do makr i posiłków,
  kontrast oraz warianty jasny/ciemny. Kolor nie ma jeszcze specyfikacji tokenów.
- **Kompozycja i gęstość:** brak docelowej referencji wypełnionego dziennika;
  zrzut Fitatu pokazuje puste posiłki. Układ kalorii/makr przy długiej nazwie,
  dużym tekście i wielu pozycjach wymaga osobnego projektu.
- **Komponenty:** nie ustalono modelu klasyfikacji przepisów, prezentacji przepisu
  bez zdjęcia ani dokładnej hierarchii kontrolek ekranu dodawania.
- **Ruch i reakcja:** potwierdzono kontekstowe dodawanie i ponowne użycie;
  brak referencji animacji, haptyki, przejść, ładowania, błędów i pustych wyników.
- **Ton wypowiedzi:** brak referencji i zatwierdzonych cech języka interfejsu.
- **Trafność wyszukiwania:** nie wiadomo, jakie zapytania dają niepożądane wyniki.
  Nie ustalono rankingu ani filtrowania. Potrzebne przykłady przed obietnicą poprawy.
- **Wagi:** dla ilustracji przepisów i szczegółów uproszczenia ekranu dodawania
  nie ustalono priorytetu. Nie dopisano go za właściciela.
- **Bezpłatność:** potwierdzona jako oczekiwanie; nie określa modelu finansowania
  ani kosztów zewnętrznych dostawców. Ten dokument ich nie rozstrzyga.
- **Dostępność:** zdjęcia nie potwierdzają kontrastu, rozmiarów celów dotykowych,
  Dynamic Type ani VoiceOver. Audyt klienta iOS pozostaje osobnym krokiem.

## Obrazy

**Brak obrazów obok dokumentu w wersji Git — świadomy wyjątek wynikający z zakazu
publikacji właściciela.** Oryginały pozostają lokalnie w głównym katalogu Mealio
i są wyłączone przez `.git/info/exclude`. Poniższe nazwy są identyfikatorami
referencyjnymi, nie deklaracją utworzenia nowych plików ani zmiany formatu:

- `R01-dziennik.png` — logiczna nazwa obejrzanego zrzutu dziennika Fitatu;
  lokalny oryginał JPEG: `3D1A3D54-73D3-46D8-BE07-B77183F747DC_4_5005_c.jpeg`.
- `R01-dodawanie-posilku.png` — logiczna nazwa obejrzanego zrzutu dodawania;
  lokalny oryginał JPEG: `91BFA280-DB94-461E-821D-A0D88C24892E_4_5005_c.jpeg`.

Nie przenosić oryginałów do śledzonego katalogu ani nie dodawać ich przez
`git add -f`. Osoba bez lokalnych zdjęć może odczytać opis decyzji, ale nie może
przeprowadzić niezależnej weryfikacji wyglądu na podstawie samego repozytorium.
