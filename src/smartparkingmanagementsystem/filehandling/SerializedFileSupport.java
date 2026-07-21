package smartparkingmanagementsystem.filehandling;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Focused filesystem operations shared by binary serialized stores. */
final class SerializedFileSupport {
    private static final int[] STREAM_HEADER = {0xAC, 0xED, 0x00, 0x05};

    private SerializedFileSupport() { }

    static void ensureParentDirectory(Path file) throws IOException {
        Path parent = file.toAbsolutePath().normalize().getParent();
        if (parent != null) Files.createDirectories(parent);
    }

    /** Inspects a fresh stream without consuming the stream later used by ObjectInputStream. */
    static boolean hasSerializationHeader(Path file) throws IOException {
        if (Files.notExists(file) || Files.size(file) < STREAM_HEADER.length) return false;
        try (FileInputStream fileInputStream = new FileInputStream(file.toFile())) {
            for (int expected : STREAM_HEADER) {
                if (fileInputStream.read() != expected) return false;
            }
            return true;
        }
    }

    static void replace(Path temporary, Path destination) throws IOException {
        try {
            Files.move(temporary, destination, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(temporary, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
