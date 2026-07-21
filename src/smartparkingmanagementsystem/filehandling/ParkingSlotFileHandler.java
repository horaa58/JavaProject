package smartparkingmanagementsystem.filehandling;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import smartparkingmanagementsystem.model.ParkingSlot;
import smartparkingmanagementsystem.util.ValidationUtils;

/** Binary Java serialization storage for the complete parking-slot ArrayList. */
public final class ParkingSlotFileHandler extends SerializedArrayListFileHandler<ParkingSlot> {
    public ParkingSlotFileHandler(Path file) {
        super(file, ParkingSlot.class, "parking slot");
    }

    @Override protected void validateRecords(ArrayList<ParkingSlot> records) {
        Set<String> identifiers = new HashSet<>();
        for (ParkingSlot slot : records) {
            String identifier = ValidationUtils.normalizeIdentifier(slot.getSlotId(), "Slot ID");
            ValidationUtils.validateFloor(slot.getFloorNumber());
            ValidationUtils.validateRate(slot.getHourlyRate());
            if (slot.getSlotType() == null) throw new IllegalArgumentException("Slot type is required.");
            if (slot.getSlotStatus() == null) throw new IllegalArgumentException("Slot status is required.");
            if (!identifiers.add(identifier)) throw new IllegalArgumentException("Duplicate slot ID.");
        }
    }
}
