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
import smartparkingmanagementsystem.manager.ParkingSlotManager;
import smartparkingmanagementsystem.model.ParkingSlot;
import smartparkingmanagementsystem.model.SlotStatus;
import smartparkingmanagementsystem.model.SlotType;
import smartparkingmanagementsystem.util.ValidationUtils;

public final class ParkingSlotPanel extends JPanel {
    private final ParkingSlotManager manager;
    private final Runnable dataChangedCallback;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final JTextField idField = new JTextField(12);
    private final JTextField floorField = new JTextField(12);
    private final JComboBox<SlotType> typeBox = new JComboBox<>(SlotType.values());
    private final JComboBox<SlotStatus> statusBox = new JComboBox<>(SlotStatus.values());
    private final JTextField rateField = new JTextField(12);
    private final JTextField searchField = new JTextField(16);

    public ParkingSlotPanel(ParkingSlotManager manager) {
        this(manager, () -> { });
    }

    public ParkingSlotPanel(ParkingSlotManager manager, Runnable dataChangedCallback) {
        this.manager = Objects.requireNonNull(manager, "manager");
        this.dataChangedCallback = Objects.requireNonNull(dataChangedCallback, "dataChangedCallback");
        idField.setName("parkingSlotIdField");
        floorField.setName("parkingFloorField");
        typeBox.setName("parkingTypeBox");
        statusBox.setName("parkingStatusBox");
        rateField.setName("parkingRateField");
        searchField.setName("parkingSearchField");
        setLayout(new BorderLayout(10, 5));
        setBorder(BorderFactory.createTitledBorder("Parking Slots"));

        tableModel = new DefaultTableModel(
                new String[] {"Slot ID", "Floor", "Type", "Status", "Hourly Rate"}, 0) {
            @Override public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setName("parkingTable");
        table.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting()) loadSelectedSlotIntoFields();
        });

        add(createEditorPanel(), BorderLayout.WEST);
        add(createListPanel(), BorderLayout.CENTER);
        showSlots(manager.getAllParkingSlots());
    }

    private JPanel createEditorPanel() {
        JPanel editor = new JPanel(new BorderLayout(5, 5));
        JPanel fields = new JPanel(new GridBagLayout());
        addField(fields, 0, "Slot ID:", idField);
        addField(fields, 1, "Floor Number:", floorField);
        addField(fields, 2, "Slot Type:", typeBox);
        addField(fields, 3, "Status:", statusBox);
        addField(fields, 4, "Hourly Rate:", rateField);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton addButton = plainButton("Add", new Color(0, 128, 0));
        JButton editButton = plainButton("Edit");
        JButton deleteButton = plainButton("Delete", Color.RED);
        addButton.addActionListener(event -> addSlot());
        editButton.addActionListener(event -> editSlot());
        deleteButton.addActionListener(event -> deleteSlot());
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
        searchButton.setName("parkingSearchButton");
        showAllButton.setName("parkingShowAllButton");
        searchButton.addActionListener(event -> searchSlots());
        showAllButton.addActionListener(event -> showAllSlots());
        searchField.addActionListener(event -> searchSlots());
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

    private void showSlots(List<ParkingSlot> slots) {
        tableModel.setRowCount(0);
        for (ParkingSlot slot : slots) {
            tableModel.addRow(new Object[] {slot.getSlotId(), slot.getFloorNumber(), slot.getSlotType(),
                    slot.getSlotStatus(), slot.getHourlyRate()});
        }
    }

    private void searchSlots() {
        clearSelectionAndFields();
        showSlots(manager.searchParkingSlots(searchField.getText()));
    }

    private void showAllSlots() {
        searchField.setText("");
        clearSelectionAndFields();
        showSlots(manager.getAllParkingSlots());
    }

    private void addSlot() {
        try {
            manager.addParkingSlot(slotFromFields());
            refreshAfterChange();
        } catch (NumberFormatException ex) {
            showWarning("Floor and hourly rate must be numbers.");
        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void editSlot() {
        ParkingSlot oldSlot = selectedSlot();
        if (oldSlot == null) return;
        try {
            manager.updateParkingSlot(oldSlot.getSlotId(), slotFromFields());
            refreshAfterChange();
        } catch (NumberFormatException ex) {
            showWarning("Floor and hourly rate must be numbers.");
        } catch (RuntimeException ex) {
            showError(ex);
        }
    }

    private void deleteSlot() {
        ParkingSlot slot = selectedSlot();
        if (slot == null) return;
        int answer = JOptionPane.showConfirmDialog(this, "Delete this parking slot?", "Delete",
                JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            try {
                manager.deleteParkingSlot(slot.getSlotId());
                refreshAfterChange();
            } catch (RuntimeException ex) {
                showError(ex);
            }
        }
    }

    private ParkingSlot slotFromFields() {
        int floor = Integer.parseInt(floorField.getText().trim());
        double rate = Double.parseDouble(rateField.getText().trim());
        return new ParkingSlot(
                ValidationUtils.normalizeIdentifier(idField.getText(), "Slot ID"),
                ValidationUtils.validateFloor(floor),
                (SlotType) typeBox.getSelectedItem(),
                (SlotStatus) statusBox.getSelectedItem(),
                ValidationUtils.validateRate(rate));
    }

    private void loadSelectedSlotIntoFields() {
        ParkingSlot slot = selectedSlot(false);
        if (slot == null) return;
        idField.setText(slot.getSlotId());
        floorField.setText(String.valueOf(slot.getFloorNumber()));
        typeBox.setSelectedItem(slot.getSlotType());
        statusBox.setSelectedItem(slot.getSlotStatus());
        rateField.setText(String.valueOf(slot.getHourlyRate()));
    }

    private ParkingSlot selectedSlot() {
        return selectedSlot(true);
    }

    private ParkingSlot selectedSlot(boolean showMessage) {
        int row = table.getSelectedRow();
        if (row == -1) {
            if (showMessage) JOptionPane.showMessageDialog(this, "Select a parking slot first.");
            return null;
        }
        String id = tableModel.getValueAt(row, 0).toString();
        return manager.findById(id).orElse(null);
    }

    private void refreshAfterChange() {
        searchField.setText("");
        clearSelectionAndFields();
        showSlots(manager.getAllParkingSlots());
        dataChangedCallback.run();
    }

    private void clearSelectionAndFields() {
        table.clearSelection();
        idField.setText("");
        floorField.setText("");
        typeBox.setSelectedIndex(0);
        statusBox.setSelectedIndex(0);
        rateField.setText("");
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid Parking Slot", JOptionPane.WARNING_MESSAGE);
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
