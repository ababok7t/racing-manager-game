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
        initTrackUnits();
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
        // Копия, чтобы снаружи нельзя было менять состав трассы.
        return new ArrayList<>(trackUnits);
    }

    public double getDifficulty() {
        return difficulty;
    }

    private void initTrackUnits() {
        // По методичке трасса должна включать разные типы участков.
        // Заполняем список детерминированно из базовых параметров трека.
        trackUnits.clear();

        // Распределяем длину по секциям (сумма долей = 1.0)
        double d = Math.max(0.0, Math.min(1.0, difficulty));
        double straightShare = 0.35 + (1.0 - d) * 0.1; // более “легкая” трасса -> больше прямых
        double turnShare = 0.35 + d * 0.1;          // более сложная -> больше поворотов
        double upShare = 0.15 + d * 0.05;
        double downShare = 1.0 - straightShare - turnShare - upShare;

        // На случай округлений
        downShare = Math.max(0.05, downShare);

        // Нормализуем доли к сумме 1.0, чтобы длина секций не выходила за рамки трека.
        double sumShares = straightShare + turnShare + upShare + downShare;
        if (sumShares > 0) {
            double k = 1.0 / sumShares;
            straightShare *= k;
            turnShare *= k;
            upShare *= k;
            downShare *= k;
        }

        trackUnits.add(new TrackUnit(TrackUnitType.STRAIGHT, length * straightShare));
        trackUnits.add(new TrackUnit(TrackUnitType.TURN, length * turnShare));
        trackUnits.add(new TrackUnit(TrackUnitType.UPHILL, length * upShare));
        trackUnits.add(new TrackUnit(TrackUnitType.DOWNHILL, length * downShare));
    }

    @Override
    public String toString() {
        return name + " (" + country + "), " + length + " км, " + laps + " кругов";
    }
}
