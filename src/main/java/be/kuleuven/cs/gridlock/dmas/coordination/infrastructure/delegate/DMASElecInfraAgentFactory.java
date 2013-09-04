package be.kuleuven.cs.gridlock.dmas.coordination.infrastructure.delegate;

import be.kuleuven.cs.gridlock.configuration.services.ServiceFactory;
import be.kuleuven.cs.gridlock.coordination.infrastructure.InfrastructureAgent;
import be.kuleuven.cs.gridlock.coordination.infrastructure.InfrastructureAgentFactory;
import be.kuleuven.cs.gridlock.routing.RoutingService;
import be.kuleuven.cs.gridlock.routing.RoutingServiceLoader;
import be.kuleuven.cs.gridlock.simulation.SimulationContext;
import be.kuleuven.cs.gridlock.simulation.api.InfrastructureReference;
import java.util.ServiceConfigurationError;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class DMASElecInfraAgentFactory implements InfrastructureAgentFactory {
    private RoutingService routingService;
    private SimulationContext context;

    @Override
    public void initialize(SimulationContext simulationContext) {
        this.context = simulationContext;
        this.routingService = ServiceFactory.Helper.load( RoutingServiceLoader.class, simulationContext.getConfiguration() );
        if( this.routingService == null ) {
            Logger.getLogger( DMASElecInfraAgentFactory.class.getCanonicalName() ).log( Level.SEVERE, "Could not instantiate a routing service" );
            throw new ServiceConfigurationError( "Could not instantiate a routing service" );
        }
    }

    @Override
    public InfrastructureAgent createAgent(InfrastructureReference component) {
        DMASElecInfraAgent agent = new DMASElecInfraAgent(component);
        agent.initialize(context);
        return agent;
    }
}
