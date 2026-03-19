package controller;

import model.*;
import model.components.*;
import model.staff.*;
import model.market.*;
import service.GameService;
import service.MarketService;
import view.ConsoleView;
import java.util.*;

public class ShopController {
    private final GameService gameService;
    private final ConsoleView view;

    public ShopController(GameService gameService, ConsoleView view) {
        this.gameService = gameService;
        this.view = view;
    }

    public void buyComponents() {
        view.clearScreen();
        Manager player = gameService.getPlayerManager();
        MarketService market = gameService.getMarketService();

        showBudget(player);

        while (true) {
            int typeChoice = showComponentMenu();
            if (typeChoice == 6) return;

            List<? extends MarketItem<?>> items = getItemsByType(typeChoice, market);
            if (items.isEmpty()) {
                view.showMessage("Нет доступных компонентов этого типа.");
                continue;
            }

            processComponentPurchase(items, player, market);
            break;
        }
    }

    private void showBudget(Manager player) {
        view.showMessage("\n=== ПОКУПКА КОМПОНЕНТОВ ===");
        view.showMessage("Ваш бюджет: $" + String.format("%,.0f", player.getBudget()));
    }

    private int showComponentMenu() {
        view.showMessage("\n1. Двигатели");
        view.showMessage("2. Трансмиссии");
        view.showMessage("3. Подвески");
        view.showMessage("4. Аэродинамика");
        view.showMessage("5. Шины");
        view.showMessage("6. Назад в главное меню");
        return view.getUserIntInput("\nВыберите тип компонента: ", 1, 6);
    }

    private List<? extends MarketItem<?>> getItemsByType(int type, MarketService market) {
        return switch (type) {
            case 1 -> market.getAvailableEngines();
            case 2 -> market.getAvailableTransmissions();
            case 3 -> market.getAvailableSuspensions();
            case 4 -> market.getAvailableAerodynamics();
            case 5 -> market.getAvailableTyres();
            default -> new ArrayList<>();
        };
    }

    private void processComponentPurchase(List<? extends MarketItem<?>> items, Manager player, MarketService market) {
        displayItems(items);

        int compChoice = view.getUserIntInput("\nВыберите компонент (0 - назад): ", 0, items.size());
        if (compChoice == 0) return;

        MarketItem<?> selected = items.get(compChoice - 1);
        Component selectedComp = (Component) selected.getItem();

        showItemDetails(selectedComp);

        if (!checkBudget(player, selected.getPrice())) return;

        if (!confirmPurchase()) return;

        executeComponentPurchase(selectedComp, selected, player, market);
    }

    private void displayItems(List<? extends MarketItem<?>> items) {
        view.showMessage("\nДоступные компоненты:");
        for (int i = 0; i < items.size(); i++) {
            MarketItem<?> item = items.get(i);
            Component comp = (Component) item.getItem();
            view.showMessage(String.format("%d. %s [Цена: $%,.0f, Хар-ка: %.1f]",
                    i + 1, comp.getName(), item.getPrice(), comp.getBasePerformance()));
        }
    }

    private void showItemDetails(Component comp) {
        view.showMessage("\n📦 Информация о товаре:");
        view.showMessage("  Название: " + comp.getName());
        view.showMessage("  Цена: $" + String.format("%,.0f", comp.getPrice()));
        view.showMessage("  Характеристика: " + String.format("%.1f", comp.getBasePerformance()));

        if (comp instanceof Engine eng) {
            view.showMessage("  Тип: " + eng.getEngineType());
            view.showMessage("  Вес: " + eng.getWeight() + " кг");
        } else if (comp instanceof Transmission trans) {
            view.showMessage("  Совместимость: " + trans.getCompatibleEngineType());
        } else if (comp instanceof Suspension susp) {
            view.showMessage("  Макс. вес: " + susp.getMaxWeight() + " кг");
        }
    }

    private boolean checkBudget(Manager player, double price) {
        if (price > player.getBudget()) {
            view.showError("Недостаточно средств! Нужно еще $" +
                    String.format("%,.0f", price - player.getBudget()));
            return false;
        }
        return true;
    }

    private boolean confirmPurchase() {
        return view.getUserConfirmation("\nПодтвердить покупку?");
    }

    private void executeComponentPurchase(Component comp, MarketItem<?> item, Manager player, MarketService market) {
        boolean success = gameService.getShopService().buyComponent(comp);

        if (success) {
            market.buyItem(item.getId());
            view.showSuccess("Компонент куплен!");
            view.showMessage("Остаток бюджета: $" + String.format("%,.0f", player.getBudget()));

            offerCreateCar(comp);
        } else {
            view.showError("Не удалось купить компонент");
        }

        view.waitForEnter();
    }

    private void offerCreateCar(Component comp) {
        boolean createCar = view.getUserConfirmation("\nХотите создать новый болид с этим компонентом?");
        if (createCar) {
            String carName = view.getUserStringInput("Введите название болида: ");
            Car newCar = gameService.getShopService().createCar(carName);

            if (comp instanceof Engine) newCar.setEngine((Engine) comp);
            else if (comp instanceof Transmission) newCar.setTransmission((Transmission) comp);
            else if (comp instanceof Suspension) newCar.setSuspension((Suspension) comp);
            else if (comp instanceof Aerodynamics) newCar.setAerodynamics((Aerodynamics) comp);
            else if (comp instanceof Tyres) newCar.setTyres((Tyres) comp);

            gameService.getCarRepository().save(newCar);
            view.showSuccess("Создан новый болид: " + newCar.getName());
        }
    }

