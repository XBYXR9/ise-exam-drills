package ise.rest.practice;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** BUSINESS LAYER -- two TODOs for you. */
@Service
public class PracticeStockService {

    private final PracticeStockRepository repository;

    public PracticeStockService(PracticeStockRepository repository) {
        this.repository = repository;
    }

    public PracticeStock createStock(PracticeStock stock) {
        return repository.save(stock);
    }

    public Optional<PracticeStock> findById(Long id) {
        return repository.findById(id);
    }

    /**
     * TODO Task 3b -- honour the onlyAvailable flag by delegating to the right
     *   repository method. Remember which layer is allowed to call which.
     */
    public List<PracticeStock> getAllStocks(boolean onlyAvailable) {
        throw new UnsupportedOperationException("TODO Task 3b");
    }

    /**
     * TODO Task 2b -- "you have to make changes to buyPullover in
     *   PulloverStockService to decrease the quantity accordingly."
     *   Decrease by one and persist the result.
     */
    public PracticeStock buyOne(PracticeStock stock) {
        throw new UnsupportedOperationException("TODO Task 2b");
    }
}
