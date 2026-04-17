import java.sql.Date;

public class Medical {

    private String medicalID;
    private String stuID;
    private String courseCode;
    private Date submissionDate;
    private String description;
    private String status;

    public Medical(String medicalID, String stuID, String courseCode, Date submissionDate, String description, String status) {
        this.medicalID = medicalID;
        this.stuID = stuID;
        this.courseCode = courseCode;
        this.submissionDate = submissionDate;
        this.description = description;
        this.status = status;
    }

    public String getMedicalID() { return medicalID; }
    public String getStuID() { return stuID; }
    public String getCourseCode() { return courseCode; }
    public Date getSubmissionDate() { return submissionDate; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }

    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }
}