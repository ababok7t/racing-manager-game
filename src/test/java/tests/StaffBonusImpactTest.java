package test.java.tests;

import model.Car;
import model.race.Track;
import model.race.Weather;
import model.staff.Engineer;
import model.staff.Pilot;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.RaceService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StaffBonusImpactTest {

    @Test
    void engineerBonusImprovesLapTimeWithSameSeed() {
        GameService gameService = new GameService();
        RaceService raceService = gameService.getRaceService();
        Car car = TestFixtures.completeCar("EngCar", 0);
        Pilot pilot = TestFixtures.pilot("Pilot", 27, 85);
        Engineer engineer = TestFixtures.engineer("E", 8, 0.95);
        Track track = gameService.getAllTracks().get(0);

        gameService.getRandom().setSeed(100);
        double withoutEngineer = raceService.simulateLap(car, pilot, track, Weather.SUNNY.getName(), List.of());
        gameService.getRandom().setSeed(100);
        double withEngineer = raceService.simulateLap(car, pilot, track, Weather.SUNNY.getName(), List.of(engineer));

        assertTrue(withEngineer < withoutEngineer);
    }
}

