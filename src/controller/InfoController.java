package controller;

import model.*;
import model.race.*;
import model.staff.*;
import service.GameService;
import view.ConsoleView;
import java.util.*;

public class InfoController {
    private final GameService gameService;
    private final ConsoleView view;

    public InfoController(GameService gameService, ConsoleView view) {
        this.gameService = gameService;
        this.view = view;
    }

    public void viewPilots() {
        view.clearScreen();
        List<Pilot> pilots = gameService.getPlayerPilots();

        view.showMessage("\n=== ВАШИ ПИЛОТЫ ===");

        if (pilots.isEmpty()) {
            view.showMessage("У вас пока нет пилотов.");
            view.showMessage("Наймите пилотов в разделе 5.");
        } else {
            displayPilots(pilots);
        }

        view.waitForEnter();
    }

    private void displayPilots(List<Pilot> pilots) {
        for (int i = 0; i < pilots.size(); i++) {
            Pilot pilot = pilots.get(i);
            view.showMessage("\n" + (i + 1) + ". " + pilot.getName());
            view.showMessage("   Возраст: " + pilot.getAge());
            view.showMessage("   Навык: " + String.format("%.1f", pilot.getSkill()));
            view.showMessage("   Опыт: " + String.format("%.0f", pilot.getExperience()));
            view.showMessage("   Агрессивность: " + (pilot.getAggression() * 100) + "%");
            view.showMessage("   Стабильность: " + (pilot.getConsistency() * 100) + "%");
        }
    }

    public void viewRaceStatistics() {
        view.clearScreen();
        String playerId = gameService.getPlayerManager().getId();
        List<Race> races = gameService.getRaceRepository().findByManagerId(playerId);

        view.showMessage("\n=== СТАТИСТИКА ВАШИХ ГОНОК ===");
        view.showMessage("Всего гонок: " + races.size());

        if (races.isEmpty()) {
            view.showMessage("\nВы еще не участвовали в гонках.");
            view.showMessage("Начните гонку в разделе 1!");
        } else {
            RaceStatistics stats = calculateStats(races, playerId);
            displayStats(stats);
            displayRecentRaces(races, playerId);
        }

        view.waitForEnter();
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
        view.showMessage("\n📊 Общая статистика:");
        view.showMessage("   🏆 Победы: " + stats.wins);
        view.showMessage("   🥉 Подиумы: " + stats.podiums);
        view.showMessage("   ⭐ Всего очков: " + stats.totalPoints);
        view.showMessage("   🏁 Лучший результат: " + stats.bestPosition + " место (" + stats.bestRace + ")");
        view.showMessage("   📈 Очков в среднем за гонку: " +
                String.format("%.1f", (double) stats.totalPoints / stats.racesCount));
    }

    private void displayRecentRaces(List<Race> races, String playerId) {
        view.showMessage("\n📅 Последние 5 гонок:");
        List<Race> recent = races.subList(Math.max(0, races.size() - 5), races.size());

        for (Race race : recent) {
            int pos = race.getPosition(playerId);
            String medal = pos == 1 ? "🥇" : (pos == 2 ? "🥈" : (pos == 3 ? "🥉" : "  "));
            view.showMessage("   " + medal + " " + race.getTrack().getName() +
                    ": " + pos + " место (" + race.getPoints(pos) + " очков)");
        }
    }

    public void viewOtherTeams() {
        view.clearScreen();
        List<Manager> opponents = gameService.getBotService().getOpponentManagers();

        view.showMessage("\n=== КОМАНДЫ СОПЕРНИКОВ ===");

        if (opponents.isEmpty()) {
            view.showMessage("Нет команд-соперников.");
        } else {
            opponents.sort((a, b) -> b.getChampionshipPoints() - a.getChampionshipPoints());
            displayOpponents(opponents);
            showPlayerRanking(opponents);
        }

        view.waitForEnter();
    }

    private void displayOpponents(List<Manager> opponents) {
        for (int i = 0; i < opponents.size(); i++) {
            Manager team = opponents.get(i);
            view.showMessage("\n" + (i + 1) + ". " + team.getName());
            view.showMessage("   Бюджет: $" + String.format("%,.0f", team.getBudget()));
            view.showMessage("   Очки: " + team.getChampionshipPoints());

            List<Pilot> pilots = gameService.getPilotRepository().findAllById(team.getPilotIds());
            if (!pilots.isEmpty()) {
                view.showMessage("   Пилоты:");
                for (Pilot pilot : pilots) {
                    view.showMessage("     • " + pilot.getName() +
                            " (навык: " + String.format("%.1f", pilot.getSkill()) + ")");
                }
            }

            view.showMessage("   Болидов: " + team.getCarIds().size());
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

        view.showMessage("\n📊 Ваша позиция: " + playerRank + " из " + (opponents.size() + 1));
    }

    public void viewRecentResults() {
        view.clearScreen();

        String playerId = gameService.getPlayerManager().getId();
        List<Race> races = gameService.getRaceRepository().findByManagerId(playerId);

        view.showMessage("\n=== ПОСЛЕДНИЕ РЕЗУЛЬТАТЫ ===");
        if (races.isEmpty()) {
            view.showMessage("У вас еще нет результатов.");
            view.waitForEnter();
            return;
        }

        races.sort((r1, r2) -> r2.getId().compareTo(r1.getId()));
        List<Race> recent = races.subList(0, Math.min(5, races.size()));

        for (Race race : recent) {
            displayRaceResult(race, playerId);
        }

        view.waitForEnter();
    }

    private void displayRaceResult(Race race, String playerId) {
        int pos = race.getPosition(playerId);
        double prize = race.getPrizeMoney(pos);
        int points = race.getPoints(pos);

        view.showMessage("\n🏁 " + race.getTrack().getName());
        view.showMessage("  Место: " + pos);
        view.showMessage("  Призовые: $" + String.format("%,.0f", prize));
        view.showMessage("  Очки: " + points);

        if (race.getIncidents().containsKey(playerId)) {
            view.showWarning("  Инцидент: " + race.getIncidents().get(playerId));
        }
    }

    /**
     * Вспомогательный класс для статистики
     */
    private static class RaceStatistics {
        int racesCount = 0;
        int wins = 0;
        int podiums = 0;
        int totalPoints = 0;
        int bestPosition = 999;
        String bestRace = "";
    }
}