package model.components;

public class AeroKit extends Component {
    private final int aerodynamics; // от 1 до 10

    public AeroKit(String name, int weight, double price, int aerodynamics) {
        super(name, weight, price);
        this.aerodynamics = aerodynamics;
    }

    public int getAerodynamics() {
        return aerodynamics;
    }

    public double calculatePerformance() {
        return aerodynamics * (1 - getWear() / 100);
    }
}
