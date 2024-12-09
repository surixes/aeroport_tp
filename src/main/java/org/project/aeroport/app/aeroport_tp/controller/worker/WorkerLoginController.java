package org.project.aeroport.app.aeroport_tp.controller.worker;

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

        String workerRole = LoginService.getWorkerRole(username, password);

        if (LoginService.isWorker(username, password)) {
            if (workerRole != null) {
                showAlert("Успех", "Вы успешно вошли как " + workerRole + "!", Alert.AlertType.INFORMATION);
                navigateToWorkerScreen(workerRole);
            }
        } else {
            showAlert("Ошибка", "Неверное имя пользователя или пароль.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleBack() {
        loadScreen("/UserSelectionScreen.fxml", "Выберите действие");
    }

    private void navigateToWorkerScreen(String role) {
        switch (role) {
            case "Пограничник":
                loadScreen("/BorderOfficerScreen.fxml", "Рабочий экран Пограничника");
                break;
            case "Таможенник":
                loadScreen("/CustomsOfficerScreen.fxml", "Рабочий экран Таможенника");
                break;
            case "Служащий аэропорта":
                loadScreen("/AirportStaffScreen.fxml", "Рабочий экран Служащего аэропорта");
                break;
            default:
                showAlert("Ошибка", "Неизвестная роль: " + role, Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

