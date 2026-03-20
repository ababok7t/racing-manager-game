package service;

import model.Contract;
import model.*;
import model.components.*;
import model.staff.*;
import java.util.*;

public class ShopService {
    private final GameService gameService;

    public ShopService(GameService gameService) {
        this.gameService = gameService;
    }

    public boolean buyComponent(Component component) {
        Manager player = gameService.getPlayerManager();

        if (player.spendBudget(component.getPrice())) {
            gameService.getMarketService().buyComponent(component);
            gameService.getComponentRepository().save(component);
            gameService.getManagerRepository().save(player);
            return true;
        }
        return false;
    }

    public boolean hirePilot(String pilotId) {
        Manager player = gameService.getPlayerManager();
        Optional<Pilot> pilotOpt = gameService.getPilotRepository().findById(pilotId);

        if (pilotOpt.isPresent()) {
            Pilot pilot = pilotOpt.get();
            if (player.spendBudget(pilot.getPrice())) {
                gameService.getMarketService().hirePilot(pilot);
                gameService.getPilotRepository().hire(pilotId, player.getId());
                player.addPilotId(pilotId);
                gameService.getManagerRepository().save(player);
                return true;
            }
        }
        return false;
    }

    public boolean hireEngineer(String engineerId) {
        Manager player = gameService.getPlayerManager();
        Optional<Engineer> engOpt = gameService.getEngineerRepository().findById(engineerId);

        if (engOpt.isPresent()) {
            Engineer engineer = engOpt.get();
            double yearlySalary = engineer.getSalary() * 12;

            if (player.spendBudget(yearlySalary)) {
                gameService.getMarketService().hireEngineer(engineer);
                gameService.getEngineerRepository().hire(engineerId, player.getId());
                player.addEngineerId(engineerId);
                gameService.getManagerRepository().save(player);
                return true;
            }
        }
        return false;
    }

    public Car createCar(String name) {
        Car car = new Car(name);
        car.setManagerId(gameService.getPlayerManager().getId());
        gameService.getCarRepository().save(car);
        gameService.getPlayerManager().addCarId(car.getId());
        return car;
    }

    public boolean assembleCar(String carId, Engine engine, Transmission transmission,
                               Suspension suspension, Aerodynamics aero, Tyres tyres) {
        Optional<Car> carOpt = gameService.getCarRepository().findById(carId);

        if (carOpt.isPresent()) {
            Car car = carOpt.get();

            if (!transmission.isCompatibleWith(engine)) {
                return false;
            }

            if (!suspension.canSupportWeight(engine.getWeight())) {
                return false;
            }

            car.setEngine(engine);
            car.setTransmission(transmission);
            car.setSuspension(suspension);
            car.setAerodynamics(aero);
            car.setTyres(tyres);
            car.setBuilt(true);

            gameService.getCarRepository().save(car);
            return true;
        }
        return false;
    }

    public boolean repairCar(String carId) {
        Optional<Car> carOpt = gameService.getCarRepository().findById(carId);
        Manager player = gameService.getPlayerManager();

        if (carOpt.isPresent()) {
            Car car = carOpt.get();
            // Разрушенные компоненты не подлежат восстановлению.
            if (car.hasBrokenComponents()) return false;
            double repairCost = car.getWearPercentage() * 100;

            if (player.spendBudget(repairCost)) {
                car.repair();
                gameService.getCarRepository().save(car);
                gameService.getManagerRepository().save(player);
                return true;
            }
        }
        return false;
    }

    public boolean signContract(Contract contract) {
        if (contract == null) return false;

        Manager player = gameService.getPlayerManager();

        // Требование по репутации.
        if (player.getReputation() < contract.getMinReputation()) {
            return false;
        }

        if (!player.spendBudget(contract.getPrice())) {
            return false;
        }

        // Маркируем контракт как недоступный на рынке.
        gameService.getMarketService().buyItem(contract.getId());

        // Сохраняем контракт у игрока.
        gameService.getContractRepository().save(contract);
        player.addContractId(contract.getId());
        gameService.getManagerRepository().save(player);
        return true;
    }
}