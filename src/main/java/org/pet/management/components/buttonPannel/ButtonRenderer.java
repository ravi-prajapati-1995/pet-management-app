package org.pet.management.components.buttonPannel;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.Objects;

import static java.awt.FlowLayout.LEFT;
import static java.awt.Image.SCALE_SMOOTH;
import static org.pet.management.config.AppConfig.getProp;

@Slf4j
public class ButtonRenderer extends JPanel implements TableCellRenderer {
    public ButtonRenderer() {
        ToolTipManager.sharedInstance().setInitialDelay(1);
        setLayout(new FlowLayout(LEFT, 10, 5));
        final var buttonSize = new Dimension(20, 20);

        final var petEditButton = new JButton(getIcon("pet.edit.icon"));
        petEditButton.setPreferredSize(buttonSize);
        petEditButton.setToolTipText("Edit Pet");
        add(petEditButton);

        final var ownerEditButton = new JButton(getIcon("owner.edit.icon"));
        ownerEditButton.setPreferredSize(buttonSize);
        ownerEditButton.setToolTipText("Edit Owner");
        add(ownerEditButton);

        final var vaccineEditButton = new JButton(getIcon("vaccine.edit.icon"));
        vaccineEditButton.setPreferredSize(buttonSize);
        vaccineEditButton.setToolTipText("Edit Vaccine info");

        add(vaccineEditButton);
    }

    @NotNull
    private ImageIcon getIcon(final String key) {
        final ImageIcon originalIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource(getProp(key))));
        final var scaledImage = originalIcon.getImage().getScaledInstance(20, 20, SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    @Override
    public Component getTableCellRendererComponent(
            final JTable table, final Object value,
            final boolean isSelected, final boolean hasFocus, final int row, final int column
    ) {
        return this;
    }
}


