# Lab 4 – LibraryService Unit Tests

Unit tests for `LibraryService.checkoutResource`, written with **JUnit 5** and **Mockito**. The `ResourceRepository` and `EmailProvider` dependencies are mocked, so no database or email server is needed.

## Project Structure

```
src/
├── main/java/lab4/
│   ├── LibraryService.java            # Class under test
│   ├── ResourceRepository.java        # Dependency (mocked)
│   ├── EmailProvider.java             # Dependency (mocked)
│   ├── DatabaseFailureException.java
│   └── EmailFailureException.java
└── test/java/lab4/
    └── LibraryServiceTest.java        # Test cases T1–T7
```

## Requirements

- Java 17 or newer
- Maven 3.6+ (or IntelliJ IDEA, which comes with Maven built in)

Check that they're installed:

```bash
java -version
mvn -version
```

## Running the Tests

### Command line

From the project root (the folder containing `pom.xml`):

```bash
mvn test
```

Maven downloads JUnit and Mockito on the first run, then compiles and runs the tests. A successful run ends with:

```
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### IntelliJ IDEA

1. **File → Open** and select the project folder (open it as a Maven project if asked).
2. Wait for Maven to finish importing dependencies.
3. Open `src/test/java/lab4/LibraryServiceTest.java`.
4. Click the green run icon next to the class name, or right-click the file and choose **Run 'LibraryServiceTest'**.

## Test Cases

| ID | Scenario | Expected Result |
|----|----------|-----------------|
| T1 | `resourceId` is `null` | Returns `false`; no calls to either mock |
| T2 | Resource is unavailable | Returns `false` |
| T3 | `updateStatus` returns `false` | Throws `DatabaseFailureException` |
| T4 | `updateStatus` throws `DatabaseFailureException` | Exception propagates |
| T5 | `sendEmail` returns `false` | Throws `EmailFailureException` |
| T6 | `sendEmail` throws `EmailFailureException` | Exception propagates |
| T7 | Successful checkout | Returns `true` and the email is sent |
