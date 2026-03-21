package model.staff;

import model.race.Weather;

public class Pilot extends Staff {
    private final int age;
    private final double skill;

    private final double aggression;
    private final double consistency;

    public Pilot(String name, int age, double skill, double price) {
        super(name, price, (int) Math.max(1, skill / 2));
        this.age = age;
        this.skill = skill;

        this.aggression = clamp(0.2 + (skill / 100.0) * 0.7, 0, 1);
        this.consistency = clamp(0.3 + (1 - aggression) * 0.6 + (age < 30 ? 0.05 : 0), 0, 1);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    public int getAge() {
        return age;
    }

    public double getSkill() {
        return skill;
    }

    public double getAggression() {
        return aggression;
    }

    public double getConsistency() {
        return consistency;
    }

    @Override
    public double calculatePerformance() {
        return (skill / 100.0) * 0.5 + aggression * 0.25 + consistency * 0.25;
    }

    //перформанс зависящий от погоды и трека
    public double calculatePerformance(String weather, double trackDifficulty) {
        Weather weatherEnum = weatherToEnum(weather);
        double weatherMod = weatherEnum.getSpeedModificator(); // 0.75..1.0

        double difficultyMod = 1.0 + (trackDifficulty - 0.5) * (consistency - 0.5) * 0.4;

        double aggressionMod = 1.0 + (aggression - 0.5) * 0.25 * (1.0 - trackDifficulty * 0.3);

        double base = calculatePerformance(); // в районе 0.3..1.0
        double result = base * weatherMod * difficultyMod * aggressionMod;

        return Math.max(0.05, result);
    }

    private Weather weatherToEnum(String weather) {
        if (weather == null) return Weather.SUNNY;
        for (Weather w : Weather.values()) {
            if (w.getName().equalsIgnoreCase(weather) || w.name().equalsIgnoreCase(weather)) {
                return w;
            }
        }
        return Weather.SUNNY;
    }

    public void gainExperience() {
        addExperience(1);
    }

    @Override
    public String toString() {
        return "Pilot{name='" + getName() + '\'' +
                ", age=" + age +
                ", skill=" + skill +
                ", aggression=" + String.format("%.2f", aggression) +
                ", consistency=" + String.format("%.2f", consistency) +
                ", hired=" + isHired() +
                '}';
    }
}
