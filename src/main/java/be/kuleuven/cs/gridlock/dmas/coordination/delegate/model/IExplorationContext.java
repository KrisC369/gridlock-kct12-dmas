package be.kuleuven.cs.gridlock.dmas.coordination.delegate.model;

import be.kuleuven.cs.gridlock.dmas.coordination.delegate.IDMASModelComponent;
import be.kuleuven.cs.gridlock.routing.RoutingService;
import be.kuleuven.cs.gridlock.simulation.api.LinkReference;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VehicleReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import be.kuleuven.cs.gridlock.utilities.graph.Graph;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public interface IExplorationContext {

    IDMASModelComponent getComponent(NodeReference ref);

    public VirtualTime getWaitingTimeForSpot(NodeReference ref, int totalChargingSpots, VirtualTime travelTime);

    public RoutingService getRouting();

    public Graph<NodeReference, LinkReference> getGraph();

    public VirtualTime getWaitingTimeForSpot(NodeReference ref, int totalChargingSpots, VirtualTime travelTime, VehicleReference vehicleReference);
}
