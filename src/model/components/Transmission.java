package model.components;

public class Transmission extends Component {
    private final int efficiency; // от 1 до 10

    public Transmission(String name, int weight, double price, int efficiency) {
        super(name, weight, price);
        this.efficiency = efficiency;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public double calculatePerformance() {
        return efficiency * (1 - getWear() / 100);
    }
}
