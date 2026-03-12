package model.race;

public enum Weather {
    SUNNY("Солнечно", 1.0),
    CLOUDY("Облачно", 0.95),
    RAIN("Дождь", 0.8);

    private String name;
    private double speedMultiplier; // влияние на скорость

    Weather(String name, double speedFactor) {
        this.name = name;
        this.speedMultiplier = speedFactor;
    }

    public String getName() { return name; }
    public double getSpeedFactor() {
        return speedMultiplier;
    }
}