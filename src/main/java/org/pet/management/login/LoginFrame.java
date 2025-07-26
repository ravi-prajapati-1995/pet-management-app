package org.pet.management.login;

import lombok.extern.slf4j.Slf4j;
import org.pet.management.dto.response.LoginResponseDTO;
import org.pet.management.pet.PetListFrame;
import org.pet.management.util.PetAppClient;

import javax.swing.*;
import java.awt.*;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static org.pet.management.util.LoadingUtils.hideLoadingIcon;
import static org.pet.management.util.LoadingUtils.showLoadingIcon;

@Slf4j
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginFrame() {
        setTitle("Pet Management Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);

        final JPanel panel = new JPanel(new GridLayout(3, 2));

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        usernameField.setName("User Name");
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        passwordField.setName("Password");
        panel.add(passwordField);

        loginButton = new JButton("Login");
        panel.add(new JLabel()); // empty label for spacing
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(e -> performLogin());
    }

    private void performLogin() {
        log.debug("performing login");

        final String username = usernameField.getText().trim();
        final String password = new String(passwordField.getPassword()).trim();
        if(username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        showLoadingIcon(this);

        final PetAppClient client = PetAppClient.getClient();
        final LoginResponseDTO login = client.login(username, password);
        if (login.isLoggedIn()) {
            final PetListFrame petListFrame = new PetListFrame();
            petListFrame.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", ERROR_MESSAGE);
        }
        hideLoadingIcon(this);
    }
}
