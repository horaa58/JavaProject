package smartparkingmanagementsystem;

import java.nio.file.Path;
import java.util.ArrayList;
import smartparkingmanagementsystem.manager.ModuleOneReportManager;
import smartparkingmanagementsystem.model.ParkingSlot;
import smartparkingmanagementsystem.model.RegistrationStatus;
import smartparkingmanagementsystem.model.SlotStatus;
import smartparkingmanagementsystem.model.SlotType;
import smartparkingmanagementsystem.model.Vehicle;
import smartparkingmanagementsystem.model.VehicleType;

final class IntegrationTest {
    static void register(TestSuite s) {
        s.test("52 add slot save reload verify", () -> { Path file = path("parking_slots.txt"); var manager = TestFixtures.slots(file); manager.addParkingSlot(TestFixtures.slot("A")); TestSuite.truth(TestFixtures.slots(file).findById("A").isPresent()); });
        s.test("53 update slot save reload verify", () -> { Path file = path("parking_slots.txt"); var manager = TestFixtures.slots(file); manager.addParkingSlot(TestFixtures.slot("A")); manager.updateParkingSlot("A", new ParkingSlot("A", 4, SlotType.LARGE, SlotStatus.RESERVED, 8)); ParkingSlot loaded = TestFixtures.slots(file).findById("A").orElseThrow(); TestSuite.equal(4, loaded.getFloorNumber()); TestSuite.equal(SlotType.LARGE, loaded.getSlotType()); });
        s.test("54 delete slot save reload verify", () -> { Path file = path("parking_slots.txt"); var manager = TestFixtures.slots(file); manager.addParkingSlot(TestFixtures.slot("A")); manager.deleteParkingSlot("A"); TestSuite.equal(0, TestFixtures.slots(file).getAllParkingSlots().size()); });
        s.test("55 change slot status save reload verify", () -> { Path file = path("parking_slots.txt"); var manager = TestFixtures.slots(file); manager.addParkingSlot(TestFixtures.slot("A")); manager.updateSlotStatus("A", SlotStatus.OCCUPIED); TestSuite.equal(SlotStatus.OCCUPIED, TestFixtures.slots(file).findById("A").orElseThrow().getSlotStatus()); });
        s.test("56 register vehicle save reload verify", () -> { Path file = path("registered_vehicles.txt"); var manager = TestFixtures.vehicles(file); manager.addVehicle(TestFixtures.vehicle("A")); TestSuite.truth(TestFixtures.vehicles(file).findByVehicleNumber("A").isPresent()); });
        s.test("57 update vehicle save reload verify", () -> { Path file = path("registered_vehicles.txt"); var manager = TestFixtures.vehicles(file); manager.addVehicle(TestFixtures.vehicle("A")); manager.updateVehicle("A", new Vehicle("A", "Updated Owner", "0612345678", VehicleType.VAN, RegistrationStatus.INACTIVE)); Vehicle loaded = TestFixtures.vehicles(file).findByVehicleNumber("A").orElseThrow(); TestSuite.equal("Updated Owner", loaded.getOwnerName()); TestSuite.equal(VehicleType.VAN, loaded.getVehicleType()); });
        s.test("58 delete vehicle save reload verify", () -> { Path file = path("registered_vehicles.txt"); var manager = TestFixtures.vehicles(file); manager.addVehicle(TestFixtures.vehicle("A")); manager.deleteVehicle("A"); TestSuite.equal(0, TestFixtures.vehicles(file).getAllVehicles().size()); });
        s.test("59 change registration status save reload verify", () -> { Path file = path("registered_vehicles.txt"); var manager = TestFixtures.vehicles(file); manager.addVehicle(TestFixtures.vehicle("A")); manager.updateRegistrationStatus("A", RegistrationStatus.INACTIVE); TestSuite.equal(RegistrationStatus.INACTIVE, TestFixtures.vehicles(file).findByVehicleNumber("A").orElseThrow().getRegistrationStatus()); });
        s.test("60 search works after serialized reload", () -> { Path directory = TestFixtures.directory(); Path slotsFile = directory.resolve("parking_slots.txt"); Path vehiclesFile = directory.resolve("registered_vehicles.txt"); var slots = TestFixtures.slots(slotsFile); var vehicles = TestFixtures.vehicles(vehiclesFile); slots.addParkingSlot(TestFixtures.slot("ZONE-ALPHA")); vehicles.addVehicle(new Vehicle("V", "Emma Davis", "0612345678", VehicleType.CAR, RegistrationStatus.ACTIVE)); TestSuite.equal(1, TestFixtures.slots(slotsFile).searchParkingSlots("alpha").size()); TestSuite.equal(1, TestFixtures.vehicles(vehiclesFile).searchVehicles("emma").size()); });
        s.test("61 reports work after serialized reload", () -> { Path directory = TestFixtures.directory(); Path slotsFile = directory.resolve("parking_slots.txt"); Path vehiclesFile = directory.resolve("registered_vehicles.txt"); var slots = TestFixtures.slots(slotsFile); var vehicles = TestFixtures.vehicles(vehiclesFile); slots.addParkingSlot(TestFixtures.slot("A")); vehicles.addVehicle(TestFixtures.vehicle("V")); var report = new ModuleOneReportManager(TestFixtures.slots(slotsFile), TestFixtures.vehicles(vehiclesFile)).generate(); TestSuite.equal(1, report.totalSlots()); TestSuite.equal(1, report.totalVehicles()); TestSuite.equal(1L, report.slotsByStatus().get(SlotStatus.AVAILABLE)); });
        s.test("62 AVAILABLE remains exact enum value", () -> { Path file = path("parking_slots.txt"); var manager = TestFixtures.slots(file); manager.addParkingSlot(new ParkingSlot("A", 0, SlotType.SMALL, SlotStatus.AVAILABLE, 2)); Object root = SerializationTestSupport.readObject(file); ParkingSlot slot = (ParkingSlot) ((ArrayList<?>) root).get(0); TestSuite.truth(slot.getSlotStatus() == SlotStatus.AVAILABLE); });
        s.test("63 ACTIVE remains exact enum value", () -> { Path file = path("registered_vehicles.txt"); var manager = TestFixtures.vehicles(file); manager.addVehicle(new Vehicle("A", "Owner", "0612345678", VehicleType.CAR, RegistrationStatus.ACTIVE)); Object root = SerializationTestSupport.readObject(file); Vehicle vehicle = (Vehicle) ((ArrayList<?>) root).get(0); TestSuite.truth(vehicle.getRegistrationStatus() == RegistrationStatus.ACTIVE); });
    }

    private static Path path(String name) throws Exception { return TestFixtures.directory().resolve(name); }
}
