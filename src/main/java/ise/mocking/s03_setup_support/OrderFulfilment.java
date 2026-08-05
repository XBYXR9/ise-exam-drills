package ise.mocking.s03_setup_support;

/** SUT with three collaborators -- the case EasyMockSupport exists for. */
public class OrderFulfilment {

    private final StockLedger ledger;
    private final ShippingLabelPrinter printer;
    private final AuditTrail audit;

    public OrderFulfilment(StockLedger ledger, ShippingLabelPrinter printer, AuditTrail audit) {
        this.ledger = ledger;
        this.printer = printer;
        this.audit = audit;
    }

    /** @return the shipping label, or null when the stock could not be reserved. */
    public String fulfil(String sku, int quantity) {
        if (!ledger.reserve(sku, quantity)) {
            audit.log("out of stock: " + sku);
            return null;
        }
        String label = printer.print(sku, quantity);
        audit.log("shipped: " + sku);
        return label;
    }
}
