# Module 1 Serialized Data Contract

## Original requirement and implementation format

The original assignment requires the filenames `parking_slots.txt` and `registered_vehicles.txt`. This implementation preserves those names exactly. An additional course requirement changes their physical content to **binary Java Object Serialization**. The `.txt` suffix is therefore a filename requirement only; neither file is normal text.

Module 1 owns:

- `data/parking_slots.txt`
- `data/registered_vehicles.txt`

Do not open, edit, parse, or rewrite these files as text. Notepad will display unreadable binary symbols; that is expected. Normal code must use `FileInputStream` and `ObjectInputStream` to read them and `FileOutputStream` and `ObjectOutputStream` to write them.

## Exact root objects

Each file contains exactly one serialized root object:

| File | Exact root implementation | Element class |
|---|---|---|
| `data/parking_slots.txt` | `java.util.ArrayList` | `smartparkingmanagementsystem.model.ParkingSlot` |
| `data/registered_vehicles.txt` | `java.util.ArrayList` | `smartparkingmanagementsystem.model.Vehicle` |

The application rejects a different root type, a null element, or an element of another class. The stream normally begins with the Java serialization header bytes `AC ED 00 05`.

## Shared class identities

Serialization records fully qualified class identities. Other module developers must compile against the exact shared source classes:

- `smartparkingmanagementsystem.model.ParkingSlot`
- `smartparkingmanagementsystem.model.Vehicle`
- `smartparkingmanagementsystem.model.SlotType`
- `smartparkingmanagementsystem.model.SlotStatus`
- `smartparkingmanagementsystem.model.VehicleType`
- `smartparkingmanagementsystem.model.RegistrationStatus`

`ParkingSlot` and `Vehicle` both implement `java.io.Serializable` and both declare `serialVersionUID = 1L`. Package names, class names, compatible field types, enum names, and serialVersionUID values are part of the contract. A look-alike class in another package is not compatible.

Safest team workflow:

1. Share the exact model and enum source files.
2. Keep package and class names identical.
3. Keep `serialVersionUID` at `1L` unless the whole team deliberately versions the format.
4. Coordinate any field or enum change before deploying it.
5. Do not independently copy and rewrite equivalent model classes.

## ParkingSlot schema

Class: `smartparkingmanagementsystem.model.ParkingSlot`  
serialVersionUID: `1L`

Fields:

- `String slotId`
- `int floorNumber`
- `SlotType slotType`
- `SlotStatus slotStatus`
- `double hourlyRate`

`SlotType`: `SMALL`, `MEDIUM`, `LARGE`, `ELECTRIC`.

`SlotStatus`:

- `AVAILABLE`: eligible for consideration by Entry/Exit or Reservation according to their rules.
- `OCCUPIED`: currently marked occupied.
- `RESERVED`: reserved/unavailable for ordinary assignment.
- `UNDER_MAINTENANCE`: unavailable for use.

Other modules must assign only a slot whose value is exactly `SlotStatus.AVAILABLE`. Module 1 does not implement assignment.

## Vehicle schema

Class: `smartparkingmanagementsystem.model.Vehicle`  
serialVersionUID: `1L`

Fields:

- `String vehicleNumber`
- `String ownerName`
- `String phone`
- `VehicleType vehicleType`
- `RegistrationStatus registrationStatus`

`VehicleType`: `CAR`, `BIKE`, `VAN`, `TRUCK`.

`RegistrationStatus`:

- `ACTIVE`: usable as an active registered-vehicle record.
- `INACTIVE`: retained master data that is not actively registered.

Other modules should consider only a vehicle whose value is exactly `RegistrationStatus.ACTIVE`.

## Reading parking slots

This academic example assumes the exact shared classes are on the classpath. Production consumers should additionally validate the exact root and every element as Module 1 does.

```java
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import smartparkingmanagementsystem.model.ParkingSlot;
import smartparkingmanagementsystem.model.SlotStatus;

try (FileInputStream fileInputStream =
             new FileInputStream("data/parking_slots.txt");
     ObjectInputStream objectInputStream =
             new ObjectInputStream(fileInputStream)) {

    @SuppressWarnings("unchecked")
    ArrayList<ParkingSlot> slots =
            (ArrayList<ParkingSlot>) objectInputStream.readObject();

    for (ParkingSlot slot : slots) {
        if (slot.getSlotStatus() == SlotStatus.AVAILABLE) {
            // Entry/Exit or Reservation may consider this slot under its own rules.
        }
    }
}
```

## Reading registered vehicles

```java
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import smartparkingmanagementsystem.model.RegistrationStatus;
import smartparkingmanagementsystem.model.Vehicle;

try (FileInputStream fileInputStream =
             new FileInputStream("data/registered_vehicles.txt");
     ObjectInputStream objectInputStream =
             new ObjectInputStream(fileInputStream)) {

    @SuppressWarnings("unchecked")
    ArrayList<Vehicle> vehicles =
            (ArrayList<Vehicle>) objectInputStream.readObject();

    for (Vehicle vehicle : vehicles) {
        if (vehicle.getRegistrationStatus() == RegistrationStatus.ACTIVE) {
            // This vehicle is actively registered.
        }
    }
}
```

## Invalid-file behavior

The application accepts only Java serialization streams with the `AC ED 00 05` header. Existing content without that header, corrupt or incompatible streams, wrong-root objects, and wrong or null elements are rejected with a clear storage error and preserved byte-for-byte. The application never parses an existing data file as ordinary text and never overwrites invalid existing content automatically.

## Security and operational warnings

- Deserialize only files generated and controlled by this project/team.
- Never deserialize arbitrary files received from untrusted sources.
- Module 1 deliberately provides no GUI file chooser for serialized input.
- Coordinate writers: Java serialization provides safe whole-file replacement, not multi-process transactions.
- Reload before coordinated external changes and use a team-agreed single-writer protocol.
- Do not rename the production files to `.dat` or `.ser`.
