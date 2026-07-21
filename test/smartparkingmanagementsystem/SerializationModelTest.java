package smartparkingmanagementsystem;

import java.io.ObjectStreamClass;
import java.io.Serializable;
import smartparkingmanagementsystem.model.ParkingSlot;
import smartparkingmanagementsystem.model.RegistrationStatus;
import smartparkingmanagementsystem.model.SlotStatus;
import smartparkingmanagementsystem.model.SlotType;
import smartparkingmanagementsystem.model.Vehicle;
import smartparkingmanagementsystem.model.VehicleType;

final class SerializationModelTest {
    static void register(TestSuite s) {
        s.test("01 ParkingSlot implements Serializable", () -> TestSuite.truth(Serializable.class.isAssignableFrom(ParkingSlot.class)));
        s.test("02 Vehicle implements Serializable", () -> TestSuite.truth(Serializable.class.isAssignableFrom(Vehicle.class)));
        s.test("03 ParkingSlot serialVersionUID is 1L", () -> TestSuite.equal(1L, ObjectStreamClass.lookup(ParkingSlot.class).getSerialVersionUID()));
        s.test("04 Vehicle serialVersionUID is 1L", () -> TestSuite.equal(1L, ObjectStreamClass.lookup(Vehicle.class).getSerialVersionUID()));
        s.test("05 ParkingSlot survives object round trip", () -> { ParkingSlot value = new ParkingSlot("S1", 2, SlotType.ELECTRIC, SlotStatus.RESERVED, 5.5); TestSuite.equal(value, SerializationTestSupport.roundTrip(value, ParkingSlot.class)); });
        s.test("06 Vehicle survives object round trip", () -> { Vehicle value = new Vehicle("V1", "Owner", "+31612345678", VehicleType.VAN, RegistrationStatus.INACTIVE); TestSuite.equal(value, SerializationTestSupport.roundTrip(value, Vehicle.class)); });
        s.test("07 enum fields survive serialization", () -> { ParkingSlot slot = SerializationTestSupport.roundTrip(new ParkingSlot("S", 0, SlotType.LARGE, SlotStatus.UNDER_MAINTENANCE, 2), ParkingSlot.class); Vehicle vehicle = SerializationTestSupport.roundTrip(new Vehicle("V", "Owner", "0612345678", VehicleType.TRUCK, RegistrationStatus.ACTIVE), Vehicle.class); TestSuite.equal(SlotType.LARGE, slot.getSlotType()); TestSuite.equal(SlotStatus.UNDER_MAINTENANCE, slot.getSlotStatus()); TestSuite.equal(VehicleType.TRUCK, vehicle.getVehicleType()); TestSuite.equal(RegistrationStatus.ACTIVE, vehicle.getRegistrationStatus()); });
        s.test("08 equals works after deserialization", () -> { ParkingSlot slot = TestFixtures.slot("EQ"); Vehicle vehicle = TestFixtures.vehicle("EQ"); TestSuite.truth(slot.equals(SerializationTestSupport.roundTrip(slot, ParkingSlot.class))); TestSuite.truth(vehicle.equals(SerializationTestSupport.roundTrip(vehicle, Vehicle.class))); });
    }
}
