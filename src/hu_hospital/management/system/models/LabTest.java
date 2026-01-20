package hu_hospital.management.system.models;

import java.time.LocalDateTime;

public class LabTest {
    private String testId;
    private String patientId;
    private String testType;
    private String description;
    private LocalDateTime orderDate;
    private LocalDateTime completionDate;
    private String results;
    private String status; // ORDERED, IN_PROGRESS, COMPLETED
    private String orderedBy; // Doctor ID
    
    public LabTest() {
        this.orderDate = LocalDateTime.now();
        this.status = "ORDERED";
    }
    
    public LabTest(String testId, String patientId, String testType, String description, String orderedBy) {
        this();
        this.testId = testId;
        this.patientId = patientId;
        this.testType = testType;
        this.description = description;
        this.orderedBy = orderedBy;
    }
    
    // Getters and Setters
    public String getTestId() { return testId; }
    public void setTestId(String testId) { this.testId = testId; }
    
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    
    public String getTestType() { return testType; }
    public void setTestType(String testType) { this.testType = testType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    
    public LocalDateTime getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDateTime completionDate) { this.completionDate = completionDate; }
    
    public String getResults() { return results; }
    public void setResults(String results) { this.results = results; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getOrderedBy() { return orderedBy; }
    public void setOrderedBy(String orderedBy) { this.orderedBy = orderedBy; }
}