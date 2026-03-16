package model.race;

public enum TrackUnitType {
    STRAIGHT("Прямой участок", 1, 0.8),
    TURN("Поворот", 0.8, 1),
    UPHILL("Подъем", 0.9, 1.1),
    DOWNHILL("Спуск", 1.1, 1.1);

    private final String type;
    private final double speedModificator;
    private final double controlModificator;

    TrackUnitType(String type, double speedModificator, double controlModificator) {
        this.type = type;
        this.speedModificator = speedModificator;
        this.controlModificator = controlModificator;
    }

    public String getType() {
        return type;
    }

    public double getSpeedModificator() {
        return speedModificator;
    }

    public double getControlModificator() {
        return controlModificator;
    }
}
