package org.pet.management.common;

import lombok.Getter;

import javax.swing.*;

import static java.lang.Integer.parseInt;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.pet.management.config.AppConfig.getProp;


public class MyInputVerifier {
    private static final String validNameRegex = getProp("valid.name.regexp");
    private static final int minNameLength = parseInt(getProp("min.name.length"));
    private static final int maxNameLength = parseInt(getProp("max.name.length"));
    @Getter
    public static InputVerifier notEmpty = new InputVerifier() {
        public boolean verify(JComponent input) {
            final String text = ((JTextField) input).getText();
            if (text != null && !text.trim().isEmpty()) {
                return true;
            } else {
                showMessageDialog(input, input.getName() + " cannot be empty!", "Validation Error", ERROR_MESSAGE);
                return false;
            }
        }
    };

    @Getter
    public static InputVerifier numberOnly = new InputVerifier() {
        public boolean verify(final JComponent input) {
            try {
                parseInt(((JTextField) input).getText());
                return true;
            } catch (NumberFormatException e) {
                showMessageDialog(input, input.getName() + "value must be integer only!", "Validation Error",
                        ERROR_MESSAGE
                );
                return false;
            }
        }
    };

    @Getter
    public static InputVerifier validName = new InputVerifier() {
        @Override
        public boolean verify(JComponent input) {
            final String text = ((JTextField) input).getText().trim();
            if (!text.matches(validNameRegex)) {
                JOptionPane.showMessageDialog(
                        input,
                        "Please enter a valid name (only letters and spaces, min 2 characters).",
                        "Invalid " + input.getName(),
                        ERROR_MESSAGE
                );
                return false;
            }
            return true;
        }
    };

    @Getter
    public static InputVerifier validNameLength = new InputVerifier() {
        @Override
        public boolean verify(final JComponent input) {
            final String text = ((JTextField) input).getText().trim();
            if (text.length() >= minNameLength && text.length() <= maxNameLength) {
                JOptionPane.showMessageDialog(
                        input,
                        String.format("%s length be between %s and %s", input.getName(), minNameLength, maxNameLength),
                        "Invalid " + input.getName(),
                        ERROR_MESSAGE
                );
                return false;
            }
            return true;
        }
    };

}
