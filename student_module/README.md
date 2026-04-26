# Student Module - Faculty of Technology Management System

## Project Structure

```
student_module/
├── src/
│   ├── Main.java                        ← Entry point
│   ├── model/
│   │   ├── User.java                    ← Base user model
│   │   ├── Student.java                 ← Student model (extends User)
│   │   ├── Course.java
│   │   ├── Attendance.java
│   │   ├── Medical.java
│   │   ├── Mark.java
│   │   ├── Notice.java
│   │   ├── Timetable.java
│   │   └── CourseMaterial.java
│   ├── dao/
│   │   ├── DatabaseConnection.java      ← MySQL connection
│   │   ├── StudentDAO.java              ← Student CRUD + auth
│   │   ├── CourseDAO.java
│   │   ├── AttendanceDAO.java
│   │   ├── MarkDAO.java
│   │   ├── MedicalDAO.java
│   │   ├── NoticeDAO.java
│   │   ├── TimetableDAO.java
│   │   └── CourseMaterialDAO.java
│   ├── ui/
│   │   ├── LoginUI.java                 ← Student-only login
│   │   └── StudentUI.java               ← Main dashboard
│   └── utils/
│       ├── SessionManager.java
│       ├── ButtonStyleUtil.java
│       └── LogoUtil.java
├── student_schema.sql                   ← DB setup script
└── README.md
```

## Setup Instructions

### 1. Database
```sql
mysql -u root -p < student_schema.sql
```

### 2. Configure DB password
Open `src/dao/DatabaseConnection.java` and update:
```java
private static final String PASSWORD = "your_password_here";
```

### 3. Dependencies
Add `mysql-connector-j-x.x.x.jar` to your classpath.
Download from: https://dev.mysql.com/downloads/connector/j/

### 4. Compile (from `src/` directory)
```bash
javac -cp .;path/to/mysql-connector.jar **/*.java
```

### 5. Run
```bash
java -cp .;path/to/mysql-connector.jar Main
```

## Sample Login Credentials
| Username | Password | Name              |
|----------|----------|-------------------|
| stu001   | pass123  | A. Krishnikan     |
| stu002   | pass123  | A.W.K.B.S. Widanage |
| stu003   | pass123  | P.H.A.E. Jayarathna |
| stu004   | pass123  | R.D.T.D. Arunawila  |

## Student Dashboard Features
- **Profile Card** — photo, registration details, current GPA
- **My Courses** — enrolled courses for current semester
- **Lecture Materials** — download files uploaded by lecturers
- **Attendance** — view % with medical leave consideration
- **Marks & GPA** — marks per course, SGPA calculation
- **Medical Records** — submit and track medical leave requests
- **Timetable** — weekly class schedule
- **Notices** — announcements targeted to students

## Key Design Decisions
- `StudentDAO` replaces `UserDAO` — contains only student-relevant methods
- Login portal rejects non-student roles with a clear message
- All DAO and model classes are generated from the original `schema.sql`
