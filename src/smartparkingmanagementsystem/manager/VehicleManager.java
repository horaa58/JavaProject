package smartparkingmanagementsystem.manager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import smartparkingmanagementsystem.filehandling.LoadResult;
import smartparkingmanagementsystem.filehandling.VehicleFileHandler;
import smartparkingmanagementsystem.model.RegistrationStatus;
import smartparkingmanagementsystem.model.Vehicle;
import smartparkingmanagementsystem.model.VehicleType;
import smartparkingmanagementsystem.util.ValidationUtils;

/** Validates, queries, and persists registered vehicle master records. */
public final class VehicleManager {
    private final VehicleFileHandler fileHandler;
    private final List<Vehicle> vehicles = new ArrayList<>();

    public VehicleManager(VehicleFileHandler fileHandler) { this.fileHandler = fileHandler; }

    public synchronized void addVehicle(Vehicle vehicle) {
        Vehicle valid = validate(vehicle);
        if (findByVehicleNumber(valid.getVehicleNumber()).isPresent()) {
            throw new IllegalArgumentException("A vehicle with this number already exists.");
        }
        mutate(() -> vehicles.add(valid));
    }

    public synchronized void updateVehicle(String originalNumber, Vehicle replacement) {
        String key = ValidationUtils.normalizeIdentifier(originalNumber, "Vehicle number");
        Vehicle valid = validate(replacement);
        int index = indexOf(key);
        if (index < 0) throw new IllegalArgumentException("Vehicle was not found.");
        int duplicate = indexOf(valid.getVehicleNumber());
        if (duplicate >= 0 && duplicate != index) {
            throw new IllegalArgumentException("A vehicle with this number already exists.");
        }
        mutate(() -> vehicles.set(index, valid));
    }

    public synchronized boolean deleteVehicle(String number) {
        int index = indexOf(ValidationUtils.normalizeIdentifier(number, "Vehicle number"));
        if (index < 0) return false;
        mutate(() -> vehicles.remove(index));
        return true;
    }

    public synchronized void updateRegistrationStatus(String number, RegistrationStatus status) {
        if (status == null) throw new IllegalArgumentException("Registration status is required.");
        int index = indexOf(ValidationUtils.normalizeIdentifier(number, "Vehicle number"));
        if (index < 0) throw new IllegalArgumentException("Vehicle was not found.");
        mutate(() -> vehicles.set(index, vehicles.get(index).withStatus(status)));
    }

    public synchronized Optional<Vehicle> findByVehicleNumber(String number) {
        if (number == null) return Optional.empty();
        String key = number.trim().toUpperCase(Locale.ROOT);
        return vehicles.stream().filter(vehicle -> vehicle.getVehicleNumber().equalsIgnoreCase(key)).findFirst();
    }

    public synchronized List<Vehicle> getAllVehicles() { return List.copyOf(vehicles); }

    public synchronized List<Vehicle> searchVehicles(String query) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) return getAllVehicles();
        return vehicles.stream().filter(vehicle -> vehicle.getVehicleNumber().toLowerCase(Locale.ROOT).contains(q)
                || vehicle.getOwnerName().toLowerCase(Locale.ROOT).contains(q)
                || vehicle.getPhone().toLowerCase(Locale.ROOT).contains(q)
                || vehicle.getVehicleType().name().toLowerCase(Locale.ROOT).contains(q)
                || vehicle.getRegistrationStatus().name().toLowerCase(Locale.ROOT).contains(q)).toList();
    }

    public synchronized List<Vehicle> searchVehicles(String query, VehicleType type,
            RegistrationStatus status) {
        return searchVehicles(query).stream()
                .filter(vehicle -> type == null || vehicle.getVehicleType() == type)
                .filter(vehicle -> status == null || vehicle.getRegistrationStatus() == status).toList();
    }

    public synchronized long countByRegistrationStatus(RegistrationStatus status) {
        return vehicles.stream().filter(vehicle -> vehicle.getRegistrationStatus() == status).count();
    }

    public synchronized Map<RegistrationStatus, Long> countByRegistrationStatus() {
        Map<RegistrationStatus, Long> result = new EnumMap<>(RegistrationStatus.class);
        for (RegistrationStatus status : RegistrationStatus.values()) {
            result.put(status, countByRegistrationStatus(status));
        }
        return result;
    }

    public synchronized void reloadFromFile() {
        LoadResult<Vehicle> result = fileHandler.load();
        vehicles.clear();
        for (Vehicle vehicle : result.records()) {
            try {
                Vehicle valid = validate(vehicle);
                if (indexOf(valid.getVehicleNumber()) < 0) vehicles.add(valid);
            } catch (IllegalArgumentException ex) {
                // Defensive manager validation; serialized handlers already reject invalid collections.
            }
        }
    }

    public synchronized void saveToFile() { fileHandler.save(vehicles); }

    private Vehicle validate(Vehicle vehicle) {
        if (vehicle == null) throw new IllegalArgumentException("Vehicle is required.");
        if (vehicle.getVehicleType() == null) throw new IllegalArgumentException("Vehicle type is required.");
        if (vehicle.getRegistrationStatus() == null) {
            throw new IllegalArgumentException("Registration status is required.");
        }
        return new Vehicle(ValidationUtils.normalizeIdentifier(vehicle.getVehicleNumber(), "Vehicle number"),
                ValidationUtils.requireText(vehicle.getOwnerName(), "Owner name"),
                ValidationUtils.validatePhone(vehicle.getPhone()), vehicle.getVehicleType(),
                vehicle.getRegistrationStatus());
    }

    private int indexOf(String number) {
        for (int index = 0; index < vehicles.size(); index++) {
            if (vehicles.get(index).getVehicleNumber().equalsIgnoreCase(number)) return index;
        }
        return -1;
    }

    private void mutate(Runnable change) {
        List<Vehicle> before = new ArrayList<>(vehicles);
        change.run();
        try { saveToFile(); }
        catch (RuntimeException ex) { vehicles.clear(); vehicles.addAll(before); throw ex; }
    }
}
