package org.project.aeroport.app.aeroport_tp.controller;

import javafx.fxml.FXML;

import static org.project.aeroport.app.aeroport_tp.Help.loadScreen;

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
}
