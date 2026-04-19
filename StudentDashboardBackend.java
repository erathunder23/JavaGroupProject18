// INHERITANCE
abstract class User {
    protected String id;
    public User(String id) { this.id = id; }
    public abstract String getRole(); // POLYMORPHISM
}

public class StudentModel extends User {
    private String name="", email="", phone="", level="", gpa="";
    private byte[] profilePic;

    public StudentModel(String id) { super(id); }

    // Getters and Setters (Encapsulation)
    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getPhone() { return phone; }
    public void setPhone(String p) { this.phone = p; }
    public String getLevel() { return level; }
    public void setLevel(String l) { this.level = l; }
    public String getGpa() { return gpa; }
    public void setGpa(String g) { this.gpa = g; }
    public byte[] getProfilePic() { return profilePic; }
    public void setProfilePic(byte[] pic) { this.profilePic = pic; }

    @Override
    public String getRole() { return "Student"; }
}

