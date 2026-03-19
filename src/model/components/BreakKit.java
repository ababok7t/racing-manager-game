package model.components;

public class BreakKit extends Component {
    private final int deceleration; // от 1 до 10

    public BreakKit(String name, int weight, double price, int deceleration) {
        super(name, weight, price);
        this.deceleration = deceleration;
    }

    public int getDeceleration() {
        return deceleration;
    }

    public double calculatePerformance() {
        return deceleration * (1 - getWear() / 100);
    }

    @Override
    public double getBasePerformance() {
        return deceleration;
    }
}
