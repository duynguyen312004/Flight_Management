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
                LocalDateTime assignmentDate = assignmentDateTimestamp != null ? assignmentDateTimestamp.toLocalDateTime() : null;

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
        String insertEmployeeQuery = "INSERT INTO employee (id, name, address, role) VALUES (?, ?, ?, ?)";
        String insertGroundStaffQuery = "INSERT INTO ground_staff (id, assigned_gate, assignment_date) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            // Thêm nhân viên vào bảng employee nếu chưa tồn tại
            String checkEmployeeQuery = "SELECT COUNT(*) FROM employee WHERE id = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkEmployeeQuery)) {
                checkStatement.setString(1, groundStaff.getId());
                ResultSet resultSet = checkStatement.executeQuery();

                if (resultSet.next() && resultSet.getInt(1) == 0) {
                    try (PreparedStatement insertEmployeeStatement = connection.prepareStatement(insertEmployeeQuery)) {
                        insertEmployeeStatement.setString(1, groundStaff.getId());
                        insertEmployeeStatement.setString(2, groundStaff.getName());
                        insertEmployeeStatement.setString(3, groundStaff.getAddress());
                        insertEmployeeStatement.setString(4, groundStaff.getRole());
                        insertEmployeeStatement.executeUpdate();
                    }
                }
            }

            // Thêm nhân viên mặt đất vào bảng ground_staff
            try (PreparedStatement statement = connection.prepareStatement(insertGroundStaffQuery)) {
                statement.setString(1, groundStaff.getId());
                statement.setString(2, groundStaff.getAssignedGate());
                if (groundStaff.getAssignmentDate() != null) {
                    statement.setTimestamp(3, Timestamp.valueOf(groundStaff.getAssignmentDate()));
                } else {
                    statement.setNull(3, Types.TIMESTAMP);
                }
                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    connection.commit();
                    return true;
                } else {
                    connection.rollback();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 3. Cập nhật thông tin nhân viên mặt đất
    public boolean updateGroundStaff(String groundStaffId, String field, String newValue) {
        String query = "UPDATE ground_staff SET " + field + " = ? WHERE id = ?";

        if (!isValidField(field)) {
            System.out.println("Invalid field name.");
            return false;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newValue);
            statement.setString(2, groundStaffId);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                return true;
            } else {
                System.out.println("No ground staff found with the given ID.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Kiểm tra trường hợp lệ
    private boolean isValidField(String field) {
        return field.equals("assigned_gate") || field.equals("assignment_date");
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
                LocalDateTime assignmentDate = assignmentDateTimestamp != null ? assignmentDateTimestamp.toLocalDateTime() : null;

                return new GroundStaff(id, resultSet.getString("name"), resultSet.getString("address"),
                        resultSet.getString("role"), assignedGate, assignmentDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
