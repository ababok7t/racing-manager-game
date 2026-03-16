package model.race;

import java.util.ArrayList;
import java.util.List;

public class Track {
    private String name;
    private double length;
    private int numberOfLaps;
    private List<TrackUnit> trackUnits;

    public Track(String name, double length, int numberOfLaps, int numberOfTurns) {
        this.name = name;
        this.length = length;
        this.numberOfLaps = numberOfLaps;
        this.trackUnits = new ArrayList<TrackUnit>();
    }

    public String getName() {
        return name;
    }

    public double getLength() {
        length = 0;
        for (TrackUnit trackUnit : trackUnits) {
            length = length + trackUnit.getLength();
        }
        return length;
    }

    public int getNumberOfLaps() {
        return numberOfLaps;
    }

    public List<TrackUnit> getTrackUnits() {
        return trackUnits;
    }
}
