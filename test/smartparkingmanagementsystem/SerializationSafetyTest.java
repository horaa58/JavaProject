package smartparkingmanagementsystem;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import smartparkingmanagementsystem.filehandling.FileOperationException;
import smartparkingmanagementsystem.filehandling.ParkingSlotFileHandler;
import smartparkingmanagementsystem.filehandling.VehicleFileHandler;
import smartparkingmanagementsystem.util.AppPaths;

final class SerializationSafetyTest {
    static void register(TestSuite s, ProductionDataSnapshot productionSnapshot) {
        s.test("76 all persistence tests use temporary paths", () -> { Path production = AppPaths.dataDirectory(); TestSuite.truth(!TestFixtures.directories().isEmpty()); for (Path directory : TestFixtures.directories()) TestSuite.truth(!directory.startsWith(production)); });
        s.test("77 tests do not modify production files", () -> TestSuite.truth(productionSnapshot.unchanged()));
        s.test("78 failed temporary write preserves valid file", () -> { Path file = TestFixtures.directory().resolve("parking_slots.txt"); var handler = new ParkingSlotFileHandler(file); handler.save(List.of(TestFixtures.slot("SAFE"))); byte[] valid = Files.readAllBytes(file); TestSuite.throwsType(FileOperationException.class, () -> saveWrongElement(handler)); TestSuite.truth(Arrays.equals(valid, Files.readAllBytes(file))); TestSuite.equal("SAFE", handler.load().records().get(0).getSlotId()); });
        s.test("79 corrupted test file is preserved", () -> { Path file = TestFixtures.directory().resolve("registered_vehicles.txt"); byte[] corrupted = {(byte) 0xAC, (byte) 0xED, 0, 5, 9, 8, 7}; Files.write(file, corrupted); TestSuite.throwsType(FileOperationException.class, () -> new VehicleFileHandler(file).load()); TestSuite.truth(Arrays.equals(corrupted, Files.readAllBytes(file))); });
        s.test("80 serialized outputs begin AC ED 00 05", () -> { Path directory = TestFixtures.directory(); Path slots = directory.resolve("parking_slots.txt"); Path vehicles = directory.resolve("registered_vehicles.txt"); new ParkingSlotFileHandler(slots).save(List.of(TestFixtures.slot("S"))); new VehicleFileHandler(vehicles).save(List.of(TestFixtures.vehicle("V"))); TestSuite.truth(SerializationTestSupport.hasHeader(slots)); TestSuite.truth(SerializationTestSupport.hasHeader(vehicles)); });
        s.test("81 startup initialization creates serialized empty lists", () -> { Path directory = TestFixtures.directory(); Path slots = directory.resolve("parking_slots.txt"); Path vehicles = directory.resolve("registered_vehicles.txt"); var slotHandler = new ParkingSlotFileHandler(slots); var vehicleHandler = new VehicleFileHandler(vehicles); slotHandler.ensureFileExists(); vehicleHandler.ensureFileExists(); TestSuite.truth(SerializationTestSupport.hasHeader(slots)); TestSuite.truth(SerializationTestSupport.hasHeader(vehicles)); TestSuite.equal(0, slotHandler.load().records().size()); TestSuite.equal(0, vehicleHandler.load().records().size()); });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void saveWrongElement(ParkingSlotFileHandler handler) {
        List wrong = SerializationTestSupport.list("not a ParkingSlot");
        handler.save(wrong);
    }
}
