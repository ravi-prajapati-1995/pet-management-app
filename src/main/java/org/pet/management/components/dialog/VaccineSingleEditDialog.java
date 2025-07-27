package org.pet.management.components.dialog;

import org.pet.management.dto.response.VaccineDTO;

import javax.swing.*;
import java.awt.*;

class VaccineSingleEditDialog extends JDialog {
    private final VaccineDTO vaccineDTO;

    public VaccineSingleEditDialog(final Dialog owner, final VaccineDTO vaccineDTO) {
        super(owner, "Edit Vaccine", true);
        this.vaccineDTO = vaccineDTO;

        final var nameField = new JTextField(vaccineDTO.getName(), 20);

        final var panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.add(new JLabel("Vaccine Name:"));
        panel.add(nameField);

        final var saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> {
//                vaccineDTO.getName() = nameField.getText().trim();
            dispose();
        });

        final var cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        final var btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        add(panel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }
}
