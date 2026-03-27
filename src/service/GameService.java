package service;

import model.*;
import model.race.*;
import model.staff.*;
import repository.*;
import java.util.*;

public class GameService {
    private final CarRepository carRepository;
    private final ComponentRepository componentRepository;
    private final PilotRepository pilotRepository;
    private final EngineerRepository engineerRepository;
    private final ManagerRepository managerRepository;
    private final TrackRepository trackRepository;
    private final RaceRepository raceRepository;
    private final MarketRepository marketRepository;
    private final ContractRepository contractRepository;

    private final RaceService raceService;
    private final ShopService shopService;
    private final MarketService marketService;
    private final BotService botService;
    private final GarageService garageService;

    private Manager playerManager;
    private final Random random = new Random();

    public GameService() {
        this.carRepository = new CarRepository();
        this.componentRepository = new ComponentRepository();
        this.pilotRepository = new PilotRepository();
        this.engineerRepository = new EngineerRepository();
        this.managerRepository = new ManagerRepository();
        this.trackRepository = new TrackRepository();
        this.raceRepository = new RaceRepository();
        this.marketRepository = new MarketRepository();
        this.contractRepository = new ContractRepository();

        this.raceService = new RaceService(this);
        this.shopService = new ShopService(this);
        this.marketService = new MarketService(this);
        this.botService = new BotService(this);
        this.garageService = new GarageService(this);

        initializeGame();
    }

    private void initializeGame() {
        playerManager = new Manager("Ваша команда", 1000000);
        managerRepository.save(playerManager);

        initializeTracks();

        marketService.initializeMarket();

        botService.initializeOpponentManagers(5);
    }

    private void initializeTracks() {
        trackRepository.save(new Track("Монца", "Италия", 5.793, 53, 0.6));
        trackRepository.save(new Track("Монако", "Монако", 3.337, 78, 0.9));
        trackRepository.save(new Track("Сильверстоун", "Великобритания", 5.891, 52, 0.7));
        trackRepository.save(new Track("Спа", "Бельгия", 7.004, 44, 0.8));
        trackRepository.save(new Track("Сочи", "Россия", 5.848, 53, 0.5));
    }

    public CarRepository getCarRepository() { return carRepository; }
    public ComponentRepository getComponentRepository() { return componentRepository; }
    public PilotRepository getPilotRepository() { return pilotRepository; }
    public EngineerRepository getEngineerRepository() { return engineerRepository; }
    public ManagerRepository getManagerRepository() { return managerRepository; }
    public TrackRepository getTrackRepository() { return trackRepository; }
    public RaceRepository getRaceRepository() { return raceRepository; }
    public ContractRepository getContractRepository() { return contractRepository; }

    public RaceService getRaceService() { return raceService; }
    public ShopService getShopService() { return shopService; }
    public MarketService getMarketService() { return marketService; }
    public BotService getBotService() { return botService; }
    public GarageService getGarageService() { return garageService; }

    public Manager getPlayerManager() { return playerManager; }
    public Random getRandom() { return random; }

    public List<Track> getAllTracks() { return trackRepository.findAll(); }

    public List<Car> getPlayerCars() {
        return carRepository.findByManagerId(playerManager.getId());
    }

    public List<Car> getUsableCars() {
        return carRepository.findByManagerId(playerManager.getId()).stream()
                .filter(car -> car.isComplete() && !car.hasBrokenComponents())
                .toList();
    }

    public List<Pilot> getPlayerPilots() {
        return pilotRepository.findByManagerId(playerManager.getId());
    }

    public List<Engineer> getPlayerEngineers() {
        return engineerRepository.findByManagerId(playerManager.getId());
    }

    public boolean canParticipate() {
        return !getUsableCars().isEmpty() &&
                !getPlayerPilots().isEmpty() &&
                !getPlayerEngineers().isEmpty();
    }

    public void processEndOfRace(Race race) {
        for (String managerId : race.getParticipantManagerIds()) {
            int position = race.getPosition(managerId);
            double prizeMoney = race.getPrizeMoney(position);
            int points = race.getPoints(position);

            managerRepository.findById(managerId).ifPresent(manager -> {
                manager.addPrizeMoney(prizeMoney);
                manager.addChampionshipPoints(points);

                //реп зависит от позиции
                if (managerId.equals(playerManager.getId())) {
                    manager.addReputation(getReputationDelta(position));
                }

                managerRepository.save(manager);
            });

            String carId = race.getCarId(managerId);
            if (carId != null) {
                carRepository.findById(carId).ifPresent(car -> {
                    car.increaseWear(10 + random.nextInt(20));
                    carRepository.save(car);
                });
            }

            String pilotId = race.getPilotId(managerId);
            if (pilotId != null) {
                pilotRepository.findById(pilotId).ifPresent(Pilot::gainExperience);
            }

            if (managerId.equals(playerManager.getId())) {
                processSponsorContractsAfterRace();
            }
        }

        // После каждой гонки соперники "ходят" как игрок: чинят/меняют и докупают с рынка.
        botService.updateAllOpponentManagers();
    }

    private int getReputationDelta(int position) {
        return switch (position) {
            case 1 -> 12;
            case 2 -> 8;
            case 3 -> 6;
            case 4 -> 4;
            default -> 2;
        };
    }

    private void processSponsorContractsAfterRace() {
        for (String contractId : playerManager.getContractIds()) {
            contractRepository.findById(contractId).ifPresent(contract -> {
                if (contract.isCompleted()) {
                    playerManager.removeContractId(contractId);
                    return;
                }

                contract.addRace();

                double payoutPerRace = contract.getPrice() * 1.5 / Math.max(1, contract.getNumberOfRaces());
                playerManager.addPrizeMoney(payoutPerRace);

                if (contract.isCompleted()) {
                    playerManager.removeContractId(contractId);
                    playerManager.addReputation(5);
                }

                contractRepository.save(contract);
            });
        }

        managerRepository.save(playerManager);
    }
}