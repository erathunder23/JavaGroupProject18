// model/Department.java
package model;

public class Department {

    private String deptID;
    private String deptName;

    public Department(String deptID, String deptName) {
        this.deptID = deptID;
        this.deptName = deptName;
    }

    // Getters and Setters
    public String getDeptID() { return deptID; }
    public void setDeptID(String deptID) { this.deptID = deptID; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    @Override
    public String toString() {
        return deptID + " - " + deptName;
    }
}

