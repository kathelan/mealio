.PHONY: test test-asr test-backend test-audit run-asr run-backend install-local-libs

test:
	mvn test

test-asr:
	mvn -pl asr -am test

test-backend:
	mvn -pl backend -am test

test-audit:
	mvn -pl request-audit test

run-asr:
	mvn -pl asr -am spring-boot:run

run-backend:
	mvn -pl backend -am spring-boot:run

install-local-libs:
	mvn -pl request-audit install -DskipTests
