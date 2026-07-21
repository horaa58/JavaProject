package smartparkingmanagementsystem;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import smartparkingmanagementsystem.filehandling.FileOperationException;
import smartparkingmanagementsystem.filehandling.ParkingSlotFileHandler;
import smartparkingmanagementsystem.filehandling.VehicleFileHandler;
import smartparkingmanagementsystem.model.ParkingSlot;
import smartparkingmanagementsystem.model.RegistrationStatus;
import smartparkingmanagementsystem.model.SlotStatus;
import smartparkingmanagementsystem.model.SlotType;
import smartparkingmanagementsystem.model.Vehicle;
import smartparkingmanagementsystem.model.VehicleType;

final class FileHandlingTest {
    static void register(TestSuite s) {
        parkingSlotStorage(s);
        vehicleStorage(s);
    }

    private static void parkingSlotStorage(TestSuite s) {
        s.test("09 missing parking file returns empty ArrayList", () -> { var result = new ParkingSlotFileHandler(path("missing/parking_slots.txt")).load().records(); TestSuite.equal(ArrayList.class, result.getClass()); TestSuite.equal(0, result.size()); });
        s.test("10 empty parking file returns empty ArrayList", () -> { Path file = emptyFile("parking_slots.txt"); var result = new ParkingSlotFileHandler(file).load().records(); TestSuite.equal(ArrayList.class, result.getClass()); TestSuite.equal(0, result.size()); });
        s.test("11 saving empty parking ArrayList succeeds", () -> { Path file = path("parking_slots.txt"); var handler = new ParkingSlotFileHandler(file); handler.save(new ArrayList<>()); TestSuite.equal(0, handler.load().records().size()); TestSuite.truth(SerializationTestSupport.hasHeader(file)); });
        s.test("12 saving one ParkingSlot succeeds", () -> { Path file = path("parking_slots.txt"); var handler = new ParkingSlotFileHandler(file); handler.save(new ArrayList<>(List.of(TestFixtures.slot("S1")))); TestSuite.equal(1, handler.load().records().size()); });
        s.test("13 saving multiple ParkingSlots succeeds", () -> { Path file = path("parking_slots.txt"); var handler = new ParkingSlotFileHandler(file); handler.save(new ArrayList<>(List.of(TestFixtures.slot("S1"), TestFixtures.slot("S2")))); TestSuite.equal(2, handler.load().records().size()); });
        s.test("14 parking file has serialization header", () -> { Path file = path("parking_slots.txt"); new ParkingSlotFileHandler(file).save(List.of(TestFixtures.slot("S"))); TestSuite.truth(SerializationTestSupport.hasHeader(file)); });
        s.test("15 loading restores parking slot count", () -> { Path file = path("parking_slots.txt"); var handler = new ParkingSlotFileHandler(file); handler.save(List.of(TestFixtures.slot("A"), TestFixtures.slot("B"), TestFixtures.slot("C"))); TestSuite.equal(3, handler.load().records().size()); });
        s.test("16 parking slot IDs are preserved", () -> { ParkingSlot value = roundTripSlot(new ParkingSlot("S-ABC", 1, SlotType.SMALL, SlotStatus.AVAILABLE, 2)); TestSuite.equal("S-ABC", value.getSlotId()); });
        s.test("17 parking floor numbers are preserved", () -> TestSuite.equal(12, roundTripSlot(new ParkingSlot("S", 12, SlotType.SMALL, SlotStatus.AVAILABLE, 2)).getFloorNumber()));
        s.test("18 parking slot types are preserved", () -> TestSuite.equal(SlotType.ELECTRIC, roundTripSlot(new ParkingSlot("S", 1, SlotType.ELECTRIC, SlotStatus.AVAILABLE, 2)).getSlotType()));
        s.test("19 parking slot statuses are preserved", () -> TestSuite.equal(SlotStatus.UNDER_MAINTENANCE, roundTripSlot(new ParkingSlot("S", 1, SlotType.SMALL, SlotStatus.UNDER_MAINTENANCE, 2)).getSlotStatus()));
        s.test("20 parking hourly rates are preserved", () -> TestSuite.close(7.75, roundTripSlot(new ParkingSlot("S", 1, SlotType.SMALL, SlotStatus.AVAILABLE, 7.75)).getHourlyRate()));
        s.test("21 parking slot order is preserved", () -> { Path file = path("parking_slots.txt"); var handler = new ParkingSlotFileHandler(file); handler.save(List.of(TestFixtures.slot("FIRST"), TestFixtures.slot("SECOND"))); var loaded = handler.load().records(); TestSuite.equal("FIRST", loaded.get(0).getSlotId()); TestSuite.equal("SECOND", loaded.get(1).getSlotId()); });
        s.test("22 Unicode parking identifiers survive", () -> TestSuite.equal("موقف-١", roundTripSlot(new ParkingSlot("موقف-١", 1, SlotType.MEDIUM, SlotStatus.AVAILABLE, 2)).getSlotId()));
        s.test("23 parking save reload preserves equality", () -> { ParkingSlot value = new ParkingSlot("EQ", 3, SlotType.LARGE, SlotStatus.RESERVED, 4.25); TestSuite.equal(value, roundTripSlot(value)); });
        s.test("24 re-saving replaces previous parking list", () -> { Path file = path("parking_slots.txt"); var handler = new ParkingSlotFileHandler(file); handler.save(List.of(TestFixtures.slot("OLD"))); handler.save(List.of(TestFixtures.slot("NEW"))); var loaded = handler.load().records(); TestSuite.equal(1, loaded.size()); TestSuite.equal("NEW", loaded.get(0).getSlotId()); });
        s.test("24a parking data survives a new handler instance", () -> { Path file = path("parking_slots.txt"); new ParkingSlotFileHandler(file).save(List.of(TestFixtures.slot("PERSISTED"))); var reopened = new ParkingSlotFileHandler(file); TestSuite.equal("PERSISTED", reopened.load().records().get(0).getSlotId()); });
        s.test("25 parking save creates missing parent", () -> { Path file = path("nested/data/parking_slots.txt"); new ParkingSlotFileHandler(file).save(List.of(TestFixtures.slot("S"))); TestSuite.truth(Files.exists(file)); });
        s.test("26 corrupted serialized parking file is rejected", () -> { Path file = path("parking_slots.txt"); Files.write(file, new byte[] {(byte) 0xAC, (byte) 0xED, 0, 5, 1, 2}); TestSuite.throwsType(FileOperationException.class, () -> new ParkingSlotFileHandler(file).load()); });
        s.test("26a non-serialized parking file is rejected and preserved", () -> { Path file = path("parking_slots.txt"); byte[] original = "not Java serialization".getBytes(StandardCharsets.UTF_8); Files.write(file, original); try { new ParkingSlotFileHandler(file).load(); throw new AssertionError("expected FileOperationException"); } catch (FileOperationException expected) { TestSuite.truth(expected.getMessage().contains("Java serialization")); } TestSuite.truth(Arrays.equals(original, Files.readAllBytes(file))); });
        s.test("27 wrong parking root object is rejected", () -> { Path file = path("parking_slots.txt"); SerializationTestSupport.writeObject(file, "not an ArrayList"); TestSuite.throwsType(FileOperationException.class, () -> new ParkingSlotFileHandler(file).load()); });
        s.test("28 wrong parking list element is rejected", () -> { Path file = path("parking_slots.txt"); SerializationTestSupport.writeObject(file, SerializationTestSupport.list("not a slot")); TestSuite.throwsType(FileOperationException.class, () -> new ParkingSlotFileHandler(file).load()); });
        s.test("29 null parking list element is rejected", () -> { Path file = path("parking_slots.txt"); SerializationTestSupport.writeObject(file, SerializationTestSupport.list((Object) null)); TestSuite.throwsType(FileOperationException.class, () -> new ParkingSlotFileHandler(file).load()); });
        s.test("30 parking save leaves no temporary file", () -> { Path file = path("parking_slots.txt"); new ParkingSlotFileHandler(file).save(List.of(TestFixtures.slot("S"))); try (var files = Files.list(file.getParent())) { TestSuite.truth(files.noneMatch(value -> value.getFileName().toString().endsWith(".tmp"))); } });
    }

