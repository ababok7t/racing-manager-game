package controller;

import service.GameService;
import view.ConsoleIO;
import view.MainMenuView;
import view.RaceView;

public class GameController {
    private final GameService gameService;
    private final ConsoleIO io;
    private final MainMenuView mainMenuView;
    private final RaceView raceView;

    private final RaceController raceController;
    private final ShopController shopController;
    private final CarController carController;
    private final InfoController infoController;
    private boolean isRunning;

    public GameController() {
        this.gameService = new GameService();
        this.io = new ConsoleIO();
        this.mainMenuView = new MainMenuView(io);
        this.raceView = new RaceView(io);

        this.raceController = new RaceController(gameService, io, raceView);
        this.shopController = new ShopController(gameService, io);
        this.carController = new CarController(gameService, io);
        this.infoController = new InfoController(gameService, io);
        this.isRunning = true;
    }

    public void start() {
        mainMenuView.showWelcomeMessage();

        while (isRunning) {
            mainMenuView.showMainMenu();
            int choice = io.getUserIntInput("Выберите пункт меню: ", 1, 12);

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
                case 11 -> shopController.manageSponsorContracts();
                case 12 -> exit();
                default -> io.showError("Неверный выбор!");
            }
        }
    }

    private void exit() {
        io.showMessage("\nСеанс окончен");
        isRunning = false;
    }
}