package be.kuleuven.cs.gridlock.dmas.coordination.vehicle.commtest;

import be.kuleuven.cs.gridlock.configuration.Configuration;
import be.kuleuven.cs.gridlock.configuration.services.TypedBasedServiceFactory;
import be.kuleuven.cs.gridlock.coordination.vehicle.VehicleAgentFactory;
import be.kuleuven.cs.gridlock.coordination.vehicle.VehicleAgentFactoryLoader;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class SimpleCommTestAgentFactoryLoader extends TypedBasedServiceFactory<VehicleAgentFactory> implements VehicleAgentFactoryLoader {
    
    public SimpleCommTestAgentFactoryLoader(){
     super( VehicleAgentFactoryLoader.AGENT_TYPE_CONFIGURATION_KEY, "simple-commTest" );
    }

    @Override
    public VehicleAgentFactory buildService( Configuration configuration ) {
        return new SimpleCommTestAgentFactory();
    }
}
