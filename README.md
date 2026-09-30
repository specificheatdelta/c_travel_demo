# Costco Travel Selenium POM

Basic Java Selenium 4 suite for [Costco Travel](https://www.costcotravel.com/) using the Page Object Model.

## Tests

1. Homepage loads with logo, login, and Help Center.
2. Hotels tab shows destination, check-in, check-out, and Search.
3. Rental Cars tab shows pickup location and Search.
4. Cruises tab shows the cruise search form.
5. Homepage skip link, logo alt text, and hotel search fields.
6. Header and search widget have no serious or critical axe-core violations.
7. Sign-in form labels email and password, with no serious or critical axe-core violations.
8. A default headless Chrome is rejected with Access Denied; the suite's headless options are not.
9. Member sign-in, only when credentials are supplied outside the repo.

Each homepage check opens Chrome, loads the homepage, checks the page, then closes the browser. Accessibility checks also open the sign-in page. The member sign-in test submits a username and password only when those values are provided outside the repo.

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

### 2. Run the suite

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
Tests run: 9, Failures: 0, Errors: 0, Skipped: 1
BUILD SUCCESS
```

The skipped test is member sign-in, until credentials are supplied. Reports are written to `reports/` and `target/surefire-reports/`.

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
| `homepageHasSkipLinkLogoAltAndSearchLabels` | Skip link, logo alt text, hotel search fields |
| `homepageSearchAndHeaderHaveNoSeriousAxeViolations` | axe-core on the header and search widget |
| `signInFormExposesLabeledFields` | Sign-in labels and axe-core on that page |
| `headlessChromeIsRestricted` | Default headless Chrome versus the suite's headless options |
| `memberCanSignIn` | Signs in when credentials are set; otherwise skipped |

### Sign-in credentials

The sign-in test reads a username and password from the environment, or from a gitignored `credentials.properties` file. It does not store them in source, and reports strip the query string from the sign-in URL.

Do not commit credentials, and do not paste them into chat. For one PowerShell session:

```powershell
$env:COSTCO_USERNAME = "you@example.com"
$env:COSTCO_PASSWORD = "your-password"
.\mvnw.cmd -Dtest=MemberSignInTests test
```

Or copy `credentials.properties.example` to `credentials.properties`, fill in the two keys, and run the same command. `credentials.properties` is listed in `.gitignore`.

Without either source, `memberCanSignIn` is skipped.

### From an IDE

Import the folder as a Maven project, then run `CostcoTravelHomeTests` as a JUnit 5 test. Set `JAVA_HOME` in the IDE run configuration if Maven cannot find the JDK.

## Headless mode

Headless is off unless you set `HEADLESS` to `true`. The four homepage tests pass that way.

```powershell
$env:HEADLESS = "true"
.\mvnw.cmd -Dtest=CostcoTravelHomeTests test
```

Clear it before a normal run:

```powershell
Remove-Item Env:HEADLESS
```

A default headless Chrome, without the Chrome options this suite already uses, is rejected. The page title is `Access Denied` and the body points at `errors.edgesuite.net` (Akamai). That rejection is what made headless look unusable. `HeadlessRestrictionTest` records both results in `reports/headless-probe.txt`.

## Accessibility results

axe-core writes:

- `reports/accessibility-homepage.txt`
- `reports/accessibility-signin.txt`

Test summaries are in `reports/<TestClass>.txt` and `target/surefire-reports/`.

## If a test fails

- **Access Denied** in the page title: you are in a default headless Chrome. Run with `HEADLESS` unset, or keep the suite Chrome options and set `HEADLESS` to `true`.
- **`JAVA_HOME` not found**: set `JAVA_HOME` to the JDK folder, then run `.\mvnw.cmd test` again.
- **ChromeDriver or browser startup error**: update Google Chrome, then rerun. Selenium Manager picks a matching driver.
- **Timeout waiting for a field**: the site layout may have changed. Locators live in `src/test/java/com/costcotravel/pages/HomePage.java`.
