package services;

import config.DatabaseConnection;
import models.Airplane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AirplaneService {

    // 1. Lấy thông tin máy bay dựa trên ID
    public Airplane getAirplaneById(String airplaneId) {
        String query = """
                SELECT airplane_id, seat_capacity, status
                FROM airplane
                WHERE airplane_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, airplaneId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return new Airplane(
                        resultSet.getString("airplane_id"),
                        resultSet.getInt("seat_capacity"),
                        resultSet.getString("status"));
            }
        } catch (SQLException e) {
            System.err.println("[AirplaneService] Error while fetching airplane with ID: " + airplaneId);
            e.printStackTrace();
        }

        return null; // Trả về null nếu không tìm thấy
    }

    public List<Airplane> getAllAirplanes() {
        List<Airplane> airplanes = new ArrayList<>();
        String query = "SELECT airplane_id, seat_capacity, status FROM airplane";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String airplaneId = resultSet.getString("airplane_id");
                int seatCapacity = resultSet.getInt("seat_capacity");
                String status = resultSet.getString("status");

                airplanes.add(new Airplane(airplaneId, seatCapacity, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return airplanes;
    }

    // 2. Thêm máy bay mới vào cơ sở dữ liệu
    public boolean addAirplane(Airplane airplane) {
        String query = """
                INSERT INTO airplane (airplane_id, seat_capacity, status)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, airplane.getAirplaneId());
            statement.setInt(2, airplane.getSeatCapacity());
            statement.setString(3, airplane.getStatus());

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("[AirplaneService] Error while adding airplane: " + airplane.getAirplaneId());
            e.printStackTrace();
        }

        return false;
    }

    // 3. Cập nhật thông tin máy bay
    public boolean updateAirplane(Airplane airplane) {
        String query = """
                UPDATE airplane
                SET seat_capacity = ?, status = ?
                WHERE airplane_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, airplane.getSeatCapacity());
            statement.setString(2, airplane.getStatus());
            statement.setString(3, airplane.getAirplaneId());

            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("[AirplaneService] Error while updating airplane: " + airplane.getAirplaneId());
            e.printStackTrace();
        }

        return false;
    }

    // 4. Xóa máy bay
    public boolean deleteAirplane(String airplaneId) {
        String query = "DELETE FROM airplane WHERE airplane_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, airplaneId);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("[AirplaneService] Error while deleting airplane with ID: " + airplaneId);
            e.printStackTrace();
        }

        return false;
    }

    public String getFlightUsingAirplane(String airplaneId) {
        String query = "SELECT flight_number FROM flight WHERE airplane_id = ? AND status != 'Cancelled'";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, airplaneId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("flight_number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "None"; // Không có chuyến bay nào đang sử dụng
    }

}
