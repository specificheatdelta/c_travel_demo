# Costco Travel Selenium POM

Basic Java Selenium 4 suite for [Costco Travel](https://www.costcotravel.com/) using the Page Object Model.

## Tests

1. Homepage loads with logo, login, and Help Center.
2. Hotels tab shows destination, check-in, check-out, and Search.
3. Rental Cars tab shows pickup location and Search.
4. Cruises tab shows the cruise search form.

Each test opens Chrome, loads the homepage, checks the page, then closes the browser.

## Requirements

- JDK 17 or newer (Java 21 works)
- Google Chrome installed and able to open normally
- Internet access to `https://www.costcotravel.com/`

Maven is not required. This repo includes the Maven Wrapper (`mvnw.cmd` on Windows, `mvnw` on macOS or Linux). The first run downloads Maven and a ChromeDriver that matches your installed Chrome.

## Run the demo

Open a terminal in the project folder.

### 1. Confirm Java

```powershell
java -version
echo $env:JAVA_HOME
```

`java -version` should report 17 or newer. If `JAVA_HOME` is empty, point it at your JDK for this session:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
```

Use the folder that contains `bin\java.exe`, not the `bin` folder itself.

### 2. Run all four tests

Leave Chrome visible. Costco Travel often returns an Access Denied page to headless Chrome.

```powershell
.\mvnw.cmd test
```

On macOS or Linux:

```bash
./mvnw test
```

The first run can take a few minutes while the wrapper downloads Maven and Selenium Manager downloads ChromeDriver. Later runs are faster.

A passing run ends with a Surefire summary similar to:

```text
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Reports are written to `target/surefire-reports/`.

### 3. Run one test

Run the whole class:

```powershell
.\mvnw.cmd -Dtest=CostcoTravelHomeTests test
```

Run a single method:

```powershell
.\mvnw.cmd -Dtest=CostcoTravelHomeTests#hotelsTabShowsSearchFields test
```

Method names:

| Method | What it checks |
| --- | --- |
| `homePageLoadsWithLogoAndLogin` | Title, logo, Login, Help Center |
| `hotelsTabShowsSearchFields` | Hotels destination, dates, and Search |
| `rentalCarsTabShowsPickupAndSearch` | Rental car pickup and Search |
| `cruisesTabShowsSearchForm` | Cruise search form |

### From an IDE

Import the folder as a Maven project, then run `CostcoTravelHomeTests` as a JUnit 5 test. Set `JAVA_HOME` in the IDE run configuration if Maven cannot find the JDK.

## Headless mode

Headless is off unless you set `HEADLESS` to `true`. Use it only if you need a browser with no window; this site may block it.

```powershell
$env:HEADLESS = "true"
.\mvnw.cmd test
```

Clear it before a normal run:

```powershell
Remove-Item Env:HEADLESS
```

## If a test fails

- **Access Denied** in the page title: run with Chrome visible and leave `HEADLESS` unset.
- **`JAVA_HOME` not found**: set `JAVA_HOME` to the JDK folder, then run `.\mvnw.cmd test` again.
- **ChromeDriver or browser startup error**: update Google Chrome, then rerun. Selenium Manager picks a matching driver.
- **Timeout waiting for a field**: the site layout may have changed. Locators live in `src/test/java/com/costcotravel/pages/HomePage.java`.
