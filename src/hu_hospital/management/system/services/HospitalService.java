package hu_hospital.management.system.services;

import hu_hospital.management.system.models.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class HospitalService {
    private static HospitalService instance;
    private Map<String, Patient> patients;
    private Map<String, Doctor> doctors;
    private Map<String, LabTest> labTests;
    private Map<String, Prescription> prescriptions;
    private AtomicInteger queueCounter;
    private AtomicInteger patientIdCounter;
    private AtomicInteger testIdCounter;
    private AtomicInteger prescriptionIdCounter;
    
    private HospitalService() {
        patients = new HashMap<>();
        doctors = new HashMap<>();
        labTests = new HashMap<>();
        prescriptions = new HashMap<>();
        queueCounter = new AtomicInteger(1);
        patientIdCounter = new AtomicInteger(1001);
        testIdCounter = new AtomicInteger(1);
        prescriptionIdCounter = new AtomicInteger(1);
        initializeSampleData();
    }
    
    public static HospitalService getInstance() {
        if (instance == null) {
            instance = new HospitalService();
        }
        return instance;
    }
    
    private void initializeSampleData() {
        // Add sample doctors
        addDoctor(new Doctor("DOC001", "John", "Smith", "General Medicine", "123-456-7890", "john.smith@hospital.com"));
        addDoctor(new Doctor("DOC002", "Sarah", "Johnson", "Cardiology", "123-456-7891", "sarah.johnson@hospital.com"));
        addDoctor(new Doctor("DOC003", "Michael", "Brown", "Pediatrics", "123-456-7892", "michael.brown@hospital.com"));
    }
    
    // Patient Management
    public String registerPatient(Patient patient) {
        String patientId = "PAT" + String.format("%04d", patientIdCounter.getAndIncrement());
        patient.setPatientId(patientId);
        patient.setQueueNumber(queueCounter.getAndIncrement());
        patients.put(patientId, patient);
        return patientId;
    }
    
    public Patient findPatientById(String patientId) {
        return patients.get(patientId);
    }
    
    public Patient findPatientByPhone(String phoneNumber) {
        return patients.values().stream()
                .filter(p -> p.getPhoneNumber().equals(phoneNumber))
                .findFirst()
                .orElse(null);
    }
    
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patients.values());
    }
    
    public List<Patient> getWaitingPatients() {
        return patients.values().stream()
                .filter(p -> "WAITING".equals(p.getStatus()) || "REGISTERED".equals(p.getStatus()))
                .sorted(Comparator.comparingInt(Patient::getQueueNumber))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    // Doctor Management
    public void addDoctor(Doctor doctor) {
        doctors.put(doctor.getDoctorId(), doctor);
    }
    
    public List<Doctor> getAllDoctors() {
        return new ArrayList<>(doctors.values());
    }
    
    public Doctor findDoctorById(String doctorId) {
        return doctors.get(doctorId);
    }
    
    // Lab Test Management
    public String orderLabTest(LabTest labTest) {
        String testId = "TEST" + String.format("%04d", testIdCounter.getAndIncrement());
        labTest.setTestId(testId);
        labTests.put(testId, labTest);
        
        // Update patient status
        Patient patient = findPatientById(labTest.getPatientId());
        if (patient != null) {
            patient.setStatus("IN_LAB");
        }
        
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
            
            // Update patient status back to waiting for doctor
            Patient patient = findPatientById(test.getPatientId());
            if (patient != null) {
                patient.setStatus("WAITING");
            }
        }
    }
    
    // Prescription Management
    public String createPrescription(Prescription prescription) {
        String prescriptionId = "PRES" + String.format("%04d", prescriptionIdCounter.getAndIncrement());
        prescription.setPrescriptionId(prescriptionId);
        prescriptions.put(prescriptionId, prescription);
        
        // Update patient status
        Patient patient = findPatientById(prescription.getPatientId());
        if (patient != null) {
            patient.setStatus("PRESCRIPTION_READY");
        }
        
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
            
            // Update patient status to completed
            Patient patient = findPatientById(prescription.getPatientId());
            if (patient != null) {
                patient.setStatus("COMPLETED");
            }
        }
    }
    
    public Prescription findPrescriptionById(String prescriptionId) {
        return prescriptions.get(prescriptionId);
    }
    
    // Queue Management
    public void updatePatientStatus(String patientId, String status) {
        Patient patient = findPatientById(patientId);
        if (patient != null) {
            patient.setStatus(status);
        }
    }
}