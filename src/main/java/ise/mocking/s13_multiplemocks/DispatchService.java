package ise.mocking.s13_multiplemocks;

/** SUT coordinating two collaborators; the courier must never see unreserved stock. */
public class DispatchService {

    private final WarehouseLedger ledger;
    private final CourierApi courier;

    public DispatchService(WarehouseLedger ledger, CourierApi courier) {
        this.ledger = ledger;
        this.courier = courier;
    }

    /** @return the courier tracking code, or null when the goods are not in stock. */
    public String dispatch(String sku, int quantity) {
        if (!ledger.reserve(sku, quantity)) {
            return null;
        }
        String tracking = courier.bookPickup(sku, quantity);
        if (tracking == null) {
            ledger.release(sku, quantity);   // compensate: no pickup, no reservation
        }
        return tracking;
    }
}
