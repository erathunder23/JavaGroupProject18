public class TechOfficer {

    private String techID;
    private String name;

    public TechOfficer(String techID, String name) {
        this.techID = techID;
        this.name = name;
    }

    public void displayRole() {
        System.out.println("Technical Officer Access Granted");
    }

    public void updateAttendanceRecord() {
        System.out.println("Attendance Controlled by Tech Officer");
    }

    public void updateMedicalRecord() {
        System.out.println("Medical Controlled by Tech Officer");
    }
}