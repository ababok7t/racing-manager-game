package test.java.tests;

import model.Car;
import model.Manager;
import model.components.Aerodynamics;
import model.components.Engine;
import model.components.Suspension;
import model.components.Transmission;
import model.components.Tyres;
import model.staff.Engineer;
import model.staff.Pilot;
import service.GameService;

final class TestFixtures {
    private TestFixtures() {
    }

    static Car completeCar(String name, double wear) {
        Car car = new Car(name);
        car.setEngine(new Engine("E", 1000, 90, 70, "Atmospheric", 160));
        car.setTransmission(new Transmission("T", 1000, 0.9, 70, "Atmospheric"));
        car.setSuspension(new Suspension("S", 1000, 7, 180, 70));
        car.setAerodynamics(new Aerodynamics("A", 1000, 8, 70));
        car.setTyres(new Tyres("Soft", 1000, 9, "Soft", 1.3, 70));
        car.setBuilt(true);
        car.getEngine().setWear(wear);
        car.getTransmission().setWear(wear);
        car.getSuspension().setWear(wear);
        car.getAerodynamics().setWear(wear);
        car.getTyres().setWear(wear);
        return car;
    }

    static Pilot pilot(String name, int age, double skill) {
        return new Pilot(name, age, skill, 1000);
    }

    static Engineer engineer(String name, int level, double efficiency) {
        return new Engineer(name, "Mechanic", level, 5000, efficiency);
    }

    static void attachCarToPlayer(GameService gameService, Car car) {
        Manager player = gameService.getPlayerManager();
        car.setManagerId(player.getId());
        gameService.getCarRepository().save(car);
        player.addCarId(car.getId());
    }

    static void hirePilotToPlayer(GameService gameService, Pilot pilot) {
        Manager player = gameService.getPlayerManager();
        gameService.getPilotRepository().save(pilot);
        gameService.getPilotRepository().hire(pilot.getId(), player.getId());
        player.addPilotId(pilot.getId());
    }

    static void hireEngineerToPlayer(GameService gameService, Engineer engineer) {
        Manager player = gameService.getPlayerManager();
        gameService.getEngineerRepository().save(engineer);
        gameService.getEngineerRepository().hire(engineer.getId(), player.getId());
        player.addEngineerId(engineer.getId());
    }
}

