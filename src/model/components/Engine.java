package model.components;

public class Engine extends Component {
    private final double basePower;
    private final String engineType;
    private final double reliability; //надежность

    public Engine(String name,
                  double price,
                  double basePower,
                  int weight,
                  String engineType,
                  double reliability) {
        super(name, weight, price);
        this.basePower = basePower;
        this.engineType = engineType;
        this.reliability = reliability;
    }

    public double getBasePower() {
        return basePower;
    }

    public String getEngineType() {
        return engineType;
    }

    public double getReliability() {
        return reliability;
    }

    public double calculatePerformance() {
        return getBasePerformance() * (1 - getWear() / 100.0);
    }

    @Override
    public double getBasePerformance() {
        return basePower;
    }
}
