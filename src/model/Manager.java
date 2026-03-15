package model;

import model.components.*;
import model.staff.*;

import java.util.*;

public class Manager {
    private String name;
    private double budget;
    private int reputation;

    private List<Car> cars;
    private Map<Class<? extends Staff>, List<Staff>> staff;
    private Map<Class<? extends Component>, List<Component>> components;
    private List<Contract> contracts;

    public Manager(String name, double budget) {
        this.name = name;
        this.budget = budget;
        this.reputation = 0;
        this.cars = new ArrayList<>();
        this.staff = new HashMap<>();
        this.components = new HashMap<>();
        this.contracts = new ArrayList<>();
    }
}