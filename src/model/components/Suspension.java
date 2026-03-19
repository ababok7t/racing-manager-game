package model.components;

public class Suspension extends Component {
    private final double baseStability;
    private final int maxWeight;

    public Suspension(String name,
                       double price,
                       double baseStability,
                       int maxWeight,
                       int weight) {
        super(name, weight, price);
        this.baseStability = baseStability;
        this.maxWeight = maxWeight;
    }

    public double getBaseStability() {
        return baseStability;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public boolean canSupportWeight(int engineWeight) {
        return engineWeight <= maxWeight;
    }

    public double calculatePerformance() {
        return getBasePerformance() * (1 - getWear() / 100.0);
    }

    @Override
    public double getBasePerformance() {
        return baseStability * 10.0;
    }
}
