# Module 1 Java Serialization Contract

This focused contract supplements `MODULE_1_DATA_CONTRACT.md`.

## Required names, binary content

```text
data/parking_slots.txt          -> ArrayList<ParkingSlot>
data/registered_vehicles.txt    -> ArrayList<Vehicle>
```

Both files are binary Java serialization streams despite the `.txt` suffix. They begin with `AC ED 00 05` after a successful save and are not intended for a text editor.

## Write protocol

1. Validate and copy records into an exact `java.util.ArrayList`.
2. Serialize that complete list to a same-directory temporary file with `FileOutputStream`, `ObjectOutputStream`, and `writeObject`.
3. Flush, close, and replace the required `.txt` destination atomically where supported.
4. If serialization fails, delete the temporary file and preserve the last destination.

## Read protocol

1. Missing or zero-byte file: return an empty `ArrayList`.
2. Header `AC ED 00 05`: reopen with `FileInputStream` and `ObjectInputStream`, then call `readObject` once.
3. Require exact `ArrayList` root, correct non-null elements, valid fields, and unique identifiers.
4. Missing `AC ED 00 05` header: report that the file is not valid Java serialization data.
5. Unknown, corrupt, or incompatible content: report an error and preserve the original bytes without overwrite.

## Compatibility

Other modules must reuse the exact `smartparkingmanagementsystem.model` classes. `ParkingSlot` and `Vehicle` use `serialVersionUID = 1L`. Changing packages, names, incompatible fields, enums, or serialVersionUID can make existing files unreadable and must be coordinated by the team.
