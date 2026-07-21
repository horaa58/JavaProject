package smartparkingmanagementsystem.filehandling;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation for a complete serialized {@link ArrayList} stored in a
 * project-controlled file. The required production filenames end in .txt, but
 * this class deliberately writes binary Java serialization streams.
 */
abstract class SerializedArrayListFileHandler<T> {
    private final Path file;
    private final Class<T> elementType;
    private final String dataName;

    SerializedArrayListFileHandler(Path file, Class<T> elementType, String dataName) {
        this.file = file.toAbsolutePath().normalize();
        this.elementType = elementType;
        this.dataName = dataName;
    }

    public final Path getFile() { return file; }

    public final void ensureFileExists() {
        try {
            SerializedFileSupport.ensureParentDirectory(file);
            if (Files.notExists(file) || Files.size(file) == 0) {
                save(new ArrayList<>());
            }
        } catch (IOException | SecurityException ex) {
            throw new FileOperationException(dataName + " data could not be initialized.", ex);
        }
    }

    /** Loads only a binary Java serialization stream. */
    public final synchronized LoadResult<T> load() {
        try {
            if (Files.notExists(file) || Files.size(file) == 0) {
                return new LoadResult<>(new ArrayList<>());
            }
            if (!SerializedFileSupport.hasSerializationHeader(file)) {
                throw corruptedFile(new StreamCorruptedException(
                        "Missing Java serialization stream header AC ED 00 05."));
            }
            return new LoadResult<>(readSerializedArrayList());
        } catch (FileOperationException ex) {
            throw ex;
        } catch (IOException | SecurityException ex) {
            throw corruptedFile(ex);
        }
    }

    /** Serializes one complete, validated ArrayList through ObjectOutputStream. */
    public final synchronized void save(List<T> records) {
        ArrayList<T> arrayList = validateAndCopy(records, false);
        Path temporary = null;
        try {
            SerializedFileSupport.ensureParentDirectory(file);
            Path parent = file.getParent();
            temporary = Files.createTempFile(parent, file.getFileName().toString(), ".tmp");

            // FileOutputStream -> ObjectOutputStream -> writeObject(ArrayList)
            try (FileOutputStream fileOutputStream = new FileOutputStream(temporary.toFile());
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeObject(arrayList);
                objectOutputStream.flush();
                fileOutputStream.getFD().sync();
            }
            SerializedFileSupport.replace(temporary, file);
            temporary = null;
        } catch (IOException | SecurityException ex) {
            throw new FileOperationException(dataName + " data could not be saved.", ex);
        } finally {
            if (temporary != null) {
                try { Files.deleteIfExists(temporary); }
                catch (IOException cleanupError) { System.err.println("Could not remove temporary file: " + cleanupError.getMessage()); }
            }
        }
    }

    private ArrayList<T> readSerializedArrayList() {
        try (FileInputStream fileInputStream = new FileInputStream(file.toFile());
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            // A fresh stream reconstructs the one complete root ArrayList.
            Object storedObject = objectInputStream.readObject();
            if (storedObject == null || storedObject.getClass() != ArrayList.class) {
                throw corruptedFile(new IOException("Unexpected serialized root object type."));
            }
            return validateAndCopy((ArrayList<?>) storedObject, true);
        } catch (InvalidClassException | ClassNotFoundException ex) {
            throw new FileOperationException("The " + dataName + " file is incompatible with the current model classes.", ex);
        } catch (StreamCorruptedException | EOFException ex) {
            throw corruptedFile(ex);
        } catch (IOException | SecurityException ex) {
            throw new FileOperationException(dataName + " data could not be loaded.", ex);
        }
    }

    private ArrayList<T> validateAndCopy(List<?> records, boolean loading) {
        if (records == null) {
            throw new FileOperationException(loading ? corruptedMessage() : dataName + " data could not be saved.");
        }
        ArrayList<T> typed = new ArrayList<>(records.size());
        try {
            for (Object record : records) {
                if (record == null || !elementType.isInstance(record)) {
                    throw new IllegalArgumentException("Unexpected or null list element.");
                }
                typed.add(elementType.cast(record));
            }
            validateRecords(typed);
            return typed;
        } catch (RuntimeException ex) {
            throw new FileOperationException(loading ? corruptedMessage() : dataName + " data could not be saved.", ex);
        }
    }

    private FileOperationException corruptedFile(Throwable cause) {
        return new FileOperationException(corruptedMessage(), cause);
    }

    private String corruptedMessage() {
        return "The " + dataName
                + " data file is not valid Java serialization data or is incompatible.";
    }

    protected abstract void validateRecords(ArrayList<T> records);
}
