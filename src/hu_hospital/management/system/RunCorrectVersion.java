package hu_hospital.management.system;

/**
 * This class ensures the correct version runs
 */
public class RunCorrectVersion {
    
    public static void main(String[] args) {
        System.out.println("🏥 Starting HU Hospital Management System");
        System.out.println("Running the correct dynamic version...");
        
        // Run the standalone version with full functionality
        StandaloneHospitalApp.main(args);
    }
}