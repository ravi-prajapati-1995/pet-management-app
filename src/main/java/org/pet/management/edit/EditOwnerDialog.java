package org.pet.management.edit;

import lombok.extern.slf4j.Slf4j;
import org.pet.management.common.CompositeVerifier;
import org.pet.management.dto.request.OwnerUpdateDTO;
import org.pet.management.pet.PetListFrame;

import javax.swing.*;
import java.awt.*;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.pet.management.common.MyInputVerifier.*;
import static org.pet.management.pet.PetListFrame.getPetDetails;
import static org.pet.management.util.PetAppClient.getClient;

@Slf4j
public class EditOwnerDialog extends JDialog {
    private final JTextField ownerNameField;
    private final JTextField ownerPhoneField;
    private final int ownerId;

    public EditOwnerDialog(JFrame parent, final int petId) {
        super(parent, "Edit Pet Details", true); // modal dialog
        final var petDetailsDTO = getPetDetails(petId);
        this.ownerId = petDetailsDTO.getOwnerId();

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        final JPanel panel = new JPanel(new GridLayout(6, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Owner Name:"));
        ownerNameField = new JTextField(petDetailsDTO.getOwner());
        ownerNameField.setName("Owner Name");
        ownerNameField.setInputVerifier(new CompositeVerifier(getNotEmpty(), getValidNameLength()));
        panel.add(ownerNameField);

        panel.add(new JLabel("Owner Telephone:"));
        ownerPhoneField = new JTextField(petDetailsDTO.getPhoneNumber());
        ownerPhoneField.setInputVerifier(new CompositeVerifier(getNotEmpty(), getValidPhoneNumber()));
        ownerPhoneField.setName("Phone Number");
        panel.add(ownerPhoneField);

        final JButton saveButton = new JButton("Save");
        panel.add(new JLabel()); // empty label for spacing
        panel.add(saveButton);

        add(panel);

        saveButton.addActionListener(e -> updateOwnerDetails());

        // Focus petNameField on open
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent e) {
                ownerNameField.requestFocusInWindow();
            }
        });
    }

    private void updateOwnerDetails() {
        try {
            final String ownerName = ownerNameField.getText();
            final String ownerPhone = ownerPhoneField.getText();

            final var client = getClient();
            final var ownerUpdateDTO = OwnerUpdateDTO.from(ownerName, ownerPhone);
            client.updateOwner(ownerId, ownerUpdateDTO);

            PetListFrame.updateOwnerDetails(ownerId, ownerUpdateDTO);
            dispose();
            showMessageDialog(this, "Owner details Updated Successfully");
        } catch (final Exception exception) {
            log.error("Error while updating owner details: {}", exception.getMessage(), exception);
            showMessageDialog(this, exception.getMessage(), "Error Occurred", ERROR_MESSAGE);
        } finally {
            dispose();
        }

    }
}
