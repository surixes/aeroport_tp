package org.project.aeroport.app.aeroport_tp.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.project.aeroport.app.aeroport_tp.model.Ticket;
import org.project.aeroport.app.aeroport_tp.service.DatabaseService;

import java.sql.*;
import java.util.Optional;

import static org.project.aeroport.app.aeroport_tp.Help.loadScreen;

public class AdminManagesTicketsController {
    @FXML
    private TableView<Ticket> ticketTable;

    @FXML
    private TableColumn<Ticket, Integer> ticketIdColumn;

    @FXML
    private TableColumn<Ticket, String> clientNameColumn;

    @FXML
    private TableColumn<Ticket, String> clientPassportDataColumn;

    @FXML
    private TableColumn<Ticket, String> seatNumberColumn;

    @FXML
    private TableColumn<Ticket, String> flightInfoColumn;

    @FXML
    private TableColumn<Ticket, String> statusColumn;

    @FXML
    public void initialize() {
        ticketIdColumn.setCellValueFactory(new PropertyValueFactory<>("ticketId"));
        clientNameColumn.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        clientPassportDataColumn.setCellValueFactory(new PropertyValueFactory<>("clientPassportData"));
        seatNumberColumn.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        flightInfoColumn.setCellValueFactory(new PropertyValueFactory<>("flightInfo"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadTickets();
    }

    private boolean showTicketDialog(Ticket ticket) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(ticket == null ? "Добавить билет" : "Редактировать билет");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        TextField clientNameField = new TextField(ticket != null ? ticket.getClientName() : "");
        TextField clientPassportDataField = new TextField(ticket != null ? ticket.getClientPassportData() : "");
        TextField seatNumberField = new TextField(ticket != null ? String.valueOf(ticket.getSeatNumber()) : "");

        ComboBox<String> flightComboBox = new ComboBox<>();
        flightComboBox.setItems(getAvailableFlights());
        if (ticket != null) {
            flightComboBox.setValue(ticket.getFlightInfo());
        }

        CheckBox isSoldCheckBox = new CheckBox("Продан");
        isSoldCheckBox.setSelected(ticket != null && "Продан".equals(ticket.getStatus()));

        gridPane.add(new Label("ФИО клиента:"), 0, 0);
        gridPane.add(clientNameField, 1, 0);
        gridPane.add(new Label("Паспортные данные клиента:"), 0, 1);
        gridPane.add(clientPassportDataField, 1, 1);
        gridPane.add(new Label("Номер места:"), 0, 2);
        gridPane.add(seatNumberField, 1, 2);
        gridPane.add(new Label("Рейс:"), 0, 3);
        gridPane.add(flightComboBox, 1, 3);
        gridPane.add(new Label("Статус:"), 0, 4);
        gridPane.add(isSoldCheckBox, 1, 4);

        dialog.getDialogPane().setContent(gridPane);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (ticket != null) {
                ticket.setClientName(clientNameField.getText());
                ticket.setClientPassportData(clientPassportDataField.getText());
                ticket.setSeatNumber(Integer.parseInt(seatNumberField.getText()));
                ticket.setFlightInfo(flightComboBox.getValue());
                ticket.setStatus(isSoldCheckBox.isSelected() ? "Продан" : "Свободен");
            }
            return true;
        }
        return false;
    }

    private ObservableList<String> getAvailableFlights() {
        ObservableList<String> flights = FXCollections.observableArrayList();
        String query = "SELECT CONCAT(departure_city, ' -> ', arrival_city) AS flight_info FROM flights";

        try (Connection connection = DatabaseService.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                flights.add(rs.getString("flight_info"));
            }
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить список рейсов.");
        }

