package ise.rest.practice;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** PERSISTENCE LAYER -- one TODO for you. */
@Repository
public class PracticeStockRepository {

    private final Map<Long, PracticeStock> stocks = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(0);

    public PracticeStock save(PracticeStock stock) {
        if (stock.getId() == null) {
            stock.setId(ids.incrementAndGet());
        }
        stocks.put(stock.getId(), stock);
        return stock;
    }

    public Optional<PracticeStock> findById(Long id) {
        return Optional.ofNullable(stocks.get(id));
    }

    public List<PracticeStock> findAll() {
        return new ArrayList<>(stocks.values());
    }

    /**
     * TODO Task 3a -- "implement required functionality in PulloverStockRepository":
     *   return only the stocks whose quantity is greater than the threshold.
     *   (With Spring Data JPA this whole method would be one derived-query signature.)
     */
    public List<PracticeStock> findByQuantityGreaterThan(int threshold) {
        throw new UnsupportedOperationException("TODO Task 3a");
    }
}
