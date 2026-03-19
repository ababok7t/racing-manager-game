package service;

import model.*;
import model.race.*;
import model.staff.*;
import model.components.*;
import java.util.*;

public class RaceService {
    private final GameService gameService;
    private final Random random;

    public RaceService(GameService gameService) {
        this.gameService = gameService;
        this.random = gameService.getRandom();
    }

    public double simulateLap(Car car, Pilot pilot, Track track, String weather, List<Engineer> engineers) {
        if (car == null || !car.isComplete() || pilot == null) {
            return Double.MAX_VALUE;
        }

        double carPerformance = car.calculatePerformance() / 100;
        double pilotPerformance = pilot.calculatePerformance(weather, track.getDifficulty());

        double engineerBonus = 1.0;
        if (!engineers.isEmpty()) {
            engineerBonus = engineers.stream()
                    .mapToDouble(Engineer::getBonus)
                    .average()
                    .orElse(1.0) + 1.0;
        }

        double baseLapTime = 90.0;
        double lapTime = baseLapTime / (carPerformance * pilotPerformance * engineerBonus);
        lapTime *= (0.97 + random.nextDouble() * 0.06);

        return lapTime;
    }

    public Optional<Component> checkIncident(Car car, Pilot pilot) {
        if (!car.isComplete()) return Optional.empty();

        double avgWear = car.getWearPercentage() / 100;
        double incidentProb = avgWear * pilot.getAggression() * 0.1;

        if (random.nextDouble() < incidentProb) {
            List<Component> components = new ArrayList<>();
            if (car.getEngine() != null) components.add(car.getEngine());
            if (car.getTransmission() != null) components.add(car.getTransmission());
            if (car.getSuspension() != null) components.add(car.getSuspension());
            if (car.getAerodynamics() != null) components.add(car.getAerodynamics());
            if (car.getTyres() != null) components.add(car.getTyres());

            if (!components.isEmpty()) {
                Component broken = components.get(random.nextInt(components.size()));
                return Optional.of(broken);
            }
        }

        return Optional.empty();
    }

    public void applyWear(Car car, Pilot pilot, int laps) {
        double wearPerLap = 0.5 * (1 + pilot.getAggression()) * (1 - pilot.getConsistency() / 2);
        car.increaseWear(wearPerLap * laps);
    }

    public Race createRace(Track track) {
        return new Race(track);
    }

    public void simulateRace(Race race) {
        Map<String, Double> totalTimes = new HashMap<>();

        for (String managerId : race.getParticipantManagerIds()) {
            String carId = race.getCarId(managerId);
            String pilotId = race.getPilotId(managerId);

            Optional<Car> carOpt = gameService.getCarRepository().findById(carId);
            Optional<Pilot> pilotOpt = gameService.getPilotRepository().findById(pilotId);
            Optional<Manager> managerOpt = gameService.getManagerRepository().findById(managerId);

            if (carOpt.isPresent() && pilotOpt.isPresent() && managerOpt.isPresent()) {
                List<Engineer> engineers = gameService.getEngineerRepository()
                        .findAllById(managerOpt.get().getEngineerIds());

                double totalTime = 0;
                boolean hasIncident = false;

                for (int lap = 0; lap < race.getTrack().getLaps(); lap++) {
                    double lapTime = simulateLap(carOpt.get(), pilotOpt.get(),
                            race.getTrack(), race.getWeather(), engineers);

                    if (lapTime == Double.MAX_VALUE) {
                        totalTime = Double.MAX_VALUE;
                        break;
                    }

                    totalTime += lapTime;

                    if (lap % 10 == 0 && lap > 0 && !hasIncident) {
                        Optional<Component> incident = checkIncident(carOpt.get(), pilotOpt.get());
                        if (incident.isPresent()) {
                            hasIncident = true;
                            race.addIncident(managerId,
                                    "Поломка " + incident.get().getName() + " на круге " + (lap + 1));
                        }
                    }
                }

                totalTimes.put(managerId, totalTime);
                applyWear(carOpt.get(), pilotOpt.get(), race.getTrack().getLaps());
            }
        }

        List<Map.Entry<String, Double>> sorted = totalTimes.entrySet().stream()
                .filter(e -> e.getValue() < Double.MAX_VALUE)
                .sorted(Map.Entry.comparingByValue())
                .toList();

        int position = 1;
        for (Map.Entry<String, Double> entry : sorted) {
            race.setResult(entry.getKey(), entry.getValue());
            race.setPosition(entry.getKey(), position);
            position++;
        }

        race.complete();
        gameService.getRaceRepository().save(race);
        gameService.processEndOfRace(race);
    }
}