package org.project.aeroport.app.aeroport_tp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class UserSelectionController {

    public void onClientSelected(ActionEvent event) {
        openNextScreen(event, "/ClientScreen.fxml");
    }

    public void onAdminSelected(ActionEvent event) {
        openNextScreen(event, "/AuthenticationAdmin.fxml");
    }

    public void onEmployeeSelected(ActionEvent event) {
        openNextScreen(event, "/AuthenticationWorker.fxml");
    }

    private void openNextScreen(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Scene scene = new Scene(loader.load(), 1200, 800);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
