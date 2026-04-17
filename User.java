import java.util.Date;

public abstract class User {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private Date dob;
    private String telephone;
    private String password;

    public User(String userId, String firstName, String lastName, String email,
                String gender, Date dob, String telephone, String password) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.telephone = telephone;
        this.password = password;
    }

    public User() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Date getDob() { return dob; }
    public void setDob(Date dob) { this.dob = dob; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public abstract boolean loginUser();
    public abstract boolean logoutUser();
    public abstract String viewTimeTable();
    public abstract boolean updateUser();
}