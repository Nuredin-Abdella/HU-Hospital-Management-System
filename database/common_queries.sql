-- Common Queries for HU Hospital Management System
-- Use these queries in pgAdmin 4 for testing and monitoring

-- =====================================================
-- PATIENT MANAGEMENT QUERIES
-- =====================================================

-- 1. View all patients with their current status
SELECT 
    patient_id,
    CONCAT(first_name, ' ', last_name) AS full_name,
    phone_number,
    status,
    queue_number,
    registration_date
FROM patients
ORDER BY registration_date DESC;

-- 2. Get current patient queue (waiting patients)
SELECT 
    queue_number,
    patient_id,
    CONCAT(first_name, ' ', last_name) AS patient_name,
    phone_number,
    status,
    registration_date
FROM patients
WHERE status IN ('WAITING', 'REGISTERED', 'WITH_DOCTOR')
ORDER BY queue_number;

-- 3. Find patient by phone number
SELECT * FROM patients 
WHERE phone_number = '555-0101';

-- 4. Get patients registered today
SELECT 
    patient_id,
    CONCAT(first_name, ' ', last_name) AS full_name,
    status,
    queue_number
FROM patients
WHERE DATE(registration_date) = CURRENT_DATE
ORDER BY queue_number;

-- 5. Count patients by status
SELECT 
    status,
    COUNT(*) as patient_count
FROM patients
GROUP BY status
ORDER BY patient_count DESC;

-- =====================================================
-- DOCTOR MANAGEMENT QUERIES
-- =====================================================

-- 6. View all doctors with their specializations
SELECT 
    doctor_id,
    CONCAT(first_name, ' ', last_name) AS doctor_name,
    specialization,
    phone_number,
    email,
    is_available
FROM doctors
ORDER BY specialization, last_name;

-- 7. Get available doctors
SELECT * FROM doctors 
WHERE is_available = TRUE
ORDER BY specialization;

-- =====================================================
-- LAB TEST QUERIES
-- =====================================================

-- 8. View all pending lab tests
SELECT 
    lt.test_id,
    CONCAT(p.first_name, ' ', p.last_name) AS patient_name,
    lt.test_type,
    lt.order_date,
    lt.status,
    CONCAT(d.first_name, ' ', d.last_name) AS ordered_by
FROM lab_tests lt
JOIN patients p ON lt.patient_id = p.patient_id
JOIN doctors d ON lt.ordered_by = d.doctor_id
WHERE lt.status IN ('ORDERED', 'IN_PROGRESS')
ORDER BY lt.order_date;

-- 9. Get completed lab tests with results
SELECT 
    lt.test_id,
    CONCAT(p.first_name, ' ', p.last_name) AS patient_name,
    lt.test_type,
    lt.completion_date,
    LEFT(lt.results, 100) AS results_preview
FROM lab_tests lt
JOIN patients p ON lt.patient_id = p.patient_id
WHERE lt.status = 'COMPLETED'
ORDER BY lt.completion_date DESC;

-- 10. Lab tests by doctor
SELECT 
    CONCAT(d.first_name, ' ', d.last_name) AS doctor_name,
    COUNT(*) as tests_ordered,
    COUNT(CASE WHEN lt.status = 'COMPLETED' THEN 1 END) as tests_completed
FROM lab_tests lt
JOIN doctors d ON lt.ordered_by = d.doctor_id
GROUP BY d.doctor_id, d.first_name, d.last_name
ORDER BY tests_ordered DESC;

-- =====================================================
-- PRESCRIPTION QUERIES
-- =====================================================

-- 11. View pending prescriptions with patient details
SELECT 
    pr.prescription_id,
    CONCAT(p.first_name, ' ', p.last_name) AS patient_name,
    p.phone_number,
    CONCAT(d.first_name, ' ', d.last_name) AS doctor_name,
    pr.diagnosis,
    pr.prescription_date,
    pr.status
FROM prescriptions pr
JOIN patients p ON pr.patient_id = p.patient_id
JOIN doctors d ON pr.doctor_id = d.doctor_id
WHERE pr.status = 'PRESCRIBED'
ORDER BY pr.prescription_date;

-- 12. Get prescription details with medications
SELECT 
    pr.prescription_id,
    CONCAT(p.first_name, ' ', p.last_name) AS patient_name,
    pr.diagnosis,
    m.medication_name,
    m.dosage,
    m.frequency,
    m.duration,
    m.instructions
