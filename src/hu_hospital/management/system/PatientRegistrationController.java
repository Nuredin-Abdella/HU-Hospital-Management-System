package hu_hospital.management.system;

import hu_hospital.management.system.models.Patient;
import hu_hospital.management.system.services.HospitalService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

public class PatientRegistrationController implements Initializable {
    
    @FXML private TextField searchPhoneField;
    @FXML private Label searchResultLabel;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker dobPicker;
    @FXML private ComboBox<String> genderComboBox;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField emergencyContactField;
    @FXML private TextArea medicalHistoryArea;
    @FXML private Label registrationStatusLabel;
    
    private HospitalService hospitalService;
    private Patient existingPatient;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        hospitalService = HospitalService.getInstance();
        
        // Initialize gender combo box
        genderComboBox.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        
        clearForm();
    }
    
    @FXML
    private void searchPatient(ActionEvent event) {
        String phoneNumber = searchPhoneField.getText().trim();
        
        if (phoneNumber.isEmpty()) {
            searchResultLabel.setText("Please enter a phone number");
            searchResultLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }
        
        existingPatient = hospitalService.findPatientByPhone(phoneNumber);
        
        if (existingPatient != null) {
            // Patient found - populate form with existing data
            searchResultLabel.setText("Patient found: " + existingPatient.getFullName());
            searchResultLabel.setStyle("-fx-text-fill: #2ecc71;");
            
            firstNameField.setText(existingPatient.getFirstName());
            lastNameField.setText(existingPatient.getLastName());
            dobPicker.setValue(existingPatient.getDateOfBirth());
            genderComboBox.setValue(existingPatient.getGender());
            phoneField.setText(existingPatient.getPhoneNumber());
            emailField.setText(existingPatient.getEmail());
            addressField.setText(existingPatient.getAddress());
            emergencyContactField.setText(existingPatient.getEmergencyContact());
            medicalHistoryArea.setText(existingPatient.getMedicalHistory());
            
            // Disable editing of basic info for existing patients
            firstNameField.setDisable(true);
            lastNameField.setDisable(true);
            dobPicker.setDisable(true);
            genderComboBox.setDisable(true);
            phoneField.setDisable(true);
        } else {
            searchResultLabel.setText("Patient not found. You can register as new patient.");
            searchResultLabel.setStyle("-fx-text-fill: #f39c12;");
            
            // Pre-fill phone number for new registration
            phoneField.setText(phoneNumber);
            enableAllFields();
        }
    }
    
    @FXML
    private void registerPatient(ActionEvent event) {
        if (!validateForm()) {
            return;
        }
        
        try {
            if (existingPatient != null) {
                // Existing patient - just update queue and status
                existingPatient.setStatus("WAITING");
                existingPatient.setQueueNumber(hospitalService.getWaitingPatients().size() + 1);
                
                registrationStatusLabel.setText("Patient " + existingPatient.getFullName() + 
                    " registered successfully! Queue Number: " + existingPatient.getQueueNumber());
                registrationStatusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
            } else {
                // New patient registration
                Patient newPatient = new Patient();
                newPatient.setFirstName(firstNameField.getText().trim());
                newPatient.setLastName(lastNameField.getText().trim());
                newPatient.setDateOfBirth(dobPicker.getValue());
                newPatient.setGender(genderComboBox.getValue());
                newPatient.setPhoneNumber(phoneField.getText().trim());
                newPatient.setEmail(emailField.getText().trim());
                newPatient.setAddress(addressField.getText().trim());
                newPatient.setEmergencyContact(emergencyContactField.getText().trim());
                newPatient.setMedicalHistory(medicalHistoryArea.getText().trim());
                newPatient.setStatus("WAITING");
                
                String patientId = hospitalService.registerPatient(newPatient);
                
                registrationStatusLabel.setText("Patient registered successfully! Patient ID: " + patientId + 
                    ", Queue Number: " + newPatient.getQueueNumber());
                registrationStatusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
            }
            
            // Refresh main dashboard if possible
            refreshMainDashboard();
            
            // Clear form after successful registration
            clearForm();
            
        } catch (Exception e) {
            registrationStatusLabel.setText("Registration failed: " + e.getMessage());
            registrationStatusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }
    }
    
    @FXML
    private void clearForm(ActionEvent event) {
        clearForm();
    }
    
    private void clearForm() {
        searchPhoneField.clear();
        searchResultLabel.setText("");
        firstNameField.clear();
        lastNameField.clear();
        dobPicker.setValue(null);
        genderComboBox.setValue(null);
        phoneField.clear();
        emailField.clear();
        addressField.clear();
        emergencyContactField.clear();
        medicalHistoryArea.clear();
        registrationStatusLabel.setText("");
        
        existingPatient = null;
        enableAllFields();
    }
    
    private void enableAllFields() {
        firstNameField.setDisable(false);
        lastNameField.setDisable(false);
        dobPicker.setDisable(false);
        genderComboBox.setDisable(false);
        phoneField.setDisable(false);
    }
    
    private boolean validateForm() {
        if (firstNameField.getText().trim().isEmpty()) {
            showValidationError("First name is required");
            return false;
        }
        
        if (lastNameField.getText().trim().isEmpty()) {
            showValidationError("Last name is required");
            return false;
        }
        
        if (dobPicker.getValue() == null) {
            showValidationError("Date of birth is required");
            return false;
        }
        
        if (genderComboBox.getValue() == null) {
            showValidationError("Gender is required");
            return false;
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            showValidationError("Phone number is required");
            return false;
        }
        
        return true;
    }
    
    private void showValidationError(String message) {
        registrationStatusLabel.setText(message);
        registrationStatusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }
    
    private void refreshMainDashboard() {
        // Dashboard will be refreshed when this window closes
        // This is handled by the main controller's window close event
    }
}