package org.project.aeroport.app.aeroport_tp.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static org.project.aeroport.app.aeroport_tp.Help.loadScreen;

public class AdminScreenController {

    @FXML
    private void onManageUsers() {
        loadScreen("/AdminManagesWorkers.fxml", "Управление работниками");
    }

    @FXML
    private void onManageSchedule() {
        loadScreen("/AdminManagesFlight.fxml", "Управление рейсами");
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
}
