package controller;

import model.*;
import model.components.*;
import service.GameService;
import view.ConsoleView;
import java.util.*;

public class CarController {
    private final GameService gameService;
    private final ConsoleView view;

    public CarController(GameService gameService, ConsoleView view) {
        this.gameService = gameService;
        this.view = view;
    }

    public void assembleCar() {
        view.clearScreen();
        List<Car> cars = getIncompleteCars();

        if (cars.isEmpty()) {
            view.showError("Нет доступных болидов для сборки!");
            view.showMessage("Сначала купите компоненты и создайте болид.");
            view.waitForEnter();
            return;
        }

        view.showMessage("\n=== СБОРКА БОЛИДА ===");
        Car selectedCar = selectCar(cars);
        if (selectedCar == null) return;

        CarAssembly assembly = new CarAssembly(selectedCar);
        if (!assembly.collectComponents()) return;

        if (assembly.confirmAndAssemble()) {
            saveAssembledCar(selectedCar, assembly);
        }
    }

    private List<Car> getIncompleteCars() {
        return gameService.getPlayerCars().stream()
                .filter(c -> !c.isComplete())
                .toList();
    }

    private Car selectCar(List<Car> cars) {
        view.showMessage("Доступные болиды:");
        for (int i = 0; i < cars.size(); i++) {
            view.showMessage((i + 1) + ". " + cars.get(i).getName());
        }

        int carChoice = view.getUserIntInput("\nВыберите болид: ", 1, cars.size()) - 1;
        return cars.get(carChoice);
    }

    private void saveAssembledCar(Car car, CarAssembly assembly) {
        boolean success = gameService.getShopService().assembleCar(
                car.getId(),
                assembly.engine,
                assembly.transmission,
                assembly.suspension,
                assembly.aerodynamics,
                assembly.tyres
        );

        if (success) {
            removeUsedComponents(assembly);
            view.showSuccess("✅ Болид успешно собран!");
            view.showMessage("Новая производительность: " +
                    String.format("%.2f", car.calculatePerformance()));
        } else {
            view.showError("❌ Не удалось собрать болид!");
        }

        view.waitForEnter();
    }

    private void removeUsedComponents(CarAssembly assembly) {
        if (assembly.engine != null) gameService.getComponentRepository().delete(assembly.engine.getId());
        if (assembly.transmission != null) gameService.getComponentRepository().delete(assembly.transmission.getId());
        if (assembly.suspension != null) gameService.getComponentRepository().delete(assembly.suspension.getId());
        if (assembly.aerodynamics != null) gameService.getComponentRepository().delete(assembly.aerodynamics.getId());
        if (assembly.tyres != null) gameService.getComponentRepository().delete(assembly.tyres.getId());
    }

    public void viewCars() {
        view.clearScreen();
        List<Car> cars = gameService.getPlayerCars();

        view.showMessage("\n=== ВАШИ БОЛИДЫ ===");

        if (cars.isEmpty()) {
            view.showMessage("У вас пока нет болидов.");
            view.showMessage("Купите компоненты и создайте первый болид!");
        } else {
            displayCars(cars);
            offerRepair(cars);
        }

        view.waitForEnter();
    }

