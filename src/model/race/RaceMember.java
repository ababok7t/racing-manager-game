package model.race;

import model.Car;
import model.Manager;
import model.staff.Pilot;

import java.util.ArrayList;
import java.util.List;

public class RaceMember {
    private Manager team;
    private Car car;
    private Pilot pilot;
    private double totalTime;
    private boolean retired;
    private List<Incident> incidents;

    RaceMember(Manager team, Car car, Pilot pilot) {
        this.team = team;
        this.car = car;
        this.pilot = pilot;
        this.totalTime = 0;
        this.retired = false;
        this.incidents = new ArrayList<Incident>();
    }

    public Manager getTeam() {
        return team;
    }

    public Car getCar() {
        return car;
    }

    public Pilot getPilot() {
        return pilot;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(double newTime) {
        this.totalTime = newTime;
    }

    public boolean isRetired() {
        return retired;
    }

    public void setRetired(boolean retired) {
        this.retired = retired;
    }

    public List<Incident> getIncidents() {
        return incidents;
    }

    public void addIncident(Incident incident) {
        incidents.add(incident);
        if (incident.isFatal()) {
            retired = true;
        }
    }
}
