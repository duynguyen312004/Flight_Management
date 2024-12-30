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
                String gateNumber = resultSet.getString("gate_number");
                boolean isAvailable = resultSet.getBoolean("is_available");
                gates.add(new Gate(gateNumber, isAvailable));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all gates.");
            e.printStackTrace();
        }

        return gates;
    }

    // 2. Lấy cổng liên quan đến một chuyến bay
    public Gate getGateByFlight(String flightNumber) {
        String query = "SELECT g.gate_number, g.is_available " +
                "FROM gate g " +
                "JOIN flight f ON g.gate_number = f.assigned_gate " +
                "WHERE f.flight_number = ?";
        Gate gate = null;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, flightNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String gateNumber = resultSet.getString("gate_number");
                boolean isAvailable = resultSet.getBoolean("is_available");
                gate = new Gate(gateNumber, isAvailable);
            } else {
                System.out.println("No gate found for flight: " + flightNumber);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching gate for flight: " + flightNumber);
            e.printStackTrace();
        }

        return gate;
    }

    // 3. Gán chuyến bay cho một cổng (đặt is_available = 0)
    public boolean assignGate(String gateNumber) {
        String query = "UPDATE gate SET is_available = 0 WHERE gate_number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, gateNumber);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Gate " + gateNumber + " assigned successfully.");
                return true;
            } else {
                System.out.println("Gate " + gateNumber + " not found or already assigned.");
            }
        } catch (SQLException e) {
            System.err.println("Error assigning flight to gate: " + gateNumber);
            e.printStackTrace();
        }

        return false;
    }

    // 4. Giải phóng cổng (đặt is_available = 1)
    public boolean releaseGate(String gateNumber) {
        String query = "UPDATE gate SET is_available = 1 WHERE gate_number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, gateNumber);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Gate " + gateNumber + " released successfully.");
                return true;
            } else {
                System.out.println("Gate " + gateNumber + " not found or already available.");
            }
        } catch (SQLException e) {
            System.err.println("Error releasing gate: " + gateNumber);
            e.printStackTrace();
        }

        return false;
    }

    // 5. Cập nhật trạng thái cổng
    public boolean updateGateAvailability(String gateNumber, boolean isAvailable) {
        String query = "UPDATE gate SET is_available = ? WHERE gate_number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setBoolean(1, isAvailable);
            statement.setString(2, gateNumber);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Gate " + gateNumber + " availability updated successfully.");
                return true;
            } else {
                System.out.println("Gate " + gateNumber + " not found.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating gate availability: " + gateNumber);
            e.printStackTrace();
        }

        return false;
    }

    // Thêm Gate
    public boolean addGate(Gate gate) {
        String query = "INSERT INTO gate (gate_number, is_available) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, gate.getGateNumber());
            statement.setBoolean(2, gate.isAvailable());
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error adding gate: " + e.getMessage());
            return false;
        }
    }

    // Xóa Gate
    public boolean deleteGate(String gateNumber) {
        String query = "DELETE FROM gate WHERE gate_number = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, gateNumber);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting gate: " + e.getMessage());
            return false;
        }
    }

    public String getFlightUsingGate(String gateNumber) {
        String query = "SELECT flight_number FROM flight WHERE assigned_gate = ? AND status != 'Cancelled'";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, gateNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("flight_number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "None"; // Không có chuyến bay nào đang sử dụng
    }

    public List<Gate> getAvailableGates() {
        List<Gate> gates = new ArrayList<>();
        String query = "SELECT gate_number, is_available FROM gate WHERE is_available = 1";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String gateNumber = resultSet.getString("gate_number");
                boolean isAvailable = resultSet.getBoolean("is_available");

                gates.add(new Gate(gateNumber, isAvailable));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return gates;
    }

    // 5. Giải phóng tất cả nhân viên liên quan đến cổng
    public boolean releaseStaffAssignedToGate(String gateNumber) {
        String query = "UPDATE ground_staff SET assigned_gate = NULL WHERE assigned_gate = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, gateNumber);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Released all staff assigned to Gate: " + gateNumber);
                return true;
            } else {
                System.out.println("No staff were assigned to Gate: " + gateNumber);
            }
        } catch (SQLException e) {
            System.err.println("Error releasing staff assigned to Gate: " + gateNumber);
            e.printStackTrace();
        }

        return false;
    }

}
