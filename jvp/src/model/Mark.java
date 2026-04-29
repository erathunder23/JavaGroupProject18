package model;

import java.math.BigDecimal;

public class Mark {
    private int markId;
    private int studentId;
    private String studentName;
    private String registrationNo;
    private int courseId;
    private String courseCode;
    private String courseName;
    private String examType; // CA, ESA, final
    private double marksObtained;
    private double maxMarks;
    private String grade;
    private double gradePoints;

    // Constructors
    public Mark() {}

    // Getters and Setters
    public int getMarkId() { return markId; }
    public void setMarkId(int markId) { this.markId = markId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(double marksObtained) { this.marksObtained = marksObtained; }

    public double getMaxMarks() { return maxMarks; }
    public void setMaxMarks(double maxMarks) { this.maxMarks = maxMarks; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public double getGradePoints() { return gradePoints; }
    public void setGradePoints(double gradePoints) { this.gradePoints = gradePoints; }

    // Method to calculate grade based on marks
    public static String calculateGrade(double marks) {
        if (marks >= 85) return "A+";
        else if (marks >= 75) return "A";
        else if (marks >= 70) return "A-";
        else if (marks >= 65) return "B+";
        else if (marks >= 60) return "B";
        else if (marks >= 55) return "B-";
        else if (marks >= 50) return "C+";
        else if (marks >= 45) return "C";
        else if (marks >= 40) return "C-";
        else if (marks >= 35) return "D";
        else return "E";
    }

    // Method to calculate grade points
    public static double calculateGradePoints(double marks) {
        if (marks >= 85) return 4.0;
        else if (marks >= 75) return 4.0;
        else if (marks >= 70) return 3.7;
        else if (marks >= 65) return 3.3;
        else if (marks >= 60) return 3.0;
        else if (marks >= 55) return 2.7;
        else if (marks >= 50) return 2.3;
        else if (marks >= 45) return 2.0;
        else if (marks >= 40) return 1.7;
        else if (marks >= 35) return 1.3;
        else return 0.0;
    }
}