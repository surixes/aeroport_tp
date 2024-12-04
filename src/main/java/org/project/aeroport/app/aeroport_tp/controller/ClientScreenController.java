package org.project.aeroport.app.aeroport_tp.controller;

import javafx.fxml.FXML;

import static org.project.aeroport.app.aeroport_tp.Help.loadScreen;

public class ClientScreenController {

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
        loadScreen("/UserSelectionScreen.fxml", "Выбор роли");
    }
}
