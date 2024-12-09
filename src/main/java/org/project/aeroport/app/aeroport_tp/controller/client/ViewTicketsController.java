package org.project.aeroport.app.aeroport_tp.controller.client;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.project.aeroport.app.aeroport_tp.service.DatabaseService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.project.aeroport.app.aeroport_tp.Help.loadScreen;

public class ViewTicketsController {

    @FXML
    private TextField passportField;

    @FXML
    private TextArea ticketsArea;

    @FXML
    private void handleBack() {
        loadScreen("/ClientScreen.fxml", "Клиентский интерфейс");
    }

    @FXML
    private void onViewTickets() {
        String passportData = passportField.getText();

        if (passportData.isEmpty()) {
            showAlert("Ошибка", "Введите паспортные данные.");
            return;
        }

        try (Connection connection = DatabaseService.getConnection()) {
            String clientQuery = "SELECT client_id FROM clients WHERE passport_data = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(clientQuery)) {
                pstmt.setString(1, passportData);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    int clientId = rs.getInt("client_id");

                    List<String> activeTickets = getClientTickets(connection, clientId, true);
                    List<String> deactivatedTickets = getClientTickets(connection, clientId, false);

                    StringBuilder message = new StringBuilder();

                    if (!activeTickets.isEmpty()) {
                        message.append("Активные билеты:\n\n");
                        for (String ticket : activeTickets) {
                            message.append(ticket).append("\n");
                        }
                    } else {
                        message.append("У вас нет активных билетов.\n\n");
                    }

                    if (!deactivatedTickets.isEmpty()) {
                        message.append("Завершенные билеты:\n\n");
                        for (String ticket : deactivatedTickets) {
                            message.append(ticket).append("\n");
                        }
                    } else {
                        message.append("У вас нет завершенных билетов.");
                    }

                    ticketsArea.setText(message.toString());
                } else {
                    showAlert("Ошибка", "Клиент с такими паспортными данными не найден.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось выполнить поиск билетов.");
        }
    }

    private List<String> getClientTickets(Connection connection, int clientId, boolean active) throws SQLException {
        String ticketQuery = "SELECT t.ticket_id, t.seat_number, f.departure_city, f.arrival_city, f.departure_time, f.arrival_time " +
                "FROM tickets t " +
                "JOIN flights f ON t.flight_id = f.flight_id " +
                "WHERE t.client_id = ? AND f.active = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(ticketQuery)) {
            pstmt.setInt(1, clientId);
            pstmt.setBoolean(2, active);
            ResultSet rs = pstmt.executeQuery();

            List<String> tickets = new ArrayList<>();
            while (rs.next()) {
                String ticketInfo = String.format("Билет #%d, Место: %d, Рейс: %s -> %s, Вылет: %s, Прилет: %s",
                        rs.getInt("ticket_id"),
                        rs.getInt("seat_number"),
                        rs.getString("departure_city"),
                        rs.getString("arrival_city"),
                        rs.getTimestamp("departure_time"),
                        rs.getTimestamp("arrival_time"));
                tickets.add(ticketInfo);
            }

            return tickets;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

