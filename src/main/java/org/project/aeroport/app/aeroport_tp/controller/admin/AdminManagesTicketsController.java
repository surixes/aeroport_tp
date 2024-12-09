package org.project.aeroport.app.aeroport_tp.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.project.aeroport.app.aeroport_tp.model.Ticket;
import org.project.aeroport.app.aeroport_tp.service.DatabaseService;

import java.sql.*;

import static org.project.aeroport.app.aeroport_tp.Help.loadScreen;

public class AdminManagesTicketsController {
    @FXML
    private TableView<Ticket> ticketTable;

    @FXML
    private TableColumn<Ticket, Integer> ticketIdColumn;

    @FXML
    private TableColumn<Ticket, String> clientNameColumn;

    @FXML
    private TableColumn<Ticket, String> seatNumberColumn;

    @FXML
    private TableColumn<Ticket, String> flightInfoColumn;

    @FXML
    private TableColumn<Ticket, String> statusColumn;

    public void initialize() {
        ticketIdColumn.setCellValueFactory(new PropertyValueFactory<>("ticketId"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        seatNumberColumn.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        flightInfoColumn.setCellValueFactory(new PropertyValueFactory<>("flightInfo"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadTickets();
    }

    private void loadTickets() {
        ObservableList<Ticket> tickets = FXCollections.observableArrayList();
        String query = "SELECT t.ticket_id, c.full_name AS client_name, t.seat_number, " +
                "CONCAT(f.departure_city, ' -> ', f.arrival_city) AS flight_info, t.is_sold " +
                "FROM tickets t " +
                "JOIN clients c ON t.client_id = c.client_id " +
                "JOIN flights f ON t.flight_id = f.flight_id";

        try (Connection connection = DatabaseService.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Ticket ticket = new Ticket(
                        rs.getInt("ticket_id"),
                        rs.getString("client_name"),
                        rs.getString("seat_number"),
                        rs.getString("flight_info"),
                        rs.getString("status")
                );
                tickets.add(ticket);
            }

            ticketTable.setItems(tickets);

        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить билеты из базы данных.");
        }
    }

    @FXML
    private void handleAddTicket() {
        // Logic for adding a ticket
    }

    @FXML
    private void handleEditTicket() {
        // Logic for editing a ticket
    }

    @FXML
    private void handleDeleteTicket() {
        Ticket selectedTicket = ticketTable.getSelectionModel().getSelectedItem();

        if (selectedTicket == null) {
            showAlert("Ошибка", "Пожалуйста, выберите билет для удаления.");
            return;
        }

        try (Connection connection = DatabaseService.getConnection()) {
            String deleteQuery = "DELETE FROM tickets WHERE ticket_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteQuery)) {
                stmt.setInt(1, selectedTicket.getTicketId());
                stmt.executeUpdate();
                showAlert("Успешно", "Билет удален.");
                loadTickets();
            }
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось удалить билет.");
        }
    }

    @FXML
    private void handleBackToAdmin() {
        loadScreen("/AdminScreen.fxml", "Администратор");
    }

    @FXML
    private void handleUpdate() {
        loadTickets();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
