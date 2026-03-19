package repository;

import model.staff.Engineer;
import java.util.List;
import java.util.stream.Collectors;

public class EngineerRepository extends Repository<Engineer> {

    public Engineer save(Engineer engineer) {
        return super.save(engineer, engineer.getId());
    }

    public List<Engineer> findByManagerId(String managerId) {
        return findAll().stream()
                .filter(eng -> managerId.equals(eng.getManagerId()))
                .collect(Collectors.toList());
    }

    public List<Engineer> findAvailable() {
        return findAll().stream()
                .filter(eng -> eng.getManagerId() == null && !eng.isHired())
                .collect(Collectors.toList());
    }

    public void hire(String engineerId, String managerId) {
        findById(engineerId).ifPresent(eng -> {
            eng.setManagerId(managerId);
            eng.setHired(true);
            save(eng);
        });
    }
}