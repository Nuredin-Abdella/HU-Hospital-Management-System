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

public class LaboratoryController implements Initializable {
    
    @FXML private TableView<LabTest> pendingTestsTable;
    @FXML private TableColumn<LabTest, String> testIdColumn;
    @FXML private TableColumn<LabTest, String> patientNameColumn;
    @FXML private TableColumn<LabTest, String> testTypeColumn;
    @FXML private TableColumn<LabTest, String> orderDateColumn;
    @FXML private TableColumn<LabTest, String> statusColumn;
    
    @FXML private Label selectedTestIdLabel;
    @FXML private Label selectedPatientLabel;
    @FXML private Label selectedTestTypeLabel;
    @FXML private Label selectedDescriptionLabel;
    @FXML private Label selectedDoctorLabel;
    
    @FXML private TextArea resultsArea;
    @FXML private Label labStatusLabel;
    
    private HospitalService hospitalService;
    private ObservableList<LabTest> pendingTests;
    private LabTest selectedTest;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hospitalService = HospitalService.getInstance();
        pendingTests = FXCollections.observableArrayList();
        
        setupPendingTestsTable();
        refreshPendingTests();
        clearSelectedTest();
    }
    
    private void setupPendingTestsTable() {
        testIdColumn.setCellValueFactory(new PropertyValueFactory<>("testId"));
        testTypeColumn.setCellValueFactory(new PropertyValueFactory<>("testType"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Custom cell factories for patient name and order date
        patientNameColumn.setCellValueFactory(cellData -> {
            Patient patient = hospitalService.findPatientById(cellData.getValue().getPatientId());
            return new javafx.beans.property.SimpleStringProperty(
                patient != null ? patient.getFullName() : "Unknown Patient"
            );
        });
        
        orderDateColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd HH:mm");
            return new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getOrderDate().format(formatter)
            );
        });
        
        pendingTestsTable.setItems(pendingTests);
        
        // Enable row selection
        pendingTestsTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    loadTestDetails(newSelection);
                }
            }
        );
    }
    
    private void refreshPendingTests() {
        pendingTests.clear();
        List<LabTest> tests = hospitalService.getPendingLabTests();
        pendingTests.addAll(tests);
    }
    
    private void loadTestDetails(LabTest test) {
        selectedTest = test;
        
        selectedTestIdLabel.setText(test.getTestId());
        selectedTestTypeLabel.setText(test.getTestType());
        selectedDescriptionLabel.setText(test.getDescription() != null ? test.getDescription() : "No description");
        
        Patient patient = hospitalService.findPatientById(test.getPatientId());
        selectedPatientLabel.setText(patient != null ? patient.getFullName() : "Unknown Patient");
        
        Doctor doctor = hospitalService.findDoctorById(test.getOrderedBy());
        selectedDoctorLabel.setText(doctor != null ? doctor.getFullName() : "Unknown Doctor");
        
        // Load existing results if any
        resultsArea.setText(test.getResults() != null ? test.getResults() : "");
    }
    
    private void clearSelectedTest() {
        selectedTest = null;
        selectedTestIdLabel.setText("No test selected");
        selectedPatientLabel.setText("");
        selectedTestTypeLabel.setText("");
        selectedDescriptionLabel.setText("");
        selectedDoctorLabel.setText("");
        resultsArea.clear();
    }
    
    @FXML
    private void startTest(ActionEvent event) {
        if (selectedTest == null) {
            showStatus("Please select a test first", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        if (!"ORDERED".equals(selectedTest.getStatus())) {
            showStatus("Test is already in progress or completed", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        selectedTest.setStatus("IN_PROGRESS");
        showStatus("Test " + selectedTest.getTestId() + " started", "-fx-text-fill: #f39c12;");
        refreshPendingTests();
    }
    
    @FXML
    private void completeTest(ActionEvent event) {
        if (selectedTest == null) {
            showStatus("Please select a test first", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        if (resultsArea.getText().trim().isEmpty()) {
            showStatus("Please enter test results first", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        hospitalService.completeLabTest(selectedTest.getTestId(), resultsArea.getText().trim());
        
        showStatus("Test " + selectedTest.getTestId() + " completed successfully", "-fx-text-fill: #2ecc71;");
        refreshPendingTests();
        clearSelectedTest();
    }
    
    @FXML
    private void saveResults(ActionEvent event) {
        if (selectedTest == null) {
            showStatus("Please select a test first", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        selectedTest.setResults(resultsArea.getText().trim());
        showStatus("Results saved for test " + selectedTest.getTestId(), "-fx-text-fill: #3498db;");
    }
    
    @FXML
    private void printResults(ActionEvent event) {
        if (selectedTest == null) {
            showStatus("Please select a test first", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        if (resultsArea.getText().trim().isEmpty()) {
            showStatus("No results to print", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        // Create a simple print dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Results");
        alert.setHeaderText("Lab Test Results - " + selectedTest.getTestId());
        
        Patient patient = hospitalService.findPatientById(selectedTest.getPatientId());
        String patientName = patient != null ? patient.getFullName() : "Unknown Patient";
        
        String printContent = String.format(
            "Patient: %s\nTest Type: %s\nTest ID: %s\nOrder Date: %s\nResults:\n%s",
            patientName,
            selectedTest.getTestType(),
            selectedTest.getTestId(),
            selectedTest.getOrderDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")),
            resultsArea.getText()
        );
        
        alert.setContentText(printContent);
        alert.showAndWait();
        
        showStatus("Results printed for test " + selectedTest.getTestId(), "-fx-text-fill: #95a5a6;");
    }
    
    @FXML
    private void sendToDoctor(ActionEvent event) {
        if (selectedTest == null) {
            showStatus("Please select a test first", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        if (!"COMPLETED".equals(selectedTest.getStatus())) {
            showStatus("Test must be completed before sending to doctor", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        // In a real system, this would send notification to doctor
        showStatus("Test results sent to doctor for " + selectedTest.getTestId(), "-fx-text-fill: #2ecc71;");
    }
    
    private void showStatus(String message, String style) {
        labStatusLabel.setText(message);
        labStatusLabel.setStyle(style + " -fx-font-weight: bold;");
    }
}