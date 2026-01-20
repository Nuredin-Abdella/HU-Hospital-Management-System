package hu_hospital.management.system;

/**
 * Quick launcher that tries different versions until one works
 */
public class QuickLauncher {
    
    public static void main(String[] args) {
        System.out.println("🏥 HU Hospital Management System - Quick Launcher");
        System.out.println("================================================");
        
        // First, test if the core system works
        System.out.println("1. Testing core system...");
        try {
            TestApplication.main(args);
            System.out.println("✅ Core system is working!");
        } catch (Exception e) {
            System.err.println("❌ Core system failed: " + e.getMessage());
            return;
        }
        
        System.out.println("\n2. Launching JavaFX application...");
        
        // Try standalone version first (most likely to work)
        try {
            System.out.println("Trying standalone version...");
            StandaloneHospitalApp.main(args);
        } catch (Exception e) {
            System.err.println("Standalone version failed: " + e.getMessage());
            
            // Try programmatic version
            try {
                System.out.println("Trying programmatic version...");
                ProgrammaticHospitalApp.main(args);
            } catch (Exception e2) {
                System.err.println("Programmatic version failed: " + e2.getMessage());
                
                // Try simple version
                try {
                    System.out.println("Trying simple version...");
                    SimpleHospitalApp.main(args);
                } catch (Exception e3) {
                    System.err.println("Simple version failed: " + e3.getMessage());
                    
                    // Last resort: try main version
                    try {
                        System.out.println("Trying main version...");
                        HU_hospitalManagementSystem.main(args);
                    } catch (Exception e4) {
                        System.err.println("All versions failed. Please check JavaFX installation.");
                        System.err.println("Main error: " + e4.getMessage());
                    }
                }
            }
        }
    }
}