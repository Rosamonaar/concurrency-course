package course.concurrency.m2_async.cf.min_price;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;

public class PriceAggregator {

    private static final int TIMEOUT_IN_MS = 2950;

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        var prices = new CopyOnWriteArrayList<Double>();
        var futures = new ArrayList<CompletableFuture<Void>>();
        shopIds.forEach(shopId -> {
                futures.add(
                        CompletableFuture
                                .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId))
                                .thenAcceptAsync(prices::add)
                );
        });
        waitAllOrTimeout(futures);
        return prices.isEmpty() ? Double.NaN : Collections.min(prices);
    }

    private void waitAllOrTimeout(ArrayList<CompletableFuture<Void>> futures) {
        var future = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        var start = System.currentTimeMillis();
        while (true) {
            if (future.isDone() || System.currentTimeMillis() - start >= TIMEOUT_IN_MS) break;
        }
    }
}
