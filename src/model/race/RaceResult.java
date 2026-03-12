package model.race;

import java.util.*;

public class RaceResult {
    private List<RaceEntry> results;
    private Weather weather;
    private int playerPosition;

    public RaceResult(List<RaceEntry> results, Weather weather) {
        this.results = results;
        this.weather = weather;
        this.playerPosition = findPlayerPosition();
    }

    private int findPlayerPosition() {
        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).isPlayer()) {
                return i + 1;
            }
        }
        return results.size();
    }

    // Призовые за позицию
    public double getPrizeForPosition(int position) {
        switch (position) {
            case 1: return 50000;
            case 2: return 30000;
            case 3: return 15000;
            default: return 0;
        }
    }

    // Геттеры
    public List<RaceEntry> getResults() { return results; }
    public Weather getWeather() { return weather; }
    public int getPlayerPosition() { return playerPosition; }
    public double getPlayerPrize() { return getPrizeForPosition(playerPosition); }

    // Вложенный класс для записи результата
    public static class RaceEntry {
        private String teamName;
        private String pilotName;
        private double totalTime;
        private boolean isPlayer;

        public RaceEntry(String teamName, String pilotName, double totalTime, boolean isPlayer) {
            this.teamName = teamName;
            this.pilotName = pilotName;
            this.totalTime = totalTime;
            this.isPlayer = isPlayer;
        }

        public String getTeamName() { return teamName; }
        public String getPilotName() { return pilotName; }
        public double getTotalTime() { return totalTime; }
        public boolean isPlayer() { return isPlayer; }
    }
}