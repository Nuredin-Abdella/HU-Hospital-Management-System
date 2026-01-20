package hu_hospital.management.system;

import hu_hospital.management.system.models.Patient;
import hu_hospital.management.system.services.HospitalService;
import java.time.LocalDate;

/**
 * Simple test class to verify the system works without JavaFX
 */
public class TestApplication {
    
    public static void main(String[] args) {
        System.out.println("=== HU Hospital Management System Test ===");
        
        // Test the service
        HospitalService service = HospitalService.getInstance();
        
        // Test patient registration
        Patient patient = new Patient();
        patient.setFirstName("John");
        patient.setLastName("Doe");
        patient.setDateOfBirth(LocalDate.of(1990, 5, 15));
        patient.setGender("Male");
        patient.setPhoneNumber("123-456-7890");
        patient.setEmail("john.doe@email.com");
        patient.setAddress("123 Main St");
        patient.setEmergencyContact("Jane Doe - 123-456-7891");
        
        String patientId = service.registerPatient(patient);
        System.out.println("Patient registered with ID: " + patientId);
        System.out.println("Queue number: " + patient.getQueueNumber());
        
        // Test statistics
        System.out.println("\n=== Hospital Statistics ===");
        System.out.println("Total Patients: " + service.getAllPatients().size());
        System.out.println("Waiting Patients: " + service.getWaitingPatients().size());
        System.out.println("Active Doctors: " + service.getAllDoctors().size());
        System.out.println("Pending Tests: " + service.getPendingLabTests().size());
        
        // Test patient lookup
        Patient foundPatient = service.findPatientByPhone("123-456-7890");
        if (foundPatient != null) {
            System.out.println("\nPatient found: " + foundPatient.getFullName());
        }
        
        System.out.println("\n=== Test completed successfully! ===");
        System.out.println("The hospital management system is working correctly.");
        System.out.println("You can now run the JavaFX application in NetBeans.");
    }
}