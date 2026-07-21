package smartparkingmanagementsystem.model;

import java.io.Serializable;
import java.util.Objects;

/** Master record for one physical parking slot. */
public final class ParkingSlot implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String slotId;
    private final int floorNumber;
    private final SlotType slotType;
    private final SlotStatus slotStatus;
    private final double hourlyRate;

    public ParkingSlot(String slotId, int floorNumber, SlotType slotType,
            SlotStatus slotStatus, double hourlyRate) {
        this.slotId = slotId;
        this.floorNumber = floorNumber;
        this.slotType = slotType;
        this.slotStatus = slotStatus;
        this.hourlyRate = hourlyRate;
    }

    public String getSlotId() { return slotId; }
    public int getFloorNumber() { return floorNumber; }
    public SlotType getSlotType() { return slotType; }
    public SlotStatus getSlotStatus() { return slotStatus; }
    public double getHourlyRate() { return hourlyRate; }

    public ParkingSlot withStatus(SlotStatus status) {
        return new ParkingSlot(slotId, floorNumber, slotType, status, hourlyRate);
    }

    @Override public boolean equals(Object value) {
        if (this == value) return true;
        if (!(value instanceof ParkingSlot other)) return false;
        return floorNumber == other.floorNumber
                && Double.compare(hourlyRate, other.hourlyRate) == 0
                && Objects.equals(slotId, other.slotId)
                && slotType == other.slotType && slotStatus == other.slotStatus;
    }

    @Override public int hashCode() {
        return Objects.hash(slotId, floorNumber, slotType, slotStatus, hourlyRate);
    }

    @Override public String toString() {
        return "ParkingSlot{slotId='" + slotId + "', floorNumber=" + floorNumber
                + ", slotType=" + slotType + ", slotStatus=" + slotStatus
                + ", hourlyRate=" + hourlyRate + "}";
    }
}
