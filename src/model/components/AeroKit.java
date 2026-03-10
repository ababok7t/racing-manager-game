package model.components;

public class AeroKit extends Component {
    private int aerodynamics;

    public AeroKit(String name, int weight, double price, int aerodynamics) {
        super(name, weight, price);
        this.aerodynamics = aerodynamics;
    }

    public int getAerodynamics() {
        return aerodynamics;
    }

    public double calculatePerformance() {
        return aerodynamics * getWear();
    }
}
