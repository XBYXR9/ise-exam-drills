package ise.mocking.s07_callcounts;

public interface SmsGateway {

    boolean send(String number, String text);
}
