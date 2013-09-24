package be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration;

import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.IExplorationContext;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.util.Itinerary;
import be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral.IVehicleContext;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;

/**
 * 
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public interface IExplorationAlgorithm {

    public void initialize(IExplorationContext context);

    public Itinerary<NodeReference, VirtualTime> getSequenceOfStations(
            IVehicleContext context, VirtualTime currentTime)
            throws NoRoutePossibleException;
}
