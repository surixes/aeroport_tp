package org.project.aeroport.app.aeroport_tp.controller.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static org.project.aeroport.app.aeroport_tp.Help.loadScreen;

import org.project.aeroport.app.aeroport_tp.service.LoginService;

public class AdminLoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (LoginService.isAdmin(username, password)) {
            showAlert("Успех", "Вы успешно вошли!", AlertType.INFORMATION);
            loadScreen("/AdminScreen.fxml", "Выбор действия");
        } else {
            showAlert("Ошибка", "Неверное имя пользователя или пароль.", AlertType.ERROR);
        }
    }

    @FXML
    private void handleBack() {
        loadScreen("/UserSelectionScreen.fxml", "Выбор роли");
    }

    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
