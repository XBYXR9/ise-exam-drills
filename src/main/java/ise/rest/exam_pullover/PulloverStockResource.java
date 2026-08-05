package ise.rest.exam_pullover;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * RETAKE EXAM, EXERCISE 6 -- the three endpoints, exactly as specified.
 *
 * Closed layered architecture: this class holds a PulloverStockService and nothing
 * else. The task says so in as many words ("Make sure to follow the closed layered
 * architectural style"), and it is free marks.
 */
@RestController
public class PulloverStockResource {

    private final PulloverStockService pulloverStockService;

    public PulloverStockResource(PulloverStockService pulloverStockService) {
        this.pulloverStockService = pulloverStockService;
    }

    /**
     * TASK 1 -- offer a pullover stock.
     * "If the passed pullover stock already has an id, throw ResponseStatusException
     *  with code 400. Otherwise invoke the service and respond with the created stock."
     */
    @PostMapping("/pulloverstocks")
    public ResponseEntity<PulloverStock> createPulloverStock(@RequestBody PulloverStock pulloverStock) {
        if (pulloverStock.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(pulloverStockService.createPulloverStock(pulloverStock));
    }

    /**
     * TASK 2 -- buy one pullover.
     * "Include pulloverStockId in the URL. 404 if no stock has this id, 400 if the
     *  quantity is 0. Otherwise invoke the service and respond with the updated stock."
     *
     * Order matters: check existence FIRST, because the quantity of a stock that does
     * not exist cannot be read.
     */
    @PostMapping("/pulloverstocks/{pulloverStockId}/buy")
    public ResponseEntity<PulloverStock> buyPullover(@PathVariable Long pulloverStockId) {
        PulloverStock stock = pulloverStockService.findById(pulloverStockId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (stock.getQuantity() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(pulloverStockService.buyPullover(stock));
    }

    /**
     * TASK 3 -- retrieve all pullover stocks.
     * "Add an optional request parameter onlyAvailable ... If the parameter is not
     *  defined in the request, the endpoint should use false as the default value."
     */
    @GetMapping("/pulloverstocks")
    public ResponseEntity<List<PulloverStock>> getAllPulloverStocks(
            @RequestParam(value = "onlyAvailable", required = false, defaultValue = "false") boolean onlyAvailable) {
        return ResponseEntity.ok(pulloverStockService.getAllPulloverStocks(onlyAvailable));
    }
}
