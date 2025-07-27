package org.pet.management.components.buttonPannel;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.pet.management.components.dialog.EditOwnerDialog;
import org.pet.management.components.dialog.EditPetInfoDialog;
import org.pet.management.components.dialog.EditVaccineDialog;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

import static java.awt.FlowLayout.CENTER;
import static java.awt.FlowLayout.LEFT;
import static java.awt.Image.SCALE_SMOOTH;
import static org.pet.management.config.AppConfig.getProp;

@Slf4j
public class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
    private boolean clicked;
    private int row;
    private JTable table;
    private final JFrame petListFrame;
    private final JPanel panel = new JPanel(new FlowLayout(CENTER, 5, 0));


    public ButtonEditor(final JCheckBox checkBox, final JFrame petListFrame) {
        panel.setPreferredSize(new Dimension(300, 40));
        panel.setLayout(new FlowLayout(LEFT, 10, 5));
        final var buttonSize = new Dimension(20, 20);

        final var petEditButton = new JButton(getIcon("pet.edit.icon"));
        petEditButton.setPreferredSize(buttonSize);
        petEditButton.setToolTipText("Edit Pet");
        panel.add(petEditButton);

        final var ownerEditButton = new JButton(getIcon("owner.edit.icon"));
        ownerEditButton.setPreferredSize(buttonSize);
        ownerEditButton.setToolTipText("Edit Owner");
        panel.add(ownerEditButton);

        final var vaccineEditButton = new JButton(getIcon("vaccine.edit.icon"));
        vaccineEditButton.setPreferredSize(buttonSize);
        vaccineEditButton.setToolTipText("Edit Vaccine info");

        panel.add(vaccineEditButton);

        petEditButton.addActionListener(e -> {
            openPetEditFrame(row);
        });

        ownerEditButton.addActionListener(e -> {
            log.debug("clicked on the owner edit button");
            openOwnerEditDialog(row);
        });
        vaccineEditButton.addActionListener(e -> {
            log.debug("Clicked on the vaccine button");
            openVaccineEditor(row);
        });

        this.petListFrame = petListFrame;
    }

    @Override
    public Component getTableCellEditorComponent(
            final JTable table, final Object obj, final boolean selected, final int row, final int col
    ) {
        this.table = table;
        this.row = row;
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "Edit";
    }

    private void openPetEditFrame(final int row) {
        log.debug("Clicked edit pet for row number: {}", row);
        final var petId = (Long) table.getValueAt(row, 0);
        final var dialog = new EditPetInfoDialog(petListFrame, petId);
        dialog.setVisible(true);
    }

    private void openOwnerEditDialog(final int row) {
        log.debug("Clicked edit owner for row number: {}", row);
        final var petId = (Long) table.getValueAt(row, 0);
        final var dialog = new EditOwnerDialog(petListFrame, petId);
        dialog.setVisible(true);
    }

    private void openVaccineEditor(final int row) {
        log.debug("Clicked edit vaccine for row number: {}", row);
        final var petId = (Long) table.getValueAt(row, 0);
        final var dialog = new EditVaccineDialog(petListFrame, (long) petId);
        dialog.setVisible(true);
    }

    @Override
    public boolean stopCellEditing() {
        clicked = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }

    @NotNull
    private ImageIcon getIcon(final String key) {
        final var originalIcon = new ImageIcon(getClass().getResource(getProp(key)));
        final var scaledImage = originalIcon.getImage().getScaledInstance(20, 20, SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
}