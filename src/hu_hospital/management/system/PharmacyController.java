package hu_hospital.management.system;

import hu_hospital.management.system.models.*;
import hu_hospital.management.system.services.HospitalService;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class PharmacyController implements Initializable {
    
    @FXML private TableView<Prescription> pendingPrescriptionsTable;
    @FXML private TableColumn<Prescription, String> prescriptionIdColumn;
    @FXML private TableColumn<Prescription, String> patientNameColumn;
    @FXML private TableColumn<Prescription, String> doctorNameColumn;
    @FXML private TableColumn<Prescription, String> prescriptionDateColumn;
    @FXML private TableColumn<Prescription, String> statusColumn;
    
    @FXML private Label selectedPrescriptionIdLabel;
    @FXML private Label selectedPatientLabel;
    @FXML private Label selectedDoctorLabel;
    @FXML private Label selectedDiagnosisLabel;
    @FXML private Label selectedDateLabel;
    
    @FXML private TableView<Medication> medicationsTable;
    @FXML private TableColumn<Medication, String> medicationNameColumn;
    @FXML private TableColumn<Medication, String> dosageColumn;
    @FXML private TableColumn<Medication, String> frequencyColumn;
    @FXML private TableColumn<Medication, Integer> durationColumn;
    @FXML private TableColumn<Medication, String> instructionsColumn;
    
    @FXML private TextArea pharmacistNotesArea;
    @FXML private Label pharmacyStatusLabel;
    
    private HospitalService hospitalService;
    private ObservableList<Prescription> pendingPrescriptions;
    private ObservableList<Medication> medications;
    private Prescription selectedPrescription;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hospitalService = HospitalService.getInstance();
        pendingPrescriptions = FXCollections.observableArrayList();
        medications = FXCollections.observableArrayList();
        
        setupPrescriptionsTable();
        setupMedicationsTable();
        refreshPendingPrescriptions();
        clearSelectedPrescription();
    }
    
    private void setupPrescriptionsTable() {
        prescriptionIdColumn.setCellValueFactory(new PropertyValueFactory<>("prescriptionId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Custom cell factories
        patientNameColumn.setCellValueFactory(cellData -> {
            Patient patient = hospitalService.findPatientById(cellData.getValue().getPatientId());
            return new javafx.beans.property.SimpleStringProperty(
                patient != null ? patient.getFullName() : "Unknown Patient"
            );
        });
        
        doctorNameColumn.setCellValueFactory(cellData -> {
            Doctor doctor = hospitalService.findDoctorById(cellData.getValue().getDoctorId());
            return new javafx.beans.property.SimpleStringProperty(
                doctor != null ? doctor.getFullName() : "Unknown Doctor"
            );
        });
        
        prescriptionDateColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd HH:mm");
            return new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getPrescriptionDate().format(formatter)
            );
        });
        
        pendingPrescriptionsTable.setItems(pendingPrescriptions);
        
        // Enable row selection
        pendingPrescriptionsTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    loadPrescriptionDetails(newSelection);
                }
            }
        );
    }
    
    private void setupMedicationsTable() {
        medicationNameColumn.setCellValueFactory(new PropertyValueFactory<>("medicationName"));
        dosageColumn.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        frequencyColumn.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        instructionsColumn.setCellValueFactory(new PropertyValueFactory<>("instructions"));
        
        medicationsTable.setItems(medications);
    }
    
    private void refreshPendingPrescriptions() {
        pendingPrescriptions.clear();
        List<Prescription> prescriptions = hospitalService.getPendingPrescriptions();
        pendingPrescriptions.addAll(prescriptions);
    }
    
    private void loadPrescriptionDetails(Prescription prescription) {
        selectedPrescription = prescription;
        
        selectedPrescriptionIdLabel.setText(prescription.getPrescriptionId());
        selectedDiagnosisLabel.setText(prescription.getDiagnosis());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        selectedDateLabel.setText(prescription.getPrescriptionDate().format(formatter));
        
        Patient patient = hospitalService.findPatientById(prescription.getPatientId());
        selectedPatientLabel.setText(patient != null ? patient.getFullName() : "Unknown Patient");
        
        Doctor doctor = hospitalService.findDoctorById(prescription.getDoctorId());
        selectedDoctorLabel.setText(doctor != null ? doctor.getFullName() : "Unknown Doctor");
        
        // Load medications
        medications.clear();
        medications.addAll(prescription.getMedications());
        
        // Clear notes
        pharmacistNotesArea.clear();
    }
    
    private void clearSelectedPrescription() {
        selectedPrescription = null;
        selectedPrescriptionIdLabel.setText("No prescription selected");
        selectedPatientLabel.setText("");
        selectedDoctorLabel.setText("");
        selectedDiagnosisLabel.setText("");
        selectedDateLabel.setText("");
        
        medications.clear();
        pharmacistNotesArea.clear();
    }
    
    @FXML
    private void viewPrescription(ActionEvent event) {
        if (selectedPrescription == null) {
            showStatus("Please select a prescription first", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        // Show detailed prescription view
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Prescription Details");
        alert.setHeaderText("Prescription ID: " + selectedPrescription.getPrescriptionId());
        
        Patient patient = hospitalService.findPatientById(selectedPrescription.getPatientId());
        Doctor doctor = hospitalService.findDoctorById(selectedPrescription.getDoctorId());
        
        StringBuilder content = new StringBuilder();
        content.append("Patient: ").append(patient != null ? patient.getFullName() : "Unknown").append("\n");
        content.append("Doctor: ").append(doctor != null ? doctor.getFullName() : "Unknown").append("\n");
        content.append("Diagnosis: ").append(selectedPrescription.getDiagnosis()).append("\n\n");
        content.append("Medications:\n");
        
        for (Medication med : selectedPrescription.getMedications()) {
            content.append("• ").append(med.getMedicationName())
                   .append(" - ").append(med.getDosage())
                   .append(" (").append(med.getFrequency())
                   .append(" for ").append(med.getDuration()).append(" days)\n");
            if (med.getInstructions() != null && !med.getInstructions().isEmpty()) {
                content.append("  Instructions: ").append(med.getInstructions()).append("\n");
            }
        }
        
        alert.setContentText(content.toString());
        alert.showAndWait();
    }
    
    @FXML
    private void dispenseMedication(ActionEvent event) {
        if (selectedPrescription == null) {
            showStatus("Please select a prescription first", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        // Show confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Dispense Medication");
        alert.setHeaderText("Confirm Dispensing");
        alert.setContentText("Are you sure you want to dispense all medications for this prescription?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            selectedPrescription.setStatus("DISPENSED");
            showStatus("Medications dispensed for prescription " + selectedPrescription.getPrescriptionId(), 
                      "-fx-text-fill: #2ecc71;");
        }
    }
    
    @FXML
    private void checkInventory(ActionEvent event) {
        if (selectedPrescription == null) {
            showStatus("Please select a prescription first", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        // Simulate inventory check
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Inventory Check");
        alert.setHeaderText("Medication Availability");
        
        StringBuilder content = new StringBuilder();
        content.append("Inventory Status:\n\n");
        
        for (Medication med : selectedPrescription.getMedications()) {
            // Simulate random availability
            boolean available = Math.random() > 0.2; // 80% chance of being available
            content.append("• ").append(med.getMedicationName())
                   .append(": ").append(available ? "✓ Available" : "✗ Out of Stock").append("\n");
        }
        
        alert.setContentText(content.toString());
        alert.showAndWait();
        
        showStatus("Inventory check completed", "-fx-text-fill: #3498db;");
    }
    
    @FXML
    private void printLabel(ActionEvent event) {
        if (selectedPrescription == null) {
            showStatus("Please select a prescription first", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        // Show print preview
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Medication Labels");
        alert.setHeaderText("Medication Labels Preview");
        
        Patient patient = hospitalService.findPatientById(selectedPrescription.getPatientId());
        
        StringBuilder content = new StringBuilder();
        content.append("Patient: ").append(patient != null ? patient.getFullName() : "Unknown").append("\n");
        content.append("Prescription ID: ").append(selectedPrescription.getPrescriptionId()).append("\n\n");
        
        for (Medication med : selectedPrescription.getMedications()) {
            content.append("MEDICATION LABEL\n");
            content.append("================\n");
            content.append("Medication: ").append(med.getMedicationName()).append("\n");
            content.append("Dosage: ").append(med.getDosage()).append("\n");
            content.append("Frequency: ").append(med.getFrequency()).append("\n");
            content.append("Duration: ").append(med.getDuration()).append(" days\n");
            if (med.getInstructions() != null && !med.getInstructions().isEmpty()) {
                content.append("Instructions: ").append(med.getInstructions()).append("\n");
            }
            content.append("\n");
        }
        
        alert.setContentText(content.toString());
        alert.showAndWait();
        
        showStatus("Labels printed for prescription " + selectedPrescription.getPrescriptionId(), 
                  "-fx-text-fill: #95a5a6;");
    }
    
    @FXML
    private void completePrescription(ActionEvent event) {
        if (selectedPrescription == null) {
            showStatus("Please select a prescription first", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        hospitalService.dispensePrescription(selectedPrescription.getPrescriptionId());
        
        showStatus("Prescription " + selectedPrescription.getPrescriptionId() + " completed successfully", 
                  "-fx-text-fill: #2ecc71;");
        
        refreshPendingPrescriptions();
        clearSelectedPrescription();
    }
    
    private void showStatus(String message, String style) {
        pharmacyStatusLabel.setText(message);
        pharmacyStatusLabel.setStyle(style + " -fx-font-weight: bold;");
    }
}