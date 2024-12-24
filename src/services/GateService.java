package services;

import config.DatabaseConnection;
import models.Gate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GateService {

    // 1. Lấy danh sách tất cả các cổng từ database
    public List<Gate> getAllGates() {
        List<Gate> gates = new ArrayList<>();
        String query = "SELECT gate_number, is_available FROM gate";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int gateNumber = resultSet.getInt("gate_number");
                boolean isAvailable = resultSet.getBoolean("is_available");
                gates.add(new Gate(gateNumber, isAvailable));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return gates;
    }

    // 2. Gán chuyến bay cho một cổng (đặt is_available = 0)
    public boolean assignFlight(int gateNumber) {
        String checkQuery = "SELECT is_available FROM gate WHERE gate_number = ?";
        String updateQuery = "UPDATE gate SET is_available = 0 WHERE gate_number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {

            
            checkStmt.setInt(1, gateNumber);
            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next()) {
                boolean isAvailable = resultSet.getBoolean("is_available");

                if (!isAvailable) {
                    System.out.println("Gate " + gateNumber + " is not available for assignment.");
                    return false;
                }
            } else {
                System.out.println("Gate " + gateNumber + " does not exist.");
                return false;
            }

          
            updateStmt.setInt(1, gateNumber);
            updateStmt.executeUpdate();
            System.out.println("Gate " + gateNumber + " has been assigned successfully.");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 3. Giải phóng cổng (đặt is_available = 1)
    public boolean releaseGate(int gateNumber) {
        String checkQuery = "SELECT is_available FROM gate WHERE gate_number = ?";
        String updateQuery = "UPDATE gate SET is_available = 1 WHERE gate_number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
             PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {

            checkStmt.setInt(1, gateNumber);
            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next()) {
                boolean isAvailable = resultSet.getBoolean("is_available");

                if (isAvailable) {
                    System.out.println("Gate " + gateNumber + " is already available.");
                    return false;
                }
            } else {
                System.out.println("Gate " + gateNumber + " does not exist.");
                return false;
            }


            updateStmt.setInt(1, gateNumber);
            updateStmt.executeUpdate();
            System.out.println("Gate " + gateNumber + " has been released successfully.");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean setAvailable(int gateNumber, boolean isAvailable) {
        String query = "UPDATE gate SET is_available = ? WHERE gate_number = ?";
    
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
    
            statement.setBoolean(1, isAvailable); 
            statement.setInt(2, gateNumber); 
    
            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Gate " + gateNumber + " updated successfully.");
                return true;
            } else {
                System.out.println("Gate " + gateNumber + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return false;
    }
}
