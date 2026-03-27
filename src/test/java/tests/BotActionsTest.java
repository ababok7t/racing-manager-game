package test.java.tests;

import model.Car;
import model.Manager;
import model.race.Race;
import model.staff.Engineer;
import model.staff.Pilot;
import org.junit.jupiter.api.Test;
import service.GameService;

import static org.junit.jupiter.api.Assertions.*;

class BotActionsTest {

    @Test
    void opponentsAreUpdatedAfterRaceProcessing() {
        GameService gameService = new GameService();
        Manager player = gameService.getPlayerManager();
        Car playerCar = TestFixtures.completeCar("PlayerCar", 0);
        Pilot pilot = TestFixtures.pilot("Pilot", 27, 88);
        Engineer engineer = TestFixtures.engineer("Engineer", 5, 0.85);

        TestFixtures.attachCarToPlayer(gameService, playerCar);
        TestFixtures.hirePilotToPlayer(gameService, pilot);
        TestFixtures.hireEngineerToPlayer(gameService, engineer);

        Manager opponent = gameService.getBotService().getOpponentManagers().get(0);
        Car opponentCar = gameService.getCarRepository().findAllById(opponent.getCarIds()).get(0);
        Car preparedOpponentCar = TestFixtures.completeCar("OpponentReady", 60);
        opponentCar.setEngine(preparedOpponentCar.getEngine());
        opponentCar.setTransmission(preparedOpponentCar.getTransmission());
        opponentCar.setSuspension(preparedOpponentCar.getSuspension());
        opponentCar.setAerodynamics(preparedOpponentCar.getAerodynamics());
        opponentCar.setTyres(preparedOpponentCar.getTyres());
        opponentCar.setBuilt(true);
        gameService.getCarRepository().save(opponentCar);

        Race race = gameService.getRaceService().createRace(gameService.getAllTracks().get(0));
        race.addParticipant(player.getId(), playerCar.getId(), pilot.getId());
        gameService.getRaceService().simulateRace(race);

        assertTrue(opponentCar.getWearPercentage() <= 50);
    }
}

