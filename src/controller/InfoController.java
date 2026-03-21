package controller;

import model.*;
import model.race.*;
import model.staff.*;
import service.GameService;
import view.ConsoleIO;
import java.util.*;

public class InfoController {
    private final GameService gameService;
    private final ConsoleIO io;

    public InfoController(GameService gameService, ConsoleIO io) {
        this.gameService = gameService;
        this.io = io;
    }

    public void viewPilots() {
        io.clearScreen();
        List<Pilot> pilots = gameService.getPlayerPilots();

        io.showMessage("\n=== ВАШИ ПИЛОТЫ ===");

        if (pilots.isEmpty()) {
            io.showMessage("У вас пока нет пилотов.");
            io.showMessage("Наймите пилотов в разделе 5.");
        } else {
            displayPilots(pilots);
        }

        io.waitForEnter();
    }

    private void displayPilots(List<Pilot> pilots) {
        for (int i = 0; i < pilots.size(); i++) {
            Pilot pilot = pilots.get(i);
            io.showMessage("\n" + (i + 1) + ". " + pilot.getName());
            io.showMessage("   Возраст: " + pilot.getAge());
            io.showMessage("   Навык: " + String.format("%.1f", pilot.getSkill()));
            io.showMessage("   Опыт: " + String.format("%d", pilot.getExperience()));
            io.showMessage("   Агрессивность: " + (pilot.getAggression() * 100) + "%");
            io.showMessage("   Стабильность: " + (pilot.getConsistency() * 100) + "%");
        }
    }

    public void viewRaceStatistics() {
        io.clearScreen();
        String playerId = gameService.getPlayerManager().getId();
        List<Race> races = gameService.getRaceRepository().findByManagerId(playerId);

        io.showMessage("\n=== СТАТИСТИКА ВАШИХ ГОНОК ===");
        io.showMessage("Всего гонок: " + races.size());

        if (races.isEmpty()) {
            io.showMessage("\nВы еще не участвовали в гонках.");
            io.showMessage("Начните гонку в разделе 1!");
        } else {
            RaceStatistics stats = calculateStats(races, playerId);
            displayStats(stats);
            displayRecentRaces(races, playerId);
        }

        io.waitForEnter();
    }

    private RaceStatistics calculateStats(List<Race> races, String playerId) {
        RaceStatistics stats = new RaceStatistics();
        for (Race race : races) {
            int pos = race.getPosition(playerId);
            stats.totalPoints += race.getPoints(pos);

            if (pos == 1) stats.wins++;
            if (pos <= 3) stats.podiums++;

            if (pos < stats.bestPosition) {
                stats.bestPosition = pos;
                stats.bestRace = race.getTrack().getName();
            }
        }
        stats.racesCount = races.size();
        return stats;
    }

    private void displayStats(RaceStatistics stats) {
        io.showMessage("\nОбщая статистика:");
        io.showMessage("   Победы: " + stats.wins);
        io.showMessage("   Подиумы: " + stats.podiums);
        io.showMessage("   Всего очков: " + stats.totalPoints);
        io.showMessage("   Лучший результат: " + stats.bestPosition + " место (" + stats.bestRace + ")");
        io.showMessage("   Очков в среднем за гонку: " +
                String.format("%.1f", (double) stats.totalPoints / stats.racesCount));
    }

    private void displayRecentRaces(List<Race> races, String playerId) {
        io.showMessage("\nПоследние 5 гонок:");
        List<Race> recent = races.subList(Math.max(0, races.size() - 5), races.size());

        for (Race race : recent) {
            int pos = race.getPosition(playerId);
            io.showMessage("   " + race.getTrack().getName() +
                    ": " + pos + " место (" + race.getPoints(pos) + " очков)");
        }
    }

    public void viewOtherTeams() {
        io.clearScreen();
        List<Manager> opponents = gameService.getBotService().getOpponentManagers();

        io.showMessage("\n=== КОМАНДЫ СОПЕРНИКОВ ===");

        if (opponents.isEmpty()) {
            io.showMessage("Нет команд-соперников.");
        } else {
            opponents.sort((a, b) -> b.getChampionshipPoints() - a.getChampionshipPoints());
            displayOpponents(opponents);
            showPlayerRanking(opponents);
        }

        io.waitForEnter();
    }

    private void displayOpponents(List<Manager> opponents) {
        for (int i = 0; i < opponents.size(); i++) {
            Manager team = opponents.get(i);
            io.showMessage("\n" + (i + 1) + ". " + team.getName());
            io.showMessage("   Бюджет: $" + String.format("%,.0f", team.getBudget()));
            io.showMessage("   Очки: " + team.getChampionshipPoints());

            List<Pilot> pilots = gameService.getPilotRepository().findAllById(team.getPilotIds());
            if (!pilots.isEmpty()) {
                io.showMessage("   Пилоты:");
                for (Pilot pilot : pilots) {
                    io.showMessage("     - " + pilot.getName() +
                            " (навык: " + String.format("%.1f", pilot.getSkill()) + ")");
                }
            }

            io.showMessage("   Болидов: " + team.getCarIds().size());
        }
    }

    private void showPlayerRanking(List<Manager> opponents) {
        Manager player = gameService.getPlayerManager();
        int playerRank = 1;
        for (Manager team : opponents) {
            if (team.getChampionshipPoints() > player.getChampionshipPoints()) {
                playerRank++;
            }
        }

        io.showMessage("\nВаша позиция: " + playerRank + " из " + (opponents.size() + 1));
    }

    public void viewRecentResults() {
        io.clearScreen();

        String playerId = gameService.getPlayerManager().getId();
        List<Race> races = gameService.getRaceRepository().findByManagerId(playerId);

        io.showMessage("\n=== ПОСЛЕДНИЕ РЕЗУЛЬТАТЫ ===");
        if (races.isEmpty()) {
            io.showMessage("У вас еще нет результатов.");
            io.waitForEnter();
            return;
        }

        races.sort((r1, r2) -> r2.getId().compareTo(r1.getId()));
        List<Race> recent = races.subList(0, Math.min(5, races.size()));

        for (Race race : recent) {
            displayRaceResult(race, playerId);
        }

        io.waitForEnter();
    }

    private void displayRaceResult(Race race, String playerId) {
        int pos = race.getPosition(playerId);
        double prize = race.getPrizeMoney(pos);
        int points = race.getPoints(pos);

        io.showMessage("\n" + race.getTrack().getName());
        io.showMessage("  Место: " + pos);
        io.showMessage("  Призовые: $" + String.format("%,.0f", prize));
        io.showMessage("  Очки: " + points);

        if (race.getIncidents().containsKey(playerId)) {
            Incident incident = race.getIncidents().get(playerId);
            io.showWarning("  Инцидент: " + incident.getType());
        }
    }

    private static class RaceStatistics {
        int racesCount = 0;
        int wins = 0;
        int podiums = 0;
        int totalPoints = 0;
        int bestPosition = 999;
        String bestRace = "";
    }
}