FROM prescriptions pr
JOIN patients p ON pr.patient_id = p.patient_id
JOIN medications m ON pr.prescription_id = m.prescription_id
WHERE pr.prescription_id = 'PRES0001'
ORDER BY m.medication_name;

-- 13. Medications dispensed today
SELECT 
    pr.prescription_id,
    CONCAT(p.first_name, ' ', p.last_name) AS patient_name,
    COUNT(m.medication_id) as medication_count,
    pr.prescription_date
FROM prescriptions pr
JOIN patients p ON pr.patient_id = p.patient_id
JOIN medications m ON pr.prescription_id = m.prescription_id
WHERE pr.status = 'DISPENSED' 
  AND DATE(pr.prescription_date) = CURRENT_DATE
GROUP BY pr.prescription_id, p.first_name, p.last_name, pr.prescription_date
ORDER BY pr.prescription_date DESC;

-- =====================================================
-- DASHBOARD STATISTICS QUERIES
-- =====================================================

-- 14. Daily statistics
SELECT 
    'Total Patients' as metric,
    COUNT(*) as count
FROM patients
UNION ALL
SELECT 
    'Patients Today' as metric,
    COUNT(*) as count
FROM patients
WHERE DATE(registration_date) = CURRENT_DATE
UNION ALL
SELECT 
    'Waiting Patients' as metric,
    COUNT(*) as count
FROM patients
WHERE status IN ('WAITING', 'REGISTERED')
UNION ALL
SELECT 
    'Active Doctors' as metric,
    COUNT(*) as count
FROM doctors
WHERE is_available = TRUE
UNION ALL
SELECT 
    'Pending Lab Tests' as metric,
    COUNT(*) as count
FROM lab_tests
WHERE status IN ('ORDERED', 'IN_PROGRESS')
UNION ALL
SELECT 
    'Pending Prescriptions' as metric,
    COUNT(*) as count
FROM prescriptions
WHERE status = 'PRESCRIBED';

-- 15. Patient flow summary
SELECT 
    status,
    COUNT(*) as patient_count,
    ROUND(COUNT(*) * 100.0 / SUM(COUNT(*)) OVER (), 2) as percentage
FROM patients
WHERE DATE(registration_date) = CURRENT_DATE
GROUP BY status
ORDER BY patient_count DESC;

-- =====================================================
-- MAINTENANCE QUERIES
-- =====================================================

-- 16. Database table sizes
SELECT 
    schemaname,
    tablename,
    attname,
    n_distinct,
    correlation
FROM pg_stats
WHERE schemaname = 'public'
ORDER BY tablename, attname;

-- 17. Recent activity (last 24 hours)
SELECT 
    'Patient Registrations' as activity,
    COUNT(*) as count
FROM patients
WHERE registration_date >= NOW() - INTERVAL '24 hours'
UNION ALL
SELECT 
    'Lab Tests Ordered' as activity,
    COUNT(*) as count
FROM lab_tests
WHERE order_date >= NOW() - INTERVAL '24 hours'
UNION ALL
SELECT 
    'Prescriptions Created' as activity,
    COUNT(*) as count
FROM prescriptions
WHERE prescription_date >= NOW() - INTERVAL '24 hours';

-- 18. Clean up test data (use carefully!)
-- DELETE FROM patients WHERE first_name = 'Test' AND last_name = 'Patient';

-- =====================================================
-- PERFORMANCE MONITORING
-- =====================================================

-- 19. Check database connections
SELECT 
    datname,
    usename,
    client_addr,
    state,
    query_start
FROM pg_stat_activity
WHERE datname = 'hu_hospital_management';

-- 20. Table row counts
SELECT 
    'patients' as table_name,
    COUNT(*) as row_count
FROM patients
UNION ALL
SELECT 
    'doctors' as table_name,
    COUNT(*) as row_count
FROM doctors
UNION ALL
SELECT 
    'lab_tests' as table_name,
    COUNT(*) as row_count
FROM lab_tests
UNION ALL
SELECT 
    'prescriptions' as table_name,
    COUNT(*) as row_count
FROM prescriptions
UNION ALL
SELECT 
    'medications' as table_name,
    COUNT(*) as row_count
FROM medications;

-- =====================================================
-- BACKUP AND RESTORE COMMANDS (run in terminal)
-- =====================================================

-- Backup database:
-- pg_dump -U postgres -h localhost hu_hospital_management > hospital_backup.sql

-- Restore database:
-- psql -U postgres -h localhost -d hu_hospital_management < hospital_backup.sql