package smartparkingmanagementsystem;

import java.awt.GraphicsEnvironment;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import smartparkingmanagementsystem.filehandling.FileOperationException;
import smartparkingmanagementsystem.filehandling.ParkingSlotFileHandler;
import smartparkingmanagementsystem.filehandling.VehicleFileHandler;
import smartparkingmanagementsystem.gui.MainFrame;
import smartparkingmanagementsystem.manager.ParkingSlotManager;
import smartparkingmanagementsystem.manager.VehicleManager;
import smartparkingmanagementsystem.util.AppPaths;

/** Java 17 entry point for the Module 1 desktop application. */
public final class SmartParkingManagementSystem {
    private SmartParkingManagementSystem() { }

    public static void main(String[] args) {
        if (args.length > 0 && "--verify-data".equals(args[0])) {
            initialize();
            System.out.println("Module 1 data files initialized successfully.");
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                Managers managers = initialize();
                MainFrame frame = new MainFrame(managers.slots(), managers.vehicles());
                frame.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                String message = ex instanceof FileOperationException ? ex.getMessage()
                        : "The application could not start. Check data folder permissions and try again.";
                if (!GraphicsEnvironment.isHeadless()) JOptionPane.showMessageDialog(null,
                        message,
                        "Startup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static Managers initialize() {
        ParkingSlotFileHandler slotFiles = new ParkingSlotFileHandler(AppPaths.parkingSlotsFile());
        VehicleFileHandler vehicleFiles = new VehicleFileHandler(AppPaths.vehiclesFile());
        slotFiles.ensureFileExists(); vehicleFiles.ensureFileExists();
        ParkingSlotManager slots = new ParkingSlotManager(slotFiles); VehicleManager vehicles = new VehicleManager(vehicleFiles);
        slots.reloadFromFile(); vehicles.reloadFromFile(); return new Managers(slots, vehicles);
    }

    private record Managers(ParkingSlotManager slots, VehicleManager vehicles) { }
}
