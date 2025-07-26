package org.pet.management.login;

import lombok.extern.slf4j.Slf4j;
import org.pet.management.petlist.PetListFrame;
import org.pet.management.login.dto.LoginResponseDTO;
import org.pet.management.util.PetAppClient;

import javax.swing.*;
import java.awt.*;

@Slf4j
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        log.debug("log is working correctrl");
        setTitle("Pet Management Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);

        final JPanel panel = new JPanel(new GridLayout(3, 2));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Login");
        panel.add(new JLabel()); // empty label for spacing
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        final String username = usernameField.getText();
        final String password = new String(passwordField.getPassword());

        final PetAppClient client = PetAppClient.getClient();
        final LoginResponseDTO login = client.login(username, password);
        if (login.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, "Login successful!");

            final PetListFrame petListFrame = new PetListFrame();
            petListFrame.setVisible(true);

            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
