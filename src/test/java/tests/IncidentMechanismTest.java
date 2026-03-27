package test.java.tests;

import model.Car;
import model.race.Track;
import model.race.Weather;
import model.staff.Pilot;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.RaceService;

import static org.junit.jupiter.api.Assertions.*;

class IncidentMechanismTest {

    @Test
    void checkIncidentReturnsEmptyForIncompleteCar() {
        GameService gameService = new GameService();
        RaceService raceService = gameService.getRaceService();
        Car incomplete = new Car("Incomplete");
        Pilot pilot = TestFixtures.pilot("P", 30, 80);
        Track track = gameService.getAllTracks().get(0);

        assertTrue(raceService.checkIncident(incomplete, pilot, track, Weather.SUNNY.getName()).isEmpty());
    }

    @Test
    void checkIncidentReturnsEmptyForWearBelowThreshold() {
        GameService gameService = new GameService();
        RaceService raceService = gameService.getRaceService();
        Car car = TestFixtures.completeCar("LowWear", 40);
        Pilot pilot = TestFixtures.pilot("P", 30, 80);
        Track track = gameService.getAllTracks().get(0);

        assertTrue(raceService.checkIncident(car, pilot, track, Weather.SUNNY.getName()).isEmpty());
    }

    @Test
    void checkIncidentDoesNotMutateCarState() {
        GameService gameService = new GameService();
        RaceService raceService = gameService.getRaceService();
        Car car = TestFixtures.completeCar("HighWear", 80);
        Pilot pilot = TestFixtures.pilot("P", 26, 95);
        Track track = gameService.getAllTracks().get(0);

        double wearBefore = car.getWearPercentage();
        double engineWearBefore = car.getEngine().getWear();
        raceService.checkIncident(car, pilot, track, Weather.SUNNY.getName());

        assertEquals(wearBefore, car.getWearPercentage(), 0.0001);
        assertEquals(engineWearBefore, car.getEngine().getWear(), 0.0001);
    }
}

