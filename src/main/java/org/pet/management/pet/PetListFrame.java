package org.pet.management.pet;

import lombok.Getter;
import org.pet.management.dto.request.OwnerUpdateDTO;
import org.pet.management.dto.request.PetUpdateDto;
import org.pet.management.dto.response.PetDetailsDTO;
import org.pet.management.edit.buttonPannel.ButtonEditor;
import org.pet.management.edit.buttonPannel.ButtonRenderer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.pet.management.dto.response.PetDetailsDTO.toArray;
import static org.pet.management.util.PetAppClient.getClient;

@Getter
public class PetListFrame extends JFrame {
    private final JTextField searchField;
    private final JTable petTable;
    private final DefaultTableModel tableModel;
    private static Map<Integer, PetDetailsDTO> petDetailsByIdMap;
    private static PetListFrame FRAME_REFERENCE;

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

        FRAME_REFERENCE = this;
    }

    public static void updatePetDetails(final int petId, final PetUpdateDto petUpdateDto) {
        final PetDetailsDTO petDetailsDTO = petDetailsByIdMap.get(petId);
        final PetDetailsDTO updatePetDetailsDTO = petDetailsDTO.toBuilder()
                .name(petUpdateDto.getName())
                .age(petUpdateDto.getAge())
                .build();
        petDetailsByIdMap.put(petId, updatePetDetailsDTO);
        FRAME_REFERENCE.reRenderPetData();
    }

    public static void updateOwnerDetails(final int ownerId, final OwnerUpdateDTO ownerUpdateDTO) {
        final var needToBeUpdateRecords = petDetailsByIdMap.values()
                .stream()
                .filter(dto -> dto.getOwnerId() == ownerId)
                .map(dto -> getUpdatedPetDetailsDTO(ownerUpdateDTO, dto))
                .collect(Collectors.toMap(PetDetailsDTO::getId, Function.identity()));
        petDetailsByIdMap.putAll(needToBeUpdateRecords);

        FRAME_REFERENCE.reRenderPetData();
    }

    private static PetDetailsDTO getUpdatedPetDetailsDTO(final OwnerUpdateDTO ownerUpdateDTO, final PetDetailsDTO dto) {
        return dto.toBuilder()
                .owner(ownerUpdateDTO.getOwnerName())
                .phoneNumber(ownerUpdateDTO.getOwnerPhone())
                .build();
    }

    private void addEditButton() {
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
        tableModel.setRowCount(0);

        final String searchText = searchField.getText().trim().toLowerCase();
        final var client = getClient();
        final List<PetDetailsDTO> petList = client.searchByName(searchText);
        saveDataLocally(petList);
        petList.forEach(dto -> tableModel.addRow(toArray(dto)));
    }

    private void reRenderPetData() {
        tableModel.setRowCount(0);
        petDetailsByIdMap.values().forEach(dto -> tableModel.addRow(toArray(dto)));

    }

    public static PetDetailsDTO getPetDetails(final int id) {
        return petDetailsByIdMap.get(id);
    }
}

