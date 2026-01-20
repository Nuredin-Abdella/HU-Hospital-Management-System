package hu_hospital.management.system;

import hu_hospital.management.system.models.Patient;
import hu_hospital.management.system.services.HospitalService;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class FXMLDocumentController implements Initializable {
    
    @FXML private TabPane mainTabPane;
    
    // Dashboard elements
    @FXML private Label totalPatientsLabel;
    @FXML private Label waitingPatientsLabel;
    @FXML private Label activeDoctorsLabel;
    @FXML private Label pendingTestsLabel;
    @FXML private TableView<Patient> queueTableView;
    @FXML private TableColumn<Patient, Integer> queueNumberColumn;
    @FXML private TableColumn<Patient, String> patientNameColumn;
    @FXML private TableColumn<Patient, String> patientIdColumn;
    @FXML private TableColumn<Patient, String> statusColumn;
    @FXML private TableColumn<Patient, String> registrationTimeColumn;
    
    private HospitalService hospitalService;
    private ObservableList<Patient> queueData;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hospitalService = HospitalService.getInstance();
        queueData = FXCollections.observableArrayList();
        
        setupQueueTable();
        refreshDashboard();
    }
    
    private void setupQueueTable() {
        queueNumberColumn.setCellValueFactory(new PropertyValueFactory<>("queueNumber"));
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        patientIdColumn.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        registrationTimeColumn.setCellValueFactory(cellData -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
            return new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getRegistrationDate().format(formatter)
            );
        });
        
        queueTableView.setItems(queueData);
    }
    
    public void refreshDashboard() {
        // Update statistics
        totalPatientsLabel.setText("Total Patients: " + hospitalService.getAllPatients().size());
        waitingPatientsLabel.setText("Waiting Patients: " + hospitalService.getWaitingPatients().size());
        activeDoctorsLabel.setText("Active Doctors: " + hospitalService.getAllDoctors().size());
        pendingTestsLabel.setText("Pending Tests: " + hospitalService.getPendingLabTests().size());
        
        // Update queue table
        queueData.clear();
        queueData.addAll(hospitalService.getWaitingPatients());
    }
    
    @FXML
    private void openPatientRegistration(ActionEvent event) {
        mainTabPane.getSelectionModel().select(1); // Select Patient Registration tab
    }
    
    @FXML
    private void openDoctorConsultation(ActionEvent event) {
        mainTabPane.getSelectionModel().select(2); // Select Doctor Consultation tab
    }
    
    @FXML
    private void openLaboratory(ActionEvent event) {
        mainTabPane.getSelectionModel().select(3); // Select Laboratory tab
    }
    
    @FXML
    private void openPharmacy(ActionEvent event) {
        mainTabPane.getSelectionModel().select(4); // Select Pharmacy tab
    }
    
    @FXML
    private void openPatientRegistrationWindow(ActionEvent event) {
        openWindow("PatientRegistration.fxml", "Patient Registration");
    }
    
    @FXML
    private void openDoctorConsultationWindow(ActionEvent event) {
        openWindow("DoctorConsultation.fxml", "Doctor Consultation");
    }
    
    @FXML
    private void openLaboratoryWindow(ActionEvent event) {
        openWindow("Laboratory.fxml", "Laboratory Management");
    }
    
    @FXML
    private void openPharmacyWindow(ActionEvent event) {
        openWindow("Pharmacy.fxml", "Pharmacy Management");
    }
    
    private void openWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle(title + " - HU Hospital Management System");
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();
            
            // Refresh dashboard when window is closed
            stage.setOnHidden(e -> refreshDashboard());
            
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot open " + title);
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
