package org.project.aeroport.app.aeroport_tp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Help {
    public static void loadScreen(String fxmlFile, String title) {
        try {
            Stage stage = (Stage) Stage.getWindows().filtered(window -> window.isShowing()).get(0);
            //Scene scene = new Scene(FXMLLoader.load(getClass().getResource(fxmlFile)));
            FXMLLoader loader = new FXMLLoader(Help.class.getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 1200, 800);
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
