package repository;

import model.Manager;
import java.util.Optional;

public class ManagerRepository extends Repository<Manager> {

    public Manager save(Manager manager) {
        return super.save(manager, manager.getId());
    }

    public Optional<Manager> findByName(String name) {
        return findAll().stream()
                .filter(m -> m.getName().equals(name))
                .findFirst();
    }

    public void updateBudget(String managerId, double newBudget) {
        findById(managerId).ifPresent(manager -> {
            manager.setBudget(newBudget);
            save(manager);
        });
    }

    public void addPoints(String managerId, int points) {
        findById(managerId).ifPresent(manager -> {
            manager.addChampionshipPoints(points);
            save(manager);
        });
    }
}