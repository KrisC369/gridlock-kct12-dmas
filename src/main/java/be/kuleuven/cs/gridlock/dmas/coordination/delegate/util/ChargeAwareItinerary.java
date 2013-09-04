package be.kuleuven.cs.gridlock.dmas.coordination.delegate.util;

import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Itinerary that keeps track of chargelevel estimates.
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class ChargeAwareItinerary<R, T extends VirtualTime> extends Itinerary<R, T> {

    private Map<R, Float> chargeEstimates;
    private Map<R, Double> waitTimes;

    public ChargeAwareItinerary() {
        super();
        this.chargeEstimates = new HashMap<R, Float>();
        this.waitTimes = new HashMap<R, Double>();
    }

    public void add(R r, T t, Float f, Double w) {
        super.add(r, t);
        chargeEstimates.put(r, f);
        waitTimes.put(r, w);
    }

    public void addAll(List<R> r, List<T> t, List<Float> f, List<Double> w) {
        if (r.size() != t.size() && r.size() != f.size()) {
            throw new IllegalArgumentException("arguments cannot differ in size");
        }
        for (int i = 0; i < r.size(); i++) {
            this.add(r.get(i), t.get(i));
            this.chargeEstimates.put(r.get(i), f.get(i));
            this.waitTimes.put(r.get(i), w.get(i));
        }
    }

    public float getChargeEstimateFor(R r) {
        if (!contains(r)) {
            throw new IllegalArgumentException("No element in this itinerary.");
        }
        return chargeEstimates.get(r);
    }

    public double getWaitEstimateFor(R r) {
        if (!contains(r)) {
            throw new IllegalArgumentException("No element in this itinerary.");
        }
        return waitTimes.get(r);
    }
}