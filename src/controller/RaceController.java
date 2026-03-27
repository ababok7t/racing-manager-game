package controller;

import model.*;
import model.race.*;
import model.staff.*;
import service.GameService;
import view.ConsoleIO;
import view.RaceView;
import java.util.*;

public class RaceController {
    private final GameService gameService;
    private final ConsoleIO io;
    private final RaceView raceView;

    public RaceController(GameService gameService, ConsoleIO io, RaceView raceView) {
        this.gameService = gameService;
        this.io = io;
        this.raceView = raceView;
    }

    public void startRace() {
        io.clearScreen();
        io.showMessage("\nПОДГОТОВКА К ГОНКЕ");

        if (!gameService.canParticipate()) {
            showCantParticipateMessage();
            return;
        }

        Track selectedTrack = selectTrack();
        if (selectedTrack == null) return;

        Car selectedCar = selectCar();
        if (selectedCar == null) return;
        if (selectedCar.getWearPercentage() > 50) {
            io.showWarning("Высокий износ болида (>50%). Возможны инциденты.");
            boolean proceed = io.getUserConfirmation("Продолжить гонку?");
            if (!proceed) {
                io.showMessage("Гонка отменена.");
                io.showMessage("Рекомендуется: 1) раздел 3 — «Собрать болид» (замена),");
                io.showMessage("или 2) раздел 6 — «Просмотреть болиды» (ремонт при отсутствии разрушенных компонентов).");
                io.waitForEnter();
                return;
            }
        }

        Pilot selectedPilot = selectPilot();
        if (selectedPilot == null) return;

        Race race = createRace(selectedTrack, selectedCar, selectedPilot);
        if (race == null) return;

        simulateAndShowResults(race);
    }

    private void showCantParticipateMessage() {
        io.showError("Невозможно начать гонку! Необходимо:");
        io.showMessage("  - Собранный болид без разрушенных компонентов");
        io.showMessage("  - Пилот в команде");
        io.showMessage("  - Инженер в команде");
        io.waitForEnter();
    }

    private Track selectTrack() {
        List<Track> tracks = gameService.getAllTracks();
        raceView.showTracks(tracks);
        int trackChoice = io.getUserIntInput("Выберите трассу: ", 1, tracks.size()) - 1;
        return tracks.get(trackChoice);
    }

    private Car selectCar() {
        List<Car> cars = gameService.getUsableCars();
        if (cars.isEmpty()) {
            io.showError("Нет готовых к гонке болидов!");
            io.waitForEnter();
            return null;
        }

        raceView.showCars(cars);
        int carChoice = io.getUserIntInput("Выберите болид: ", 1, cars.size()) - 1;
        return cars.get(carChoice);
    }

    private Pilot selectPilot() {
        List<Pilot> pilots = gameService.getPlayerPilots();
        if (pilots.isEmpty()) {
            io.showError("Нет пилотов в команде!");
            io.waitForEnter();
            return null;
        }

        raceView.showPilots(pilots);
        int pilotChoice = io.getUserIntInput("Выберите пилота: ", 1, pilots.size()) - 1;
        return pilots.get(pilotChoice);
    }

    private Race createRace(Track track, Car car, Pilot pilot) {
        Race race = gameService.getRaceService().createRace(track);
        race.addParticipant(gameService.getPlayerManager().getId(), car.getId(), pilot.getId());

        int opponentsAdded = addOpponents(race);

        showRaceInfo(race, car, pilot, opponentsAdded);

        boolean confirm = io.getUserConfirmation("\nНачать гонку?");
        if (!confirm) {
            io.showMessage("Гонка отменена.");
            io.waitForEnter();
            return null;
        }

        return race;
    }

    private int addOpponents(Race race) {
        List<Manager> opponents = gameService.getBotService().getOpponentManagers();
        int opponentsAdded = 0;
        for (Manager opponent : opponents) {
            Map.Entry<String, String> lineup = gameService.getBotService().getRandomLineup(opponent.getId());
            if (lineup != null) {
                race.addParticipant(opponent.getId(), lineup.getKey(), lineup.getValue());
                opponentsAdded++;
            }
        }
        return opponentsAdded;
    }

    private void showRaceInfo(Race race, Car car, Pilot pilot, int opponentsAdded) {
        Track track = race.getTrack();
        io.showMessage("\nИнформация о гонке:");
        io.showMessage("  Трасса: " + track.getName() + " (" + track.getCountry() + ")");
        io.showMessage("  Круги: " + track.getLaps());
        io.showMessage("  Погода: " + race.getWeather());
        io.showMessage("  Участников: " + (opponentsAdded + 1) + " (вы + " + opponentsAdded + " соперников)");
        io.showMessage("  Ваш болид: " + car.getName());
        io.showMessage("  Ваш пилот: " + pilot.getName());
    }

    private void simulateAndShowResults(Race race) {
        io.showMessage("\nГОНКА НАЧИНАЕТСЯ!\n");
        gameService.getRaceService().simulateRace(race);
        Map<String, String> teamNames = new HashMap<>();
        Map<String, String> pilotNamesByManagerId = new HashMap<>();

        for (String managerId : race.getParticipantManagerIds()) {
            String teamName = gameService.getManagerRepository()
                    .findById(managerId)
                    .map(Manager::getName)
                    .orElse(managerId);
            teamNames.put(managerId, teamName);

            String pilotId = race.getPilotId(managerId);
            if (pilotId != null) {
                String pilotName = gameService.getPilotRepository()
                        .findById(pilotId)
                        .map(Pilot::getName)
                        .orElse("");
                pilotNamesByManagerId.put(managerId, pilotName);
            }
        }

        raceView.showRaceResults(race, teamNames, pilotNamesByManagerId);

        showPlayerResults(race);
        checkIncidents(race);

        io.waitForEnter();
    }

    private void showPlayerResults(Race race) {
        int position = race.getPosition(gameService.getPlayerManager().getId());
        double prizeMoney = race.getPrizeMoney(position);
        int points = race.getPoints(position);

        io.showMessage("\nРЕЗУЛЬТАТЫ ВАШЕЙ КОМАНДЫ:");
        io.showMessage("   Место: " + position);
        io.showMessage("   Призовые: $" + prizeMoney);
        io.showMessage("   Очки: " + points);
        io.showMessage("   Общий бюджет: $" + gameService.getPlayerManager().getBudget());
        io.showMessage("   Всего очков: " + gameService.getPlayerManager().getChampionshipPoints());
    }

    private void checkIncidents(Race race) {
        Incident inc = race.getIncidents().get(gameService.getPlayerManager().getId());
        if (inc == null) return;

        io.showWarning("\nИнцидент у вашей команды: " + inc.getType());
        if (inc.isFatal()) {
            io.showMessage("   Компонент разрушен. Рекомендуется заменить разрушенные компоненты перед следующей гонкой (раздел 3).");
        } else {
            io.showMessage("   Это не фатально, но может ухудшить время/состояние болида.");
        }
    }
}