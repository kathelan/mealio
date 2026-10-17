# Standardy inżynieryjne — przeniesienie i wdrożenie

Data: 2026-10-17.

Cel: wykonać korekty użytkownika dotyczące Optional/builderów i udostępnić standardy
w `.ai/docs/engineering-standards.md`. Początkowo wskazana ścieżka nie istniała;
po doprecyzowaniu użytkownika przeniesiono istniejącą treść z docs/engineering-practices.md.

Zmiany: nowy plik kanoniczny, odnośnik zgodności w docs, wskazanie w AGENTS.md,
pamięć preferencji w lessons, dwa testy ArchUnit oraz wdrożona specyfikacja.
CLAUDE.md odsyła do AGENTS.md i nie powiela zasad. Zachowano wcześniejsze zmiany
użytkownika w opisie stosu AGENTS.md oraz w README.md poza zakresem commita.

Weryfikacja: pełne mvn clean verify bieżącego drzewa z rozwijanym product:
111 testów (backend 89, ASR 11, request-audit 11), brak błędów/pominięć,
JaCoCo 88,76%, reguły ArchUnit przechodzą. Weryfikacja odnośników i diffu: OK.

W trakcie pracy pojawiło się zainstalowane Xcode, którego niezaakceptowana licencja
blokowała systemowe shimy python3/git. Użyto istniejących binariów Command Line Tools
i ustawienia DEVELOPER_DIR dla builda; nie zmieniano globalnej konfiguracji Xcode
ani nie akceptowano jego licencji.

Niezależny drugi agent przygotował research z oficjalnymi źródłami w osobnej
specyfikacji i raporcie. Jego dodatkowe rekomendacje nie są automatycznie wdrożone.
