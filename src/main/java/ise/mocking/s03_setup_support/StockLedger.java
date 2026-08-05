package ise.mocking.s03_setup_support;

public interface StockLedger {

    boolean reserve(String sku, int quantity);
}
