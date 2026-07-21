package smartparkingmanagementsystem;

import java.nio.file.Path;
import smartparkingmanagementsystem.manager.VehicleManager;
import smartparkingmanagementsystem.model.RegistrationStatus;
import smartparkingmanagementsystem.model.Vehicle;
import smartparkingmanagementsystem.model.VehicleType;

final class VehicleManagerTest {
    static void register(TestSuite s) {
        s.test("17 register valid vehicle", () -> { var m = manager(); m.addVehicle(TestFixtures.vehicle("ABC123")); TestSuite.equal(1, m.getAllVehicles().size()); });
        s.test("18 reject duplicate vehicle number", () -> { var m = manager(); m.addVehicle(TestFixtures.vehicle("ABC123")); TestSuite.rejects(() -> m.addVehicle(TestFixtures.vehicle("abc123"))); });
        s.test("19 reject empty owner name", () -> TestSuite.rejects(() -> manager().addVehicle(new Vehicle("A", "", "+31612345678", VehicleType.CAR, RegistrationStatus.ACTIVE))));
        s.test("20 reject invalid phone", () -> TestSuite.rejects(() -> manager().addVehicle(new Vehicle("A", "Owner", "abc", VehicleType.CAR, RegistrationStatus.ACTIVE))));
        s.test("21 find vehicle by number", () -> { var m = manager(); m.addVehicle(TestFixtures.vehicle("ABC123")); TestSuite.truth(m.findByVehicleNumber("abc123").isPresent()); });
        s.test("22 update vehicle details", () -> { var m = manager(); m.addVehicle(TestFixtures.vehicle("A")); m.updateVehicle("A", new Vehicle("B", "New Owner", "0612345678", VehicleType.VAN, RegistrationStatus.INACTIVE)); TestSuite.equal("New Owner", m.findByVehicleNumber("B").orElseThrow().getOwnerName()); });
        s.test("23 delete vehicle", () -> { var m = manager(); m.addVehicle(TestFixtures.vehicle("A")); TestSuite.truth(m.deleteVehicle("A")); });
        s.test("24 search partial owner", () -> { var m = manager(); m.addVehicle(new Vehicle("A", "Emma Davis", "0612345678", VehicleType.BIKE, RegistrationStatus.ACTIVE)); TestSuite.equal(1, m.searchVehicles("davi").size()); });
        s.test("25 search phone", () -> { var m = manager(); m.addVehicle(TestFixtures.vehicle("A")); TestSuite.equal(1, m.searchVehicles("61234").size()); });
        s.test("26 search vehicle type", () -> { var m = manager(); m.addVehicle(new Vehicle("A", "Owner", "0612345678", VehicleType.TRUCK, RegistrationStatus.ACTIVE)); TestSuite.equal(1, m.searchVehicles("truck").size()); });
        s.test("27 search registration status", () -> { var m = manager(); m.addVehicle(new Vehicle("A", "Owner", "0612345678", VehicleType.CAR, RegistrationStatus.INACTIVE)); TestSuite.equal(1, m.searchVehicles("inactive").size()); });
        s.test("28 case-insensitive vehicle search", () -> { var m = manager(); m.addVehicle(new Vehicle("A", "McDONALD", "0612345678", VehicleType.CAR, RegistrationStatus.ACTIVE)); TestSuite.equal(1, m.searchVehicles("mcdonald").size()); });
    }
    private static VehicleManager manager() throws Exception { Path file = TestFixtures.directory().resolve("registered_vehicles.txt"); return TestFixtures.vehicles(file); }
}
