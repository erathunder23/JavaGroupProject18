package model;

public class Course {
    private int courseId;
    private String courseCode;
    private String courseName;
    private int credits;
    private int theoryCredits;
    private int practicalCredits;
    private int semester;
    private int lecturerId;
    private String lecturerName;
    private String description;
    private String materials;

    // Constructors
    public Course() {}

    public Course(int courseId, String courseCode, String courseName, int credits) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
    }

    // Getters and Setters
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public int getTheoryCredits() { return theoryCredits; }
    public void setTheoryCredits(int theoryCredits) { this.theoryCredits = theoryCredits; }

    public int getPracticalCredits() { return practicalCredits; }
    public void setPracticalCredits(int practicalCredits) { this.practicalCredits = practicalCredits; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public int getLecturerId() { return lecturerId; }
    public void setLecturerId(int lecturerId) { this.lecturerId = lecturerId; }

    public String getLecturerName() { return lecturerName; }
    public void setLecturerName(String lecturerName) { this.lecturerName = lecturerName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMaterials() { return materials; }
    public void setMaterials(String materials) { this.materials = materials; }
}