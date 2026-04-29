package model;

import java.util.Date;

public class Medical {
    private int    medicalId;
    private int    studentId;
    private String studentName;       // joined from users for display
    private String registrationNo;    // joined from students for display
    private int    courseId;          // FK to courses table
    private String courseCode;        // joined from courses for display
    private String sessionType;       // theory / practical / both
    private Date   submissionDate;
    private Date   medicalDate;       // start date
    private Date   endDate;
    private String reason;
    private String documentPath;
    private String status;            // pending, approved, rejected
    private Integer approvedBy;
    private String  approvedByName;
    private Date   approvalDate;
    private String remarks;

    public Medical() {}

    public int    getMedicalId()                   { return medicalId; }
    public void   setMedicalId(int v)              { this.medicalId = v; }

    public int    getStudentId()                   { return studentId; }
    public void   setStudentId(int v)              { this.studentId = v; }

    public String getStudentName()                 { return studentName; }
    public void   setStudentName(String v)         { this.studentName = v; }

    public String getRegistrationNo()              { return registrationNo; }
    public void   setRegistrationNo(String v)      { this.registrationNo = v; }

    public int    getCourseId()                    { return courseId; }
    public void   setCourseId(int v)               { this.courseId = v; }

    public String getCourseCode()                  { return courseCode; }
    public void   setCourseCode(String v)          { this.courseCode = v; }

    public String getSessionType()                 { return sessionType; }
    public void   setSessionType(String v)         { this.sessionType = v; }

    public Date   getSubmissionDate()              { return submissionDate; }
    public void   setSubmissionDate(Date v)        { this.submissionDate = v; }

    public Date   getMedicalDate()                 { return medicalDate; }
    public void   setMedicalDate(Date v)           { this.medicalDate = v; }

    public Date   getEndDate()                     { return endDate; }
    public void   setEndDate(Date v)               { this.endDate = v; }

    public String getReason()                      { return reason; }
    public void   setReason(String v)              { this.reason = v; }

    public String getDocumentPath()                { return documentPath; }
    public void   setDocumentPath(String v)        { this.documentPath = v; }

    public String getStatus()                      { return status; }
    public void   setStatus(String v)              { this.status = v; }

    public Integer getApprovedBy()                 { return approvedBy; }
    public void    setApprovedBy(Integer v)        { this.approvedBy = v; }

    public String getApprovedByName()              { return approvedByName; }
    public void   setApprovedByName(String v)      { this.approvedByName = v; }

    public Date   getApprovalDate()                { return approvalDate; }
    public void   setApprovalDate(Date v)          { this.approvalDate = v; }

    public String getRemarks()                     { return remarks; }
    public void   setRemarks(String v)             { this.remarks = v; }
}
