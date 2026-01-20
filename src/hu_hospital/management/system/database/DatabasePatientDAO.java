package hu_hospital.management.system.database;

import hu_hospital.management.system.models.Patient;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Patient operations - matches your database schema
 */
public class DatabasePatientDAO {
    
    /**
     * Insert a new patient into the database
     */
    public String insertPatient(Patient patient) throws SQLException {
        String sql = """
            INSERT INTO patient (first_name, last_name, gender, age, phone, address, registration_date)
            VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE)
            RETURNING patient_id
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setString(3, patient.getGender());
            
            // Calculate age from date of birth
            int age = patient.getDateOfBirth() != null ? 
                java.time.Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears() : 0;
            stmt.setInt(4, age);
            
            stmt.setString(5, patient.getPhoneNumber());
            stmt.setString(6, patient.getAddress());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int patientId = rs.getInt("patient_id");
                patient.setPatientId("PAT" + String.format("%04d", patientId));
                patient.setQueueNumber(getNextQueueNumber());
                patient.setStatus("WAITING");
                
                return patient.getPatientId();
            }
            
            throw new SQLException("Failed to insert patient");
        }
    }
    
    /**
     * Find patient by ID
     */
    public Patient findPatientById(String patientId) throws SQLException {
        // Extract numeric ID from PAT0001 format
        int numericId = Integer.parseInt(patientId.substring(3));
        
        String sql = "SELECT * FROM patient WHERE patient_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, numericId);
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
        String sql = "SELECT * FROM patient WHERE phone = ?";
        
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
        String sql = "SELECT * FROM patient ORDER BY registration_date DESC";
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
     * Get waiting patients (simulate queue)
     */
    public List<Patient> getWaitingPatients() throws SQLException {
        String sql = """
            SELECT * FROM patient 
            WHERE registration_date = CURRENT_DATE
            ORDER BY patient_id
            """;
        List<Patient> patients = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int queueNum = 1;
            while (rs.next()) {
                Patient patient = mapResultSetToPatient(rs);
                patient.setQueueNumber(queueNum++);
                patient.setStatus("WAITING");
                patients.add(patient);
            }
        }
        
        return patients;
    }
    
    /**
     * Update patient information
     */
    public void updatePatient(Patient patient) throws SQLException {
        int numericId = Integer.parseInt(patient.getPatientId().substring(3));
        
        String sql = """
            UPDATE patient SET 
                first_name = ?, last_name = ?, gender = ?, age = ?,
                phone = ?, address = ?
            WHERE patient_id = ?
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, patient.getFirstName());
            stmt.setString(2, patient.getLastName());
            stmt.setString(3, patient.getGender());
            
            int age = patient.getDateOfBirth() != null ? 
                java.time.Period.between(patient.getDateOfBirth(), LocalDate.now()).getYears() : 0;
            stmt.setInt(4, age);
            
            stmt.setString(5, patient.getPhoneNumber());
            stmt.setString(6, patient.getAddress());
            stmt.setInt(7, numericId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Patient not found: " + patient.getPatientId());
            }
        }
    }
    
    /**
     * Get next queue number for today
     */
    private int getNextQueueNumber() throws SQLException {
        String sql = "SELECT COUNT(*) + 1 as next_queue FROM patient WHERE registration_date = CURRENT_DATE";
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("next_queue");
            }
            return 1;
        }
    }
    
    /**
     * Map ResultSet to Patient object
     */
    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        
        patient.setPatientId("PAT" + String.format("%04d", rs.getInt("patient_id")));
        patient.setFirstName(rs.getString("first_name"));
        patient.setLastName(rs.getString("last_name"));
        patient.setGender(rs.getString("gender"));
        patient.setPhoneNumber(rs.getString("phone"));
        patient.setAddress(rs.getString("address"));
        
        // Calculate date of birth from age (approximate)
        int age = rs.getInt("age");
        if (age > 0) {
            patient.setDateOfBirth(LocalDate.now().minusYears(age));
        }
        
        Date regDate = rs.getDate("registration_date");
        if (regDate != null) {
            patient.setRegistrationDate(regDate.toLocalDate().atStartOfDay());
        }
        
        return patient;
    }
}