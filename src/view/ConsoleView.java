package view;

import model.*;
import model.race.*;
import model.staff.*;
import model.components.*;
import java.util.*;

public class ConsoleView {
    private final Scanner scanner;

    public ConsoleView() {
        this.scanner = new Scanner(System.in);
    }

    public void showWelcomeMessage() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("   ДОБРО ПОЖАЛОВАТЬ В RACING MANAGER");
        System.out.println("=".repeat(60));
        System.out.println("Вы - директор гоночной команды.");
        System.out.println("Управляйте бюджетом, нанимайте персонал,");
        System.out.println("покупайте компоненты и побеждайте в гонках!\n");
    }

    public void clearScreen() {
        // В консоли нет “настоящего” screen clear везде одинакового.
        // Поэтому используем безопасный вариант через переносы строк.
        System.out.print("\n".repeat(60));
    }

    public void showMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("ГЛАВНОЕ МЕНЮ");
        System.out.println("=".repeat(50));
        System.out.println("1. Начать гонку");
        System.out.println("2. Купить комплектующие");
        System.out.println("3. Собрать болид");
        System.out.println("4. Нанять инженера");
        System.out.println("5. Нанять пилота");
        System.out.println("6. Просмотреть болиды");
        System.out.println("7. Просмотреть пилотов");
        System.out.println("8. Просмотреть статистику гонок");
        System.out.println("9. Просмотреть другие команды");
        System.out.println("10. Просмотреть результаты последних гонок");
        System.out.println("11. Выход");
        System.out.println("=".repeat(50));
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showError(String message) {
        System.out.println("❌ " + message);
    }

    public void showSuccess(String message) {
        System.out.println("✅ " + message);
    }

    public void showWarning(String message) {
        System.out.println("⚠️ " + message);
    }

    public int getUserIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("Пожалуйста, введите число.");
            scanner.next();
            System.out.print(prompt);
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    public int getUserIntInput(String prompt, int min, int max) {
        int value;
        do {
            value = getUserIntInput(prompt);
            if (value < min || value > max) {
                System.out.println("Введите число от " + min + " до " + max);
            }
        } while (value < min || value > max);
        return value;
    }

    public String getUserStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public boolean getUserConfirmation(String prompt) {
        System.out.print(prompt + " (1 - Да, 2 - Нет): ");
        int choice = getUserIntInput("", 1, 2);
        return choice == 1;
    }

    public void waitForEnter() {
        System.out.print("Нажмите Enter для продолжения...");
        scanner.nextLine();
    }

    public void showTracks(List<Track> tracks) {
        System.out.println("\nДоступные трассы:");
        for (int i = 0; i < tracks.size(); i++) {
            System.out.println((i + 1) + ". " + tracks.get(i));
        }
    }

    public void showCars(List<Car> cars) {
        System.out.println("\nДоступные болиды:");
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            System.out.println((i + 1) + ". " + car.getName() +
                    " [Произв: " + String.format("%.2f", car.calculatePerformance()) +
                    ", Износ: " + String.format("%.1f%%", car.getWearPercentage()) + "]");
        }
    }

    public void showPilots(List<Pilot> pilots) {
        System.out.println("\nПилоты:");
        for (int i = 0; i < pilots.size(); i++) {
            System.out.println((i + 1) + ". " + pilots.get(i));
        }
    }

    public void showComponents(List<? extends Component> components) {
        System.out.println("\nДоступные компоненты:");
        for (int i = 0; i < components.size(); i++) {
            System.out.println((i + 1) + ". " + components.get(i));
        }
    }

    public void showRaceInfo(Race race) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("ГОНКА: " + race.getTrack().getName());
        System.out.println("=".repeat(60));
        System.out.println("Трасса: " + race.getTrack().getName() +
                " (" + race.getTrack().getCountry() + ")");
        System.out.println("Длина круга: " + race.getTrack().getLength() + " км");
        System.out.println("Круги: " + race.getTrack().getLaps());
        System.out.println("Погода: " + race.getWeather());
        System.out.println("Участников: " + race.getParticipantManagerIds().size());
        System.out.println("=".repeat(60));
    }

    public void showRaceResults(Race race) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("РЕЗУЛЬТАТЫ ГОНКИ: " + race.getTrack().getName());
        System.out.println("=".repeat(60));

        Map<String, Integer> positions = race.getAllPositions();
        List<Map.Entry<String, Integer>> sorted = positions.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .toList();

        for (Map.Entry<String, Integer> entry : sorted) {
            String medal = switch (entry.getValue()) {
                case 1 -> "🥇 ";
                case 2 -> "🥈 ";
                case 3 -> "🥉 ";
                default -> "   ";
            };
            System.out.println(medal + entry.getValue() + ". " + entry.getKey());
        }

        Map<String, String> incidents = race.getIncidents();
        if (!incidents.isEmpty()) {
            System.out.println("\n⚠️ ИНЦИДЕНТЫ:");
            for (String incident : incidents.values()) {
                System.out.println("   ⚠️ " + incident);
            }
        }

        System.out.println("=".repeat(60));
    }
}