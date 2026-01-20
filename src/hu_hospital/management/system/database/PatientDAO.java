package hu_hospital.management.system.database;

import hu_hospital.management.system.models.Patient;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Patient operations
 */
public class PatientDAO {
    
    /**
     * Insert a new patient into the database
     */
    public String insertPatient(Patient patient) throws SQLException {
        String sql = """
            INSERT INTO patients (patient_id, first_name, last_name, date_of_birth, gender, 
                                phone_number, email, address, emergency_contact, medical_history, 
                                queue_number, status)
            VALUES (generate_patient_id(), ?, ?, ?, ?, ?, ?, ?, ?, ?, get_next_queue_number(), ?)
            RETURNING patient_id, queue_number
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setDate(3, Date.valueOf(patient.getDateOfBirth()));
            stmt.setString(4, patient.getGender());
            stmt.setString(5, patient.getPhoneNumber());
            stmt.setString(6, patient.getEmail());
            stmt.setString(7, patient.getAddress());
            stmt.setString(8, patient.getEmergencyContact());
            stmt.setString(9, patient.getMedicalHistory());
            stmt.setString(10, patient.getStatus());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String patientId = rs.getString("patient_id");
                int queueNumber = rs.getInt("queue_number");
                
                patient.setPatientId(patientId);
                patient.setQueueNumber(queueNumber);
                
                return patientId;
            }
            
            throw new SQLException("Failed to insert patient");
        }
    }
    
    /**
     * Find patient by ID
     */
    public Patient findPatientById(String patientId) throws SQLException {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patientId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPatient(rs);
            }
            
            return null;
        }
    }
    
    /**
     * Find patient by phone number
     */
    public Patient findPatientByPhone(String phoneNumber) throws SQLException {
        String sql = "SELECT * FROM patients WHERE phone_number = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, phoneNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPatient(rs);
            }
            
            return null;
        }
    }
    
    /**
     * Get all patients
     */
    public List<Patient> getAllPatients() throws SQLException {
        String sql = "SELECT * FROM patients ORDER BY registration_date DESC";
        List<Patient> patients = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        }
        
        return patients;
    }
    
    /**
     * Get waiting patients (in queue)
     */
    public List<Patient> getWaitingPatients() throws SQLException {
        String sql = """
            SELECT * FROM patients 
            WHERE status IN ('WAITING', 'REGISTERED') 
            ORDER BY queue_number
            """;
        List<Patient> patients = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                patients.add(mapResultSetToPatient(rs));
            }
        }
        
        return patients;
    }
    
    /**
     * Update patient status
     */
    public void updatePatientStatus(String patientId, String status) throws SQLException {
        String sql = "UPDATE patients SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE patient_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setString(2, patientId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Patient not found: " + patientId);
            }
        }
    }
    
    /**
     * Update patient information
     */
    public void updatePatient(Patient patient) throws SQLException {
        String sql = """
            UPDATE patients SET 
                first_name = ?, last_name = ?, date_of_birth = ?, gender = ?,
                phone_number = ?, email = ?, address = ?, emergency_contact = ?,
                medical_history = ?, status = ?, updated_at = CURRENT_TIMESTAMP
            WHERE patient_id = ?
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setDate(3, Date.valueOf(patient.getDateOfBirth()));
            stmt.setString(4, patient.getGender());
            stmt.setString(5, patient.getPhoneNumber());
            stmt.setString(6, patient.getEmail());
            stmt.setString(7, patient.getAddress());
            stmt.setString(8, patient.getEmergencyContact());
            stmt.setString(9, patient.getMedicalHistory());
            stmt.setString(10, patient.getStatus());
            stmt.setString(11, patient.getPatientId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Patient not found: " + patient.getPatientId());
            }
        }
    }
    
    /**
     * Delete patient
     */
    public void deletePatient(String patientId) throws SQLException {
        String sql = "DELETE FROM patients WHERE patient_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patientId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Patient not found: " + patientId);
            }
        }
    }
    
    /**
     * Map ResultSet to Patient object
     */
    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        
        patient.setPatientId(rs.getString("patient_id"));
        patient.setFirstName(rs.getString("first_name"));
        patient.setLastName(rs.getString("last_name"));
        patient.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        patient.setGender(rs.getString("gender"));
        patient.setPhoneNumber(rs.getString("phone_number"));
        patient.setEmail(rs.getString("email"));
        patient.setAddress(rs.getString("address"));
        patient.setEmergencyContact(rs.getString("emergency_contact"));
        patient.setMedicalHistory(rs.getString("medical_history"));
        patient.setQueueNumber(rs.getInt("queue_number"));
        patient.setStatus(rs.getString("status"));
        
        Timestamp regDate = rs.getTimestamp("registration_date");
        if (regDate != null) {
            patient.setRegistrationDate(regDate.toLocalDateTime());
        }
        
        return patient;
    }
}