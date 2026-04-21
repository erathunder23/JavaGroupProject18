// model/Person.java
package model;

// ABSTRACTION: Abstract class - cannot be instantiated directly
public abstract class Person {

    // ENCAPSULATION: private fields
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String gender;
    private String dob;
    private String telephone;
    private String password;

    // Constructor
    public Person(String id, String firstName, String lastName,
                  String email, String gender, String dob,
                  String telephone, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.telephone = telephone;
        this.password = password;
    }

    // ABSTRACTION: Force subclasses to implement this
    public abstract String getRole();

    // POLYMORPHISM: toString can be overridden
    @Override
    public String toString() {
        return firstName + " " + lastName + " [" + getRole() + "]";
    }

    // ENCAPSULATION: Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getTelephone() { return telephone; }
    public void setTelephone(String telephone) { this.telephone = telephone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

