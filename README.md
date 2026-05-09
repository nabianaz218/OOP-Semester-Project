# OOP-Semester-Project
Java OOP semester project (Spring 2026)
# Social Media Analyzer (Semester OOP Project)

### **Group Information**
* **Member 1 :** Ifrah Akhtar | **CMS ID:** [023-25-0213] | **Section:** [B]
* **Member 2:** Nabia Naz | **CMS ID:** [023-25-0224] | **Section:** [B]
* **Member 3:** [Khadija Khalid] | **CMS ID:** [023-25-0217] | **Section:** [B]

### **Project Purpose**
The **Social Media Analyzer** is a Java-based application designed to track and analyze user engagement metrics.It provides an interface for managing user accounts and generating reports on posts, likes, followers, and comments using a persistent MySQL database. 

### **Core Modules**
**UI/UX Layer:** `Main.java` (Login), `Dashboard.java`, and `AnalysisResult.java` for graphical interaction. 
**Management Service:** `UserManagement` handles account creation, updates, and deletions. 
***Data Layer:** JDBC integration for real-time SQL data processing and persistence. 

### **OOP Features Implemented**
* **Inheritance:** UI classes extend `JFrame` and custom panels extend `JPanel`. 
* **Polymorphism:** Use of interfaces (`ActionListener`) and method overriding (`paintComponent`). 
* **Encapsulation:** Private data members and protected methods for secure data handling.
* **Collections:** Use of `ArrayList` and `Vector` for managing dynamic user data. 
---

### **How to Compile and Run**
**1. Prerequisites:**
* **JDK:** Version 17 or higher.
* **Database:** MySQL Server 8.0+.
* **Driver:** `mysql-connector-j` JAR.

**2. Setup Database:**
* Locate the `database_setup.sql` file in this repository.
* Run this script in your MySQL terminal to create the `datab` schema and all required tables. 

**3. Run Application:**
* Add the MySQL Connector JAR to your IDE's Build Path.
* Ensure images (`login.png`, `login2.png`, `login3.png`, `image_9.png`) are in the root directory. 
* Compile and run `Main.java`. 

**4. Login Credentials:**
* **Username:** root
* **Password:** n@bIa123 

---

### **Submission Deliverables**
* **Video Link:** [INSERT YOUR LINK HERE]  
* **GitHub Repo:** [https://github.com/nabianaz218/OOP-Semester-Project]
