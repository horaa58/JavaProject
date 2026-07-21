# Module 1 User Guide

## Overview

This repository currently implements only Module 1: parking slot and registered vehicle master-data management. It provides one basic Swing page containing Parking Slots, Registered Vehicles, and Reports. It does not perform entries, exits, fees, reservations, staffing, or security monitoring.

## Requirements, compile, and run

Install a Java 17 JDK and open the repository root in VS Code or Apache NetBeans IDE 21. From a PowerShell terminal in either IDE:

```powershell
.\scripts\run.ps1
```

The script compiles `src` into `out/classes` and starts `smartparkingmanagementsystem.SmartParkingManagementSystem`. To compile and run tests instead, use `.\scripts\test.ps1`. Generated classes can be removed safely with `.\scripts\clean.ps1`.

For a non-GUI storage startup check, use `.\scripts\run.ps1 -VerifyData`.

VS Code and NetBeans-specific opening, building, debugging, and test instructions are documented in `IDE_SETUP.md`.

## Parking slots

- Add: in **Parking Slots**, choose **Add**, enter all five values, and save.
- Edit: select a row and choose **Edit**. Changing an ID is allowed only when the replacement remains unique.
- Delete: select a row, choose **Delete**, and confirm.
- Status: select a row, choose **Change Status**, select a supported status, and confirm. It is saved immediately.
- Search: use the single search field for Slot ID, floor, type, or status. Clear the field and select **Search** to restore all rows.

## Registered vehicles

- Register: in **Registered Vehicles**, choose **Add**, enter owner/vehicle data, and save.
- Edit: select a row and choose **Edit**.
- Delete: select a row, choose **Delete**, and confirm.
- Registration status is stored with the vehicle and can be changed through **Edit**.
- Search: use the single search field for vehicle number, owner name, phone, or vehicle type. Clear the field and select **Search** to restore all rows.

## Reports

The **Report** panel shows the Module 1 summary: total parking slots, Available, Occupied, Reserved, Under Maintenance, and total registered vehicles. It refreshes automatically after successful add, edit, delete, or parking-status changes. Empty data is shown as zero.

## Binary serialized data storage

Data is relative to the project working directory and retains the assignment-required filenames:

```text
data/parking_slots.txt
data/registered_vehicles.txt
```

Despite their `.txt` extension, both files contain binary Java serialized `ArrayList` objects. They are written with `ObjectOutputStream` and read with `ObjectInputStream`. Do not manually edit them; opening them in Notepad will show unreadable symbols, which is expected. See `MODULE_1_DATA_CONTRACT.md` for the exact shared-class contract.

Only valid Java serialization data is accepted. A non-serialized, corrupt, or unknown existing file is reported and preserved rather than overwritten.

## Integration notes

Module 1 owns both files. Module 2 may read slot/rate/vehicle data; Module 3 may read slot and vehicle data; Module 4 may read them for monitoring. Consumers assign only exact `SlotStatus.AVAILABLE` slots and treat only exact `RegistrationStatus.ACTIVE` registrations as usable. They must use `ObjectInputStream` and the exact shared classes in `smartparkingmanagementsystem.model`, never look-alike classes or Swing panels.

## Troubleshooting

- `javac` or `java` not found: install/configure a Java 17 JDK and restart the terminal.
- PowerShell says scripts are disabled: use `powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\\scripts\\run.ps1` (or `test.ps1`). This bypass is process-local and does not alter the machine policy.
- Compilation reports the wrong release: check `java -version` and `javac -version` both resolve to 17 or newer.
- Startup data error: ensure the project directory is writable and `data` is not locked by another program.
- Corrupted/incompatible warning: preserve the file and restore a trusted backup or compatible shared model classes; the application will not overwrite it automatically.
- Empty table: clear the search field and select **Search**; do not inspect or edit the binary file as text.
