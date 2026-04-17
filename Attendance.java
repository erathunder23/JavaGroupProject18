public class Attendance {
    private String stuID;
    private String courseCode;
    private String date;
    private String type;
    private String status;

    public Attendance(String stuID, String courseCode, String date, String type, String status) {
        this.stuID = stuID;
        this.courseCode = courseCode;
        this.date = date;
        this.type = type;
        this.status = status;
    }

    public String getStuID() { return stuID; }
    public String getCourseCode() { return courseCode; }
    public String getDate() { return date; }
    public String getType() { return type; }
    public String getStatus() { return status; }
}