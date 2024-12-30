package services;

import config.DatabaseConnection;
import models.GroundStaff;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GroundStaffService {

    // 1. Lấy tất cả nhân viên mặt đất từ database
    public List<GroundStaff> getAllGroundStaff() {
        List<GroundStaff> groundStaffList = new ArrayList<>();
        String query = "SELECT gs.id, gs.assigned_gate, gs.assignment_date, " +
                "e.name, e.address, e.role FROM ground_staff gs " +
                "JOIN employee e ON gs.id = e.id";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String role = resultSet.getString("role");
                String assignedGate = resultSet.getString("assigned_gate");
                Timestamp assignmentDateTimestamp = resultSet.getTimestamp("assignment_date");
                LocalDateTime assignmentDate = assignmentDateTimestamp != null
                        ? assignmentDateTimestamp.toLocalDateTime()
                        : null;

                GroundStaff groundStaff = new GroundStaff(id, name, address, role, assignedGate, assignmentDate);
                groundStaffList.add(groundStaff);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return groundStaffList;
    }

    // 2. Thêm nhân viên mặt đất mới vào database
    public boolean addGroundStaff(GroundStaff groundStaff) {
        String employeeQuery = "INSERT INTO employee (id, name, address, role) VALUES (?, ?, ?, ?)";
        String groundStaffQuery = "INSERT INTO ground_staff (id) VALUES (?)";

        Connection connection = null; // Khai báo biến connection ở đây
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            // Thêm dữ liệu vào bảng `employee`
            try (PreparedStatement employeeStatement = connection.prepareStatement(employeeQuery)) {
                employeeStatement.setString(1, groundStaff.getId());
                employeeStatement.setString(2, groundStaff.getName());
                employeeStatement.setString(3, groundStaff.getAddress());
                employeeStatement.setString(4, groundStaff.getRole());
                employeeStatement.executeUpdate();
            }

            // Thêm dữ liệu vào bảng `ground_staff`
            try (PreparedStatement groundStaffStatement = connection.prepareStatement(groundStaffQuery)) {
                groundStaffStatement.setString(1, groundStaff.getId());
                groundStaffStatement.executeUpdate();
            }

            connection.commit(); // Xác nhận giao dịch
            return true;

        } catch (SQLException e) {
            System.err.println("Error while adding ground staff: " + e.getMessage());
            e.printStackTrace();

            // Hủy giao dịch nếu có lỗi
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Error during rollback: " + rollbackEx.getMessage());
                    rollbackEx.printStackTrace();
                }
            }

            return false;

        } finally {
            // Đóng kết nối
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error while closing connection: " + closeEx.getMessage());
                    closeEx.printStackTrace();
                }
            }
        }
    }

    // 3. Cập nhật thông tin nhân viên mặt đất
    public boolean updateGroundStaff(GroundStaff staff) {
        String query = "UPDATE employee SET name = ?, address = ?, role = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            // Gán các giá trị cập nhật
            statement.setString(1, staff.getName());
            statement.setString(2, staff.getAddress());
            statement.setString(3, staff.getRole());
            statement.setString(4, staff.getId());

            // Thực hiện cập nhật
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error while updating ground staff: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 4. Xóa nhân viên mặt đất theo ID
    public boolean deleteGroundStaff(String groundStaffId) {
        String query = "DELETE FROM ground_staff WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, groundStaffId);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 5. Lấy nhân viên mặt đất theo ID
    public GroundStaff getGroundStaffById(String id) {
        String query = "SELECT gs.id, gs.assigned_gate, gs.assignment_date, " +
                "e.name, e.address, e.role FROM ground_staff gs " +
                "JOIN employee e ON gs.id = e.id WHERE gs.id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String assignedGate = resultSet.getString("assigned_gate");
                Timestamp assignmentDateTimestamp = resultSet.getTimestamp("assignment_date");
                LocalDateTime assignmentDate = assignmentDateTimestamp != null
                        ? assignmentDateTimestamp.toLocalDateTime()
                        : null;

                return new GroundStaff(id, resultSet.getString("name"), resultSet.getString("address"),
                        resultSet.getString("role"), assignedGate, assignmentDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<GroundStaff> getAvailableGroundStaff() {
        List<GroundStaff> availableStaff = new ArrayList<>();
        String query = """
                SELECT gs.id, e.name, e.address, e.role
                FROM ground_staff gs
                JOIN employee e ON gs.id = e.id
                WHERE gs.assigned_gate IS NULL
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                String role = resultSet.getString("role");

                GroundStaff groundStaff = new GroundStaff(id, name, address, role, null, null);
                availableStaff.add(groundStaff);
            }
        } catch (SQLException e) {
            System.err.println("[GroundStaffService] Error while fetching available ground staff.");
            e.printStackTrace();
        }

        return availableStaff;
    }

    public boolean assignStaffToGate(String staffId, String gateNumber) {
        String query = """
                UPDATE ground_staff
                SET assigned_gate = ?, assignment_date = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, gateNumber);
            statement.setString(2, staffId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[GroundStaffService] Error while assigning staff to gate.");
            e.printStackTrace();
            return false;
        }
    }

}
