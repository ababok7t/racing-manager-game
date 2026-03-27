package test.java.tests;

import model.Car;
import model.race.Track;
import model.race.Weather;
import model.staff.Pilot;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.RaceService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PilotCharacteristicsImpactTest {

    @Test
    void higherSkillPilotProducesFasterLapWithSameSeed() {
        GameService gameService = new GameService();
        RaceService raceService = gameService.getRaceService();
        Car car = TestFixtures.completeCar("PilotCar", 0);
        Track track = gameService.getAllTracks().get(0);
        Pilot weaker = TestFixtures.pilot("Weak", 35, 55);
        Pilot stronger = TestFixtures.pilot("Strong", 24, 95);

        gameService.getRandom().setSeed(777);
        double weakTime = raceService.simulateLap(car, weaker, track, Weather.SUNNY.getName(), List.of());
        gameService.getRandom().setSeed(777);
        double strongTime = raceService.simulateLap(car, stronger, track, Weather.SUNNY.getName(), List.of());

        assertTrue(strongTime < weakTime);
    }
}

