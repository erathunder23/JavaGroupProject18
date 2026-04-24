package model;

import java.util.Date;

public class Medical {
    private int medicalId;
    private int studentId;
    private String studentName;
    private String registrationNo;
    private Date submissionDate;
    private Date medicalDate;
    private String reason;
    private String documentPath;
    private String status; // pending, approved, rejected
    private Integer approvedBy;
    private String approvedByName;
    private Date approvalDate;
    private String remarks;

    // Constructors
    public Medical() {}

    // Getters and Setters
    public int getMedicalId() { return medicalId; }
    public void setMedicalId(int medicalId) { this.medicalId = medicalId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

    public Date getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(Date submissionDate) { this.submissionDate = submissionDate; }

    public Date getMedicalDate() { return medicalDate; }
    public void setMedicalDate(Date medicalDate) { this.medicalDate = medicalDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getDocumentPath() { return documentPath; }
    public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Integer approvedBy) { this.approvedBy = approvedBy; }

    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }

    public Date getApprovalDate() { return approvalDate; }
    public void setApprovalDate(Date approvalDate) { this.approvalDate = approvalDate; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}