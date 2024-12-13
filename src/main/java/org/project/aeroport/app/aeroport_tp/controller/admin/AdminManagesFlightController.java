package org.project.aeroport.app.aeroport_tp.controller.admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.project.aeroport.app.aeroport_tp.LocalTimeSpinnerValueFactory;
import org.project.aeroport.app.aeroport_tp.model.Flight;
import org.project.aeroport.app.aeroport_tp.service.DatabaseService;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.project.aeroport.app.aeroport_tp.Help.loadScreen;
import static org.project.aeroport.app.aeroport_tp.LocalTimeSpinnerValueFactory.configureTimeSpinner;

public class AdminManagesFlightController {
    @FXML
    private TableView<Flight> flightTable;

    @FXML
    private TableColumn<Flight, Integer> flightIdColumn;

    @FXML
    private TableColumn<Flight, String> departureCityColumn;

    @FXML
    private TableColumn<Flight, String> arrivalCityColumn;

    @FXML
    private TableColumn<Flight, Timestamp> departureTimeColumn;

    @FXML
    private TableColumn<Flight, Timestamp> arrivalTimeColumn;

    @FXML
    private TableColumn<Flight, Double> priceColumn;

    public void initialize() {
        flightIdColumn.setCellValueFactory(new PropertyValueFactory<>("flightId"));
        departureCityColumn.setCellValueFactory(new PropertyValueFactory<>("departureCity"));
        arrivalCityColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalCity"));
        departureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("departureTime"));
        arrivalTimeColumn.setCellValueFactory(new PropertyValueFactory<>("arrivalTime"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        loadFlights();
    }

    @FXML
    private void handleAddFlight() {
        try {
            TextInputDialog departureCityDialog = new TextInputDialog();
            departureCityDialog.setTitle("Добавление рейса");
            departureCityDialog.setHeaderText("Добавить новый рейс");
            departureCityDialog.setContentText("Город отправления:");
            Optional<String> departureCityInput = departureCityDialog.showAndWait();

            if (departureCityInput.isEmpty() || departureCityInput.get().trim().isEmpty()) {
                showAlert("Ошибка", "Город отправления не может быть пустым!");
                return;
            }

            TextInputDialog arrivalCityDialog = new TextInputDialog();
            arrivalCityDialog.setContentText("Город прибытия:");
            Optional<String> arrivalCityInput = arrivalCityDialog.showAndWait();

            if (arrivalCityInput.isEmpty() || arrivalCityInput.get().trim().isEmpty()) {
                showAlert("Ошибка", "Город прибытия не может быть пустым!");
                return;
            }

            TextInputDialog departureTimeDialog = new TextInputDialog();
            departureTimeDialog.setContentText("Время отправления (формат: ГГГГ-ММ-ДД ЧЧ:ММ):");
            Optional<String> departureTimeInput = departureTimeDialog.showAndWait();

            if (departureTimeInput.isEmpty() || departureTimeInput.get().trim().isEmpty()) {
                showAlert("Ошибка", "Время отправления не может быть пустым!");
                return;
            }

            String departureTimeWithSeconds = departureTimeInput.get() + ":00";
            if (!departureTimeWithSeconds.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                showAlert("Ошибка", "Неверный формат времени! Используйте формат ГГГГ-ММ-ДД ЧЧ:ММ.");
                return;
            }

            TextInputDialog arrivalTimeDialog = new TextInputDialog();
            arrivalTimeDialog.setContentText("Время прибытия (формат: ГГГГ-ММ-ДД ЧЧ:ММ):");
            Optional<String> arrivalTimeInput = arrivalTimeDialog.showAndWait();

            if (arrivalTimeInput.isEmpty() || arrivalTimeInput.get().trim().isEmpty()) {
                showAlert("Ошибка", "Время прибытия не может быть пустым!");
                return;
            }

            String arrivalTimeWithSeconds = arrivalTimeInput.get() + ":00";
            if (!arrivalTimeWithSeconds.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                showAlert("Ошибка", "Неверный формат времени! Используйте формат ГГГГ-ММ-ДД ЧЧ:ММ.");
                return;
            }

            TextInputDialog priceDialog = new TextInputDialog("0.00");
            priceDialog.setContentText("Стоимость билета:");
            Optional<String> priceInput = priceDialog.showAndWait();

            if (priceInput.isEmpty() || priceInput.get().trim().isEmpty()) {
                showAlert("Ошибка", "Стоимость билета не может быть пустой!");
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceInput.get());
            } catch (NumberFormatException e) {
                showAlert("Ошибка", "Стоимость должна быть числом!");
                return;
            }

            try (Connection connection = DatabaseService.getConnection()) {
                String flightQuery = "INSERT INTO flights (departure_city, arrival_city, departure_time, arrival_time, price) VALUES (?, ?, ?, ?, ?) RETURNING flight_id;";
                int flightId;

                try (PreparedStatement stmt = connection.prepareStatement(flightQuery)) {
                    stmt.setString(1, departureCityInput.get());
                    stmt.setString(2, arrivalCityInput.get());
                    stmt.setTimestamp(3, Timestamp.valueOf(departureTimeWithSeconds));
                    stmt.setTimestamp(4, Timestamp.valueOf(arrivalTimeWithSeconds));
                    stmt.setDouble(5, price);

                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        flightId = rs.getInt("flight_id");
                    } else {
                        throw new SQLException("Не удалось создать рейс.");
                    }
                }

                String ticketQuery = "INSERT INTO tickets (flight_id, seat_number) VALUES (?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(ticketQuery)) {
                    for (int seatNumber = 1; seatNumber <= 250; seatNumber++) {
                        stmt.setInt(1, flightId);
                        stmt.setInt(2, seatNumber);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }

                showAlert("Успешно", "Рейс и билеты успешно добавлены!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Произошла ошибка при добавлении рейса!");
        } catch (IllegalArgumentException e) {
            showAlert("Ошибка", "Неверный формат времени! Используйте формат ГГГГ-ММ-ДД ЧЧ:ММ.");
        }
    }

