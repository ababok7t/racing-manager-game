package controller;

import model.*;
import model.components.*;
import model.staff.*;
import model.market.*;
import service.GameService;
import service.MarketService;
import repository.ContractRepository;
import view.ConsoleIO;
import java.util.*;

public class ShopController {
    private final GameService gameService;
    private final ConsoleIO io;

    public ShopController(GameService gameService, ConsoleIO io) {
        this.gameService = gameService;
        this.io = io;
    }

    public void buyComponents() {
        io.clearScreen();
        Manager player = gameService.getPlayerManager();
        MarketService market = gameService.getMarketService();

        showBudget(player);

        while (true) {
            int typeChoice = showComponentMenu();
            if (typeChoice == 6) return;

            List<? extends MarketItem<?>> items = getItemsByType(typeChoice, market);
            if (items.isEmpty()) {
                io.showMessage("Нет доступных компонентов этого типа.");
                continue;
            }

            processComponentPurchase(items, player, market);
            break;
        }
    }

    private void showBudget(Manager player) {
        io.showMessage("\n=== ПОКУПКА КОМПОНЕНТОВ ===");
        io.showMessage("Ваш бюджет: $" + String.format("%,.0f", player.getBudget()));
    }

    private int showComponentMenu() {
        io.showMessage("\n1. Двигатели");
        io.showMessage("2. Трансмиссии");
        io.showMessage("3. Подвески");
        io.showMessage("4. Аэродинамика");
        io.showMessage("5. Шины");
        io.showMessage("6. Назад в главное меню");
        return io.getUserIntInput("\nВыберите тип компонента: ", 1, 6);
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

        int compChoice = io.getUserIntInput("\nВыберите компонент (0 - назад): ", 0, items.size());
        if (compChoice == 0) return;

        MarketItem<?> selected = items.get(compChoice - 1);
        Component selectedComp = (Component) selected.getItem();

        showItemDetails(selectedComp);

        if (!checkBudget(player, selected.getPrice())) return;

        if (!confirmPurchase()) return;

        executeComponentPurchase(selectedComp, selected, player, market);
    }

    private void displayItems(List<? extends MarketItem<?>> items) {
        io.showMessage("\nДоступные компоненты:");
        for (int i = 0; i < items.size(); i++) {
            MarketItem<?> item = items.get(i);
            Component comp = (Component) item.getItem();
            io.showMessage(String.format("%d. %s [Цена: $%,.0f, Хар-ка: %.1f]",
                    i + 1, comp.getName(), item.getPrice(), comp.getBasePerformance()));
        }
    }

    private void showItemDetails(Component comp) {
        io.showMessage("\n📦 Информация о товаре:");
        io.showMessage("  Название: " + comp.getName());
        io.showMessage("  Цена: $" + String.format("%,.0f", comp.getPrice()));
        io.showMessage("  Характеристика: " + String.format("%.1f", comp.getBasePerformance()));

        if (comp instanceof Engine eng) {
            io.showMessage("  Тип: " + eng.getEngineType());
            io.showMessage("  Вес: " + eng.getWeight() + " кг");
        } else if (comp instanceof Transmission trans) {
            io.showMessage("  Совместимость: " + trans.getCompatibleEngineType());
        } else if (comp instanceof Suspension susp) {
            io.showMessage("  Макс. вес: " + susp.getMaxWeight() + " кг");
        }
    }

    private boolean checkBudget(Manager player, double price) {
        if (price > player.getBudget()) {
            io.showError("Недостаточно средств! Нужно еще $" +
                    String.format("%,.0f", price - player.getBudget()));
            return false;
        }
        return true;
    }

    private boolean confirmPurchase() {
        return io.getUserConfirmation("\nПодтвердить покупку?");
    }

    private void executeComponentPurchase(Component comp, MarketItem<?> item, Manager player, MarketService market) {
        boolean success = gameService.getShopService().buyComponent(comp);

        if (success) {
            market.buyItem(item.getId());
            io.showSuccess("Компонент куплен!");
            io.showMessage("Остаток бюджета: $" + String.format("%,.0f", player.getBudget()));

            offerCreateCar(comp);
        } else {
            io.showError("Не удалось купить компонент");
        }

        io.waitForEnter();
    }

    private void offerCreateCar(Component comp) {
        boolean createCar = io.getUserConfirmation("\nХотите создать новый болид с этим компонентом?");
        if (createCar) {
            String carName = io.getUserStringInput("Введите название болида: ");
            Car newCar = gameService.getShopService().createCar(carName);

            if (comp instanceof Engine) newCar.setEngine((Engine) comp);
            else if (comp instanceof Transmission) newCar.setTransmission((Transmission) comp);
            else if (comp instanceof Suspension) newCar.setSuspension((Suspension) comp);
            else if (comp instanceof Aerodynamics) newCar.setAerodynamics((Aerodynamics) comp);
            else if (comp instanceof Tyres) newCar.setTyres((Tyres) comp);

            gameService.getCarRepository().save(newCar);
            io.showSuccess("Создан новый болид: " + newCar.getName());
        }
    }

