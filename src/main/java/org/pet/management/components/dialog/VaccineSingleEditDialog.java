package org.pet.management.components.dialog;

import org.pet.management.common.CompositeVerifier;
import org.pet.management.common.MyInputVerifier;
import org.pet.management.dto.response.VaccineDTO;
import org.pet.management.util.DateTimeUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.FlowLayout.RIGHT;
import static org.pet.management.common.MyInputVerifier.dateTimeInPast;
import static org.pet.management.common.MyInputVerifier.validDateTimeFormat;

class VaccineSingleEditDialog extends JDialog {
    private final VaccineDTO vaccineDTO;

    public VaccineSingleEditDialog(
            final Dialog owner,
            final VaccineDTO vaccineDTO,
            final Map<Long, VaccineDTO> vaccineById
    ) {
        super(owner, "Edit Vaccine", true);
        this.vaccineDTO = vaccineDTO;

        final var nameField = new JTextField(vaccineDTO.getName(), 20);
        nameField.setName("Vaccine Name");
        nameField.setInputVerifier(new CompositeVerifier(MyInputVerifier.getNotEmpty()));

        final var dateField = new JTextField(DateTimeUtils.toString(vaccineDTO.getTime()), 20);
        dateField.setName("Vaccine Date");
        dateField.setInputVerifier(new CompositeVerifier(validDateTimeFormat, dateTimeInPast));

        final var panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panel.add(new JLabel("Vaccine Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Vaccine Date:"));
        panel.add(dateField);


        final var saveBtn = new JButton("Save");
        saveBtn.addActionListener(e -> {
            vaccineDTO.setName(nameField.getText());
            vaccineDTO.setTime(DateTimeUtils.from(dateField.getText()));

            vaccineById.put(vaccineDTO.getId(), vaccineDTO);
            dispose();
        });

        final var cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        final var btnPanel = new JPanel(new FlowLayout(RIGHT));
        btnPanel.add(saveBtn);
        btnPanel.add(cancelBtn);

        add(panel, CENTER);
        add(btnPanel, SOUTH);
        pack();
        setLocationRelativeTo(owner);
    }
}
