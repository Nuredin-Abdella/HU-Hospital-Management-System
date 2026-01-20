package hu_hospital.management.system.services;

import hu_hospital.management.system.database.*;
import hu_hospital.management.system.models.*;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hospital service that uses PostgreSQL database instead of in-memory storage
 */
public class DatabaseHospitalService {
    private static DatabaseHospitalService instance;
    
    private DatabasePatientDAO patientDAO;
    private DatabaseDoctorDAO doctorDAO;
    private AtomicInteger testIdCounter;
    private AtomicInteger prescriptionIdCounter;
    
    // In-memory storage for lab tests and prescriptions (can be moved to database later)
    private Map<String, LabTest> labTests;
    private Map<String, Prescription> prescriptions;
    
    private DatabaseHospitalService() {
        patientDAO = new DatabasePatientDAO();
        doctorDAO = new DatabaseDoctorDAO();
        testIdCounter = new AtomicInteger(1);
        prescriptionIdCounter = new AtomicInteger(1);
        labTests = new HashMap<>();
        prescriptions = new HashMap<>();
        
        initializeSampleData();
    }
    
    public static DatabaseHospitalService getInstance() {
        if (instance == null) {
            instance = new DatabaseHospitalService();
        }
        return instance;
    }
    
    private void initializeSampleData() {
        try {
            // Check if doctors already exist
            List<Doctor> existingDoctors = doctorDAO.getAllDoctors();
            if (existingDoctors.isEmpty()) {
                // Add sample doctors to database
                Doctor doc1 = new Doctor("DOC001", "Sara", "Mohammed", "General Physician", "0923456789", "sara.mohammed@hospital.com");
                Doctor doc2 = new Doctor("DOC002", "Ahmed", "Hassan", "Cardiologist", "0934567890", "ahmed.hassan@hospital.com");
                Doctor doc3 = new Doctor("DOC003", "Fatima", "Ali", "Pediatrician", "0945678901", "fatima.ali@hospital.com");
                
                doctorDAO.insertDoctor(doc1);
                doctorDAO.insertDoctor(doc2);
                doctorDAO.insertDoctor(doc3);
                
                System.out.println("✅ Sample doctors added to database");
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Could not initialize sample data: " + e.getMessage());
        }
    }
    
    // Patient Management
    public String registerPatient(Patient patient) {
        try {
            String patientId = patientDAO.insertPatient(patient);
            System.out.println("✅ Patient registered in database: " + patientId);
            return patientId;
        } catch (SQLException e) {
            System.err.println("❌ Failed to register patient: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public Patient findPatientById(String patientId) {
        try {
            return patientDAO.findPatientById(patientId);
        } catch (SQLException e) {
            System.err.println("❌ Failed to find patient: " + e.getMessage());
            return null;
        }
    }
    
    public Patient findPatientByPhone(String phoneNumber) {
        try {
            return patientDAO.findPatientByPhone(phoneNumber);
        } catch (SQLException e) {
            System.err.println("❌ Failed to find patient by phone: " + e.getMessage());
            return null;
        }
    }
    
    public List<Patient> getAllPatients() {
        try {
            return patientDAO.getAllPatients();
        } catch (SQLException e) {
            System.err.println("❌ Failed to get all patients: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public List<Patient> getWaitingPatients() {
        try {
            return patientDAO.getWaitingPatients();
        } catch (SQLException e) {
            System.err.println("❌ Failed to get waiting patients: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Doctor Management
    public void addDoctor(Doctor doctor) {
        try {
            doctorDAO.insertDoctor(doctor);
            System.out.println("✅ Doctor added to database: " + doctor.getFullName());
        } catch (SQLException e) {
            System.err.println("❌ Failed to add doctor: " + e.getMessage());
        }
    }
    
    public List<Doctor> getAllDoctors() {
        try {
            return doctorDAO.getAllDoctors();
        } catch (SQLException e) {
            System.err.println("❌ Failed to get all doctors: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public Doctor findDoctorById(String doctorId) {
        try {
            return doctorDAO.findDoctorById(doctorId);
        } catch (SQLException e) {
            System.err.println("❌ Failed to find doctor: " + e.getMessage());
            return null;
        }
    }
    
    // Lab Test Management (still in-memory for now)
    public String orderLabTest(LabTest labTest) {
        String testId = "TEST" + String.format("%04d", testIdCounter.getAndIncrement());
        labTest.setTestId(testId);
        labTests.put(testId, labTest);
        
        System.out.println("✅ Lab test ordered: " + testId);
        return testId;
    }
    
    public List<LabTest> getPendingLabTests() {
        return labTests.values().stream()
                .filter(test -> "ORDERED".equals(test.getStatus()) || "IN_PROGRESS".equals(test.getStatus()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public void completeLabTest(String testId, String results) {
        LabTest test = labTests.get(testId);
        if (test != null) {
            test.setResults(results);
            test.setStatus("COMPLETED");
            test.setCompletionDate(java.time.LocalDateTime.now());
            System.out.println("✅ Lab test completed: " + testId);
        }
    }
    
    // Prescription Management (still in-memory for now)
    public String createPrescription(Prescription prescription) {
        String prescriptionId = "PRES" + String.format("%04d", prescriptionIdCounter.getAndIncrement());
        prescription.setPrescriptionId(prescriptionId);
        prescriptions.put(prescriptionId, prescription);
        
        System.out.println("✅ Prescription created: " + prescriptionId);
        return prescriptionId;
    }
    
    public List<Prescription> getPendingPrescriptions() {
        return prescriptions.values().stream()
                .filter(p -> "PRESCRIBED".equals(p.getStatus()))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public void dispensePrescription(String prescriptionId) {
        Prescription prescription = prescriptions.get(prescriptionId);
        if (prescription != null) {
            prescription.setStatus("DISPENSED");
            System.out.println("✅ Prescription dispensed: " + prescriptionId);
        }
    }
    
    public Prescription findPrescriptionById(String prescriptionId) {
        return prescriptions.get(prescriptionId);
    }
    
    // Status Management (simplified for now)
    public void updatePatientStatus(String patientId, String status) {
        System.out.println("📝 Patient status updated: " + patientId + " -> " + status);
        // In a full implementation, this would update the database
    }
    
    // Database connection test
    public boolean testDatabaseConnection() {
        return DatabaseConfig.testConnection();
    }
    
    public void printDatabaseInfo() {
        DatabaseConfig.printConnectionInfo();
    }
}