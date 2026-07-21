package smartparkingmanagementsystem;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

final class SerializationTestSupport {
    private SerializationTestSupport() { }

    static <T> T roundTrip(T value, Class<T> type) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) { output.writeObject(value); }
        try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return type.cast(input.readObject());
        }
    }

    static void writeObject(Path file, Object value) throws Exception {
        if (file.getParent() != null) Files.createDirectories(file.getParent());
        try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(file))) {
            output.writeObject(value);
        }
    }

    static Object readObject(Path file) throws Exception {
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(file))) {
            return input.readObject();
        }
    }

    static boolean hasHeader(Path file) throws Exception {
        byte[] bytes = Files.readAllBytes(file);
        return bytes.length >= 4 && (bytes[0] & 0xFF) == 0xAC && (bytes[1] & 0xFF) == 0xED
                && bytes[2] == 0 && bytes[3] == 5;
    }

    static ArrayList<Object> list(Object... values) {
        ArrayList<Object> result = new ArrayList<>();
        for (Object value : values) result.add(value);
        return result;
    }
}