    public void hireEngineer() {
        view.clearScreen();
        Manager player = gameService.getPlayerManager();
        MarketService market = gameService.getMarketService();

        view.showMessage("\n=== НАЕМ ИНЖЕНЕРА ===");
        view.showMessage("Ваш бюджет: $" + String.format("%,.0f", player.getBudget()));

        List<MarketItem<Engineer>> engineers = market.getAvailableEngineers();

        if (engineers.isEmpty()) {
            view.showMessage("Нет доступных инженеров для найма.");
            view.waitForEnter();
            return;
        }

        displayEngineers(engineers);

        int choice = view.getUserIntInput("\nВыберите инженера (0 - отмена): ", 0, engineers.size());
        if (choice == 0) return;

        processEngineerHire(engineers.get(choice - 1), player, market);
    }

    private void displayEngineers(List<MarketItem<Engineer>> engineers) {
        view.showMessage("\nДоступные инженеры:");
        for (int i = 0; i < engineers.size(); i++) {
            Engineer eng = engineers.get(i).getItem();
            double yearlySalary = eng.getSalary() * 12;
            view.showMessage(String.format("%d. %s [Спец: %s, Ур.%d, З/п: $%,.0f/год, Эфф: %.0f%%]",
                    i + 1, eng.getName(), eng.getSpecialization(), eng.getLevel(),
                    yearlySalary, eng.getEfficiency() * 100));
        }
    }

    private void processEngineerHire(MarketItem<Engineer> item, Manager player, MarketService market) {
        Engineer engineer = item.getItem();
        double yearlySalary = engineer.getSalary() * 12;

        showEngineerDetails(engineer, yearlySalary);

        if (!checkBudget(player, yearlySalary)) {
            view.waitForEnter();
            return;
        }

        if (!confirmHire()) return;

        executeEngineerHire(engineer, item, player, market);
    }

    private void showEngineerDetails(Engineer engineer, double yearlySalary) {
        view.showMessage("\n📋 Информация о кандидате:");
        view.showMessage("  Имя: " + engineer.getName());
        view.showMessage("  Специализация: " + engineer.getSpecialization());
        view.showMessage("  Уровень: " + engineer.getLevel());
        view.showMessage("  Годовая зарплата: $" + String.format("%,.0f", yearlySalary));
        view.showMessage("  Эффективность: " + (engineer.getEfficiency() * 100) + "%");
    }

    private boolean confirmHire() {
        return view.getUserConfirmation("\nПодтвердить найм?");
    }

    private void executeEngineerHire(Engineer engineer, MarketItem<Engineer> item, Manager player, MarketService market) {
        boolean success = gameService.getShopService().hireEngineer(engineer.getId());

        if (success) {
            market.buyItem(item.getId());
            view.showSuccess("Инженер нанят!");
            view.showMessage("Остаток бюджета: $" + String.format("%,.0f", player.getBudget()));
            view.showMessage("Теперь в команде " + gameService.getPlayerEngineers().size() + " инженеров.");
        } else {
            view.showError("Не удалось нанять инженера");
        }

        view.waitForEnter();
    }

    public void hirePilot() {
        view.clearScreen();
        Manager player = gameService.getPlayerManager();
        MarketService market = gameService.getMarketService();

        view.showMessage("\n=== НАЕМ ПИЛОТА ===");
        view.showMessage("Ваш бюджет: $" + String.format("%,.0f", player.getBudget()));

        List<MarketItem<Pilot>> pilots = market.getAvailablePilots();

        if (pilots.isEmpty()) {
            view.showMessage("Нет доступных пилотов для найма.");
            view.waitForEnter();
            return;
        }

        displayPilots(pilots);

        int choice = view.getUserIntInput("\nВыберите пилота (0 - отмена): ", 0, pilots.size());
        if (choice == 0) return;

        processPilotHire(pilots.get(choice - 1), player, market);
    }

    private void displayPilots(List<MarketItem<Pilot>> pilots) {
        view.showMessage("\nДоступные пилоты:");
        for (int i = 0; i < pilots.size(); i++) {
            Pilot pilot = pilots.get(i).getItem();
            view.showMessage(String.format("%d. %s [Возраст: %d, Навык: %.1f, Цена: $%,.0f]",
                    i + 1, pilot.getName(), pilot.getAge(), pilot.getSkill(), pilot.getPrice()));
        }
    }

    private void processPilotHire(MarketItem<Pilot> item, Manager player, MarketService market) {
        Pilot pilot = item.getItem();

        showPilotDetails(pilot);

        if (!checkBudget(player, pilot.getPrice())) {
            view.waitForEnter();
            return;
        }

        if (!confirmHire()) return;

        executePilotHire(pilot, item, player, market);
    }

    private void showPilotDetails(Pilot pilot) {
        view.showMessage("\n📋 Информация о пилоте:");
        view.showMessage("  Имя: " + pilot.getName());
        view.showMessage("  Возраст: " + pilot.getAge());
        view.showMessage("  Навык: " + pilot.getSkill());
        view.showMessage("  Цена контракта: $" + String.format("%,.0f", pilot.getPrice()));
        view.showMessage("  Агрессивность: " + (pilot.getAggression() * 100) + "%");
        view.showMessage("  Стабильность: " + (pilot.getConsistency() * 100) + "%");
    }

    private void executePilotHire(Pilot pilot, MarketItem<Pilot> item, Manager player, MarketService market) {
        boolean success = gameService.getShopService().hirePilot(pilot.getId());

        if (success) {
            market.buyItem(item.getId());
            view.showSuccess("Пилот нанят!");
            view.showMessage("Остаток бюджета: $" + String.format("%,.0f", player.getBudget()));
            view.showMessage("Теперь в команде " + gameService.getPlayerPilots().size() + " пилотов.");
        } else {
            view.showError("Не удалось нанять пилота");
        }

        view.waitForEnter();
    }
}