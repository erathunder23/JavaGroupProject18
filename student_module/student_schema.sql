-- ============================================================
--  Faculty of Technology – Student Module
--  COMPLETE SCHEMA  (drop & recreate for clean setup)
--  Run this once in MySQL Workbench or CLI:
--      mysql -u root -p < student_schema.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS faculty_technology_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE faculty_technology_db;

SET FOREIGN_KEY_CHECKS = 0;

-- ── Users ────────────────────────────────────────────────────
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    user_id         INT PRIMARY KEY AUTO_INCREMENT,
    username        VARCHAR(50)  UNIQUE NOT NULL,
    password        VARCHAR(100) NOT NULL,
    full_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(100),
    phone           VARCHAR(20),
    role            ENUM('admin','lecturer','student','technical_officer') NOT NULL,
    profile_picture VARCHAR(500),
    contact_details TEXT,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ── Lecturers ─────────────────────────────────────────────────
DROP TABLE IF EXISTS lecturers;
CREATE TABLE lecturers (
    lecturer_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id     INT UNIQUE NOT NULL,
    employee_no VARCHAR(20) UNIQUE NOT NULL,
    department  VARCHAR(100),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ── Students ──────────────────────────────────────────────────
DROP TABLE IF EXISTS students;
CREATE TABLE students (
    student_id       INT PRIMARY KEY AUTO_INCREMENT,
    user_id          INT UNIQUE NOT NULL,
    registration_no  VARCHAR(30) UNIQUE NOT NULL,
    batch_year       INT NOT NULL,
    current_semester INT DEFAULT 1,
    is_repeat        TINYINT(1) DEFAULT 0,
    is_batch_missed  TINYINT(1) DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ── Courses ───────────────────────────────────────────────────
DROP TABLE IF EXISTS courses;
CREATE TABLE courses (
    course_id         INT PRIMARY KEY AUTO_INCREMENT,
    course_code       VARCHAR(20) UNIQUE NOT NULL,
    course_name       VARCHAR(150) NOT NULL,
    credits           INT NOT NULL DEFAULT 3,
    theory_credits    INT DEFAULT 0,
    practical_credits INT DEFAULT 0,
    semester          INT NOT NULL DEFAULT 1,
    lecturer_id       INT,
    description       TEXT,
    FOREIGN KEY (lecturer_id) REFERENCES lecturers(lecturer_id) ON DELETE SET NULL
);

-- ── Attendance ────────────────────────────────────────────────
DROP TABLE IF EXISTS attendance;
CREATE TABLE attendance (
    attendance_id  INT PRIMARY KEY AUTO_INCREMENT,
    student_id     INT NOT NULL,
    course_id      INT NOT NULL,
    session_date   DATE NOT NULL,
    session_type   ENUM('theory','practical') NOT NULL,
    session_number INT NOT NULL DEFAULT 1,
    status         ENUM('present','absent') DEFAULT 'absent',
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id)  REFERENCES courses(course_id)   ON DELETE CASCADE,
    UNIQUE KEY uq_att (student_id, course_id, session_date, session_type)
);

-- ── Medicals  (COMPLETE – all columns the app needs) ──────────
DROP TABLE IF EXISTS medicals;
CREATE TABLE medicals (
    medical_id      INT PRIMARY KEY AUTO_INCREMENT,
    student_id      INT NOT NULL,
    course_id       INT,                          -- which course the leave covers
    session_type    VARCHAR(20) DEFAULT 'both',   -- theory / practical / both
    submission_date DATE NOT NULL DEFAULT (CURDATE()),
    medical_date    DATE NOT NULL,                -- start date
    end_date        DATE,                         -- end date
    reason          TEXT,
    document_path   VARCHAR(500),                 -- saved image path
    status          ENUM('pending','approved','rejected') DEFAULT 'pending',
    approved_by     INT,
    approval_date   DATE,
    remarks         TEXT,
    FOREIGN KEY (student_id)  REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id)   REFERENCES courses(course_id)   ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES users(user_id)       ON DELETE SET NULL
);

