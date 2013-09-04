package be.kuleuven.cs.gridlock.dmas.coordination.delegate.model;

import be.kuleuven.cs.gridlock.configuration.services.ServiceFactory;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.IDMASModelComponent;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration.IExplorationAlgorithm;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration.NoRoutePossibleException;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.pheromones.IPheromone;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.pheromones.IRegistration;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.util.Itinerary;
import be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral.IVehicleContext;
import be.kuleuven.cs.gridlock.routing.RoutingService;
import be.kuleuven.cs.gridlock.routing.RoutingServiceLoader;
import be.kuleuven.cs.gridlock.simulation.SimulationComponent;
import be.kuleuven.cs.gridlock.simulation.SimulationContext;
import be.kuleuven.cs.gridlock.simulation.api.LinkReference;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VehicleReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import be.kuleuven.cs.gridlock.simulation.timeframe.TimeFrameConsumer;
import be.kuleuven.cs.gridlock.utilities.graph.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

/**
 * An implementation of the DMASAPI in a DMASModel.
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class DMASModel implements IDMASModelAPI, IExplorationContext {

    //TODO clean up not using setIpheromone for registrations. This solution delegates to DMASModelComponents.
    private final Map<NodeReference, IDMASModelComponent> compLoc;
    private final IExplorationAlgorithm exploration;
    private RoutingService routing;
    private final Graph<NodeReference, LinkReference> graph;

    protected DMASModel(Graph<NodeReference, LinkReference> graph,
            IExplorationAlgorithm exploration) {
        this.compLoc = new HashMap<NodeReference, IDMASModelComponent>();
        this.exploration = exploration;
        this.graph = graph;
    }

    @Override
    public void dropPheromone(NodeReference location, IPheromone phero) {
        if (!isValid(phero)) {
            throw new IllegalArgumentException("invalid pheromone");
        }
        if (getComponent(location) == null) {
            throw new IllegalArgumentException("Node not registered");
        }
        getComponent(location).dropRegistration(phero);
    }

    @Override
    public IDMASModelComponent getComponent(NodeReference ref) {
        return compLoc.get(ref);
    }

    public Set<IPheromone> getReservations(NodeReference ref) {
        return getComponent(ref).getRegistrations();
    }

    @Override
    public void initialize(SimulationContext simulationContext) {
        exploration.initialize(this);
        this.routing = ServiceFactory.Helper.load(RoutingServiceLoader.class, simulationContext.getConfiguration());
    }

    @Override
    public Collection<? extends TimeFrameConsumer> getConsumers() {
        return Collections.singleton(this);
    }

    @Override
    public Collection<? extends SimulationComponent> getSubComponents() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public void consume(VirtualTime currentTime, double timeFrameDuration) {
        evaporatePheromones(timeFrameDuration);
    }

    @Override
    public boolean continueSimulation() {
        return !getAllPheromones().isEmpty();
        //return false;
    }

    @Override
    public void register(IDMASModelComponent t) {
        if (!isValid(t)) {
            throw new IllegalArgumentException("already registered or something.");
        }
        this.compLoc.put(new NodeReference((Long) t.getInfrastructureReference().getId()), t);
    }

    @Override
    public void unregister(IDMASModelComponent t) {
        if (compLoc.containsValue(t)) {
            throw new IllegalArgumentException("Component is not registered yet.");
        }
        NodeReference toRem = null;
        for (Entry<NodeReference, IDMASModelComponent> entry : compLoc.entrySet()) {
            if (entry.getValue().equals(t)) {
                toRem = entry.getKey();
            }
        }
        compLoc.remove(toRem);
    }

    private boolean isValid(IDMASModelComponent t) {
        if (t == null) {
            return false;
        }
        if (this.compLoc.containsValue(t)) {
            return false;
        }
        return true;
    }

    private boolean isValid(IPheromone phero) {
        return true; //TODO add checks.
    }

    private void evaporatePheromones(double timeFrameDuration) {
        for (IDMASModelComponent comp : this.compLoc.values()) {
            comp.evaporateDelegate(timeFrameDuration);
        }
    }

    private boolean isValid(IRegistration t) {
        return true;
    }

    private Collection<IPheromone> getAllPheromones() {
        List<IPheromone> toRetList = new ArrayList<IPheromone>();
        for (NodeReference ref : this.compLoc.keySet()) {
            toRetList.addAll(getComponent(ref).getRegistrations());
        }
        return toRetList;
    }

    @Override
    public RoutingService getRouting() {
        return routing;
    }

    @Override
    public Graph<NodeReference, LinkReference> getGraph() {
        return graph;
    }

    private Set<IRegistration> getRegistrations(NodeReference ref) {
        Set<IPheromone> phers = getComponent(ref).getRegistrations();
        Set<IRegistration> ret = new HashSet<IRegistration>();
        for (IPheromone s : phers) {
            ret.add(s.getRegistration());
        }
        return ret;
    }

    private Set<IRegistration> getRegistrations(NodeReference ref, VehicleReference vref) {
        Set<IPheromone> phers = getComponent(ref).getRegistrations();
        Set<IRegistration> ret = new HashSet<IRegistration>();
        for (IPheromone s : phers) {
            if (s.getOriginRef() != vref.getId()) {
                ret.add(s.getRegistration());
            }
        }
        return ret;
    }

    @Override
    public VirtualTime getWaitingTimeForSpot(NodeReference ref, int totalChargingSpots, VirtualTime travelTime) {
        List<IRegistration> regs = asSortedList(getRegistrations(ref));
        if (regs.size() < totalChargingSpots) {
            return VirtualTime.createVirtualTime(0);
        }
        WaitQueueSim wqs = new WaitQueueSim(regs, totalChargingSpots);
        ReturnAggr simulateWaiting = wqs.simulateWaiting(travelTime);
        List<VirtualTime> dep = simulateWaiting.getDeparture();
        List<VirtualTime> arr = simulateWaiting.getArrival();
        if (dep.isEmpty()) {
            return VirtualTime.createVirtualTime(0);
        }
        double waitsecs = dep.get(dep.size() - 1).sub(travelTime).getSeconds();
        if (waitsecs < 0) {
            return VirtualTime.createVirtualTime(0);
        }
        return VirtualTime.createVirtualTime(waitsecs);
    }

    private <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }

    @Override
    public Itinerary<NodeReference, VirtualTime> getSequenceOfStations(IVehicleContext context, VirtualTime currentTime)
            throws NoRoutePossibleException {
        return exploration.getSequenceOfStations(context, currentTime);
    }

    @Override
    public VirtualTime getWaitingTimeForSpot(NodeReference ref, int totalChargingSpots, VirtualTime travelTime, VehicleReference vehicleReference) {
        List<IRegistration> regs = asSortedList(getRegistrations(ref, vehicleReference));
        if (regs.size() < totalChargingSpots) {
            return VirtualTime.createVirtualTime(0);
        }
        WaitQueueSim wqs = new WaitQueueSim(regs, totalChargingSpots);
        ReturnAggr simulateWaiting = wqs.simulateWaiting(travelTime);
        List<VirtualTime> dep = simulateWaiting.getDeparture();
        List<VirtualTime> arr = simulateWaiting.getArrival();
        if (dep.isEmpty()) {
            return VirtualTime.createVirtualTime(0);
        }
        double waitsecs = dep.get(dep.size() - 1).sub(travelTime).getSeconds();
        if (waitsecs < 0) {
            return VirtualTime.createVirtualTime(0);
        }
        return VirtualTime.createVirtualTime(waitsecs);
    }

    private class WaitQueueSim {

        Set<VirtualTime> events;
        List<VirtualTime> starts;
        List<VirtualTime> origStarts;
        List<VirtualTime> ends;
        int cap;
        int marker;

        public WaitQueueSim(Collection<IRegistration> events, int cap) {
            this.events = new TreeSet<VirtualTime>();
            this.starts = new ArrayList<VirtualTime>();
            this.origStarts = new ArrayList<VirtualTime>();
            this.ends = new ArrayList<VirtualTime>();
            this.marker = 0;
            this.cap = cap;
            for (IRegistration t : events) {
                this.events.add(t.getArrivalTime());
                this.events.add(t.getDepartureTime());
                this.starts.add(t.getArrivalTime());
                this.origStarts.add(t.getArrivalTime());
                this.ends.add(t.getDepartureTime());
            }
        }

        public ReturnAggr simulateWaiting(VirtualTime stopMark) {
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
                return new ReturnAggr(retA, retB);
            }
            return new ReturnAggr(starts, ends);
        }
    }

    public class ReturnAggr {

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
