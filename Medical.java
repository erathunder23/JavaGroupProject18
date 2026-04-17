public class Medical {
    private String medicalID;
    private String stuID;
    private String courseCode;
    private String date;
    private String description;
    private String status;

    public Medical(String medicalID, String stuID, String courseCode, String date, String description, String status) {
        this.medicalID = medicalID;
        this.stuID = stuID;
        this.courseCode = courseCode;
        this.date = date;
        this.description = description;
        this.status = status;
    }

    public String getMedicalID() { return medicalID; }
    public String getStuID() { return stuID; }
    public String getCourseCode() { return courseCode; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
}