package ise.rest.exam_pullover;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** Business layer. Task 2 says the quantity change belongs here, not in the resource. */
@Service
public class PulloverStockService {

    private final PulloverStockRepository repository;

    public PulloverStockService(PulloverStockRepository repository) {
        this.repository = repository;
    }

    public PulloverStock createPulloverStock(PulloverStock stock) {
        return repository.save(stock);
    }

    public Optional<PulloverStock> findById(Long id) {
        return repository.findById(id);
    }

    public List<PulloverStock> getAllPulloverStocks(boolean onlyAvailable) {
        return onlyAvailable ? repository.findByQuantityGreaterThan(0) : repository.findAll();
    }

    /** TASK 2: decrease the quantity accordingly. */
    public PulloverStock buyPullover(PulloverStock stock) {
        stock.setQuantity(stock.getQuantity() - 1);
        return repository.save(stock);
    }
}
