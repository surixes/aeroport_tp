package org.project.aeroport.app.aeroport_tp.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginService {
    public static boolean isAdmin(String username, String password) {
        String AdminQuery = "SELECT username, password FROM administrators WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AdminQuery)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isWorker(String username, String password) {
        String WorkerQuery = "SELECT username, password FROM worker WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(WorkerQuery)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static String getWorkerRole(String username, String password) {
        String query = "SELECT position FROM worker WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("position");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
