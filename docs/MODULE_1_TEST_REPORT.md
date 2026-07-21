# Module 1 Serialization Test Report

## Environment

- Verification date: 2026-07-20
- Operating environment: Windows PowerShell, VS Code-compatible plain Java project, Apache NetBeans IDE 21
- Runtime/compiler: Java 17.0.11 LTS
- Compilation options: `javac --release 17 -encoding UTF-8`
- External libraries: none; NetBeans verification used its bundled Apache Ant

## Storage under test

- `data/parking_slots.txt`
- `data/registered_vehicles.txt`

Both required filenames retain the `.txt` extension. Their final content is binary Java Object Serialization, not readable text. Each final production file was 58 bytes after empty-list initialization and began with `AC ED 00 05`.

Root objects:

- `ArrayList<smartparkingmanagementsystem.model.ParkingSlot>`
- `ArrayList<smartparkingmanagementsystem.model.Vehicle>`

`ParkingSlot` and `Vehicle` implement `Serializable`; each has `serialVersionUID = 1L`.

## Commands executed

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\verify-project.ps1
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\clean.ps1
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\build.ps1
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\test.ps1
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\run.ps1 -VerifyData
$env:JAVA_HOME='C:\Program Files\Java\jdk-17'
& 'C:\Program Files\NetBeans-21\netbeans\extide\ant\bin\ant.bat' clean test jar
& 'C:\Program Files\NetBeans-21\netbeans\extide\ant\bin\ant.bat' '-Dapplication.args=--verify-data' run
java -jar dist\SmartParkingManagementSystem.jar --verify-data
```

VS Code and the NetBeans graphical IDE were not launched in the verification environment. Their shared configuration was parsed and checked, and their equivalent Java 17 script and Ant commands were executed from the repository root.

## Compilation and automated results

- Production compilation: passed.
- Test compilation: passed.
- Automated tests: **106 passed, 0 failed, 106 total**.
- Serialization and persistence suite: 73 cases all passed.
- CRUD/search/report regression suite: 32 cases all passed.

Coverage includes:

- `Serializable` and both `serialVersionUID` values.
- Model and enum round trips and equality.
- Missing and zero-byte files.
- Empty, single, and multiple-record `ArrayList` roots.
- Every parking-slot and vehicle field, ordering, and Unicode.
- Data persistence across newly constructed handler instances.
- Exact `AC ED 00 05` stream headers.
- Replacement, parent-directory creation, and temporary-file cleanup.
- Corrupted streams, wrong roots, wrong elements, and null elements.
- Manager add/update/delete/status/reload workflows.
- Search and reports after serialized reload.
- Exact `SlotStatus.AVAILABLE` and `RegistrationStatus.ACTIVE` values.
- Rejection and byte preservation for existing non-serialized files.
- Failed-save and corrupted-file byte preservation.
- Production-file isolation.
- Headless-safe Swing panel construction and report refresh on the event thread.

## Serialization-only results

The storage tests confirmed that both handlers accept only valid Java serialization streams. Existing files without the required header are rejected with a clear error, remain byte-for-byte unchanged, and are never interpreted as ordinary text. Missing and zero-byte files retain the documented empty-list behavior, while startup initialization writes a valid serialized empty `ArrayList` when required.

## GUI and scripts

- `scripts/test.ps1`: passed from the project root and printed passed/failed/total counts.
- `scripts/run.ps1 -VerifyData`: passed from the project root and compiled/loaded both production stores.
- NetBeans 21 bundled Ant with JDK 17: clean, test, and JAR targets passed.
- The NetBeans Ant `run` target loaded both production stores from the repository-root working directory.
- The runnable JAR reported Java class-file major version 61 (Java 17) and passed `--verify-data`.
- The headless-safe Swing panel construction smoke test passed within the automated suite.
- The basic single-page Swing window, CRUD dialogs, searches, parking status action, automatic report refresh, and summary report compiled successfully.

## Data safety

- All persistence tests used unique `Files.createTempDirectory` paths.
- A byte snapshot proved the automated test run did not modify production storage.
- Both production files existed before verification and retained their original bytes and SHA-256 hashes throughout all builds, tests, and startup checks.
- Failed temporary writes did not replace a valid destination.
- Corrupted test files remained byte-for-byte unchanged.
- No `.dat` or `.ser` production files were introduced.
- `clean.ps1` removes generated classes only; it preserves production data.

## Manual/static checklist status

Code/static/startup checks were completed. The full human interaction checklist in `MODULE_1_IMPLEMENTATION_PLAN.md` remains available for a person at a graphical desktop; it is not falsely claimed as fully performed.

## Remaining limitations

- Java serialization is tightly coupled to exact package/class identities, compatible fields, enum names, and `serialVersionUID` values. Other students must share these exact model sources.
- Deserialization is intended only for trusted project-controlled files; no arbitrary-file chooser is provided.
- Whole-file replacement is safe against partial writes but is not multi-process transaction locking. Module developers must coordinate writers.
- If the application finds corrupt/incompatible data, it intentionally refuses startup/save rather than destroying the file; recovery requires a trusted backup or compatible classes.

## Scope confirmation

Only Module 1 is implemented. No entry/exit, fee/payment, reservation, staff, authentication, incident, or security-monitoring business logic was added.
