package model.race;

public enum Weather {
    SUNNY("Солнечно", 1.0),
    CLOUDY("Облачно", 0.95),
    FOGGY("Туманно", 0.85),
    RAINY("Дождливо", 0.75);

    private final String name;
    private final double speedModificator;

    Weather(String name, double speedModificator) {
        this.name = name;
        this.speedModificator = speedModificator;
    }

    public String getName() {
        return name;
    }

    public double getSpeedModificator() {
        return speedModificator;
    }
}
