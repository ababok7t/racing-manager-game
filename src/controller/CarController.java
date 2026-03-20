package controller;

import model.*;
import model.components.*;
import service.GameService;
import view.ConsoleIO;
import java.util.*;

public class CarController {
    private final GameService gameService;
    private final ConsoleIO io;

    public CarController(GameService gameService, ConsoleIO io) {
        this.gameService = gameService;
        this.io = io;
    }

    public void assembleCar() {
        io.clearScreen();
        List<Car> cars = getIncompleteCars();

        if (cars.isEmpty()) {
            io.showError("Нет доступных болидов для сборки!");
            io.showMessage("Сначала купите компоненты и создайте болид.");
            io.waitForEnter();
            return;
        }

        io.showMessage("\n=== СБОРКА БОЛИДА ===");
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
                // "Не полностью рабочие" болиды: отсутствуют компоненты или есть разрушенные детали
                .filter(c -> !c.isComplete() || c.hasBrokenComponents())
                .toList();
    }

    private Car selectCar(List<Car> cars) {
        io.showMessage("Доступные болиды:");
        for (int i = 0; i < cars.size(); i++) {
            io.showMessage((i + 1) + ". " + cars.get(i).getName());
        }

        int carChoice = io.getUserIntInput("\nВыберите болид: ", 1, cars.size()) - 1;
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
            io.showSuccess("✅ Болид успешно собран!");
            io.showMessage("Новая производительность: " +
                    String.format("%.2f", car.calculatePerformance()));
        } else {
            io.showError("❌ Не удалось собрать болид!");
        }

