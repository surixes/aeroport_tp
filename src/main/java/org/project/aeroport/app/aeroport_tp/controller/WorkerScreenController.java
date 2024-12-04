package org.project.aeroport.app.aeroport_tp.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class WorkerScreenController {

    @FXML
    private void onRegisterFlights() {
        System.out.println("Регистрация рейсов...");
        // Реализация регистрации рейсов
    }

    @FXML
    private void onWorkWithSchedule() {
        System.out.println("Работа с расписанием...");
        // Реализация работы с расписанием
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
