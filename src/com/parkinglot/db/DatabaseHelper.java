package com.parkinglot.db;

import java.sql.*;
import java.util.*;

public class DatabaseHelper {

    private static final String DB_URL = "jdbc:sqlite:db/parking.db";

    // Create a connection to the database
    public static Connection connect() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            System.out.println("Connection failed! " + e.getMessage());
            return null;
        }
    }

    // Create the table and insert initial sample data
    public static void initializeDatabase() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS parking_spots (
                spot_id TEXT PRIMARY KEY,
                floor INTEGER,
                status TEXT
            );
        """;

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Table checked/created.");

            if (isEmpty(conn)) {
                insertSampleData(conn);
                System.out.println("Sample data inserted.");
            }
        } catch (SQLException e) {
            System.out.println("DB init error: " + e.getMessage());
        }
    }

    // Check if the table is empty
    private static boolean isEmpty(Connection conn) throws SQLException {
        String checkSQL = "SELECT COUNT(*) AS count FROM parking_spots";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(checkSQL)) {
            return rs.getInt("count") == 0;
        }
    }

    // Insert initial sample data (6 spots on each floor)
    private static void insertSampleData(Connection conn) throws SQLException {
        String insertSQL = "INSERT INTO parking_spots (spot_id, floor, status) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            for (int floor = 1; floor <= 3; floor++) { // 3 floors now
                for (int i = 1; i <= 20; i++) {
                    String id = "F" + floor + "S" + i;
                    pstmt.setString(1, id);
                    pstmt.setInt(2, floor);
                    pstmt.setString(3, i % 2 == 0 ? "Occupied" : "Free");
                    pstmt.addBatch();
                }
            }
            
            
            pstmt.executeBatch();
        }
    }

    // Retrieve all parking spots
    public static List<Map<String, String>> getAllParkingSpots() {
        List<Map<String, String>> spots = new ArrayList<>();
        String query = "SELECT * FROM parking_spots";

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Map<String, String> spot = new HashMap<>();
                spot.put("spot_id", rs.getString("spot_id"));
                spot.put("floor", String.valueOf(rs.getInt("floor")));
                spot.put("status", rs.getString("status"));
                spots.add(spot);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching parking spots: " + e.getMessage());
        }

        return spots;
    }

    // Toggle the status of a specific parking spot (Free <-> Occupied)
    public static void toggleSpotStatus(String spotId) {
        String getStatusSQL = "SELECT status FROM parking_spots WHERE spot_id = ?";
        String updateSQL = "UPDATE parking_spots SET status = ? WHERE spot_id = ?";

        try (Connection conn = connect();
             PreparedStatement getStmt = conn.prepareStatement(getStatusSQL);
             PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {

            getStmt.setString(1, spotId);
            ResultSet rs = getStmt.executeQuery();

            if (rs.next()) {
                String currentStatus = rs.getString("status");
                String newStatus = currentStatus.equals("Free") ? "Occupied" : "Free";

                // Debugging output to verify status change
                System.out.println("Toggling spot " + spotId + " from " + currentStatus + " to " + newStatus);

                updateStmt.setString(1, newStatus);
                updateStmt.setString(2, spotId);
                updateStmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println("Failed to toggle status: " + e.getMessage());
        }
    }

    // Get the status of a specific parking spot
    public static String getParkingSpotStatus(String spotId) {
        String getStatusSQL = "SELECT status FROM parking_spots WHERE spot_id = ?";
        String status = "";

        try (Connection conn = connect();
             PreparedStatement getStmt = conn.prepareStatement(getStatusSQL)) {

            getStmt.setString(1, spotId);
            ResultSet rs = getStmt.executeQuery();

            if (rs.next()) {
                status = rs.getString("status");
            }

        } catch (SQLException e) {
            System.out.println("Error fetching status: " + e.getMessage());
        }

        return status;
    }
}
