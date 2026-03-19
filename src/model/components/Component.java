package model.components;

public abstract class Component {
    private final String id;
    private final String name;
    private final int weight;
    private final double price;
    private double wear; // износ в %, от 0 до 100

    protected Component(String name, int weight, double price) {
        this.id = java.util.UUID.randomUUID().toString();
        this.name = name;
        this.weight = weight;
        this.price = price;
        this.wear = 0;
    }

    public String getId() {
        return id;
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
        this.wear = Math.max(0, Math.min(wear, 100));
    }

    public void repair() {
        this.wear = 0;
    }

    public boolean isBroken() {
        return wear >= 100;
    }

    public void addWear(double receivedWear) {
        this.wear = Math.min(wear + receivedWear, 100);
    }

    public abstract double calculatePerformance();

    /**
     * Базовая характеристика компонента без учета износа (для отображения).
     */
    public abstract double getBasePerformance();

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                ", price=" + price +
                ", wear=" + wear +
                "%}";
    }
}
