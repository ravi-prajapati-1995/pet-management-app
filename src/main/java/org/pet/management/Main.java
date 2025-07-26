package org.pet.management;

import org.pet.management.login.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            final LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}