# Smart Parking Management System

This portable Java 17 desktop repository currently implements **Module 1 only: Parking Slot and Vehicle Registration Management**. It can be opened directly in **Visual Studio Code** or **Apache NetBeans IDE 21**. The complete specification in `docs/project.md` defines four student-owned modules; entry/exit/fees, reservations, and staff/security remain intentionally out of scope here.

Module 1 provides a handwritten Java Swing application for parking-slot and vehicle CRUD, case-insensitive search, immediate status persistence, binary Java Object Serialization, and automatically refreshed summary reports.

## Run and test

From a PowerShell terminal in the project root:

```powershell
.\scripts\run.ps1
.\scripts\test.ps1
.\scripts\clean.ps1
```

The VS Code Java extension uses `src` and emits IDE classes to `bin` according to `.vscode/settings.json`. Project scripts emit disposable classes under `out`. Runtime paths remain exactly `data/parking_slots.txt` and `data/registered_vehicles.txt`, but their contents are binary serialized `ArrayList` objects—not readable text. Do not edit them in Notepad. Tests use isolated temporary directories.

## IDE support

- VS Code: open this repository root, install Extension Pack for Java, then press `F5` and select **Run Smart Parking Management System**. `Ctrl+Shift+B` performs the Java 17 build.
- Apache NetBeans IDE 21: use **File > Open Project**, select this repository root (containing `build.xml` and `nbproject`), then press `F6` to run or **Shift+F11** to clean and build.

Both IDEs use the same `src` and `test` folders, Java 17 entry point, UTF-8 encoding, repository-root working directory, and serialized data files. See `docs/IDE_SETUP.md` for detailed instructions.

## Documentation

- `docs/MODULE_1_DATA_CONTRACT.md` — stable contracts consumed by other modules.
- `docs/MODULE_1_SERIALIZATION_CONTRACT.md` — focused binary format, validation, and compatibility rules.
- `docs/IDE_SETUP.md` — VS Code and Apache NetBeans IDE 21 instructions.
- `docs/MODULE_1_USER_GUIDE.md` — complete operator/build instructions.
- `docs/MODULE_1_TEST_REPORT.md` — recorded verification evidence.
- `docs/project.md` — complete four-module project brief and primary source of truth.
