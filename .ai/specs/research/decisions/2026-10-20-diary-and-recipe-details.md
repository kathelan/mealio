# Decyzje właściciela — dziennik, parametry przepisów i cel kalorii

Data: 2026-10-20. Źródło: bieżąca rozmowa `grilling` z właścicielem produktu.
Właściciel zlecił aktualizację briefu, zadań w issues i lokalnego backlogu.
To potwierdzone decyzje produktowe, nie nowe badanie skuteczności ani wdrożenie.
Nie zapisujemy danych osobowych ani rzeczywistych pomiarów masy ciała.

## Potwierdzone decyzje

| Zakres / odpowiedzi | Ustalenie |
|---|---|
| Q1–Q4, korekta Q7 | Można poprawić ilość produktu, porcje i skład konkretnego spożycia przepisu, również we wcześniejszych dniach; zmiana dotyczy danego wpisu i bilansu jego dnia. Szablon i inne wpisy pozostają bez zmian. Wpis można przenieść między sekcjami lub datami, zachowując jego wartości, i usunąć z krótką możliwością Cofnij. |
| Q5, Q9–Q10 skorygowane przez Q14 | Zmiana nazwy sekcji obowiązuje także w historii. Interfejs ma Dodaj/Usuń, bez archiwum i przywracania. Po usunięciu sekcji wcześniejsze wpisy pozostają pod jej ostatnią nazwą; sekcja znika z wyboru przy dodawaniu. Nowa sekcja o tej samej nazwie jest osobną sekcją. |
| Korekta Q12, Q13 | W edytorze przepisu składnik wybiera się przez wyszukiwanie lub skan. Checkbox oznacza zmienny input; przy nim wybiera się gramaturę albo liczbę sztuk. Przy dodawaniu przepisu formularz pyta o wybrane pola na jedną porcję; liczba zjedzonych porcji je mnoży. Nie wprowadzamy obowiązkowej pary pól liczba sztuk i masa sztuki. Sztuki nadal wymagają jawnego przelicznika produktu. |
| Q18–Q20 | Przepis może mieć wiele opcjonalnych własnych płaskich etykiet wybieranych z listy albo żadną. Początkowe etykiety obejmują Śniadanie, Obiad, Kolacja, Przekąska; użytkownik może je zmieniać i uzupełniać. Filtrowanie działa w bibliotece i przy wyborze przepisu do dziennika; można pokazać wszystkie przepisy. Etykiety są niezależne od sekcji dnia. |
| Korekta Q21, Q22 | Domyślny widok jest dzienny. Można przełączyć się na tydzień z wykresem dziennych kcal. Dni bez wpisów oznaczamy jako Brak wpisów; suma zapisanych danych nie dowodzi pełnego spożycia. Niekompletne wartości pozostają jawne. |
| Q16–Q17 | Zmiana bazy nie aktualizuje samoczynnie używającego jej przepisu. Przejście na nową wersję jest świadomą akcją. Usunięcie bazy z biblioteki nie psuje istniejących przepisów: zachowują zapisaną wersję i obliczenia; usuniętej bazy nie wybiera się jako nowego składnika. |
| Korekta Q23 i Q25 | Wartości przepisu wynikają z podanych składników, bez obowiązku mierzenia gotowej potrawy. Nie narzucamy ważenia wszystkiego na sucho niezależnie od podstawy produktu. Bazy sosów mogą być używane w g/ml i wnosić wartości do porcji; jawny przelicznik pozostaje wymaganiem projektu. Bulion, w tym jego problem uzysku/odcedzania, odłożono do kolejnej fazy. Właściciel nie zaakceptował obowiązkowego pomiaru objętości ani deklaracji 1 porcja = 250 ml. |
| Q24, Q26 | Aktualizacja masy ciała i ponowne wyliczenie propozycji celu kalorii wchodzą do osobistego MVP. Propozycję zatwierdza użytkownik, z możliwością zachowania ręcznej korekty; wcześniejsze dni zachowują wcześniejszy cel. Na razie ustawienia należą do jednego dziennika, bez kont i userId. Przyszłe przypisanie danych użytkownikom jest osobnym etapem. |

## Otwarte i niezatwierdzone

- Q02 briefu: wzór zapotrzebowania, dane wejściowe, model aktywności, sposób redukcji i granice propozycji. Aktualizacja masy nie rozstrzyga tych wyborów. Research agenta nie jest zatwierdzeniem algorytmu.
- Przelicznik używanej ilości bazy sosu w g/ml na wartości przepisu: SDD musi określić skąd pochodzi, jak go zweryfikować i co robić przy braku. Nie wyliczamy kcal/100 ml z samych mas składników ani nie zakładamy 1 ml = 1 g.
- Q7 nie rozstrzygnęło polityki wyboru wersji produktu przy świadomej edycji starego wpisu. Potwierdzono izolację wpisów i brak automatycznej zmiany historii; SDD musi doprecyzować korektę niepełnych danych i wybór wersji.
- Q11: właściciel odpowiedział może tak na zmianę kolejności sekcji. Pozostaje propozycją, nie obowiązkowym kryterium MVP.
- Nie zatwierdzono nowego układu makr/sum tygodnia, średnich ani dodatkowych wykresów poza dziennymi kcal.
- Nie ustalono czasu dostępności Cofnij, sposobu trwałego cofania ani datowania korekty pomiaru masy; szczegóły do SDD.

## Powiązanie z pracą

#15/#26: etykiety i edytor przepisu. #16/#27: sekcje. #17/#27: edycja,
przenoszenie, usuwanie i dzień/tydzień. #21/#28: zmienne inputy. #22/#29: bazy sosów,
wersje i zachowanie po usunięciu. Cel kalorii śledzi [E02-T01 #37](https://github.com/kathelan/mealio/issues/37), osobne zadanie analizy/SDD;
nie jest upoważnieniem do implementacji nieustalonego algorytmu.
