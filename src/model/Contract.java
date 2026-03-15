package model;

public class Contract {
    private final String name;
    private final double price;
    private int numberOfRaces;
    private final int minReputation;
    private int racesCompleted;

    public Contract(String name, double price, int racesAmount, int minReputation) {
        this.name = name;
        this.price = price;
        this.numberOfRaces = racesAmount;
        this.minReputation = minReputation;
        this.racesCompleted = 0;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getNumberOfRaces() {
        return numberOfRaces;
    }

    public int getMinReputation() {
        return minReputation;
    }

    public int getRacesCompleted() {
        return racesCompleted;
    }

    public void addRace() {
        this.racesCompleted = racesCompleted + 1;
    }
}
