package model.market;

public class MarketItem {
    private String name;
    private String type;        // "ENGINE", "TRANSMISSION", "PILOT" и т.д.
    private double price;
    private double performance;  // характеристика (для компонентов)
    private Object item;         // сам товар (компонент, пилот и т.д.)

    public MarketItem(String name, String type, double price, double performance, Object item) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.performance = performance;
        this.item = item;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public double getPerformance() { return performance; }
    public Object getItem() { return item; }

    @Override
    public String toString() {
        return String.format("%s | %s | Цена: $%.2f | Хар-ка: %.1f",
                name, type, price, performance);
    }
}