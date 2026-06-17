// model/Lecturer.java
package model;

// INHERITANCE: Lecturer extends Person
public class Lecturer extends Person {

    private String deptID;

    public Lecturer(String lecturerID, String firstName, String lastName,
                    String email, String gender, String dob,
                    String telephone, String deptID, String password) {

        // Call parent constructor
        super(lecturerID, firstName, lastName, email, gender, dob, telephone, password);
        this.deptID = deptID;
    }

    // POLYMORPHISM: Implementing abstract method from Person
    @Override
    public String getRole() {
        return "Lecturer";
    }

    public String getDeptID() { return deptID; }
    public void setDeptID(String deptID) { this.deptID = deptID; }

    @Override
    public String toString() {
        return getFirstName() + " " + getLastName() + " | Dept: " + deptID;
    }
}
