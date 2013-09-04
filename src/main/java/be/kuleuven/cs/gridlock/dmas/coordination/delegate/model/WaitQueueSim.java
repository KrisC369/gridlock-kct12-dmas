package be.kuleuven.cs.gridlock.dmas.coordination.delegate.model;

import be.kuleuven.cs.gridlock.dmas.coordination.delegate.pheromones.IRegistration;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

/**
 * Simulates waiting queues for the dmas Model.
 *
 * @author kristof.coninx ,AT> cs.kuleuven.be
 */
public final class WaitQueueSim {

    private final Set<VirtualTime> events;
    private final List<VirtualTime> starts;
    private final List<VirtualTime> origStarts;
    private final List<VirtualTime> ends;
    private final int cap;

    /**
     * Constructor for a waitsim instance.
     *
     * @param events all the registrations events.
     * @param cap The total capacity of the queue.
     */
    protected WaitQueueSim(Collection<IRegistration> events, int cap) {
        this.events = new TreeSet<VirtualTime>();
        this.starts = new ArrayList<VirtualTime>();
        this.origStarts = new ArrayList<VirtualTime>();
        this.ends = new ArrayList<VirtualTime>();
        this.cap = cap;
        for (IRegistration t : events) {
            this.events.add(t.getArrivalTime());
            this.events.add(t.getDepartureTime());
            this.starts.add(t.getArrivalTime());
            this.origStarts.add(t.getArrivalTime());
            this.ends.add(t.getDepartureTime());
        }
    }

    /**
     * Run a simulation run.
     *
     * @param stopMark mark to stop the simulation at.
     * @return a set of returnAggregated results.
     */
    protected ReturnAggr simulateWaiting(VirtualTime stopMark) {
        int i = 0;
        final PriorityQueue<VirtualTime> q = new PriorityQueue<VirtualTime>();
        final List<VirtualTime> retA = new ArrayList<VirtualTime>();
        final List<VirtualTime> retB = new ArrayList<VirtualTime>();
        if (starts.size() > cap) {
            //i=cap-1;
            int count = 0;
            while (i < starts.size() && origStarts.get(i).compareTo(stopMark) < 0) {
                while (count < cap && i < starts.size()) {
                    retA.add(starts.get(i));
                    retB.add(ends.get(i));
                    q.add(ends.get(i));
                    i++;
                    count++;
                }
                VirtualTime nextEnd = q.poll();
                count--;
                for (int k = i; k < starts.size(); k++) {
                    if (starts.get(k).compareTo(nextEnd) < 0) {
                        double newS = nextEnd.sub(starts.get(k)).getSeconds();
                        starts.set(k, nextEnd);
                        ends.set(k, ends.get(k).add(newS));
                    }
                }
            }
            return new ReturnAggr(retA, retB);
        }
        return new ReturnAggr(starts, ends);
    }

    /**
     * Static wrapper class aggregating the simresults.
     */
    public static class ReturnAggr {

        private List<VirtualTime> arrival;
        private List<VirtualTime> departure;

        public ReturnAggr(List<VirtualTime> arrival, List<VirtualTime> departure) {
            this.arrival = arrival;
            this.departure = departure;
        }

        public List<VirtualTime> getDeparture() {
            return departure;
        }

        public List<VirtualTime> getArrival() {
            return arrival;
        }
    }
}
