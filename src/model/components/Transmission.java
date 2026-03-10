package model.components;

public class Transmission extends Component {
    private int efficiency;

    public Transmission(String name, int weight, double price, int efficiency) {
        super(name, weight, price);
        this.efficiency = efficiency;
    }

    public int getEfficiency() {
        return efficiency;
    }

    public double calculatePerformance() {
        return efficiency * getWear();
    }
}
