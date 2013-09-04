package be.kuleuven.cs.gridlock.dmas.coordination.vehicle.commtest;

import be.kuleuven.cs.gridlock.communication.CommunicationManager;
import be.kuleuven.cs.gridlock.configuration.services.ServiceFactory;
import be.kuleuven.cs.gridlock.coordination.vehicle.VehicleAgent;
import be.kuleuven.cs.gridlock.coordination.vehicle.VehicleAgentFactory;
import be.kuleuven.cs.gridlock.dmas.coordination.infrastructure.charging.SimpleElecInfraAgentFactory;
import be.kuleuven.cs.gridlock.routing.RoutingService;
import be.kuleuven.cs.gridlock.routing.RoutingServiceLoader;
import be.kuleuven.cs.gridlock.simulation.SimulationContext;
import be.kuleuven.cs.gridlock.simulation.api.VehicleReference;
import java.util.ServiceConfigurationError;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class SimpleCommTestAgentFactory implements VehicleAgentFactory {
    private RoutingService routingService;
    private SimulationContext context;
    private CommunicationManager commsMan;
    
   @Override
    public void initialize( SimulationContext simulationContext ) {
        this.context = simulationContext;
        this.routingService = ServiceFactory.Helper.load( RoutingServiceLoader.class, simulationContext.getConfiguration() );
        
        if( this.routingService == null ) {
            Logger.getLogger( SimpleCommTestAgentFactory.class.getCanonicalName() ).log( Level.SEVERE, "Could not instantiate a routing service" );
            throw new ServiceConfigurationError( "Could not instantiate a routing service" );
        }
        
        this.commsMan = this.context.getSimulationComponent(CommunicationManager.class);
        if (commsMan == null) {
            Logger.getLogger(SimpleElecInfraAgentFactory.class.getCanonicalName()).log(Level.SEVERE, "Could not load CommunicationsManager");
            throw new ServiceConfigurationError("Could not load a communications manager.");
        }
    }

    @Override
    public VehicleAgent createAgent( VehicleReference vehicle ) {
        return new SimpleCommTestAgent( vehicle, context, routingService, commsMan );
    }
    
}
