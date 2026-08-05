package ise.mocking.s04_mocktypes;

public interface MailSender {

    void sendConfirmation(String email);

    void sendWelcome(String email);
}
