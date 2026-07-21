package smartparkingmanagementsystem;

import smartparkingmanagementsystem.manager.ModuleOneReportManager;
import smartparkingmanagementsystem.model.ParkingSlot;
import smartparkingmanagementsystem.model.SlotStatus;
import smartparkingmanagementsystem.model.SlotType;

final class ModuleOneReportManagerTest {
    static void register(TestSuite s) {
        s.test("43 calculate total slots", () -> {
            var f = fixture();
            f.slots.addParkingSlot(TestFixtures.slot("A"));
            TestSuite.equal(1, f.report().totalSlots());
        });
        s.test("44 calculate all required status counts", () -> {
            var f = fixture();
            int id = 1;
            for (SlotStatus status : SlotStatus.values()) {
                f.slots.addParkingSlot(new ParkingSlot("S" + id++, 0, SlotType.SMALL, status, 2));
            }
            for (SlotStatus status : SlotStatus.values()) {
                TestSuite.equal(1L, f.report().slotsByStatus().get(status));
            }
        });
        s.test("45 calculate total registered vehicles", () -> {
            var f = fixture();
            f.vehicles.addVehicle(TestFixtures.vehicle("A"));
            f.vehicles.addVehicle(TestFixtures.vehicle("B"));
            TestSuite.equal(2, f.report().totalVehicles());
        });
        s.test("46 empty report values are zero", () -> {
            var f = fixture();
            TestSuite.equal(0, f.report().totalSlots());
            TestSuite.equal(0, f.report().totalVehicles());
            for (SlotStatus status : SlotStatus.values()) {
                TestSuite.equal(0L, f.report().slotsByStatus().get(status));
            }
        });
    }

    private static Fixture fixture() throws Exception {
        var directory = TestFixtures.directory();
        return new Fixture(TestFixtures.slots(directory.resolve("s.txt")),
                TestFixtures.vehicles(directory.resolve("v.txt")));
    }

    private record Fixture(smartparkingmanagementsystem.manager.ParkingSlotManager slots,
            smartparkingmanagementsystem.manager.VehicleManager vehicles) {
        ModuleOneReportManager.ReportSnapshot report() {
            return new ModuleOneReportManager(slots, vehicles).generate();
        }
    }
}
