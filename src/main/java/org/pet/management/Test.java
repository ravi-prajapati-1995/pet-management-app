package org.pet.management;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

public class Test {
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(Test::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        final var frame = new JFrame("JDatePicker Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new FlowLayout());

        // Setup the date model
        final var model = new UtilDateModel();
        model.setSelected(true);  // today's date

        // Set properties for format
        final var p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");

        // Create the date panel and picker
        final var datePanel = new JDatePanelImpl(model, p);
        final var datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        // Time field (optional)
        final var timeField = new JTextField("14:30", 5);

        // Button to get selected date and time
        final var getDateButton = new JButton("Get Date-Time");
        getDateButton.addActionListener(e -> {
            final var selectedDate = (Date) datePicker.getModel().getValue();
            final var time = timeField.getText();
            final var sdf = new SimpleDateFormat("yyyy-MM-dd");
            final var fullDateTime = sdf.format(selectedDate) + " " + time;
            JOptionPane.showMessageDialog(frame, "Selected Date-Time: " + fullDateTime);
        });

        frame.add(new JLabel("Select Date:"));
        frame.add(datePicker);
        frame.add(new JLabel("Time (HH:mm):"));
        frame.add(timeField);
        frame.add(getDateButton);

        frame.setVisible(true);
    }

    // Formatter for the date picker
    public static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        @Override
        public Object stringToValue(final String text) throws ParseException {
            return dateFormatter.parse(text);
        }

        @Override
        public String valueToString(Object value) {
            if (value != null) {
                if (value instanceof GregorianCalendar) {
                    value =
                            ((GregorianCalendar) value).toZonedDateTime().toLocalDateTime()
                                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
                return "1995-07-30";
            }
            return "";
        }
    }
}
