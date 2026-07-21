# IDE Setup: VS Code and Apache NetBeans IDE 21

The same Java 17 project can be opened and run in either IDE. Always open the repository root—the folder containing `src`, `test`, `data`, `build.xml`, and `nbproject`—rather than opening `src` by itself.

## Common requirement

Install a Java 17 JDK. Confirm from a terminal:

```powershell
java -version
javac -version
```

Both commands should report Java 17 or a newer JDK capable of compiling with Java 17 compatibility.

## Visual Studio Code

1. Install **Extension Pack for Java** from Microsoft.
2. Choose **File > Open Folder** and select `SmartParkingManagementSystem`.
3. Wait for the Java language server to finish importing `src` and `test`.
4. Press `F5` and select **Run Smart Parking Management System**.

Configured actions:

- `Ctrl+Shift+B`: production build through `scripts/build.ps1`.
- **Terminal > Run Task > Java 17: Test**: run all deterministic tests.
- Run and Debug > **Verify Serialized Data**: verify storage without opening Swing.
- Run and Debug > **Run All Tests**: run `AllTests` with assertions enabled.

VS Code generates IDE classes in `bin`; terminal scripts generate output in `out`.

## Apache NetBeans IDE 21

1. Start NetBeans 21.
2. Choose **File > Open Project**.
3. Select the `SmartParkingManagementSystem` repository root. NetBeans recognizes `nbproject/project.xml` as a Java SE project.
4. If requested, select a Java 17 platform under **Tools > Java Platforms**.
5. Press `F6` or choose **Run > Run Project**.

Configured NetBeans actions:

- **Shift+F11 / Clean and Build Project**: creates `dist/SmartParkingManagementSystem.jar`.
- **Run Project**: launches `smartparkingmanagementsystem.SmartParkingManagementSystem`.
- **Test Project**: compiles `test` and runs `smartparkingmanagementsystem.AllTests`.
- **Debug Project**: uses NetBeans' JPDA debugger.

NetBeans generates classes in `build` and the runnable JAR in `dist`. Personal settings created in `nbproject/private` are ignored.

## Portable data behavior

Both IDEs use the repository root as the working directory, so the application always reads and writes:

```text
data/parking_slots.txt
data/registered_vehicles.txt
```

These `.txt` files contain binary serialized `ArrayList` objects. Do not move them into `src`, `bin`, `build`, `dist`, or `out`, and do not edit them as text.

## Terminal fallback

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\build.ps1
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\run.ps1
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\scripts\test.ps1
```

## Troubleshooting

- NetBeans shows a broken platform: set Project Properties > Libraries > Java Platform to an installed JDK 17.
- VS Code does not recognize Java files: run **Java: Clean Java Language Server Workspace**, then reopen the root.
- Data appears empty in one IDE: verify that the repository root—not `src`—was opened.
- PowerShell blocks scripts: use the process-local `-ExecutionPolicy Bypass` commands above.
- Do not copy `bin`, `build`, `dist`, `out`, or `nbproject/private`; these contain generated or personal IDE state.
