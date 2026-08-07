# Selenium Java + Jenkins Demo Framework

A minimal but production-shaped Selenium WebDriver framework:

- **Java 17 + Maven**
- **TestNG** as the test runner
- **Page Object Model** (`pages/`)
- **WebDriverManager** (no manual driver binaries)
- **Jenkinsfile** for a declarative CI pipeline
- Sample tests against the public demo site https://www.saucedemo.com

## Project layout

```
selenium-java-jenkins-demo/
├── pom.xml
├── testng.xml
├── Jenkinsfile
├── src/main/java/
│   ├── framework/DriverManager.java   # thread-safe driver factory
│   └── pages/                         # Page Object classes
│       ├── BasePage.java
│       ├── LoginPage.java
│       └── ProductsPage.java
└── src/test/java/tests/
    ├── BaseTest.java                  # @BeforeMethod/@AfterMethod setup-teardown
    └── LoginTest.java                 # 3 sample tests
```

## 1. Run locally first

```bash
cd selenium-java-jenkins-demo

# Chrome, headless (default)
mvn clean test

# Firefox
mvn clean test -Dbrowser=firefox

# Headed (see the browser)
mvn clean test -Dheadless=false
```

Reports land in `target/surefire-reports/`.

Prereqs: JDK 17+, Maven 3.6+, and Chrome and/or Firefox installed. You do **not**
need to download ChromeDriver/GeckoDriver manually — WebDriverManager fetches
the matching driver binary at runtime.

## 2. Jenkins setup

### a) Install plugins
In **Manage Jenkins → Plugins**, make sure these are installed:
- Pipeline
- Git
- Maven Integration
- JUnit (for the `junit` report step in the Jenkinsfile — TestNG's surefire
  XML output is JUnit-format compatible)

### b) Configure tools
**Manage Jenkins → Tools**:
- Add a **JDK** installation named `JDK17` (or point it at an existing JDK 17 install)
- Add a **Maven** installation named `Maven3`

(These names must match the `tools {}` block in the `Jenkinsfile`. Rename either
side if you use different names.)

### c) Make sure agents have browsers
The Jenkins agent that runs the job needs Chrome and/or Firefox installed
(headless mode doesn't need a display, but the browser binary itself must be
present). On Debian/Ubuntu agents:

```bash
sudo apt-get update
sudo apt-get install -y chromium-browser firefox-esr
```

Or use a Docker agent image that already bundles Chrome, e.g.
`selenium/standalone-chrome` or `cypress/browsers`, if you'd rather not install
browsers on the host.

### d) Create the Jenkins job
1. **New Item → Pipeline** (or **Multibranch Pipeline** if you want PR/branch builds automatically).
2. Under **Pipeline**, choose **Pipeline script from SCM**.
3. SCM: **Git** → paste your repo URL (push this project to GitHub/GitLab/Bitbucket first) → set the branch (e.g. `main`).
4. Script Path: `Jenkinsfile` (default, already correct if it's at repo root).
5. Save.

## 3. Steps to run the job

1. Push this project to your git remote:
   ```bash
   cd selenium-java-jenkins-demo
   git init
   git add .
   git commit -m "Selenium Java + Jenkins demo framework"
   git remote add origin <your-repo-url>
   git push -u origin main
   ```
2. In Jenkins, open the pipeline job you created above.
3. Click **Build with Parameters**.
4. Choose:
   - `BROWSER`: `chrome` or `firefox`
   - `SUITE_FILE`: leave as `testng.xml` (or point to another suite file if you add one)
5. Click **Build**.
6. Watch progress under **Build History → (build #) → Console Output**.
7. After the build finishes:
   - **Test Result** link on the build page shows the TestNG/JUnit results trend.
   - **Build Artifacts** contains the raw `target/surefire-reports/**` files.

### Optional: trigger automatically
- **On every push**: enable **GitHub hook trigger for GITScm polling** (needs a
  webhook configured on the repo) under the job's **Build Triggers**.
- **On a schedule**: add `triggers { cron('H 2 * * *') }` inside the `Jenkinsfile`'s
  `pipeline {}` block to run nightly, for example.

## Extending this framework
- Add more Page Objects under `src/main/java/pages/`.
- Add more `@Test` classes under `src/test/java/tests/` and reference them in `testng.xml`.
- Swap `saucedemo.com` for your own app under test by changing `baseUrl` in
  `src/test/resources/config.properties`.
- For screenshots on failure, hook into `BaseTest.tearDown()` where the
  `ITestResult.FAILURE` branch already exists.
