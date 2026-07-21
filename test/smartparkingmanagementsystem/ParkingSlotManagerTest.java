package smartparkingmanagementsystem;

import java.nio.file.Path;
import smartparkingmanagementsystem.manager.ParkingSlotManager;
import smartparkingmanagementsystem.model.ParkingSlot;
import smartparkingmanagementsystem.model.SlotStatus;
import smartparkingmanagementsystem.model.SlotType;

final class ParkingSlotManagerTest {
    static void register(TestSuite s) {
        s.test("01 add a valid parking slot", () -> { var m = manager(); m.addParkingSlot(TestFixtures.slot("S001")); TestSuite.equal(1, m.getAllParkingSlots().size()); });
        s.test("02 reject duplicate slot ID", () -> { var m = manager(); m.addParkingSlot(TestFixtures.slot("S001")); TestSuite.rejects(() -> m.addParkingSlot(TestFixtures.slot("s001"))); });
        s.test("03 reject empty slot ID", () -> TestSuite.rejects(() -> manager().addParkingSlot(TestFixtures.slot(" "))));
        s.test("04 reject negative floor", () -> TestSuite.rejects(() -> manager().addParkingSlot(new ParkingSlot("S", -1, SlotType.SMALL, SlotStatus.AVAILABLE, 1))));
        s.test("05 reject nonpositive rate", () -> { TestSuite.rejects(() -> manager().addParkingSlot(new ParkingSlot("S", 0, SlotType.SMALL, SlotStatus.AVAILABLE, 0))); TestSuite.rejects(() -> manager().addParkingSlot(new ParkingSlot("S", 0, SlotType.SMALL, SlotStatus.AVAILABLE, -1))); });
        s.test("06 find a slot by ID", () -> { var m = manager(); m.addParkingSlot(TestFixtures.slot("S001")); TestSuite.truth(m.findById("s001").isPresent()); });
        s.test("07 update slot details", () -> { var m = manager(); m.addParkingSlot(TestFixtures.slot("S001")); m.updateParkingSlot("S001", new ParkingSlot("S009", 4, SlotType.LARGE, SlotStatus.RESERVED, 7)); TestSuite.equal(4, m.findById("S009").orElseThrow().getFloorNumber()); });
        s.test("08 update slot status", () -> { var m = manager(); m.addParkingSlot(TestFixtures.slot("S001")); m.updateSlotStatus("S001", SlotStatus.OCCUPIED); TestSuite.equal(SlotStatus.OCCUPIED, m.findById("S001").orElseThrow().getSlotStatus()); });
        s.test("09 delete existing slot", () -> { var m = manager(); m.addParkingSlot(TestFixtures.slot("S001")); TestSuite.truth(m.deleteParkingSlot("S001")); TestSuite.equal(0, m.getAllParkingSlots().size()); });
        s.test("10 delete nonexistent slot", () -> TestSuite.truth(!manager().deleteParkingSlot("MISSING")));
        s.test("11 search partial slot ID", () -> { var m = manager(); m.addParkingSlot(TestFixtures.slot("ZONE-A-12")); TestSuite.equal(1, m.searchParkingSlots("a-1").size()); });
        s.test("12 search floor", () -> { var m = manager(); m.addParkingSlot(new ParkingSlot("S", 8, SlotType.SMALL, SlotStatus.AVAILABLE, 1)); TestSuite.equal(1, m.searchParkingSlots("8").size()); });
        s.test("13 search slot type", () -> { var m = manager(); m.addParkingSlot(new ParkingSlot("S", 0, SlotType.ELECTRIC, SlotStatus.AVAILABLE, 1)); TestSuite.equal(1, m.searchParkingSlots("electric").size()); });
        s.test("14 search slot status", () -> { var m = manager(); m.addParkingSlot(new ParkingSlot("S", 0, SlotType.SMALL, SlotStatus.UNDER_MAINTENANCE, 1)); TestSuite.equal(1, m.searchParkingSlots("maintenance").size()); });
        s.test("15 count slots by status", () -> { var m = manager(); m.addParkingSlot(TestFixtures.slot("A")); m.addParkingSlot(TestFixtures.slot("B")); TestSuite.equal(2L, m.countByStatus(SlotStatus.AVAILABLE)); });
        s.test("16 case-insensitive slot search", () -> { var m = manager(); m.addParkingSlot(TestFixtures.slot("Alpha")); TestSuite.equal(1, m.searchParkingSlots("ALPHA").size()); });
    }
    private static ParkingSlotManager manager() throws Exception { Path file = TestFixtures.directory().resolve("parking_slots.txt"); return TestFixtures.slots(file); }
}
