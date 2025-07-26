package org.pet.management.edit;

import javax.swing.*;
import java.awt.*;

import static org.pet.management.pet.PetListFrame.getPetDetails;

public class UpdateVaccineDialog extends JDialog {
    private final JTextField petNameField;
    private final JTextField ageField;
    private final JTextField lastVaccinationField;
    private final JTextField ownerNameField;
    private final JTextField ownerPhoneField;

    public UpdateVaccineDialog(JFrame parent, final int petId) {
        super(parent, "Edit Pet Details", true); // modal dialog

        final var petDetailsDTO = getPetDetails(petId);

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        final JPanel panel = new JPanel(new GridLayout(6, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Pet Name:"));
        petNameField = new JTextField(petDetailsDTO.getName());
        panel.add(petNameField);

        panel.add(new JLabel("Age:"));
        ageField = new JTextField(String.valueOf(petDetailsDTO.getAge()));
        panel.add(ageField);

        panel.add(new JLabel("Last Vaccination:"));
        lastVaccinationField = new JTextField(petDetailsDTO.getLastVaccine());
        panel.add(lastVaccinationField);

        panel.add(new JLabel("Owner Name:"));
        ownerNameField = new JTextField(petDetailsDTO.getOwner());
        panel.add(ownerNameField);

        panel.add(new JLabel("Owner Telephone:"));
        ownerPhoneField = new JTextField(petDetailsDTO.getPhoneNumber());
        panel.add(ownerPhoneField);

        JButton saveButton = new JButton("Save");
        panel.add(new JLabel()); // empty label for spacing
        panel.add(saveButton);

        add(panel);

        saveButton.addActionListener(e -> saveData());

        // Focus petNameField on open
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent e) {
                petNameField.requestFocusInWindow();
            }
        });
    }

    private void saveData() {
        final String petName = petNameField.getText();
        final int age = Integer.parseInt(ageField.getText());
        final String lastVaccination = lastVaccinationField.getText();
        final String ownerName = ownerNameField.getText();
        final String ownerPhone = ownerPhoneField.getText();

        JOptionPane.showMessageDialog(this, "Saved:\nPet: " + petName + "\nAge: " + age +
                "\nLast Vaccination: " + lastVaccination +
                "\nOwner: " + ownerName + "\nPhone: " + ownerPhone);

        // TODO: Connect backend call here

        dispose(); // Close dialog
    }
}
