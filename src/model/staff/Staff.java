package model.staff;

import java.util.UUID;

public abstract class Staff {
    private final String id;
    private final String name;
    private double price;
    private int experience;

    private String managerId;
    private boolean hired;

    public Staff(String name, double price, int experience) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.price = price;
        this.experience = experience;
        this.managerId = null;
        this.hired = false;
    }

    public String getId() {
        return id;
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

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String newManagerId) {
        this.managerId = newManagerId;
    }

    public boolean isHired() {
        return hired;
    }

    public void setHired(boolean hired) {
        this.hired = hired;
    }

    public abstract double calculatePerformance();
}
