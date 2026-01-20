package hu_hospital.management.system;

import hu_hospital.management.system.database.DatabaseConfig;
import hu_hospital.management.system.models.*;
import hu_hospital.management.system.services.DatabaseHospitalService;
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

public class DatabaseHospitalApp extends Application {
    
    private DatabaseHospitalService hospitalService;
    private ObservableList<Patient> queueData;
    private Label totalPatientsLabel, waitingPatientsLabel, activeDoctorsLabel, pendingTestsLabel;
    private TableView<Patient> queueTableView;
    
    @Override
    public void start(Stage primaryStage) {
        // Initialize database service
        hospitalService = DatabaseHospitalService.getInstance();
        queueData = FXCollections.observableArrayList();
        
        // Test database connection
        System.out.println("🏥 HU Hospital Management System - Database Version");
        System.out.println("==================================================");
        hospitalService.printDatabaseInfo();
        
        if (!hospitalService.testDatabaseConnection()) {
            showDatabaseError(primaryStage);
            return;
        }
        
        // Create main layout
        BorderPane mainLayout = new BorderPane();
        
        // Header
        Label headerLabel = new Label("HU Hospital Management System - Database Connected");
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
        primaryStage.setTitle("HU Hospital Management System - Database Version");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        System.out.println("✅ Hospital Management System launched with database connection!");
    }
    
    private void showDatabaseError(Stage primaryStage) {
        VBox errorLayout = new VBox(20);
        errorLayout.setPadding(new Insets(50));
        errorLayout.setAlignment(Pos.CENTER);
        
        Label errorTitle = new Label("❌ Database Connection Failed");
        errorTitle.setFont(Font.font("System", FontWeight.BOLD, 24));
        errorTitle.setStyle("-fx-text-fill: #e74c3c;");
        
        Label errorMessage = new Label(
            "Could not connect to PostgreSQL database.\n\n" +
            "Please check:\n" +
            "• PostgreSQL is running\n" +
            "• Database 'hospital' exists\n" +
            "• Username and password are correct\n" +
            "• JDBC driver is in classpath"
        );
        errorMessage.setFont(Font.font("System", 14));
        
        Button retryButton = new Button("Retry Connection");
        retryButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        retryButton.setOnAction(e -> {
            if (hospitalService.testDatabaseConnection()) {
                primaryStage.close();
                start(primaryStage);
            } else {
                showAlert("Connection Failed", "Still cannot connect to database. Please check your setup.");
            }
        });
        
        Button configButton = new Button("Show Configuration");
        configButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        configButton.setOnAction(e -> {
            hospitalService.printDatabaseInfo();
            showAlert("Database Configuration", 
                "Check console output for database configuration details.\n\n" +
                "Update DatabaseConfig.java with your correct:\n" +
                "• Database name\n" +
                "• Username\n" +
                "• Password");
        });
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(retryButton, configButton);
        
        errorLayout.getChildren().addAll(errorTitle, errorMessage, buttonBox);
        
        Scene scene = new Scene(errorLayout, 600, 400);
        primaryStage.setTitle("Database Connection Error");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createStatsBox() {
        VBox statsBox = new VBox(10);
        statsBox.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-background-radius: 5;");
        statsBox.setPrefWidth(300);
        
        Label statsTitle = new Label("Database Statistics");
        statsTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        totalPatientsLabel = new Label("Total Patients: 0");
        waitingPatientsLabel = new Label("Waiting Patients: 0");
        activeDoctorsLabel = new Label("Active Doctors: 0");
        pendingTestsLabel = new Label("Pending Tests: 0");
        
        Label dbStatusLabel = new Label("✅ Database Connected");
        dbStatusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
        
        statsBox.getChildren().addAll(statsTitle, totalPatientsLabel, waitingPatientsLabel, 
                                     activeDoctorsLabel, pendingTestsLabel, dbStatusLabel);
        
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
        
        Button viewPatientsBtn = new Button("View All Patients");
        viewPatientsBtn.setPrefWidth(250);
        viewPatientsBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        viewPatientsBtn.setOnAction(e -> viewAllPatients());
        
        Button testDbBtn = new Button("Test Database");
        testDbBtn.setPrefWidth(250);
        testDbBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
        testDbBtn.setOnAction(e -> testDatabase());
        
        Button refreshBtn = new Button("Refresh Dashboard");
        refreshBtn.setPrefWidth(250);
        refreshBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");
        refreshBtn.setOnAction(e -> refreshDashboard());
        
        actionsBox.getChildren().addAll(actionsTitle, patientRegBtn, viewPatientsBtn, testDbBtn, refreshBtn);
        
        return actionsBox;
    }
    
    private VBox createQueueSection() {
        VBox queueSection = new VBox(10);
        
        Label queueTitle = new Label("Today's Patient Queue (From Database)");
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
        
        TableColumn<Patient, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setPrefWidth(120);
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        
        TableColumn<Patient, String> registrationTimeColumn = new TableColumn<>("Registration Time");
        registrationTimeColumn.setPrefWidth(150);
        registrationTimeColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getRegistrationDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getRegistrationDate().format(formatter)
                );
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        
        queueTableView.getColumns().addAll(queueNumberColumn, patientNameColumn, patientIdColumn, 
                                          phoneColumn, registrationTimeColumn);
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
        
        System.out.println("📊 Dashboard refreshed from database");
    }
    
    private void openPatientRegistration(Stage parentStage) {
        Stage regStage = new Stage();
        regStage.setTitle("Patient Registration - Database");
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        
        Label title = new Label("Patient Registration (Saves to Database)");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        DatePicker dobPicker = new DatePicker();
        ComboBox<String> genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("Male", "Female");
        TextField phoneField = new TextField();
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
        form.add(new Label("Address:"), 0, 5);
        form.add(addressArea, 1, 5);
        
        Button registerBtn = new Button("Register Patient in Database");
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
            patient.setAddress(addressArea.getText());
            
            String patientId = hospitalService.registerPatient(patient);
            if (patientId != null) {
                showAlert("Success", "Patient registered successfully in database!\n" +
                         "Patient ID: " + patientId + 
                         "\nQueue Number: " + patient.getQueueNumber() +
                         "\n\nCheck pgAdmin to see the data!");
                
                refreshDashboard();
                regStage.close();
            } else {
                showAlert("Error", "Failed to register patient. Check database connection.");
            }
        });
        
        layout.getChildren().addAll(title, form, registerBtn);
        
        Scene scene = new Scene(layout, 400, 500);
        regStage.setScene(scene);
        regStage.show();
    }
    
    private void viewAllPatients() {
        StringBuilder patientList = new StringBuilder("Patients in Database:\n\n");
        
        for (Patient patient : hospitalService.getAllPatients()) {
            patientList.append("• ").append(patient.getPatientId())
                      .append(": ").append(patient.getFullName())
                      .append(" (").append(patient.getPhoneNumber()).append(")\n");
        }
        
        if (hospitalService.getAllPatients().isEmpty()) {
            patientList.append("No patients found in database.");
        }
        
        showAlert("All Patients", patientList.toString());
    }
    
    private void testDatabase() {
        if (hospitalService.testDatabaseConnection()) {
            showAlert("Database Test", "✅ Database connection successful!\n\n" +
                     "Patients in DB: " + hospitalService.getAllPatients().size() + "\n" +
                     "Doctors in DB: " + hospitalService.getAllDoctors().size());
        } else {
            showAlert("Database Test", "❌ Database connection failed!\n\nCheck your configuration.");
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}