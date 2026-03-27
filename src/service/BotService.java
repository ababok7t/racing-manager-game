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
                .filter(car -> !car.hasBrokenComponents())
                .toList();

        List<Pilot> pilots = gameService.getPilotRepository().findAllById(manager.getPilotIds());

        if (cars.isEmpty() || pilots.isEmpty()) return null;

        Car car = cars.get(random.nextInt(cars.size()));
        Pilot pilot = pilots.get(random.nextInt(pilots.size()));

        return new AbstractMap.SimpleEntry<>(car.getId(), pilot.getId());
    }

    public void updateAllOpponentManagers() {
        MarketService marketService = gameService.getMarketService();
        // Local pools so we don't "oversell" the same market item to multiple cars in one tick.
        List<MarketItem<Engine>> availableEngines = new ArrayList<>(marketService.getAvailableEngines());
        List<MarketItem<Transmission>> availableTransmissions = new ArrayList<>(marketService.getAvailableTransmissions());
        List<MarketItem<Suspension>> availableSuspensions = new ArrayList<>(marketService.getAvailableSuspensions());
        List<MarketItem<Aerodynamics>> availableAerodynamics = new ArrayList<>(marketService.getAvailableAerodynamics());
        List<MarketItem<Tyres>> availableTyres = new ArrayList<>(marketService.getAvailableTyres());

        for (Manager manager : opponentManagers) {
            updateOpponentManager(manager, marketService,
                    availableEngines, availableTransmissions, availableSuspensions, availableAerodynamics, availableTyres);
        }

        // Обновляем рынок
        marketService.refreshMarket();
    }

    private void updateOpponentManager(
            Manager manager,
            MarketService marketService,
            List<MarketItem<Engine>> availableEngines,
            List<MarketItem<Transmission>> availableTransmissions,
            List<MarketItem<Suspension>> availableSuspensions,
            List<MarketItem<Aerodynamics>> availableAerodynamics,
            List<MarketItem<Tyres>> availableTyres
    ) {
        List<Car> cars = gameService.getCarRepository().findAllById(manager.getCarIds());
        for (Car car : cars) {
            repairCarIfEligible(manager, car);
            updateCarComponentsIfNeeded(
                    manager,
                    car,
                    marketService,
                    availableEngines,
                    availableTransmissions,
                    availableSuspensions,
                    availableAerodynamics,
                    availableTyres
            );
        }

        gameService.getManagerRepository().save(manager);
    }

    private void updateCarComponentsIfNeeded(
            Manager manager,
            Car car,
            MarketService marketService,
            List<MarketItem<Engine>> availableEngines,
            List<MarketItem<Transmission>> availableTransmissions,
            List<MarketItem<Suspension>> availableSuspensions,
            List<MarketItem<Aerodynamics>> availableAerodynamics,
            List<MarketItem<Tyres>> availableTyres
    ) {
        if (car.isComplete() && !car.hasBrokenComponents()) return;

        boolean carModified = false;

        carModified |= detachBrokenComponents(car);

        // Match player assembly rules: engine first, then compatible transmission/suspension.
        carModified |= tryBuyEngine(manager, car, marketService, availableEngines);
        carModified |= tryBuyTransmission(manager, car, marketService, availableTransmissions);
        carModified |= tryBuySuspension(manager, car, marketService, availableSuspensions);
        carModified |= tryBuyAerodynamics(manager, car, marketService, availableAerodynamics);
        carModified |= tryBuyTyres(manager, car, marketService, availableTyres);

        boolean hasAllComponents = carHasAllComponents(car);
        if (!carModified && !hasAllComponents) return;

        if (hasAllComponents) car.setBuilt(true);
        gameService.getCarRepository().save(car);
    }

    private void repairCarIfEligible(Manager manager, Car car) {
        // Аналог логики игрока: ремонт возможен только если нет разрушенных компонентов.
        if (!car.isComplete()) return;
        if (car.hasBrokenComponents()) return;
        if (car.getWearPercentage() <= 50) return;

        double repairCost = car.getWearPercentage() * 100;
        if (!manager.spendBudget(repairCost)) return;

        car.repair();
        gameService.getCarRepository().save(car);
    }

    private boolean detachBrokenComponents(Car car) {
        boolean changed = false;

        if (car.getEngine() != null && car.getEngine().isBroken()) {
            car.setEngine(null);
            car.setBuilt(false);
            // При замене двигателя старые узлы могут стать несовместимыми — безопаснее пересобрать.
            car.setTransmission(null);
            car.setSuspension(null);
            changed = true;
        }

        if (car.getTransmission() != null && car.getTransmission().isBroken()) {
            car.setTransmission(null);
            car.setBuilt(false);
            changed = true;
        }
        if (car.getSuspension() != null && car.getSuspension().isBroken()) {
            car.setSuspension(null);
            car.setBuilt(false);
            changed = true;
        }
        if (car.getAerodynamics() != null && car.getAerodynamics().isBroken()) {
            car.setAerodynamics(null);
            car.setBuilt(false);
            changed = true;
        }
        if (car.getTyres() != null && car.getTyres().isBroken()) {
            car.setTyres(null);
            car.setBuilt(false);
            changed = true;
        }

        // Если двигатель сменили/поставили впервые — проверим совместимость оставшихся узлов.
        if (car.getEngine() != null) {
            if (car.getTransmission() != null && !car.getTransmission().isCompatibleWith(car.getEngine())) {
                car.setTransmission(null);
                car.setBuilt(false);
                changed = true;
            }
            if (car.getSuspension() != null && !car.getSuspension().canSupportWeight(car.getEngine().getWeight())) {
                car.setSuspension(null);
                car.setBuilt(false);
                changed = true;
            }
        }

        return changed;
    }

    private boolean tryBuyEngine(
            Manager manager,
            Car car,
            MarketService marketService,
            List<MarketItem<Engine>> availableEngines
    ) {
        if (car.getEngine() != null || availableEngines.isEmpty()) return false;

        MarketItem<Engine> item = availableEngines.get(random.nextInt(availableEngines.size()));
        if (!manager.spendBudget(item.getPrice())) return false;

        car.setEngine(cloneEngine(item.getItem()));
        marketService.buyItem(item.getId());
        availableEngines.remove(item);
        return true;
    }

    private boolean tryBuyTransmission(
            Manager manager,
            Car car,
            MarketService marketService,
            List<MarketItem<Transmission>> availableTransmissions
    ) {
        if (car.getTransmission() != null || car.getEngine() == null || availableTransmissions.isEmpty()) return false;

        List<MarketItem<Transmission>> compatible = availableTransmissions.stream()
                .filter(t -> t.getItem().isCompatibleWith(car.getEngine()))
                .toList();
        if (compatible.isEmpty()) return false;

        MarketItem<Transmission> item = compatible.get(random.nextInt(compatible.size()));
        if (!manager.spendBudget(item.getPrice())) return false;

        car.setTransmission(cloneTransmission(item.getItem()));
        marketService.buyItem(item.getId());
        availableTransmissions.remove(item);
        return true;
    }

    private boolean tryBuySuspension(
            Manager manager,
            Car car,
            MarketService marketService,
            List<MarketItem<Suspension>> availableSuspensions
    ) {
        if (car.getSuspension() != null || car.getEngine() == null || availableSuspensions.isEmpty()) return false;

        List<MarketItem<Suspension>> compatible = availableSuspensions.stream()
                .filter(s -> s.getItem().canSupportWeight(car.getEngine().getWeight()))
                .toList();
        if (compatible.isEmpty()) return false;

        MarketItem<Suspension> item = compatible.get(random.nextInt(compatible.size()));
        if (!manager.spendBudget(item.getPrice())) return false;

        car.setSuspension(cloneSuspension(item.getItem()));
        marketService.buyItem(item.getId());
        availableSuspensions.remove(item);
        return true;
    }

    private boolean tryBuyAerodynamics(
            Manager manager,
            Car car,
            MarketService marketService,
            List<MarketItem<Aerodynamics>> availableAerodynamics
    ) {
        if (car.getAerodynamics() != null || availableAerodynamics.isEmpty()) return false;

        MarketItem<Aerodynamics> item = availableAerodynamics.get(random.nextInt(availableAerodynamics.size()));
        if (!manager.spendBudget(item.getPrice())) return false;

        car.setAerodynamics(cloneAerodynamics(item.getItem()));
        marketService.buyItem(item.getId());
        availableAerodynamics.remove(item);
        return true;
    }

    private boolean tryBuyTyres(
            Manager manager,
            Car car,
            MarketService marketService,
            List<MarketItem<Tyres>> availableTyres
    ) {
        if (car.getTyres() != null || availableTyres.isEmpty()) return false;

        MarketItem<Tyres> item = availableTyres.get(random.nextInt(availableTyres.size()));
        if (!manager.spendBudget(item.getPrice())) return false;

        car.setTyres(cloneTyres(item.getItem()));
        marketService.buyItem(item.getId());
        availableTyres.remove(item);
        return true;
    }

    private boolean carHasAllComponents(Car car) {
        return car.getEngine() != null
                && car.getTransmission() != null
                && car.getSuspension() != null
                && car.getAerodynamics() != null
                && car.getTyres() != null;
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