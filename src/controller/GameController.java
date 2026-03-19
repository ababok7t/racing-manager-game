package controller;

import service.GameService;
import view.ConsoleView;

public class GameController {
    private final GameService gameService;
    private final ConsoleView view;
    private final RaceController raceController;
    private final ShopController shopController;
    private final CarController carController;
    private final InfoController infoController;
    private boolean isRunning;

    public GameController() {
        this.gameService = new GameService();
        this.view = new ConsoleView();
        this.raceController = new RaceController(gameService, view);
        this.shopController = new ShopController(gameService, view);
        this.carController = new CarController(gameService, view);
        this.infoController = new InfoController(gameService, view);
        this.isRunning = true;
    }

    public void start() {
        view.showWelcomeMessage();

        while (isRunning) {
            view.showMainMenu();
            int choice = view.getUserIntInput("Выберите пункт меню: ", 1, 11);

            switch (choice) {
                case 1 -> raceController.startRace();
                case 2 -> shopController.buyComponents();
                case 3 -> carController.assembleCar();
                case 4 -> shopController.hireEngineer();
                case 5 -> shopController.hirePilot();
                case 6 -> carController.viewCars();
                case 7 -> infoController.viewPilots();
                case 8 -> infoController.viewRaceStatistics();
                case 9 -> infoController.viewOtherTeams();
                case 10 -> infoController.viewRecentResults();
                case 11 -> exit();
                default -> view.showError("Неверный выбор!");
            }
        }
    }

    private void exit() {
        view.showMessage("\nСпасибо за игру! До свидания!");
        isRunning = false;
    }
}