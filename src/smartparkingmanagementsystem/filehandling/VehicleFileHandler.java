package smartparkingmanagementsystem.filehandling;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import smartparkingmanagementsystem.model.Vehicle;
import smartparkingmanagementsystem.util.ValidationUtils;

/** Binary Java serialization storage for the complete registered-vehicle ArrayList. */
public final class VehicleFileHandler extends SerializedArrayListFileHandler<Vehicle> {
    public VehicleFileHandler(Path file) {
        super(file, Vehicle.class, "registered vehicle");
    }

    @Override protected void validateRecords(ArrayList<Vehicle> records) {
        Set<String> identifiers = new HashSet<>();
        for (Vehicle vehicle : records) {
            String identifier = ValidationUtils.normalizeIdentifier(vehicle.getVehicleNumber(), "Vehicle number");
            ValidationUtils.requireText(vehicle.getOwnerName(), "Owner name");
            ValidationUtils.validatePhone(vehicle.getPhone());
            if (vehicle.getVehicleType() == null) throw new IllegalArgumentException("Vehicle type is required.");
            if (vehicle.getRegistrationStatus() == null) {
                throw new IllegalArgumentException("Registration status is required.");
            }
            if (!identifiers.add(identifier)) throw new IllegalArgumentException("Duplicate vehicle number.");
        }
    }
}
