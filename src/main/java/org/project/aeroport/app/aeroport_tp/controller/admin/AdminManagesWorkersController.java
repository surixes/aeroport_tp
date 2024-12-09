package org.project.aeroport.app.aeroport_tp.controller.admin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import org.project.aeroport.app.aeroport_tp.service.DatabaseService;

import static org.project.aeroport.app.aeroport_tp.Help.loadScreen;

public class AdminManagesWorkersController {

    @FXML
    private void handleAddWorker() {
        try {
            TextInputDialog usernameDialog = new TextInputDialog();
            usernameDialog.setTitle("Добавление работника");
            usernameDialog.setHeaderText("Введите данные нового работника");
            usernameDialog.setContentText("Имя пользователя:");
            Optional<String> username = usernameDialog.showAndWait();

            if (username.isEmpty()) {
                return;
            }

            TextInputDialog passwordDialog = new TextInputDialog();
            passwordDialog.setContentText("Пароль:");
            Optional<String> password = passwordDialog.showAndWait();

            if (password.isEmpty()) {
                return;
            }

            TextInputDialog fullNameDialog = new TextInputDialog();
            fullNameDialog.setContentText("ФИО работника:");
            Optional<String> fullName = fullNameDialog.showAndWait();

            if (fullName.isEmpty()) {
                return;
            }

            ChoiceDialog<String> positionDialog = new ChoiceDialog<>(
                    "Служащий аэропорта",
                    "Служащий аэропорта", "Таможенник", "Пограничник"
            );
            positionDialog.setTitle("Выбор должности");
            positionDialog.setHeaderText("Выберите должность:");
            Optional<String> position = positionDialog.showAndWait();

            if (position.isEmpty()) {
                return;
            }

            TextInputDialog salaryDialog = new TextInputDialog("0.00");
            salaryDialog.setContentText("Зарплата:");
            Optional<String> salaryInput = salaryDialog.showAndWait();

            if (salaryInput.isEmpty()) {
                return;
            }

            double salary;
            try {
                salary = Double.parseDouble(salaryInput.get());
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Введите корректное значение зарплаты!");
                return;
            }

            try (Connection connection = DatabaseService.getConnection()) {
                String query = "INSERT INTO worker (username, password, full_name, position, salary) VALUES (?, ?, ?, ?, ?);";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, username.get());
                    stmt.setString(2, password.get());
                    stmt.setString(3, fullName.get());
                    stmt.setString(4, position.get());
                    stmt.setDouble(5, salary);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        showAlert("Успешно", "Работник добавлен!");
                    } else {
                        showAlert("Ошибка", "Не удалось добавить работника!");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при работе с базой данных!");
        }
    }


    @FXML
    private void handleDeleteWorker() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Удаление работника");
        dialog.setHeaderText("Удалить работника");
        dialog.setContentText("Введите username работника:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(username -> {
            try {
                try (Connection connection = DatabaseService.getConnection()) {
                    String query = "DELETE FROM worker WHERE username = ?;";

                    try (PreparedStatement stmt = connection.prepareStatement(query)) {
                        stmt.setString(1, username);
                        int rowsAffected = stmt.executeUpdate();

                        if (rowsAffected > 0) {
                            showAlert("Успешно", "Работник с ID " + username + " удалён!");
                        } else {
                            showAlert("Ошибка", "Работник с ID " + username + " не найден!");
                        }
                    }
                }
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Введите корректный числовой ID!");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Ошибка", "Произошла ошибка при удалении работника.");
            }
        });
    }


    @FXML
    private void handleUpdateWorker() {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Изменение работника");
            dialog.setHeaderText("Изменить данные работника");
            dialog.setContentText("Введите username работника:");
            Optional<String> usernameInput = dialog.showAndWait();

            if (usernameInput.isEmpty()) {
                return;
            }
            String username = usernameInput.get();

            TextInputDialog salaryDialog = new TextInputDialog();
            salaryDialog.setContentText("Введите новую зарплату:");
            Optional<String> newSalaryInput = salaryDialog.showAndWait();

            if (newSalaryInput.isEmpty()) {
                return;
            }

            double newSalary;
            try {
                newSalary = Double.parseDouble(newSalaryInput.get());
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Зарплата должна быть числом!");
                return;
            }

            try (Connection connection = DatabaseService.getConnection()) {
                String query = "UPDATE worker SET salary = ? WHERE username = ?;";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setDouble(1, newSalary);
                    stmt.setString(2, username);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        showAlert("Успешно", "Зарплата работника обновлена!");
                    } else {
                        showAlert("Ошибка", "Работник с указанным username не найден!");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при работе с базой данных!");
        }
    }


    @FXML
    private void handleBack() {
        loadScreen("/AdminScreen.fxml", "Выбор Действия");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
