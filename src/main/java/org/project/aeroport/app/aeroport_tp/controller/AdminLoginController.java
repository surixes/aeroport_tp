package org.project.aeroport.app.aeroport_tp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.project.aeroport.app.aeroport_tp.service.LoginService;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;  // Поле для имени пользователя
    @FXML
    private PasswordField passwordField;  // Поле для пароля

    // Метод для обработки нажатия кнопки "Войти"
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Проверка учетных данных
        if (LoginService.authenticateUser(username, password)) {
            showAlert("Успех", "Вы успешно вошли!", AlertType.INFORMATION);
            loadScreen("/AdminScreen.fxml", "Выбор действия");
            // Открытие основного окна или переход на другой экран
            // Здесь может быть вызов другого окна, например, для админа
        } else {
            showAlert("Ошибка", "Неверное имя пользователя или пароль.", AlertType.ERROR);
        }
    }

    // Метод для отображения всплывающих сообщений
    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadScreen(String fxmlFile, String title) {
        try {
            Stage stage = (Stage) Stage.getWindows().filtered(window -> window.isShowing()).get(0);
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource(fxmlFile)));
            stage.setTitle(title);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