        io.waitForEnter();
    }

    private void removeUsedComponents(CarAssembly assembly) {
        if (assembly.engine != null) gameService.getComponentRepository().delete(assembly.engine.getId());
        if (assembly.transmission != null) gameService.getComponentRepository().delete(assembly.transmission.getId());
        if (assembly.suspension != null) gameService.getComponentRepository().delete(assembly.suspension.getId());
        if (assembly.aerodynamics != null) gameService.getComponentRepository().delete(assembly.aerodynamics.getId());
        if (assembly.tyres != null) gameService.getComponentRepository().delete(assembly.tyres.getId());
    }

    public void viewCars() {
        io.clearScreen();
        List<Car> cars = gameService.getPlayerCars();

        io.showMessage("\n=== ВАШИ БОЛИДЫ ===");

        if (cars.isEmpty()) {
            io.showMessage("У вас пока нет болидов.");
            io.showMessage("Купите компоненты и создайте первый болид!");
        } else {
            displayCars(cars);
            offerRepair(cars);
        }

        io.waitForEnter();
    }

    private void displayCars(List<Car> cars) {
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            io.showMessage("\n" + (i + 1) + ". " + car.getName());

            if (car.isComplete()) {
                displayCompleteCar(car);
            } else {
                io.showMessage("   ❌ Статус: Не собран");
                io.showMessage("   🔧 Требуется сборка");
            }
        }
    }

    private void displayCompleteCar(Car car) {
        io.showMessage("   ✅ Статус: Собран");
        io.showMessage("   📊 Производительность: " +
                String.format("%.2f", car.calculatePerformance()));
        io.showMessage("   🔧 Износ: " + String.format("%.1f%%", car.getWearPercentage()));

        if (car.hasBrokenComponents()) {
            io.showWarning("   ⚠️ Разрушенные компоненты! Ремонт невозможен — только замена.");
            if (car.getEngine() != null && car.getEngine().isBroken()) {
                io.showMessage("     • Двигатель: сломан");
            }
            if (car.getTransmission() != null && car.getTransmission().isBroken()) {
                io.showMessage("     • Трансмиссия: сломана");
            }
            if (car.getSuspension() != null && car.getSuspension().isBroken()) {
                io.showMessage("     • Подвеска: сломана");
            }
            if (car.getAerodynamics() != null && car.getAerodynamics().isBroken()) {
                io.showMessage("     • Аэродинамика: сломана");
            }
            if (car.getTyres() != null && car.getTyres().isBroken()) {
                io.showMessage("     • Шины: сломаны");
            }
            io.showMessage("   Купите нужные компоненты в разделе 2 и замените их в разделе 3.");
        } else if (car.getWearPercentage() > 50) {
            io.showWarning("   ⚠️ Высокий износ (>50%). Вероятность инцидента выше.");
        }

        io.showMessage("\n   Компоненты:");
        if (car.getEngine() != null)
            io.showMessage("     • Двигатель: " + car.getEngine().getName());
        if (car.getTransmission() != null)
            io.showMessage("     • Трансмиссия: " + car.getTransmission().getName());
        if (car.getSuspension() != null)
            io.showMessage("     • Подвеска: " + car.getSuspension().getName());
        if (car.getAerodynamics() != null)
            io.showMessage("     • Аэродинамика: " + car.getAerodynamics().getName());
        if (car.getTyres() != null)
            io.showMessage("     • Шины: " + car.getTyres().getName());
    }

    private void offerRepair(List<Car> cars) {
        boolean needsRepair = cars.stream()
                .anyMatch(c -> c.isComplete()
                        && !c.hasBrokenComponents()
                        && c.getWearPercentage() > 50);

        if (needsRepair) {
            if (gameService.getPlayerEngineers().isEmpty()) {
                io.showError("Для ремонта нужен инженер. Найдите его в разделе 4.");
                io.waitForEnter();
                return;
            }
            boolean repair = io.getUserConfirmation("\nХотите отремонтировать болиды?");
            if (repair) {
                repairCars();
            }
        }
    }

    public void repairCars() {
        Manager player = gameService.getPlayerManager();
        List<Car> cars = gameService.getPlayerCars().stream()
                .filter(c -> c.isComplete() && !c.hasBrokenComponents())
                .toList();

        if (cars.isEmpty()) {
            io.showMessage("Нет болидов для ремонта.");
            return;
        }

        io.showMessage("\n=== РЕМОНТ БОЛИДОВ ===");

        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            double repairCost = car.getWearPercentage() * 100;
            io.showMessage(String.format("%d. %s [Износ: %.1f%%, Стоимость ремонта: $%,.0f]",
                    i + 1, car.getName(), car.getWearPercentage(), repairCost));
        }

        int choice = io.getUserIntInput("\nВыберите болид для ремонта (0 - отмена): ", 0, cars.size());
        if (choice == 0) return;

        processRepair(cars.get(choice - 1), player);
    }

    private void processRepair(Car car, Manager player) {
        if (gameService.getPlayerEngineers().isEmpty()) {
            io.showError("Для ремонта нужен инженер. Найдите его в разделе 4.");
            io.waitForEnter();
            return;
        }

        double repairCost = car.getWearPercentage() * 100;

        io.showMessage("Стоимость ремонта: $" + String.format("%,.0f", repairCost));
        io.showMessage("Ваш бюджет: $" + String.format("%,.0f", player.getBudget()));

        if (repairCost > player.getBudget()) {
            io.showError("Недостаточно средств!");
            return;
        }

        if (!io.getUserConfirmation("Подтвердить ремонт?")) return;

        boolean success = gameService.getShopService().repairCar(car.getId());

        if (success) {
            io.showSuccess("Болид отремонтирован!");
            io.showMessage("Новый износ: 0%");
        } else {
            io.showError("Не удалось отремонтировать болид");
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
            // Если компонент уже стоит и он не разрушен — оставляем
            if (engine != null && !engine.isBroken()) return true;

            List<Engine> engines = gameService.getComponentRepository().findByType(Engine.class);
            if (engines.isEmpty()) {
                io.showError("Нет купленных двигателей. Для замены сломанного двигателя купите новый в разделе 2.");
                io.waitForEnter();
                return false;
            }

            io.showMessage("\nДоступные двигатели (купленные):");
            for (int i = 0; i < engines.size(); i++) {
                Engine eng = engines.get(i);
                io.showMessage(String.format("%d. %s [Мощность: %.1f, Тип: %s, Цена: $%,.0f]",
                        i + 1, eng.getName(), eng.getBasePower(), eng.getEngineType(), eng.getPrice()));
            }

            int choice = io.getUserIntInput("Выберите двигатель: ", 1, engines.size()) - 1;
            engine = engines.get(choice);
            return true;
        }

        private boolean selectTransmission() {
            if (transmission != null && !transmission.isBroken()) return true;

            List<Transmission> transmissions = gameService.getComponentRepository().findByType(Transmission.class);
            if (transmissions.isEmpty()) {
                io.showError("Нет купленных трансмиссий. Купите новую трансмиссию в разделе 2 для замены сломанной.");
                io.waitForEnter();
                return false;
            }

            List<Transmission> compatible = transmissions.stream()
                    .filter(t -> t.isCompatibleWith(engine))
                    .toList();

            if (compatible.isEmpty()) {
                io.showError("Нет купленных совместимых трансмиссий для выбранного двигателя. Купите подходящую в разделе 2.");
                io.waitForEnter();
                return false;
            }

            io.showMessage("\nДоступные трансмиссии (совместимые):");
            for (int i = 0; i < compatible.size(); i++) {
                Transmission trans = compatible.get(i);
                io.showMessage(String.format("%d. %s [Эффективность: %.2f, Цена: $%,.0f]",
                        i + 1, trans.getName(), trans.getBaseEfficiency(), trans.getPrice()));
            }

            int choice = io.getUserIntInput("Выберите трансмиссию: ", 1, compatible.size()) - 1;
            transmission = compatible.get(choice);
            return true;
        }

        private boolean selectSuspension() {
            if (suspension != null && !suspension.isBroken()) return true;

            List<Suspension> suspensions = gameService.getComponentRepository().findByType(Suspension.class);
            if (suspensions.isEmpty()) {
                io.showError("Нет купленных подвесок. Купите новую подвеску в разделе 2 для замены сломанной.");
                io.waitForEnter();
                return false;
            }

            List<Suspension> compatible = suspensions.stream()
                    .filter(s -> s.canSupportWeight(engine.getWeight()))
                    .toList();

            if (compatible.isEmpty()) {
                io.showError("Нет купленных совместимых подвесок, которые выдержат вес выбранного двигателя. Купите подходящую в разделе 2.");
                io.waitForEnter();
                return false;
            }

            io.showMessage("\nДоступные подвески (совместимые):");
            for (int i = 0; i < compatible.size(); i++) {
                Suspension susp = compatible.get(i);
                io.showMessage(String.format("%d. %s [Стабильность: %.1f, Макс.вес: %d кг, Цена: $%,.0f]",
                        i + 1, susp.getName(), susp.getBaseStability(), susp.getMaxWeight(), susp.getPrice()));
            }

            int choice = io.getUserIntInput("Выберите подвеску: ", 1, compatible.size()) - 1;
            suspension = compatible.get(choice);
            return true;
        }

        private boolean selectAerodynamics() {
            if (aerodynamics != null && !aerodynamics.isBroken()) return true;

            List<Aerodynamics> aeroList = gameService.getComponentRepository().findByType(Aerodynamics.class);
            if (aeroList.isEmpty()) {
                io.showError("Нет купленных аэродинамических пакетов. Купите новый в разделе 2 для замены сломанного.");
                io.waitForEnter();
                return false;
            }

            io.showMessage("\nДоступная аэродинамика (купленная):");
            for (int i = 0; i < aeroList.size(); i++) {
                Aerodynamics aero = aeroList.get(i);
                io.showMessage(String.format("%d. %s [Прижимная сила: %.1f, Цена: $%,.0f]",
                        i + 1, aero.getName(), aero.getBaseDownforce(), aero.getPrice()));
            }

            int choice = io.getUserIntInput("Выберите аэродинамику: ", 1, aeroList.size()) - 1;
            aerodynamics = aeroList.get(choice);
            return true;
        }

        private boolean selectTyres() {
            if (tyres != null && !tyres.isBroken()) return true;

            List<Tyres> tyresList = gameService.getComponentRepository().findByType(Tyres.class);
            if (tyresList.isEmpty()) {
                io.showError("Нет купленных шин. Купите новый комплект в разделе 2 для замены сломанного.");
                io.waitForEnter();
                return false;
            }

            io.showMessage("\nДоступные шины (купленные):");
            for (int i = 0; i < tyresList.size(); i++) {
                Tyres t = tyresList.get(i);
                io.showMessage(String.format("%d. %s [Сцепление: %.1f, Состав: %s, Цена: $%,.0f]",
                        i + 1, t.getName(), t.getBaseGrip(), t.getCompound(), t.getPrice()));
            }

            int choice = io.getUserIntInput("Выберите шины: ", 1, tyresList.size()) - 1;
            tyres = tyresList.get(choice);
            return true;
        }

        boolean confirmAndAssemble() {
            io.showMessage("\n📋 Итоговая конфигурация:");
            io.showMessage("  Двигатель: " + engine.getName());
            io.showMessage("  Трансмиссия: " + transmission.getName());
            io.showMessage("  Подвеска: " + suspension.getName());
            io.showMessage("  Аэродинамика: " + aerodynamics.getName());
            io.showMessage("  Шины: " + tyres.getName());

            double estimatedPerf = engine.getBasePower() * 0.3 +
                    transmission.getBaseEfficiency() * 0.2 +
                    suspension.getBaseStability() * 0.2 +
                    aerodynamics.getBaseDownforce() * 0.2 +
                    tyres.getBaseGrip() * 0.1;

            io.showMessage("  Примерная производительность: " + String.format("%.2f", estimatedPerf));

            return io.getUserConfirmation("\nПодтвердить сборку?");
        }
    }
}