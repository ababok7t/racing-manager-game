package model.staff;

public class Pilot extends Staff {
    private int speed;
    private double controllability;

    public Pilot(String name, double price, int experience, int speed, double controllability) {
        super(name, price, experience);
        this.speed = speed;
        this.controllability = controllability;
    }

    public int getSpeed() {
        return speed;
    }

    public double getControllability() {
        return controllability;
    }

    public double calculatePerformance() {
        return (speed + controllability) / 2;
    }
}
