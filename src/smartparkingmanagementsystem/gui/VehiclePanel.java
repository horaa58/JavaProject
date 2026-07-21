package smartparkingmanagementsystem.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import smartparkingmanagementsystem.manager.VehicleManager;
import smartparkingmanagementsystem.model.RegistrationStatus;
import smartparkingmanagementsystem.model.Vehicle;
import smartparkingmanagementsystem.model.VehicleType;
import smartparkingmanagementsystem.util.ValidationUtils;

public final class VehiclePanel extends JPanel {
    private final VehicleManager manager;
    private final Runnable dataChangedCallback;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField numberField = new JTextField(12);
    private final JTextField ownerField = new JTextField(12);
    private final JTextField phoneField = new JTextField(12);
    private final JComboBox<VehicleType> typeBox = new JComboBox<>(VehicleType.values());
    private final JComboBox<RegistrationStatus> statusBox = new JComboBox<>(RegistrationStatus.values());
    private final JTextField searchField = new JTextField(16);

    public VehiclePanel(VehicleManager manager) {
        this(manager, () -> { });
    }

    public VehiclePanel(VehicleManager manager, Runnable dataChangedCallback) {
        this.manager = Objects.requireNonNull(manager, "manager");
        this.dataChangedCallback = Objects.requireNonNull(dataChangedCallback, "dataChangedCallback");
        numberField.setName("vehicleNumberField");
        ownerField.setName("vehicleOwnerField");
        phoneField.setName("vehiclePhoneField");
        typeBox.setName("vehicleTypeBox");
        statusBox.setName("vehicleStatusBox");
        searchField.setName("vehicleSearchField");
        setLayout(new BorderLayout(10, 5));
        setBorder(BorderFactory.createTitledBorder("Registered Vehicles"));

        tableModel = new DefaultTableModel(
                new String[] {"Vehicle Number", "Owner Name", "Phone", "Type", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setName("vehicleTable");
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) loadSelectedVehicleIntoFields();
        });

        add(createEditorPanel(), BorderLayout.WEST);
        add(createListPanel(), BorderLayout.CENTER);
        showVehicles(manager.getAllVehicles());
    }

    private JPanel createEditorPanel() {
        JPanel editor = new JPanel(new BorderLayout(5, 5));
        JPanel fields = new JPanel(new GridBagLayout());
        addField(fields, 0, "Vehicle Number:", numberField);
        addField(fields, 1, "Owner Name:", ownerField);
        addField(fields, 2, "Phone:", phoneField);
        addField(fields, 3, "Vehicle Type:", typeBox);
        addField(fields, 4, "Registration Status:", statusBox);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton addButton = plainButton("Add", new Color(0, 128, 0));
        JButton editButton = plainButton("Edit");
        JButton deleteButton = plainButton("Delete", Color.RED);
        addButton.addActionListener(event -> addVehicle());
        editButton.addActionListener(event -> editVehicle());
        deleteButton.addActionListener(event -> deleteVehicle());
        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);

        editor.add(fields, BorderLayout.CENTER);
        editor.add(actions, BorderLayout.SOUTH);
        return editor;
    }

    private JPanel createListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout(5, 5));
        JPanel search = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton searchButton = plainButton("Search", Color.BLUE);
        JButton showAllButton = plainButton("Show All");
        searchButton.setName("vehicleSearchButton");
        showAllButton.setName("vehicleShowAllButton");
        searchButton.addActionListener(event -> searchVehicles());
        showAllButton.addActionListener(event -> showAllVehicles());
        searchField.addActionListener(event -> searchVehicles());
        search.add(new JLabel("Search:"));
        search.add(searchField);
        search.add(searchButton);
        search.add(showAllButton);
        listPanel.add(search, BorderLayout.NORTH);
        listPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        return listPanel;
    }

    private static void addField(JPanel panel, int row, String label, java.awt.Component field) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.LINE_END;
        labelConstraints.insets = new Insets(2, 2, 2, 5);
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setLabelFor(field);
        panel.add(fieldLabel, labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.weightx = 1;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.insets = new Insets(2, 0, 2, 2);
        panel.add(field, fieldConstraints);
    }

    private void showVehicles(List<Vehicle> vehicles) {
        tableModel.setRowCount(0);
        for (Vehicle vehicle : vehicles) {
            tableModel.addRow(new Object[] {vehicle.getVehicleNumber(), vehicle.getOwnerName(), vehicle.getPhone(),
                    vehicle.getVehicleType(), vehicle.getRegistrationStatus()});
        }
    }

    private void searchVehicles() {
        clearSelectionAndFields();
        showVehicles(manager.searchVehicles(searchField.getText()));
    }

    private void showAllVehicles() {
        searchField.setText("");
        clearSelectionAndFields();
        showVehicles(manager.getAllVehicles());
    }

    private void addVehicle() {
        try {
            manager.addVehicle(vehicleFromFields());
            refreshAfterChange();
        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void editVehicle() {
        Vehicle oldVehicle = selectedVehicle();
        if (oldVehicle == null) return;
        try {
            manager.updateVehicle(oldVehicle.getVehicleNumber(), vehicleFromFields());
            refreshAfterChange();
        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void deleteVehicle() {
        Vehicle vehicle = selectedVehicle();
        if (vehicle == null) return;
        int answer = JOptionPane.showConfirmDialog(this, "Delete this vehicle?", "Delete",
                JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            try {
                manager.deleteVehicle(vehicle.getVehicleNumber());
                refreshAfterChange();
            } catch (RuntimeException ex) {
                showError(ex);
            }
        }
    }

    private Vehicle vehicleFromFields() {
        return new Vehicle(
                ValidationUtils.normalizeIdentifier(numberField.getText(), "Vehicle number"),
                ValidationUtils.requireText(ownerField.getText(), "Owner name"),
                ValidationUtils.validatePhone(phoneField.getText()),
                (VehicleType) typeBox.getSelectedItem(),
                (RegistrationStatus) statusBox.getSelectedItem());
    }

    private void loadSelectedVehicleIntoFields() {
        Vehicle vehicle = selectedVehicle(false);
        if (vehicle == null) return;
        numberField.setText(vehicle.getVehicleNumber());
        ownerField.setText(vehicle.getOwnerName());
        phoneField.setText(vehicle.getPhone());
        typeBox.setSelectedItem(vehicle.getVehicleType());
        statusBox.setSelectedItem(vehicle.getRegistrationStatus());
    }

    private Vehicle selectedVehicle() {
        return selectedVehicle(true);
    }

    private Vehicle selectedVehicle(boolean showMessage) {
        int row = table.getSelectedRow();
        if (row == -1) {
            if (showMessage) JOptionPane.showMessageDialog(this, "Select a vehicle first.");
            return null;
        }
        String number = tableModel.getValueAt(row, 0).toString();
        return manager.findByVehicleNumber(number).orElse(null);
    }

    private void refreshAfterChange() {
        searchField.setText("");
        clearSelectionAndFields();
        showVehicles(manager.getAllVehicles());
        dataChangedCallback.run();
    }

    private void clearSelectionAndFields() {
        table.clearSelection();
        numberField.setText("");
        ownerField.setText("");
        phoneField.setText("");
        typeBox.setSelectedIndex(0);
        statusBox.setSelectedIndex(0);
    }

    private void showError(RuntimeException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private static JButton plainButton(String text) {
        return plainButton(text, null);
    }

    private static JButton plainButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        if (color != null) button.setForeground(color);
        return button;
    }
}
