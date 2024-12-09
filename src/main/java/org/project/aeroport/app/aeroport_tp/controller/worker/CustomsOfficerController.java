package org.project.aeroport.app.aeroport_tp.controller.worker;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.project.aeroport.app.aeroport_tp.Help;
import org.project.aeroport.app.aeroport_tp.service.DatabaseService;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import static org.project.aeroport.app.aeroport_tp.Help.loadScreen;

public class CustomsOfficerController {

    @FXML
    private TextField baggageIdField;

    @FXML
    private ChoiceBox<String> inspectionResultBox;

    private final Map<String, String> inspectionRecords = new HashMap<>();

    @FXML
    private void initialize() {
        inspectionResultBox.getItems().addAll("Положительно", "Отрицательно");
        inspectionResultBox.setValue("Положительно");
    }

    @FXML
    private void inspectBaggage() {
        String PassportId = baggageIdField.getText();
        String inspectionResult = inspectionResultBox.getValue();

        if (PassportId.isEmpty()) {
            showAlert("Ошибка", "Введите номер паспорта", Alert.AlertType.ERROR);
        } else {
            if (verifyPassengerByPassport(PassportId)) {
                inspectionRecords.put(PassportId, inspectionResult);
                System.out.println("Проверка багажа: " + PassportId + " - " + inspectionResult);
                showAlert("Успех", "Результат проверки сохранен: " + inspectionResult, Alert.AlertType.INFORMATION);
            }
        }
    }

    @FXML
    private boolean verifyPassengerByPassport(String passportNumber) {
        try (Connection connection = DatabaseService.getConnection()) {
            String sql = "SELECT full_name, email, client_id, all_check_passed FROM clients WHERE passport_data = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, passportNumber);
                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    int clientId = resultSet.getInt("client_id");
                    int allCheckPassed = resultSet.getInt("all_check_passed");
                    allCheckPassed = Help.updateCheckStatus(clientId, allCheckPassed);
                    showAlert("Успех", "Результат проверки сохранен: " + "Положительно. " + "Проверок пройдено: " + allCheckPassed + ".", Alert.AlertType.INFORMATION);
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
