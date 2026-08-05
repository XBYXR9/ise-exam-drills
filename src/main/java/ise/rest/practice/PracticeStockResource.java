package ise.rest.practice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * PRACTICE DRILL -- Retake exam, exercise 6 (REST, 22 points).
 *
 * NETWORK LAYER (Resource). Three endpoints to build. The tests are already written
 * for you, exactly as Artemis does it:
 *     src/test/java/ise/practice/rest/PracticeStockResourceTest.java
 *
 * How to work:
 *   1. delete the @Disabled from PracticeStockResourceTest
 *   2. fill in the TODOs here, in PracticeStockService and in PracticeStockRepository
 *   3. gradlew.bat test --tests "ise.practice.rest.*"
 *   4. gradlew.bat runPracticeServer   and poke it with a browser or curl
 *   5. diff against ise.rest.exam_pullover.PulloverStockResource
 *
 * =====================================================================
 * PROBLEM STATEMENT (exam wording)
 *
 * You are working for an online shop which sells pullovers. Sellers can offer their
 * pullovers and customers can buy these pullovers. This online shop also allows all
 * end users to see all offered pullovers. Make sure to follow the CLOSED LAYERED
 * architectural style.
 *
 * 1. Offer a pullover stock
 *    Create a REST endpoint with an appropriate HTTP method and URL that allows
 *    sellers to create one stock of pullovers, in a method named createStock.
 *    If the passed stock already has an id, throw a ResponseStatusException with
 *    code 400 (Bad Request). Otherwise invoke the corresponding methods in the
 *    service and respond with the created stock.
 *
 * 2. Buy a pullover
 *    Create a REST endpoint that allows customers to buy one pullover of a specific
 *    type, in a method named buyOne. Include the stock id in the URL.
 *    If no stock exists with this id, throw a ResponseStatusException with code 404.
 *    If the stock has a quantity of 0, throw one with code 400. Otherwise invoke the
 *    service and respond with the updated stock.
 *
 * 3. Retrieve all pullover stocks
 *    Create a REST endpoint that allows customers to retrieve all stocks, in a method
 *    named getAllStocks. Add an OPTIONAL request parameter onlyAvailable to retrieve
 *    only those with a quantity greater than 0. If the parameter is not defined in
 *    the request, the endpoint should use false as the default value.
 * =====================================================================
 */
@RestController
public class PracticeStockResource {

    private final PracticeStockService stockService;

    // Note what is NOT here: a PracticeStockRepository field. Closed layered
    // architecture means the Resource only ever talks to the Service.
    public PracticeStockResource(PracticeStockService stockService) {
        this.stockService = stockService;
    }

    // TODO Task 1 -- which HTTP method annotation, and which URL?
    //   Signature to aim for:
    //     public ResponseEntity<PracticeStock> createStock(PracticeStock stock)
    //   Do not forget the annotation that binds the request body.
    public ResponseEntity<PracticeStock> createStock(PracticeStock stock) {
        throw new UnsupportedOperationException("TODO Task 1");
    }

    // TODO Task 2a -- the id belongs in the PATH, and "buy" is an action on a
    //   sub-resource rather than a CRUD verb. Check existence BEFORE quantity:
    //   you cannot read the quantity of a stock that is not there.
    public ResponseEntity<PracticeStock> buyOne(Long stockId) {
        throw new UnsupportedOperationException("TODO Task 2a");
    }

    // TODO Task 3c -- the parameter is optional AND defaults to false. Getting only
    //   half of that right gives you a 500 on a request without the parameter.
    public ResponseEntity<List<PracticeStock>> getAllStocks(boolean onlyAvailable) {
        throw new UnsupportedOperationException("TODO Task 3c");
    }
}
