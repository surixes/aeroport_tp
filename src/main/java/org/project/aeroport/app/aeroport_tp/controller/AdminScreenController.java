package org.project.aeroport.app.aeroport_tp.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class AdminScreenController {

    @FXML
    private void onManageUsers() {
        System.out.println("Открыть управление пользователями...");
        // Реализация управления пользователями
    }

    @FXML
    private void onManageSchedule() {
        System.out.println("Открыть управление расписанием...");
        // Реализация управления расписанием
    }

    @FXML
    private void onViewStats() {
        System.out.println("Открыть просмотр статистики...");
        // Реализация статистики
    }

    @FXML
    private void onBack() {
        loadScreen("/UserSelectionScreen.fxml", "Выбор роли");
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
