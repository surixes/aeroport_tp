package org.project.aeroport.app.aeroport_tp.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class UserScreenController {

    @FXML
    private void onSearchFlights() {
        System.out.println("Поиск рейсов...");
        // Реализация поиска рейсов
    }

    @FXML
    private void onBookTickets() {
        System.out.println("Бронирование билетов...");
        // Реализация бронирования билетов
    }

    @FXML
    private void onBack() {
        loadScreen("StartScreen.fxml", "Выбор роли");
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
