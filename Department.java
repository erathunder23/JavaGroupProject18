package com.tecmis.model;

public class Department {
    private String deptID;
    private String deptName;

    public Department() {}
    public Department(String deptID, String deptName) {
        this.deptID = deptID;
        this.deptName = deptName;
    }
    // getters and setters
    public String getDeptID() { return deptID; }
    public void setDeptID(String deptID) { this.deptID = deptID; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
}