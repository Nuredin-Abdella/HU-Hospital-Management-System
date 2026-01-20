package hu_hospital.management.system;

import hu_hospital.management.system.models.Patient;
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
import java.time.format.DateTimeFormatter;

public class ProgrammaticHospitalApp extends Application {
    
    private HospitalService hospitalService;
    private ObservableList<Patient> queueData;
    private Label totalPatientsLabel, waitingPatientsLabel, activeDoctorsLabel, pendingTestsLabel;
    private TableView<Patient> queueTableView;
    
    @Override
    public void start(Stage primaryStage) {
        hospitalService = HospitalService.getInstance();
        queueData = FXCollections.observableArrayList();
        
        // Create main layout
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        
        // Header
        Label headerLabel = new Label("HU Hospital Management System");
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        headerLabel.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-padding: 20;");
        headerLabel.setMaxWidth(Double.MAX_VALUE);
        headerLabel.setAlignment(Pos.CENTER);
        
        // Stats and Actions
        HBox topSection = new HBox(20);
        
        // Stats box
        VBox statsBox = createStatsBox();
        
        // Actions box
        VBox actionsBox = createActionsBox();
        
        topSection.getChildren().addAll(statsBox, actionsBox);
        
        // Patient queue
        VBox queueSection = createQueueSection();
        
        mainLayout.getChildren().addAll(headerLabel, topSection, queueSection);
        
        // Refresh data
        refreshDashboard();
        
        Scene scene = new Scene(mainLayout, 1000, 700);
        primaryStage.setTitle("HU Hospital Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createStatsBox() {
        VBox statsBox = new VBox(10);
        statsBox.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-background-radius: 5;");
        
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
    
    private VBox createActionsBox() {
        VBox actionsBox = new VBox(10);
        actionsBox.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label actionsTitle = new Label("Quick Actions");
        actionsTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        
        Button patientRegBtn = new Button("Patient Registration");
        patientRegBtn.setPrefWidth(200);
        patientRegBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        patientRegBtn.setOnAction(e -> showMessage("Patient Registration", "Patient Registration module would open here."));
        
        Button doctorBtn = new Button("Doctor Consultation");
        doctorBtn.setPrefWidth(200);
        doctorBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        doctorBtn.setOnAction(e -> showMessage("Doctor Consultation", "Doctor Consultation module would open here."));
        
        Button labBtn = new Button("Laboratory");
        labBtn.setPrefWidth(200);
        labBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        labBtn.setOnAction(e -> showMessage("Laboratory", "Laboratory module would open here."));
        
        Button pharmacyBtn = new Button("Pharmacy");
        pharmacyBtn.setPrefWidth(200);
        pharmacyBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
        pharmacyBtn.setOnAction(e -> showMessage("Pharmacy", "Pharmacy module would open here."));
        
        actionsBox.getChildren().addAll(actionsTitle, patientRegBtn, doctorBtn, labBtn, pharmacyBtn);
        
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
    
    private void showMessage(String title, String message) {
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