    public void hireEngineer() {
        io.clearScreen();
        Manager player = gameService.getPlayerManager();
        MarketService market = gameService.getMarketService();

        io.showMessage("\n=== НАЕМ ИНЖЕНЕРА ===");
        io.showMessage("Ваш бюджет: $" + String.format("%,.0f", player.getBudget()));

        List<MarketItem<Engineer>> engineers = market.getAvailableEngineers();

        if (engineers.isEmpty()) {
            io.showMessage("Нет доступных инженеров для найма.");
            io.waitForEnter();
            return;
        }

        displayEngineers(engineers);

        int choice = io.getUserIntInput("\nВыберите инженера (0 - отмена): ", 0, engineers.size());
        if (choice == 0) return;

        processEngineerHire(engineers.get(choice - 1), player, market);
    }

    private void displayEngineers(List<MarketItem<Engineer>> engineers) {
        io.showMessage("\nДоступные инженеры:");
        for (int i = 0; i < engineers.size(); i++) {
            Engineer eng = engineers.get(i).getItem();
            double yearlySalary = eng.getSalary() * 12;
            io.showMessage(String.format("%d. %s [Спец: %s, Ур.%d, З/п: $%,.0f/год, Эфф: %.0f%%]",
                    i + 1, eng.getName(), eng.getSpecialization(), eng.getLevel(),
                    yearlySalary, eng.getEfficiency() * 100));
        }
    }

    private void processEngineerHire(MarketItem<Engineer> item, Manager player, MarketService market) {
        Engineer engineer = item.getItem();
        double yearlySalary = engineer.getSalary() * 12;

        showEngineerDetails(engineer, yearlySalary);

        if (!checkBudget(player, yearlySalary)) {
            io.waitForEnter();
            return;
        }

        if (!confirmHire()) return;

        executeEngineerHire(engineer, item, player, market);
    }

    private void showEngineerDetails(Engineer engineer, double yearlySalary) {
        io.showMessage("\n📋 Информация о кандидате:");
        io.showMessage("  Имя: " + engineer.getName());
        io.showMessage("  Специализация: " + engineer.getSpecialization());
        io.showMessage("  Уровень: " + engineer.getLevel());
        io.showMessage("  Годовая зарплата: $" + String.format("%,.0f", yearlySalary));
        io.showMessage("  Эффективность: " + (engineer.getEfficiency() * 100) + "%");
    }

    private boolean confirmHire() {
        return io.getUserConfirmation("\nПодтвердить найм?");
    }

    private void executeEngineerHire(Engineer engineer, MarketItem<Engineer> item, Manager player, MarketService market) {
        boolean success = gameService.getShopService().hireEngineer(engineer.getId());

        if (success) {
            market.buyItem(item.getId());
            io.showSuccess("Инженер нанят!");
            io.showMessage("Остаток бюджета: $" + String.format("%,.0f", player.getBudget()));
            io.showMessage("Теперь в команде " + gameService.getPlayerEngineers().size() + " инженеров.");
        } else {
            io.showError("Не удалось нанять инженера");
        }

        io.waitForEnter();
    }

    public void hirePilot() {
        io.clearScreen();
        Manager player = gameService.getPlayerManager();
        MarketService market = gameService.getMarketService();

        io.showMessage("\n=== НАЕМ ПИЛОТА ===");
        io.showMessage("Ваш бюджет: $" + String.format("%,.0f", player.getBudget()));

        List<MarketItem<Pilot>> pilots = market.getAvailablePilots();

        if (pilots.isEmpty()) {
            io.showMessage("Нет доступных пилотов для найма.");
            io.waitForEnter();
            return;
        }

        displayPilots(pilots);

        int choice = io.getUserIntInput("\nВыберите пилота (0 - отмена): ", 0, pilots.size());
        if (choice == 0) return;

        processPilotHire(pilots.get(choice - 1), player, market);
    }

    private void displayPilots(List<MarketItem<Pilot>> pilots) {
        io.showMessage("\nДоступные пилоты:");
        for (int i = 0; i < pilots.size(); i++) {
            Pilot pilot = pilots.get(i).getItem();
            io.showMessage(String.format("%d. %s [Возраст: %d, Навык: %.1f, Цена: $%,.0f]",
                    i + 1, pilot.getName(), pilot.getAge(), pilot.getSkill(), pilot.getPrice()));
        }
    }

