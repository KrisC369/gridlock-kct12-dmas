package be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.simple;

import be.kuleuven.cs.gridlock.communication.CommunicationManager;
import be.kuleuven.cs.gridlock.coordination.implementation.simple.SimpleVehicleAgent;
import be.kuleuven.cs.gridlock.routing.RoutingService;
import be.kuleuven.cs.gridlock.simulation.SimulationContext;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VehicleReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import be.kuleuven.cs.gridlock.simulation.model.charging.IChargeable;
import be.kuleuven.cs.gridlock.simulation.model.charging.station.StationManager;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class SimpleElecVehicleAgent extends SimpleVehicleAgent {

    private final CommunicationManager comms;
    private Set<NodeReference> passedNodes;
    private boolean onRouteToCharge;
    private boolean routeStale;
    private NodeReference currentPosition;

    public SimpleElecVehicleAgent(VehicleReference reference, SimulationContext context, RoutingService routing, CommunicationManager comms) {
        super(reference, context, routing);
        this.comms = comms;
        this.passedNodes = new HashSet<NodeReference>();
        this.onRouteToCharge = false;
        this.routeStale = false;
    }

    private IChargeable getEV() {
        try {
            return (IChargeable) (this.getVehicle());
        } catch (ClassCastException cce) {
            return null;
        }
    }

    private CommunicationManager getComms() {
        return this.comms;
    }

    @Override
    public void consume(VirtualTime currentTime, double timeFrameDuration) {
        if (!isOnRouteToCharge() && isRouteStale() && getCurrentPosition() != null) {
            chargingBehavior(getCurrentPosition());
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
        this.setOnRouteToCharge(false);

    }

    private void chargingBehavior(NodeReference position) {
        float ar = this.getEV().getActionRadius() / 1000; //convert m to km.
        float distance = (float) this.calculateRoute(position).resolveDistance(getGraph());
        if (distance > ar) {
            NodeReference stationDst = getStationManager().findClosestStationTo(position, passedNodes.toArray(new NodeReference[passedNodes.size()]));
            if (stationDst != null) {
                scheduleChargingStop(stationDst);
            } else {
                resetChargingStop();
            }
        } else {
            resetChargingStop();
        }
    }

    private StationManager getStationManager() {
        return this.getContext().getSimulationComponent(StationManager.class);
    }

    private void setRouteStale(boolean stale) {
        this.routeStale = stale;
    }

    private void scheduleChargingStop(NodeReference station) {
        //getVehicle().setDestination(station);
        getEV().setDestinationToChargeAt(station);
        setOnRouteToCharge(true);
    }

    private void resetChargingStop() {
        this.getEV().resetDestinationBeforeCharging();
        setOnRouteToCharge(false);
    }

    private void setOnRouteToCharge(boolean b) {
        this.onRouteToCharge = b;
    }

    private boolean isOnRouteToCharge() {
        return this.onRouteToCharge;
    }

    private boolean isRouteStale() {
        return this.routeStale;
    }

    private NodeReference getCurrentPosition() {
        return currentPosition;
    }

    private void setCurrentPosition(NodeReference nodeReference) {
        this.currentPosition = nodeReference;
    }
}