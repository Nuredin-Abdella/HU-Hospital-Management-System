-- HU Hospital Management System Database Schema
-- PostgreSQL Database Creation Script

-- Create Database
CREATE DATABASE hu_hospital_management;

-- Connect to the database
\c hu_hospital_management;

-- Create Tables

-- 1. Doctors Table
CREATE TABLE doctors (
    doctor_id VARCHAR(10) PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    email VARCHAR(100),
    is_available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Patients Table
CREATE TABLE patients (
    patient_id VARCHAR(10) PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10) NOT NULL CHECK (gender IN ('Male', 'Female', 'Other')),
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100),
    address TEXT,
    emergency_contact VARCHAR(100),
    medical_history TEXT,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    queue_number INTEGER,
    status VARCHAR(20) DEFAULT 'REGISTERED' CHECK (status IN ('REGISTERED', 'WAITING', 'WITH_DOCTOR', 'IN_LAB', 'PRESCRIPTION_READY', 'COMPLETED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Lab Tests Table
CREATE TABLE lab_tests (
    test_id VARCHAR(10) PRIMARY KEY,
    patient_id VARCHAR(10) NOT NULL,
    test_type VARCHAR(100) NOT NULL,
    description TEXT,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completion_date TIMESTAMP,
    results TEXT,
    status VARCHAR(20) DEFAULT 'ORDERED' CHECK (status IN ('ORDERED', 'IN_PROGRESS', 'COMPLETED')),
    ordered_by VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (ordered_by) REFERENCES doctors(doctor_id)
);

-- 4. Prescriptions Table
CREATE TABLE prescriptions (
    prescription_id VARCHAR(10) PRIMARY KEY,
    patient_id VARCHAR(10) NOT NULL,
    doctor_id VARCHAR(10) NOT NULL,
    prescription_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    diagnosis TEXT NOT NULL,
    instructions TEXT,
    status VARCHAR(20) DEFAULT 'PRESCRIBED' CHECK (status IN ('PRESCRIBED', 'DISPENSED', 'COMPLETED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
);

-- 5. Medications Table
CREATE TABLE medications (
    medication_id SERIAL PRIMARY KEY,
    prescription_id VARCHAR(10) NOT NULL,
    medication_name VARCHAR(100) NOT NULL,
    dosage VARCHAR(50) NOT NULL,
    frequency VARCHAR(50) NOT NULL,
    duration INTEGER NOT NULL, -- in days
    instructions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(prescription_id) ON DELETE CASCADE
);

-- 6. Consultations Table (Optional - for detailed consultation records)
CREATE TABLE consultations (
    consultation_id SERIAL PRIMARY KEY,
    patient_id VARCHAR(10) NOT NULL,
    doctor_id VARCHAR(10) NOT NULL,
    consultation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    chief_complaint TEXT,
    symptoms TEXT,
    physical_examination TEXT,
    diagnosis TEXT,
    notes TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id)
);

-- Create Indexes for better performance
CREATE INDEX idx_patients_phone ON patients(phone_number);
CREATE INDEX idx_patients_status ON patients(status);
CREATE INDEX idx_patients_queue ON patients(queue_number);
CREATE INDEX idx_lab_tests_patient ON lab_tests(patient_id);
CREATE INDEX idx_lab_tests_status ON lab_tests(status);
CREATE INDEX idx_prescriptions_patient ON prescriptions(patient_id);
CREATE INDEX idx_prescriptions_status ON prescriptions(status);
CREATE INDEX idx_medications_prescription ON medications(prescription_id);

-- Insert Sample Data

-- Sample Doctors
INSERT INTO doctors (doctor_id, first_name, last_name, specialization, phone_number, email) VALUES
('DOC001', 'John', 'Smith', 'General Medicine', '123-456-7890', 'john.smith@hospital.com'),
('DOC002', 'Sarah', 'Johnson', 'Cardiology', '123-456-7891', 'sarah.johnson@hospital.com'),
('DOC003', 'Michael', 'Brown', 'Pediatrics', '123-456-7892', 'michael.brown@hospital.com'),
('DOC004', 'Emily', 'Davis', 'Orthopedics', '123-456-7893', 'emily.davis@hospital.com'),
('DOC005', 'Robert', 'Wilson', 'Neurology', '123-456-7894', 'robert.wilson@hospital.com');

-- Sample Patients
INSERT INTO patients (patient_id, first_name, last_name, date_of_birth, gender, phone_number, email, address, emergency_contact, medical_history, queue_number, status) VALUES
('PAT1001', 'Alice', 'Johnson', '1985-03-15', 'Female', '555-0101', 'alice.johnson@email.com', '123 Main St, City', 'Bob Johnson - 555-0102', 'No known allergies', 1, 'WAITING'),
('PAT1002', 'David', 'Smith', '1978-07-22', 'Male', '555-0103', 'david.smith@email.com', '456 Oak Ave, City', 'Mary Smith - 555-0104', 'Diabetes Type 2', 2, 'WAITING'),
('PAT1003', 'Emma', 'Brown', '1992-11-08', 'Female', '555-0105', 'emma.brown@email.com', '789 Pine St, City', 'James Brown - 555-0106', 'Hypertension', 3, 'WITH_DOCTOR');

-- Sample Lab Tests
INSERT INTO lab_tests (test_id, patient_id, test_type, description, status, ordered_by) VALUES
('TEST0001', 'PAT1001', 'Complete Blood Count (CBC)', 'Routine blood work', 'ORDERED', 'DOC001'),
('TEST0002', 'PAT1002', 'Blood Sugar Test', 'Diabetes monitoring', 'IN_PROGRESS', 'DOC001'),
('TEST0003', 'PAT1003', 'ECG', 'Heart rhythm check', 'COMPLETED', 'DOC002');

-- Sample Prescriptions
INSERT INTO prescriptions (prescription_id, patient_id, doctor_id, diagnosis, instructions, status) VALUES
('PRES0001', 'PAT1002', 'DOC001', 'Type 2 Diabetes Mellitus', 'Take with meals, monitor blood sugar', 'PRESCRIBED'),
('PRES0002', 'PAT1003', 'DOC002', 'Hypertension', 'Take once daily in the morning', 'PRESCRIBED');

-- Sample Medications
INSERT INTO medications (prescription_id, medication_name, dosage, frequency, duration, instructions) VALUES
('PRES0001', 'Metformin', '500mg', 'Twice daily', 30, 'Take with breakfast and dinner'),
('PRES0001', 'Glipizide', '5mg', 'Once daily', 30, 'Take 30 minutes before breakfast'),
('PRES0002', 'Lisinopril', '10mg', 'Once daily', 30, 'Take in the morning with water'),
('PRES0002', 'Amlodipine', '5mg', 'Once daily', 30, 'Take at the same time each day');

-- Create Views for Common Queries

-- View: Patient Queue
CREATE VIEW patient_queue AS
SELECT 
    p.queue_number,
    p.patient_id,
    CONCAT(p.first_name, ' ', p.last_name) AS patient_name,
    p.status,
    p.registration_date
FROM patients p
WHERE p.status IN ('WAITING', 'WITH_DOCTOR', 'IN_LAB', 'PRESCRIPTION_READY')
ORDER BY p.queue_number;

-- View: Pending Lab Tests
CREATE VIEW pending_lab_tests AS
SELECT 
    lt.test_id,
    lt.patient_id,
    CONCAT(p.first_name, ' ', p.last_name) AS patient_name,
    lt.test_type,
    lt.order_date,
    lt.status,
    CONCAT(d.first_name, ' ', d.last_name) AS ordered_by_doctor
FROM lab_tests lt
JOIN patients p ON lt.patient_id = p.patient_id
JOIN doctors d ON lt.ordered_by = d.doctor_id
WHERE lt.status IN ('ORDERED', 'IN_PROGRESS')
ORDER BY lt.order_date;

-- View: Pending Prescriptions
CREATE VIEW pending_prescriptions AS
SELECT 
    pr.prescription_id,
    pr.patient_id,
    CONCAT(p.first_name, ' ', p.last_name) AS patient_name,
    pr.doctor_id,
    CONCAT(d.first_name, ' ', d.last_name) AS doctor_name,
    pr.diagnosis,
    pr.prescription_date,
    pr.status
FROM prescriptions pr
JOIN patients p ON pr.patient_id = p.patient_id
JOIN doctors d ON pr.doctor_id = d.doctor_id
WHERE pr.status = 'PRESCRIBED'
ORDER BY pr.prescription_date;

-- View: Prescription Details with Medications
CREATE VIEW prescription_details AS
SELECT 
    pr.prescription_id,
    CONCAT(p.first_name, ' ', p.last_name) AS patient_name,
    CONCAT(d.first_name, ' ', d.last_name) AS doctor_name,
    pr.diagnosis,
    pr.prescription_date,
    m.medication_name,
    m.dosage,
    m.frequency,
    m.duration,
    m.instructions AS medication_instructions
FROM prescriptions pr
JOIN patients p ON pr.patient_id = p.patient_id
JOIN doctors d ON pr.doctor_id = d.doctor_id
JOIN medications m ON pr.prescription_id = m.prescription_id
ORDER BY pr.prescription_date DESC, m.medication_name;

-- Create Functions for Common Operations

-- Function: Get Next Queue Number
CREATE OR REPLACE FUNCTION get_next_queue_number()
RETURNS INTEGER AS $$
BEGIN
    RETURN COALESCE(MAX(queue_number), 0) + 1 FROM patients WHERE DATE(registration_date) = CURRENT_DATE;
END;
$$ LANGUAGE plpgsql;

-- Function: Generate Patient ID
CREATE OR REPLACE FUNCTION generate_patient_id()
RETURNS VARCHAR(10) AS $$
DECLARE
    next_id INTEGER;
BEGIN
    SELECT COALESCE(MAX(CAST(SUBSTRING(patient_id FROM 4) AS INTEGER)), 1000) + 1 
    INTO next_id 
    FROM patients;
    
    RETURN 'PAT' || LPAD(next_id::TEXT, 4, '0');
END;
$$ LANGUAGE plpgsql;

-- Triggers for automatic updates

-- Trigger: Update timestamp on patient update
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER patients_update_timestamp
    BEFORE UPDATE ON patients
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER doctors_update_timestamp
    BEFORE UPDATE ON doctors
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER lab_tests_update_timestamp
    BEFORE UPDATE ON lab_tests
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER prescriptions_update_timestamp
    BEFORE UPDATE ON prescriptions
    FOR EACH ROW
    EXECUTE FUNCTION update_timestamp();

-- Grant permissions (adjust as needed)
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO hospital_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO hospital_user;