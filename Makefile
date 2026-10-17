.PHONY: build lint unit integration verify test test-asr test-backend test-audit run-asr run-backend install-local-libs

build:
	mvn --batch-mode --no-transfer-progress clean package -DskipTests

lint:
	mvn --batch-mode --no-transfer-progress validate

unit:
	mvn --batch-mode --no-transfer-progress clean verify -DskipITs

integration:
	mvn --batch-mode --no-transfer-progress clean verify -Pintegration-tests -DskipUnitTests

verify:
	mvn clean verify

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
	mvn -pl request-audit -am install -DskipTests
