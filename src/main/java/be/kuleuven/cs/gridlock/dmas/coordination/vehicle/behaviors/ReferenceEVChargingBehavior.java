package be.kuleuven.cs.gridlock.dmas.coordination.vehicle.behaviors;

import be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral.IVehicleContext;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import java.util.Collection;

/**
 * Reference behavior to compare DMAS solution with.
 * Behavior chooses the least occupied station within range.
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class ReferenceEVChargingBehavior implements IElecVehicleBehavior {

    @Override
    public void executeBehavior(VirtualTime currentTime, double timeFrameDuration, IVehicleContext context) {
        if (/*!context.isOnRouteToCharge() && */context.isRouteStale() && context.getCurrentPosition() != null) {
            chargingBehavior(context.getCurrentPosition(), context);
        }
    }

    private void chargingBehavior(NodeReference position, IVehicleContext context) {
        float ar = context.getEV().getActionRadius() / 1000; //convert m to km.
        float distance = (float) context.calculateRouteReference(position).resolveDistance(context.getGraphReference());
        NodeReference[] passedNodes = context.getPassedNodes().toArray(new NodeReference[context.getPassedNodes().size()]);
        //Collection<NodeReference> stationDsts = context.getStationManager().findAllStationsWithinRange(position, ar, passedNodes);
        Collection<NodeReference> stationDsts = context.getStationManager()
                        .findAllStationsWithinRangeOnRoute(position, context.getEV().getIntendedFinalDestination(),ar, passedNodes);

        if (distance > ar) {
            if (!stationDsts.isEmpty()) {
                setLeastOccupiedStation(context, position, stationDsts);
            } else {
                //context.resetChargingStop();
            }
        }
        context.routeRefreshed();
    }

    private void setLeastOccupiedStation(IVehicleContext context, NodeReference position, Collection<NodeReference> stationDsts) {
        NodeReference minRef = null;
        float minCharge = Float.POSITIVE_INFINITY;
        for (NodeReference statref : stationDsts) {
            float charge = context.getStationManager().getStation(statref).getCostFunction();
            if (minRef == null || (charge < minCharge)) {
                minRef = statref;
                minCharge = charge;
            }
        }

//        if (minRef == null) {
//            context.resetChargingStop();
//        } else {
            context.scheduleChargingStop(minRef);
//        }
    }
//    private void setClosestStation(IVehicleContext context, NodeReference position, Collection<NodeReference> stationDsts) {
//        NodeReference minRef = null;
//        double minLength = Double.POSITIVE_INFINITY;
//        for (NodeReference statref : stationDsts) {
//            context.scheduleChargingStop(statref);
//            double d = context.calculateRouteReference(position).resolveDistance(context.getGraphReference());
//            if (minRef == null || (d < minLength)) {
//                minRef = statref;
//                minLength = d;
//            }
//        }
//
//        if (minRef == null) {
//            context.resetChargingStop();
//        } else {
//            context.scheduleChargingStop(minRef);
//        }
//    }
}
