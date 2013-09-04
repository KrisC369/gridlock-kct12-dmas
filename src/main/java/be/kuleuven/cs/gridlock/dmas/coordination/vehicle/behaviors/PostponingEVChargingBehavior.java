package be.kuleuven.cs.gridlock.dmas.coordination.vehicle.behaviors;

import be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral.IVehicleContext;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class PostponingEVChargingBehavior implements IElecVehicleBehavior {

    private double minPercentToCharge;

    public PostponingEVChargingBehavior(double percent) {
        this.minPercentToCharge = percent;
    }

    @Override
    public void executeBehavior(VirtualTime currentTime, double timeFrameDuration, IVehicleContext context) {
        if (!context.isOnRouteToCharge() && context.isRouteStale() && context.getCurrentPosition() != null) {
            chargingBehavior(context.getCurrentPosition(), context);
        }
    }

    private void chargingBehavior(NodeReference position, IVehicleContext context) {
        float ar = context.getEV().getActionRadius() / 1000; //convert m to km.
        float distance = (float) context.calculateRouteReference(position).resolveDistance(context.getGraphReference());
        double percentage = context.getEV().getChargePercentage();
        if ((percentage <= minPercentToCharge) && (distance > ar)) {
            NodeReference stationDst = context.getStationManager().findClosestStationTo(position, context.getPassedNodes().toArray(new NodeReference[context.getPassedNodes().size()]));
            if (stationDst != null) {
                context.scheduleChargingStop(stationDst);
                //test to see if station isn't further away than destination.
                float newdistance = (float) context.calculateRouteReference(position).resolveDistance(context.getGraphReference());
                if (newdistance > distance) {
                    context.resetChargingStop();
                }
            } else {
                context.resetChargingStop();
            }
        } else {
            context.resetChargingStop();
        }
    }
}
