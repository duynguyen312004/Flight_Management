package services;

import config.DatabaseConnection;
import models.Flight;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FlightService {

    // 1. Lấy tất cả chuyến bay
    public List<Flight> getAllFlights() {
        List<Flight> flights = new ArrayList<>();
        String query = "SELECT flight_number, departure_location, arrival_location, departure_time, arrival_time, status, assigned_gate, airplane_id FROM flight";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Flight flight = mapResultSetToFlight(resultSet);
                flights.add(flight);
                System.out.printf("[FlightService] Loaded flight: %s%n", flight);
            }
        } catch (SQLException e) {
            System.err.println("[FlightService] Error while fetching flights.");
            e.printStackTrace();
        }

        return flights;
    }

    // 2. Lấy chuyến bay theo trạng thái
    public List<Flight> getFlightsByStatus(String status) {
        List<Flight> flights = new ArrayList<>();
        String query = "SELECT flight_number, departure_location, arrival_location, departure_time, arrival_time, status, assigned_gate, airplane_id FROM flight WHERE status = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                flights.add(mapResultSetToFlight(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("[FlightService] Error while fetching flights by status: " + status);
            e.printStackTrace();
        }

        return flights;
    }

    // 3. Lấy chuyến bay theo mã chuyến bay
    public Flight getFlightByNumber(String flightNumber) {
        String query = "SELECT flight_number, departure_location, arrival_location, departure_time, arrival_time, status, assigned_gate, airplane_id FROM flight WHERE flight_number = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, flightNumber);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapResultSetToFlight(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("[FlightService] Error while fetching flight by number: " + flightNumber);
            e.printStackTrace();
        }
        return null;
    }

    // Thêm chuyến bay mới
    public boolean addFlight(Flight flight) {
        String query = "INSERT INTO flight (flight_number, departure_location, arrival_location, departure_time, arrival_time, status, assigned_gate, airplane_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String updateAirplaneStatus = "UPDATE airplane SET status = 'In Use' WHERE airplane_id = ?";
        String updateGateStatus = "UPDATE gate SET is_available = 0 WHERE gate_number = ?";
        Connection connection = null;
        PreparedStatement insertStatement = null;
        PreparedStatement airplaneUpdateStatement = null;
        PreparedStatement gateUpdateStatement = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false); // Bắt đầu transaction

            // 1. Thêm chuyến bay
            insertStatement = connection.prepareStatement(query);
            insertStatement.setString(1, flight.getFlightNumber());
            insertStatement.setString(2, flight.getDepartureLocation());
            insertStatement.setString(3, flight.getArrivalLocation());
            insertStatement.setString(4, flight.getDepartureTime());
            insertStatement.setString(5, flight.getArrivalTime());
            insertStatement.setString(6, flight.getStatus());
            insertStatement.setString(7, flight.getAssignedGate());
            insertStatement.setString(8, flight.getAirplaneId());
            int rowsInserted = insertStatement.executeUpdate();

            if (rowsInserted == 0) {
                throw new SQLException("Failed to insert flight.");
            }

            // 2. Cập nhật trạng thái của Airplane
            airplaneUpdateStatement = connection.prepareStatement(updateAirplaneStatus);
            airplaneUpdateStatement.setString(1, flight.getAirplaneId());
            int airplaneUpdated = airplaneUpdateStatement.executeUpdate();

            if (airplaneUpdated == 0) {
                throw new SQLException("Failed to update airplane status.");
            }

            // 3. Cập nhật trạng thái của Gate
            gateUpdateStatement = connection.prepareStatement(updateGateStatus);
            gateUpdateStatement.setString(1, flight.getAssignedGate());
            int gateUpdated = gateUpdateStatement.executeUpdate();

            if (gateUpdated == 0) {
                throw new SQLException("Failed to update gate status.");
            }

            // Commit transaction nếu tất cả thành công
            connection.commit();
            return true;
        } catch (SQLException e) {
            // Rollback nếu có lỗi
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            System.err.println("[FlightService] Error while adding new flight.");
            e.printStackTrace();
        } finally {
            // Đóng các tài nguyên
            try {
                if (insertStatement != null)
                    insertStatement.close();
                if (airplaneUpdateStatement != null)
                    airplaneUpdateStatement.close();
                if (gateUpdateStatement != null)
                    gateUpdateStatement.close();
                if (connection != null)
                    connection.setAutoCommit(true); // Trả về chế độ tự động commit
            } catch (SQLException closeEx) {
                closeEx.printStackTrace();
            }
        }
        return false;
    }

    // 5. Cập nhật thông tin chuyến bay
    public boolean updateFlightDetails(Flight updatedFlight, Flight originalFlight) {
        String updateFlightQuery = "UPDATE flight SET departure_time = ?, arrival_time = ?, departure_location = ?, " +
                "arrival_location = ?, status = ?, assigned_gate = ?, airplane_id = ? WHERE flight_number = ?";
        String releaseGateQuery = "UPDATE gate SET is_available = 1 WHERE gate_number = ?";
        String assignGateQuery = "UPDATE gate SET is_available = 0 WHERE gate_number = ?";
        String releaseAirplaneQuery = "UPDATE airplane SET status = 'Available' WHERE airplane_id = ?";
        String assignAirplaneQuery = "UPDATE airplane SET status = 'In Use' WHERE airplane_id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Nếu cổng thay đổi, giải phóng cổng cũ và gán cổng mới
            if (!updatedFlight.getAssignedGate().equals(originalFlight.getAssignedGate())) {
                try (PreparedStatement releaseGateStmt = connection.prepareStatement(releaseGateQuery);
                        PreparedStatement assignGateStmt = connection.prepareStatement(assignGateQuery)) {
                    releaseGateStmt.setString(1, originalFlight.getAssignedGate());
                    releaseGateStmt.executeUpdate();

                    assignGateStmt.setString(1, updatedFlight.getAssignedGate());
                    assignGateStmt.executeUpdate();
                }
            }

            // 2. Nếu máy bay thay đổi, giải phóng máy bay cũ và gán máy bay mới
            if (!updatedFlight.getAirplaneId().equals(originalFlight.getAirplaneId())) {
                try (PreparedStatement releaseAirplaneStmt = connection.prepareStatement(releaseAirplaneQuery);
                        PreparedStatement assignAirplaneStmt = connection.prepareStatement(assignAirplaneQuery)) {
                    releaseAirplaneStmt.setString(1, originalFlight.getAirplaneId());
                    releaseAirplaneStmt.executeUpdate();

                    assignAirplaneStmt.setString(1, updatedFlight.getAirplaneId());
                    assignAirplaneStmt.executeUpdate();
                }
            }

            // 3. Cập nhật thông tin chuyến bay
            try (PreparedStatement updateFlightStmt = connection.prepareStatement(updateFlightQuery)) {
                updateFlightStmt.setString(1, updatedFlight.getDepartureTime());
                updateFlightStmt.setString(2, updatedFlight.getArrivalTime());
                updateFlightStmt.setString(3, updatedFlight.getDepartureLocation());
                updateFlightStmt.setString(4, updatedFlight.getArrivalLocation());
                updateFlightStmt.setString(5, updatedFlight.getStatus());
                updateFlightStmt.setString(6, updatedFlight.getAssignedGate());
                updateFlightStmt.setString(7, updatedFlight.getAirplaneId());
                updateFlightStmt.setString(8, updatedFlight.getFlightNumber());
                updateFlightStmt.executeUpdate();
            }

            connection.commit(); // Xác nhận giao dịch
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 6. Xóa chuyến bay
    public boolean deleteFlight(String flightNumber) {
        String deleteFlightQuery = "DELETE FROM flight WHERE flight_number = ?";
        String releaseGateQuery = "UPDATE gate SET is_available = 1 WHERE gate_number = (SELECT assigned_gate FROM flight WHERE flight_number = ?)";
        String releaseAirplaneQuery = "UPDATE airplane SET status = 'Available' WHERE airplane_id = (SELECT airplane_id FROM flight WHERE flight_number = ?)";
        String resetFlightCrewQuery = "UPDATE flight_crew SET flight_number = NULL WHERE flight_number = ?";
        String resetGroundStaffQuery = "UPDATE ground_staff SET assigned_gate = NULL WHERE assigned_gate = (SELECT assigned_gate FROM flight WHERE flight_number = ?)";
        String deleteTicketsQuery = "DELETE FROM ticket WHERE flight_number = ?";

        Connection connection = null; // Khai báo biến connection
        try {
            connection = DatabaseConnection.getConnection(); // Khởi tạo kết nối
            connection.setAutoCommit(false); // Bắt đầu transaction

            // 1. Giải phóng cổng
            try (PreparedStatement gateStatement = connection.prepareStatement(releaseGateQuery)) {
                gateStatement.setString(1, flightNumber);
                gateStatement.executeUpdate();
            }

            // 2. Giải phóng máy bay
            try (PreparedStatement airplaneStatement = connection.prepareStatement(releaseAirplaneQuery)) {
                airplaneStatement.setString(1, flightNumber);
                airplaneStatement.executeUpdate();
            }

            // 3. Giải phóng phi hành đoàn
            try (PreparedStatement crewStatement = connection.prepareStatement(resetFlightCrewQuery)) {
                crewStatement.setString(1, flightNumber);
                crewStatement.executeUpdate();
            }

            // 4. Giải phóng nhân viên mặt đất
            try (PreparedStatement staffStatement = connection.prepareStatement(resetGroundStaffQuery)) {
                staffStatement.setString(1, flightNumber);
                staffStatement.executeUpdate();
            }

            // 5. Xóa vé liên quan
            try (PreparedStatement ticketsStatement = connection.prepareStatement(deleteTicketsQuery)) {
                ticketsStatement.setString(1, flightNumber);
                ticketsStatement.executeUpdate();
            }

            // 6. Xóa chuyến bay
            int rowsAffected;
            try (PreparedStatement flightStatement = connection.prepareStatement(deleteFlightQuery)) {
                flightStatement.setString(1, flightNumber);
                rowsAffected = flightStatement.executeUpdate();
            }

            connection.commit(); // Xác nhận transaction

            if (rowsAffected > 0) {
                System.out.println("Flight " + flightNumber + " deleted successfully.");
                return true;
            } else {
                System.out.println("Flight " + flightNumber + " not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback(); // Hủy transaction nếu có lỗi
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close(); // Đóng kết nối
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean updateFlight(Flight flight) {
        String query = "UPDATE flight SET departure_location = ?, arrival_location = ?, departure_time = ?, " +
                "arrival_time = ?, status = ?, assigned_gate = ?, airplane_id = ? WHERE flight_number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, flight.getDepartureLocation());
            statement.setString(2, flight.getArrivalLocation());
            statement.setString(3, flight.getDepartureTime());
            statement.setString(4, flight.getArrivalTime());
            statement.setString(5, flight.getStatus());
            statement.setString(6, flight.getAssignedGate());
            statement.setString(7, flight.getAirplaneId());
            statement.setString(8, flight.getFlightNumber());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 6. Giải phóng tất cả phi hành đoàn liên quan đến chuyến bay
    public boolean releaseFlightCrew(String flightNumber) {
        String query = "UPDATE flight_crew SET flight_number = NULL WHERE flight_number = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, flightNumber);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Released all crew assigned to Flight: " + flightNumber);
                return true;
            } else {
                System.out.println("No crew were assigned to Flight: " + flightNumber);
            }
        } catch (SQLException e) {
            System.err.println("Error releasing crew for Flight: " + flightNumber);
            e.printStackTrace();
        }

        return false;
    }

    // 7. lưu trữ dữ liệu của chuyến bay vào bảng flight_history
    public boolean saveFlightToHistory(Flight flight, String ticketsJson, String crewJson, String groundStaffJson) {
        String insertHistoryQuery = """
                    INSERT INTO flight_history (flight_number, departure_location, arrival_location,
                                                departure_time, arrival_time, status, airplane_id,
                                                assigned_gate, tickets, crew, ground_staff)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(insertHistoryQuery)) {

            statement.setString(1, flight.getFlightNumber());
            statement.setString(2, flight.getDepartureLocation());
            statement.setString(3, flight.getArrivalLocation());
            statement.setTimestamp(4, Timestamp.valueOf(flight.getDepartureTime()));
            statement.setTimestamp(5, Timestamp.valueOf(flight.getArrivalTime()));
            statement.setString(6, flight.getStatus());
            statement.setString(7, flight.getAirplaneId());
            statement.setString(8, flight.getAssignedGate());
            statement.setString(9, ticketsJson); // JSON của vé
            statement.setString(10, crewJson); // JSON của phi hành đoàn
            statement.setString(11, groundStaffJson); // JSON của nhân viên mặt đất

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean archiveFlight(String flightNumber) {
        String selectQuery = """
                SELECT f.flight_number, f.departure_location, f.arrival_location,
                       f.departure_time, f.arrival_time, f.status, f.assigned_gate, f.airplane_id,
                       (SELECT JSON_ARRAYAGG(JSON_OBJECT(
                            'ticket_id', t.ticket_id,
                            'seat_number', t.seat_number,
                            'seat_class', t.seat_class,
                            'price', t.price,
                            'passenger_id', t.passenger_id, -- Chỉ lưu passenger_id
                            'flight_number', t.flight_number))
                        FROM ticket t
                        WHERE t.flight_number = f.flight_number) AS tickets_json,
                       (SELECT JSON_ARRAYAGG(JSON_OBJECT(
                            'id', fc.id,
                            'name', e.name,
                            'role', e.role,
                            'address', e.address,
                            'crew_role', fc.crew_role,
                            'flight_number', fc.flight_number,
                            'assignment_date', fc.assignment_date))
                        FROM flight_crew fc
                        JOIN employee e ON fc.id = e.id
                        WHERE fc.flight_number = f.flight_number) AS crew_json,
                       (SELECT JSON_ARRAYAGG(JSON_OBJECT(
                            'id', gs.id,
                            'name', e.name,
                            'role', e.role,
                            'address', e.address,
                            'assigned_gate', gs.assigned_gate,
                            'assignment_date', gs.assignment_date))
                        FROM ground_staff gs
                        JOIN employee e ON gs.id = e.id
                        WHERE gs.assigned_gate = f.assigned_gate) AS ground_staff_json
                FROM flight f
                WHERE f.flight_number = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(selectQuery)) {

            statement.setString(1, flightNumber);
            System.out.println("[DEBUG] Executing SELECT query for flight archive...");

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Lấy dữ liệu từ ResultSet
                    String ticketsJson = resultSet.getString("tickets_json");
                    String crewJson = resultSet.getString("crew_json");
                    String groundStaffJson = resultSet.getString("ground_staff_json");

                    // In dữ liệu JSON ra console để debug
                    System.out.println("[DEBUG] Tickets JSON: " + ticketsJson);
                    System.out.println("[DEBUG] Crew JSON: " + crewJson);
                    System.out.println("[DEBUG] Ground Staff JSON: " + groundStaffJson);

                    // Khởi tạo đối tượng Flight từ ResultSet
                    Flight flight = new Flight(
                            resultSet.getString("flight_number"),
                            resultSet.getString("departure_location"),
                            resultSet.getString("arrival_location"),
                            resultSet.getTimestamp("departure_time").toString(),
                            resultSet.getTimestamp("arrival_time").toString(),
                            resultSet.getString("status"),
                            resultSet.getString("airplane_id"),
                            resultSet.getString("assigned_gate"));

                    // Lưu chuyến bay vào lịch sử
                    return saveFlightToHistory(flight, ticketsJson, crewJson, groundStaffJson);
                } else {
                    System.err.println("[ERROR] No flight data found for flight number: " + flightNumber);
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] SQLException occurred while archiving flight:");
            e.printStackTrace();
            return false;
        }
    }

    // 8. Ánh xạ từ ResultSet sang đối tượng Flight
    private Flight mapResultSetToFlight(ResultSet resultSet) throws SQLException {
        return new Flight(
                resultSet.getString("flight_number"),
                resultSet.getString("departure_location"),
                resultSet.getString("arrival_location"),
                resultSet.getString("departure_time"),
                resultSet.getString("arrival_time"),
                resultSet.getString("status"),
                resultSet.getString("assigned_gate"),
                resultSet.getString("airplane_id"));
    }
}
