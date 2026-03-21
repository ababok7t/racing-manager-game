package service;

import model.*;
import model.staff.*;
import model.components.*;
import model.market.MarketItem;
import java.util.*;

public class BotService {
    private final GameService gameService;
    private final Random random;
    private List<Manager> opponentManagers;

    public BotService(GameService gameService) {
        this.gameService = gameService;
        this.random = gameService.getRandom();
        this.opponentManagers = new ArrayList<>();
    }

    public void initializeOpponentManagers(int count) {
        String[] teamNames = {"Red Bull Racing", "Ferrari", "Mercedes",
                "McLaren", "Aston Martin", "Alpine", "Williams"};

        for (int i = 0; i < count && i < teamNames.length; i++) {
            createOpponentManager(teamNames[i]);
        }
    }

    private void createOpponentManager(String name) {
        Manager manager = new Manager(name, 1500000 + random.nextInt(2000000));
        gameService.getManagerRepository().save(manager);
        opponentManagers.add(manager);

        int carCount = 1 + random.nextInt(2);
        for (int i = 0; i < carCount; i++) {
            Car car = new Car(name + " Car " + (i + 1));
            car.setManagerId(manager.getId());

            // Соперники должны быть собраны “из рынка”, т.к. на старте componentRepository пустой.
            MarketService market = gameService.getMarketService();

            List<Engine> engines = market.getAvailableEngines().stream()
                    .map(MarketItem::getItem)
                    .toList();
            List<Transmission> transmissions = market.getAvailableTransmissions().stream()
                    .map(MarketItem::getItem)
                    .toList();
            List<Suspension> suspensions = market.getAvailableSuspensions().stream()
                    .map(MarketItem::getItem)
                    .toList();
            List<Aerodynamics> aerodynamics = market.getAvailableAerodynamics().stream()
                    .map(MarketItem::getItem)
                    .toList();
            List<Tyres> tyresList = market.getAvailableTyres().stream()
                    .map(MarketItem::getItem)
                    .toList();

            if (!engines.isEmpty()
                    && !transmissions.isEmpty()
                    && !suspensions.isEmpty()
                    && !aerodynamics.isEmpty()
                    && !tyresList.isEmpty()) {
                Engine engine = engines.get(random.nextInt(engines.size()));

                List<Transmission> compatibleTransmissions = transmissions.stream()
                        .filter(t -> t.isCompatibleWith(engine))
                        .toList();
                if (compatibleTransmissions.isEmpty()) {
                    gameService.getCarRepository().save(car);
                    manager.addCarId(car.getId());
                    continue;
                }
                Transmission transmission = compatibleTransmissions.get(random.nextInt(compatibleTransmissions.size()));

                List<Suspension> compatibleSuspensions = suspensions.stream()
                        .filter(s -> s.canSupportWeight(engine.getWeight()))
                        .toList();
                if (compatibleSuspensions.isEmpty()) {
                    gameService.getCarRepository().save(car);
                    manager.addCarId(car.getId());
                    continue;
                }
                Suspension suspension = compatibleSuspensions.get(random.nextInt(compatibleSuspensions.size()));

                Aerodynamics selectedAerodynamics = aerodynamics.get(random.nextInt(aerodynamics.size()));
                Tyres tyres = tyresList.get(random.nextInt(tyresList.size()));

                // Клонируем компоненты, чтобы износ соперников не портил товары в меню.
                car.setEngine(cloneEngine(engine));
                car.setTransmission(cloneTransmission(transmission));
                car.setSuspension(cloneSuspension(suspension));
                car.setAerodynamics(cloneAerodynamics(selectedAerodynamics));
                car.setTyres(cloneTyres(tyres));
                car.setBuilt(true);
            }

            gameService.getCarRepository().save(car);
            manager.addCarId(car.getId());
        }

        List<Pilot> availablePilots = gameService.getPilotRepository().findAvailable();
        int pilotCount = Math.min(1 + random.nextInt(2), availablePilots.size());
        for (int i = 0; i < pilotCount; i++) {
            if (!availablePilots.isEmpty()) {
                Pilot pilot = availablePilots.get(random.nextInt(availablePilots.size()));
                gameService.getPilotRepository().hire(pilot.getId(), manager.getId());
                manager.addPilotId(pilot.getId());
                availablePilots.remove(pilot);
            }
        }

        List<Engineer> availableEngineers = gameService.getEngineerRepository().findAvailable();
        int engCount = Math.min(2 + random.nextInt(2), availableEngineers.size());
        for (int i = 0; i < engCount; i++) {
            if (!availableEngineers.isEmpty()) {
                Engineer engineer = availableEngineers.get(random.nextInt(availableEngineers.size()));
                gameService.getEngineerRepository().hire(engineer.getId(), manager.getId());
                manager.addEngineerId(engineer.getId());
                availableEngineers.remove(engineer);
            }
        }

        gameService.getManagerRepository().save(manager);
    }

    public List<Manager> getOpponentManagers() {
        return new ArrayList<>(opponentManagers);
    }

    public Map.Entry<String, String> getRandomLineup(String managerId) {
        Optional<Manager> managerOpt = gameService.getManagerRepository().findById(managerId);
        if (managerOpt.isEmpty()) return null;

        Manager manager = managerOpt.get();

        List<Car> cars = gameService.getCarRepository().findAllById(manager.getCarIds()).stream()
                .filter(Car::isComplete)
                .toList();

        List<Pilot> pilots = gameService.getPilotRepository().findAllById(manager.getPilotIds());

        if (cars.isEmpty() || pilots.isEmpty()) return null;

        Car car = cars.get(random.nextInt(cars.size()));
        Pilot pilot = pilots.get(random.nextInt(pilots.size()));

        return new AbstractMap.SimpleEntry<>(car.getId(), pilot.getId());
    }

    public void updateAllOpponentManagers() {
        for (Manager manager : opponentManagers) {
            manager.addPrizeMoney(50000);
            gameService.getManagerRepository().save(manager);
        }
    }

    private Engine cloneEngine(Engine engine) {
        return new Engine(
                engine.getName(),
                engine.getPrice(),
                engine.getBasePower(),
                engine.getWeight(),
                engine.getEngineType(),
                engine.getReliability()
        );
    }

    private Transmission cloneTransmission(Transmission transmission) {
        return new Transmission(
                transmission.getName(),
                transmission.getPrice(),
                transmission.getBaseEfficiency(),
                transmission.getWeight(),
                transmission.getCompatibleEngineType()
        );
    }

    private Suspension cloneSuspension(Suspension suspension) {
        return new Suspension(
                suspension.getName(),
                suspension.getPrice(),
                suspension.getBaseStability(),
                suspension.getMaxWeight(),
                suspension.getWeight()
        );
    }

    private Aerodynamics cloneAerodynamics(Aerodynamics aerodynamics) {
        return new Aerodynamics(
                aerodynamics.getName(),
                aerodynamics.getPrice(),
                aerodynamics.getBaseDownforce(),
                aerodynamics.getWeight()
        );
    }

    private Tyres cloneTyres(Tyres tyres) {
        return new Tyres(
                tyres.getName(),
                tyres.getPrice(),
                tyres.getBaseGrip(),
                tyres.getCompound(),
                tyres.getGripModifier(),
                tyres.getWeight()
        );
    }
}