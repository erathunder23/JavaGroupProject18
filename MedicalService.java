import java.util.List;

public interface MedicalService {

    boolean addMedical(Medical m);

    boolean updateMedical(Medical m);

    boolean deleteMedical(String medicalID);

    Medical getMedicalById(String medicalID);

    List<Medical> getAllMedicals();
}