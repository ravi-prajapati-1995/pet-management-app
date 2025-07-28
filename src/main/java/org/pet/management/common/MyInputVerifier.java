package org.pet.management.common;

import lombok.Getter;
import org.pet.management.util.DateTimeUtils;

import javax.swing.*;

import java.time.LocalDateTime;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static org.pet.management.config.AppConfig.getProp;


public class MyInputVerifier {
    private static final String validNameRegex = getProp("valid.name.regexp");
    private static final int minNameLength = parseInt(getProp("min.name.length"));
    private static final int maxNameLength = parseInt(getProp("max.name.length"));
    private static final int petMaxAge = parseInt(getProp("pet.max.age"));
    private static final String validPhoneNumberRegex = getProp("valid.phone.regexp");
    private static final String validDateTimeRegex = getProp("valid.date.format.regexp");

    @Getter
    public static InputVerifier notEmpty = new InputVerifier() {
        public boolean verify(final JComponent input) {
            final var text = ((JTextField) input).getText();
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
            } catch (final NumberFormatException e) {
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
        public boolean verify(final JComponent input) {
            final var text = ((JTextField) input).getText().trim();
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
            final var text = ((JTextField) input).getText().trim();
            if (text.length() < minNameLength || text.length() > maxNameLength) {
                JOptionPane.showMessageDialog(
                        input,
                        format("%s length be between %s and %s", input.getName(), minNameLength, maxNameLength),
                        "Invalid " + input.getName(),
                        ERROR_MESSAGE
                );
                return false;
            }
            return true;
        }
    };

    @Getter
    public static InputVerifier validateMaxAge = new InputVerifier() {
        @Override
        public boolean verify(final JComponent input) {
            final var text = ((JTextField) input).getText().trim();
            if (Integer.parseInt(text) > petMaxAge) {
                JOptionPane.showMessageDialog(
                        input,
                        format("Age should be less than equal %s", petMaxAge),
                        "Invalid " + input.getName(),
                        ERROR_MESSAGE
                );
                return false;
            }
            return true;
        }
    };

    @Getter
    public static InputVerifier validPhoneNumber = new InputVerifier() {
        @Override
        public boolean verify(final JComponent input) {
            final var text = ((JTextField) input).getText().trim();
            if (!text.matches(validPhoneNumberRegex)) {
                JOptionPane.showMessageDialog(
                        input,
                        "Please enter a valid phone number.e.g. +919876543210/+447123456789",
                        "Invalid " + input.getName(),
                        ERROR_MESSAGE
                );
                return false;
            }
            return true;
        }
    };

    @Getter
    public static InputVerifier validDateTimeFormat = new InputVerifier() {
        @Override
        public boolean verify(final JComponent input) {
            final var text = ((JTextField) input).getText().trim();
            if (!text.matches(validDateTimeRegex)) {
                JOptionPane.showMessageDialog(
                        input,
                        "Date should be in (yyyy-MM-dd HH:mm)",
                        "Invalid " + input.getName(),
                        ERROR_MESSAGE
                );
                return false;
            }
            return true;
        }
    };

    @Getter
    public static InputVerifier dateTimeInPast = new InputVerifier() {
        @Override
        public boolean verify(final JComponent input) {
            final var text = ((JTextField) input).getText().trim();
            try {
                if (DateTimeUtils.from(text).isAfter(LocalDateTime.now())) {
                    JOptionPane.showMessageDialog(
                            input,
                            "Date must be in past",
                            "Invalid " + input.getName(),
                            ERROR_MESSAGE
                    );
                    return false;
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        input,
                        "Date is not valid",
                        "Invalid " + input.getName(),
                        ERROR_MESSAGE
                );
                return false;
            }
            return true;
        }
    };
}
