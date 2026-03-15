package model.components;

public class Suspension extends Component {
    private final int controllability; // от 1 до 10

    public Suspension(String name, int weight, double price, int controllability) {
        super(name, weight, price);
        this.controllability = controllability;
    }

    public int getControllability() {
        return controllability;
    }

    public double calculatePerformance() {
        return controllability * (1 - getWear() / 100);
    }
}
