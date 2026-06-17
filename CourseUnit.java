// model/CourseUnit.java
package model;

public class CourseUnit {

    private String courseCode;
    private String title;
    private int credit;
    private int theoryHrs;
    private int practicalHrs;
    private String moduleID;
    private String lecturerID;

    public CourseUnit(String courseCode, String title, int credit,
                      int theoryHrs, int practicalHrs,
                      String moduleID, String lecturerID) {
        this.courseCode = courseCode;
        this.title = title;
        this.credit = credit;
        this.theoryHrs = theoryHrs;
        this.practicalHrs = practicalHrs;
        this.moduleID = moduleID;
        this.lecturerID = lecturerID;
    }

    // Getters and Setters
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getCredit() { return credit; }
    public void setCredit(int credit) { this.credit = credit; }

    public int getTheoryHrs() { return theoryHrs; }
    public void setTheoryHrs(int theoryHrs) { this.theoryHrs = theoryHrs; }

    public int getPracticalHrs() { return practicalHrs; }
    public void setPracticalHrs(int practicalHrs) { this.practicalHrs = practicalHrs; }

    public String getModuleID() { return moduleID; }
    public void setModuleID(String moduleID) { this.moduleID = moduleID; }

    public String getLecturerID() { return lecturerID; }
    public void setLecturerID(String lecturerID) { this.lecturerID = lecturerID; }
}