    @FXML
    private void handleEditFlight() {
        Flight selectedFlight = flightTable.getSelectionModel().getSelectedItem();

        if (selectedFlight == null) {
            showAlert("Ошибка", "Пожалуйста, выберите рейс для редактирования.");
            return;
        }

        Dialog<Flight> dialog = new Dialog<>();
        dialog.setTitle("Редактирование рейса");
        dialog.setHeaderText("Изменить данные рейса");

        ButtonType updateButtonType = new ButtonType("Обновить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField departureCityField = new TextField(selectedFlight.getDepartureCity());
        departureCityField.setPromptText("Город отправления");
        TextField arrivalCityField = new TextField(selectedFlight.getArrivalCity());
        arrivalCityField.setPromptText("Город прибытия");

        DatePicker departureDatePicker = new DatePicker(selectedFlight.getDepartureTime().toLocalDateTime().toLocalDate());
        DatePicker arrivalDatePicker = new DatePicker(selectedFlight.getArrivalTime().toLocalDateTime().toLocalDate());

        Spinner<LocalTime> departureTimeSpinner = new Spinner<>();
        departureTimeSpinner.setValueFactory(new LocalTimeSpinnerValueFactory(LocalTime.MIN, LocalTime.MAX, 1));
        configureTimeSpinner(departureTimeSpinner);
        departureTimeSpinner.getValueFactory().setValue(selectedFlight.getDepartureTime().toLocalDateTime().toLocalTime());

        Spinner<LocalTime> arrivalTimeSpinner = new Spinner<>();
        arrivalTimeSpinner.setValueFactory(new LocalTimeSpinnerValueFactory(LocalTime.MIN, LocalTime.MAX, 1));
        configureTimeSpinner(arrivalTimeSpinner);
        arrivalTimeSpinner.getValueFactory().setValue(selectedFlight.getArrivalTime().toLocalDateTime().toLocalTime());



        TextField priceField = new TextField(String.valueOf(selectedFlight.getPrice()));
        priceField.setPromptText("Цена");

        grid.add(new Label("Город отправления:"), 0, 0);
        grid.add(departureCityField, 1, 0);
        grid.add(new Label("Город прибытия:"), 0, 1);
        grid.add(arrivalCityField, 1, 1);
        grid.add(new Label("Дата отправления:"), 0, 2);
        grid.add(departureDatePicker, 1, 2);
        grid.add(new Label("Время отправления:"), 0, 3);
        grid.add(departureTimeSpinner, 1, 3);
        grid.add(new Label("Дата прибытия:"), 0, 4);
        grid.add(arrivalDatePicker, 1, 4);
        grid.add(new Label("Время прибытия:"), 0, 5);
        grid.add(arrivalTimeSpinner, 1, 5);
        grid.add(new Label("Цена:"), 0, 6);
        grid.add(priceField, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                String departureCity = departureCityField.getText();
                String arrivalCity = arrivalCityField.getText();
                LocalDate departureDate = departureDatePicker.getValue();
                LocalTime departureTime = departureTimeSpinner.getValue();
                LocalDate arrivalDate = arrivalDatePicker.getValue();
                LocalTime arrivalTime = arrivalTimeSpinner.getValue();
                double price = Double.parseDouble(priceField.getText());

                return new Flight(
                        selectedFlight.getFlightId(),
                        departureCity,
                        arrivalCity,
                        Timestamp.valueOf(departureDate.atTime(departureTime)),
                        Timestamp.valueOf(arrivalDate.atTime(arrivalTime)),
                        price
                );
            }
            return null;
        });

