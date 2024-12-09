package org.project.aeroport.app.aeroport_tp.controller.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.project.aeroport.app.aeroport_tp.model.Flight;
import org.project.aeroport.app.aeroport_tp.service.DatabaseService;

import java.sql.*;

import static org.project.aeroport.app.aeroport_tp.Help.loadScreen;

public class BookTicketsController {

    @FXML
    private TableView<Flight> flightsTable;

    @FXML
    private TableColumn<Flight, String> departureCityColumn;

    @FXML
    private TableColumn<Flight, String> arrivalCityColumn;

    @FXML
    private TableColumn<Flight, Double> priceColumn;

    @FXML
    public void initialize() {
        departureCityColumn.setCellValueFactory(new PropertyValueFactory<>("departureCity"));
        arrivalCityColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalCity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        loadFlights();
    }

    private void loadFlights() {
        try (Connection connection = DatabaseService.getConnection()) {
            String query = "SELECT * FROM flights";
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    flightsTable.getItems().add(new Flight(
                            rs.getInt("flight_id"),
                            rs.getString("departure_city"),
                            rs.getString("arrival_city"),
                            rs.getTimestamp("departure_time"),
                            rs.getTimestamp("arrival_time"),
                            rs.getDouble("price")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось загрузить рейсы.");
        }
    }

    @FXML
    private void onBook() {
        Flight selectedFlight = flightsTable.getSelectionModel().getSelectedItem();
        if (selectedFlight != null) {
            Dialog<ClientData> dialog = createBookingDialog();
            dialog.showAndWait().ifPresent(clientData -> {
                try (Connection connection = DatabaseService.getConnection()) {
                    int clientId = ensureClientExists(connection, clientData);
                    String ticketQuery = "SELECT ticket_id FROM tickets WHERE flight_id = ? AND is_sold = false LIMIT 1";
                    try (PreparedStatement pstmt = connection.prepareStatement(ticketQuery)) {
                        pstmt.setInt(1, selectedFlight.getFlightId());
                        ResultSet rs = pstmt.executeQuery();

                        if (rs.next()) {
                            int ticketId = rs.getInt("ticket_id");

                            String updateTicketQuery = "UPDATE tickets SET is_sold = true, client_id = ? WHERE ticket_id = ?";
                            try (PreparedStatement updatePstmt = connection.prepareStatement(updateTicketQuery)) {
                                updatePstmt.setInt(1, clientId);
                                updatePstmt.setInt(2, ticketId);
                                updatePstmt.executeUpdate();
                                showAlert("Успех", "Билет успешно забронирован!");
                            }
                        } else {
                            showAlert("Ошибка", "Нет доступных билетов на выбранный рейс.");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Ошибка", "Не удалось выполнить бронирование.");
                }
            });
        } else {
            showAlert("Ошибка", "Выберите рейс для бронирования.");
        }
    }

    private Dialog<ClientData> createBookingDialog() {
        Dialog<ClientData> dialog = new Dialog<>();
        dialog.setTitle("Данные клиента");

        Label nameLabel = new Label("Полное имя:");
        TextField nameField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        Label passportLabel = new Label("Паспортные данные:");
        TextField passportField = new TextField();

        VBox vbox = new VBox(nameLabel, nameField, emailLabel, emailField, passportLabel, passportField);
        dialog.getDialogPane().setContent(vbox);

        ButtonType okButtonType = new ButtonType("ОК", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == okButtonType) {
                return new ClientData(nameField.getText(), emailField.getText(), passportField.getText());
            }
            return null;
        });

        return dialog;
    }

    private int ensureClientExists(Connection connection, ClientData clientData) throws SQLException {
        String selectClientQuery = "SELECT client_id FROM clients WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectClientQuery)) {
            pstmt.setString(1, clientData.email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("client_id");
            }
        }

        String insertClientQuery = "INSERT INTO clients (full_name, email, passport_data) VALUES (?, ?, ?) RETURNING client_id";
        try (PreparedStatement pstmt = connection.prepareStatement(insertClientQuery)) {
            pstmt.setString(1, clientData.fullName);
            pstmt.setString(2, clientData.email);
            pstmt.setString(3, clientData.passportData);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("client_id");
            }
        }

        throw new SQLException("Не удалось создать или найти клиента.");
    }

    @FXML
    private void onBack() {
        loadScreen("/ClientScreen.fxml", "Клиентский интерфейс");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class ClientData {
        String fullName;
        String email;
        String passportData;

        ClientData(String fullName, String email, String passportData) {
            this.fullName = fullName;
            this.email = email;
            this.passportData = passportData;
        }
    }
}
