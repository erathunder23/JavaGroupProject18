package model;

public class Student extends User {
    private int studentId;
    private String registrationNo;
    private int batchYear;
    private int currentSemester;
    private boolean isRepeat;
    private boolean isBatchMissed;

    public Student() {}

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

    public int getBatchYear() { return batchYear; }
    public void setBatchYear(int batchYear) { this.batchYear = batchYear; }

    public int getCurrentSemester() { return currentSemester; }
    public void setCurrentSemester(int currentSemester) { this.currentSemester = currentSemester; }

    public boolean isRepeat() { return isRepeat; }
    public void setRepeat(boolean repeat) { isRepeat = repeat; }

    public boolean isBatchMissed() { return isBatchMissed; }
    public void setBatchMissed(boolean batchMissed) { isBatchMissed = batchMissed; }
}
