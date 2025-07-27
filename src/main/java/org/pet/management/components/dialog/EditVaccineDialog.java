package org.pet.management.components.dialog;

import org.pet.management.dto.response.VaccineDTO;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public final class EditVaccineDialog extends JDialog {
    private final Long petId;
    public EditVaccineDialog(final Window owner, final Long petId) {
        super(owner, "Edit Vaccines", ModalityType.APPLICATION_MODAL);
        this.petId = petId;

        // Sample data - replace with your actual vaccine list
        final List<VaccineDTO> vaccines = new ArrayList<>();
        vaccines.add(new VaccineDTO(1L, "Rabies", LocalDateTime.now()));
        vaccines.add(new VaccineDTO(2L, "Parvo", LocalDateTime.now()));

        final var model = new VaccineTableModel(vaccines);
        final var table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        final var scrollPane = new JScrollPane(table);

        final var editBtn = new JButton("Edit Selected");
        editBtn.addActionListener(ae -> {
            final var selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                final var selected = model.getVaccineAt(selectedRow);
                new VaccineSingleEditDialog(this, selected).setVisible(true);
                model.fireTableRowsUpdated(selectedRow, selectedRow);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a vaccine to edit.");
            }
        });

        final var btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(editBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        setSize(400, 300);
        setLocationRelativeTo(owner);
    }

    static final class VaccineTableModel extends AbstractTableModel {
        private final String[] columns = {"ID", "Vaccine Name", "Date Given", "Next Due"};
        private final List<VaccineDTO> vaccines;

        VaccineTableModel(final List<VaccineDTO> vaccines) {
            this.vaccines = vaccines;
        }

        public VaccineDTO getVaccineAt(final int row) {
            return vaccines.get(row);
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
            return v.getId();
        }
    }
}