        Optional<Flight> result = dialog.showAndWait();

        result.ifPresent(updatedFlight -> {
            String query = "UPDATE flights SET departure_city = ?, arrival_city = ?, departure_time = ?, arrival_time = ?, price = ? WHERE flight_id = ?";

            try (PreparedStatement stmt = DatabaseService.getConnection().prepareStatement(query)) {
                stmt.setString(1, updatedFlight.getDepartureCity());
                stmt.setString(2, updatedFlight.getArrivalCity());
                stmt.setTimestamp(3, updatedFlight.getDepartureTime());
                stmt.setTimestamp(4, updatedFlight.getArrivalTime());
                stmt.setDouble(5, updatedFlight.getPrice());
                stmt.setInt(6, updatedFlight.getFlightId());

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Успех", "Рейс обновлен успешно.");
                    loadFlights();
                }
            } catch (SQLException e) {
                showAlert("Ошибка", "Не удалось обновить рейс в базе данных.");
            }
        });
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
            flightTable.setItems(flights);
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить рейсы из базы данных.");
        }
    }


    @FXML
    private void handleDeleteFlight() {
        Flight selectedFlight = flightTable.getSelectionModel().getSelectedItem();

        if (selectedFlight == null) {
            showAlert("Ошибка", "Пожалуйста, выберите рейс для деактивации.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Подтверждение");
        confirmationAlert.setHeaderText("Деактивация рейса");
        confirmationAlert.setContentText("Вы уверены, что хотите деактивировать этот рейс?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection connection = DatabaseService.getConnection()) {
                String deactivateQuery = "UPDATE flights SET active = false WHERE flight_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(deactivateQuery)) {
                    stmt.setInt(1, selectedFlight.getFlightId());
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        showAlert("Успешно", "Рейс был деактивирован.");
                        loadFlights();
                    } else {
                        showAlert("Ошибка", "Не удалось деактивировать рейс.");
                    }
                }
            } catch (SQLException e) {
                showAlert("Ошибка", "Произошла ошибка при деактивации рейса.");
            }
        }
    }

    @FXML
    private void handleUpdate() {
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
            flightTable.setItems(flights);
        } catch (SQLException e) {
            showAlert("Ошибка", "Не удалось загрузить рейсы из базы данных.");
        }
    }

    @FXML
    private void handleBack() {
        loadScreen("/AdminScreen.fxml", "Выбор действия");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
