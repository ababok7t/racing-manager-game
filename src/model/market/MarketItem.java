package model.market;

import model.Contract;
import model.components.Component;
import model.staff.Pilot;
import model.staff.Engineer;

public class MarketItem<T> {
    private String id;
    private T item;
    private double price;
    private ItemType type;
    private boolean isAvailable;

    public enum ItemType {
        COMPONENT, PILOT, ENGINEER, CONTRACT
    }

    public MarketItem(T item, double price, ItemType type) {
        this.item = item;
        this.price = price;
        this.type = type;
        this.isAvailable = true;
    }

    public String getId() {
        if (item instanceof Component) return ((Component) item).getId();
        if (item instanceof Pilot) return ((Pilot) item).getId();
        if (item instanceof Engineer) return ((Engineer) item).getId();
        if (item instanceof Contract) return ((Contract) item).getId();
        return null;
    }

    public T getItem() { return item; }
    public double getPrice() { return price; }
    public ItemType getType() { return type; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public String getName() {
        if (item instanceof Component) return ((Component) item).getName();
        if (item instanceof Pilot) return ((Pilot) item).getName();
        if (item instanceof Engineer) return ((Engineer) item).getName();
        if (item instanceof Contract) return ((Contract) item).getName();
        return "Unknown";
    }

    @Override
    public String toString() {
        String typeStr = switch (type) {
            case COMPONENT -> "Компонент";
            case PILOT -> "Пилот";
            case ENGINEER -> "Инженер";
            case CONTRACT -> "Контракт";
        };
        return String.format("%s: %s [Цена: %.0f]", typeStr, getName(), price);
    }
}