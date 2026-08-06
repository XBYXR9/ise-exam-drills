package ise.testing.s18_testdoubles;

public interface SubscriberRepository {

    Subscriber findById(String subscriberId);

    void save(Subscriber subscriber);
}
