package be.kuleuven.cs.gridlock.dmas.coordination.delegate.model;

import be.kuleuven.cs.gridlock.configuration.services.ServiceFactory;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.IDMASModelComponent;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.WaitQueueSim.ReturnAggr;
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
import java.util.Set;

/**
 * An implementation of the DMASAPI in a DMASModel.
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class DMASModel implements IDMASModelAPI, IExplorationContext {

    //TODO clean up not using setIpheromone for registrations. This solution delegates to DMASModelComponents.
    private final long unusedVehRef;
    private final Map<NodeReference, IDMASModelComponent> compLoc;
    private final IExplorationAlgorithm exploration;
    private RoutingService routing;
    private final Graph<NodeReference, LinkReference> graph;

    protected DMASModel(Graph<NodeReference, LinkReference> graph,
            IExplorationAlgorithm exploration) {
        this.compLoc = new HashMap<NodeReference, IDMASModelComponent>();
        this.exploration = exploration;
        this.graph = graph;
        unusedVehRef = -1l;
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
        return getRegistrations(ref, new VehicleReference(unusedVehRef));
    }

    private Set<IRegistration> getRegistrations(NodeReference ref, VehicleReference vref) {
        final Set<IPheromone> phers = getComponent(ref).getRegistrations();
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
        return internalWaitingTimeForSpot(totalChargingSpots, travelTime, asSortedList(getRegistrations(ref)));

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
        return internalWaitingTimeForSpot(totalChargingSpots, travelTime, asSortedList(getRegistrations(ref, vehicleReference)));
    }

    private VirtualTime internalWaitingTimeForSpot(int totalChargingSpots, VirtualTime travelTime, List<IRegistration> regs) {

        if (regs.size() < totalChargingSpots) {
            return VirtualTime.createVirtualTime(0);
        }
        final WaitQueueSim wqs = new WaitQueueSim(regs, totalChargingSpots);
        ReturnAggr simulateWaiting = wqs.simulateWaiting(travelTime);
        List<VirtualTime> dep = simulateWaiting.getDeparture();
        if (dep.isEmpty()) {
            return VirtualTime.createVirtualTime(0);
        }
        double waitsecs = dep.get(dep.size() - 1).sub(travelTime).getSeconds();
        if (waitsecs < 0) {
            return VirtualTime.createVirtualTime(0);
        }
        return VirtualTime.createVirtualTime(waitsecs);
    }
}