    private void processPilotHire(MarketItem<Pilot> item, Manager player, MarketService market) {
        Pilot pilot = item.getItem();

        showPilotDetails(pilot);

        if (!checkBudget(player, pilot.getPrice())) {
            io.waitForEnter();
            return;
        }

        if (!confirmHire()) return;

        executePilotHire(pilot, item, player, market);
    }

    private void showPilotDetails(Pilot pilot) {
        io.showMessage("\n📋 Информация о пилоте:");
        io.showMessage("  Имя: " + pilot.getName());
        io.showMessage("  Возраст: " + pilot.getAge());
        io.showMessage("  Навык: " + pilot.getSkill());
        io.showMessage("  Цена контракта: $" + String.format("%,.0f", pilot.getPrice()));
        io.showMessage("  Агрессивность: " + (pilot.getAggression() * 100) + "%");
        io.showMessage("  Стабильность: " + (pilot.getConsistency() * 100) + "%");
    }

    private void executePilotHire(Pilot pilot, MarketItem<Pilot> item, Manager player, MarketService market) {
        boolean success = gameService.getShopService().hirePilot(pilot.getId());

        if (success) {
            market.buyItem(item.getId());
            io.showSuccess("Пилот нанят!");
            io.showMessage("Остаток бюджета: $" + String.format("%,.0f", player.getBudget()));
            io.showMessage("Теперь в команде " + gameService.getPlayerPilots().size() + " пилотов.");
        } else {
            io.showError("Не удалось нанять пилота");
        }

        io.waitForEnter();
    }

    public void manageSponsorContracts() {
        io.clearScreen();
        Manager player = gameService.getPlayerManager();
        MarketService market = gameService.getMarketService();
        ContractRepository contractRepository = gameService.getContractRepository();

        while (true) {
            io.showMessage("\n=== КОНТРАКТЫ СО СПОНСОРАМИ ===");
            io.showMessage("Ваш бюджет: $" + String.format("%,.0f", player.getBudget()));
            io.showMessage("Репутация: " + player.getReputation());

            showActiveContracts(contractRepository, player);

            List<MarketItem<Contract>> available = market.getAvailableContracts();
            List<MarketItem<Contract>> signable = available.stream()
                    .filter(item -> player.getReputation() >= item.getItem().getMinReputation())
                    .toList();

            if (signable.isEmpty()) {
                io.showMessage("\nНет доступных контрактов (или репутация слишком низкая).");
                io.showMessage("Нажмите Enter, чтобы вернуться в меню.");
                io.waitForEnter();
                return;
            }

            io.showMessage("\nДоступные для подписания контракты:");
            for (int i = 0; i < signable.size(); i++) {
                Contract c = signable.get(i).getItem();
                int remaining = Math.max(0, c.getNumberOfRaces() - c.getRacesCompleted());
                io.showMessage(String.format("%d. %s [Цена: $%,.0f, Гонки: %d, Мин. реп.: %d, Осталось: %d]",
                        i + 1, c.getName(), c.getPrice(), c.getNumberOfRaces(), c.getMinReputation(), remaining));
            }

            int choice = io.getUserIntInput("\nВыберите контракт для подписания (0 - назад): ", 0, signable.size());
            if (choice == 0) return;

            Contract selected = signable.get(choice - 1).getItem();
            io.showMessage("\nВы выбрали: " + selected.getName());
            io.showMessage("Стоимость: $" + String.format("%,.0f", selected.getPrice()));
            io.showMessage("Выполнение: " + selected.getNumberOfRaces() + " гонок");
            io.showMessage("Минимальная репутация: " + selected.getMinReputation());

            if (!io.getUserConfirmation("\nПодписать контракт?")) {
                io.waitForEnter();
                continue;
            }

            boolean success = gameService.getShopService().signContract(selected);
            if (success) {
                io.showSuccess("Контракт подписан!");
            } else {
                io.showError("Не удалось подписать контракт (возможно, недостаточно бюджета/репутации).");
            }

            io.waitForEnter();
        }
    }

    private void showActiveContracts(ContractRepository contractRepository, Manager player) {
        List<Contract> active = contractRepository.findAllById(player.getContractIds());

        io.showMessage("\nАктивные контракты:");
        if (active.isEmpty()) {
            io.showMessage("   (нет)");
            return;
        }

        for (int i = 0; i < active.size(); i++) {
            Contract c = active.get(i);
            int remaining = Math.max(0, c.getNumberOfRaces() - c.getRacesCompleted());
            io.showMessage(String.format("   %d) %s [Осталось: %d/%d гонок]", i + 1, c.getName(), remaining, c.getNumberOfRaces()));
        }
    }
}