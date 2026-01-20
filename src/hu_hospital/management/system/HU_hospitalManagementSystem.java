package hu_hospital.management.system;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class HU_hospitalManagementSystem extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        try {
            // Try to load the main FXML file with explicit path checking
            URL fxmlLocation = getClass().getResource("FXMLDocument.fxml");
            System.out.println("Looking for FXML at: " + fxmlLocation);
            
            if (fxmlLocation == null) {
                System.err.println("FXMLDocument.fxml not found, trying SimpleDashboard.fxml");
                fxmlLocation = getClass().getResource("SimpleDashboard.fxml");
            }
            
            if (fxmlLocation == null) {
                System.err.println("No FXML files found, launching programmatic version");
                launchProgrammaticVersion(stage);
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();
            
            Scene scene = new Scene(root, 1200, 800);
            
            stage.setTitle("HU Hospital Management System");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
            
        } catch (IOException e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback to programmatic version
            launchProgrammaticVersion(stage);
        }
    }
    
    private void launchProgrammaticVersion(Stage stage) {
        try {
            System.out.println("Launching standalone version...");
            StandaloneHospitalApp standaloneApp = new StandaloneHospitalApp();
            standaloneApp.start(stage);
        } catch (Exception e) {
            System.err.println("Failed to launch standalone version: " + e.getMessage());
            e.printStackTrace();
            
            // Final fallback to programmatic version
            try {
                System.out.println("Launching programmatic version...");
                ProgrammaticHospitalApp programmaticApp = new ProgrammaticHospitalApp();
                programmaticApp.start(stage);
            } catch (Exception e2) {
                System.err.println("Failed to launch programmatic version: " + e2.getMessage());
                e2.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
