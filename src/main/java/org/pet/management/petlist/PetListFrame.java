package org.pet.management.petlist;

import org.pet.management.edit.buttonPannel.ButtonEditor;
import org.pet.management.edit.buttonPannel.ButtonRenderer;
import org.pet.management.petlist.dto.PetDetailsDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.pet.management.petlist.dto.PetDetailsDTO.toArray;
import static org.pet.management.util.PetAppClient.getClient;

public class PetListFrame extends JFrame {
    private final JTextField searchField;
    private JTable petTable;
    private final DefaultTableModel tableModel;
    private static Map<Integer, PetDetailsDTO> petDetailsByIdMap;

    public PetListFrame() {
        setTitle("Pet Management System");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        final JPanel topPanel = new JPanel();
        searchField = new JTextField(20);
        final JButton searchButton = new JButton("Search");
        topPanel.add(new JLabel("Search by Pet Name:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);

        // Table setup
        final String[] columns = {"id", "Pet Name", "Age", "Last Vaccination", "Owner Name", "Owner Telephone", "Edit"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(final int row, final int column) {
                return column == 6;
            }
        };
        petTable = new JTable(tableModel);
        petTable.setRowHeight(30);

        addEditButton();
        add(topPanel, NORTH);
        add(new JScrollPane(petTable), CENTER);
        hideIdColumn();
        loadData();

        searchButton.addActionListener(e -> searchPets());
    }

    private void addEditButton() {
//        petTable.getColumnModel().getColumn(6).setCellRenderer(
//                new ActionCellEditorRenderer(this));
//        petTable.getColumnModel().getColumn(6).setCellEditor(
//                new ActionCellEditorRenderer(this));

        petTable.getColumn("Edit").setCellRenderer(new ButtonRenderer());
        petTable.getColumn("Edit").setCellEditor(new ButtonEditor(new JCheckBox(), this));
    }

    private void hideIdColumn() {
        petTable.getColumnModel().getColumn(0).setMinWidth(0);
        petTable.getColumnModel().getColumn(0).setMaxWidth(0);
        petTable.getColumnModel().getColumn(0).setWidth(0);

        petTable.getColumnModel().getColumn(6).setMinWidth(100);
        petTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        petTable.getColumnModel().getColumn(6).setMaxWidth(120);
    }

    private void loadData() {
        final var client = getClient();
        final List<PetDetailsDTO> petList = client.getPetList();
        saveDataLocally(petList);
        petList.forEach(dto -> tableModel.addRow(toArray(dto)));
    }

    private void saveDataLocally(final List<PetDetailsDTO> petList) {
        petDetailsByIdMap = petList.stream()
                .collect(toMap(PetDetailsDTO::getId, identity()));
    }

    private void searchPets() {
        final String searchText = searchField.getText().trim().toLowerCase();
        // Clear existing rows
        tableModel.setRowCount(0);

        // For now, filter dummy data
        final List<Object[]> allPets = List.of(
                new Object[]{"Buddy", 5, "2025-07-22 12:00", "John Doe", "1234567890"},
                new Object[]{"Charlie", 3, "2025-07-20 15:00", "Alice Smith", "9876543210"},
                new Object[]{"Max", 4, "2025-07-21 09:00", "Bob Johnson", "1112223333"}
        );

        for (final Object[] pet : allPets) {
            final String petName = ((String) pet[0]).toLowerCase();
            if (petName.contains(searchText)) {
                tableModel.addRow(pet);
            }
        }
    }

    public static PetDetailsDTO getPetDetails(final int id) {
        return petDetailsByIdMap.get(id);
    }
}

