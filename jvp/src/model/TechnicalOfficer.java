package model;

public class TechnicalOfficer extends User {
    private int officerId;
    private String employeeNo;
    private String department;

    public TechnicalOfficer() {}

    public int getOfficerId() { return officerId; }
    public void setOfficerId(int officerId) { this.officerId = officerId; }

    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}