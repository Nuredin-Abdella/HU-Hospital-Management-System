package hu_hospital.management.system;

import hu_hospital.management.system.models.*;
import hu_hospital.management.system.services.HospitalService;
import java.net.URL;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class DoctorConsultationController implements Initializable {
    
    @FXML private TableView<Patient> waitingPatientsTable;
    @FXML private TableColumn<Patient, Integer> queueNumColumn;
    @FXML private TableColumn<Patient, String> patientNameColumn;
    @FXML private TableColumn<Patient, String> patientIdColumn;
    @FXML private TableColumn<Patient, String> statusColumn;
    @FXML private Button callNextPatientBtn;
    
    @FXML private Label currentPatientNameLabel;
    @FXML private Label currentPatientAgeLabel;
    @FXML private Label currentPatientPhoneLabel;
    @FXML private Label currentPatientHistoryLabel;
    
    @FXML private TextArea complaintArea;
    @FXML private TextArea symptomsArea;
    @FXML private TextArea examinationArea;
    @FXML private TextArea diagnosisArea;
    @FXML private Label consultationStatusLabel;
    
    private HospitalService hospitalService;
    private ObservableList<Patient> waitingPatients;
    private Patient currentPatient;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hospitalService = HospitalService.getInstance();
        waitingPatients = FXCollections.observableArrayList();
        
        setupWaitingPatientsTable();
        refreshWaitingPatients();
        clearCurrentPatient();
    }
    
    private void setupWaitingPatientsTable() {
        queueNumColumn.setCellValueFactory(new PropertyValueFactory<>("queueNumber"));
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        waitingPatientsTable.setItems(waitingPatients);
        
        // Enable row selection
        waitingPatientsTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    loadPatientDetails(newSelection);
                }
            }
        );
    }
    
    private void refreshWaitingPatients() {
        waitingPatients.clear();
        List<Patient> waiting = hospitalService.getWaitingPatients();
        waitingPatients.addAll(waiting);
    }
    
    @FXML
    private void callNextPatient(ActionEvent event) {
        if (waitingPatients.isEmpty()) {
            showStatus("No patients waiting", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        Patient nextPatient = waitingPatients.get(0);
        loadPatientDetails(nextPatient);
        
        // Update patient status
        hospitalService.updatePatientStatus(nextPatient.getPatientId(), "WITH_DOCTOR");
        refreshWaitingPatients();
        
        showStatus("Patient " + nextPatient.getFullName() + " called for consultation", "-fx-text-fill: #2ecc71;");
    }
    
    private void loadPatientDetails(Patient patient) {
        currentPatient = patient;
        
        currentPatientNameLabel.setText(patient.getFullName());
        
        // Calculate age
        if (patient.getDateOfBirth() != null) {
            int age = Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears();
            currentPatientAgeLabel.setText(age + " years");
        } else {
            currentPatientAgeLabel.setText("N/A");
        }
        
        currentPatientPhoneLabel.setText(patient.getPhoneNumber());
        currentPatientHistoryLabel.setText(patient.getMedicalHistory() != null ? 
            patient.getMedicalHistory() : "No medical history available");
        
        // Clear consultation fields
        complaintArea.clear();
        symptomsArea.clear();
        examinationArea.clear();
        diagnosisArea.clear();
    }
    
    private void clearCurrentPatient() {
        currentPatient = null;
        currentPatientNameLabel.setText("No patient selected");
        currentPatientAgeLabel.setText("");
        currentPatientPhoneLabel.setText("");
        currentPatientHistoryLabel.setText("");
        
        complaintArea.clear();
        symptomsArea.clear();
        examinationArea.clear();
        diagnosisArea.clear();
    }
    
    @FXML
    private void sendToLab(ActionEvent event) {
        if (currentPatient == null) {
            showStatus("Please select a patient first", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        // Show lab test selection dialog
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Order Lab Test");
        dialog.setHeaderText("Select lab test for " + currentPatient.getFullName());
        
        ComboBox<String> testTypeCombo = new ComboBox<>();
        testTypeCombo.getItems().addAll(
            "Complete Blood Count (CBC)",
            "Blood Chemistry Panel",
            "Urinalysis",
            "X-Ray Chest",
            "ECG",
            "Blood Sugar Test",
            "Lipid Profile",
            "Liver Function Test"
        );
        testTypeCombo.setValue("Complete Blood Count (CBC)");
        
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Additional instructions or notes...");
        descriptionArea.setPrefRowCount(3);
        
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Test Type:"), testTypeCombo,
            new Label("Description/Instructions:"), descriptionArea
        );
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return testTypeCombo.getValue();
            }
            return null;
        });
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            // Create lab test
            LabTest labTest = new LabTest();
            labTest.setPatientId(currentPatient.getPatientId());
            labTest.setTestType(result.get());
            labTest.setDescription(descriptionArea.getText());
            labTest.setOrderedBy("DOC001"); // Default doctor for now
            
            String testId = hospitalService.orderLabTest(labTest);
            
            showStatus("Lab test ordered successfully. Test ID: " + testId, "-fx-text-fill: #2ecc71;");
            refreshWaitingPatients();
            clearCurrentPatient();
        }
    }
    
    @FXML
    private void createPrescription(ActionEvent event) {
        if (currentPatient == null) {
            showStatus("Please select a patient first", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        if (diagnosisArea.getText().trim().isEmpty()) {
            showStatus("Please enter a diagnosis first", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        // Show prescription dialog
        Dialog<Prescription> dialog = new Dialog<>();
        dialog.setTitle("Create Prescription");
        dialog.setHeaderText("Create prescription for " + currentPatient.getFullName());
        
        // Create prescription form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        TextField medicationField = new TextField();
        medicationField.setPromptText("Medication name");
        
        TextField dosageField = new TextField();
        dosageField.setPromptText("Dosage (e.g., 500mg)");
        
        ComboBox<String> frequencyCombo = new ComboBox<>();
        frequencyCombo.getItems().addAll("Once daily", "Twice daily", "Three times daily", "Four times daily", "As needed");
        frequencyCombo.setValue("Twice daily");
        
        TextField durationField = new TextField();
        durationField.setPromptText("Duration in days");
        
        TextArea instructionsArea = new TextArea();
        instructionsArea.setPromptText("Special instructions...");
        instructionsArea.setPrefRowCount(2);
        
        grid.add(new Label("Medication:"), 0, 0);
        grid.add(medicationField, 1, 0);
        grid.add(new Label("Dosage:"), 0, 1);
        grid.add(dosageField, 1, 1);
        grid.add(new Label("Frequency:"), 0, 2);
        grid.add(frequencyCombo, 1, 2);
        grid.add(new Label("Duration (days):"), 0, 3);
        grid.add(durationField, 1, 3);
        grid.add(new Label("Instructions:"), 0, 4);
        grid.add(instructionsArea, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    Prescription prescription = new Prescription();
                    prescription.setPatientId(currentPatient.getPatientId());
                    prescription.setDoctorId("DOC001"); // Default doctor
                    prescription.setDiagnosis(diagnosisArea.getText());
                    prescription.setInstructions(instructionsArea.getText());
                    
                    // Add medication
                    Medication medication = new Medication(
                        medicationField.getText(),
                        dosageField.getText(),
                        frequencyCombo.getValue(),
                        Integer.parseInt(durationField.getText()),
                        instructionsArea.getText()
                    );
                    prescription.addMedication(medication);
                    
                    return prescription;
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });
        
        Optional<Prescription> result = dialog.showAndWait();
        if (result.isPresent()) {
            String prescriptionId = hospitalService.createPrescription(result.get());
            showStatus("Prescription created successfully. ID: " + prescriptionId, "-fx-text-fill: #2ecc71;");
            refreshWaitingPatients();
            clearCurrentPatient();
        }
    }
    
    @FXML
    private void completeConsultation(ActionEvent event) {
        if (currentPatient == null) {
            showStatus("Please select a patient first", "-fx-text-fill: #e74c3c;");
            return;
        }
        
        // Update patient status to completed
        hospitalService.updatePatientStatus(currentPatient.getPatientId(), "COMPLETED");
        
        showStatus("Consultation completed for " + currentPatient.getFullName(), "-fx-text-fill: #2ecc71;");
        refreshWaitingPatients();
        clearCurrentPatient();
    }
    
    private void showStatus(String message, String style) {
        consultationStatusLabel.setText(message);
        consultationStatusLabel.setStyle(style + " -fx-font-weight: bold;");
    }
}