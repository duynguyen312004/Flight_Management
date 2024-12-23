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
    public List<Gate> getAllGates() {
        List<Gate> gates = new ArrayList<>();
        String query = "SELECT * FROM gate";

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
}
