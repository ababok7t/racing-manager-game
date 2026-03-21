package view;

import java.util.Scanner;

/**
 * Универсальный слой ввода/вывода для консольного интерфейса.
 * Экранная логика (меню/списки/результаты) должна жить в отдельных view-классах.
 */
public class ConsoleIO {
    private final Scanner scanner;

    public ConsoleIO() {
        this.scanner = new Scanner(System.in);
    }

    public void clearScreen() {
        // В консоли нет “настоящего” clear, поэтому используем безопасный вариант.
        System.out.print("\n".repeat(60));
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void showError(String message) {
        System.out.println("[ОШИБКА] " + message);
    }

    public void showSuccess(String message) {
        System.out.println("[OK] " + message);
    }

    public void showWarning(String message) {
        System.out.println("[ВНИМАНИЕ] " + message);
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
}

