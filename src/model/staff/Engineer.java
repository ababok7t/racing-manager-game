package model.staff;

public class Engineer extends Staff {
    private double creativity;
    private double intellect;

    public Engineer(String name, double price, int experience, double creativity, double intellect) {
        super(name, price, experience);
        this.creativity = creativity;
        this.intellect = intellect;
    }

    public double getCreativity() {
        return creativity;
    }

    public double getIntellect() {
        return intellect;
    }

    public double calculatePerformance() {
        return (creativity + intellect) / 2;
    }
}
