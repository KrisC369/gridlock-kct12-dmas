package be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral.mixed;

import be.kuleuven.cs.gridlock.configuration.Configuration;
import be.kuleuven.cs.gridlock.configuration.services.TypedBasedServiceFactory;
import be.kuleuven.cs.gridlock.coordination.vehicle.VehicleAgentFactory;
import be.kuleuven.cs.gridlock.coordination.vehicle.VehicleAgentFactoryLoader;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class MixedBehavioralElecVehicleAgentFactoryLoader extends TypedBasedServiceFactory<VehicleAgentFactory> implements VehicleAgentFactoryLoader {

    public MixedBehavioralElecVehicleAgentFactoryLoader() {
        super(VehicleAgentFactoryLoader.AGENT_TYPE_CONFIGURATION_KEY, "behavioral-mixed");
    }

    @Override
    public VehicleAgentFactory buildService(Configuration configuration) {
        return new MixedBehavioralElecVehicleAgentBuilder();
    }
}
