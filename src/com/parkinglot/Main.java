import com.parkinglot.db.DatabaseHelper;
import com.parkinglot.gui.ParkingLotGUI;

public class Main {
    public static void main(String[] args) {
        // Step 1: Ensure database and table are set up
        DatabaseHelper.initializeDatabase();

        // Step 2: Start the GUI
        javax.swing.SwingUtilities.invokeLater(ParkingLotGUI::createAndShowGUI);
    }
}