        return flights;
    }

    private void loadTickets() {
        ObservableList<Ticket> tickets = FXCollections.observableArrayList();
        String query = """
        SELECT t.ticket_id, c.full_name AS client_name, c.passport_data, t.seat_number, 
               CONCAT(f.departure_city, ' -> ', f.arrival_city) AS flight_info,
               CASE WHEN t.is_sold THEN 'Продан' ELSE 'Свободен' END AS status
        FROM tickets t
        JOIN clients c ON t.client_id = c.client_id
        JOIN flights f ON t.flight_id = f.flight_id
        """;

        try (Connection connection = DatabaseService.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Ticket ticket = new Ticket(
                        rs.getInt("ticket_id"),
                        rs.getString("client_name"),
                        rs.getString("passport_data"),
                        rs.getInt("seat_number"),
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
        Ticket newTicket = new Ticket(0, "", "", 0, "", "Свободен");
        boolean confirmed = showTicketDialog(newTicket);
        if (confirmed) {
            String clientName = newTicket.getClientName();
            String clientPassportData = newTicket.getClientPassportData();
            int seatNumber = newTicket.getSeatNumber();
            String flightInfo = newTicket.getFlightInfo();
            boolean isSold = "Продан".equals(newTicket.getStatus());

            if (clientName.isEmpty() || clientPassportData.isEmpty() || flightInfo.isEmpty()) {
                showAlert("Ошибка", "Пожалуйста, заполните все поля.");
                return;
            }

            String checkClientQuery = "SELECT client_id FROM clients WHERE full_name = ? AND passport_data = ?";
            String insertTicketQuery = "INSERT INTO tickets (client_id, seat_number, flight_id, is_sold) " +
                    "VALUES ((SELECT client_id FROM clients WHERE full_name = ? AND passport_data = ?), ?, " +
                    "(SELECT flight_id FROM flights WHERE CONCAT(departure_city, ' -> ', arrival_city) = ?), ?)";

            try (Connection connection = DatabaseService.getConnection()) {
                int clientId = -1;
                try (PreparedStatement checkStmt = connection.prepareStatement(checkClientQuery)) {
                    checkStmt.setString(1, clientName);
                    checkStmt.setString(2, clientPassportData);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        clientId = rs.getInt("client_id");
                    }
                }

                if (clientId != -1) {
                    try (PreparedStatement stmt = connection.prepareStatement(insertTicketQuery)) {
                        stmt.setString(1, clientName);
                        stmt.setString(2, clientPassportData);
                        stmt.setInt(3, seatNumber);
                        stmt.setString(4, flightInfo);
                        stmt.setBoolean(5, isSold);
                        stmt.executeUpdate();
                        showAlert("Успешно", "Билет добавлен.");
                        loadTickets();
                    }
                } else {
                    showAlert("Ошибка", "Не удалось определить ID клиента.");
                }
            } catch (SQLException e) {
                showAlert("Ошибка", "Не удалось добавить билет.");
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void handleEditTicket() {
        Ticket selectedTicket = ticketTable.getSelectionModel().getSelectedItem();
        if (selectedTicket == null) {
            showAlert("Ошибка", "Пожалуйста, выберите билет для редактирования.");
            return;
        }

        boolean confirmed = showTicketDialog(selectedTicket);
        if (confirmed) {
            String clientName = selectedTicket.getClientName();
            String clientPassportData = selectedTicket.getClientPassportData();
            int seatNumber = selectedTicket.getSeatNumber();
            String flightInfo = selectedTicket.getFlightInfo();
            boolean isSold = "Продан".equals(selectedTicket.getStatus());

            String updateQuery = "UPDATE tickets SET client_id = (SELECT client_id FROM clients WHERE full_name = ? AND passport_data = ?), " +
                    "seat_number = ?, flight_id = (SELECT flight_id FROM flights WHERE CONCAT(departure_city, ' -> ', arrival_city) = ?), " +
                    "is_sold = ? WHERE ticket_id = ?";

            try (Connection connection = DatabaseService.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
                stmt.setString(1, clientName);
                stmt.setString(2, clientPassportData);
                stmt.setInt(3, seatNumber);
                stmt.setString(4, flightInfo);
                stmt.setBoolean(5, isSold);
                stmt.setInt(6, selectedTicket.getTicketId());
                stmt.executeUpdate();
                showAlert("Успешно", "Билет обновлён.");
                loadTickets();
            } catch (SQLException e) {
                showAlert("Ошибка", "Не удалось обновить билет.");
            }
        }
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
                showAlert("Успешно", "Билет удалён.");
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
