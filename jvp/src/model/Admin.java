package model;

public class Admin extends User {
    private int adminId;
    private String employeeNo;
    private String department;
    private String accessLevel;

    public Admin() {}

    public Admin(int adminId, String employeeNo, String department, String accessLevel) {
        this.adminId = adminId;
        this.employeeNo = employeeNo;
        this.department = department;
        this.accessLevel = accessLevel;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", employeeNo='" + employeeNo + '\'' +
                ", department='" + department + '\'' +
                ", accessLevel='" + accessLevel + '\'' +
                '}';
    }
}