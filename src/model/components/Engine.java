package model.components;

public class Engine extends Component {
    private final int horsePower;

    public Engine(String name, int weight, double price, int horsePower) {
        super(name, weight, price);
        this.horsePower = horsePower;
    }

    public int getHorsePower() {
        return horsePower;
    }

    public double calculatePerformance() {
        return horsePower * (1 - getWear() / 100) / 100;
    }
}
