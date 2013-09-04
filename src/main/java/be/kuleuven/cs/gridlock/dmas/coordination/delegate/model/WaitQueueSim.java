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
 *
 * @author kristof.coninx ,AT> cs.kuleuven.be
 */
public final class WaitQueueSim {

    private Set<VirtualTime> events;
    private List<VirtualTime> starts;
    private List<VirtualTime> origStarts;
    private List<VirtualTime> ends;
    private int cap;

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

    protected DMASModel.ReturnAggr simulateWaiting(VirtualTime stopMark) {
        int i = 0;
        PriorityQueue<VirtualTime> q = new PriorityQueue<VirtualTime>();
        List<VirtualTime> retA = new ArrayList<VirtualTime>();
        List<VirtualTime> retB = new ArrayList<VirtualTime>();
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
            return new DMASModel.ReturnAggr(retA, retB);
        }
        return new DMASModel.ReturnAggr(starts, ends);
    }
}
