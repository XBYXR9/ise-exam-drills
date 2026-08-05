package ise.rest.exam_pullover;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/** Task 3 asks for the filtering to be implemented HERE, in the repository. */
@Repository
public class PulloverStockRepository {

    private final Map<Long, PulloverStock> stocks = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(0);

    public PulloverStock save(PulloverStock stock) {
        if (stock.getId() == null) {
            stock.setId(ids.incrementAndGet());
        }
        stocks.put(stock.getId(), stock);
        return stock;
    }

    public Optional<PulloverStock> findById(Long id) {
        return Optional.ofNullable(stocks.get(id));
    }

    public List<PulloverStock> findAll() {
        return new ArrayList<>(stocks.values());
    }

    /** With JPA this whole method would be one line: List<PulloverStock> findByQuantityGreaterThan(int q); */
    public List<PulloverStock> findByQuantityGreaterThan(int threshold) {
        List<PulloverStock> matches = new ArrayList<>();
        for (PulloverStock stock : stocks.values()) {
            if (stock.getQuantity() > threshold) {
                matches.add(stock);
            }
        }
        return matches;
    }
}
