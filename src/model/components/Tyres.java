package model.components;

public class Tyres extends Component {
    private final double baseGrip;
    private final String compound;
    private final double gripModifier;

    public Tyres(String name,
                  double price,
                  double baseGrip,
                  String compound, //тип шин и(мягуий, средний или жесткий)
                  double gripModifier, //сцепление
                  int weight) {
        super(name, weight, price);
        this.baseGrip = baseGrip;
        this.compound = compound;
        this.gripModifier = gripModifier;
    }

    public double getBaseGrip() {
        return baseGrip;
    }

    public String getCompound() {
        return compound;
    }

    public double getGripModifier() {
        return gripModifier;
    }

    @Override
    public double calculatePerformance() {
        double modifier = 0.85 + (gripModifier / 1.3) * 0.15; // мягкая нормализация
        return getBasePerformance() * modifier * (1 - getWear() / 100.0);
    }

    @Override
    public double getBasePerformance() {
        return baseGrip * 10.0;
    }
}

