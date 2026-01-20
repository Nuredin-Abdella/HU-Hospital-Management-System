package hu_hospital.management.system.database;

import hu_hospital.management.system.models.Doctor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Doctor operations - matches your database schema
 */
public class DatabaseDoctorDAO {
    
    /**
     * Insert a new doctor into the database
     */
    public String insertDoctor(Doctor doctor) throws SQLException {
        String sql = """
            INSERT INTO doctor (first_name, last_name, specialization, phone, email)
            VALUES (?, ?, ?, ?, ?)
            RETURNING doctor_id
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, doctor.getFirstName());
            stmt.setString(2, doctor.getLastName());
            stmt.setString(3, doctor.getSpecialization());
            stmt.setString(4, doctor.getPhoneNumber());
            stmt.setString(5, doctor.getEmail());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int doctorId = rs.getInt("doctor_id");
                String docId = "DOC" + String.format("%03d", doctorId);
                doctor.setDoctorId(docId);
                return docId;
            }
            
            throw new SQLException("Failed to insert doctor");
        }
    }
    
    /**
     * Find doctor by ID
     */
    public Doctor findDoctorById(String doctorId) throws SQLException {
        // Extract numeric ID from DOC001 format
        int numericId = Integer.parseInt(doctorId.substring(3));
        
        String sql = "SELECT * FROM doctor WHERE doctor_id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, numericId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToDoctor(rs);
            }
            
            return null;
        }
    }
    
    /**
     * Get all doctors
     */
    public List<Doctor> getAllDoctors() throws SQLException {
        String sql = "SELECT * FROM doctor ORDER BY last_name, first_name";
        List<Doctor> doctors = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                doctors.add(mapResultSetToDoctor(rs));
            }
        }
        
        return doctors;
    }
    
    /**
     * Update doctor information
     */
    public void updateDoctor(Doctor doctor) throws SQLException {
        int numericId = Integer.parseInt(doctor.getDoctorId().substring(3));
        
        String sql = """
            UPDATE doctor SET 
                first_name = ?, last_name = ?, specialization = ?, phone = ?, email = ?
            WHERE doctor_id = ?
            """;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, doctor.getFirstName());
            stmt.setString(2, doctor.getLastName());
            stmt.setString(3, doctor.getSpecialization());
            stmt.setString(4, doctor.getPhoneNumber());
            stmt.setString(5, doctor.getEmail());
            stmt.setInt(6, numericId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Doctor not found: " + doctor.getDoctorId());
            }
        }
    }
    
    /**
     * Map ResultSet to Doctor object
     */
    private Doctor mapResultSetToDoctor(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        
        doctor.setDoctorId("DOC" + String.format("%03d", rs.getInt("doctor_id")));
        doctor.setFirstName(rs.getString("first_name"));
        doctor.setLastName(rs.getString("last_name"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setPhoneNumber(rs.getString("phone"));
        doctor.setEmail(rs.getString("email"));
        doctor.setAvailable(true); // Default to available
        
        return doctor;
    }
}