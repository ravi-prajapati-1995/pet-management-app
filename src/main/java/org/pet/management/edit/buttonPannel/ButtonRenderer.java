package org.pet.management.edit.buttonPannel;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

import static java.awt.FlowLayout.LEFT;
import static java.awt.Image.SCALE_SMOOTH;
import static org.pet.management.config.AppConfig.getProp;

@Slf4j
public class ButtonRenderer extends JPanel implements TableCellRenderer {
    public ButtonRenderer() {
        ToolTipManager.sharedInstance().setInitialDelay(1);
        log.debug("log is working correctrl");
        setLayout(new FlowLayout(LEFT, 10, 5));
        final Dimension buttonSize = new Dimension(20, 20);

        final JButton petEditButton = new JButton(getIcon("pet.edit.icon"));
        petEditButton.setPreferredSize(buttonSize);
        petEditButton.setToolTipText("Edit Pet");
        add(petEditButton);

        final JButton ownerEditButton = new JButton(getIcon("owner.edit.icon"));
        ownerEditButton.setPreferredSize(buttonSize);
        ownerEditButton.setToolTipText("Edit Owner");
        add(ownerEditButton);

        final JButton vaccineEditButton = new JButton(getIcon("vaccine.edit.icon"));
        vaccineEditButton.setPreferredSize(buttonSize);
        vaccineEditButton.setToolTipText("Edit Vaccine info");

        add(vaccineEditButton);
    }

    @NotNull
    private ImageIcon getIcon(final String key) {
        final ImageIcon originalIcon = new ImageIcon(getClass().getResource(getProp(key)));
        final Image scaledImage = originalIcon.getImage().getScaledInstance(20, 20, SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column
    ) {
        return this;
    }
}


