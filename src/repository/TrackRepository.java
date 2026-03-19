package repository;

import model.race.Track;
import java.util.Optional;

public class TrackRepository extends Repository<Track> {

    public Track save(Track track) {
        return super.save(track, track.getId());
    }

    public Optional<Track> findByName(String name) {
        return findAll().stream()
                .filter(track -> track.getName().equals(name))
                .findFirst();
    }
}