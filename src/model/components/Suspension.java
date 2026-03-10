package model.components;

public class Suspension extends Component {
    private int controllability;

    public Suspension(String name, int weight, double price, int controllability) {
        super(name, weight, price);
        this.controllability = controllability;
    }

    public int getControllability() {
        return controllability;
    }

    public double calculatePerformance() {
        return controllability * getWear();
    }
}