    private static void vehicleStorage(TestSuite s) {
        s.test("31 missing vehicle file returns empty ArrayList", () -> { var result = new VehicleFileHandler(path("missing/registered_vehicles.txt")).load().records(); TestSuite.equal(ArrayList.class, result.getClass()); TestSuite.equal(0, result.size()); });
        s.test("32 empty vehicle file returns empty ArrayList", () -> { Path file = emptyFile("registered_vehicles.txt"); var result = new VehicleFileHandler(file).load().records(); TestSuite.equal(ArrayList.class, result.getClass()); TestSuite.equal(0, result.size()); });
        s.test("33 saving empty vehicle ArrayList succeeds", () -> { Path file = path("registered_vehicles.txt"); var handler = new VehicleFileHandler(file); handler.save(new ArrayList<>()); TestSuite.equal(0, handler.load().records().size()); TestSuite.truth(SerializationTestSupport.hasHeader(file)); });
        s.test("34 saving one Vehicle succeeds", () -> { Path file = path("registered_vehicles.txt"); var handler = new VehicleFileHandler(file); handler.save(new ArrayList<>(List.of(TestFixtures.vehicle("V1")))); TestSuite.equal(1, handler.load().records().size()); });
        s.test("35 saving multiple Vehicles succeeds", () -> { Path file = path("registered_vehicles.txt"); var handler = new VehicleFileHandler(file); handler.save(new ArrayList<>(List.of(TestFixtures.vehicle("V1"), TestFixtures.vehicle("V2")))); TestSuite.equal(2, handler.load().records().size()); });
        s.test("36 vehicle file has serialization header", () -> { Path file = path("registered_vehicles.txt"); new VehicleFileHandler(file).save(List.of(TestFixtures.vehicle("V"))); TestSuite.truth(SerializationTestSupport.hasHeader(file)); });
        s.test("37 loading restores vehicle count", () -> { Path file = path("registered_vehicles.txt"); var handler = new VehicleFileHandler(file); handler.save(List.of(TestFixtures.vehicle("A"), TestFixtures.vehicle("B"), TestFixtures.vehicle("C"))); TestSuite.equal(3, handler.load().records().size()); });
        s.test("38 vehicle numbers are preserved", () -> TestSuite.equal("ABC-123", roundTripVehicle(new Vehicle("ABC-123", "Owner", "0612345678", VehicleType.CAR, RegistrationStatus.ACTIVE)).getVehicleNumber()));
        s.test("39 vehicle owner names are preserved", () -> TestSuite.equal("Jane Smith", roundTripVehicle(new Vehicle("V", "Jane Smith", "0612345678", VehicleType.CAR, RegistrationStatus.ACTIVE)).getOwnerName()));
        s.test("40 vehicle phone numbers are preserved", () -> TestSuite.equal("+31612345678", roundTripVehicle(new Vehicle("V", "Owner", "+31612345678", VehicleType.CAR, RegistrationStatus.ACTIVE)).getPhone()));
        s.test("41 vehicle types are preserved", () -> TestSuite.equal(VehicleType.TRUCK, roundTripVehicle(new Vehicle("V", "Owner", "0612345678", VehicleType.TRUCK, RegistrationStatus.ACTIVE)).getVehicleType()));
        s.test("42 registration statuses are preserved", () -> TestSuite.equal(RegistrationStatus.INACTIVE, roundTripVehicle(new Vehicle("V", "Owner", "0612345678", VehicleType.CAR, RegistrationStatus.INACTIVE)).getRegistrationStatus()));
        s.test("43 vehicle order is preserved", () -> { Path file = path("registered_vehicles.txt"); var handler = new VehicleFileHandler(file); handler.save(List.of(TestFixtures.vehicle("FIRST"), TestFixtures.vehicle("SECOND"))); var loaded = handler.load().records(); TestSuite.equal("FIRST", loaded.get(0).getVehicleNumber()); TestSuite.equal("SECOND", loaded.get(1).getVehicleNumber()); });
        s.test("44 Unicode owner names survive", () -> TestSuite.equal("José أحمد", roundTripVehicle(new Vehicle("V", "José أحمد", "+31612345678", VehicleType.CAR, RegistrationStatus.ACTIVE)).getOwnerName()));
        s.test("45 vehicle save reload preserves equality", () -> { Vehicle value = new Vehicle("EQ", "Owner", "0612345678", VehicleType.BIKE, RegistrationStatus.INACTIVE); TestSuite.equal(value, roundTripVehicle(value)); });
        s.test("46 re-saving replaces previous vehicle list", () -> { Path file = path("registered_vehicles.txt"); var handler = new VehicleFileHandler(file); handler.save(List.of(TestFixtures.vehicle("OLD"))); handler.save(List.of(TestFixtures.vehicle("NEW"))); var loaded = handler.load().records(); TestSuite.equal(1, loaded.size()); TestSuite.equal("NEW", loaded.get(0).getVehicleNumber()); });
        s.test("46a vehicle data survives a new handler instance", () -> { Path file = path("registered_vehicles.txt"); new VehicleFileHandler(file).save(List.of(TestFixtures.vehicle("PERSISTED"))); var reopened = new VehicleFileHandler(file); TestSuite.equal("PERSISTED", reopened.load().records().get(0).getVehicleNumber()); });
        s.test("47 vehicle save creates missing parent", () -> { Path file = path("nested/data/registered_vehicles.txt"); new VehicleFileHandler(file).save(List.of(TestFixtures.vehicle("V"))); TestSuite.truth(Files.exists(file)); });
        s.test("48 corrupted serialized vehicle file is rejected", () -> { Path file = path("registered_vehicles.txt"); Files.write(file, new byte[] {(byte) 0xAC, (byte) 0xED, 0, 5, 1, 2}); TestSuite.throwsType(FileOperationException.class, () -> new VehicleFileHandler(file).load()); });
        s.test("48a non-serialized vehicle file is rejected and preserved", () -> { Path file = path("registered_vehicles.txt"); byte[] original = "not Java serialization".getBytes(StandardCharsets.UTF_8); Files.write(file, original); try { new VehicleFileHandler(file).load(); throw new AssertionError("expected FileOperationException"); } catch (FileOperationException expected) { TestSuite.truth(expected.getMessage().contains("Java serialization")); } TestSuite.truth(Arrays.equals(original, Files.readAllBytes(file))); });
        s.test("49 wrong vehicle root object is rejected", () -> { Path file = path("registered_vehicles.txt"); SerializationTestSupport.writeObject(file, "not an ArrayList"); TestSuite.throwsType(FileOperationException.class, () -> new VehicleFileHandler(file).load()); });
        s.test("50 wrong vehicle list element is rejected", () -> { Path file = path("registered_vehicles.txt"); SerializationTestSupport.writeObject(file, SerializationTestSupport.list(TestFixtures.slot("notVehicle"))); TestSuite.throwsType(FileOperationException.class, () -> new VehicleFileHandler(file).load()); });
        s.test("51 vehicle save leaves no temporary file", () -> { Path file = path("registered_vehicles.txt"); new VehicleFileHandler(file).save(List.of(TestFixtures.vehicle("V"))); try (var files = Files.list(file.getParent())) { TestSuite.truth(files.noneMatch(value -> value.getFileName().toString().endsWith(".tmp"))); } });
    }

    private static ParkingSlot roundTripSlot(ParkingSlot value) throws Exception {
        Path file = path("parking_slots.txt"); var handler = new ParkingSlotFileHandler(file);
        handler.save(List.of(value)); return handler.load().records().get(0);
    }
    private static Vehicle roundTripVehicle(Vehicle value) throws Exception {
        Path file = path("registered_vehicles.txt"); var handler = new VehicleFileHandler(file);
        handler.save(List.of(value)); return handler.load().records().get(0);
    }
    private static Path path(String relative) throws Exception { return TestFixtures.directory().resolve(relative); }
    private static Path emptyFile(String name) throws Exception { Path file = path(name); Files.createFile(file); return file; }
}
