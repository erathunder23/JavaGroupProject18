-- Create Database
CREATE DATABASE IF NOT EXISTS faculty_technology_db;
USE faculty_technology_db;

-- Users table (base table for all users)
CREATE TABLE users (
                       user_id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       password VARCHAR(100) NOT NULL,
                       full_name VARCHAR(100) NOT NULL,
                       email VARCHAR(100),
                       phone VARCHAR(15),
                       role ENUM('admin', 'lecturer', 'student', 'technical_officer') NOT NULL,
                       profile_picture VARCHAR(255),
                       contact_details TEXT,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Students table
CREATE TABLE students (
                          student_id INT PRIMARY KEY AUTO_INCREMENT,
                          user_id INT UNIQUE NOT NULL,
                          registration_no VARCHAR(20) UNIQUE NOT NULL,
                          batch_year INT NOT NULL,
                          current_semester INT,
                          is_repeat BOOLEAN DEFAULT FALSE,
                          is_batch_missed BOOLEAN DEFAULT FALSE,
                          FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Lecturers table
CREATE TABLE lecturers (
                           lecturer_id INT PRIMARY KEY AUTO_INCREMENT,
                           user_id INT UNIQUE NOT NULL,
                           employee_no VARCHAR(20) UNIQUE NOT NULL,
                           department VARCHAR(100),
                           FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Technical Officers table
CREATE TABLE technical_officers (
                                    officer_id INT PRIMARY KEY AUTO_INCREMENT,
                                    user_id INT UNIQUE NOT NULL,
                                    employee_no VARCHAR(20) UNIQUE NOT NULL,
                                    department VARCHAR(100),
                                    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Courses table
CREATE TABLE courses (
                         course_id INT PRIMARY KEY AUTO_INCREMENT,
                         course_code VARCHAR(20) UNIQUE NOT NULL,
                         course_name VARCHAR(100) NOT NULL,
                         credits INT NOT NULL,
                         theory_credits INT DEFAULT 0,
                         practical_credits INT DEFAULT 0,
                         semester INT,
                         lecturer_id INT,
                         description TEXT,
                         materials TEXT,
                         FOREIGN KEY (lecturer_id) REFERENCES lecturers(lecturer_id)
);

-- Student Courses (enrollment)
CREATE TABLE student_courses (
                                 enrollment_id INT PRIMARY KEY AUTO_INCREMENT,
                                 student_id INT NOT NULL,
                                 course_id INT NOT NULL,
                                 enrollment_date DATE,
                                 FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
                                 FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
                                 UNIQUE KEY unique_enrollment (student_id, course_id)
);

-- Attendance table
CREATE TABLE attendance (
                            attendance_id INT PRIMARY KEY AUTO_INCREMENT,
                            student_id INT NOT NULL,
                            course_id INT NOT NULL,
                            session_date DATE NOT NULL,
                            session_type ENUM('theory', 'practical') NOT NULL,
                            session_number INT NOT NULL,
                            status ENUM('present', 'absent') DEFAULT 'absent',
                            FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
                            FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
                            UNIQUE KEY unique_attendance (student_id, course_id, session_date, session_type)
);

-- Medical records
CREATE TABLE medicals (
                          medical_id INT PRIMARY KEY AUTO_INCREMENT,
                          student_id INT NOT NULL,
                          submission_date DATE NOT NULL,
                          medical_date DATE NOT NULL,
                          reason TEXT,
                          document_path VARCHAR(255),
                          status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
                          approved_by INT,
                          approval_date DATE,
                          remarks TEXT,
                          FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
                          FOREIGN KEY (approved_by) REFERENCES users(user_id)
);

-- Marks table
CREATE TABLE marks (
                       mark_id INT PRIMARY KEY AUTO_INCREMENT,
                       student_id INT NOT NULL,
                       course_id INT NOT NULL,
                       exam_type ENUM('CA', 'ESA', 'final') NOT NULL,
                       marks_obtained DECIMAL(5,2),
                       max_marks DECIMAL(5,2) DEFAULT 100,
                       grade VARCHAR(2),
                       grade_points DECIMAL(3,2),
                       FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
                       FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
                       UNIQUE KEY unique_mark (student_id, course_id, exam_type)
);

-- Notices table
CREATE TABLE notices (
                         notice_id INT PRIMARY KEY AUTO_INCREMENT,
                         title VARCHAR(200) NOT NULL,
                         content TEXT NOT NULL,
                         target_role ENUM('all', 'student', 'lecturer', 'technical_officer', 'admin') DEFAULT 'all',
                         created_by INT,
                         created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         expiry_date DATE,
                         FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- Timetable table
CREATE TABLE timetable (
                           timetable_id INT PRIMARY KEY AUTO_INCREMENT,
                           course_id INT NOT NULL,
                           day_of_week ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday') NOT NULL,
                           start_time TIME NOT NULL,
                           end_time TIME NOT NULL,
                           session_type ENUM('theory', 'practical') NOT NULL,
                           venue VARCHAR(100),
                           semester INT NOT NULL,
                           FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE
);

-- GPA Calculation table (for CGPA tracking)
CREATE TABLE gpa_records (
                             record_id INT PRIMARY KEY AUTO_INCREMENT,
                             student_id INT NOT NULL,
                             semester INT NOT NULL,
                             sgpa DECIMAL(3,2),
                             cgpa DECIMAL(3,2),
                             calculated_date DATE,
                             FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- Insert sample data
-- Admin account (password: admin123)
INSERT INTO users (username, password, full_name, email, phone, role) VALUES
    ('admin', 'admin123', 'System Administrator', 'admin@techfaculty.edu', '011-1234567', 'admin');

-- Insert sample courses for current semester
INSERT INTO courses (course_code, course_name, credits, theory_credits, practical_credits, semester, description) VALUES
                                                                                                                      ('ICT2132', 'Object Oriented Programming Practicum', 4, 2, 2, 2, 'Java programming with OOP concepts'),
                                                                                                                      ('ICT2131', 'Database Management Systems', 3, 2, 1, 2, 'SQL and Database Design'),
                                                                                                                      ('ICT2133', 'Web Development', 3, 2, 1, 2, 'HTML, CSS, JavaScript'),
                                                                                                                      ('ICT2134', 'Software Engineering', 3, 3, 0, 2, 'Software Development Lifecycle');

-- Insert sample lecturers
INSERT INTO users (username, password, full_name, email, phone, role) VALUES
                                                                          ('lecturer1', 'pass123', 'Dr. Kamal Perera', 'kamal@techfaculty.edu', '071-1234561', 'lecturer'),
                                                                          ('lecturer2', 'pass123', 'Prof. Nimal Silva', 'nimal@techfaculty.edu', '071-1234562', 'lecturer'),
                                                                          ('lecturer3', 'pass123', 'Dr. Amali Fernando', 'amali@techfaculty.edu', '071-1234563', 'lecturer'),
                                                                          ('lecturer4', 'pass123', 'Ms. Ruwanthi Jayawardena', 'ruwanthi@techfaculty.edu', '071-1234564', 'lecturer'),
                                                                          ('lecturer5', 'pass123', 'Mr. Chaminda Rathnayake', 'chaminda@techfaculty.edu', '071-1234565', 'lecturer');

INSERT INTO lecturers (user_id, employee_no, department) VALUES
                                                             (2, 'LEC001', 'ICT'),
                                                             (3, 'LEC002', 'ICT'),
                                                             (4, 'LEC003', 'ICT'),
                                                             (5, 'LEC004', 'ICT'),
                                                             (6, 'LEC005', 'ICT');

-- Assign lecturers to courses
UPDATE courses SET lecturer_id = 1 WHERE course_code = 'ICT2132';
UPDATE courses SET lecturer_id = 2 WHERE course_code = 'ICT2131';
UPDATE courses SET lecturer_id = 3 WHERE course_code = 'ICT2133';
UPDATE courses SET lecturer_id = 4 WHERE course_code = 'ICT2134';

-- Insert sample technical officers
INSERT INTO users (username, password, full_name, email, phone, role) VALUES
                                                                          ('tech1', 'pass123', 'Saman Bandara', 'saman@techfaculty.edu', '077-1234561', 'technical_officer'),
                                                                          ('tech2', 'pass123', 'Sunil Kumara', 'sunil@techfaculty.edu', '077-1234562', 'technical_officer'),
                                                                          ('tech3', 'pass123', 'Priyantha Weerasinghe', 'priyantha@techfaculty.edu', '077-1234563', 'technical_officer'),
                                                                          ('tech4', 'pass123', 'Lakshman Jayasinghe', 'lakshman@techfaculty.edu', '077-1234564', 'technical_officer');

INSERT INTO technical_officers (user_id, employee_no, department) VALUES
                                                                      (7, 'TEC001', 'ICT'),
                                                                      (8, 'TEC002', 'ICT'),
                                                                      (9, 'TEC003', 'ICT'),
                                                                      (10, 'TEC004', 'ICT');

-- Insert sample students (20 students including repeating and batch missed)
INSERT INTO users (username, password, full_name, email, phone, role) VALUES
                                                                          ('stu001', 'pass123', 'A. Krishnikan', 'krishnikan@student.edu', '076-1234001', 'student'),
                                                                          ('stu002', 'pass123', 'A.W.K.B.S. Widanage', 'widanage@student.edu', '076-1234002', 'student'),
                                                                          ('stu003', 'pass123', 'P.H.A.E. Jayarathna', 'jayarathna@student.edu', '076-1234003', 'student'),
                                                                          ('stu004', 'pass123', 'R.D.T.D. Arunawila', 'arunawila@student.edu', '076-1234004', 'student');

-- Add remaining 16 students similarly (simplified for demo)
-- For production, add all 20 students

INSERT INTO students (user_id, registration_no, batch_year, current_semester) VALUES
                                                                                  (11, 'TG/2023/1777', 2023, 2),
                                                                                  (12, 'TG/2023/1703', 2023, 2),
                                                                                  (13, 'TG/2023/1723', 2023, 2),
                                                                                  (14, 'TG/2023/1764', 2023, 2);

-- Insert timetable entries
INSERT INTO timetable (course_id, day_of_week, start_time, end_time, session_type, venue, semester) VALUES
                                                                                                        (1, 'Monday', '09:00:00', '11:00:00', 'theory', 'ICT Lab 01', 2),
                                                                                                        (1, 'Wednesday', '14:00:00', '16:00:00', 'practical', 'ICT Lab 01', 2),
                                                                                                        (2, 'Tuesday', '10:00:00', '12:00:00', 'theory', 'Room 201', 2),
                                                                                                        (3, 'Thursday', '09:00:00', '11:00:00', 'theory', 'Room 202', 2),
                                                                                                        (4, 'Friday', '11:00:00', '13:00:00', 'theory', 'Room 203', 2);

-- Insert sample notices
INSERT INTO notices (title, content, target_role, created_by, expiry_date) VALUES
                                                                               ('Welcome to Semester 2', 'Welcome all students to the second semester. Please check your timetables.', 'all', 1, DATE_ADD(CURDATE(), INTERVAL 30 DAY)),
                                                                               ('Assignment Submission', 'OOP assignment deadline extended to next Friday.', 'student', 1, DATE_ADD(CURDATE(), INTERVAL 7 DAY));

-- Insert sample attendance (15 sessions each for theory and practical)
-- This will be inserted dynamically through the application

-- Admins table
CREATE TABLE admins (
                        admin_id INT PRIMARY KEY AUTO_INCREMENT,
                        user_id INT UNIQUE NOT NULL,
                        employee_no VARCHAR(20) UNIQUE NOT NULL,
                        department VARCHAR(100),
                        access_level ENUM('super', 'full', 'limited') DEFAULT 'full',
                        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Insert sample admin data
INSERT INTO users (username, password, full_name, email, phone, role) VALUES
    ('admin', 'admin123', 'System Administrator', 'admin@techfaculty.edu', '011-1234567', 'admin');

INSERT INTO admins (user_id, employee_no, department, access_level) VALUES
    (1, 'ADM001', 'Administration', 'super');

-- Add materials table for lecture notes
CREATE TABLE course_materials (
                                  material_id INT PRIMARY KEY AUTO_INCREMENT,
                                  course_id INT NOT NULL,
                                  lecturer_id INT NOT NULL,
                                  title VARCHAR(200) NOT NULL,
                                  description TEXT,
                                  file_path VARCHAR(500) NOT NULL,
                                  file_name VARCHAR(200) NOT NULL,
                                  file_size BIGINT,
                                  uploaded_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
                                  FOREIGN KEY (lecturer_id) REFERENCES lecturers(lecturer_id) ON DELETE CASCADE
);

-- Add column to courses table for materials
ALTER TABLE courses ADD COLUMN lecture_materials TEXT;

-- Add profile_picture column to users table if not exists
ALTER TABLE users MODIFY COLUMN profile_picture VARCHAR(500);


-- Add attachment columns to notices table
ALTER TABLE notices ADD COLUMN attachment_path VARCHAR(500);
ALTER TABLE notices ADD COLUMN attachment_name VARCHAR(200);