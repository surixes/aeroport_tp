package org.project.aeroport.app.aeroport_tp.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseService {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";  // Укажите ваш URL базы данных
    private static final String USER = "postgres"; // Укажите имя пользователя
    private static final String PASSWORD = "postgres"; // Укажите пароль

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Нет JDBC-драйвера! Подключите JDBC-драйвер к проекту согласно инструкции.");
            throw new RuntimeException(e);
        }
        // Устанавливаем соединение
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        return conn;
    }
}
