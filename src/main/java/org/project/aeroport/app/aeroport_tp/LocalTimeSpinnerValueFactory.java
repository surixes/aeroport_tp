package org.project.aeroport.app.aeroport_tp;

import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class LocalTimeSpinnerValueFactory extends SpinnerValueFactory<LocalTime> {
    private final LocalTime min;
    private final LocalTime max;
    private final int stepMinutes;

    public LocalTimeSpinnerValueFactory(LocalTime min, LocalTime max, int stepMinutes) {
        this.min = min;
        this.max = max;
        this.stepMinutes = stepMinutes;

        // Set the initial value
        setValue(min);
    }

    @Override
    public void decrement(int steps) {
        if (getValue() == null) {
            setValue(min);
        } else {
            LocalTime newValue = getValue().minusMinutes(stepMinutes * steps);
            if (newValue.isBefore(min)) {
                newValue = max; // Cyclical behavior
            }
            setValue(newValue);
        }
    }

    @Override
    public void increment(int steps) {
        if (getValue() == null) {
            setValue(min);
        } else {
            LocalTime newValue = getValue().plusMinutes(stepMinutes * steps);
            if (newValue.isAfter(max)) {
                newValue = min; // Cyclical behavior
            }
            setValue(newValue);
        }
    }

    /**
     * Configures the Spinner for manual time input.
     */
    public static void configureTimeSpinner(Spinner<LocalTime> spinner) {
        spinner.setEditable(true);

        // Create a StringConverter to handle LocalTime conversion
        StringConverter<LocalTime> timeConverter = new StringConverter<>() {
            @Override
            public String toString(LocalTime time) {
                return time != null ? time.toString() : "";
            }

            @Override
            public LocalTime fromString(String string) {
                try {
                    return LocalTime.parse(string);
                } catch (DateTimeParseException e) {
                    return null; // Return null for invalid input
                }
            }
        };

        // Set a TextFormatter with the StringConverter
        TextFormatter<LocalTime> timeFormatter = new TextFormatter<>(timeConverter, LocalTime.MIN);

        // Bind the TextFormatter to the Spinner's editor
        spinner.getEditor().setTextFormatter(timeFormatter);

        // Listen for text changes and update the Spinner's value
        spinner.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            try {
                spinner.getValueFactory().setValue(LocalTime.parse(newValue));
            } catch (DateTimeParseException ignored) {
                // Ignore invalid input
            }
        });
    }
}
