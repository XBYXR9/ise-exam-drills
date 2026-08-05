package ise.mocking.s08_exceptions;

public class GatewayTimeoutException extends RuntimeException {

    public GatewayTimeoutException(String message) {
        super(message);
    }
}
