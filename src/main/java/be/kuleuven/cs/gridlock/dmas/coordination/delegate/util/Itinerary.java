package be.kuleuven.cs.gridlock.dmas.coordination.delegate.util;

import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Utility class for representing itineraries. Basically just a list
 * and map combination.
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class Itinerary<R, T extends VirtualTime> implements Iterable<T> {

    public static class NoUnvisitedElementsAvailableException extends Exception {
    }
    private final List<R> list;
    private final Map<R, T> map;
    private int index;
    private int count;
    private Map<R, Boolean> been;

    public Itinerary() {
        this.list = new ArrayList<>();
        this.map = new HashMap<>();
        this.been = new HashMap<>();
        index = 0;
        count = -1;
    }

    public T getTimeFor(R arg) {
        if (!list.contains(arg)) {
            throw new IllegalArgumentException("this argument is not present in list");
        }
        return map.get(arg);
    }

    public T getTimeFor(int idx) {
        return getTimeFor(list.get(idx));
    }

    public void add(R r, T t) {
        list.add(r);
        map.put(r, t);
        been.put(r, false);
        count++;
    }

    public void addAll(List<R> r, List<T> t) {
        if (r.size() != t.size()) {
            throw new IllegalArgumentException("arguments cannot differ in size");
        }
        for (int i = 0; i < r.size(); i++) {
            this.add(r.get(i), t.get(i));
        }
    }

    public List<R> getKeys() {
        return new ArrayList<>(list);
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

    public R get(int idx) {
        return list.get(idx);
    }

    public R get() {
        R r = list.get(index++);
        index %= count;
        return r;
    }

    public boolean hasBeen(R r) {
        return been.get(r);
    }

    public void setVisited(R r) {
        if (!list.contains(r)) {
            throw new IllegalArgumentException();
        }
        this.been.put(r, true);
    }

    public R getFirstUnVisited() throws NoUnvisitedElementsAvailableException {
        for (R r : list) {
            if (!hasBeen(r)) {
                return r;
            }
        }
        throw new NoUnvisitedElementsAvailableException();
    }

    public R getFirst() {
        if (list.isEmpty()) {
            throw new IllegalStateException("Itinerary is empty");
        }
        return list.get(0);
    }

    public boolean contains(R r) {
        return list.contains(r);
    }

    public void remove(R r) {
        if (!list.contains(r)) {
            throw new IllegalArgumentException();
        }
        list.remove(r);
        map.remove(r);
        been.remove(r);
    }

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
}
