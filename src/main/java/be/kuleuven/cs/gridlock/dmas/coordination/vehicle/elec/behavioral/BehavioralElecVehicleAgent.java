package be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral;

import be.kuleuven.cs.gridlock.communication.CommunicationManager;
import be.kuleuven.cs.gridlock.coordination.implementation.simple.SimpleVehicleAgent;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.util.Itinerary;
import be.kuleuven.cs.gridlock.dmas.coordination.vehicle.behaviors.IElecVehicleBehavior;
import be.kuleuven.cs.gridlock.routing.Path;
import be.kuleuven.cs.gridlock.routing.RoutingService;
import be.kuleuven.cs.gridlock.simulation.SimulationContext;
import be.kuleuven.cs.gridlock.simulation.api.LinkReference;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VehicleReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import be.kuleuven.cs.gridlock.simulation.model.charging.IChargeable;
import be.kuleuven.cs.gridlock.simulation.model.charging.station.StationManager;
import be.kuleuven.cs.gridlock.utilities.graph.Graph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Vehicle agent for electrical vehicles, capable of using different behavior instances.
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class BehavioralElecVehicleAgent extends SimpleVehicleAgent implements IVehicleContext, IBehavingEntity {

    //private final CommunicationManager comms;
    private List<NodeReference> passedNodes;
    private boolean onRouteToCharge;
    private boolean routeStale;
    private NodeReference currentPosition;
    private final List<IElecVehicleBehavior> behaviors;
    private List<NodeReference> advisedPath;
    private Itinerary<NodeReference, VirtualTime> itinerary;

    public BehavioralElecVehicleAgent(VehicleReference reference, SimulationContext context, RoutingService routing, CommunicationManager comms) {
        super(reference, context, routing);
        //this.comms = comms;
        this.passedNodes = new ArrayList<NodeReference>();
        this.onRouteToCharge = false;
        this.routeStale = false;
        this.behaviors = new ArrayList<IElecVehicleBehavior>();
        this.advisedPath = new ArrayList<NodeReference>();
        this.itinerary = new Itinerary<NodeReference, VirtualTime>();
    }

//    private CommunicationManager getComms() {
//        return this.comms;
//    }

    @Override
    public void consume(VirtualTime currentTime, double timeFrameDuration) {
        executeBehaviors(currentTime, timeFrameDuration);
    }

    private void executeBehaviors(VirtualTime currentTime, double timeFrameDuration) {
        for (IElecVehicleBehavior behavior : this.behaviors) {
            behavior.executeBehavior(currentTime, timeFrameDuration, this);
        }
    }

    @Override
    public boolean continueSimulation() {
        return false;
    }

    @Override
    public void updatePosition(NodeReference nodeReference) {
        this.passedNodes.add(nodeReference);
        this.setCurrentPosition(nodeReference);
        this.setRouteStale(true);
        if (itinerary.contains(nodeReference)) {
            this.setOnRouteToCharge(false);
            itinerary.remove(nodeReference);
        }

    }

    @Override
    public IChargeable getEV() {
        try {
            return (IChargeable) (this.getVehicle());
        } catch (ClassCastException cce) {
            return null;
        }
    }

    @Override
    public StationManager getStationManager() {
        return this.getContext().getSimulationComponent(StationManager.class);
    }

    private void setRouteStale(boolean stale) {
        this.routeStale = stale;
    }

    @Override
    public void scheduleChargingStop(NodeReference stationDst) {
        //getVehicle().setDestination(stationDst);
        getEV().setDestinationToChargeAt(stationDst);
        setOnRouteToCharge(true);
        routeRefreshed();
    }

    @Override
    public void resetChargingStop() {
        this.getEV().resetDestinationBeforeCharging();
        setOnRouteToCharge(false);
    }

    private void setOnRouteToCharge(boolean b) {
        this.onRouteToCharge = b;
    }

    @Override
    public boolean isOnRouteToCharge() {
        return this.onRouteToCharge;
    }

    @Override
    public boolean isRouteStale() {
        return this.routeStale;
    }

    @Override
    public NodeReference getCurrentPosition() {
        return currentPosition;
    }

    private void setCurrentPosition(NodeReference nodeReference) {
        this.currentPosition = nodeReference;
    }

    @Override
    public Path calculateRouteReference(NodeReference position) {
        return this.calculateRoute(position);
    }

    @Override
    public Graph<NodeReference, LinkReference> getGraphReference() {
        return this.getGraph();
    }

    @Override
    public List<NodeReference> getPassedNodes() {
        return this.passedNodes;
    }

    @Override
    public void addBehavior(IElecVehicleBehavior behavior) {
        this.behaviors.add(behavior);
    }

    @Override
    public Collection<IElecVehicleBehavior> getBehaviors() {
        return new ArrayList<IElecVehicleBehavior>(this.behaviors);
    }

    @Override
    public void replaceBehaviorInstance(IElecVehicleBehavior behavior) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAdvisedPath(List<NodeReference> sequence) {
        this.advisedPath = sequence; //TODO Not used?
    }

    @Override
    public List<NodeReference> getAdvisedPath() {
        return new ArrayList<NodeReference>(this.advisedPath); //TODO not used?
    }

    @Override
    public NodeReference getChargingStop() {
        if (isOnRouteToCharge()) {
            return this.getEV().getCurrentDestination();
        }
        return null;
    }

    @Override
    public Itinerary<NodeReference, VirtualTime> getItinerary() {
        return this.itinerary;
    }

    @Override
    public void setItinerary(Itinerary<NodeReference, VirtualTime> it) {
        this.itinerary = it;
    }

    @Override
    public void routeRefreshed() {
        setRouteStale(false);
    }
}
