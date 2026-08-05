package ise.mocking.s13_multiplemocks;

public interface WarehouseLedger {

    boolean reserve(String sku, int quantity);

    void release(String sku, int quantity);
}
