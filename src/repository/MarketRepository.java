package repository;

import model.market.Market;
import model.market.MarketItem;
import java.util.*;
import java.util.stream.Collectors;

public class MarketRepository extends Repository<Market> {

    public Market save(Market market) {
        return super.save(market, market.getId());
    }

    public Optional<Market> findByName(String name) {
        return findAll().stream()
                .filter(m -> m.getName().equals(name))
                .findFirst();
    }

    public List<MarketItem<?>> getAllItems() {
        return findAll().stream()
                .flatMap(m -> m.getItems().stream())
                .collect(Collectors.toList());
    }

    public List<MarketItem<?>> getAvailableItems() {
        return findAll().stream()
                .flatMap(m -> m.getAvailableItems().stream())
                .collect(Collectors.toList());
    }

    public MarketItem<?> findItemById(String itemId) {
        for (Market market : findAll()) {
            MarketItem<?> item = market.findItemById(itemId);
            if (item != null) {
                return item;
            }
        }
        return null;
    }

    public void removeItemFromAll(String itemId) {
        for (Market market : findAll()) {
            market.removeItemById(itemId);
            save(market);
        }
    }

    public void addItemToMarket(String marketId, MarketItem<?> item) {
        findById(marketId).ifPresent(market -> {
            market.addItem(item);
            save(market);
        });
    }

    public void removeItemFromMarket(String marketId, String itemId) {
        findById(marketId).ifPresent(market -> {
            market.removeItemById(itemId);
            save(market);
        });
    }
}