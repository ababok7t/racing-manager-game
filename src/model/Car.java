package model;

import model.components.*;

import java.util.UUID;

public class Car {
    private final String id;
    private final String name;
    private Manager owner;
    private Platform platform;
    private Transmission transmission;
    private Engine engine;
    private Suspension suspension;
    private AeroKit aeroKit;
    private TireKit tireKit;
    private BreakKit breakKit;
    private boolean isReady;

    public Car(String name, Manager owner) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.owner = owner;
        this.isReady = false;
    }

    public Platform getPlatform() {
        return platform;
    }
    public void setPlatform(Platform newPlatform) {
        this.platform = newPlatform;
    }

    public Transmission getTransmission() {
        return transmission;
    }
    public void setTransmission(Transmission newTransmission) {
        this.transmission = newTransmission;
    }

    public Engine getEngine() {
        return engine;
    }
    public void setEngine(Engine newEngine) {
        this.engine = newEngine;
    }

    public Suspension getSuspension() {
        return suspension;
    }
    public void setSuspension(Suspension newSuspension) {
        this.suspension = newSuspension;
    }

    public AeroKit getAeroKit() {
        return aeroKit;
    }
    public void setAeroKit(AeroKit newAeroKit) {
        this.aeroKit = newAeroKit;
    }

    public TireKit getTireKit() {
        return tireKit;
    }
    public void setTireKit(TireKit newTireKit) {
        this.tireKit =  newTireKit;
    }

    public BreakKit getBreakKit() {
        return breakKit;
    }
    public void setBreakKit(BreakKit newBreakKit) {
        this.breakKit = newBreakKit;
    }

    public boolean isReady() {
        return platform != null && transmission != null && engine != null && suspension != null
                && aeroKit != null && tireKit != null && breakKit != null;
    }

    public double calculatePerformance() {
        if (!isReady) return 0;
        return (platform.calculatePerformance() + transmission.calculatePerformance() +
                engine.calculatePerformance() + suspension.calculatePerformance()
                + aeroKit.calculatePerformance() + tireKit.calculatePerformance() +
                transmission.calculatePerformance()) / 7;
    }

    public void addWear(double receivedWear) {
        platform.addWear(receivedWear);
        transmission.addWear(receivedWear);
        engine.addWear(receivedWear);
        suspension.addWear(receivedWear);
        aeroKit.addWear(receivedWear);
        tireKit.addWear(receivedWear);
        breakKit.addWear(receivedWear);
    }

}
