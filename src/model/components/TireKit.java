package model.components;

public class TireKit extends Component {
    private int strength;

    public TireKit(String name, int weight, double price, int strength) {
        super(name, weight, price);
        this.strength = strength;
    }

    public int getStrength() {
        return strength;
    }

    public double calculatePerformance() {
        return strength * getWear();
    }
}
