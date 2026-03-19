package model.staff;

public class Engineer extends Staff {
    private final String specialization;
    private final int level;
    private final double salary; // в год
    private final double efficiency; // 0..1+

    // MarketService создает инженеров как:
    // (name, specialization, level, monthlySalary, efficiency)
    public Engineer(String name, String specialization, int level, double monthlySalary, double efficiency) {
        super(name, monthlySalary * 12.0, level);
        this.specialization = specialization;
        this.level = level;
        this.salary = monthlySalary;
        this.efficiency = efficiency;
    }

    public String getSpecialization() {
        return specialization;
    }

    public int getLevel() {
        return level;
    }

    public double getSalary() {
        return salary;
    }

    public double getEfficiency() {
        return efficiency;
    }

    /**
     * Бонус к работе команды: чем выше уровень и эффективность, тем сильнее инженерский вклад.
     * Значение предназначено для агрегирования в RaceService.
     */
    public double getBonus() {
        return efficiency * (level / 10.0);
    }

    @Override
    public double calculatePerformance() {
        return getBonus();
    }

    @Override
    public String toString() {
        return "Engineer{name='" + getName() + '\'' +
                ", specialization='" + specialization + '\'' +
                ", level=" + level +
                ", efficiency=" + String.format("%.2f", efficiency) +
                ", hired=" + isHired() +
                '}';
    }
}
