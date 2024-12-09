package org.project.aeroport.app.aeroport_tp.controller.client;

import javafx.fxml.FXML;

import static org.project.aeroport.app.aeroport_tp.Help.loadScreen;

public class ClientScreenController {

    @FXML
    private void onSearchFlights() {
        loadScreen("/SearchFlightsScreen.fxml", "Поиск рейсов");
    }

    @FXML
    private void onBookTickets() {
        loadScreen("/BookTicketsScreen.fxml", "Бронирование билетов");
    }

    @FXML
    private void onViewMyTickets() {
        loadScreen("/SearchTickets.fxml", "Мои билеты");
    }

    @FXML
    private void onBack() {
        loadScreen("/UserSelectionScreen.fxml", "Выбор роли");
    }
}
