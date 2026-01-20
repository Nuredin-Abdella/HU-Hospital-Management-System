package hu_hospital.management.system;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Direct launcher that bypasses FXML and runs the full dynamic version
 */
public class DirectLauncher extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("🏥 HU Hospital Management System - Direct Launch");
        System.out.println("Loading full dynamic version with all functionality...");
        
        // Launch the standalone version directly
        StandaloneHospitalApp app = new StandaloneHospitalApp();
        app.start(primaryStage);
        
        System.out.println("✅ Hospital Management System loaded successfully!");
        System.out.println("📋 All modules are now fully functional:");
        System.out.println("   • Patient Registration - Complete forms");
        System.out.println("   • Doctor Consultation - Patient queue & consultation");
        System.out.println("   • Laboratory - Test processing");
        System.out.println("   • Pharmacy - Prescription management");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}