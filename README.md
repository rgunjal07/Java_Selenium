# SauceDemo E-Commerce Order Validation

Java 25 (the latest LTS release), Selenium 4, TestNG, and Maven automation for the supplied SauceDemo purchase scenario. The test logs in, discovers the inventory's price extremes at runtime, buys both products, checks the cart and checkout amounts, completes the order, generates a PDF proof of purchase, and reads the PDF back to verify its contents.

## Requirements

- JDK 25 (LTS)
- Maven 3.8+
- Chrome for the default suite; Chrome, Edge, and Firefox for the cross-browser suite
- Internet access to download Maven dependencies and WebDriverManager browser drivers
- Access to `https://www.saucedemo.com/`

## Run

From the project root:

```powershell
mvn clean verify
mvn clean test -Dheadless=true
mvn clean test -Dtestng.suiteXmlFile=src/test/resources/testng-cross-browser.xml -Dheadless=true
mvn clean test -Dbrowser=firefox -Dheadless=true
mvn allure:report
mvn allure:serve
```

The default [testng.xml](src/test/resources/testng.xml) runs the order-validation test in Chrome. [testng-cross-browser.xml](src/test/resources/testng-cross-browser.xml) runs the same workflow in Chrome, Edge, and Firefox in parallel. Set the suite's `thread-count` lower if the test machine cannot run three browsers simultaneously. Maven Surefire executes the TestNG suite and writes XML/text results under `target/surefire-reports/`. `mvn clean verify` also generates `target/site/surefire-report.html`.

Allure TestNG writes raw result files under `target/allure-results/`. Run `mvn allure:report` to create the browsable report under `target/site/allure-maven-plugin/`, or `mvn allure:serve` to build and open it locally. `@Step` annotations are woven by AspectJ; the generated PDF is attached to the Allure test result.

## Scenario coverage

`ECommerceOrderValidationTest` executes this flow:

1. Log in with the configured SauceDemo account.
2. Read all product names and prices from the live inventory page.
3. Select the highest and lowest prices dynamically (name is a deterministic tie-breaker), add both items, and verify the cart contains exactly those products.
4. Sum the cart item prices with `BigDecimal` and compare the sum to the selected prices.
5. Submit checkout information, verify the checkout subtotal against the selected products, and verify the displayed bill total equals subtotal plus tax.
6. Finish checkout and assert the order confirmation message.
7. Generate a timestamped PDF proof from the confirmed checkout summary and use PDFBox to extract its text. Assert both item names and the checkout bill amount appear in the file.

### PDF assumption

The SauceDemo checkout flow provides an order confirmation page but no invoice/PDF download control. To satisfy the assessment's proof-of-purchase requirement without pretending the website offers that feature, the test generates a local PDF after the order is confirmed. The document uses the actual product names and prices, subtotal, tax, and total read from the checkout summary; it then validates the generated file itself. PDF files are saved under `target/artifacts/`.

## Configuration

Default settings and demo account values are in [config.properties](src/test/resources/config.properties). Override values with Java properties, for example:

```powershell
mvn clean test -Dbrowser=chrome -Dheadless=true -DbaseUrl=https://www.saucedemo.com
```

System properties take precedence over environment variables, which take precedence over the properties file. Environment variable names use uppercase underscore format (for example `BASE_URL`, `HEADLESS`, `USERNAME`, `FIRST_NAME`). Use the public SauceDemo sample account only; do not place private credentials in source control.

## Project structure

```text
src/main/java/com/automation/
├── base/       BasePage and BaseTest lifecycle
├── constants/  Shared framework constants
├── drivers/    Browser factory and thread-local WebDriver
├── listeners/  TestNG listener, retries, and Extent Reports
├── models/     Immutable Product and OrderSummary values
├── pages/      SauceDemo login, inventory, cart, and checkout page objects
└── utils/      Configuration, waits, screenshots, data readers, and PDF utilities
src/test/java/com/automation/
├── dataproviders/  JSON and Excel providers from the framework starter
└── tests/          ECommerceOrderValidationTest
src/test/resources/
├── testdata/       Sample JSON and Excel files
├── config.properties
├── log4j2.xml
└── testng*.xml
```

## Reporting and outputs

- Extent report: `reports/extent-report.html`
- Allure raw results: `target/allure-results/`
- Allure HTML report: `target/site/allure-maven-plugin/` (generate with `mvn allure:report`)
- Surefire HTML report: `target/site/surefire-report.html` (generate with `mvn clean verify`)
- Failure screenshots: `reports/screenshots/`
- Execution log: `logs/automation.log`
- Generated validated order proofs: `target/artifacts/`
- TestNG/Surefire results: `target/surefire-reports/`

GitHub Actions and Jenkins examples are included. CI should install the selected browser and run in headless mode, for example `mvn --batch-mode clean verify -Dheadless=true allure:report`.
