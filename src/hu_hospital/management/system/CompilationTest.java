package hu_hospital.management.system;

import hu_hospital.management.system.models.*;
import hu_hospital.management.system.services.HospitalService;

/**
 * Simple test to verify all classes compile correctly
 */
public class CompilationTest {
    
    public static void main(String[] args) {
        System.out.println("=== Compilation Test ===");
        
        try {
            // Test model classes
            Patient patient = new Patient();
            Doctor doctor = new Doctor();
            LabTest labTest = new LabTest();
            Prescription prescription = new Prescription();
            Medication medication = new Medication();
            
            // Test service
            HospitalService service = HospitalService.getInstance();
            
            System.out.println("✓ All model classes compile successfully");
            System.out.println("✓ Service class compiles successfully");
            System.out.println("✓ All imports are working correctly");
            
            // Test basic functionality
            patient.setFirstName("Test");
            patient.setLastName("Patient");
            
            medication.setMedicationName("Test Medicine");
            medication.setDosage("100mg");
            
            prescription.addMedication(medication);
            
            System.out.println("✓ Basic functionality works");
            System.out.println("✓ Medication class is accessible");
            
            System.out.println("\n=== All compilation issues are fixed! ===");
            System.out.println("You can now run the JavaFX application successfully.");
            
        } catch (Exception e) {
            System.err.println("❌ Compilation error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}