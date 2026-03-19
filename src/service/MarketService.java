package service;

import model.market.*;
import model.components.*;
import model.staff.*;
import repository.MarketRepository;
import java.util.*;

public class MarketService {
    private final MarketRepository marketRepository;
    private Market mainMarket;
    @SuppressWarnings("unused")
    private final GameService gameService;

    public MarketService() {
        this(null);
    }

    public MarketService(GameService gameService) {
        this.gameService = gameService;
        this.marketRepository = new MarketRepository();
        initializeMainMarket();
    }

    /**
     * Метод вызывается из GameService при старте. В текущей реализации маркет заполняется в конструкторе,
     * поэтому метод здесь оставлен как совместимость.
     */
    public void initializeMarket() {
        if (gameService == null) return;

        // Инициализируем "доступных для найма" сущности в репозиториях.
        // Компоненты игрок покупает через ShopService, поэтому их сюда не добавляем.
        for (MarketItem<Pilot> item : mainMarket.getPilots()) {
            Pilot pilot = item.getItem();
            gameService.getPilotRepository().save(pilot);
        }

        for (MarketItem<Engineer> item : mainMarket.getEngineers()) {
            Engineer engineer = item.getItem();
            gameService.getEngineerRepository().save(engineer);
        }
    }

    private void initializeMainMarket() {
        mainMarket = new Market("Главный автомобильный рынок");
        fillMarketWithItems();
        marketRepository.save(mainMarket);
    }

    private void fillMarketWithItems() {
        // Добавляем компоненты
        addComponents();
        // Добавляем пилотов
        addPilots();
        // Добавляем инженеров
        addEngineers();
    }

    private void addComponents() {
        // Двигатели
        mainMarket.addItem(new MarketItem<>(
                new Engine("Базовый V6", 20000, 80, 70, "Atmospheric", 150),
                20000, MarketItem.ItemType.COMPONENT));
        mainMarket.addItem(new MarketItem<>(
                new Engine("Спортивный V8", 35000, 120, 60, "Turbo", 160),
                35000, MarketItem.ItemType.COMPONENT));
        mainMarket.addItem(new MarketItem<>(
                new Engine("Элитный V12", 60000, 180, 50, "Hybrid", 170),
                60000, MarketItem.ItemType.COMPONENT));

        // Трансмиссии
        mainMarket.addItem(new MarketItem<>(
                new Transmission("Спортивная 6-ступ", 15000, 0.85, 70, "Atmospheric"),
                15000, MarketItem.ItemType.COMPONENT));
        mainMarket.addItem(new MarketItem<>(
                new Transmission("Гоночная 8-ступ", 25000, 0.95, 60, "Turbo"),
                25000, MarketItem.ItemType.COMPONENT));

        // Подвески
        mainMarket.addItem(new MarketItem<>(
                new Suspension("Стандартная", 12000, 7, 180, 70),
                12000, MarketItem.ItemType.COMPONENT));
        mainMarket.addItem(new MarketItem<>(
                new Suspension("Спортивная", 22000, 9, 200, 60),
                22000, MarketItem.ItemType.COMPONENT));

        // Аэродинамика
        mainMarket.addItem(new MarketItem<>(
                new Aerodynamics("Базовый пакет", 10000, 8, 70),
                10000, MarketItem.ItemType.COMPONENT));
        mainMarket.addItem(new MarketItem<>(
                new Aerodynamics("Гоночный пакет", 28000, 15, 60),
                28000, MarketItem.ItemType.COMPONENT));

        // Шины
        mainMarket.addItem(new MarketItem<>(
                new Tyres("Soft", 8000, 9, "Soft", 1.3, 50),
                8000, MarketItem.ItemType.COMPONENT));
        mainMarket.addItem(new MarketItem<>(
                new Tyres("Medium", 6000, 7, "Medium", 1.0, 65),
                6000, MarketItem.ItemType.COMPONENT));
        mainMarket.addItem(new MarketItem<>(
                new Tyres("Hard", 4000, 5, "Hard", 0.7, 80),
                4000, MarketItem.ItemType.COMPONENT));
    }

