package be.kuleuven.cs.gridlock.dmas.coordination.vehicle.behaviors;

import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.IDMASModelAPI;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.pheromones.PheromoneFactory;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.util.ChargeAwareItinerary;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.util.Itinerary;
import be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral.IVehicleContext;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import be.kuleuven.cs.gridlock.simulation.events.EventController;
import java.lang.IllegalStateException;

/**
 * Reference behavior to compare DMAS solution with.
 * Behavior chooses the least occupied station within range.
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class IntentionDMASBehavior implements IElecVehicleBehavior {

    private long timeoutcounter;
    private int period;
    private IDMASModelAPI api;
    private PheromoneFactory factory;
    private double pessimism;
    private final double MAXTIME = 1800;
    private EventController eventcont;

    public IntentionDMASBehavior(int period, IDMASModelAPI api, PheromoneFactory fact, double pessimism, EventController eventcontroller) {
        this.period = period;
        this.timeoutcounter = 0;
        this.api = api;
        this.factory = fact;
        this.pessimism = pessimism;
        this.eventcont = eventcontroller;
    }

    @Override
    public void executeBehavior(VirtualTime currentTime, double timeFrameDuration, IVehicleContext context) {
        if (context.getEV().isChargin() || context.getEV().isQueuedForCharging()) {
            doOnSiteIntentionStep(context, currentTime);
        } else if (timeoutcounter++ % period == 0) {
            doIntentionStep(context, currentTime);
        }
    }

    private void doIntentionStep(IVehicleContext context, VirtualTime currentTime) {
        NodeReference ref = context.getEV().getCurrentDestination();
        if (context.isOnRouteToCharge() && context.getItinerary().contains(ref)) {
            VirtualTime arr = estimateArrival(ref, context, currentTime);
            VirtualTime depp = estimateDeparture(ref, context, arr);
            api.dropPheromone(ref, factory.createIntentionPheromone(context.getEV().getVehicleEntity().getVehicleReference().getId(), ref, arr, depp));
        }
    }

    private VirtualTime estimateArrival(NodeReference ref, IVehicleContext context, VirtualTime currentTime) {
        Itinerary<NodeReference, VirtualTime> itinerary = context.getItinerary();
        if (itinerary == null) {
            throw new IllegalStateException();
        }
        return VirtualTime.createVirtualTime(itinerary.getTimeFor(ref).getSeconds());
    }

    private VirtualTime estimateDeparture(NodeReference ref, IVehicleContext context, VirtualTime currentTime) {
        Itinerary<NodeReference, VirtualTime> itinerary = context.getItinerary();
        try {
            float charge = ((ChargeAwareItinerary) itinerary).getChargeEstimateFor(ref);
            //double wait = ((ChargeAwareItinerary) itinerary).getWaitEstimateFor(ref);
            float needed = context.getEV().getMaxEnergy() - charge;
            double secondsToWait = (double) ((needed / (context.getStationManager().getStation(new NodeReference((Long) ref.getId())).getChargeRate())) * 3600);
            double totalDelta = weighedAvg(secondsToWait/*+wait*/);
            eventcont.publishEvent("intention:charging:estimate", "time",currentTime, "vehicle", context.getEV().getVehicleEntity().getVehicleReference().getId(),
                    "estimate",VirtualTime.createVirtualTime(secondsToWait));
            
            //System.out.println(totalDelta);
            return currentTime.add(totalDelta);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
        //return currentTime.add(30 * 60);//TODO test guessing of chargingtime.
    }

    private double weighedAvg(double d) {
        return ((this.MAXTIME * this.pessimism) + ((1 - this.pessimism) * d));
    }

    private void doOnSiteIntentionStep(IVehicleContext context, VirtualTime currentTime) {
        NodeReference ref = context.getEV().getCurrentDestination();
        float needed = context.getEV().getMaxEnergy() - context.getEV().getCurrentCharge();
        double secondsToWait = (double) ((needed / (context.getStationManager().getStation(new NodeReference((Long) ref.getId())).getChargeRate())) * 3600);
        double totalDelta = /*weighedAvg*/(secondsToWait/*+wait*/);
        VirtualTime arr = currentTime;
        VirtualTime depp = currentTime.add(totalDelta);
        api.dropPheromone(ref, factory.createIntentionPheromone(context.getEV().getVehicleEntity().getVehicleReference().getId(), ref, arr, depp));
    }
}
