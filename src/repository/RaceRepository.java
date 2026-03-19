package repository;

import model.race.Race;
import java.util.List;
import java.util.stream.Collectors;

public class RaceRepository extends Repository<Race> {

    public Race save(Race race) {
        return super.save(race, race.getId());
    }

    public List<Race> findByManagerId(String managerId) {
        return findAll().stream()
                .filter(race -> race.getParticipantManagerIds().contains(managerId))
                .collect(Collectors.toList());
    }

    public List<Race> findCompleted() {
        return findAll().stream()
                .filter(Race::isCompleted)
                .collect(Collectors.toList());
    }

    public List<Race> findRecent(int limit) {
        return findAll().stream()
                .sorted((r1, r2) -> r2.getId().compareTo(r1.getId()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}