package ise.testing.s15_observers;

import java.util.ArrayList;
import java.util.List;

/** Subject of the Observer pattern: notifies every attached chart view on a price change. */
public class Etf {

    private final List<ChartView> views = new ArrayList<>();
    private double price;

    public void attach(ChartView view) {
        views.add(view);
    }

    public void detach(ChartView view) {
        views.remove(view);
    }

    public void setPrice(double price) {
        this.price = price;
        notifyViews();
    }

    private void notifyViews() {
        for (ChartView view : views) {
            view.update();
        }
    }

    public double getPrice() {
        return price;
    }

    public int getViewCount() {
        return views.size();
    }
}
