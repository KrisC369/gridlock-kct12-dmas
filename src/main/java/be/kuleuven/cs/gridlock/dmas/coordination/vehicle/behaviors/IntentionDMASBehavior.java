package be.kuleuven.cs.gridlock.dmas.coordination.vehicle.behaviors;

import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.IDMASModelAPI;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.pheromones.PheromoneFactory;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.util.ChargeAwareItinerary;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.util.Itinerary;
import be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral.IVehicleContext;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import be.kuleuven.cs.gridlock.simulation.events.EventController;

/**
 * Reference behavior to compare DMAS solution with. Behavior chooses the least
 * occupied station within range.
 * 
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class IntentionDMASBehavior implements IElecVehicleBehavior {

    private static final double MAXTIME = 1800;
    private static final double TIMECONVERSIONRATE = 3600;
    private long timeoutcounter;
    private final int period;
    private final IDMASModelAPI api;
    private final PheromoneFactory factory;
    private final double pessimism;
    private final EventController eventcont;

    /**
     * Default constructor.
     * 
     * @param period
     *            The period to send intentions with.
     * @param api
     *            The api to use.
     * @param fact
     *            The factory to get pheromones from.
     * @param pessimism
     *            The pessimism rate.
     * @param eventcontroller
     *            The eventcontroller to use.
     */
    public IntentionDMASBehavior(int period, IDMASModelAPI api,
	    PheromoneFactory fact, double pessimism,
	    EventController eventcontroller) {
	this.period = period;
	this.timeoutcounter = 0;
	this.api = api;
	this.factory = fact;
	this.pessimism = pessimism;
	this.eventcont = eventcontroller;
    }

    @Override
    public void executeBehavior(VirtualTime currentTime,
	    double timeFrameDuration, IVehicleContext context) {
	if (context.getEV().isChargin()
		|| context.getEV().isQueuedForCharging()) {
	    doOnSiteIntentionStep(context, currentTime);
	} else if (timeoutcounter++ % period == 0) {
	    doIntentionStep(context, currentTime);
	}
    }

    private void doIntentionStep(IVehicleContext context,
	    VirtualTime currentTime) {
	NodeReference ref = context.getEV().getCurrentDestination();
	if (context.isOnRouteToCharge() && context.getItinerary().contains(ref)) {
	    VirtualTime arr = estimateArrival(ref, context, currentTime);
	    VirtualTime depp = estimateDeparture(ref, context, arr);
	    api.dropPheromone(ref, factory.createIntentionPheromone(context
		    .getEV().getVehicleEntity().getVehicleReference().getId(),
		    ref, arr, depp));
	}
    }

    private VirtualTime estimateArrival(NodeReference ref,
	    IVehicleContext context, VirtualTime currentTime) {
	Itinerary<NodeReference, VirtualTime> itinerary = context
		.getItinerary();
	if (itinerary == null) {
	    throw new IllegalStateException();
	}
	return VirtualTime.createVirtualTime(itinerary.getTimeFor(ref)
		.getSeconds());
    }

    private VirtualTime estimateDeparture(NodeReference ref,
	    IVehicleContext context, VirtualTime currentTime) {
	Itinerary<NodeReference, VirtualTime> itinerary = context
		.getItinerary();
	try {
	    float charge = ((ChargeAwareItinerary<NodeReference, VirtualTime>) itinerary)
		    .getChargeEstimateFor(ref);
	    float needed = context.getEV().getMaxEnergy() - charge;
	    double secondsToWait = (needed / (context.getStationManager()
		    .getStation(new NodeReference(ref.getId())).getChargeRate()))
		    * TIMECONVERSIONRATE;
	    double totalDelta = weighedAvg(secondsToWait/* +wait */);
	    eventcont.publishEvent("intention:charging:estimate", "time",
		    currentTime, "vehicle", context.getEV().getVehicleEntity()
			    .getVehicleReference().getId(), "estimate",
		    VirtualTime.createVirtualTime(secondsToWait));

	    return currentTime.add(totalDelta);
	} catch (Exception e) {
	    throw new IllegalArgumentException(e);
	}
    }

    private double weighedAvg(double d) {
	return (MAXTIME * this.pessimism) + ((1 - this.pessimism) * d);
    }

    private void doOnSiteIntentionStep(IVehicleContext context,
	    VirtualTime currentTime) {
	NodeReference ref = context.getEV().getCurrentDestination();
	float needed = context.getEV().getMaxEnergy()
		- context.getEV().getCurrentCharge();
	double secondsToWait = (needed / (context.getStationManager()
		.getStation(new NodeReference(ref.getId())).getChargeRate()))
		* TIMECONVERSIONRATE;
	double totalDelta = /* weighedAvg */secondsToWait/* +wait */;
	VirtualTime arr = currentTime;
	VirtualTime depp = currentTime.add(totalDelta);
	api.dropPheromone(ref, factory.createIntentionPheromone(context.getEV()
		.getVehicleEntity().getVehicleReference().getId(), ref, arr,
		depp));
    }
}
