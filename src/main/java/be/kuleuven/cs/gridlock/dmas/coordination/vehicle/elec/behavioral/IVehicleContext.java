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
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public interface IVehicleContext {

    public IChargeable getEV();

    public StationManager getStationManager();

    public void resetChargingStop();

    public void scheduleChargingStop(NodeReference stationDst);

    public NodeReference getCurrentPosition();

    public boolean isOnRouteToCharge();

    public boolean isRouteStale();
    
    public void routeRefreshed();

    public Path calculateRouteReference(NodeReference position);
    
    public Graph<NodeReference, LinkReference> getGraphReference();

    public List<NodeReference> getPassedNodes();
 
    @Deprecated
    public void setAdvisedPath(List<NodeReference> sequence); 
    
    @Deprecated
    public List<NodeReference> getAdvisedPath();

    public NodeReference getChargingStop();
    
    public Itinerary<NodeReference,VirtualTime> getItinerary();
    
    public void setItinerary(Itinerary<NodeReference,VirtualTime> it);
}
