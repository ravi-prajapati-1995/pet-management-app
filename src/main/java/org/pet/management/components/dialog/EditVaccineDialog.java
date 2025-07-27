package org.pet.management.components.dialog;

import org.jetbrains.annotations.NotNull;
import org.pet.management.components.frame.PetListFrame;
import org.pet.management.dto.request.UpdateVaccineDTO;
import org.pet.management.dto.response.VaccineDTO;
import org.pet.management.util.DateTimeUtils;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.Dialog.ModalityType.APPLICATION_MODAL;
import static java.time.LocalDateTime.now;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static org.pet.management.util.JsonUtil.asJsonString;
import static org.pet.management.util.PetAppClient.getClient;


public final class EditVaccineDialog extends JDialog {
    private Map<Long, VaccineDTO> vaccineById;
    private Long newlyAddedId;
    private Long petId;

    public EditVaccineDialog(final Window owner, final Long petId) {
        super(owner, "Edit Vaccines", APPLICATION_MODAL);
        vaccineById = new HashMap<>();
        newlyAddedId = 0L;
        this.petId = petId;

        final List<VaccineDTO> vaccines = getVaccineData(petId);
        newlyAddedId = vaccines.stream().mapToLong(VaccineDTO::getId).max().orElse(1L);
        vaccineById = vaccines.stream().collect(Collectors.toMap(VaccineDTO::getId, identity()));
        final var model = new VaccineTableModel(vaccines);
        final JTable table = new JTable(model);
        table.setSelectionMode(SINGLE_SELECTION);

        setSize(400, 300);
        setLocationRelativeTo(owner);

        final var scrollPane = new JScrollPane(table);
        final var btnPanel = getjPanel(table, model);
        add(scrollPane, CENTER);
        add(btnPanel, SOUTH);
    }

    private List<VaccineDTO> getVaccineData(final Long petId) {
        return getClient().getVaccineByPet(petId);
    }

    @NotNull
    private JPanel getjPanel(final JTable table, final VaccineTableModel model) {
        final var editBtn = new JButton("Edit Selected");
        editBtn.addActionListener(ae -> editCurrentRow(table, model));
        final JButton addBtn = new JButton("Add New");
        final JButton updateBtn = new JButton("Update All");

        addBtn.addActionListener(ae -> addNewVaccine(model));
        updateBtn.addActionListener(ae -> updateAll(table, model));

        final var btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(addBtn);
        btnPanel.add(editBtn);
        btnPanel.add(updateBtn);
        return btnPanel;
    }

    private void updateAll(final JTable table, final VaccineTableModel model) {
        final var updateVaccineDto = vaccineById.values().stream()
                .map(UpdateVaccineDTO::from)
                .collect(toList());

        final var latestVaccineTime = updateVaccineDto
                .stream()
                .map(UpdateVaccineDTO::getVaccineTime)
                .max(LocalDateTime::compareTo).orElse(null);

        final var jsonString = asJsonString(updateVaccineDto);
        getClient().updateVaccineData(petId, jsonString);
        PetListFrame.updateVaccineInfo(petId, latestVaccineTime);
        dispose();
    }

    private void addNewVaccine(final VaccineTableModel model) {
        final VaccineDTO newVaccine = new VaccineDTO(++newlyAddedId, "", now());
        final var editDialog = new VaccineSingleEditDialog(this, newVaccine, vaccineById);
        editDialog.setVisible(true);
        if (!newVaccine.getName().isEmpty()) {
            model.vaccines.add(newVaccine);
            model.fireTableRowsInserted(model.getRowCount() - 1, model.getRowCount() - 1);
        }
    }

    private void editCurrentRow(final JTable table, final VaccineTableModel model) {
        final var selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            final var selected = model.getVaccineAt(selectedRow);
            new VaccineSingleEditDialog(this, selected, vaccineById).setVisible(true);
            model.fireTableRowsUpdated(selectedRow, selectedRow);
        } else {
            showMessageDialog(this, "Please select a vaccine to edit.");
        }
    }


    static final class VaccineTableModel extends AbstractTableModel {
        private final String[] columns = {"Vaccine Name", "Date Given"};
        private List<VaccineDTO> vaccines;

        VaccineTableModel(final List<VaccineDTO> vaccines) {
            this.vaccines = vaccines;
        }

        public VaccineDTO getVaccineAt(final int row) {
            return vaccines.get(row);
        }

        public void setVaccines(final List<VaccineDTO> vaccines) {
            this.vaccines = vaccines;
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return vaccines.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(final int col) {
            return columns[col];
        }

        @Override
        public Object getValueAt(final int row, final int col) {
            final var v = vaccines.get(row);
            final Object result;
            switch (col) {
                case 0:
                    result = v.getName();
                    break;
                case 1:
                    result = DateTimeUtils.toString(v.getTime());
                    break;
                default:
                    return -1;
            }
            return result;
        }

    }
}

