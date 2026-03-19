package model.race;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Track {
    private final String id;
    private final String name;
    private final String country;
    private final double length;
    private final int laps;
    private final double difficulty; // 0..1+

    // Технически можно хранить секции, но для ЛР1 достаточно параметров трека.
    private final List<TrackUnit> trackUnits;

    // Маркет/игровой слой создает трек так: (name, country, length, laps, difficulty)
    public Track(String name, String country, double length, int laps, double difficulty) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.country = country;
        this.length = length;
        this.laps = laps;
        this.difficulty = difficulty;
        this.trackUnits = new ArrayList<>();
    }

    // Старый конструктор (если где-то используется)
    public Track(String name, double length, int numberOfLaps, int numberOfTurns) {
        this(name, "Unknown", length, numberOfLaps, Math.max(0.1, Math.min(1.0, numberOfTurns / 100.0)));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public double getLength() {
        return length;
    }

    public int getLaps() {
        return laps;
    }

    public List<TrackUnit> getTrackUnits() {
        return trackUnits;
    }

    public double getDifficulty() {
        return difficulty;
    }

    @Override
    public String toString() {
        return name + " (" + country + "), " + length + " км, " + laps + " кругов";
    }
}
