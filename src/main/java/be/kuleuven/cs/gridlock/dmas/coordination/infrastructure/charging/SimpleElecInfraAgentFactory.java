package be.kuleuven.cs.gridlock.dmas.coordination.infrastructure.charging;

import be.kuleuven.cs.gridlock.communication.CommunicationManager;
import be.kuleuven.cs.gridlock.coordination.infrastructure.InfrastructureAgent;
import be.kuleuven.cs.gridlock.coordination.infrastructure.InfrastructureAgentFactory;
import be.kuleuven.cs.gridlock.simulation.SimulationContext;
import be.kuleuven.cs.gridlock.simulation.api.InfrastructureReference;
import java.util.ServiceConfigurationError;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class SimpleElecInfraAgentFactory implements InfrastructureAgentFactory {
//    private RoutingService routingService;

    private SimulationContext context;
    private CommunicationManager commsMan;

    @Override
    public void initialize(SimulationContext simulationContext) {
        this.context = simulationContext;
        this.commsMan = context.getSimulationComponent(CommunicationManager.class);
        if (commsMan == null) {
            Logger.getLogger(SimpleElecInfraAgentFactory.class.getCanonicalName()).log(Level.SEVERE, "Could not load CommunicationsManager");
            throw new ServiceConfigurationError("Could not load a communications manager.");
        }
//            throw new ServiceConfigurationError( "Could not instantiate a routing service" );
//        this.routingService = ServiceFactory.Helper.load( RoutingServiceLoader.class, simulationContext.getConfiguration() );

//        if( this.routingService == null ) {
//            Logger.getLogger( SimpleElecInfraAgentFactory.class.getCanonicalName() ).log( Level.SEVERE, "Could not instantiate a routing service" );
//            throw new ServiceConfigurationError( "Could not instantiate a routing service" );
//        }
    }

    @Override
    public InfrastructureAgent createAgent(InfrastructureReference component) {
        return new SimpleElecInfraAgent(component, context,commsMan);
    }
}
