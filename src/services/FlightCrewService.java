package services;

import config.DatabaseConnection;
import models.FlightCrew;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightCrewService {

    // 1. Lấy tất cả phi hành đoàn từ database
    public List<FlightCrew> getAllFlightCrews() {
        List<FlightCrew> flightCrews = new ArrayList<>();
        String query = "SELECT fc.id, fc.crew_role, fc.flight_number, fc.assignment_date, " +
                       "e.name, e.address, e.role FROM flight_crew fc " +
                       "JOIN employee e ON fc.id = e.id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String role = resultSet.getString("role");
                String crewRole = resultSet.getString("crew_role");
                String flightNumber = resultSet.getString("flight_number");
                Timestamp assignmentDateTimestamp = resultSet.getTimestamp("assignment_date");
                LocalDateTime assignmentDate = assignmentDateTimestamp != null ? assignmentDateTimestamp.toLocalDateTime() : null;

                FlightCrew flightCrew = new FlightCrew(id, name, address, role, crewRole, flightNumber, assignmentDate);
                flightCrews.add(flightCrew);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flightCrews;
    }

    // 2. Thêm phi hành đoàn mới
    public boolean addFlightCrew(FlightCrew flightCrew) {
        String insertEmployeeQuery = "INSERT INTO employee (id, name, address, role) VALUES (?, ?, ?, ?)";
        String insertFlightCrewQuery = "INSERT INTO flight_crew (id, crew_role, flight_number, assignment_date) VALUES (?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false); // Bắt đầu giao dịch

            // Kiểm tra và thêm nhân viên vào bảng employee nếu chưa tồn tại
            String checkEmployeeQuery = "SELECT COUNT(*) FROM employee WHERE id = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkEmployeeQuery)) {
                checkStatement.setString(1, flightCrew.getId());
                ResultSet resultSet = checkStatement.executeQuery();
                if (resultSet.next() && resultSet.getInt(1) == 0) {
                    try (PreparedStatement insertEmployeeStatement = connection.prepareStatement(insertEmployeeQuery)) {
                        insertEmployeeStatement.setString(1, flightCrew.getId());
                        insertEmployeeStatement.setString(2, flightCrew.getName());
                        insertEmployeeStatement.setString(3, flightCrew.getAddress());
                        insertEmployeeStatement.setString(4, flightCrew.getRole());
                        insertEmployeeStatement.executeUpdate();
                    }
                }
            }

            // Kiểm tra tồn tại của chuyến bay
            String checkFlightQuery = "SELECT COUNT(*) FROM flight WHERE flight_number = ?";
            try (PreparedStatement checkFlightStatement = connection.prepareStatement(checkFlightQuery)) {
                checkFlightStatement.setString(1, flightCrew.getFlightNumber());
                ResultSet flightResult = checkFlightStatement.executeQuery();
                if (flightResult.next() && flightResult.getInt(1) == 0) {
                    System.err.println("[FlightCrewService] Flight does not exist: " + flightCrew.getFlightNumber());
                    connection.rollback(); // Hủy giao dịch
                    return false;
                }
            }

            // Thêm phi hành đoàn vào bảng flight_crew
            try (PreparedStatement statement = connection.prepareStatement(insertFlightCrewQuery)) {
                statement.setString(1, flightCrew.getId());
                statement.setString(2, flightCrew.getCrewRole());
                statement.setString(3, flightCrew.getFlightNumber());
                statement.setTimestamp(4, flightCrew.getAssignmentDate() != null ? Timestamp.valueOf(flightCrew.getAssignmentDate()) : null);

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("[FlightCrewService] Flight Crew added successfully.");
                    connection.commit(); // Xác nhận giao dịch
                    return true;
                } else {
                    System.err.println("[FlightCrewService] Failed to add Flight Crew.");
                    connection.rollback(); // Hủy giao dịch nếu thất bại
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 3. Cập nhật thông tin phi hành đoàn
    public boolean updateFlightCrew(String flightCrewId, String field, String newValue) {
        String query = "UPDATE flight_crew SET " + field + " = ? WHERE id = ?";

        if (!isValidField(field)) {
            System.err.println("[FlightCrewService] Invalid field: " + field);
            return false;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newValue);
            statement.setString(2, flightCrewId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("[FlightCrewService] Field updated successfully for Flight Crew: " + flightCrewId);
                return true;
            } else {
                System.err.println("[FlightCrewService] No Flight Crew found with ID: " + flightCrewId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 4. Xóa phi hành đoàn theo ID
    public boolean deleteFlightCrew(String flightCrewId) {
        String query = "DELETE FROM flight_crew WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, flightCrewId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("[FlightCrewService] Flight Crew deleted successfully.");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 5. Lấy thông tin chuyến bay của phi hành đoàn
    public String getFlightDetailsByFlightCrew(String flightCrewId) {
        String query = "SELECT flight_number, departure_time, arrival_time, departure_airport, arrival_airport " +
                       "FROM flight WHERE flight_number IN (SELECT flight_number FROM flight_crew WHERE id = ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, flightCrewId);
            ResultSet resultSet = statement.executeQuery();
            StringBuilder flightDetails = new StringBuilder();

            while (resultSet.next()) {
                String flightNumber = resultSet.getString("flight_number");
                Timestamp departureTime = resultSet.getTimestamp("departure_time");
                Timestamp arrivalTime = resultSet.getTimestamp("arrival_time");
                String departureAirport = resultSet.getString("departure_airport");
                String arrivalAirport = resultSet.getString("arrival_airport");

                flightDetails.append("Flight Number: ").append(flightNumber)
                        .append(", Departure Time: ").append(departureTime)
                        .append(", Arrival Time: ").append(arrivalTime)
                        .append(", Departure Airport: ").append(departureAirport)
                        .append(", Arrival Airport: ").append(arrivalAirport)
                        .append("\n");
            }

            if (flightDetails.length() > 0) {
                return flightDetails.toString();
            } else {
                System.err.println("[FlightCrewService] No flights found for Flight Crew ID: " + flightCrewId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "[FlightCrewService] An error occurred while fetching flight details.";
    }

    // 6. Lấy phi hành đoàn theo ID
    public FlightCrew getFlightCrewById(String id) {
        String query = "SELECT fc.id, fc.crew_role, fc.flight_number, fc.assignment_date, " +
                       "e.name, e.address, e.role FROM flight_crew fc " +
                       "JOIN employee e ON fc.id = e.id WHERE fc.id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String crewRole = resultSet.getString("crew_role");
                String flightNumber = resultSet.getString("flight_number");
                Timestamp assignmentDateTimestamp = resultSet.getTimestamp("assignment_date");
                LocalDateTime assignmentDate = assignmentDateTimestamp != null ? assignmentDateTimestamp.toLocalDateTime() : null;

                return new FlightCrew(id, resultSet.getString("name"), resultSet.getString("address"),
                        resultSet.getString("role"), crewRole, flightNumber, assignmentDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // 7. Lấy danh sách phi hành đoàn theo chuyến bay
public List<FlightCrew> getFlightCrewByFlight(String flightNumber) {
    List<FlightCrew> flightCrews = new ArrayList<>();
    String query = "SELECT fc.id, fc.crew_role, fc.flight_number, fc.assignment_date, " +
                   "e.name, e.address, e.role FROM flight_crew fc " +
                   "JOIN employee e ON fc.id = e.id WHERE fc.flight_number = ?";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(query)) {

        statement.setString(1, flightNumber);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String name = resultSet.getString("name");
            String address = resultSet.getString("address");
            String role = resultSet.getString("role");
            String crewRole = resultSet.getString("crew_role");
            Timestamp assignmentDateTimestamp = resultSet.getTimestamp("assignment_date");
            LocalDateTime assignmentDate = assignmentDateTimestamp != null ? assignmentDateTimestamp.toLocalDateTime() : null;

            FlightCrew flightCrew = new FlightCrew(id, name, address, role, crewRole, flightNumber, assignmentDate);
            flightCrews.add(flightCrew);
        }
    } catch (SQLException e) {
        System.err.println("[FlightCrewService] Error while fetching flight crew for flight: " + flightNumber);
        e.printStackTrace();
    }

    return flightCrews;
}

    // Kiểm tra tính hợp lệ của trường cập nhật
    private boolean isValidField(String field) {
        return field.equals("crew_role") || field.equals("flight_number") || field.equals("assignment_date");
    }
}
