package tests;

import model.Manager;
import model.components.Engine;
import model.market.MarketItem;
import model.staff.Engineer;
import model.staff.Pilot;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.ShopService;

import static org.junit.jupiter.api.Assertions.*;

class ShopPurchaseAndHiringTest {

    @Test
    void buyComponentReducesBudgetAndSavesComponent() {
        GameService gameService = new GameService();
        ShopService shop = gameService.getShopService();
        Manager player = gameService.getPlayerManager();
        MarketItem<Engine> item = gameService.getMarketService().getAvailableEngines().get(0);

        double budgetBefore = player.getBudget();
        boolean success = shop.buyComponent(item.getItem());

        assertTrue(success);
        assertTrue(player.getBudget() < budgetBefore);
        assertTrue(gameService.getComponentRepository().findById(item.getItem().getId()).isPresent());
    }

    @Test
    void hirePilotMarksPilotAsHired() {
        GameService gameService = new GameService();
        ShopService shop = gameService.getShopService();
        MarketItem<Pilot> item = gameService.getMarketService().getAvailablePilots().get(0);

        boolean success = shop.hirePilot(item.getItem().getId());

        assertTrue(success);
        assertTrue(gameService.getPilotRepository().findById(item.getItem().getId()).orElseThrow().isHired());
    }

    @Test
    void hireEngineerMarksEngineerAsHired() {
        GameService gameService = new GameService();
        ShopService shop = gameService.getShopService();
        MarketItem<Engineer> item = gameService.getMarketService().getAvailableEngineers().get(0);

        boolean success = shop.hireEngineer(item.getItem().getId());

        assertTrue(success);
        assertTrue(gameService.getEngineerRepository().findById(item.getItem().getId()).orElseThrow().isHired());
    }
}

