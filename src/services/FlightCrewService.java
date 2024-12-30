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
                LocalDateTime assignmentDate = assignmentDateTimestamp != null
                        ? assignmentDateTimestamp.toLocalDateTime()
                        : null;

                FlightCrew flightCrew = new FlightCrew(id, name, address, role, crewRole, flightNumber, assignmentDate);
                flightCrews.add(flightCrew);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flightCrews;
    }

    public boolean addFlightCrew(FlightCrew crew) {
        String insertEmployeeQuery = "INSERT INTO employee (id, name, address, role) VALUES (?, ?, ?, ?)";
        String insertFlightCrewQuery = "INSERT INTO flight_crew (id, crew_role, flight_number, assignment_date) VALUES (?, ?, ?, ?)";

        Connection connection = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            // Thêm vào bảng employee
            try (PreparedStatement empStmt = connection.prepareStatement(insertEmployeeQuery)) {
                empStmt.setString(1, crew.getId());
                empStmt.setString(2, crew.getName());
                empStmt.setString(3, crew.getAddress());
                empStmt.setString(4, crew.getRole());
                empStmt.executeUpdate();
            }

            // Thêm vào bảng flight_crew
            try (PreparedStatement crewStmt = connection.prepareStatement(insertFlightCrewQuery)) {
                crewStmt.setString(1, crew.getId());
                crewStmt.setString(2, crew.getCrewRole()); // Sử dụng crewRole được chọn
                crewStmt.setString(3, null); // Chưa được gán flight number
                crewStmt.setTimestamp(4, null); // Không có Assignment Date
                crewStmt.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();

            if (connection != null) {
                try {
                    connection.rollback(); // Hủy giao dịch nếu có lỗi
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }

            return false;

        } finally {
            if (connection != null) {
                try {
                    connection.close(); // Đóng kết nối
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
    }

    // 3. Cập nhật thông tin phi hành đoàn
    // 3. Cập nhật thông tin phi hành đoàn
    public boolean updateFlightCrew(FlightCrew crew) {
        String updateEmployeeQuery = "UPDATE employee SET name = ?, address = ?, role = ? WHERE id = ?";
        String updateFlightCrewQuery = "UPDATE flight_crew SET crew_role = ? WHERE id = ?";
        Connection connection = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false); // Bắt đầu giao dịch

            // Cập nhật thông tin trong bảng employee
            try (PreparedStatement employeeStatement = connection.prepareStatement(updateEmployeeQuery)) {
                employeeStatement.setString(1, crew.getName());
                employeeStatement.setString(2, crew.getAddress());
                employeeStatement.setString(3, crew.getRole());
                employeeStatement.setString(4, crew.getId());
                employeeStatement.executeUpdate();
            }

            // Cập nhật thông tin trong bảng flight_crew
            try (PreparedStatement flightCrewStatement = connection.prepareStatement(updateFlightCrewQuery)) {
                flightCrewStatement.setString(1, crew.getCrewRole());
                flightCrewStatement.setString(2, crew.getId());
                flightCrewStatement.executeUpdate();
            }

            connection.commit(); // Xác nhận giao dịch
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback(); // Hủy giao dịch nếu có lỗi
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            return false;

        } finally {
            if (connection != null) {
                try {
                    connection.close(); // Đảm bảo đóng kết nối
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
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
                LocalDateTime assignmentDate = assignmentDateTimestamp != null
                        ? assignmentDateTimestamp.toLocalDateTime()
                        : null;

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
                LocalDateTime assignmentDate = assignmentDateTimestamp != null
                        ? assignmentDateTimestamp.toLocalDateTime()
                        : null;

                FlightCrew flightCrew = new FlightCrew(id, name, address, role, crewRole, flightNumber, assignmentDate);
                flightCrews.add(flightCrew);
            }
        } catch (SQLException e) {
            System.err.println("[FlightCrewService] Error while fetching flight crew for flight: " + flightNumber);
            e.printStackTrace();
        }

        return flightCrews;
    }

}
