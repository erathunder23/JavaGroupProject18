package model;

import java.util.Date;

public class Medical {
    private int    medicalId;
    private int    studentId;
    private int    courseId;        // FK to courses table
    private String courseCode;      // joined from courses for display
    private String sessionType;     // theory / practical / both
    private Date   submissionDate;
    private Date   medicalDate;     // start date
    private Date   endDate;
    private String reason;
    private String documentPath;
    private String status;
    private String remarks;

    public Medical() {}

    public int    getMedicalId()                { return medicalId; }
    public void   setMedicalId(int v)           { this.medicalId = v; }

    public int    getStudentId()                { return studentId; }
    public void   setStudentId(int v)           { this.studentId = v; }

    public int    getCourseId()                 { return courseId; }
    public void   setCourseId(int v)            { this.courseId = v; }

    public String getCourseCode()               { return courseCode; }
    public void   setCourseCode(String v)       { this.courseCode = v; }

    public String getSessionType()              { return sessionType; }
    public void   setSessionType(String v)      { this.sessionType = v; }

    public Date   getSubmissionDate()           { return submissionDate; }
    public void   setSubmissionDate(Date v)     { this.submissionDate = v; }

    public Date   getMedicalDate()              { return medicalDate; }
    public void   setMedicalDate(Date v)        { this.medicalDate = v; }

    public Date   getEndDate()                  { return endDate; }
    public void   setEndDate(Date v)            { this.endDate = v; }

    public String getReason()                   { return reason; }
    public void   setReason(String v)           { this.reason = v; }

    public String getDocumentPath()             { return documentPath; }
    public void   setDocumentPath(String v)     { this.documentPath = v; }

    public String getStatus()                   { return status; }
    public void   setStatus(String v)           { this.status = v; }

    public String getRemarks()                  { return remarks; }
    public void   setRemarks(String v)          { this.remarks = v; }
}
