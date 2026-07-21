package smartparkingmanagementsystem;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import smartparkingmanagementsystem.util.AppPaths;

record ProductionDataSnapshot(FileState slots, FileState vehicles) {
    static ProductionDataSnapshot capture() throws Exception {
        return new ProductionDataSnapshot(FileState.capture(AppPaths.parkingSlotsFile()),
                FileState.capture(AppPaths.vehiclesFile()));
    }

    boolean unchanged() throws Exception {
        return slots.matchesCurrent() && vehicles.matchesCurrent();
    }

    record FileState(Path path, boolean existed, byte[] bytes) {
        static FileState capture(Path path) throws Exception {
            boolean exists = Files.exists(path);
            return new FileState(path, exists, exists ? Files.readAllBytes(path) : new byte[0]);
        }
        boolean matchesCurrent() throws Exception {
            return existed == Files.exists(path) && (!existed || Arrays.equals(bytes, Files.readAllBytes(path)));
        }
    }
}
