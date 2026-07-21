package smartparkingmanagementsystem.manager;

import java.util.EnumMap;
import java.util.Map;
import smartparkingmanagementsystem.model.SlotStatus;

/** Computes only the summary values required by the Module 1 report. */
public final class ModuleOneReportManager {
    private final ParkingSlotManager slotManager;
    private final VehicleManager vehicleManager;

    public ModuleOneReportManager(ParkingSlotManager slotManager, VehicleManager vehicleManager) {
        this.slotManager = slotManager;
        this.vehicleManager = vehicleManager;
    }

    public ReportSnapshot generate() {
        var slots = slotManager.getAllParkingSlots();
        Map<SlotStatus, Long> byStatus = new EnumMap<>(SlotStatus.class);
        for (SlotStatus status : SlotStatus.values()) {
            byStatus.put(status, 0L);
        }
        for (var slot : slots) {
            byStatus.merge(slot.getSlotStatus(), 1L, Long::sum);
        }
        return new ReportSnapshot(slots.size(), Map.copyOf(byStatus), vehicleManager.getAllVehicles().size());
    }

    public record ReportSnapshot(int totalSlots, Map<SlotStatus, Long> slotsByStatus, int totalVehicles) { }
}
