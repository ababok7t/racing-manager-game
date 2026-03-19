package model.components;

public class Transmission extends Component {
    private final double baseEfficiency; // 0..1+
    private final String compatibleEngineType;

    public Transmission(String name,
                         double price,
                         double baseEfficiency,
                         int weight,
                         String compatibleEngineType) {
        super(name, weight, price);
        this.baseEfficiency = baseEfficiency;
        this.compatibleEngineType = compatibleEngineType;
    }

    public double getBaseEfficiency() {
        return baseEfficiency;
    }

    public String getCompatibleEngineType() {
        return compatibleEngineType;
    }

    public boolean isCompatibleWith(Engine engine) {
        return engine != null && compatibleEngineType.equals(engine.getEngineType());
    }

    public double calculatePerformance() {
        // Нормируем в район 0..100 для скорости симуляции
        return getBasePerformance() * (1 - getWear() / 100.0);
    }

    @Override
    public double getBasePerformance() {
        return baseEfficiency * 100.0;
    }
}
