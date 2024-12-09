package org.project.aeroport.app.aeroport_tp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.project.aeroport.app.aeroport_tp.service.DatabaseService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Help {
    public static void loadScreen(String fxmlFile, String title) {
        try {
            Stage stage = (Stage) Stage.getWindows().filtered(window -> window.isShowing()).get(0);
            FXMLLoader loader = new FXMLLoader(Help.class.getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 1200, 800);
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int updateCheckStatus(int clientId, int currentCheckCount) throws SQLException {
        if (currentCheckCount < 3) {
            currentCheckCount++;

            try (Connection connection = DatabaseService.getConnection()) {
                String updateQuery = "UPDATE clients SET all_check_passed = ? WHERE client_id = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, currentCheckCount);
                    updateStmt.setInt(2, clientId);
                    updateStmt.executeUpdate();
                }
            }
        }

        if (currentCheckCount == 3) {
            showAlert("Успех", "Клиент прошел все проверки!", Alert.AlertType.INFORMATION);
        }

        return currentCheckCount;
    }

    private static void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
