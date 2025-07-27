package org.pet.management;

import org.pet.management.components.frame.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            final var frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}