package org.pet.management.components.frame;

import lombok.extern.slf4j.Slf4j;
import org.pet.management.util.PetAppClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static org.pet.management.util.LoadingUtils.hideLoadingIcon;
import static org.pet.management.util.LoadingUtils.showLoadingIcon;
import static org.pet.management.util.UIUtils.RoundedBorder;

@Slf4j
public final class LoginFrame extends JFrame {
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    public LoginFrame() {
        setTitle("Pet Management Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        final var panel = new JPanel(new GridLayout(3, 2, 0, 8));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        usernameField.setName("User Name");
        usernameField.setBorder(new RoundedBorder(10));
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        passwordField.setName("Password");
        passwordField.setBorder(new RoundedBorder(10));
        panel.add(passwordField);

        loginButton = new JButton("Login");
        panel.add(new JLabel()); // empty label for spacing
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        log.debug("performing login");

        try {
            final var username = usernameField.getText().trim();
            final var password = new String(passwordField.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            showLoadingIcon(this);
            final var client = PetAppClient.getClient();
            final var login = client.login(username, password);
            if (login.isLoggedIn()) {
                final var petListFrame = new PetListFrame();
                petListFrame.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, login.getErrorMessage(), "Error", ERROR_MESSAGE);
            }
        } catch (final Exception e) {
            log.error("error while login ", e);
            JOptionPane.showMessageDialog(this, "Some error occurred ", "Error", ERROR_MESSAGE);

        } finally {
            hideLoadingIcon(this);
        }

    }
}
