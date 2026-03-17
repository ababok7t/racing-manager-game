package model.race;

import java.util.ArrayList;
import java.util.List;

public class RaceSession {
    public Track track;
    public Weather weather;
    private List<RaceMember> raceMembers;
    private boolean finished;

    public RaceSession(Track track, Weather weather) {
        this.track = track;
        this.weather = weather;
        this.raceMembers = new ArrayList<RaceMember>();
        this.finished = false;
    }

    public Track getTrack() {
        return track;
    }

    public Weather getWeather() {
        return weather;
    }

    public List<RaceMember> getRaceMembers() {
        return raceMembers;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void addMember(RaceMember member) {
        raceMembers.add(member);
    }
}
