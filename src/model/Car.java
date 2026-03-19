package model;

import model.components.*;
import java.util.UUID;

public class Car {
    private final String id;
    private final String name;
    private String managerId;

    private Engine engine;
    private Transmission transmission;
    private Suspension suspension;
    private Aerodynamics aerodynamics;
    private Tyres tyres;

    private boolean built;

    public Car(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.managerId = null;
        this.built = false;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String newManagerId) {
        this.managerId = newManagerId;
    }

    public Engine getEngine() {
        return engine;
    }

    public void setEngine(Engine newEngine) {
        this.engine = newEngine;
    }

    public Transmission getTransmission() {
        return transmission;
    }

    public void setTransmission(Transmission newTransmission) {
        this.transmission = newTransmission;
    }

    public Suspension getSuspension() {
        return suspension;
    }

    public void setSuspension(Suspension newSuspension) {
        this.suspension = newSuspension;
    }

    public Aerodynamics getAerodynamics() {
        return aerodynamics;
    }

    public void setAerodynamics(Aerodynamics newAerodynamics) {
        this.aerodynamics = newAerodynamics;
    }

    public Tyres getTyres() {
        return tyres;
    }

    public void setTyres(Tyres newTyres) {
        this.tyres = newTyres;
    }

    public void setBuilt(boolean built) {
        this.built = built;
    }

    public boolean isComplete() {
        return built && engine != null && transmission != null && suspension != null && aerodynamics != null && tyres != null;
    }

    public boolean hasBrokenComponents() {
        return (engine != null && engine.isBroken())
                || (transmission != null && transmission.isBroken())
                || (suspension != null && suspension.isBroken())
                || (aerodynamics != null && aerodynamics.isBroken())
                || (tyres != null && tyres.isBroken());
    }

    public double calculatePerformance() {
        if (!isComplete()) return 0;
        double sum = engine.calculatePerformance()
                + transmission.calculatePerformance()
                + suspension.calculatePerformance()
                + aerodynamics.calculatePerformance()
                + tyres.calculatePerformance();
        return sum / 5.0;
    }

    public double getWearPercentage() {
        if (!isComplete()) return 0;
        return (engine.getWear() + transmission.getWear() + suspension.getWear()
                + aerodynamics.getWear() + tyres.getWear()) / 5.0;
    }

    public void increaseWear(double receivedWear) {
        // Увеличиваем износ для всех установленных деталей
        if (engine != null) engine.addWear(receivedWear);
        if (transmission != null) transmission.addWear(receivedWear);
        if (suspension != null) suspension.addWear(receivedWear);
        if (aerodynamics != null) aerodynamics.addWear(receivedWear);
        if (tyres != null) tyres.addWear(receivedWear);
    }

    public void repair() {
        if (engine != null) engine.repair();
        if (transmission != null) transmission.repair();
        if (suspension != null) suspension.repair();
        if (aerodynamics != null) aerodynamics.repair();
        if (tyres != null) tyres.repair();
    }

    @Override
    public String toString() {
        return "Car{name='" + name + "', wear=" + String.format("%.1f", getWearPercentage()) + "%}";
    }
}