-- ── Marks ─────────────────────────────────────────────────────
DROP TABLE IF EXISTS marks;
CREATE TABLE marks (
    mark_id        INT PRIMARY KEY AUTO_INCREMENT,
    student_id     INT NOT NULL,
    course_id      INT NOT NULL,
    exam_type      ENUM('CA','ESA','final') NOT NULL,
    marks_obtained DECIMAL(5,2) DEFAULT 0,
    max_marks      DECIMAL(5,2) DEFAULT 100,
    grade          VARCHAR(3),
    grade_points   DECIMAL(3,2) DEFAULT 0,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id)  REFERENCES courses(course_id)   ON DELETE CASCADE,
    UNIQUE KEY uq_mark (student_id, course_id, exam_type)
);

-- ── Notices ───────────────────────────────────────────────────
DROP TABLE IF EXISTS notices;
CREATE TABLE notices (
    notice_id       INT PRIMARY KEY AUTO_INCREMENT,
    title           VARCHAR(200) NOT NULL,
    content         TEXT NOT NULL,
    target_role     ENUM('all','student','lecturer','technical_officer','admin') DEFAULT 'all',
    created_by      INT,
    created_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiry_date     DATE,
    attachment_path VARCHAR(500),
    attachment_name VARCHAR(200),
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- ── Timetable ─────────────────────────────────────────────────
DROP TABLE IF EXISTS timetable;
CREATE TABLE timetable (
    timetable_id INT PRIMARY KEY AUTO_INCREMENT,
    course_id    INT NOT NULL,
    day_of_week  ENUM('Monday','Tuesday','Wednesday','Thursday','Friday','Saturday') NOT NULL,
    start_time   TIME NOT NULL,
    end_time     TIME NOT NULL,
    session_type ENUM('theory','practical') NOT NULL,
    venue        VARCHAR(100),
    semester     INT NOT NULL DEFAULT 1,
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

-- ── Course Materials ──────────────────────────────────────────
DROP TABLE IF EXISTS course_materials;
CREATE TABLE course_materials (
    material_id   INT PRIMARY KEY AUTO_INCREMENT,
    course_id     INT NOT NULL,
    lecturer_id   INT NOT NULL,
    title         VARCHAR(200) NOT NULL,
    description   TEXT,
    file_path     VARCHAR(500) NOT NULL,
    file_name     VARCHAR(200) NOT NULL,
    file_size     BIGINT DEFAULT 0,
    uploaded_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id)   REFERENCES courses(course_id)     ON DELETE CASCADE,
    FOREIGN KEY (lecturer_id) REFERENCES lecturers(lecturer_id) ON DELETE CASCADE
);

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
--  SAMPLE DATA  –  matches semester 1 students
-- ============================================================

-- 1. Admin user
INSERT INTO users (username, password, full_name, email, phone, role) VALUES
('admin', 'admin123', 'System Administrator', 'admin@techfaculty.edu', '011-1234567', 'admin');

-- 2. Lecturers
INSERT INTO users (username, password, full_name, email, phone, role) VALUES
('lec_kamal',    'lec123', 'Dr. Kamal Perera',           'kamal@techfaculty.edu',    '071-2345601', 'lecturer'),
('lec_nimal',    'lec123', 'Prof. Nimal Silva',           'nimal@techfaculty.edu',    '071-2345602', 'lecturer'),
('lec_amali',    'lec123', 'Dr. Amali Fernando',          'amali@techfaculty.edu',    '071-2345603', 'lecturer'),
('lec_ruwanthi', 'lec123', 'Ms. Ruwanthi Jayawardena',    'ruwanthi@techfaculty.edu', '071-2345604', 'lecturer');

INSERT INTO lecturers (user_id, employee_no, department) VALUES
(2, 'EMP-LEC-001', 'Information & Communication Technology'),
(3, 'EMP-LEC-002', 'Information & Communication Technology'),
(4, 'EMP-LEC-003', 'Information & Communication Technology'),
(5, 'EMP-LEC-004', 'Information & Communication Technology');

-- 3. Semester 1 courses
INSERT INTO courses (course_code, course_name, credits, theory_credits, practical_credits, semester, lecturer_id, description) VALUES
('ICT1131', 'Introduction to Computing',              3, 2, 1, 1, 1, 'Fundamentals of computers and IT'),
('ICT1132', 'Programming Fundamentals',               4, 2, 2, 1, 2, 'Intro to programming using Python'),
('ICT1133', 'Mathematics for Computing',              3, 3, 0, 1, 3, 'Discrete maths, logic, sets'),
('ICT1134', 'Communication Skills',                   2, 2, 0, 1, 4, 'Academic and professional writing'),
('ICT1135', 'Computer Hardware & Networking Basics',  3, 2, 1, 1, 1, 'Hardware components and networking');

-- 4. Timetable for semester 1
INSERT INTO timetable (course_id, day_of_week, start_time, end_time, session_type, venue, semester) VALUES
(1, 'Monday',    '08:00:00', '10:00:00', 'theory',    'Hall A',        1),
(2, 'Monday',    '10:00:00', '12:00:00', 'theory',    'ICT Lab 01',    1),
(2, 'Wednesday', '14:00:00', '16:00:00', 'practical', 'ICT Lab 01',    1),
(3, 'Tuesday',   '08:00:00', '10:00:00', 'theory',    'Hall B',        1),
(4, 'Thursday',  '10:00:00', '12:00:00', 'theory',    'Room 201',      1),
(5, 'Friday',    '08:00:00', '10:00:00', 'theory',    'ICT Lab 02',    1),
(5, 'Friday',    '10:00:00', '12:00:00', 'practical', 'ICT Lab 02',    1);

-- 5. Students (semester 1)
INSERT INTO users (username, password, full_name, email, phone, role) VALUES
('stu001', 'pass123', 'A. Krishnikan',          'krishnikan@student.edu',  '076-1234001', 'student'),
('stu002', 'pass123', 'A.W.K.B.S. Widanage',    'widanage@student.edu',    '076-1234002', 'student'),
('stu003', 'pass123', 'P.H.A.E. Jayarathna',    'jayarathna@student.edu',  '076-1234003', 'student'),
('stu004', 'pass123', 'R.D.T.D. Arunawila',     'arunawila@student.edu',   '076-1234004', 'student');

INSERT INTO students (user_id, registration_no, batch_year, current_semester) VALUES
(6,  'TG/2023/1777', 2023, 1),
(7,  'TG/2023/1703', 2023, 1),
(8,  'TG/2023/1723', 2023, 1),
(9,  'TG/2023/1764', 2023, 1);

-- 6. Attendance for student 1 (student_id=1), course ICT1131 (course_id=1)
INSERT INTO attendance (student_id, course_id, session_date, session_type, session_number, status) VALUES
(1,1,'2024-01-15','theory',1,'present'),
(1,1,'2024-01-22','theory',2,'present'),
(1,1,'2024-01-29','theory',3,'absent'),
(1,1,'2024-02-05','theory',4,'present'),
(1,1,'2024-02-12','theory',5,'absent'),
(1,1,'2024-02-19','theory',6,'present'),
(1,1,'2024-02-26','theory',7,'present'),
(1,1,'2024-03-04','theory',8,'absent'),
-- ICT1132 theory
(1,2,'2024-01-15','theory',1,'present'),
(1,2,'2024-01-22','theory',2,'present'),
(1,2,'2024-01-29','theory',3,'present'),
(1,2,'2024-02-05','theory',4,'absent'),
(1,2,'2024-02-12','theory',5,'present'),
-- ICT1132 practical
(1,2,'2024-01-17','practical',1,'present'),
(1,2,'2024-01-24','practical',2,'absent'),
(1,2,'2024-01-31','practical',3,'present'),
(1,2,'2024-02-07','practical',4,'present');

-- 7. Marks for student 1
INSERT INTO marks (student_id, course_id, exam_type, marks_obtained, max_marks, grade, grade_points) VALUES
(1,1,'CA',   72, 100, 'B+', 3.30),
(1,1,'ESA',  65, 100, 'B',  3.00),
(1,1,'final',68, 100, 'B',  3.00),
(1,2,'CA',   85, 100, 'A-', 3.70),
(1,2,'ESA',  78, 100, 'B+', 3.30),
(1,2,'final',82, 100, 'A-', 3.70),
(1,3,'CA',   55, 100, 'C+', 2.30),
(1,3,'ESA',  60, 100, 'B-', 2.70),
(1,3,'final',58, 100, 'C+', 2.30),
(1,4,'CA',   90, 100, 'A',  4.00),
(1,4,'ESA',  88, 100, 'A',  4.00),
(1,4,'final',89, 100, 'A',  4.00);

-- 8. Sample notices (no attachment — attachment_path left NULL intentionally)
INSERT INTO notices (title, content, target_role, created_by, expiry_date) VALUES
('Welcome – Semester 1 2024',
 'Dear students, welcome to the Faculty of Technology. Please collect your timetables from the office. Orientation is on Monday 8 AM in Main Hall.',
 'all', 1, DATE_ADD(CURDATE(), INTERVAL 60 DAY)),
('Library Card Registration',
 'All first-year students must register for their library card by the end of this week. Visit the library with your student ID.',
 'student', 1, DATE_ADD(CURDATE(), INTERVAL 14 DAY)),
('ICT1132 Lab Rules',
 'Students must bring their lab books to every practical session. No food or drinks allowed in the ICT labs.',
 'student', 1, DATE_ADD(CURDATE(), INTERVAL 30 DAY));

-- 9. Sample medical (already submitted, pending)
INSERT INTO medicals (student_id, course_id, session_type, submission_date, medical_date, end_date, reason, status) VALUES
(1, 1, 'theory', CURDATE(), '2024-01-29', '2024-01-29',
 'Fever and flu – visited university medical centre', 'pending');

-- ============================================================
--  HOW TO INSERT DATA  (quick reference)
-- ============================================================
--
-- ADD A STUDENT:
--   INSERT INTO users (username,password,full_name,email,phone,role)
--   VALUES ('stu005','pass123','New Student','new@student.edu','076-0000005','student');
--
--   INSERT INTO students (user_id,registration_no,batch_year,current_semester)
--   VALUES (LAST_INSERT_ID(),'TG/2023/1800',2023,1);
--
-- ADD ATTENDANCE:
--   INSERT INTO attendance (student_id,course_id,session_date,session_type,session_number,status)
--   VALUES (1, 1, '2024-03-11', 'theory', 9, 'present');
--
-- ADD MARKS:
--   INSERT INTO marks (student_id,course_id,exam_type,marks_obtained,max_marks,grade,grade_points)
--   VALUES (1, 5, 'CA', 75, 100, 'B+', 3.30);
--
-- ADD A NOTICE:
--   INSERT INTO notices (title,content,target_role,created_by)
--   VALUES ('Test Notice','Notice content here.','student',1);
--
-- ADD COURSE MATERIAL (file must exist on disk first):
--   INSERT INTO course_materials (course_id,lecturer_id,title,description,file_path,file_name,file_size)
--   VALUES (1, 1, 'Lecture 1 Slides','Introduction slides','uploads/materials/lec1.pdf','lec1.pdf',204800);
--
-- APPROVE A MEDICAL:
--   UPDATE medicals SET status='approved', approved_by=1, approval_date=CURDATE() WHERE medical_id=1;
--
-- ============================================================
