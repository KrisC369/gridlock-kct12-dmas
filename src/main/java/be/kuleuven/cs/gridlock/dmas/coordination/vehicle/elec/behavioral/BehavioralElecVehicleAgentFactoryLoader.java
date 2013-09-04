package be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral;

import be.kuleuven.cs.gridlock.configuration.Configuration;
import be.kuleuven.cs.gridlock.configuration.services.TypedBasedServiceFactory;
import be.kuleuven.cs.gridlock.coordination.vehicle.VehicleAgentFactory;
import be.kuleuven.cs.gridlock.coordination.vehicle.VehicleAgentFactoryLoader;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class BehavioralElecVehicleAgentFactoryLoader extends TypedBasedServiceFactory<VehicleAgentFactory> implements VehicleAgentFactoryLoader {

    public BehavioralElecVehicleAgentFactoryLoader() {
        super(VehicleAgentFactoryLoader.AGENT_TYPE_CONFIGURATION_KEY, "behavioral-elec");
    }

    @Override
    public VehicleAgentFactory buildService(Configuration configuration) {

        return new BehavioralElecVehicleAgentBuilder();
    }
}
