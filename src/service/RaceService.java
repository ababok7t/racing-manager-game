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
                    .orElse(1.0) + 1.0; //если пусто то 1
        }

        double baseLapTime = 90.0;
        double lapTime = baseLapTime / (carPerformance * pilotPerformance * engineerBonus);

        List<TrackUnit> units = track.getTrackUnits();
        double avgSpeedMod = units.stream()
                .mapToDouble(u -> u.getType().getSpeedModificator())
                .average()
                .orElse(1.0);
        double avgControlMod = units.stream()
                .mapToDouble(u -> u.getType().getControlModificator())
                .average()
                .orElse(1.0);

        if (avgSpeedMod > 0) {
            lapTime *= 1.0 / avgSpeedMod;
        }

        double consistency = pilot.getConsistency();
        double controlPenalty = 1.0 + Math.max(0.0, avgControlMod - 1.0) * (1.0 - consistency);
        lapTime *= controlPenalty;

        lapTime *= (0.97 + random.nextDouble() * 0.06);

        return lapTime;
    }

    public Optional<Incident> checkIncident(Car car, Pilot pilot, Track track, String weather) {
        if (!car.isComplete()) return Optional.empty();

        double wear01 = car.getWearPercentage() / 100.0;
        if (wear01 < 0.50) return Optional.empty();

        double wearFactor = Math.min(1.0, (wear01 - 0.50) / 0.50);
        double difficulty = (track == null) ? 0.6 : Math.max(0.0, track.getDifficulty());
        double weatherRisk = 1.0 + (1.0 - weatherSpeedModifier(weather)) * 0.8;
        double incidentProb = wearFactor
                * (0.03 + pilot.getAggression() * 0.12)
                * (1.10 - pilot.getConsistency() * 0.30)
                * (1.0 + difficulty * 0.35)
                * weatherRisk;
        if (random.nextDouble() >= incidentProb) return Optional.empty();

        double spinW = 6.0 + (1.0 - pilot.getConsistency()) * 6.0;
        double aeroW = 1.0 + (car.getAerodynamics().getWear() / 100.0) * 6.0 * wearFactor;
        double collisionW = (0.8 + pilot.getAggression() * 5.0 * wearFactor + (car.getTyres().getWear() / 100.0) * 2.5)
                * (1.0 + difficulty * 0.25)
                * (1.0 + (weatherRisk - 1.0) * 0.6);

        double engineReliabilityFactor = engineFailureFactor(car.getEngine());
        double engineW = (0.7 + (car.getEngine().getWear() / 100.0) * 7.0 * wearFactor) * engineReliabilityFactor;

        double breakW = 0.9
                + (car.getSuspension().getWear() / 100.0) * 3.5 * wearFactor
                + (car.getTransmission().getWear() / 100.0) * 3.5 * wearFactor;

        double r = random.nextDouble() * (spinW + aeroW + collisionW + engineW + breakW);
        Incident incident;
        if ((r -= spinW) < 0) incident = Incident.SPIN;
        else if ((r -= aeroW) < 0) incident = Incident.AERO_DAMAGE;
        else if ((r -= collisionW) < 0) incident = Incident.COLLISION;
        else if ((r -= engineW) < 0) incident = Incident.ENGINE_ERROR;
        else incident = Incident.BREAK_ERROR;

        return Optional.of(incident);
    }

    private double weatherSpeedModifier(String weather) {
        if (weather == null) return 1.0;
        for (Weather w : Weather.values()) {
            if (w.getName().equalsIgnoreCase(weather) || w.name().equalsIgnoreCase(weather)) {
                return w.getSpeedModificator();
            }
        }
        return 1.0;
    }

    private double engineFailureFactor(Engine engine) {
        if (engine == null) return 1.0;
        double rel = Math.max(50.0, Math.min(200.0, engine.getReliability()));
        return 150.0 / rel;
    }

    private void applyIncidentConsequences(Car car, Incident incident) {
        if (incident == Incident.SPIN) return;

        switch (incident) {
            case AERO_DAMAGE -> car.getAerodynamics().addWear(35);
            case COLLISION -> car.getTyres().setWear(100);
            case ENGINE_ERROR -> car.getEngine().setWear(100);
            case BREAK_ERROR -> {
                if (random.nextBoolean()) car.getSuspension().setWear(100);
                else car.getTransmission().setWear(100);
            }
            default -> {
            }
        }
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
            double totalTime = simulateParticipantRace(race, managerId);
            if (totalTime < Double.MAX_VALUE) {
                totalTimes.put(managerId, totalTime);
            }
        }

        setPositionsAndResults(race, totalTimes);

        race.complete();
        gameService.getRaceRepository().save(race);
        gameService.processEndOfRace(race);
    }

    private double simulateParticipantRace(Race race, String managerId) {
        String carId = race.getCarId(managerId);
        String pilotId = race.getPilotId(managerId);

        Optional<Car> carOpt = gameService.getCarRepository().findById(carId);
        Optional<Pilot> pilotOpt = gameService.getPilotRepository().findById(pilotId);
        Optional<Manager> managerOpt = gameService.getManagerRepository().findById(managerId);

        if (carOpt.isEmpty() || pilotOpt.isEmpty() || managerOpt.isEmpty()) return Double.MAX_VALUE;

        List<Engineer> engineers = gameService.getEngineerRepository()
                .findAllById(managerOpt.get().getEngineerIds());

        return simulateLapsAndMaybeIncidents(
                race,
                managerId,
                carOpt.get(),
                pilotOpt.get(),
                engineers
        );
    }

    private double simulateLapsAndMaybeIncidents(
            Race race,
            String managerId,
            Car car,
            Pilot pilot,
            List<Engineer> engineers
    ) {
        double totalTime = 0;
        boolean hasIncident = false;

        int laps = race.getTrack().getLaps();
        for (int lap = 0; lap < laps; lap++) {
            double lapTime = simulateLap(car, pilot, race.getTrack(), race.getWeather(), engineers);

            if (lapTime == Double.MAX_VALUE) {
                return Double.MAX_VALUE;
            }

            totalTime += lapTime;

            if (lap % 10 == 0 && lap > 0 && !hasIncident) {
                Optional<Incident> incident = checkIncident(car, pilot, race.getTrack(), race.getWeather());
                if (incident.isPresent()) {
                    hasIncident = true;
                    Incident inc = incident.get();
                    race.addIncident(managerId, inc);

                    totalTime += inc.getTime();
                    applyIncidentConsequences(car, inc);
                    if (inc.isFatal()) return Double.MAX_VALUE;
                }
            }
        }

        applyWear(car, pilot, laps);
        return totalTime;
    }

    private void setPositionsAndResults(Race race, Map<String, Double> totalTimes) {
        List<Map.Entry<String, Double>> sorted = totalTimes.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .toList();

        int position = 1;
        for (Map.Entry<String, Double> entry : sorted) {
            race.setResult(entry.getKey(), entry.getValue());
            race.setPosition(entry.getKey(), position);
            position++;
        }
    }
}