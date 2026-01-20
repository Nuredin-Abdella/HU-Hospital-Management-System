# HU Hospital Management System

A comprehensive hospital management system built with JavaFX and FXML for Apache NetBeans.

## Features

### 1. Patient Registration
- Professional patient registration form
- Search existing patients by phone number
- Automatic patient ID generation
- Queue number assignment
- Medical history tracking

### 2. Doctor Consultation
- Patient queue management
- Detailed consultation forms
- Lab test ordering
- Prescription creation
- Patient status tracking

### 3. Laboratory Management
- Pending lab tests view
- Test result entry
- Status tracking (Ordered → In Progress → Completed)
- Result printing and doctor notification

### 4. Pharmacy Management
- Prescription dispensing
- Medication inventory checking
- Label printing
- Prescription completion tracking

### 5. Professional Dashboard
- Real-time statistics
- Patient queue overview
- Quick navigation between modules
- Status monitoring

## Recent Fixes

### Compilation Issues Fixed:
- ✅ **Medication Class**: Made `Medication` class public and created separate file
- ✅ **Import Statements**: Added proper imports for JavaFX layout classes
- ✅ **Class Accessibility**: Fixed package-private class access issues
- ✅ **Controller Dependencies**: Resolved cross-controller dependencies

### Files Fixed:
1. `DoctorConsultationController.java` - Added missing imports for VBox and GridPane
2. `PharmacyController.java` - Fixed Medication class access
3. `Prescription.java` - Removed inner Medication class
4. `Medication.java` - Created as separate public class

## Project Structure

```
src/hu_hospital/management/system/
├── models/
│   ├── Patient.java          # Patient data model
│   ├── Doctor.java           # Doctor data model
│   ├── LabTest.java          # Lab test data model
│   └── Prescription.java     # Prescription and medication models
├── services/
│   └── HospitalService.java  # Business logic and data management
├── controllers/
│   ├── FXMLDocumentController.java           # Main dashboard controller
│   ├── PatientRegistrationController.java   # Patient registration logic
│   ├── DoctorConsultationController.java    # Doctor consultation logic
│   ├── LaboratoryController.java            # Laboratory management logic
│   └── PharmacyController.java              # Pharmacy management logic
├── fxml/
│   ├── FXMLDocument.fxml                     # Main dashboard UI
│   ├── PatientRegistration.fxml             # Patient registration UI
│   ├── DoctorConsultation.fxml              # Doctor consultation UI
│   ├── Laboratory.fxml                      # Laboratory management UI
│   └── Pharmacy.fxml                        # Pharmacy management UI
├── HU_hospitalManagementSystem.java         # Main application class
└── styles.css                               # Application styling
```

## How to Run in NetBeans

1. **Open the Project**
   - Open Apache NetBeans IDE
   - File → Open Project
   - Navigate to the project folder and select it

2. **Configure JavaFX**
   - Right-click on the project → Properties
   - Go to Libraries → Add Library → JavaFX (if not already added)
   - Ensure JavaFX is properly configured in your NetBeans installation

3. **Build and Run**
   - Right-click on the project → Clean and Build
   - Right-click on `HU_hospitalManagementSystem.java` → Run File
   - Or use F6 to run the entire project

## Workflow

### Patient Registration Flow
1. Search for existing patient by phone number
2. If found, patient details are loaded for quick re-registration
3. If new patient, fill out the complete registration form
4. Patient receives queue number and status becomes "WAITING"

### Doctor Consultation Flow
1. Doctor calls next patient from the queue
2. Patient status changes to "WITH_DOCTOR"
3. Doctor fills consultation details
4. Options:
   - Send to Lab → Patient status becomes "IN_LAB"
   - Create Prescription → Patient status becomes "PRESCRIPTION_READY"
   - Complete Visit → Patient status becomes "COMPLETED"

### Laboratory Flow
1. View pending lab tests ordered by doctors
2. Start test → Status changes to "IN_PROGRESS"
3. Enter test results
4. Complete test → Status changes to "COMPLETED"
5. Patient status returns to "WAITING" for doctor review

### Pharmacy Flow
1. View pending prescriptions
2. Check medication inventory
3. Print medication labels
4. Dispense medications
5. Complete prescription → Patient status becomes "COMPLETED"

## Key Features

- **Real-time Updates**: Dashboard shows live statistics and patient queue
- **Professional UI**: Clean, medical-grade interface design
- **Complete Workflow**: Covers entire patient journey from registration to discharge
- **Data Persistence**: In-memory data management with sample data
- **Status Tracking**: Real-time patient status updates throughout the system
- **Professional Forms**: Comprehensive forms for all medical processes

## Sample Data

The system comes pre-loaded with sample doctors:
- Dr. John Smith (General Medicine)
- Dr. Sarah Johnson (Cardiology)
- Dr. Michael Brown (Pediatrics)

## Technical Requirements

- Java 8 or higher
- JavaFX (included with Java 8, separate module for Java 11+)
- Apache NetBeans IDE
- Windows/Mac/Linux operating system

## Future Enhancements

- Database integration (MySQL/PostgreSQL)
- User authentication and role management
- Report generation
- Appointment scheduling
- Billing and insurance management
- Electronic medical records (EMR)
- Integration with medical devices
- Multi-language support

## Support

For issues or questions about this hospital management system, please refer to the code comments and JavaDoc documentation within the source files.