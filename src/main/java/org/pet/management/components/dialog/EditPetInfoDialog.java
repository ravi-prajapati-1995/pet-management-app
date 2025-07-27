package org.pet.management.components.dialog;

import org.pet.management.common.CompositeVerifier;
import org.pet.management.dto.request.PetUpdateDto;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static javax.swing.BorderFactory.createEmptyBorder;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.pet.management.common.MyInputVerifier.*;
import static org.pet.management.components.frame.PetListFrame.getPetDetails;
import static org.pet.management.components.frame.PetListFrame.updatePetDetails;
import static org.pet.management.util.PetAppClient.getClient;

public class EditPetInfoDialog extends JDialog {
    private final JTextField petNameField;
    private final JTextField ageField;
    private final int petId;

    public EditPetInfoDialog(final JFrame parent, final int petId) {
        super(parent, "Edit Pet Details", true); // modal dialog
        this.petId = petId;
        final var petDetailsDTO = getPetDetails(petId);

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        final var panel = new JPanel(new GridLayout(6, 2, 8, 8));
        panel.setBorder(createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Pet Name:"));
        petNameField = new JTextField(petDetailsDTO.getName());
        petNameField.setName("Pet Name");
        panel.add(petNameField);
        petNameField.setInputVerifier(new CompositeVerifier(getNotEmpty(), getValidNameLength()));

        panel.add(new JLabel("Age (In years):"));
        ageField = new JTextField(String.valueOf(petDetailsDTO.getAge()));
        ageField.setName("Age");
        ageField.setInputVerifier(new CompositeVerifier(getNotEmpty(), getNumberOnly(), getValidateMaxAge()));

        panel.add(ageField);

        final var saveButton = new JButton("Save");
        panel.add(new JLabel()); // empty label for spacing
        panel.add(saveButton);
        add(panel);

        saveButton.addActionListener(e -> updateData());

        addWindowListener(new WindowAdapter() {
            public void windowOpened(final WindowEvent e) {
                petNameField.requestFocusInWindow();
            }
        });
    }

    private void updateData() {
        try {
            final var petName = petNameField.getText();
            ageField.getInputVerifier().verify(ageField);
            final var age = Integer.parseInt(ageField.getText());

            final var client = getClient();
            final var petUpdateDto = PetUpdateDto.from(petName, age);
            client.updatePet(petId, petUpdateDto);

            updatePetDetails(petId, petUpdateDto);
            dispose();
            showMessageDialog(this, "Pet details Updated Successfully");
        } catch (final Exception e) {
            showMessageDialog(this, e.getMessage(), "Error Occurred", ERROR_MESSAGE);
        } finally {
            dispose();
        }
    }
}
