package model.market;

import java.util.*;

public class Market {
    private List<MarketItem> items;

    public Market() {
        this.items = new ArrayList<>();
        fillMarket(); // заполняем товарами при создании
    }

    // Заполнение рынка товарами
    private void fillMarket() {
        // Двигатели
        items.add(new MarketItem("V6 Base", "ENGINE", 15000, 5.0, null));
        items.add(new MarketItem("V8 Sport", "ENGINE", 25000, 7.5, null));
        items.add(new MarketItem("V12 Turbo", "ENGINE", 40000, 9.0, null));

        // Трансмиссии
        items.add(new MarketItem("6-Speed", "TRANSMISSION", 8000, 4.0, null));
        items.add(new MarketItem("8-Speed", "TRANSMISSION", 15000, 6.5, null));
        items.add(new MarketItem("Sequential", "TRANSMISSION", 20000, 8.0, null));

        // Подвески
        items.add(new MarketItem("Standard", "SUSPENSION", 5000, 3.0, null));
        items.add(new MarketItem("Sport", "SUSPENSION", 10000, 5.5, null));
        items.add(new MarketItem("Pro", "SUSPENSION", 18000, 8.0, null));

        // Пилоты
        items.add(new MarketItem("Новичок Смит", "PILOT", 5000, 60, null));
        items.add(new MarketItem("Профи Джонс", "PILOT", 20000, 75, null));
        items.add(new MarketItem("Чемпион Ли", "PILOT", 35000, 90, null));

        // Инженеры
        items.add(new MarketItem("Механик", "ENGINEER", 8000, 65, null));
        items.add(new MarketItem("Аэродинамик", "ENGINEER", 12000, 80, null));
        items.add(new MarketItem("Главный инженер", "ENGINEER", 20000, 95, null));
    }

    // Получить все товары
    public List<MarketItem> getAllItems() {
        return items;
    }

    // Получить товары определенного типа
    public List<MarketItem> getItemsByType(String type) {
        List<MarketItem> result = new ArrayList<>();
        for (MarketItem item : items) {
            if (item.getType().equals(type)) {
                result.add(item);
            }
        }
        return result;
    }

    // Купить товар (удалить с рынка)
    public void removeItem(MarketItem item) {
        items.remove(item);
    }

    // Добавить товар (продажа)
    public void addItem(MarketItem item) {
        items.add(item);
    }

    // Обновить цены (рандомно)
    public void updatePrices() {
        Random rand = new Random();
        for (MarketItem item : items) {
            double priceChange = 0.8 + rand.nextDouble() * 0.4; // 0.8 - 1.2
            item = new MarketItem(
                    item.getName(),
                    item.getType(),
                    item.getPrice() * priceChange,
                    item.getPerformance(),
                    item.getItem()
            );
        }
    }
}