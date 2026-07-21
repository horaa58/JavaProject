package smartparkingmanagementsystem.gui;

import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import smartparkingmanagementsystem.manager.ModuleOneReportManager;
import smartparkingmanagementsystem.model.SlotStatus;

public final class ReportsPanel extends JPanel {
    private final ModuleOneReportManager manager;
    private final JLabel totalSlots = new JLabel();
    private final JLabel availableSlots = new JLabel();
    private final JLabel occupiedSlots = new JLabel();
    private final JLabel reservedSlots = new JLabel();
    private final JLabel maintenanceSlots = new JLabel();
    private final JLabel totalVehicles = new JLabel();

    public ReportsPanel(ModuleOneReportManager manager) {
        this.manager = manager;
        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBorder(BorderFactory.createTitledBorder("Report"));

        add(totalSlots);
        add(availableSlots);
        add(occupiedSlots);
        add(reservedSlots);
        add(maintenanceSlots);
        add(totalVehicles);
        refreshReports();
    }

    public void refreshReports() {
        ModuleOneReportManager.ReportSnapshot data = manager.generate();
        totalSlots.setText("Total Parking Slots: " + data.totalSlots());
        availableSlots.setText("Available Slots: " + count(data, SlotStatus.AVAILABLE));
        occupiedSlots.setText("Occupied Slots: " + count(data, SlotStatus.OCCUPIED));
        reservedSlots.setText("Reserved Slots: " + count(data, SlotStatus.RESERVED));
        maintenanceSlots.setText("Under Maintenance: " + count(data, SlotStatus.UNDER_MAINTENANCE));
        totalVehicles.setText("Registered Vehicles: " + data.totalVehicles());
    }

    private static String count(ModuleOneReportManager.ReportSnapshot data, SlotStatus status) {
        return String.valueOf(data.slotsByStatus().get(status));
    }
}
