package org.project.aeroport.app.aeroport_tp.controller.admin;

import javafx.fxml.FXML;

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
    private void onManageTickers() {
        loadScreen("/AdminManagesTickets.fxml", "Управление билетами");
    }

    @FXML
    private void onBack() {
        loadScreen("/UserSelectionScreen.fxml", "Выбор роли");
    }
}
