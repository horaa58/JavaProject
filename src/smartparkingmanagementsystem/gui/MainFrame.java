package smartparkingmanagementsystem.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import smartparkingmanagementsystem.manager.ModuleOneReportManager;
import smartparkingmanagementsystem.manager.ParkingSlotManager;
import smartparkingmanagementsystem.manager.VehicleManager;

public final class MainFrame extends JFrame {
    public MainFrame(ParkingSlotManager slotManager, VehicleManager vehicleManager) {
        super("Smart Parking Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 650);
        setLayout(new BorderLayout(5, 5));

        ReportsPanel reports = new ReportsPanel(new ModuleOneReportManager(slotManager, vehicleManager));
        JPanel records = new JPanel(new GridLayout(2, 1, 5, 5));
        records.add(new ParkingSlotPanel(slotManager, reports::refreshReports));
        records.add(new VehiclePanel(vehicleManager, reports::refreshReports));

        add(records, BorderLayout.CENTER);
        add(reports, BorderLayout.SOUTH);
        setLocationRelativeTo(null);
    }
}
