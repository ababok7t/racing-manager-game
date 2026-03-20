package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Manager {
    private final String id;
    private final String name;
    private double budget;

    private int championshipPoints;
    private int reputation;

    private final List<String> carIds;
    private final List<String> pilotIds;
    private final List<String> engineerIds;
    private final List<String> contractIds;

    public Manager(String name, double budget) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.budget = budget;
        this.reputation = 0;
        this.championshipPoints = 0;
        this.carIds = new ArrayList<>();
        this.pilotIds = new ArrayList<>();
        this.engineerIds = new ArrayList<>();
        this.contractIds = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double newBudget) {
        this.budget = newBudget;
    }

    public int getChampionshipPoints() {
        return championshipPoints;
    }

    public void addChampionshipPoints(int points) {
        this.championshipPoints += points;
    }

    public void addPrizeMoney(double prizeMoney) {
        this.budget += prizeMoney;
    }

    public boolean spendBudget(double amount) {
        if (amount < 0) return false;
        if (budget < amount) return false;
        budget -= amount;
        return true;
    }

    public List<String> getCarIds() {
        return new ArrayList<>(carIds);
    }

    public void addCarId(String carId) {
        if (carId != null && !carIds.contains(carId)) {
            carIds.add(carId);
        }
    }

    public List<String> getPilotIds() {
        return new ArrayList<>(pilotIds);
    }

    public void addPilotId(String pilotId) {
        if (pilotId != null && !pilotIds.contains(pilotId)) {
            pilotIds.add(pilotId);
        }
    }

    public List<String> getEngineerIds() {
        return new ArrayList<>(engineerIds);
    }

    public void addEngineerId(String engineerId) {
        if (engineerId != null && !engineerIds.contains(engineerId)) {
            engineerIds.add(engineerId);
        }
    }

    public int getReputation() {
        return reputation;
    }

    public void addReputation(int delta) {
        reputation = Math.max(0, reputation + delta);
    }

    public List<String> getContractIds() {
        return new ArrayList<>(contractIds);
    }

    public void addContractId(String contractId) {
        if (contractId != null && !contractIds.contains(contractId)) {
            contractIds.add(contractId);
        }
    }

    public void removeContractId(String contractId) {
        contractIds.remove(contractId);
    }
}