package view;

public class MainMenuView {
    private final ConsoleIO io;

    public MainMenuView(ConsoleIO io) {
        this.io = io;
    }

    public void showWelcomeMessage() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("   ДОБРО ПОЖАЛОВАТЬ В RACING MANAGER");
        System.out.println("=".repeat(60));
        System.out.println("Вы - директор гоночной команды.");
        System.out.println("Управляйте бюджетом, нанимайте персонал,");
        System.out.println("покупайте компоненты и побеждайте в гонках!\n");
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
        System.out.println("11. Контракты со спонсорами");
        System.out.println("12. Выход");
        System.out.println("=".repeat(50));
    }
}

