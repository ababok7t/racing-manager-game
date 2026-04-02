package view;

import model.Car;
import model.race.ForceMajeurResult;
import model.race.Incident;
import model.race.Race;
import model.race.Track;
import model.staff.Pilot;

import java.util.List;
import java.util.Map;

public class RaceView {
    private final ConsoleIO io;

    public RaceView(ConsoleIO io) {
        this.io = io;
    }

    public void showTracks(List<Track> tracks) {
        io.showMessage("\nДоступные трассы:");
        for (int i = 0; i < tracks.size(); i++) {
            io.showMessage((i + 1) + ". " + tracks.get(i));
        }
    }

    public void showCars(List<Car> cars) {
        io.showMessage("\nДоступные болиды:");
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            io.showMessage((i + 1) + ". " + car.getName() +
                    " [Произв: " + String.format("%.2f", car.calculatePerformance()) +
                    ", Износ: " + String.format("%.1f%%", car.getWearPercentage()) +
                    ", Страховка: " + (car.isInsured() ? "да" : "нет") + "]");
        }
    }

    public void showPilots(List<Pilot> pilots) {
        io.showMessage("\nПилоты:");
        for (int i = 0; i < pilots.size(); i++) {
            Pilot p = pilots.get(i);
            io.showMessage((i + 1) + ". " + p.getName() +
                    " [Возраст: " + p.getAge() +
                    ", Навык: " + String.format("%.1f", p.getSkill()) +
                    ", Агрессия: " + String.format("%.0f", p.getAggression() * 100) + "%" +
                    ", Стабильность: " + String.format("%.0f", p.getConsistency() * 100) + "%]");
        }
    }

    public void showRaceResults(Race race, Map<String, String> teamNames, Map<String, String> pilotNamesByManagerId) {
        io.showMessage("\n" + "=".repeat(60));
        io.showMessage("РЕЗУЛЬТАТЫ ГОНКИ: " + race.getTrack().getName());
        io.showMessage("=".repeat(60));

        Map<String, Integer> positions = race.getAllPositions();
        List<Map.Entry<String, Integer>> sorted = positions.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .toList();

        for (Map.Entry<String, Integer> entry : sorted) {
            String managerId = entry.getKey();
            String teamName = teamNames.getOrDefault(managerId, managerId);
            String pilotName = pilotNamesByManagerId.get(managerId);

            if (pilotName == null || pilotName.isBlank()) {
                io.showMessage(entry.getValue() + ". " + teamName);
            } else {
                io.showMessage(entry.getValue() + ". " + teamName + " (" + pilotName + ")");
            }
        }

        Map<String, Incident> incidents = race.getIncidents();
        if (!incidents.isEmpty()) {
            io.showMessage("\nИНЦИДЕНТЫ:");
            for (Incident incident : incidents.values()) {
                io.showMessage("   - " + incident.getType());
            }
        }

        Map<String, ForceMajeurResult> forceMajeurs = race.getForceMajeurs();
        if (!forceMajeurs.isEmpty()) {
            io.showMessage("\nФОРС-МАЖОР:");
            for (ForceMajeurResult fm : forceMajeurs.values()) {
                io.showMessage("   - " + fm.getDescription()
                        + (fm.isCarSurvived() ? " (болид спасён)" : " (болид уничтожен)"));
            }
        }

        io.showMessage("=".repeat(60));
    }
}

