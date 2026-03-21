package model.race;

import java.util.*;

import model.Car;
import model.Manager;

public class Race {
    private final String id;
    private final Track track;
    private final String weather;

    private final List<String> participantManagerIds;
    private final Map<String, String> carIdByManagerId;
    private final Map<String, String> pilotIdByManagerId;

    private final Map<String, Double> resultByManagerId;
    private final Map<String, Integer> positionByManagerId;
    private final Map<String, Incident> incidentsByManagerId;

    private boolean completed;

    public Race(Track track) {
        this.id = UUID.randomUUID().toString();
        this.track = track;
        this.weather = pickRandomWeather().getName();
        this.participantManagerIds = new ArrayList<>();
        this.carIdByManagerId = new HashMap<>();
        this.pilotIdByManagerId = new HashMap<>();
        this.resultByManagerId = new HashMap<>();
        this.positionByManagerId = new HashMap<>();
        this.incidentsByManagerId = new HashMap<>();
        this.completed = false;
    }

    private Weather pickRandomWeather() {
        Weather[] values = Weather.values();
        return values[new Random().nextInt(values.length)];
    }

    public String getId() {
        return id;
    }

    public Track getTrack() {
        return track;
    }

    public String getWeather() {
        return weather;
    }

    public void addParticipant(String managerId, String carId, String pilotId) {
        if (!participantManagerIds.contains(managerId)) {
            participantManagerIds.add(managerId);
        }
        carIdByManagerId.put(managerId, carId);
        pilotIdByManagerId.put(managerId, pilotId);
    }

    public List<String> getParticipantManagerIds() {
        return new ArrayList<>(participantManagerIds);
    }

    public String getCarId(String managerId) {
        return carIdByManagerId.get(managerId);
    }

    public String getPilotId(String managerId) {
        return pilotIdByManagerId.get(managerId);
    }

    public void addIncident(String managerId, Incident incident) {
        incidentsByManagerId.put(managerId, incident);
    }

    public Map<String, Incident> getIncidents() {
        return new HashMap<>(incidentsByManagerId);
    }

    public void setResult(String managerId, double totalTime) {
        resultByManagerId.put(managerId, totalTime);
    }

    public void setPosition(String managerId, int position) {
        positionByManagerId.put(managerId, position);
    }

    public int getPosition(String managerId) {
        return positionByManagerId.getOrDefault(managerId, 0);
    }

    public Map<String, Integer> getAllPositions() {
        return new HashMap<>(positionByManagerId);
    }

    public boolean isCompleted() {
        return completed;
    }

    public void complete() {
        this.completed = true;
    }

    public double getPrizeMoney(int position) {
        return switch (position) {
            case 1 -> 1_000_000;
            case 2 -> 700_000;
            case 3 -> 500_000;
            default -> 0;
        };
    }

    public int getPoints(int position) {
        return switch (position) {
            case 1 -> 25;
            case 2 -> 18;
            case 3 -> 15;
            case 4 -> 12;
            case 5 -> 10;
            case 6 -> 8;
            case 7 -> 6;
            case 8 -> 4;
            case 9 -> 2;
            case 10 -> 1;
            default -> 0;
        };
    }
}

