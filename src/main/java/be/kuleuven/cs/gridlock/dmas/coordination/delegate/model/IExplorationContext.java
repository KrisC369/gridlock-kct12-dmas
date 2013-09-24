package be.kuleuven.cs.gridlock.dmas.coordination.delegate.model;

import be.kuleuven.cs.gridlock.dmas.coordination.delegate.IDMASModelComponent;
import be.kuleuven.cs.gridlock.routing.RoutingService;
import be.kuleuven.cs.gridlock.simulation.api.LinkReference;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VehicleReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import be.kuleuven.cs.gridlock.utilities.graph.Graph;

/**
 * The interface of a vehicle context specification in a dmas model.
 * 
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public interface IExplorationContext {

    /**
     * Returns the model component reference.
     * 
     * @param ref
     *            A nodereference instance
     * @return An IDMAS model component instance.
     */
    IDMASModelComponent getComponent(NodeReference ref);

    /**
     * Default getter.
     * 
     * @return the routing service.
     */
    RoutingService getRouting();

    /**
     * Default getter.
     * 
     * @return the graph instance.
     */
    Graph<NodeReference, LinkReference> getGraph();

    /**
     * Returns the waiting time for a certain spot at a node.
     * 
     * @param ref
     * @param ref
     *            the node ref to check.
     * @param totalChargingSpots
     *            the amount of charging spots available at the node.
     * @param travelTime
     *            The travel time to arrival.
     * @param vehicleReference
     *            the vehiclereference that performs the query.
     * @return A timeduration.
     */
    VirtualTime getWaitingTimeForSpot(NodeReference ref,
            int totalChargingSpots, VirtualTime travelTime,
            VehicleReference vehicleReference);
}
