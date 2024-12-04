package org.project.aeroport.app.aeroport_tp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.project.aeroport.app.aeroport_tp.service.LoginService;

import static org.project.aeroport.app.aeroport_tp.Help.loadScreen;

public class WorkerLoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (LoginService.isWorker(username, password)) {
            showAlert("Успех", "Вы успешно вошли!", Alert.AlertType.INFORMATION);
            loadScreen("/WorkerScreen.fxml", "Выбор действия");
        } else {
            showAlert("Ошибка", "Неверное имя пользователя или пароль.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBack() {
        loadScreen("/UserSelectionController.fxml", "Выберите действие");
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
