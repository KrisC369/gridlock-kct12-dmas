package be.kuleuven.cs.gridlock.dmas.coordination.delegate.model;

import be.kuleuven.cs.gridlock.dmas.coordination.delegate.IDMASModelComponent;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration.NoRoutePossibleException;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.pheromones.IPheromone;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.util.Itinerary;
import be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral.IVehicleContext;
import be.kuleuven.cs.gridlock.simulation.SimulationComponent;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import be.kuleuven.cs.gridlock.simulation.timeframe.TimeFrameConsumer;

/**
 * API for operations DMASModel users can call on this coordination model.
 * 
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public interface IDMASModelAPI extends SimulationComponent, TimeFrameConsumer,
		IRegisterable<IDMASModelComponent> {

	/**
	 * Operation for dropping a pheromone at a specific location.
	 * 
	 * @param location
	 *            The location where the pheromone should be dropped at.
	 * @param phero
	 *            The pheromone object that needs to be dropped.
	 */
	void dropPheromone(NodeReference location, IPheromone phero);

	/**
	 * Retunrs the sequence of stations suggested by this model.
	 * 
	 * @param context
	 *            The vehicle context.
	 * @param currentTime
	 *            The current time.
	 * @return An itinerary instance.
	 * @throws NoRoutePossibleException
	 *             when no route or itinerary is possible.
	 */
	Itinerary<NodeReference, VirtualTime> getSequenceOfStations(
			IVehicleContext context, VirtualTime currentTime)
			throws NoRoutePossibleException;
}
