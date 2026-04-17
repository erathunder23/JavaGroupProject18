import java.util.List;

public class MedicalServiceImpl implements MedicalService {

    @Override
    public boolean addMedical(Medical m) {
        try {
            return MedicalDAO.insert(m);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateMedical(Medical m) {
        try {
            return MedicalDAO.update(m);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteMedical(String medicalID) {
        try {
            return MedicalDAO.delete(medicalID);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Medical getMedicalById(String medicalID) {
        try {
            return MedicalDAO.getById(medicalID);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<Medical> getAllMedicals() {
        try {
            return MedicalDAO.getAll();
        } catch (Exception e) {
            return null;
        }
    }
}