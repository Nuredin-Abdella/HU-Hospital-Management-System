package hu_hospital.management.system.models;

public class Medication {
    private String medicationName;
    private String dosage;
    private String frequency;
    private int duration; // in days
    private String instructions;
    
    public Medication() {
    }
    
    public Medication(String medicationName, String dosage, String frequency, int duration, String instructions) {
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.frequency = frequency;
        this.duration = duration;
        this.instructions = instructions;
    }
    
    // Getters and Setters
    public String getMedicationName() { return medicationName; }
    public void setMedicationName(String medicationName) { this.medicationName = medicationName; }
    
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    
    @Override
    public String toString() {
        return medicationName + " - " + dosage + " (" + frequency + " for " + duration + " days)";
    }
}