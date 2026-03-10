package model.components;

public abstract class Component {
    private final String id;
    private final String name;
    private int weight;
    private double price;
    private double wear;

    protected Component(String name, int weight, double price) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.weight = weight;
        this.price = price;
        this.wear = 0;
    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public double getPrice() {
        return price;
    }

    public double getWear() {
        return wear;
    }

    public void addWear(double receivedWear) {
        this.wear = Math.min(receivedWear, 100);
    }

    public void repair() {
        this.wear = 0;
    }

    public boolean isBroken() {
        return wear >= 100;
    }

    public abstract double calculatePerformance();
}
