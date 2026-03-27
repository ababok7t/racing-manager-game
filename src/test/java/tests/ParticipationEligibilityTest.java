package test.java.tests;

import model.Car;
import model.staff.Engineer;
import model.staff.Pilot;
import org.junit.jupiter.api.Test;
import service.GameService;

import static org.junit.jupiter.api.Assertions.*;

class ParticipationEligibilityTest {

    @Test
    void canParticipateIsFalseWhenNoReadyLineupExists() {
        GameService gameService = new GameService();

        assertFalse(gameService.canParticipate());
    }

    @Test
    void canParticipateIsTrueWhenReadyCarPilotAndEngineerExist() {
        GameService gameService = new GameService();
        Car car = TestFixtures.completeCar("ReadyCar", 0);
        Pilot pilot = TestFixtures.pilot("Pilot", 25, 82);
        Engineer engineer = TestFixtures.engineer("Engineer", 4, 0.8);

        TestFixtures.attachCarToPlayer(gameService, car);
        TestFixtures.hirePilotToPlayer(gameService, pilot);
        TestFixtures.hireEngineerToPlayer(gameService, engineer);

        assertTrue(gameService.canParticipate());
    }

    @Test
    void canParticipateBecomesFalseWhenReadyCarGetsBrokenComponent() {
        GameService gameService = new GameService();
        Car car = TestFixtures.completeCar("ReadyCar", 0);
        Pilot pilot = TestFixtures.pilot("Pilot", 25, 82);
        Engineer engineer = TestFixtures.engineer("Engineer", 4, 0.8);

        TestFixtures.attachCarToPlayer(gameService, car);
        TestFixtures.hirePilotToPlayer(gameService, pilot);
        TestFixtures.hireEngineerToPlayer(gameService, engineer);
        car.getEngine().setWear(100);

        assertFalse(gameService.canParticipate());
    }
}

