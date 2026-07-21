package smartparkingmanagementsystem;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.JTable;
import javax.swing.JTextField;
import smartparkingmanagementsystem.gui.ParkingSlotPanel;
import smartparkingmanagementsystem.gui.ReportsPanel;
import smartparkingmanagementsystem.gui.VehiclePanel;
import smartparkingmanagementsystem.manager.ModuleOneReportManager;

/** Headless-safe smoke coverage for the Swing panels used by both IDE launchers. */
final class GuiSmokeTest {
    private GuiSmokeTest() { }

    static void register(TestSuite suite) {
        suite.test("01 construct and refresh Swing panels on the event thread", () -> {
            var directory = TestFixtures.directory();
            var slots = TestFixtures.slots(directory.resolve("parking.txt"));
            var vehicles = TestFixtures.vehicles(directory.resolve("vehicles.txt"));
            slots.addParkingSlot(TestFixtures.slot("P-1"));
            vehicles.addVehicle(TestFixtures.vehicle("V-1"));

            SwingUtilities.invokeAndWait(() -> {
                ReportsPanel reports = new ReportsPanel(new ModuleOneReportManager(slots, vehicles));
                ParkingSlotPanel parkingPanel = new ParkingSlotPanel(slots, reports::refreshReports);
                VehiclePanel vehiclePanel = new VehiclePanel(vehicles, reports::refreshReports);

                reports.refreshReports();
                TestSuite.truth(parkingPanel.getComponentCount() > 0);
                TestSuite.truth(vehiclePanel.getComponentCount() > 0);
                TestSuite.truth(reports.getComponentCount() > 0);

                JTable parkingTable = named(parkingPanel, JTable.class, "parkingTable");
                JTextField slotId = named(parkingPanel, JTextField.class, "parkingSlotIdField");
                parkingTable.setRowSelectionInterval(0, 0);
                TestSuite.equal("P-1", slotId.getText());

                JTable vehicleTable = named(vehiclePanel, JTable.class, "vehicleTable");
                JTextField vehicleNumber = named(vehiclePanel, JTextField.class, "vehicleNumberField");
                vehicleTable.setRowSelectionInterval(0, 0);
                TestSuite.equal("V-1", vehicleNumber.getText());

                JTextField parkingSearch = named(parkingPanel, JTextField.class, "parkingSearchField");
                parkingSearch.setText("missing");
                named(parkingPanel, JButton.class, "parkingSearchButton").doClick();
                TestSuite.equal(0, parkingTable.getRowCount());
                named(parkingPanel, JButton.class, "parkingShowAllButton").doClick();
                TestSuite.equal(1, parkingTable.getRowCount());
                TestSuite.equal("", parkingSearch.getText());

                JTextField vehicleSearch = named(vehiclePanel, JTextField.class, "vehicleSearchField");
                vehicleSearch.setText("missing");
                named(vehiclePanel, JButton.class, "vehicleSearchButton").doClick();
                TestSuite.equal(0, vehicleTable.getRowCount());
                named(vehiclePanel, JButton.class, "vehicleShowAllButton").doClick();
                TestSuite.equal(1, vehicleTable.getRowCount());
                TestSuite.equal("", vehicleSearch.getText());
            });
        });
    }

    private static <T extends Component> T named(Container root, Class<T> type, String name) {
        T match = findNamed(root, type, name);
        if (match != null) return match;
        throw new AssertionError("Missing component: " + name);
    }

    private static <T extends Component> T findNamed(Container root, Class<T> type, String name) {
        for (Component component : root.getComponents()) {
            if (type.isInstance(component) && name.equals(component.getName())) return type.cast(component);
            if (component instanceof Container container) {
                T match = findNamed(container, type, name);
                if (match != null) return match;
            }
        }
        return null;
    }
}
