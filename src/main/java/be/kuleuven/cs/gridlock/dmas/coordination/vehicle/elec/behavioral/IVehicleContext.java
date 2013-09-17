package be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral;

import be.kuleuven.cs.gridlock.dmas.coordination.delegate.util.Itinerary;
import be.kuleuven.cs.gridlock.routing.Path;
import be.kuleuven.cs.gridlock.simulation.api.LinkReference;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import be.kuleuven.cs.gridlock.simulation.model.charging.IChargeable;
import be.kuleuven.cs.gridlock.simulation.model.charging.station.StationManager;
import be.kuleuven.cs.gridlock.utilities.graph.Graph;
import java.util.List;

/**
 * Vehicle context interface for vehicle behaviors.
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public interface IVehicleContext {

    /**
     * Returns the EV instance.
     *
     * @return an IChargeable instance
     */
    IChargeable getEV();

    /**
     * Returns the station manager.
     *
     * @return a stationmanager instance.
     */
    StationManager getStationManager();

    /**
     * Resets the charging stop.
     */
    void resetChargingStop();

    /**
     * Schedule a charging stop at a station.
     *
     * @param stationDst a noderef representing a station.
     */
    void scheduleChargingStop(NodeReference stationDst);

    /**
     * Return current postition.
     *
     * @return a nodereference.
     */
    NodeReference getCurrentPosition();

    /**
     * Returns if on route to charging destination.
     *
     * @return boolean.
     */
    boolean isOnRouteToCharge();

    /**
     * Returns if route should be refreshed or not.
     *
     * @return boolean.
     */
    boolean isRouteStale();

    /**
     * Notify instance that the route has been refreshed.
     */
    void routeRefreshed();

    /**
     * Calculate a new path to a set destination.
     *
     * @param position the initial position to route from.
     * @return a path.
     */
    Path calculateRouteReference(NodeReference position);

    /**
     * Get the graph representation reference.
     *
     * @return a graph instance.
     */
    Graph<NodeReference, LinkReference> getGraphReference();

    /**
     * Returns a list of passed nodes.
     *
     * @return a list of noderefs.
     */
    List<NodeReference> getPassedNodes();

    /**
     * Sets the path manually.
     *
     * @param sequence the sequence of nodes representing the path.
     * @deprecated use itinerary methods instead.
     */
    @Deprecated
    void setAdvisedPath(List<NodeReference> sequence);

    /**
     * Returns the advised path to take.
     *
     * @return a list of noderefs.
     * @deprecated use itinerary methods instead.
     */
    @Deprecated
    List<NodeReference> getAdvisedPath();

    /**
     * Returns the node planned to charge at.
     *
     * @return a node or null if not planning to charge.
     */
    NodeReference getChargingStop();

    /**
     * Returns the itinerary of this vehicle.
     *
     * @return an itinerary instance.
     */
    Itinerary<NodeReference, VirtualTime> getItinerary();

    /**
     * Set a new itinerary for this object.
     *
     * @param an itinerary instance.
     */
    void setItinerary(Itinerary<NodeReference, VirtualTime> it);
}
