# PostgreSQL Database Setup for HU Hospital Management System

## 📋 Prerequisites

1. **PostgreSQL installed** on your system
2. **pgAdmin 4** installed for database management
3. **PostgreSQL JDBC Driver** for Java connection

## 🚀 Step-by-Step Setup

### Step 1: Install PostgreSQL and pgAdmin 4

**Windows:**
1. Download PostgreSQL from: https://www.postgresql.org/download/windows/
2. Run the installer and follow the setup wizard
3. Remember the password you set for the `postgres` user
4. pgAdmin 4 is included in the installation

**Alternative - Using pgAdmin 4 only:**
1. Download pgAdmin 4 from: https://www.pgadmin.org/download/
2. Install and connect to an existing PostgreSQL server

### Step 2: Connect to PostgreSQL using pgAdmin 4

1. **Open pgAdmin 4**
2. **Create a new server connection:**
   - Right-click "Servers" → "Create" → "Server"
   - **General Tab:**
     - Name: `HU Hospital Server`
   - **Connection Tab:**
     - Host name/address: `localhost`
     - Port: `5432`
     - Maintenance database: `postgres`
     - Username: `postgres`
     - Password: `[your postgres password]`
3. **Click "Save"**

### Step 3: Create the Database

1. **In pgAdmin 4:**
   - Right-click on your server → "Create" → "Database"
   - Database name: `hu_hospital_management`
   - Owner: `postgres`
   - Click "Save"

2. **Or use SQL:**
   ```sql
   CREATE DATABASE hu_hospital_management;
   ```

### Step 4: Run the Database Schema

1. **Open Query Tool:**
   - Right-click on `hu_hospital_management` database
   - Select "Query Tool"

2. **Copy and paste the entire content from `hospital_schema.sql`**

3. **Execute the script:**
   - Click the "Execute" button (▶️) or press F5
   - You should see "Query returned successfully"

### Step 5: Verify Database Creation

**Check if tables were created:**
```sql
-- List all tables
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public';
```

**Expected tables:**
- doctors
- patients
- lab_tests
- prescriptions
- medications
- consultations

**Check sample data:**
```sql
-- View sample doctors
SELECT * FROM doctors;

-- View sample patients
SELECT * FROM patients;

-- View patient queue
SELECT * FROM patient_queue;
```

### Step 6: Configure Java Application

1. **Add PostgreSQL JDBC Driver to your project:**
   - Download from: https://jdbc.postgresql.org/download.html
   - Or add to Maven/Gradle dependencies:
   ```xml
   <dependency>
       <groupId>org.postgresql</groupId>
       <artifactId>postgresql</artifactId>
       <version>42.6.0</version>
   </dependency>
   ```

2. **Update DatabaseConfig.java:**
   ```java
   private static final String DB_USER = "postgres"; // Your username
   private static final String DB_PASSWORD = "your_password"; // Your password
   ```

3. **Add JDBC JAR to NetBeans project:**
   - Right-click project → Properties
   - Libraries → Add JAR/Folder
   - Select postgresql-xx.x.x.jar

### Step 7: Test Database Connection

**Run the database test:**
```java
// In your Java application
public static void main(String[] args) {
    DatabaseConfig.printConnectionInfo();
    
    if (DatabaseConfig.testConnection()) {
        System.out.println("✅ Database connection successful!");
    } else {
        System.out.println("❌ Database connection failed!");
    }
}
```

## 🔧 Common Connection Issues

### Issue 1: "Connection refused"
**Solution:**
- Ensure PostgreSQL service is running
- Check if port 5432 is open
- Verify host and port in connection string

### Issue 2: "Authentication failed"
**Solution:**
- Verify username and password
- Check pg_hba.conf file for authentication method
- Try connecting with pgAdmin first

### Issue 3: "Database does not exist"
**Solution:**
- Create the database first using pgAdmin or SQL
- Ensure database name matches exactly

### Issue 4: "JDBC Driver not found"
**Solution:**
- Download PostgreSQL JDBC driver
- Add JAR file to project classpath
- Verify driver class name: `org.postgresql.Driver`

## 📊 Useful PostgreSQL Commands

```sql
-- Connect to database
\c hu_hospital_management;

-- List all tables
\dt

-- Describe table structure
\d patients

-- View table data with limit
SELECT * FROM patients LIMIT 5;

-- Check database size
SELECT pg_size_pretty(pg_database_size('hu_hospital_management'));

-- Show current connections
SELECT * FROM pg_stat_activity WHERE datname = 'hu_hospital_management';
```

## 🔐 Security Recommendations

1. **Create a dedicated database user:**
```sql
CREATE USER hospital_user WITH PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE hu_hospital_management TO hospital_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO hospital_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO hospital_user;
```

2. **Update connection to use dedicated user:**
```java
private static final String DB_USER = "hospital_user";
private static final String DB_PASSWORD = "secure_password";
```

3. **Enable SSL (for production):**
```java
props.setProperty("ssl", "true");
props.setProperty("sslmode", "require");
```

## 📝 Next Steps

After successful setup:
1. ✅ Database and tables created
2. ✅ Sample data inserted
3. ✅ Java connection configured
4. ✅ Test connection successful

You can now:
- Run your Java application with database integration
- Use pgAdmin 4 to view and manage data
- Execute queries to test functionality
- Monitor database performance and connections