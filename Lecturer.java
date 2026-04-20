package com.tecmis.model;

public class Lecturer extends User {
    private String deptID;

    public Lecturer() { super(); }
    public Lecturer(String id, String firstName, String lastName, String email,
                    String gender, String dob, String telephone, String password, String deptID) {
        super(id, firstName, lastName, email, gender, dob, telephone, password);
        this.deptID = deptID;
    }
    public String getDeptID() { return deptID; }
    public void setDeptID(String deptID) { this.deptID = deptID; }
}