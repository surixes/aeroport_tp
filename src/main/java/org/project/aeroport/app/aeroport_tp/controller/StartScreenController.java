package org.project.aeroport.app.aeroport_tp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartScreenController {

    public void onStartButtonClick(ActionEvent event) {
        try {
            // Открываем окно выбора пользователя
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserSelectionScreen.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);

            // Получаем текущее окно и задаем новую сцену
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
