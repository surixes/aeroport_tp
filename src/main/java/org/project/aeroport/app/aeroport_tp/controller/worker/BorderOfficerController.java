package org.project.aeroport.app.aeroport_tp.controller.worker;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.project.aeroport.app.aeroport_tp.Help;
import org.project.aeroport.app.aeroport_tp.service.DatabaseService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.project.aeroport.app.aeroport_tp.Help.loadScreen;

public class BorderOfficerController {

    @FXML
    private TextField passportNumberField;

    @FXML
    private ChoiceBox<String> verificationResultBox;

    private final Map<String, String> verificationRecords = new HashMap<>();

    @FXML
    private void initialize() {
        verificationResultBox.getItems().addAll("Положительно", "Отрицательно");
        verificationResultBox.setValue("Положительно");
    }

    @FXML
    private void checkPassport() {
        String passportNumber = passportNumberField.getText();
        String verificationResult = verificationResultBox.getValue();

        if (passportNumber.isEmpty()) {
            showAlert("Ошибка", "Введите номер паспорта", Alert.AlertType.ERROR);
        } else {
            if (verifyPassengerByPassport(passportNumber)) {
                verificationRecords.put(passportNumber, verificationResult);
                System.out.println("Проверка паспорта: " + passportNumber + " - " + verificationResult);
            }
        }
    }

    @FXML
    private boolean verifyPassengerByPassport(String passportNumber) {
        if (passportNumber.isEmpty()) {
            showAlert("Ошибка", "Введите номер паспорта", Alert.AlertType.ERROR);
            return false;
        }

        try (Connection connection = DatabaseService.getConnection()) {
            String sql = "SELECT full_name, email, client_id, all_check_passed FROM clients WHERE passport_data = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, passportNumber);
                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    int clientId = resultSet.getInt("client_id");
                    int allCheckPassed = resultSet.getInt("all_check_passed");
                    allCheckPassed = Help.updateCheckStatus(clientId, allCheckPassed);
                    showAlert("Успех", "Результат проверки сохранен: " + "Положительно. " + "Проверок пройдено: " +allCheckPassed + ".", Alert.AlertType.INFORMATION);
                    return true;
                } else {
                    showAlert("Ошибка", "Пассажир с таким паспортом не найден", Alert.AlertType.ERROR);
                    return false;
                }
            }
        } catch (SQLException e) {
            showAlert("Ошибка", "Ошибка при подключении к базе данных: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }
    }

    @FXML
    private void handleBack() {
        loadScreen("/AuthenticationWorker.fxml", "Выбор действия");
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

