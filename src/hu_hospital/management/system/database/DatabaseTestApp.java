package hu_hospital.management.system.database;

import hu_hospital.management.system.models.Patient;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Test application for database connectivity and operations
 */
public class DatabaseTestApp {
    
    public static void main(String[] args) {
        System.out.println("🏥 HU Hospital Management System - Database Test");
        System.out.println("================================================");
        
        // Print connection info
        DatabaseConfig.printConnectionInfo();
        System.out.println();
        
        // Test connection
        System.out.println("1. Testing database connection...");
        if (DatabaseConfig.testConnection()) {
            System.out.println("✅ Database connection successful!");
        } else {
            System.out.println("❌ Database connection failed!");
            System.out.println("Please check your PostgreSQL installation and configuration.");
            return;
        }
        
        System.out.println();
        
        // Test Patient DAO operations
        PatientDAO patientDAO = new PatientDAO();
        
        try {
            // Test 1: Get all patients
            System.out.println("2. Testing patient retrieval...");
            List<Patient> allPatients = patientDAO.getAllPatients();
            System.out.println("✅ Found " + allPatients.size() + " patients in database");
            
            // Display first few patients
            for (int i = 0; i < Math.min(3, allPatients.size()); i++) {
                Patient p = allPatients.get(i);
                System.out.println("   - " + p.getPatientId() + ": " + p.getFullName() + 
                                 " (Status: " + p.getStatus() + ")");
            }
            
            System.out.println();
            
            // Test 2: Get waiting patients
            System.out.println("3. Testing patient queue...");
            List<Patient> waitingPatients = patientDAO.getWaitingPatients();
            System.out.println("✅ Found " + waitingPatients.size() + " patients in queue");
            
            for (Patient p : waitingPatients) {
                System.out.println("   Queue #" + p.getQueueNumber() + ": " + p.getFullName() + 
                                 " (Status: " + p.getStatus() + ")");
            }
            
            System.out.println();
            
            // Test 3: Insert new patient
            System.out.println("4. Testing patient registration...");
            Patient newPatient = new Patient();
            newPatient.setFirstName("Test");
            newPatient.setLastName("Patient");
            newPatient.setDateOfBirth(LocalDate.of(1990, 1, 1));
            newPatient.setGender("Male");
            newPatient.setPhoneNumber("555-TEST-" + System.currentTimeMillis());
            newPatient.setEmail("test.patient@email.com");
            newPatient.setAddress("123 Test Street");
            newPatient.setEmergencyContact("Emergency Contact");
            newPatient.setMedicalHistory("No known allergies");
            newPatient.setStatus("WAITING");
            
            String patientId = patientDAO.insertPatient(newPatient);
            System.out.println("✅ New patient registered with ID: " + patientId);
            System.out.println("   Queue Number: " + newPatient.getQueueNumber());
            
            System.out.println();
            
            // Test 4: Find patient by ID
            System.out.println("5. Testing patient lookup...");
            Patient foundPatient = patientDAO.findPatientById(patientId);
            if (foundPatient != null) {
                System.out.println("✅ Patient found: " + foundPatient.getFullName());
                System.out.println("   Phone: " + foundPatient.getPhoneNumber());
                System.out.println("   Status: " + foundPatient.getStatus());
            } else {
                System.out.println("❌ Patient not found");
            }
            
            System.out.println();
            
            // Test 5: Update patient status
            System.out.println("6. Testing status update...");
            patientDAO.updatePatientStatus(patientId, "WITH_DOCTOR");
            Patient updatedPatient = patientDAO.findPatientById(patientId);
            System.out.println("✅ Patient status updated to: " + updatedPatient.getStatus());
            
            System.out.println();
            
            // Test 6: Find patient by phone
            System.out.println("7. Testing phone lookup...");
            Patient phonePatient = patientDAO.findPatientByPhone(newPatient.getPhoneNumber());
            if (phonePatient != null) {
                System.out.println("✅ Patient found by phone: " + phonePatient.getFullName());
            } else {
                System.out.println("❌ Patient not found by phone");
            }
            
            System.out.println();
            System.out.println("🎉 All database tests completed successfully!");
            System.out.println("Your database is ready for the hospital management system.");
            
        } catch (SQLException e) {
            System.err.println("❌ Database operation failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close connection
            DatabaseConfig.closeConnection();
        }
    }
}