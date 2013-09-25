package be.kuleuven.cs.gridlock.dmas.coordination.delegate.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;

/**
 * Utility class for representing itineraries. Basically just a list and map
 * combination.
 * 
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class Itinerary<R, T extends VirtualTime> implements Iterable<T> {

  private final List<R> list;
  private final Map<R, T> map;
  private int index;
  private int count;
  private final Map<R, Boolean> been;

  /**
   * Default constructor
   */
  public Itinerary() {
    this.list = new ArrayList<>();
    this.map = new HashMap<>();
    this.been = new HashMap<>();
    index = 0;
    count = -1;
  }

  /**
   * Get the timestamp for R.
   * 
   * @param arg
   *          the R instance
   * @return a timestamp T
   */
  public T getTimeFor(R arg) {
    if (!list.contains(arg)) {
      throw new IllegalArgumentException("this argument is not present in list");
    }
    return map.get(arg);
  }

  /**
   * Get the timestamp for an index.
   * 
   * @param idx
   *          the index.
   * @return a timestamp.
   */
  public T getTimeFor(int idx) {
    return getTimeFor(list.get(idx));
  }

  /**
   * Add pair of R-T.
   * 
   * @param r
   *          the R.
   * @param t
   *          the T.
   */
  public void add(R r, T t) {
    list.add(r);
    map.put(r, t);
    been.put(r, false);
    count++;
  }

  /**
   * Add lists of matching pairs.
   * 
   * @param r
   *          the list of R elems.
   * @param t
   *          the list of matching T elems.
   */
  public void addAll(List<R> r, List<T> t) {
    if (r.size() != t.size()) {
      throw new IllegalArgumentException("arguments cannot differ in size");
    }
    for (int i = 0; i < r.size(); i++) {
      this.add(r.get(i), t.get(i));
    }
  }

  /**
   * Return all keys R.
   * 
   * @return a list of R.
   */
  public List<R> getKeys() {
    return new ArrayList<>(list);
  }

  /**
   * Returns if empty
   * 
   * @return boolean.
   */
  public boolean isEmpty() {
    return list.isEmpty();
  }

  /**
   * Returns the size of itinerary.
   * 
   * @return integer size.
   */
  public int size() {
    return list.size();
  }

  /**
   * Rreturn the R matching index.
   * 
   * @param idx
   *          the index.
   * @return an R.
   */
  public R get(int idx) {
    return list.get(idx);
  }

  /**
   * Return the next R in line.
   * 
   * @return an R.
   */
  public R get() {
    R r = list.get(index++);
    index %= count;
    return r;
  }

  /**
   * Returns whether R is present in hasbeen list.
   * 
   * @param r
   *          the R.
   * @return boolean.
   */
  public boolean hasBeen(R r) {
    return been.get(r);
  }

  /**
   * Put an R in the hasbeen list.
   * 
   * @param r
   *          the R.
   */
  public void setVisited(R r) {
    if (!list.contains(r)) {
      throw new IllegalArgumentException();
    }
    this.been.put(r, true);
  }

  /**
   * Returns first R not in hasbeen list
   * 
   * @return an R.
   * @throws NoUnvisitedElementsAvailableException
   *           If no unvisted elements are left.
   */
  public R getFirstUnVisited() throws NoUnvisitedElementsAvailableException {
    for (R r : list) {
      if (!hasBeen(r)) {
        return r;
      }
    }
    throw new NoUnvisitedElementsAvailableException();
  }

  /**
   * Returns the first R.
   * 
   * @return an R.
   */
  public R getFirst() {
    if (list.isEmpty()) {
      throw new IllegalStateException("Itinerary is empty");
    }
    return list.get(0);
  }

  /**
   * Simple Contains operation.
   * 
   * @param r
   *          the R.
   * @return a boolean.
   */
  public boolean contains(R r) {
    return list.contains(r);
  }

  /**
   * Simple remove operation.
   * 
   * @param r
   */
  public void remove(R r) {
    if (!list.contains(r)) {
      throw new IllegalArgumentException();
    }
    list.remove(r);
    map.remove(r);
    been.remove(r);
  }

  /**
   * Simple iterator operation without remove support.
   */
  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {

      private int idx = 0;

      @Override
      public boolean hasNext() {
        return idx < list.size();
      }

      @Override
      public T next() {
        return map.get(list.get(idx++));
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
      }
    };
  }

  /**
   * Static inner exception class.
   * 
   * @author kristofc
   */
  public static class NoUnvisitedElementsAvailableException extends Exception {

    private static final long serialVersionUID = 7405645171570594188L;
  }
}
