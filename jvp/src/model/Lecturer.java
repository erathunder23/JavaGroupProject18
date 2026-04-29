package model;

public class Lecturer extends User {
    private int lecturerId;
    private String employeeNo;
    private String department;

    public Lecturer() {}

    public int getLecturerId() { return lecturerId; }
    public void setLecturerId(int lecturerId) { this.lecturerId = lecturerId; }

    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}