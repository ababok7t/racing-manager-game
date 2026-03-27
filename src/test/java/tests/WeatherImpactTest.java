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

class WeatherImpactTest {

    @Test
    void rainyWeatherProducesSlowerLapThanSunnyWithSameSeed() {
        GameService gameService = new GameService();
        RaceService raceService = gameService.getRaceService();
        Car car = TestFixtures.completeCar("WeatherCar", 0);
        Pilot pilot = TestFixtures.pilot("Pilot", 27, 85);
        Track track = gameService.getAllTracks().get(0);

        gameService.getRandom().setSeed(42);
        double sunny = raceService.simulateLap(car, pilot, track, Weather.SUNNY.getName(), List.of());
        gameService.getRandom().setSeed(42);
        double rainy = raceService.simulateLap(car, pilot, track, Weather.RAINY.getName(), List.of());

        assertTrue(rainy > sunny);
    }
}

