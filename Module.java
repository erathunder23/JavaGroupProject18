// model/Module.java
package model;

public class Module {

    private String moduleID;
    private String moduleName;
    private int credit;
    private String type;    // "Theory", "Practical", "Mixed"
    private int hours;

    public Module(String moduleID, String moduleName, int credit, String type, int hours) {
        this.moduleID = moduleID;
        this.moduleName = moduleName;
        this.credit = credit;
        this.type = type;
        this.hours = hours;
    }

    // Getters and Setters
    public String getModuleID() { return moduleID; }
    public void setModuleID(String moduleID) { this.moduleID = moduleID; }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public int getCredit() { return credit; }
    public void setCredit(int credit) { this.credit = credit; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getHours() { return hours; }
    public void setHours(int hours) { this.hours = hours; }
}
