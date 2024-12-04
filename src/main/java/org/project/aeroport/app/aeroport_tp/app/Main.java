package org.project.aeroport.app.aeroport_tp.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Загрузка стартового экрана
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StartScreen.fxml"));
            Scene scene = new Scene(loader.load(), 1200, 800);

            // Настройка окна
            primaryStage.setTitle("Airport Management System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true); // Запрет изменения размера окна
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args); // Запуск приложения JavaFX
    }
}
