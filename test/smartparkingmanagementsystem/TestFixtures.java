package smartparkingmanagementsystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import smartparkingmanagementsystem.filehandling.ParkingSlotFileHandler;
import smartparkingmanagementsystem.filehandling.VehicleFileHandler;
import smartparkingmanagementsystem.manager.ParkingSlotManager;
import smartparkingmanagementsystem.manager.VehicleManager;
import smartparkingmanagementsystem.model.ParkingSlot;
import smartparkingmanagementsystem.model.RegistrationStatus;
import smartparkingmanagementsystem.model.SlotStatus;
import smartparkingmanagementsystem.model.SlotType;
import smartparkingmanagementsystem.model.Vehicle;
import smartparkingmanagementsystem.model.VehicleType;

final class TestFixtures {
    private static final List<Path> DIRECTORIES = new ArrayList<>();
    private TestFixtures() { }
    static synchronized Path directory() throws IOException {
        Path directory = Files.createTempDirectory("smart-parking-module1-").toAbsolutePath().normalize();
        DIRECTORIES.add(directory);
        return directory;
    }
    static synchronized List<Path> directories() { return List.copyOf(DIRECTORIES); }
    static ParkingSlot slot(String id) { return new ParkingSlot(id, 1, SlotType.MEDIUM, SlotStatus.AVAILABLE, 3.5); }
    static Vehicle vehicle(String number) { return new Vehicle(number, "Jane Doe", "+31612345678", VehicleType.CAR, RegistrationStatus.ACTIVE); }
    static ParkingSlotManager slots(Path file) { ParkingSlotManager value = new ParkingSlotManager(new ParkingSlotFileHandler(file)); value.reloadFromFile(); return value; }
    static VehicleManager vehicles(Path file) { VehicleManager value = new VehicleManager(new VehicleFileHandler(file)); value.reloadFromFile(); return value; }
}
