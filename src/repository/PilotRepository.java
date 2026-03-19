package repository;

import model.staff.Pilot;
import java.util.List;
import java.util.stream.Collectors;

public class PilotRepository extends Repository<Pilot> {

    public Pilot save(Pilot pilot) {
        return super.save(pilot, pilot.getId());
    }

    public List<Pilot> findByManagerId(String managerId) {
        return findAll().stream()
                .filter(pilot -> managerId.equals(pilot.getManagerId()))
                .collect(Collectors.toList());
    }

    public List<Pilot> findAvailable() {
        return findAll().stream()
                .filter(pilot -> pilot.getManagerId() == null && !pilot.isHired())
                .collect(Collectors.toList());
    }

    public void hire(String pilotId, String managerId) {
        findById(pilotId).ifPresent(pilot -> {
            pilot.setManagerId(managerId);
            pilot.setHired(true);
            save(pilot);
        });
    }
}