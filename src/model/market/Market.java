package model.market;

import model.Contract;
import java.util.*;

public class Market {
    private String id;
    private String name;
    private List<MarketItem<?>> items;
    private boolean isOpen;

    public Market(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.items = new ArrayList<>();
        this.isOpen = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<MarketItem<?>> getItems() { return new ArrayList<>(items); }

    public void addItem(MarketItem<?> item) {
        items.add(item);
    }

    public void removeItem(MarketItem<?> item) {
        items.remove(item);
    }

    public void removeItemById(String itemId) {
        items.removeIf(item -> item.getId().equals(itemId));
    }

    public MarketItem<?> findItemById(String itemId) {
        return items.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    public List<MarketItem<?>> getAvailableItems() {
        return items.stream()
                .filter(MarketItem::isAvailable)
                .toList();
    }

    public List<MarketItem<model.components.Component>> getComponents() {
        return items.stream()
                .filter(item -> item.getType() == MarketItem.ItemType.COMPONENT)
                .map(item -> (MarketItem<model.components.Component>) item)
                .toList();
    }

    public List<MarketItem<model.staff.Pilot>> getPilots() {
        return items.stream()
                .filter(item -> item.getType() == MarketItem.ItemType.PILOT)
                .map(item -> (MarketItem<model.staff.Pilot>) item)
                .toList();
    }

    public List<MarketItem<model.staff.Engineer>> getEngineers() {
        return items.stream()
                .filter(item -> item.getType() == MarketItem.ItemType.ENGINEER)
                .map(item -> (MarketItem<model.staff.Engineer>) item)
                .toList();
    }

    public List<MarketItem<Contract>> getContracts() {
        return items.stream()
                .filter(item -> item.getType() == MarketItem.ItemType.CONTRACT)
                .map(item -> (MarketItem<Contract>) item)
                .toList();
    }

    public boolean isOpen() { return isOpen; }
    public void setOpen(boolean open) { isOpen = open; }

    public int getItemCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    @Override
    public String toString() {
        return String.format("Маркет %s | Товаров: %d | Популярность: %.1f",
                name, items.size());
    }
}