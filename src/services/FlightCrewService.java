package services;

import config.DatabaseConnection;
import models.FlightCrew;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class FlightCrewService {

    // 1. Lấy tất cả các phi hành đoàn từ database
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

    public boolean addFlightCrew(FlightCrew flightCrew) {
        String insertEmployeeQuery = "INSERT INTO employee (id, name, address, role) VALUES (?, ?, ?, ?)";
        String insertFlightCrewQuery = "INSERT INTO flight_crew (id, crew_role, flight_number, assignment_date) VALUES (?, ?, ?, ?)";
        
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);  
    
            // Thêm nhân viên vào bảng employee nếu chưa tồn tại
            String checkEmployeeQuery = "SELECT COUNT(*) FROM employee WHERE id = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkEmployeeQuery)) {
                checkStatement.setString(1, flightCrew.getId());
                ResultSet resultSet = checkStatement.executeQuery();
    
                if (resultSet.next() && resultSet.getInt(1) == 0) {
                    // Thêm nhân viên vào bảng employee nếu không tồn tại
                    try (PreparedStatement insertEmployeeStatement = connection.prepareStatement(insertEmployeeQuery)) {
                        insertEmployeeStatement.setString(1, flightCrew.getId());
                        insertEmployeeStatement.setString(2, flightCrew.getName()); 
                        insertEmployeeStatement.setString(3, flightCrew.getAddress()); 
                        insertEmployeeStatement.setString(4, flightCrew.getRole());
                        insertEmployeeStatement.executeUpdate();
                    }
                }
            }
    
            // Kiểm tra sự tồn tại của flight_number trong bảng flight trước khi thêm phi hành đoàn
            String checkFlightQuery = "SELECT COUNT(*) FROM flight WHERE flight_number = ?";
            try (PreparedStatement checkFlightStatement = connection.prepareStatement(checkFlightQuery)) {
                checkFlightStatement.setString(1, flightCrew.getFlightNumber());
                ResultSet flightResult = checkFlightStatement.executeQuery();
                if (flightResult.next() && flightResult.getInt(1) == 0) {
                    System.out.println("Flight with flight_number " + flightCrew.getFlightNumber() + " does not exist in the database.");
                    connection.rollback(); 
                    return false;
                }
            }
    
            // Thêm phi hành đoàn vào bảng flight_crew
            try (PreparedStatement statement = connection.prepareStatement(insertFlightCrewQuery)) {
                statement.setString(1, flightCrew.getId()); 
                statement.setString(2, flightCrew.getCrewRole()); 
                
                if (flightCrew.getFlightNumber() == null || flightCrew.getFlightNumber().isEmpty()) {
                    statement.setNull(3, java.sql.Types.VARCHAR);  
                } else {
                    statement.setString(3, flightCrew.getFlightNumber());
                }
                
                if (flightCrew.getAssignmentDate() != null) {
                    statement.setTimestamp(4, Timestamp.valueOf(flightCrew.getAssignmentDate()));
                } else {
                    System.out.println("Assignment date is invalid.");
                    connection.rollback();  
                    return false;
                }
    
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Flight Crew added successfully.");
                    connection.commit();  
                    return true;
                } else {
                    System.out.println("Failed to add flight crew.");
                    connection.rollback(); 
                }
            }
    
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();  
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return false;
    }
    

    public boolean updateFlightCrew(String flightCrewId, String field, String newValue) {
        String query = "UPDATE flight_crew SET " + field + " = ? WHERE id = ?";

        // Kiểm tra xem trường có hợp lệ không
        if (!isValidField(field)) {
            System.out.println("Invalid field name.");
            return false;  // Trường không hợp lệ
        }

        try (Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            // Set giá trị mới vào câu lệnh SQL
            statement.setString(1, newValue);  // Giá trị mới cho trường
            statement.setString(2, flightCrewId);  // ID của phi hành đoàn

            int rowsUpdated = statement.executeUpdate();  // Thực thi câu lệnh SQL

            if (rowsUpdated > 0) {
                System.out.println("Flight Crew field updated successfully.");
                return true;  // Nếu có bản ghi được cập nhật
            } else {
                System.out.println("No flight crew found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();  // In lỗi nếu có sự cố
        }

        return false;  // Nếu không có gì được cập nhật
    }

    // Phương thức kiểm tra tính hợp lệ của trường (field)
    private boolean isValidField(String field) {
        // Danh sách các trường hợp lệ
        return field.equals("crew_role") || field.equals("flight_number") || field.equals("assignment_date");
    }


    // 4. Xóa phi hành đoàn theo ID
    public boolean deleteFlightCrew(String flightCrewId) {
        String query = "DELETE FROM flight_crew WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, flightCrewId);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Flight Crew deleted successfully.");
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

            return flightDetails.length() > 0 ? flightDetails.toString() : "No flights found for this flight crew.";
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "An error occurred while fetching the flight details.";
    }

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
}
