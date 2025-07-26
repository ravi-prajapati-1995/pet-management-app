package org.pet.management.edit;

import org.pet.management.common.CompositeVerifier;

import javax.swing.*;
import java.awt.*;

import static javax.swing.BorderFactory.createEmptyBorder;
import static org.pet.management.common.MyInputVerifier.*;
import static org.pet.management.petlist.PetListFrame.getPetDetails;

public class EditPetInfoDialog extends JDialog {
    private final JTextField petNameField;
    private final JTextField ageField;

    public EditPetInfoDialog(JFrame parent, final int petId) {
        super(parent, "Edit Pet Details", true); // modal dialog

        final var petDetailsDTO = getPetDetails(petId);

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        final JPanel panel = new JPanel(new GridLayout(6, 2, 8, 8));
        panel.setBorder(createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Pet Name:"));
        petNameField = new JTextField(petDetailsDTO.getName());
        petNameField.setName("Pet Name");
        panel.add(petNameField);
        petNameField.setInputVerifier(new CompositeVerifier(getNotEmpty()));

        panel.add(new JLabel("Age:"));
        ageField = new JTextField(String.valueOf(petDetailsDTO.getAge()));
        ageField.setName("Age");
        ageField.setInputVerifier(new CompositeVerifier(getNotEmpty(), getNumberOnly()));

        panel.add(ageField);

        final JButton saveButton = new JButton("Save");
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
        ageField.getInputVerifier().verify(ageField);
        final int age = Integer.parseInt(ageField.getText());
        JOptionPane.showMessageDialog(this, "Saved:\nPet: " + petName + "\nAge: " + age );
        dispose(); // Close dialog
    }
}
