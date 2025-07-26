package org.pet.management.util;

import javax.swing.*;
import java.awt.*;

public class LoadingUtils {
    private static JPanel glass ;


    static {
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true); // spinner style
        progressBar.setString("Loading...");
        progressBar.setStringPainted(true);

        glass = new JPanel(new GridBagLayout());
        glass.setOpaque(false);
        glass.add(progressBar, new GridBagConstraints());
        glass.setBackground(new Color(0, 0, 0, 50));
        glass.setOpaque(true);
    }

    public static void showProgressBar(final JFrame frame) {

    }
    public static void showLoadingIcon(final JFrame frame) {
        frame.setGlassPane(glass);
        glass.setVisible(true);
    }

    public static void hideLoadingIcon(final JFrame frame) {
        glass.setVisible(false);
    }
}
