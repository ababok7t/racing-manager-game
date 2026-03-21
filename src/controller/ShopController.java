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
            int componentTypeChoice = showComponentMenu();
            if (componentTypeChoice == 6) return;

            List<? extends MarketItem<?>> availableItems = getItemsByType(componentTypeChoice, market);
            if (availableItems.isEmpty()) {
                io.showMessage("Нет доступных компонентов этого типа.");
                continue;
            }

            processComponentPurchase(availableItems, player, market);
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

    private void processComponentPurchase(List<? extends MarketItem<?>> availableItems, Manager player, MarketService market) {
        displayItems(availableItems);

        int componentChoice = io.getUserIntInput("\nВыберите компонент (0 - назад): ", 0, availableItems.size());
        if (componentChoice == 0) return;

        MarketItem<?> selectedMarketItem = availableItems.get(componentChoice - 1);
        Component selectedComponent = (Component) selectedMarketItem.getItem();

        showItemDetails(selectedComponent);

        if (!checkBudget(player, selectedMarketItem.getPrice())) return;

        if (!confirmPurchase()) return;

        executeComponentPurchase(selectedComponent, player);
    }

    private void displayItems(List<? extends MarketItem<?>> availableItems) {
        io.showMessage("\nДоступные компоненты:");
        for (int i = 0; i < availableItems.size(); i++) {
            MarketItem<?> marketItem = availableItems.get(i);
            Component component = (Component) marketItem.getItem();
            io.showMessage(String.format("%d. %s [Цена: $%,.0f, Хар-ка: %.1f]",
                    i + 1, component.getName(), marketItem.getPrice(), component.getBasePerformance()));
        }
    }

    private void showItemDetails(Component component) {
        io.showMessage("\nИнформация о товаре:");
        io.showMessage("  Название: " + component.getName());
        io.showMessage("  Цена: $" + String.format("%,.0f", component.getPrice()));
        io.showMessage("  Характеристика: " + String.format("%.1f", component.getBasePerformance()));

        if (component instanceof Engine engine) {
            io.showMessage("  Тип: " + engine.getEngineType());
            io.showMessage("  Вес: " + engine.getWeight() + " кг");
        } else if (component instanceof Transmission transmission) {
            io.showMessage("  Совместимость: " + transmission.getCompatibleEngineType());
        } else if (component instanceof Suspension suspension) {
            io.showMessage("  Макс. вес: " + suspension.getMaxWeight() + " кг");
        }
    }

    private boolean checkBudget(Manager player, double componentPrice) {
        if (componentPrice > player.getBudget()) {
            io.showError("Недостаточно средств! Нужно еще $" +
                    String.format("%,.0f", componentPrice - player.getBudget()));
            return false;
        }
        return true;
    }

    private boolean confirmPurchase() {
        return io.getUserConfirmation("\nПодтвердить покупку?");
    }

    private void executeComponentPurchase(Component component, Manager player) {
        boolean success = gameService.getShopService().buyComponent(component);

        if (success) {
            io.showSuccess("Компонент куплен!");
            io.showMessage("Остаток бюджета: $" + String.format("%,.0f", player.getBudget()));

            offerCreateCar(component);
        } else {
            io.showError("Не удалось купить компонент");
        }

        io.waitForEnter();
    }

    private void offerCreateCar(Component component) {
        boolean createCar = io.getUserConfirmation("\nХотите создать новый болид с этим компонентом?");
        if (createCar) {
            String carName = io.getUserStringInput("Введите название болида: ");
            Car newCar = gameService.getShopService().createCar(carName);

            if (component instanceof Engine engine) newCar.setEngine(engine);
            else if (component instanceof Transmission transmission) newCar.setTransmission(transmission);
            else if (component instanceof Suspension suspension) newCar.setSuspension(suspension);
            else if (component instanceof Aerodynamics aerodynamics) newCar.setAerodynamics(aerodynamics);
            else if (component instanceof Tyres tyres) newCar.setTyres(tyres);

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

        processEngineerHire(engineers.get(choice - 1), player);
    }

    private void displayEngineers(List<MarketItem<Engineer>> engineers) {
        io.showMessage("\nДоступные инженеры:");
        for (int i = 0; i < engineers.size(); i++) {
            Engineer engineer = engineers.get(i).getItem();
            double yearlySalary = engineer.getSalary() * 12;
            io.showMessage(String.format("%d. %s [Спец: %s, Ур.%d, З/п: $%,.0f/год, Эфф: %.0f%%]",
                    i + 1, engineer.getName(), engineer.getSpecialization(), engineer.getLevel(),
                    yearlySalary, engineer.getEfficiency() * 100));
        }
    }

    private void processEngineerHire(MarketItem<Engineer> item, Manager player) {
        Engineer engineer = item.getItem();
        double yearlySalary = engineer.getSalary() * 12;

        showEngineerDetails(engineer, yearlySalary);

        if (!checkBudget(player, yearlySalary)) {
            io.waitForEnter();
            return;
        }

        if (!confirmHire()) return;

        executeEngineerHire(engineer, player);
    }

    private void showEngineerDetails(Engineer engineer, double yearlySalary) {
        io.showMessage("\nИнформация о кандидате:");
        io.showMessage("  Имя: " + engineer.getName());
        io.showMessage("  Специализация: " + engineer.getSpecialization());
        io.showMessage("  Уровень: " + engineer.getLevel());
        io.showMessage("  Годовая зарплата: $" + String.format("%,.0f", yearlySalary));
        io.showMessage("  Эффективность: " + (engineer.getEfficiency() * 100) + "%");
    }

    private boolean confirmHire() {
        return io.getUserConfirmation("\nПодтвердить найм?");
    }

    private void executeEngineerHire(Engineer engineer, Manager player) {
        boolean success = gameService.getShopService().hireEngineer(engineer.getId());

        if (success) {
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

        processPilotHire(pilots.get(choice - 1), player);
    }

    private void displayPilots(List<MarketItem<Pilot>> pilots) {
        io.showMessage("\nДоступные пилоты:");
        for (int i = 0; i < pilots.size(); i++) {
            Pilot pilot = pilots.get(i).getItem();
            io.showMessage(String.format("%d. %s [Возраст: %d, Навык: %.1f, Цена: $%,.0f]",
                    i + 1, pilot.getName(), pilot.getAge(), pilot.getSkill(), pilot.getPrice()));
        }
    }

    private void processPilotHire(MarketItem<Pilot> item, Manager player) {
        Pilot pilot = item.getItem();

        showPilotDetails(pilot);

        if (!checkBudget(player, pilot.getPrice())) {
            io.waitForEnter();
            return;
        }

        if (!confirmHire()) return;

        executePilotHire(pilot, player);
    }

    private void showPilotDetails(Pilot pilot) {
        io.showMessage("\nИнформация о пилоте:");
        io.showMessage("  Имя: " + pilot.getName());
        io.showMessage("  Возраст: " + pilot.getAge());
        io.showMessage("  Навык: " + pilot.getSkill());
        io.showMessage("  Цена контракта: $" + String.format("%,.0f", pilot.getPrice()));
        io.showMessage("  Агрессивность: " + (pilot.getAggression() * 100) + "%");
        io.showMessage("  Стабильность: " + (pilot.getConsistency() * 100) + "%");
    }

    private void executePilotHire(Pilot pilot, Manager player) {
        boolean success = gameService.getShopService().hirePilot(pilot.getId());

        if (success) {
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

            List<MarketItem<Contract>> availableContracts = market.getAvailableContracts();
            List<MarketItem<Contract>> signableContracts = availableContracts.stream()
                    .filter(item -> player.getReputation() >= item.getItem().getMinReputation())
                    .toList();

            if (signableContracts.isEmpty()) {
                io.showMessage("\nНет доступных контрактов (или репутация слишком низкая).");
                io.showMessage("Нажмите Enter, чтобы вернуться в меню.");
                io.waitForEnter();
                return;
            }

            io.showMessage("\nДоступные для подписания контракты:");
            for (int i = 0; i < signableContracts.size(); i++) {
                Contract contract = signableContracts.get(i).getItem();
                int remaining = Math.max(0, contract.getNumberOfRaces() - contract.getRacesCompleted());
                io.showMessage(String.format("%d. %s [Цена: $%,.0f, Гонки: %d, Мин. реп.: %d, Осталось: %d]",
                        i + 1, contract.getName(), contract.getPrice(),
                        contract.getNumberOfRaces(), contract.getMinReputation(), remaining));
            }

            int contractChoice = io.getUserIntInput("\nВыберите контракт для подписания (0 - назад): ", 0, signableContracts.size());
            if (contractChoice == 0) return;

            Contract selectedContract = signableContracts.get(contractChoice - 1).getItem();
            io.showMessage("\nВы выбрали: " + selectedContract.getName());
            io.showMessage("Стоимость: $" + String.format("%,.0f", selectedContract.getPrice()));
            io.showMessage("Выполнение: " + selectedContract.getNumberOfRaces() + " гонок");
            io.showMessage("Минимальная репутация: " + selectedContract.getMinReputation());

            if (!io.getUserConfirmation("\nПодписать контракт?")) {
                io.waitForEnter();
                continue;
            }

            boolean success = gameService.getShopService().signContract(selectedContract);
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
            Contract contract = active.get(i);
            int remaining = Math.max(0, contract.getNumberOfRaces() - contract.getRacesCompleted());
            io.showMessage(String.format("   %d) %s [Осталось: %d/%d гонок]", i + 1, contract.getName(), remaining, contract.getNumberOfRaces()));
        }
    }
}