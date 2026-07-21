package smartparkingmanagementsystem.util;

import java.nio.file.Path;

public final class AppPaths {
    private AppPaths() { }
    public static Path dataDirectory() { return Path.of("data").toAbsolutePath().normalize(); }
    public static Path parkingSlotsFile() { return dataDirectory().resolve("parking_slots.txt"); }
    public static Path vehiclesFile() { return dataDirectory().resolve("registered_vehicles.txt"); }
}