    private void displayCars(List<Car> cars) {
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            view.showMessage("\n" + (i + 1) + ". " + car.getName());

            if (car.isComplete()) {
                displayCompleteCar(car);
            } else {
                view.showMessage("   ❌ Статус: Не собран");
                view.showMessage("   🔧 Требуется сборка");
            }
        }
    }

    private void displayCompleteCar(Car car) {
        view.showMessage("   ✅ Статус: Собран");
        view.showMessage("   📊 Производительность: " +
                String.format("%.2f", car.calculatePerformance()));
        view.showMessage("   🔧 Износ: " + String.format("%.1f%%", car.getWearPercentage()));

        if (car.getWearPercentage() > 80) {
            view.showWarning("   ⚠️ Высокий износ! Требуется ремонт.");
        }

        view.showMessage("\n   Компоненты:");
        if (car.getEngine() != null)
            view.showMessage("     • Двигатель: " + car.getEngine().getName());
        if (car.getTransmission() != null)
            view.showMessage("     • Трансмиссия: " + car.getTransmission().getName());
        if (car.getSuspension() != null)
            view.showMessage("     • Подвеска: " + car.getSuspension().getName());
        if (car.getAerodynamics() != null)
            view.showMessage("     • Аэродинамика: " + car.getAerodynamics().getName());
        if (car.getTyres() != null)
            view.showMessage("     • Шины: " + car.getTyres().getName());
    }

    private void offerRepair(List<Car> cars) {
        boolean needsRepair = cars.stream()
                .anyMatch(c -> c.isComplete() && c.getWearPercentage() > 50);

        if (needsRepair) {
            boolean repair = view.getUserConfirmation("\nХотите отремонтировать болиды?");
            if (repair) {
                repairCars();
            }
        }
    }

    public void repairCars() {
        Manager player = gameService.getPlayerManager();
        List<Car> cars = gameService.getPlayerCars().stream()
                .filter(Car::isComplete)
                .toList();

        if (cars.isEmpty()) {
            view.showMessage("Нет болидов для ремонта.");
            return;
        }

        view.showMessage("\n=== РЕМОНТ БОЛИДОВ ===");

        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            double repairCost = car.getWearPercentage() * 100;
            view.showMessage(String.format("%d. %s [Износ: %.1f%%, Стоимость ремонта: $%,.0f]",
                    i + 1, car.getName(), car.getWearPercentage(), repairCost));
        }

        int choice = view.getUserIntInput("\nВыберите болид для ремонта (0 - отмена): ", 0, cars.size());
        if (choice == 0) return;

        processRepair(cars.get(choice - 1), player);
    }

    private void processRepair(Car car, Manager player) {
        double repairCost = car.getWearPercentage() * 100;

        view.showMessage("Стоимость ремонта: $" + String.format("%,.0f", repairCost));
        view.showMessage("Ваш бюджет: $" + String.format("%,.0f", player.getBudget()));

        if (repairCost > player.getBudget()) {
            view.showError("Недостаточно средств!");
            return;
        }

        if (!view.getUserConfirmation("Подтвердить ремонт?")) return;

        boolean success = gameService.getShopService().repairCar(car.getId());

        if (success) {
            view.showSuccess("Болид отремонтирован!");
            view.showMessage("Новый износ: 0%");
        } else {
            view.showError("Не удалось отремонтировать болид");
        }
    }

    /**
     * Вспомогательный класс для сборки болида
     */
    private class CarAssembly {
        private final Car car;
        private Engine engine;
        private Transmission transmission;
        private Suspension suspension;
        private Aerodynamics aerodynamics;
        private Tyres tyres;

        CarAssembly(Car car) {
            this.car = car;
            this.engine = car.getEngine();
            this.transmission = car.getTransmission();
            this.suspension = car.getSuspension();
            this.aerodynamics = car.getAerodynamics();
            this.tyres = car.getTyres();
        }

        boolean collectComponents() {
            if (!selectEngine()) return false;
            if (!selectTransmission()) return false;
            if (!selectSuspension()) return false;
            if (!selectAerodynamics()) return false;
            return selectTyres();
        }

        private boolean selectEngine() {
            if (engine != null) return true;

            List<Engine> engines = gameService.getComponentRepository().findByType(Engine.class);
            if (engines.isEmpty()) {
                view.showError("Нет купленных двигателей!");
                view.waitForEnter();
                return false;
            }

            view.showMessage("\nДоступные двигатели (купленные):");
            for (int i = 0; i < engines.size(); i++) {
                Engine eng = engines.get(i);
                view.showMessage(String.format("%d. %s [Мощность: %.1f, Тип: %s, Цена: $%,.0f]",
                        i + 1, eng.getName(), eng.getBasePower(), eng.getEngineType(), eng.getPrice()));
            }

            int choice = view.getUserIntInput("Выберите двигатель: ", 1, engines.size()) - 1;
            engine = engines.get(choice);
            return true;
        }

        private boolean selectTransmission() {
            if (transmission != null) return true;

            List<Transmission> transmissions = gameService.getComponentRepository().findByType(Transmission.class);
            if (transmissions.isEmpty()) {
                view.showError("Нет купленных трансмиссий!");
                view.waitForEnter();
                return false;
            }

            List<Transmission> compatible = transmissions.stream()
                    .filter(t -> t.isCompatibleWith(engine))
                    .toList();

            if (compatible.isEmpty()) {
                view.showError("Нет купленных совместимых трансмиссий!");
                view.waitForEnter();
                return false;
            }

            view.showMessage("\nДоступные трансмиссии (совместимые):");
            for (int i = 0; i < compatible.size(); i++) {
                Transmission trans = compatible.get(i);
                view.showMessage(String.format("%d. %s [Эффективность: %.2f, Цена: $%,.0f]",
                        i + 1, trans.getName(), trans.getBaseEfficiency(), trans.getPrice()));
            }

            int choice = view.getUserIntInput("Выберите трансмиссию: ", 1, compatible.size()) - 1;
            transmission = compatible.get(choice);
            return true;
        }

        private boolean selectSuspension() {
            if (suspension != null) return true;

            List<Suspension> suspensions = gameService.getComponentRepository().findByType(Suspension.class);
            if (suspensions.isEmpty()) {
                view.showError("Нет купленных подвесок!");
                view.waitForEnter();
                return false;
            }

            List<Suspension> compatible = suspensions.stream()
                    .filter(s -> s.canSupportWeight(engine.getWeight()))
                    .toList();

            if (compatible.isEmpty()) {
                view.showError("Нет купленных подвесок, которые выдержат вес двигателя!");
                view.waitForEnter();
                return false;
            }

            view.showMessage("\nДоступные подвески (совместимые):");
            for (int i = 0; i < compatible.size(); i++) {
                Suspension susp = compatible.get(i);
                view.showMessage(String.format("%d. %s [Стабильность: %.1f, Макс.вес: %d кг, Цена: $%,.0f]",
                        i + 1, susp.getName(), susp.getBaseStability(), susp.getMaxWeight(), susp.getPrice()));
            }

            int choice = view.getUserIntInput("Выберите подвеску: ", 1, compatible.size()) - 1;
            suspension = compatible.get(choice);
            return true;
        }

        private boolean selectAerodynamics() {
            if (aerodynamics != null) return true;

            List<Aerodynamics> aeroList = gameService.getComponentRepository().findByType(Aerodynamics.class);
            if (aeroList.isEmpty()) {
                view.showError("Нет купленных аэродинамических пакетов!");
                view.waitForEnter();
                return false;
            }

            view.showMessage("\nДоступная аэродинамика (купленная):");
            for (int i = 0; i < aeroList.size(); i++) {
                Aerodynamics aero = aeroList.get(i);
                view.showMessage(String.format("%d. %s [Прижимная сила: %.1f, Цена: $%,.0f]",
                        i + 1, aero.getName(), aero.getBaseDownforce(), aero.getPrice()));
            }

            int choice = view.getUserIntInput("Выберите аэродинамику: ", 1, aeroList.size()) - 1;
            aerodynamics = aeroList.get(choice);
            return true;
        }

        private boolean selectTyres() {
            if (tyres != null) return true;

            List<Tyres> tyresList = gameService.getComponentRepository().findByType(Tyres.class);
            if (tyresList.isEmpty()) {
                view.showError("Нет купленных шин!");
                view.waitForEnter();
                return false;
            }

            view.showMessage("\nДоступные шины (купленные):");
            for (int i = 0; i < tyresList.size(); i++) {
                Tyres t = tyresList.get(i);
                view.showMessage(String.format("%d. %s [Сцепление: %.1f, Состав: %s, Цена: $%,.0f]",
                        i + 1, t.getName(), t.getBaseGrip(), t.getCompound(), t.getPrice()));
            }

            int choice = view.getUserIntInput("Выберите шины: ", 1, tyresList.size()) - 1;
            tyres = tyresList.get(choice);
            return true;
        }

        boolean confirmAndAssemble() {
            view.showMessage("\n📋 Итоговая конфигурация:");
            view.showMessage("  Двигатель: " + engine.getName());
            view.showMessage("  Трансмиссия: " + transmission.getName());
            view.showMessage("  Подвеска: " + suspension.getName());
            view.showMessage("  Аэродинамика: " + aerodynamics.getName());
            view.showMessage("  Шины: " + tyres.getName());

            double estimatedPerf = engine.getBasePower() * 0.3 +
                    transmission.getBaseEfficiency() * 0.2 +
                    suspension.getBaseStability() * 0.2 +
                    aerodynamics.getBaseDownforce() * 0.2 +
                    tyres.getBaseGrip() * 0.1;

            view.showMessage("  Примерная производительность: " + String.format("%.2f", estimatedPerf));

            return view.getUserConfirmation("\nПодтвердить сборку?");
        }
    }
}