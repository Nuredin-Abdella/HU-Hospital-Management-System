package hu_hospital.management.system.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Prescription {
    private String prescriptionId;
    private String patientId;
    private String doctorId;
    private LocalDateTime prescriptionDate;
    private String diagnosis;
    private List<Medication> medications;
    private String instructions;
    private String status; // PRESCRIBED, DISPENSED, COMPLETED
    
    public Prescription() {
        this.prescriptionDate = LocalDateTime.now();
        this.medications = new ArrayList<>();
        this.status = "PRESCRIBED";
    }
    
    public Prescription(String prescriptionId, String patientId, String doctorId, String diagnosis) {
        this();
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.diagnosis = diagnosis;
    }
    
    // Getters and Setters
    public String getPrescriptionId() { return prescriptionId; }
    public void setPrescriptionId(String prescriptionId) { this.prescriptionId = prescriptionId; }
    
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    
    public LocalDateTime getPrescriptionDate() { return prescriptionDate; }
    public void setPrescriptionDate(LocalDateTime prescriptionDate) { this.prescriptionDate = prescriptionDate; }
    
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    
    public List<Medication> getMedications() { return medications; }
    public void setMedications(List<Medication> medications) { this.medications = medications; }
    
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public void addMedication(Medication medication) {
        this.medications.add(medication);
    }
}