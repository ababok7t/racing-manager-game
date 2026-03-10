package model;

import java.util.UUID;

public class Contract {
    private final String id;
    private final String name;
    private double price;
    private int racesAmount;
    private int minReputation;

    public Contract(String name, double price, int racesAmount, int minReputation) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.price = price;
        this.racesAmount = racesAmount;
        this.minReputation = minReputation;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getRacesAmount() {
        return racesAmount;
    }

    public int getMinReputation() {
        return minReputation;
    }

    public void decreaseRacesAmount() {
        this.racesAmount = racesAmount--;
    }
}
