package test.java.tests;

import model.Car;
import model.race.Race;
import model.race.Track;
import model.staff.Engineer;
import model.staff.Pilot;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.RaceService;

import static org.junit.jupiter.api.Assertions.*;

class RaceFlowTest {

    @Test
    void raceCompletesAndAssignsPlayerPosition() {
        GameService gameService = new GameService();
        RaceService raceService = gameService.getRaceService();
        Car car = TestFixtures.completeCar("RaceCar", 0);
        Pilot pilot = TestFixtures.pilot("Pilot", 28, 88);
        Engineer engineer = TestFixtures.engineer("Engineer", 5, 0.9);

        TestFixtures.attachCarToPlayer(gameService, car);
        TestFixtures.hirePilotToPlayer(gameService, pilot);
        TestFixtures.hireEngineerToPlayer(gameService, engineer);

        Track track = gameService.getAllTracks().get(0);
        Race race = raceService.createRace(track);
        race.addParticipant(gameService.getPlayerManager().getId(), car.getId(), pilot.getId());

        raceService.simulateRace(race);

        assertTrue(race.isCompleted());
        assertTrue(race.getPosition(gameService.getPlayerManager().getId()) > 0);
    }
}

