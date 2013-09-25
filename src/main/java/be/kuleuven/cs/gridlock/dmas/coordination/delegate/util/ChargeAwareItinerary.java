package be.kuleuven.cs.gridlock.dmas.coordination.delegate.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;

/**
 * Itinerary that keeps track of chargelevel estimates.
 * 
 * @author Kristof Coninx <kristof.coninx AT cs.kuleuven.be>
 * @param <R>
 *          an R key type for this itinerary.
 * @param <T>
 *          A T representing a virtualTime timestamp to go with R.
 */
public class ChargeAwareItinerary<R, T extends VirtualTime> extends
    Itinerary<R, T> {

  private final Map<R, Float> chargeEstimates;
  private final Map<R, Double> waitTimes;

  /**
   * Default construcor.
   */
  public ChargeAwareItinerary() {
    super();
    this.chargeEstimates = new HashMap<R, Float>();
    this.waitTimes = new HashMap<R, Double>();
  }

  /**
   * Add Tuple from R T float and double.
   * 
   * @param r
   *          the R.
   * @param t
   *          the T.
   * @param f
   *          the F.
   * @param w
   *          the W.
   */
  public void add(R r, T t, Float f, Double w) {
    super.add(r, t);
    chargeEstimates.put(r, f);
    waitTimes.put(r, w);
  }

  /**
   * Add bulk tuples.
   * 
   * @param r
   *          the R list.
   * @param t
   *          the T list.
   * @param f
   *          the F list.
   * @param w
   *          the W list.
   */
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

  /**
   * Returns the charge estimation value for r.
   * 
   * @param r
   *          the R.
   * @return a float.
   */
  public float getChargeEstimateFor(R r) {
    if (!contains(r)) {
      throw new IllegalArgumentException("No element in this itinerary.");
    }
    return chargeEstimates.get(r);
  }

  /**
   * Returns the waitingtime estimation for r.
   * 
   * @param r
   *          the R.
   * @return a double.
   */
  public double getWaitEstimateFor(R r) {
    if (!contains(r)) {
      throw new IllegalArgumentException("No element in this itinerary.");
    }
    return waitTimes.get(r);
  }
}