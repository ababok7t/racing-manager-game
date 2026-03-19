package controller;

import model.*;
import model.race.*;
import model.staff.*;
import service.GameService;
import view.ConsoleView;
import java.util.*;

public class RaceController {
    private final GameService gameService;
    private final ConsoleView view;

    public RaceController(GameService gameService, ConsoleView view) {
        this.gameService = gameService;
        this.view = view;
    }

    public void startRace() {
        view.clearScreen();
        view.showMessage("\n🏁 ПОДГОТОВКА К ГОНКЕ 🏁");

        if (!gameService.canParticipate()) {
            showCantParticipateMessage();
            return;
        }

        Track selectedTrack = selectTrack();
        if (selectedTrack == null) return;

        Car selectedCar = selectCar();
        if (selectedCar == null) return;

        Pilot selectedPilot = selectPilot();
        if (selectedPilot == null) return;

        Race race = createRace(selectedTrack, selectedCar, selectedPilot);
        if (race == null) return;

        simulateAndShowResults(race);
    }

    private void showCantParticipateMessage() {
        view.showError("Невозможно начать гонку! Необходимо:");
        view.showMessage("  • Собранный болид (износ менее 80%)");
        view.showMessage("  • Пилот в команде");
        view.showMessage("  • Инженер в команде");
        view.waitForEnter();
    }

    private Track selectTrack() {
        List<Track> tracks = gameService.getAllTracks();
        view.showTracks(tracks);
        int trackChoice = view.getUserIntInput("Выберите трассу: ", 1, tracks.size()) - 1;
        return tracks.get(trackChoice);
    }

    private Car selectCar() {
        List<Car> cars = gameService.getUsableCars();
        if (cars.isEmpty()) {
            view.showError("Нет готовых к гонке болидов!");
            view.waitForEnter();
            return null;
        }

        view.showCars(cars);
        int carChoice = view.getUserIntInput("Выберите болид: ", 1, cars.size()) - 1;
        return cars.get(carChoice);
    }

    private Pilot selectPilot() {
        List<Pilot> pilots = gameService.getPlayerPilots();
        if (pilots.isEmpty()) {
            view.showError("Нет пилотов в команде!");
            view.waitForEnter();
            return null;
        }

        view.showPilots(pilots);
        int pilotChoice = view.getUserIntInput("Выберите пилота: ", 1, pilots.size()) - 1;
        return pilots.get(pilotChoice);
    }

    private Race createRace(Track track, Car car, Pilot pilot) {
        Race race = gameService.getRaceService().createRace(track);
        race.addParticipant(gameService.getPlayerManager().getId(), car.getId(), pilot.getId());

        int opponentsAdded = addOpponents(race);

        showRaceInfo(race, car, pilot, opponentsAdded);

        boolean confirm = view.getUserConfirmation("\nНачать гонку?");
        if (!confirm) {
            view.showMessage("Гонка отменена.");
            view.waitForEnter();
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
        view.showMessage("\n📋 Информация о гонке:");
        view.showMessage("  Трасса: " + track.getName() + " (" + track.getCountry() + ")");
        view.showMessage("  Круги: " + track.getLaps());
        view.showMessage("  Погода: " + race.getWeather());
        view.showMessage("  Участников: " + (opponentsAdded + 1) + " (вы + " + opponentsAdded + " соперников)");
        view.showMessage("  Ваш болид: " + car.getName());
        view.showMessage("  Ваш пилот: " + pilot.getName());
    }

    private void simulateAndShowResults(Race race) {
        view.showMessage("\n🏁 ГОНКА НАЧИНАЕТСЯ! 🏁\n");
        gameService.getRaceService().simulateRace(race);
        view.showRaceResults(race);

        showPlayerResults(race);
        checkIncidents(race);

        view.waitForEnter();
    }

    private void showPlayerResults(Race race) {
        int position = race.getPosition(gameService.getPlayerManager().getId());
        double prizeMoney = race.getPrizeMoney(position);
        int points = race.getPoints(position);

        view.showMessage("\n💰 РЕЗУЛЬТАТЫ ВАШЕЙ КОМАНДЫ:");
        view.showMessage("   Место: " + position);
        view.showMessage("   Призовые: $" + prizeMoney);
        view.showMessage("   Очки: " + points);
        view.showMessage("   Общий бюджет: $" + gameService.getPlayerManager().getBudget());
        view.showMessage("   Всего очков: " + gameService.getPlayerManager().getChampionshipPoints());
    }

    private void checkIncidents(Race race) {
        if (race.getIncidents().containsKey(gameService.getPlayerManager().getId())) {
            view.showWarning("\n⚠️ ВНИМАНИЕ: В вашем болиде произошла поломка!");
            view.showMessage("   Рекомендуется отремонтировать болид перед следующей гонкой.");
        }
    }
}