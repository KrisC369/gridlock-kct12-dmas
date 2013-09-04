package be.kuleuven.cs.gridlock.dmas.coordination.infrastructure.charging;

import be.kuleuven.cs.gridlock.configuration.Configuration;
import be.kuleuven.cs.gridlock.configuration.services.TypedBasedServiceFactory;
import be.kuleuven.cs.gridlock.coordination.infrastructure.InfrastructureAgentFactory;
import be.kuleuven.cs.gridlock.coordination.infrastructure.InfrastructureAgentFactoryLoader;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class SimpleElecInfraAgentFactoryLoader extends TypedBasedServiceFactory<InfrastructureAgentFactory> implements InfrastructureAgentFactoryLoader {
    
    public SimpleElecInfraAgentFactoryLoader(){
     super( InfrastructureAgentFactoryLoader.AGENT_TYPE_CONFIGURATION_KEY, "simple-elec" );
    }

    @Override
    public InfrastructureAgentFactory buildService( Configuration configuration ) {
        return new SimpleElecInfraAgentFactory();
    }
}
