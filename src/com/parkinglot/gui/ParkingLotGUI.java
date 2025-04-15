package com.parkinglot.gui;

import com.parkinglot.db.DatabaseHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ParkingLotGUI {

    private static final int TOTAL_FLOORS = 3;
    private static final int SPOTS_PER_FLOOR = 20;

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Parking Lot Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);

        JTabbedPane tabbedPane = new JTabbedPane();

        for (int floor = 1; floor <= TOTAL_FLOORS; floor++) {
            JPanel floorPanel = new JPanel(new GridLayout(4, 5, 10, 10)); // 4 rows x 5 columns
            floorPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            for (int spot = 1; spot <= SPOTS_PER_FLOOR; spot++) {
                String spotId = "F" + floor + "S" + spot;
                JButton spotButton = new JButton(spotId);
                spotButton.setName(spotId);

                String status = DatabaseHelper.getParkingSpotStatus(spotId);
                updateParkingSpotButton(spotButton, status);

                spotButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DatabaseHelper.toggleSpotStatus(spotId);
                        String newStatus = DatabaseHelper.getParkingSpotStatus(spotId);
                        updateParkingSpotButton(spotButton, newStatus);
                    }
                });

                floorPanel.add(spotButton);
            }

            tabbedPane.add("Floor " + floor, floorPanel);
        }

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    // Update the button text and background color based on status
    public static void updateParkingSpotButton(JButton spotButton, String status) {
        String spotId = spotButton.getName(); // e.g., F1S3

        if ("Free".equalsIgnoreCase(status)) {
            spotButton.setText(spotId + " - Free");
            spotButton.setBackground(Color.GREEN);
            spotButton.setForeground(Color.BLACK);
        } else if ("Occupied".equalsIgnoreCase(status)) {
            spotButton.setText(spotId + " - Occupied");
            spotButton.setBackground(Color.RED);
            spotButton.setForeground(Color.BLACK);
        } else {
            spotButton.setText(spotId + " - Unknown");
            spotButton.setBackground(Color.GRAY);
            spotButton.setForeground(Color.WHITE);
        }
    }
}
