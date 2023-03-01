package course.concurrency.m2_async.cf.report;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import course.concurrency.m2_async.cf.LoadGenerator;

public class ReportServiceCF {

    private ExecutorService executor
//            = ForkJoinPool.commonPool(); // Execution time: 75300 | Execution time: 50721
//    = new ForkJoinPool(); // Execution time: 72146 | Execution time: 50502
//    = new ForkJoinPool(6); // Execution time: 136728 | Execution time: 51051
//    = new ForkJoinPool(12); // Execution time: 72149 | Execution time: 53307
//    = new ForkJoinPool(24); // Execution time: 36055 | Execution time: -
//    = new ForkJoinPool(48); // Execution time: 19542 | Execution time: -
//    = new ForkJoinPool(72); // Execution time: 15042 | Execution time: -
//    = new ForkJoinPool(96); // Execution time: 15041 | Execution time: 50955
//    = Executors.newCachedThreadPool(); // Execution time: 15043 | Execution time: 57257
//    = Executors.newFixedThreadPool(6); // Execution time: 136767 | Execution time: 49929
//    = Executors.newFixedThreadPool(12); // Execution time: 69113 | Execution time: -
//    = Executors.newFixedThreadPool(24); // Execution time: 36085 | Execution time: -
//    = Executors.newFixedThreadPool(48); // Execution time: 19548 | Execution time: -
//    = Executors.newFixedThreadPool(72); // Execution time: 15054 | Execution time: -
    = Executors.newFixedThreadPool(96); // Execution time: 15046 | Execution time: 57057
    private LoadGenerator loadGenerator = new LoadGenerator();

    public Others.Report getReport() {
        CompletableFuture<Collection<Others.Item>> itemsCF =
                CompletableFuture.supplyAsync(() -> getItems(), executor);

        CompletableFuture<Collection<Others.Customer>> customersCF =
                CompletableFuture.supplyAsync(() -> getActiveCustomers(), executor);

        CompletableFuture<Others.Report> reportTask =
                customersCF.thenCombine(itemsCF,
                        (customers, orders) -> combineResults(orders, customers));

        return reportTask.join();
    }

    private Others.Report combineResults(Collection<Others.Item> items, Collection<Others.Customer> customers) {
        return new Others.Report();
    }

    private Collection<Others.Customer> getActiveCustomers() {
        loadGenerator.work();
        loadGenerator.work();
        return List.of(new Others.Customer(), new Others.Customer());
    }

    private Collection<Others.Item> getItems() {
        loadGenerator.work();
        return List.of(new Others.Item(), new Others.Item());
    }

    public void shutdown() {
        executor.shutdown();
    }
}