    private void addPilots() {
        mainMarket.addItem(new MarketItem<>(
                new Pilot("Льюис Хэмилтон", 38, 95, 500000),
                500000, MarketItem.ItemType.PILOT));
        mainMarket.addItem(new MarketItem<>(
                new Pilot("Макс Ферстаппен", 26, 94, 480000),
                480000, MarketItem.ItemType.PILOT));
        mainMarket.addItem(new MarketItem<>(
                new Pilot("Шарль Леклер", 26, 89, 350000),
                350000, MarketItem.ItemType.PILOT));
        mainMarket.addItem(new MarketItem<>(
                new Pilot("Ландо Норрис", 24, 85, 250000),
                250000, MarketItem.ItemType.PILOT));
    }

    private void addEngineers() {
        mainMarket.addItem(new MarketItem<>(
                new Engineer("Джон Смит", "Механик", 5, 6000, 0.9),
                72000, MarketItem.ItemType.ENGINEER)); // годовая зарплата
        mainMarket.addItem(new MarketItem<>(
                new Engineer("Майк Браун", "Аэродинамик", 4, 5500, 0.85),
                66000, MarketItem.ItemType.ENGINEER));
        mainMarket.addItem(new MarketItem<>(
                new Engineer("Роберт Джонсон", "Электронщик", 4, 5200, 0.85),
                62400, MarketItem.ItemType.ENGINEER));
    }

    public Market getMainMarket() {
        return mainMarket;
    }

    public MarketRepository getMarketRepository() {
        return marketRepository;
    }

    public List<MarketItem<?>> getAllItems() {
        return mainMarket.getItems();
    }

    public List<MarketItem<?>> getAvailableItems() {
        return mainMarket.getAvailableItems();
    }

    public List<MarketItem<Engine>> getAvailableEngines() {
        return mainMarket.getItems().stream()
                .filter(MarketItem::isAvailable)
                .filter(item -> item.getItem() instanceof Engine)
                .map(item -> (MarketItem<Engine>) (MarketItem<?>) item)
                .toList();
    }

    public List<MarketItem<Transmission>> getAvailableTransmissions() {
        return mainMarket.getItems().stream()
                .filter(MarketItem::isAvailable)
                .filter(item -> item.getItem() instanceof Transmission)
                .map(item -> (MarketItem<Transmission>) (MarketItem<?>) item)
                .toList();
    }

    public List<MarketItem<Suspension>> getAvailableSuspensions() {
        return mainMarket.getItems().stream()
                .filter(MarketItem::isAvailable)
                .filter(item -> item.getItem() instanceof Suspension)
                .map(item -> (MarketItem<Suspension>) (MarketItem<?>) item)
                .toList();
    }

    public List<MarketItem<Aerodynamics>> getAvailableAerodynamics() {
        return mainMarket.getItems().stream()
                .filter(MarketItem::isAvailable)
                .filter(item -> item.getItem() instanceof Aerodynamics)
                .map(item -> (MarketItem<Aerodynamics>) (MarketItem<?>) item)
                .toList();
    }

    public List<MarketItem<Tyres>> getAvailableTyres() {
        return mainMarket.getItems().stream()
                .filter(MarketItem::isAvailable)
                .filter(item -> item.getItem() instanceof Tyres)
                .map(item -> (MarketItem<Tyres>) (MarketItem<?>) item)
                .toList();
    }

    public List<MarketItem<Pilot>> getAvailablePilots() {
        return mainMarket.getPilots().stream()
                .filter(MarketItem::isAvailable)
                .toList();
    }

    public List<MarketItem<Engineer>> getAvailableEngineers() {
        return mainMarket.getEngineers().stream()
                .filter(MarketItem::isAvailable)
                .toList();
    }

    public void buyItem(String itemId) {
        MarketItem<?> item = mainMarket.findItemById(itemId);
        if (item != null) {
            item.setAvailable(false);
        }
    }

    // Методы для ShopService (маркируем товар как купленный/нанятый)
    public void buyComponent(Component component) {
        if (component == null) return;
        buyItem(component.getId());
    }

    public void hirePilot(Pilot pilot) {
        if (pilot == null) return;
        buyItem(pilot.getId());
    }

    public void hireEngineer(Engineer engineer) {
        if (engineer == null) return;
        buyItem(engineer.getId());
    }

    public void refreshMarket() {
        // Обновляем доступность товаров
        for (MarketItem<?> item : mainMarket.getItems()) {
            if (!item.isAvailable() && Math.random() < 0.3) { // 30% шанс появления нового товара
                item.setAvailable(true);
            }
        }
    }
}