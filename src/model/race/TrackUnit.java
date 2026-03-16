package model.race;

public class TrackUnit {
    private final TrackUnitType type;
    private final double length;

    TrackUnit(TrackUnitType type, double length) {
        this.type = type;
        this.length = length;
    }

    public TrackUnitType getType() {
        return type;
    }

    public double getLength() {
        return length;
    }
}
