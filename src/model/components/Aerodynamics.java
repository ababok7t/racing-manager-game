package model.components;

public class Aerodynamics extends Component {
    private final double baseDownforce;

    public Aerodynamics(String name, double price, double baseDownforce, int weight) {
        super(name, weight, price);
        this.baseDownforce = baseDownforce;
    }

    public double getBaseDownforce() {
        return baseDownforce;
    }

    @Override
    public double calculatePerformance() {
        return getBasePerformance() * (1 - getWear() / 100.0);
    }

    @Override
    public double getBasePerformance() { //для баланса
        return baseDownforce * 10.0;
    }
}

