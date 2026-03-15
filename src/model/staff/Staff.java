package model.staff;

import model.Manager;

import java.util.UUID;

public abstract class Staff {
    private final String name;
    private double price;
    private int experience;
    private Manager manager;

    public Staff(String name, double price, int experience) {
        this.name = name;
        this.price = price;
        this.experience = experience;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getExperience() {
        return experience;
    }

    public void addExperience(int exp) {
        this.experience = getExperience() + exp;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager newManager) {
        this.manager = newManager;
    }

    public abstract double calculatePerformance();
}
