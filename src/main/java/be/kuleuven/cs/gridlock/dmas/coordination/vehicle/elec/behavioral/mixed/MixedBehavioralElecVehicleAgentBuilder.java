package be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral.mixed;

import be.kuleuven.cs.gridlock.coordination.vehicle.VehicleAgent;
import be.kuleuven.cs.gridlock.coordination.vehicle.VehicleAgentFactory;
import be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral.BehavioralElecVehicleAgentBuilder;
import be.kuleuven.cs.gridlock.simulation.SimulationContext;
import be.kuleuven.cs.gridlock.simulation.api.VehicleReference;
import java.util.List;
import java.util.ServiceConfigurationError;
import org.apache.commons.math3.random.RandomData;

/**
 * Builder for creating behavioral vehicle agents based on the configuration.
 * This builder alternates the building proces between two sets of behavioral configurations
 * based on the settings in the config file.
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class MixedBehavioralElecVehicleAgentBuilder extends BehavioralElecVehicleAgentBuilder implements VehicleAgentFactory {

    private RandomData random;
    private int behaviorSetting1;
    private int behaviorSetting2;
    private double mixratio;
    public static final String RATIOKEY = "coordination.vehicle.behavior.ratio";
    public static final String CONFIGKEY = "coordination.vehicle.behavior.config";
    private String EVFACTORY_RANDOM_SEED_KEY = "gridlock.random.behaviormix.seed";
    private float SEED;

    @Override
    public void initialize(SimulationContext simulationContext) {
        super.initialize(simulationContext);
        this.SEED = simulationContext.getConfiguration().getFloat(EVFACTORY_RANDOM_SEED_KEY, 325);
        this.random = simulationContext.getRandomNumberManager().randomSourceFor(SEED);

        //this.behaviorSettings = this.context.getConfiguration().getInt("coordination.vehicle.behavior.config", 0);
        List<?> tmplist = simulationContext.getConfiguration().getList(CONFIGKEY);
        if (tmplist.size() != 2) {
            throw new ServiceConfigurationError("Mixing only possible between 2 behavior configs.");
        }
        this.behaviorSetting1 = Integer.parseInt(tmplist.get(0).toString());
        this.behaviorSetting2 = Integer.parseInt(tmplist.get(1).toString());
        this.mixratio = simulationContext.getConfiguration().getDouble(RATIOKEY, 1);

    }

    @Override
    public VehicleAgent createAgent(VehicleReference vehicle) {
        int behavior;
        if (this.random.nextUniform(0, 1) < mixratio) {
            behavior = this.behaviorSetting1;
        } else {
            behavior = this.behaviorSetting2;
        }
        return createAgentByBehaviorSettings(vehicle, behavior);
    }
}