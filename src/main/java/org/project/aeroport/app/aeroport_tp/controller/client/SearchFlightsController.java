package org.project.aeroport.app.aeroport_tp.controller.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.project.aeroport.app.aeroport_tp.model.Flight;
import org.project.aeroport.app.aeroport_tp.service.DatabaseService;

import java.sql.*;

import static org.project.aeroport.app.aeroport_tp.Help.loadScreen;

public class SearchFlightsController {

    @FXML
    private TextField departureCityField;

    @FXML
    private TextField arrivalCityField;

    @FXML
    private TableView<Flight> flightsTable;

    @FXML
    private TableColumn<Flight, String> departureCityColumn;

    @FXML
    private TableColumn<Flight, String> arrivalCityColumn;

    @FXML
    private TableColumn<Flight, String> departureTimeColumn;

    @FXML
    private TableColumn<Flight, String> arrivalTimeColumn;

    @FXML
    private TableColumn<Flight, Double> priceColumn;

    private final ObservableList<Flight> flightsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        departureCityColumn.setCellValueFactory(new PropertyValueFactory<>("departureCity"));
        arrivalCityColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalCity"));
        departureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        loadFlights();
    }

    @FXML
    private void onSearch() {
        String departureCity = departureCityField.getText();
        String arrivalCity = arrivalCityField.getText();

        flightsList.clear();

        try (Connection connection = DatabaseService.getConnection()) {
            String query = "SELECT * FROM flights WHERE departure_city ILIKE ? AND arrival_city ILIKE ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, "%" + departureCity + "%");
                stmt.setString(2, "%" + arrivalCity + "%");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    flightsList.add(new Flight(
                            rs.getInt("flight_id"),
                            rs.getString("departure_city"),
                            rs.getString("arrival_city"),
                            rs.getTimestamp("departure_time"),
                            rs.getTimestamp("arrival_time"),
                            rs.getDouble("price")
                    ));
                }
                flightsTable.setItems(flightsList);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить рейсы.");
        }
    }

    @FXML
    private void onBack() {
        loadScreen("/ClientScreen.fxml", "Клиентский интерфейс");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadFlights() {
        ObservableList<Flight> flights = FXCollections.observableArrayList();
        String query = "SELECT * FROM flights WHERE active = true";

        try (Statement stmt = DatabaseService.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Flight flight = new Flight(rs.getInt("flight_id"),
                        rs.getString("departure_city"),
                        rs.getString("arrival_city"),
                        rs.getTimestamp("departure_time"),
                        rs.getTimestamp("arrival_time"),
                        rs.getDouble("price"));
                flights.add(flight);
            }
            flightsTable.setItems(flights);
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить рейсы из базы данных.");
        }
    }
}
