package service;

import model.Car;
import model.components.Engine;
import model.components.Suspension;
import model.components.Transmission;

import java.util.ArrayList;
import java.util.List;

public class GarageService {
    private final GameService gameService;

    public GarageService(GameService gameService) {
        this.gameService = gameService;
    }

    public List<Car> getCarsNeedingAssemblyOrReplacement() {
        return gameService.getPlayerCars().stream()
                .filter(car -> !car.isComplete() || car.hasBrokenComponents())
                .toList();
    }

    public List<Car> getCarsEligibleForRepair() {
        return gameService.getPlayerCars().stream()
                .filter(car -> car.isComplete() && !car.hasBrokenComponents())
                .toList();
    }

    public List<Transmission> getCompatibleTransmissions(Engine engine) {
        return gameService.getComponentRepository().findByType(Transmission.class).stream()
                .filter(transmission -> transmission.isCompatibleWith(engine))
                .toList();
    }

    public List<Suspension> getCompatibleSuspensions(Engine engine) {
        return gameService.getComponentRepository().findByType(Suspension.class).stream()
                .filter(suspension -> suspension.canSupportWeight(engine.getWeight()))
                .toList();
    }

    public List<String> getBrokenComponentLabels(Car car) {
        List<String> broken = new ArrayList<>();
        if (car.getEngine() != null && car.getEngine().isBroken()) broken.add("Двигатель");
        if (car.getTransmission() != null && car.getTransmission().isBroken()) broken.add("Трансмиссия");
        if (car.getSuspension() != null && car.getSuspension().isBroken()) broken.add("Подвеска");
        if (car.getAerodynamics() != null && car.getAerodynamics().isBroken()) broken.add("Аэродинамика");
        if (car.getTyres() != null && car.getTyres().isBroken()) broken.add("Шины");
        return broken;
    }
}

