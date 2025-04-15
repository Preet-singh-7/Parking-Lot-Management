package com.parkinglot.gui;

import com.parkinglot.db.DatabaseHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

public class ParkingLotGUI {

    private static JLabel occupancyLabel = new JLabel();

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Parking Lot Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Top occupancy label
        occupancyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        occupancyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        updateOccupancyCount();
        frame.add(occupancyLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Create one tab per floor
        for (int floor = 1; floor <= 3; floor++) {
            JPanel floorPanel = new JPanel(new GridLayout(4, 5, 10, 10)); // 4x5 = 20 spots
            for (int spot = 1; spot <= 20; spot++) {
                String spotId = "F" + floor + "S" + spot;
                JButton spotButton = new JButton(spotId);
                spotButton.setName(spotId);

                String status = DatabaseHelper.getParkingSpotStatus(spotId);
                updateParkingSpotButton(spotButton, status, spotId);

                spotButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DatabaseHelper.toggleSpotStatus(spotId);
                        String newStatus = DatabaseHelper.getParkingSpotStatus(spotId);
                        updateParkingSpotButton(spotButton, newStatus, spotId);
                        updateOccupancyCount();
                    }
                });

                floorPanel.add(spotButton);
            }
            tabbedPane.add("Floor " + floor, floorPanel);
        }

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setSize(600, 500);
        frame.setVisible(true);
    }

    // Update the button's text and color based on the parking spot status
    public static void updateParkingSpotButton(JButton spotButton, String status, String spotId) {
        if ("Free".equals(status)) {
            spotButton.setText(spotId + " - Free");
            spotButton.setBackground(Color.GREEN);
            spotButton.setForeground(Color.BLACK);
        } else if ("Occupied".equals(status)) {
            spotButton.setText(spotId + " - Occupied");
            spotButton.setBackground(Color.RED);
            spotButton.setForeground(Color.BLACK);
        } else {
            spotButton.setText(spotId + " - Unknown");
            spotButton.setBackground(Color.GRAY);
            spotButton.setForeground(Color.WHITE);
        }
    }

    // Updates the label with the current total number of occupied spots
    public static void updateOccupancyCount() {
        List<Map<String, String>> allSpots = DatabaseHelper.getAllParkingSpots();
        long occupiedCount = allSpots.stream()
                .filter(spot -> "Occupied".equalsIgnoreCase(spot.get("status")))
                .count();

        occupancyLabel.setText("Total Occupied Spots: " + occupiedCount + " / " + allSpots.size());
    }
}
