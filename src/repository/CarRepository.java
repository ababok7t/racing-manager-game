package repository;

import model.Car;
import java.util.List;
import java.util.stream.Collectors;

public class CarRepository extends Repository<Car> {

    public Car save(Car car) {
        return super.save(car, car.getId());
    }

    public List<Car> findByManagerId(String managerId) {
        return findAll().stream()
                .filter(car -> managerId.equals(car.getManagerId()))
                .collect(Collectors.toList());
    }

    public List<Car> findAvailable() {
        return findAll().stream()
                .filter(car -> car.getManagerId() == null)
                .collect(Collectors.toList());
    }

    public List<Car> findReadyForRace() {
        return findAll().stream()
                .filter(car -> car.isComplete() && car.getWearPercentage() < 80)
                .collect(Collectors.toList());
    }
}