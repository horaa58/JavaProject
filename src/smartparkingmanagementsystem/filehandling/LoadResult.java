package smartparkingmanagementsystem.filehandling;

import java.util.ArrayList;

/** Validated records loaded from binary Java serialization storage. */
public record LoadResult<T>(ArrayList<T> records) {
    public LoadResult {
        records = new ArrayList<>(records);
    }
}
