import java.sql.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Tech Officer Menu ---");
            System.out.println("1. Insert");
            System.out.println("2. Update");
            System.out.println("3. Delete");
            System.out.println("4. Search by ID");
            System.out.println("5. View All");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            try {
                switch (choice) {

                    case 1:
                        System.out.print("TechID: ");
                        String id = sc.nextLine();

                        System.out.print("Name: ");
                        String name = sc.nextLine();

                        System.out.print("Email: ");
                        String email = sc.nextLine();

                        System.out.print("Gender: ");
                        String gender = sc.nextLine();

                        System.out.print("DOB (yyyy-mm-dd): ");
                        Date dob = Date.valueOf(sc.nextLine());

                        System.out.print("Telephone: ");
                        String tel = sc.nextLine();

                        System.out.print("Password: ");
                        String pass = sc.nextLine();

                        TechOfficer t1 = new TechOfficer(id, name, email, gender, dob, tel, pass);
                        if (t1.insert()) {
                            System.out.println("Inserted Successfully");
                        }
                        break;

                    case 2:
                        System.out.print("Enter TechID to update: ");
                        String uid = sc.nextLine();

                        TechOfficer t2 = TechOfficer.getById(uid);
                        if (t2 != null) {
                            System.out.print("New Name: ");
                            t2.setName(sc.nextLine());

                            System.out.print("New Email: ");
                            t2.setEmail(sc.nextLine());

                            System.out.print("New Gender: ");
                            t2.setGender(sc.nextLine());

                            System.out.print("New DOB (yyyy-mm-dd): ");
                            t2.setDob(Date.valueOf(sc.nextLine()));

                            System.out.print("New Telephone: ");
                            t2.setTelephone(sc.nextLine());

                            System.out.print("New Password: ");
                            t2.setPassword(sc.nextLine());

                            if (t2.update()) {
                                System.out.println("Updated Successfully");
                            }
                        } else {
                            System.out.println("Record Not Found");
                        }
                        break;

                    case 3:
                        System.out.print("Enter TechID to delete: ");
                        String did = sc.nextLine();

                        if (TechOfficer.delete(did)) {
                            System.out.println("Deleted Successfully");
                        } else {
                            System.out.println("Record Not Found");
                        }
                        break;

                    case 4:
                        System.out.print("Enter TechID: ");
                        String sid = sc.nextLine();

                        TechOfficer t3 = TechOfficer.getById(sid);
                        if (t3 != null) {
                            System.out.println("ID: " + t3.getTechID());
                            System.out.println("Name: " + t3.getName());
                            System.out.println("Email: " + t3.getEmail());
                            System.out.println("Gender: " + t3.getGender());
                            System.out.println("DOB: " + t3.getDob());
                            System.out.println("Telephone: " + t3.getTelephone());
                        } else {
                            System.out.println("Record Not Found");
                        }
                        break;

                    case 5:
                        List<TechOfficer> list = TechOfficer.getAll();
                        for (TechOfficer t : list) {
                            System.out.println(t.getTechID() + " | " + t.getName() + " | " + t.getEmail());
                        }
                        break;

                    case 6:
                        System.out.println("Exiting...");
                        sc.close();
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid Choice");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}