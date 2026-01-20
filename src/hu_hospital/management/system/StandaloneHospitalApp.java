package hu_hospital.management.system;

import hu_hospital.management.system.models.*;
import hu_hospital.management.system.services.HospitalService;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class StandaloneHospitalApp extends Application {
    
    private HospitalService hospitalService;
    private ObservableList<Patient> queueData;
    private Label totalPatientsLabel, waitingPatientsLabel, activeDoctorsLabel, pendingTestsLabel;
    private TableView<Patient> queueTableView;
    
    @Override
    public void start(Stage primaryStage) {
        hospitalService = HospitalService.getInstance();
        queueData = FXCollections.observableArrayList();
        
        // Create main layout
        BorderPane mainLayout = new BorderPane();
        
        // Header
        Label headerLabel = new Label("HU Hospital Management System");
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        headerLabel.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-padding: 20;");
        headerLabel.setMaxWidth(Double.MAX_VALUE);
        headerLabel.setAlignment(Pos.CENTER);
        mainLayout.setTop(headerLabel);
        
        // Center content
        VBox centerContent = new VBox(20);
        centerContent.setPadding(new Insets(20));
        
        // Stats and Actions
        HBox topSection = new HBox(20);
        
        // Stats box
        VBox statsBox = createStatsBox();
        
        // Actions box
        VBox actionsBox = createActionsBox(primaryStage);
        
        topSection.getChildren().addAll(statsBox, actionsBox);
        
        // Patient queue
        VBox queueSection = createQueueSection();
        
        centerContent.getChildren().addAll(topSection, queueSection);
        mainLayout.setCenter(centerContent);
        
        // Refresh data
        refreshDashboard();
        
        Scene scene = new Scene(mainLayout, 1200, 800);
        primaryStage.setTitle("HU Hospital Management System - Standalone Version");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        System.out.println("✅ Hospital Management System launched successfully!");
        System.out.println("📊 Dashboard loaded with " + hospitalService.getAllPatients().size() + " patients");
        System.out.println("👨‍⚕️ " + hospitalService.getAllDoctors().size() + " doctors available");
    }
    
    private VBox createStatsBox() {
        VBox statsBox = new VBox(10);
        statsBox.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-background-radius: 5;");
        statsBox.setPrefWidth(300);
        
        Label statsTitle = new Label("Quick Stats");
        statsTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        totalPatientsLabel = new Label("Total Patients: 0");
        waitingPatientsLabel = new Label("Waiting Patients: 0");
        activeDoctorsLabel = new Label("Active Doctors: 0");
        pendingTestsLabel = new Label("Pending Tests: 0");
        
        statsBox.getChildren().addAll(statsTitle, totalPatientsLabel, waitingPatientsLabel, 
                                     activeDoctorsLabel, pendingTestsLabel);
        
        return statsBox;
    }
    
    private VBox createActionsBox(Stage parentStage) {
        VBox actionsBox = new VBox(10);
        actionsBox.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-background-radius: 5;");
        actionsBox.setPrefWidth(300);
        
        Label actionsTitle = new Label("Quick Actions");
        actionsTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        Button patientRegBtn = new Button("Patient Registration");
        patientRegBtn.setPrefWidth(250);
        patientRegBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        patientRegBtn.setOnAction(e -> openPatientRegistration(parentStage));
        
        Button doctorBtn = new Button("Doctor Consultation");
        doctorBtn.setPrefWidth(250);
        doctorBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        doctorBtn.setOnAction(e -> openDoctorConsultation(parentStage));
        
        Button labBtn = new Button("Laboratory");
        labBtn.setPrefWidth(250);
        labBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        labBtn.setOnAction(e -> openLaboratory(parentStage));
        
        Button pharmacyBtn = new Button("Pharmacy");
        pharmacyBtn.setPrefWidth(250);
        pharmacyBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
        pharmacyBtn.setOnAction(e -> openPharmacy(parentStage));
        
        Button refreshBtn = new Button("Refresh Dashboard");
        refreshBtn.setPrefWidth(250);
        refreshBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");
        refreshBtn.setOnAction(e -> refreshDashboard());
        
        actionsBox.getChildren().addAll(actionsTitle, patientRegBtn, doctorBtn, labBtn, pharmacyBtn, refreshBtn);
        
        return actionsBox;
    }
    
    private VBox createQueueSection() {
        VBox queueSection = new VBox(10);
        
        Label queueTitle = new Label("Patient Queue");
        queueTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        queueTableView = new TableView<>();
        queueTableView.setPrefHeight(300);
        
        TableColumn<Patient, Integer> queueNumberColumn = new TableColumn<>("Queue #");
        queueNumberColumn.setPrefWidth(75);
        queueNumberColumn.setCellValueFactory(new PropertyValueFactory<>("queueNumber"));
        
        TableColumn<Patient, String> patientNameColumn = new TableColumn<>("Patient Name");
        patientNameColumn.setPrefWidth(150);
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        
        TableColumn<Patient, String> patientIdColumn = new TableColumn<>("Patient ID");
        patientIdColumn.setPrefWidth(100);
        patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        
        TableColumn<Patient, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setPrefWidth(120);
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        TableColumn<Patient, String> registrationTimeColumn = new TableColumn<>("Registration Time");
        registrationTimeColumn.setPrefWidth(150);
        registrationTimeColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
            return new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getRegistrationDate().format(formatter)
            );
        });
        
        queueTableView.getColumns().addAll(queueNumberColumn, patientNameColumn, patientIdColumn, 
                                          statusColumn, registrationTimeColumn);
        queueTableView.setItems(queueData);
        
        queueSection.getChildren().addAll(queueTitle, queueTableView);
        
        return queueSection;
    }
    
    private void refreshDashboard() {
        totalPatientsLabel.setText("Total Patients: " + hospitalService.getAllPatients().size());
        waitingPatientsLabel.setText("Waiting Patients: " + hospitalService.getWaitingPatients().size());
        activeDoctorsLabel.setText("Active Doctors: " + hospitalService.getAllDoctors().size());
        pendingTestsLabel.setText("Pending Tests: " + hospitalService.getPendingLabTests().size());
        
        queueData.clear();
        queueData.addAll(hospitalService.getWaitingPatients());
    }
    
    private void openPatientRegistration(Stage parentStage) {
        Stage regStage = new Stage();
        regStage.setTitle("Patient Registration");
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        
        Label title = new Label("Patient Registration");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        DatePicker dobPicker = new DatePicker();
        ComboBox<String> genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("Male", "Female", "Other");
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        TextArea addressArea = new TextArea();
        addressArea.setPrefRowCount(2);
        
        form.add(new Label("First Name:"), 0, 0);
        form.add(firstNameField, 1, 0);
        form.add(new Label("Last Name:"), 0, 1);
        form.add(lastNameField, 1, 1);
        form.add(new Label("Date of Birth:"), 0, 2);
        form.add(dobPicker, 1, 2);
        form.add(new Label("Gender:"), 0, 3);
        form.add(genderCombo, 1, 3);
        form.add(new Label("Phone:"), 0, 4);
        form.add(phoneField, 1, 4);
        form.add(new Label("Email:"), 0, 5);
        form.add(emailField, 1, 5);
        form.add(new Label("Address:"), 0, 6);
        form.add(addressArea, 1, 6);
        
        Button registerBtn = new Button("Register Patient");
        registerBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        registerBtn.setOnAction(e -> {
            if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty() || 
                dobPicker.getValue() == null || genderCombo.getValue() == null || phoneField.getText().isEmpty()) {
                showAlert("Error", "Please fill in all required fields.");
                return;
            }
            
            Patient patient = new Patient();
            patient.setFirstName(firstNameField.getText());
            patient.setLastName(lastNameField.getText());
            patient.setDateOfBirth(dobPicker.getValue());
            patient.setGender(genderCombo.getValue());
            patient.setPhoneNumber(phoneField.getText());
            patient.setEmail(emailField.getText());
            patient.setAddress(addressArea.getText());
            patient.setStatus("WAITING");
            
            String patientId = hospitalService.registerPatient(patient);
            showAlert("Success", "Patient registered successfully!\nPatient ID: " + patientId + 
                     "\nQueue Number: " + patient.getQueueNumber());
            
            refreshDashboard();
            regStage.close();
        });
        
        layout.getChildren().addAll(title, form, registerBtn);
        
        Scene scene = new Scene(layout, 400, 500);
        regStage.setScene(scene);
        regStage.show();
    }
    
    private void openDoctorConsultation(Stage parentStage) {
        Stage consultationStage = new Stage();
        consultationStage.setTitle("Doctor Consultation");
        
        BorderPane layout = new BorderPane();
        
        // Left side - Patient Queue
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(15));
        leftPanel.setStyle("-fx-background-color: #ecf0f1;");
        leftPanel.setPrefWidth(300);
        
        Label queueTitle = new Label("Patient Queue");
        queueTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        TableView<Patient> waitingTable = new TableView<>();
        waitingTable.setPrefHeight(200);
        
        TableColumn<Patient, Integer> queueCol = new TableColumn<>("Queue #");
        queueCol.setPrefWidth(60);
        queueCol.setCellValueFactory(new PropertyValueFactory<>("queueNumber"));
        
        TableColumn<Patient, String> nameCol = new TableColumn<>("Patient Name");
        nameCol.setPrefWidth(120);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        
        TableColumn<Patient, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(100);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        waitingTable.getColumns().addAll(queueCol, nameCol, statusCol);
        
        ObservableList<Patient> waitingPatients = FXCollections.observableArrayList();
        waitingPatients.addAll(hospitalService.getWaitingPatients());
        waitingTable.setItems(waitingPatients);
        
        Button callNextBtn = new Button("Call Next Patient");
        callNextBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        callNextBtn.setPrefWidth(200);
        
        leftPanel.getChildren().addAll(queueTitle, waitingTable, callNextBtn);
        
        // Right side - Consultation Form
        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setPrefWidth(500);
        
        Label consultationTitle = new Label("Patient Consultation");
        consultationTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        // Current patient info
        VBox patientInfo = new VBox(5);
        patientInfo.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-border-color: #dee2e6;");
        Label currentPatientLabel = new Label("No patient selected");
        currentPatientLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        Label patientDetailsLabel = new Label("");
        patientInfo.getChildren().addAll(new Label("Current Patient:"), currentPatientLabel, patientDetailsLabel);
        
        // Consultation form
        GridPane consultationForm = new GridPane();
        consultationForm.setHgap(10);
        consultationForm.setVgap(10);
        
        TextArea complaintArea = new TextArea();
        complaintArea.setPromptText("Chief complaint...");
        complaintArea.setPrefRowCount(2);
        
        TextArea symptomsArea = new TextArea();
        symptomsArea.setPromptText("Symptoms...");
        symptomsArea.setPrefRowCount(2);
        
        TextArea diagnosisArea = new TextArea();
        diagnosisArea.setPromptText("Diagnosis...");
        diagnosisArea.setPrefRowCount(2);
        
        consultationForm.add(new Label("Chief Complaint:"), 0, 0);
        consultationForm.add(complaintArea, 1, 0);
        consultationForm.add(new Label("Symptoms:"), 0, 1);
        consultationForm.add(symptomsArea, 1, 1);
        consultationForm.add(new Label("Diagnosis:"), 0, 2);
        consultationForm.add(diagnosisArea, 1, 2);
        
        // Action buttons
        HBox actionButtons = new HBox(10);
        
        Button sendToLabBtn = new Button("Send to Lab");
        sendToLabBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        
        Button prescribeBtn = new Button("Create Prescription");
        prescribeBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        
        Button completeBtn = new Button("Complete Visit");
        completeBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        
        actionButtons.getChildren().addAll(sendToLabBtn, prescribeBtn, completeBtn);
        
        rightPanel.getChildren().addAll(consultationTitle, patientInfo, consultationForm, actionButtons);
        
        layout.setLeft(leftPanel);
        layout.setCenter(rightPanel);
        
        // Event handlers
        final Patient[] currentPatient = {null};
        
        callNextBtn.setOnAction(e -> {
            if (!waitingPatients.isEmpty()) {
                currentPatient[0] = waitingPatients.get(0);
                hospitalService.updatePatientStatus(currentPatient[0].getPatientId(), "WITH_DOCTOR");
                currentPatientLabel.setText(currentPatient[0].getFullName());
                patientDetailsLabel.setText("ID: " + currentPatient[0].getPatientId() + 
                                          " | Phone: " + currentPatient[0].getPhoneNumber());
                
                waitingPatients.clear();
                waitingPatients.addAll(hospitalService.getWaitingPatients());
                refreshDashboard();
            } else {
                showAlert("No Patients", "No patients waiting in queue.");
            }
        });
        
        sendToLabBtn.setOnAction(e -> {
            if (currentPatient[0] == null) {
                showAlert("Error", "Please select a patient first.");
                return;
            }
            
            // Lab test selection dialog
            ChoiceDialog<String> dialog = new ChoiceDialog<>("Complete Blood Count (CBC)", 
                "Complete Blood Count (CBC)", "Blood Chemistry Panel", "Urinalysis", 
                "X-Ray Chest", "ECG", "Blood Sugar Test");
            dialog.setTitle("Order Lab Test");
            dialog.setHeaderText("Select lab test for " + currentPatient[0].getFullName());
            
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                LabTest labTest = new LabTest();
                labTest.setPatientId(currentPatient[0].getPatientId());
                labTest.setTestType(result.get());
                labTest.setDescription("Ordered from consultation");
                labTest.setOrderedBy("DOC001");
                
                String testId = hospitalService.orderLabTest(labTest);
                showAlert("Success", "Lab test ordered successfully!\nTest ID: " + testId);
                
                currentPatient[0] = null;
                currentPatientLabel.setText("No patient selected");
                patientDetailsLabel.setText("");
                complaintArea.clear();
                symptomsArea.clear();
                diagnosisArea.clear();
                refreshDashboard();
            }
        });
        
        prescribeBtn.setOnAction(e -> {
            if (currentPatient[0] == null) {
                showAlert("Error", "Please select a patient first.");
                return;
            }
            
            if (diagnosisArea.getText().trim().isEmpty()) {
                showAlert("Error", "Please enter a diagnosis first.");
                return;
            }
            
            // Create prescription dialog
            createPrescriptionDialog(currentPatient[0], diagnosisArea.getText(), () -> {
                currentPatient[0] = null;
                currentPatientLabel.setText("No patient selected");
                patientDetailsLabel.setText("");
                complaintArea.clear();
                symptomsArea.clear();
                diagnosisArea.clear();
                refreshDashboard();
            });
        });
        
        completeBtn.setOnAction(e -> {
            if (currentPatient[0] == null) {
                showAlert("Error", "Please select a patient first.");
                return;
            }
            
            hospitalService.updatePatientStatus(currentPatient[0].getPatientId(), "COMPLETED");
            showAlert("Success", "Consultation completed for " + currentPatient[0].getFullName());
            
            currentPatient[0] = null;
            currentPatientLabel.setText("No patient selected");
            patientDetailsLabel.setText("");
            complaintArea.clear();
            symptomsArea.clear();
            diagnosisArea.clear();
            refreshDashboard();
        });
        
        Scene scene = new Scene(layout, 900, 600);
        consultationStage.setScene(scene);
        consultationStage.show();
    }
    
    private void openLaboratory(Stage parentStage) {
        Stage labStage = new Stage();
        labStage.setTitle("Laboratory Management");
        
        BorderPane layout = new BorderPane();
        
        // Left side - Pending Tests
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(15));
        leftPanel.setStyle("-fx-background-color: #ecf0f1;");
        leftPanel.setPrefWidth(400);
        
        Label testsTitle = new Label("Pending Lab Tests");
        testsTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        TableView<LabTest> testsTable = new TableView<>();
        testsTable.setPrefHeight(250);
        
        TableColumn<LabTest, String> testIdCol = new TableColumn<>("Test ID");
        testIdCol.setPrefWidth(80);
        testIdCol.setCellValueFactory(new PropertyValueFactory<>("testId"));
        
        TableColumn<LabTest, String> patientCol = new TableColumn<>("Patient");
        patientCol.setPrefWidth(120);
        patientCol.setCellValueFactory(cellData -> {
            Patient patient = hospitalService.findPatientById(cellData.getValue().getPatientId());
            return new javafx.beans.property.SimpleStringProperty(
                patient != null ? patient.getFullName() : "Unknown"
            );
        });
        
        TableColumn<LabTest, String> testTypeCol = new TableColumn<>("Test Type");
        testTypeCol.setPrefWidth(150);
        testTypeCol.setCellValueFactory(new PropertyValueFactory<>("testType"));
        
        TableColumn<LabTest, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(100);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        testsTable.getColumns().addAll(testIdCol, patientCol, testTypeCol, statusCol);
        
        ObservableList<LabTest> pendingTests = FXCollections.observableArrayList();
        pendingTests.addAll(hospitalService.getPendingLabTests());
        testsTable.setItems(pendingTests);
        
        HBox testButtons = new HBox(10);
        Button startTestBtn = new Button("Start Test");
        startTestBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        
        Button completeTestBtn = new Button("Complete Test");
        completeTestBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        
        testButtons.getChildren().addAll(startTestBtn, completeTestBtn);
        
        leftPanel.getChildren().addAll(testsTitle, testsTable, testButtons);
        
        // Right side - Test Results
        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setPrefWidth(400);
        
        Label resultsTitle = new Label("Test Results");
        resultsTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        // Selected test info
        VBox testInfo = new VBox(5);
        testInfo.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-border-color: #dee2e6;");
        Label selectedTestLabel = new Label("No test selected");
        selectedTestLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        Label testDetailsLabel = new Label("");
        testInfo.getChildren().addAll(new Label("Selected Test:"), selectedTestLabel, testDetailsLabel);
        
        // Results form
        VBox resultsForm = new VBox(10);
        Label resultsLabel = new Label("Test Results:");
        TextArea resultsArea = new TextArea();
        resultsArea.setPromptText("Enter test results here...");
        resultsArea.setPrefRowCount(8);
        
        Button saveResultsBtn = new Button("Save Results");
        saveResultsBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        saveResultsBtn.setPrefWidth(150);
        
        resultsForm.getChildren().addAll(resultsLabel, resultsArea, saveResultsBtn);
        
        rightPanel.getChildren().addAll(resultsTitle, testInfo, resultsForm);
        
        layout.setLeft(leftPanel);
        layout.setCenter(rightPanel);
        
        // Event handlers
        final LabTest[] selectedTest = {null};
        
        testsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedTest[0] = newSelection;
                selectedTestLabel.setText(newSelection.getTestId() + " - " + newSelection.getTestType());
                Patient patient = hospitalService.findPatientById(newSelection.getPatientId());
                testDetailsLabel.setText("Patient: " + (patient != null ? patient.getFullName() : "Unknown") +
                                       "\nStatus: " + newSelection.getStatus());
                resultsArea.setText(newSelection.getResults() != null ? newSelection.getResults() : "");
            }
        });
        
        startTestBtn.setOnAction(e -> {
            if (selectedTest[0] == null) {
                showAlert("Error", "Please select a test first.");
                return;
            }
            
            if (!"ORDERED".equals(selectedTest[0].getStatus())) {
                showAlert("Error", "Test is already in progress or completed.");
                return;
            }
            
            selectedTest[0].setStatus("IN_PROGRESS");
            showAlert("Success", "Test " + selectedTest[0].getTestId() + " started.");
            
            pendingTests.clear();
            pendingTests.addAll(hospitalService.getPendingLabTests());
        });
        
        completeTestBtn.setOnAction(e -> {
            if (selectedTest[0] == null) {
                showAlert("Error", "Please select a test first.");
                return;
            }
            
            if (resultsArea.getText().trim().isEmpty()) {
                showAlert("Error", "Please enter test results first.");
                return;
            }
            
            hospitalService.completeLabTest(selectedTest[0].getTestId(), resultsArea.getText().trim());
            showAlert("Success", "Test " + selectedTest[0].getTestId() + " completed successfully!");
            
            pendingTests.clear();
            pendingTests.addAll(hospitalService.getPendingLabTests());
            
            selectedTest[0] = null;
            selectedTestLabel.setText("No test selected");
            testDetailsLabel.setText("");
            resultsArea.clear();
            refreshDashboard();
        });
        
        saveResultsBtn.setOnAction(e -> {
            if (selectedTest[0] == null) {
                showAlert("Error", "Please select a test first.");
                return;
            }
            
            selectedTest[0].setResults(resultsArea.getText().trim());
            showAlert("Success", "Results saved for test " + selectedTest[0].getTestId());
        });
        
        Scene scene = new Scene(layout, 900, 600);
        labStage.setScene(scene);
        labStage.show();
    }
    
    private void openPharmacy(Stage parentStage) {
        Stage pharmacyStage = new Stage();
        pharmacyStage.setTitle("Pharmacy Management");
        
        BorderPane layout = new BorderPane();
        
        // Left side - Pending Prescriptions
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(15));
        leftPanel.setStyle("-fx-background-color: #ecf0f1;");
        leftPanel.setPrefWidth(400);
        
        Label prescriptionsTitle = new Label("Pending Prescriptions");
        prescriptionsTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        TableView<Prescription> prescriptionsTable = new TableView<>();
        prescriptionsTable.setPrefHeight(200);
        
        TableColumn<Prescription, String> prescIdCol = new TableColumn<>("Prescription ID");
        prescIdCol.setPrefWidth(120);
        prescIdCol.setCellValueFactory(new PropertyValueFactory<>("prescriptionId"));
        
        TableColumn<Prescription, String> patientCol = new TableColumn<>("Patient");
        patientCol.setPrefWidth(120);
        patientCol.setCellValueFactory(cellData -> {
            Patient patient = hospitalService.findPatientById(cellData.getValue().getPatientId());
            return new javafx.beans.property.SimpleStringProperty(
                patient != null ? patient.getFullName() : "Unknown"
            );
        });
        
        TableColumn<Prescription, String> doctorCol = new TableColumn<>("Doctor");
        doctorCol.setPrefWidth(120);
        doctorCol.setCellValueFactory(cellData -> {
            Doctor doctor = hospitalService.findDoctorById(cellData.getValue().getDoctorId());
            return new javafx.beans.property.SimpleStringProperty(
                doctor != null ? doctor.getFullName() : "Unknown"
            );
        });
        
        prescriptionsTable.getColumns().addAll(prescIdCol, patientCol, doctorCol);
        
        ObservableList<Prescription> pendingPrescriptions = FXCollections.observableArrayList();
        pendingPrescriptions.addAll(hospitalService.getPendingPrescriptions());
        prescriptionsTable.setItems(pendingPrescriptions);
        
        Button dispenseBtn = new Button("Dispense Medication");
        dispenseBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        dispenseBtn.setPrefWidth(200);
        
        leftPanel.getChildren().addAll(prescriptionsTitle, prescriptionsTable, dispenseBtn);
        
        // Right side - Prescription Details
        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setPrefWidth(500);
        
        Label detailsTitle = new Label("Prescription Details");
        detailsTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        // Selected prescription info
        VBox prescInfo = new VBox(5);
        prescInfo.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-border-color: #dee2e6;");
        Label selectedPrescLabel = new Label("No prescription selected");
        selectedPrescLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        Label prescDetailsLabel = new Label("");
        Label diagnosisLabel = new Label("");
        prescInfo.getChildren().addAll(new Label("Selected Prescription:"), selectedPrescLabel, 
                                     prescDetailsLabel, diagnosisLabel);
        
        // Medications table
        Label medicationsTitle = new Label("Medications:");
        medicationsTitle.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        TableView<Medication> medicationsTable = new TableView<>();
        medicationsTable.setPrefHeight(150);
        
        TableColumn<Medication, String> medNameCol = new TableColumn<>("Medication");
        medNameCol.setPrefWidth(120);
        medNameCol.setCellValueFactory(new PropertyValueFactory<>("medicationName"));
        
        TableColumn<Medication, String> dosageCol = new TableColumn<>("Dosage");
        dosageCol.setPrefWidth(80);
        dosageCol.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        
        TableColumn<Medication, String> frequencyCol = new TableColumn<>("Frequency");
        frequencyCol.setPrefWidth(100);
        frequencyCol.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        
        TableColumn<Medication, Integer> durationCol = new TableColumn<>("Duration");
        durationCol.setPrefWidth(80);
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));
        
        medicationsTable.getColumns().addAll(medNameCol, dosageCol, frequencyCol, durationCol);
        
        ObservableList<Medication> medications = FXCollections.observableArrayList();
        medicationsTable.setItems(medications);
        
        // Pharmacy actions
        HBox pharmacyActions = new HBox(10);
        Button checkInventoryBtn = new Button("Check Inventory");
        checkInventoryBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        
        Button printLabelBtn = new Button("Print Labels");
        printLabelBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        
        pharmacyActions.getChildren().addAll(checkInventoryBtn, printLabelBtn);
        
        rightPanel.getChildren().addAll(detailsTitle, prescInfo, medicationsTitle, 
                                       medicationsTable, pharmacyActions);
        
        layout.setLeft(leftPanel);
        layout.setCenter(rightPanel);
        
        // Event handlers
        final Prescription[] selectedPrescription = {null};
        
        prescriptionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedPrescription[0] = newSelection;
                selectedPrescLabel.setText(newSelection.getPrescriptionId());
                
                Patient patient = hospitalService.findPatientById(newSelection.getPatientId());
                Doctor doctor = hospitalService.findDoctorById(newSelection.getDoctorId());
                
                prescDetailsLabel.setText("Patient: " + (patient != null ? patient.getFullName() : "Unknown") +
                                        "\nDoctor: " + (doctor != null ? doctor.getFullName() : "Unknown"));
                diagnosisLabel.setText("Diagnosis: " + newSelection.getDiagnosis());
                
                medications.clear();
                medications.addAll(newSelection.getMedications());
            }
        });
        
        checkInventoryBtn.setOnAction(e -> {
            if (selectedPrescription[0] == null) {
                showAlert("Error", "Please select a prescription first.");
                return;
            }
            
            StringBuilder inventory = new StringBuilder("Medication Inventory Status:\n\n");
            for (Medication med : selectedPrescription[0].getMedications()) {
                boolean available = Math.random() > 0.2; // 80% chance available
                inventory.append("• ").append(med.getMedicationName())
                        .append(": ").append(available ? "✓ Available" : "✗ Out of Stock")
                        .append("\n");
            }
            
            showAlert("Inventory Check", inventory.toString());
        });
        
        printLabelBtn.setOnAction(e -> {
            if (selectedPrescription[0] == null) {
                showAlert("Error", "Please select a prescription first.");
                return;
            }
            
            StringBuilder labels = new StringBuilder("Medication Labels:\n\n");
            Patient patient = hospitalService.findPatientById(selectedPrescription[0].getPatientId());
            
            for (Medication med : selectedPrescription[0].getMedications()) {
                labels.append("MEDICATION LABEL\n")
                      .append("================\n")
                      .append("Patient: ").append(patient != null ? patient.getFullName() : "Unknown").append("\n")
                      .append("Medication: ").append(med.getMedicationName()).append("\n")
                      .append("Dosage: ").append(med.getDosage()).append("\n")
                      .append("Frequency: ").append(med.getFrequency()).append("\n")
                      .append("Duration: ").append(med.getDuration()).append(" days\n\n");
            }
            
            showAlert("Print Labels", labels.toString());
        });
        
        dispenseBtn.setOnAction(e -> {
            if (selectedPrescription[0] == null) {
                showAlert("Error", "Please select a prescription first.");
                return;
            }
            
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Dispensing");
            confirmAlert.setHeaderText("Dispense Medication");
            confirmAlert.setContentText("Are you sure you want to dispense all medications for this prescription?");
            
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                hospitalService.dispensePrescription(selectedPrescription[0].getPrescriptionId());
                showAlert("Success", "Medications dispensed successfully for prescription " + 
                         selectedPrescription[0].getPrescriptionId());
                
                pendingPrescriptions.clear();
                pendingPrescriptions.addAll(hospitalService.getPendingPrescriptions());
                
                selectedPrescription[0] = null;
                selectedPrescLabel.setText("No prescription selected");
                prescDetailsLabel.setText("");
                diagnosisLabel.setText("");
                medications.clear();
                refreshDashboard();
            }
        });
        
        Scene scene = new Scene(layout, 1000, 600);
        pharmacyStage.setScene(scene);
        pharmacyStage.show();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void createPrescriptionDialog(Patient patient, String diagnosis, Runnable onComplete) {
        Stage prescStage = new Stage();
        prescStage.setTitle("Create Prescription");
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        
        Label title = new Label("Create Prescription for " + patient.getFullName());
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        
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
        instructionsArea.setPrefRowCount(3);
        
        form.add(new Label("Medication:"), 0, 0);
        form.add(medicationField, 1, 0);
        form.add(new Label("Dosage:"), 0, 1);
        form.add(dosageField, 1, 1);
        form.add(new Label("Frequency:"), 0, 2);
        form.add(frequencyCombo, 1, 2);
        form.add(new Label("Duration (days):"), 0, 3);
        form.add(durationField, 1, 3);
        form.add(new Label("Instructions:"), 0, 4);
        form.add(instructionsArea, 1, 4);
        
        HBox buttons = new HBox(10);
        Button createBtn = new Button("Create Prescription");
        createBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        
        buttons.getChildren().addAll(createBtn, cancelBtn);
        
        layout.getChildren().addAll(title, form, buttons);
        
        createBtn.setOnAction(e -> {
            if (medicationField.getText().isEmpty() || dosageField.getText().isEmpty() || 
                durationField.getText().isEmpty()) {
                showAlert("Error", "Please fill in all required fields.");
                return;
            }
            
            try {
                Prescription prescription = new Prescription();
                prescription.setPatientId(patient.getPatientId());
                prescription.setDoctorId("DOC001");
                prescription.setDiagnosis(diagnosis);
                prescription.setInstructions(instructionsArea.getText());
                
                Medication medication = new Medication(
                    medicationField.getText(),
                    dosageField.getText(),
                    frequencyCombo.getValue(),
                    Integer.parseInt(durationField.getText()),
                    instructionsArea.getText()
                );
                prescription.addMedication(medication);
                
                String prescriptionId = hospitalService.createPrescription(prescription);
                showAlert("Success", "Prescription created successfully!\nPrescription ID: " + prescriptionId);
                
                prescStage.close();
                onComplete.run();
                
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid number for duration.");
            }
        });
        
        cancelBtn.setOnAction(e -> prescStage.close());
        
        Scene scene = new Scene(layout, 400, 400);
        prescStage.setScene(scene);
        prescStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}