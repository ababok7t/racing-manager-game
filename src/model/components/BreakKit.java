package model.components;

public class BreakKit extends Component {
    private int deceleration;

    public BreakKit(String name, int weight, double price, int deceleration) {
        super(name, weight, price);
        this.deceleration = deceleration;
    }

    public int getDeceleration() {
        return deceleration;
    }

    public double calculatePerformance() {
        return deceleration * getWear();
    }
}
