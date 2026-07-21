package smartparkingmanagementsystem.manager;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import smartparkingmanagementsystem.filehandling.LoadResult;
import smartparkingmanagementsystem.filehandling.ParkingSlotFileHandler;
import smartparkingmanagementsystem.model.ParkingSlot;
import smartparkingmanagementsystem.model.SlotStatus;
import smartparkingmanagementsystem.model.SlotType;
import smartparkingmanagementsystem.util.ValidationUtils;

/** Validates, queries, and persists parking slot master records. */
public final class ParkingSlotManager {
    private final ParkingSlotFileHandler fileHandler;
    private final List<ParkingSlot> slots = new ArrayList<>();

    public ParkingSlotManager(ParkingSlotFileHandler fileHandler) { this.fileHandler = fileHandler; }

    public synchronized void addParkingSlot(ParkingSlot slot) {
        ParkingSlot valid = validate(slot);
        if (findById(valid.getSlotId()).isPresent()) {
            throw new IllegalArgumentException("A parking slot with this ID already exists.");
        }
        mutate(() -> slots.add(valid));
    }

    public synchronized void updateParkingSlot(String originalId, ParkingSlot replacement) {
        String key = ValidationUtils.normalizeIdentifier(originalId, "Slot ID");
        ParkingSlot valid = validate(replacement);
        int index = indexOf(key);
        if (index < 0) throw new IllegalArgumentException("Parking slot was not found.");
        int duplicate = indexOf(valid.getSlotId());
        if (duplicate >= 0 && duplicate != index) {
            throw new IllegalArgumentException("A parking slot with this ID already exists.");
        }
        mutate(() -> slots.set(index, valid));
    }

    public synchronized boolean deleteParkingSlot(String slotId) {
        int index = indexOf(ValidationUtils.normalizeIdentifier(slotId, "Slot ID"));
        if (index < 0) return false;
        mutate(() -> slots.remove(index));
        return true;
    }

    public synchronized void updateSlotStatus(String slotId, SlotStatus status) {
        if (status == null) throw new IllegalArgumentException("Slot status is required.");
        int index = indexOf(ValidationUtils.normalizeIdentifier(slotId, "Slot ID"));
        if (index < 0) throw new IllegalArgumentException("Parking slot was not found.");
        mutate(() -> slots.set(index, slots.get(index).withStatus(status)));
    }

    public synchronized Optional<ParkingSlot> findById(String id) {
        if (id == null) return Optional.empty();
        String key = id.trim().toUpperCase(Locale.ROOT);
        return slots.stream().filter(slot -> slot.getSlotId().equalsIgnoreCase(key)).findFirst();
    }

    public synchronized List<ParkingSlot> getAllParkingSlots() { return List.copyOf(slots); }

    public synchronized List<ParkingSlot> searchParkingSlots(String query) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) return getAllParkingSlots();
        return slots.stream().filter(slot -> slot.getSlotId().toLowerCase(Locale.ROOT).contains(q)
                || Integer.toString(slot.getFloorNumber()).contains(q)
                || slot.getSlotType().name().toLowerCase(Locale.ROOT).contains(q)
                || slot.getSlotStatus().name().toLowerCase(Locale.ROOT).contains(q)).toList();
    }

    public synchronized List<ParkingSlot> searchParkingSlots(String query, SlotType type,
            SlotStatus status, Integer floor) {
        return searchParkingSlots(query).stream()
                .filter(slot -> type == null || slot.getSlotType() == type)
                .filter(slot -> status == null || slot.getSlotStatus() == status)
                .filter(slot -> floor == null || slot.getFloorNumber() == floor).toList();
    }

    public synchronized long countByStatus(SlotStatus status) {
        return slots.stream().filter(slot -> slot.getSlotStatus() == status).count();
    }

    public synchronized Map<SlotStatus, Long> countByStatus() {
        Map<SlotStatus, Long> result = new EnumMap<>(SlotStatus.class);
        for (SlotStatus status : SlotStatus.values()) result.put(status, countByStatus(status));
        return result;
    }

    public synchronized void reloadFromFile() {
        LoadResult<ParkingSlot> result = fileHandler.load();
        slots.clear();
        for (ParkingSlot slot : result.records()) {
            try {
                ParkingSlot valid = validate(slot);
                if (indexOf(valid.getSlotId()) < 0) slots.add(valid);
            } catch (IllegalArgumentException ex) {
                // Defensive manager validation; serialized handlers already reject invalid collections.
            }
        }
    }

    public synchronized void saveToFile() { fileHandler.save(slots); }

    private ParkingSlot validate(ParkingSlot slot) {
        if (slot == null) throw new IllegalArgumentException("Parking slot is required.");
        if (slot.getSlotType() == null) throw new IllegalArgumentException("Slot type is required.");
        if (slot.getSlotStatus() == null) throw new IllegalArgumentException("Slot status is required.");
        return new ParkingSlot(ValidationUtils.normalizeIdentifier(slot.getSlotId(), "Slot ID"),
                ValidationUtils.validateFloor(slot.getFloorNumber()), slot.getSlotType(), slot.getSlotStatus(),
                ValidationUtils.validateRate(slot.getHourlyRate()));
    }

    private int indexOf(String id) {
        for (int index = 0; index < slots.size(); index++) {
            if (slots.get(index).getSlotId().equalsIgnoreCase(id)) return index;
        }
        return -1;
    }

    private void mutate(Runnable change) {
        List<ParkingSlot> before = new ArrayList<>(slots);
        change.run();
        try { saveToFile(); }
        catch (RuntimeException ex) { slots.clear(); slots.addAll(before); throw ex; }
    }
}
