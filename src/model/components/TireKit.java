package model.components;

public class TireKit extends Component {
    private final int strength; // от 1 до 10

    public TireKit(String name, int weight, double price, int strength) {
        super(name, weight, price);
        this.strength = strength;
    }

    public int getStrength() {
        return strength;
    }

    public double calculatePerformance() {
        return strength * (1 - getWear() / 100);
    }
}
