package be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration;

import be.kuleuven.cs.gridlock.configuration.Configuration;
import be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral.IVehicleContext;
import be.kuleuven.cs.gridlock.routing.Path;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class AntBuilder {

    public static final String TRAVELTIMECOST_KEY = "value.ant.cost.traveltime";
    public static final String WAITTIMECOST_KEY = "value.ant.cost.waittime";
    public static final String CHARGETIMECOST_KEY = "value.ant.cost.chargetime";
    private final float traveltimecost;
    private final float waittimecost;
    private final float chargetimecost;

    ExplorationAnt buildExplorationAnt(VirtualTime currentTime, IVehicleContext context, Path route) {
        long id = context.getEV().getVehicleEntity().getVehicleReference().getId();
        float currentEnergy = context.getEV().getCurrentCharge();
        float consumption = context.getEV().getConsumptionRate();
        float maxEnergy = context.getEV().getMaxEnergy();
        float requiredEnergy = calculateRequiredEnergy(context, route, consumption);
        return new ExplorationAnt(id, currentTime, currentEnergy, requiredEnergy, maxEnergy, consumption, traveltimecost, waittimecost, chargetimecost);

    }

    private static float calculateRequiredEnergy(IVehicleContext context, Path route, float consumption) {
        return (route.getLength() / consumption);
    }

    public AntBuilder(Configuration config) {
        this.traveltimecost = config.getFloat(TRAVELTIMECOST_KEY, 1);
        this.waittimecost = config.getFloat(WAITTIMECOST_KEY, 1);
        this.chargetimecost = config.getFloat(CHARGETIMECOST_KEY, 1);
    }
}
