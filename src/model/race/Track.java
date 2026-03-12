package model.race;

public class Track {
    private String name;
    private double length;        // длина в км
    private int laps;             // количество кругов
    private int corners;           // количество поворотов

    public Track(String name, double length, int laps, int corners) {
        this.name = name;
        this.length = length;
        this.laps = laps;
        this.corners = corners;
    }

    // Время базового круга (чем длиннее трасса, тем больше время)
    public double getBaseLapTime() {
        return length * 60; // например, 5 км * 60 = 300 секунд
    }

    // Насколько важна управляемость на этой трассе
    public double getHandlingImportance() {
        return corners / 20.0; // чем больше поворотов, тем важнее управляемость
    }

    // Геттеры
    public String getName() { return name; }
    public double getLength() { return length; }
    public int getLaps() { return laps; }
    public int getCorners() { return corners; }

    @Override
    public String toString() {
        return String.format("%s | Длина: %.2f км | Кругов: %d | Поворотов: %d",
                name, length, laps, corners);
    }
}