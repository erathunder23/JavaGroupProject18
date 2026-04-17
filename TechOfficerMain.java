import java.sql.Date;
import java.util.List;
import java.util.Scanner;

public class TechOfficerMain {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try {

            System.out.print("Enter TechID: ");
            String id = sc.nextLine();

            System.out.print("Enter Name: ");
            String name = sc.nextLine();

            System.out.print("Enter Email: ");
            String email = sc.nextLine();

            System.out.print("Enter Gender: ");
            String gender = sc.nextLine();

            System.out.print("Enter DOB (YYYY-MM-DD): ");
            Date dob = Date.valueOf(sc.nextLine());

            System.out.print("Enter Telephone: ");
            String tel = sc.nextLine();

            System.out.print("Enter Password: ");
            String pass = sc.nextLine();

            TechOfficer t = new TechOfficer(id, name, email, gender, dob, tel, pass);

            boolean inserted = t.insert();
            System.out.println("Insert: " + inserted);

            System.out.print("Enter TechID to fetch: ");
            String fid = sc.nextLine();

            TechOfficer fetched = TechOfficer.getById(fid);
            if (fetched != null) {
                System.out.println("Name: " + fetched.getName());
            } else {
                System.out.println("Not found");
            }

            System.out.print("Enter TechID to update: ");
            String uid = sc.nextLine();

            TechOfficer updateObj = TechOfficer.getById(uid);
            if (updateObj != null) {

                System.out.print("New Name: ");
                updateObj.setName(sc.nextLine());

                System.out.print("New Email: ");
                updateObj.setEmail(sc.nextLine());

                boolean updated = updateObj.update();
                System.out.println("Update: " + updated);
            }

            List<TechOfficer> list = TechOfficer.getAll();
            for (TechOfficer to : list) {
                System.out.println(to.getTechID() + " " + to.getName());
            }

            System.out.print("Enter TechID to delete: ");
            String did = sc.nextLine();

            boolean deleted = TechOfficer.delete(did);
            System.out.println("Delete: " + deleted);

        } catch (Exception e) {
            System.out.println("Error");
        }

        sc.close();
    }
}