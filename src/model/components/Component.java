package model.components;

public abstract class Component {
    private final String name;
    private final int weight;
    private final double price;
    private double wear; // от 0 до 1

    protected Component(String name, int weight, double price) {
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

    public void setWear(double wear) {
        this.wear = wear;
    }

    public void repair() {
        this.wear = 0;
    }

    public boolean isBroken() {
        return wear >= 1;
    }

    public void addWear(double receivedWear) {
        this.wear = Math.min(wear + receivedWear, 1);
    }

    public abstract double calculatePerformance();
}
