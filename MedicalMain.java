import java.sql.Date;
import java.util.List;

public class MedicalMain {

    public static void main(String[] args) {

        MedicalService service = new MedicalServiceImpl();

        try {

            Medical m1 = new Medical(
                    "M001",
                    "S001",
                    "CS101",
                    Date.valueOf("2026-04-17"),
                    "Medical leave - Fever",
                    "Pending"
            );

            boolean inserted = service.addMedical(m1);
            System.out.println("Insert: " + inserted);

            Medical fetched = service.getMedicalById("M001");
            if (fetched != null) {
                System.out.println("Fetched: " + fetched.getDescription());
            }

            m1.setDescription("Updated Medical - Flu");
            m1.setStatus("Approved");

            boolean updated = service.updateMedical(m1);
            System.out.println("Update: " + updated);

            List<Medical> list = service.getAllMedicals();
            if (list != null) {
                for (Medical m : list) {
                    System.out.println(m.getMedicalID() + " " + m.getStatus());
                }
            }

            boolean deleted = service.deleteMedical("M001");
            System.out.println("Delete: " + deleted);

        } catch (Exception e) {
            System.out.println("Error");
        }
    }
}