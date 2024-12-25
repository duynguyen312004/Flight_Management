package services;

import config.DatabaseConnection;
import models.Ticket;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketService {

    // 1. Lấy tất cả các vé từ cơ sở dữ liệu
    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT ticket_id, flight_number, passenger_id, seat_number, seat_class, price FROM ticket";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                tickets.add(mapResultSetToTicket(resultSet));
            }

            System.out.println("[TicketService] Loaded " + tickets.size() + " tickets.");
        } catch (SQLException e) {
            System.err.println("[TicketService] Error while fetching tickets.");
            e.printStackTrace();
        }

        return tickets;
    }

    public int getTicketCountByFlight(String flightNumber) {
        String query = "SELECT COUNT(*) FROM ticket WHERE flight_number = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, flightNumber);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 2. Thêm vé mới vào cơ sở dữ liệu
    public boolean addTicket(Ticket ticket) {
        // Kiểm tra sự tồn tại của flight_number và passenger_id
        if (!isFlightExist(ticket.getFlightNumber())) {
            System.err.println("[TicketService] Flight number " + ticket.getFlightNumber() + " does not exist.");
            return false;
        }

        if (!isPassengerExist(ticket.getPassengerID())) {
            System.err.println("[TicketService] Passenger ID " + ticket.getPassengerID() + " does not exist.");
            return false;
        }

        String insertQuery = "INSERT INTO ticket (ticket_id, flight_number, passenger_id, seat_number, seat_class, price) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(insertQuery)) {

            statement.setString(1, ticket.getTicketID());
            statement.setString(2, ticket.getFlightNumber());
            statement.setString(3, ticket.getPassengerID());
            statement.setString(4, ticket.getSeatNumber());
            statement.setString(5, ticket.getSeatClass());
            statement.setDouble(6, ticket.getPrice());

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("[TicketService] Ticket added successfully.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[TicketService] Error while adding ticket.");
            e.printStackTrace();
        }

        return false;
    }

    // 3. Cập nhật thông tin vé trong cơ sở dữ liệu
    public boolean updateTicket(Ticket ticket) {
        String query = "UPDATE ticket SET flight_number = ?, passenger_id = ?, seat_number = ?, seat_class = ?, price = ? WHERE ticket_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, ticket.getFlightNumber());
            statement.setString(2, ticket.getPassengerID());
            statement.setString(3, ticket.getSeatNumber());
            statement.setString(4, ticket.getSeatClass());
            statement.setDouble(5, ticket.getPrice());
            statement.setString(6, ticket.getTicketID());

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("[TicketService] Ticket updated successfully.");
                return true;
            } else {
                System.err.println("[TicketService] Ticket with ID " + ticket.getTicketID() + " not found.");
            }
        } catch (SQLException e) {
            System.err.println("[TicketService] Error while updating ticket.");
            e.printStackTrace();
        }

        return false;
    }

    // 4. Xóa vé khỏi cơ sở dữ liệu
    public boolean deleteTicket(String ticketID) {
        String query = "DELETE FROM ticket WHERE ticket_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, ticketID);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("[TicketService] Ticket deleted successfully.");
                return true;
            } else {
                System.err.println("[TicketService] Ticket with ID " + ticketID + " not found.");
            }
        } catch (SQLException e) {
            System.err.println("[TicketService] Error while deleting ticket.");
            e.printStackTrace();
        }

        return false;
    }

    // 5. Lấy vé của hành khách từ cơ sở dữ liệu
    public List<Ticket> getTicketsByPassenger(String passengerId) {
        List<Ticket> tickets = new ArrayList<>();
        String query = "SELECT ticket_id, flight_number, seat_number, seat_class, price FROM ticket WHERE passenger_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, passengerId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                tickets.add(mapResultSetToTicket(resultSet));
            }

            System.out.println(
                    "[TicketService] Loaded " + tickets.size() + " tickets for passenger ID " + passengerId + ".");
        } catch (SQLException e) {
            System.err.println("[TicketService] Error while fetching tickets for passenger ID " + passengerId + ".");
            e.printStackTrace();
        }

        return tickets;
    }

    // 6. Kiểm tra sự tồn tại của chuyến bay
    private boolean isFlightExist(String flightNumber) {
        String query = "SELECT COUNT(*) FROM flight WHERE flight_number = ?";
        return checkExistence(query, flightNumber);
    }

    // 7. Kiểm tra sự tồn tại của hành khách
    private boolean isPassengerExist(String passengerId) {
        String query = "SELECT COUNT(*) FROM passenger WHERE id = ?";
        return checkExistence(query, passengerId);
    }

    // 8. Hàm kiểm tra sự tồn tại chung
    private boolean checkExistence(String query, String value) {
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, value);
            ResultSet resultSet = statement.executeQuery();

            return resultSet.next() && resultSet.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 9. Ánh xạ ResultSet sang đối tượng Ticket
    private Ticket mapResultSetToTicket(ResultSet resultSet) throws SQLException {
        return new Ticket(
                resultSet.getString("ticket_id"),
                resultSet.getString("flight_number"),
                resultSet.getString("passenger_id"),
                resultSet.getString("seat_number"),
                resultSet.getString("seat_class"),
                resultSet.getDouble("price"));
    }
}
