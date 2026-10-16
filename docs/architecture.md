# Architektura Mealio

## Styl systemu

Backend rozwijamy jako modularny monolit: jeden proces i jedno wdrożenie, z granicami modułów odpowiadającymi obszarom biznesowym. Nie wydzielamy mikroserwisu ani osobnego artefaktu Maven bez konkretnej potrzeby operacyjnej lub reużycia.

Pierwszą osią podziału jest domena biznesowa, nie warstwa techniczna:

```text
com.mealio
  product
  diary
  nutrition
  mealparser
  category
  recipe
```

Planowane moduły:

- `product` — katalog, kody kreskowe, porcje i wartości odżywcze,
- `diary` — wpisy spożycia, sekcje dnia i dzienne podsumowania,
- `nutrition` — deterministyczne obliczenia kalorii i makroskładników,
- `mealparser` — tekst na kandydatów produktów, ilości i jednostki,
- `category` — hierarchia kategorii,
- `recipe` — przepisy i ich składniki,
- `asr` — audio na tekst; bez interpretacji posiłku.

Moduł `identity` nie należy do pierwszej iteracji. Zostanie dodany razem z uwierzytelnianiem
po ustabilizowaniu przepływu produktu i posiłku.

## Wnętrze modułu

Każdy moduł może zawierać:

```text
<module>/
  api/              publiczny kontrakt dla innych modułów
  application/      przypadki użycia i granice transakcji
  domain/           model oraz reguły biznesowe
  infrastructure/   REST, JPA, klienci zewnętrzni i konfiguracja
```

Nie tworzymy pustej warstwy ani interfejsu tylko dla zachowania symetrii. Struktura wynika z potrzeb modułu.

## Kierunek zależności

```text
adapter wejściowy -> api/port in -> application -> domain
                                            |
                                            v
                                      port wyjściowy
                                            ^
                                            |
                                  adapter infrastrukturalny
```

Reguły:

- `domain` nie zależy od Springa, JPA, HTTP ani infrastruktury,
- `application` nie zna kontrolerów, encji JPA i konkretnych klientów HTTP,
- `infrastructure` implementuje porty wymagane przez aplikację,
- inne moduły używają wyłącznie publicznego `api` właściciela,
- moduł nie importuje encji JPA, Spring Data repository ani adaptera innego modułu,
- DTO HTTP znajdują się przy adapterze webowym, nie w domenie,
- publiczne kontrakty modułów są małe i stabilne.

Port wejściowy opisuje przypadek użycia aplikacji i domyślnie należy do `api` albo `application.port.in`. Port wyjściowy opisuje potrzebę aplikacji wobec środowiska i należy do `application.port.out` lub domeny, jeśli bezpośrednio wynika z jej języka.

## Komunikacja modułów

- Zapytanie wymagające natychmiastowej odpowiedzi korzysta z publicznego API modułu.
- Efekty uboczne i luźne powiązanie mogą korzystać ze zdarzeń aplikacyjnych.
- Moduły nie komunikują się przez cudze tabele lub repozytoria.
- Relacyjna baza może być wspólna dla wdrożenia, lecz właściciel tabel i migracji musi być jednoznaczny.
- Cykle zależności są zabronione.

## Integracje zewnętrzne

OpenAI, dostawcy kodów kreskowych, katalogi produktów i magazyny plików są adapterami wychodzącymi za portami. Każda integracja ma typowaną konfigurację, limity czasu, mapowanie błędów, testowy substytut i nie ujawnia własnych DTO poza adapterem.

## AI i żywienie

```text
tekst lub ASR
  -> mealparser: proponowane produkty, ilości, jednostki i niepewność
  -> product: dopasowanie do katalogu
  -> nutrition: deterministyczne obliczenie
  -> użytkownik: potwierdzenie
  -> diary: zapis zaakceptowanych wpisów spożycia
```

Model językowy nie jest źródłem danych żywieniowych. Niepewne dopasowanie pozostaje jawne i wymaga potwierdzenia.

## Egzekwowanie granic

Granice mają być sprawdzane automatycznie przez Spring Modulith lub ArchUnit. Testy powinny wykrywać cykle, dostęp do wewnętrznych pakietów innego modułu oraz zależności domeny od frameworków. Fizyczne moduły Maven wprowadzamy dopiero, gdy logiczne granice są stabilne albo potrzebna jest niezależna publikacja.
