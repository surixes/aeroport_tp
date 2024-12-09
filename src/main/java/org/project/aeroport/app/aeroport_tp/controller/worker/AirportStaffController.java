package org.project.aeroport.app.aeroport_tp.controller.worker;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

public class AirportStaffController {


    @FXML
    private TextField taskDescriptionField;

    @FXML
    private TextField passportField;

    @FXML
    private TextField ticketField;

    private final Map<String, String> completedTasks = new HashMap<>();

    @FXML
    private void verifyPassportAndTicket() {
        String passportNumber = passportField.getText();
        String ticketNumber = ticketField.getText();

        if (passportNumber.isEmpty() || ticketNumber.isEmpty()) {
            showAlert("Ошибка", "Введите номер паспорта и билет", Alert.AlertType.ERROR);
            return;
        }

        try (Connection connection = DatabaseService.getConnection()) {
            String clientQuery = "SELECT client_id, all_check_passed FROM clients WHERE passport_data = ?";
            try (PreparedStatement clientStmt = connection.prepareStatement(clientQuery)) {
                clientStmt.setString(1, passportNumber);
                ResultSet clientResultSet = clientStmt.executeQuery();

                if (clientResultSet.next()) {
                    int clientId = clientResultSet.getInt("client_id");
                    int allCheckPassed = clientResultSet.getInt("all_check_passed");

                    int ticketId;
                    try {
                        ticketId = Integer.parseInt(ticketNumber);
                    } catch (NumberFormatException e) {
                        showAlert("Ошибка", "Номер билета должен быть числом.", Alert.AlertType.ERROR);
                        return;
                    }

                    String ticketQuery = "SELECT ticket_id FROM tickets WHERE client_id = ? AND ticket_id = ? AND is_sold = TRUE";
                    try (PreparedStatement ticketStmt = connection.prepareStatement(ticketQuery)) {
                        ticketStmt.setInt(1, clientId);
                        ticketStmt.setInt(2, ticketId);
                        ResultSet ticketResultSet = ticketStmt.executeQuery();

                        if (ticketResultSet.next()) {
                            allCheckPassed = Help.updateCheckStatus(clientId, allCheckPassed);
                            showAlert("Успех", "Клиент с паспортом " + passportNumber + " имеет действительный билет. Проверок пройдено: " + allCheckPassed + ".", Alert.AlertType.INFORMATION);
                        } else {
                            showAlert("Ошибка", "У клиента нет действительного билета с номером " + ticketNumber, Alert.AlertType.ERROR);
                        }
                    }
                } else {
                    showAlert("Ошибка", "Клиент с паспортом " + passportNumber + " не найден.", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            showAlert("Ошибка", "Ошибка при подключении к базе данных: " + e.getMessage(), Alert.AlertType.ERROR);
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
