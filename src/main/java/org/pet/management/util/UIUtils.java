package org.pet.management.util;

import javax.swing.border.AbstractBorder;
import java.awt.*;

public class UIUtils {
    public static class RoundedBorder extends AbstractBorder {
        private final int radius;

        public RoundedBorder(final int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {
            final var g2 = (Graphics2D) g.create();
            g2.setColor(Color.GRAY);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(x + 1, y + 1, width - 3, height - 3, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(final Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        @Override
        public Insets getBorderInsets(final Component c, final Insets insets) {
            insets.set(radius / 2, radius / 2, radius / 2, radius / 2);
            return insets;
        }
    }
}
