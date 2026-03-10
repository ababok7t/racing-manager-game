package model.components;

public class Platform extends Component {
    private int maxEngineWeight;

    public Platform(String name, int weight, double price, int maxEngineWeight) {
        super(name, weight, price);
        this.maxEngineWeight = maxEngineWeight;
    }

    private int getMaxEngineWeight() {
        return maxEngineWeight;
    }

    public double calculatePerformance() {
        return maxEngineWeight * getWear();
    }
}
