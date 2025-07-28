package org.pet.management;

import org.pet.management.components.frame.LoginFrame;

import javax.swing.*;

public class AppLauncher {
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            final var frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}