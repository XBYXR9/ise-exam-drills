package ise.mocking.s16_injectionseams;

/** The concrete pump. Real hardware in production, an obstacle in a test. */
public class BuiltInWaterPump implements WaterPump {

    @Override
    public boolean pump(int millilitres) {
        return millilitres <= 500;
    }
}
