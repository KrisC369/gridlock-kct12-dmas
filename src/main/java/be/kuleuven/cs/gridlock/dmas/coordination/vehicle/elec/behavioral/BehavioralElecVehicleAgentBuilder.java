package be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral;

import be.kuleuven.cs.gridlock.communication.CommunicationManager;
import be.kuleuven.cs.gridlock.configuration.services.ServiceFactory;
import be.kuleuven.cs.gridlock.coordination.vehicle.VehicleAgent;
import be.kuleuven.cs.gridlock.coordination.vehicle.VehicleAgentFactory;
import be.kuleuven.cs.gridlock.dmas.coordination.infrastructure.charging.SimpleElecInfraAgentFactory;
import be.kuleuven.cs.gridlock.dmas.coordination.vehicle.behaviors.ElecVehicleBehaviorFactory;
import be.kuleuven.cs.gridlock.routing.RoutingService;
import be.kuleuven.cs.gridlock.routing.RoutingServiceLoader;
import be.kuleuven.cs.gridlock.simulation.SimulationContext;
import be.kuleuven.cs.gridlock.simulation.api.VehicleReference;
import java.util.ServiceConfigurationError;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Builder for creating behavioral vehicle agents based on the configuration.
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class BehavioralElecVehicleAgentBuilder implements VehicleAgentFactory {

    private RoutingService routingService;
    private SimulationContext context;
    private CommunicationManager commsMan;
    private ElecVehicleBehaviorFactory behaviorFactory;
    private int behaviorSettings;

    @Override
    public void initialize(SimulationContext simulationContext) {
        this.context = simulationContext;
        this.routingService = ServiceFactory.Helper.load(RoutingServiceLoader.class, simulationContext.getConfiguration());

        if (this.routingService == null) {
            Logger.getLogger(BehavioralElecVehicleAgentBuilder.class.getCanonicalName()).log(Level.SEVERE, "Could not instantiate a routing service");
            throw new ServiceConfigurationError("Could not instantiate a routing service");
        }

        this.commsMan = this.context.getSimulationComponent(CommunicationManager.class);
        if (commsMan == null) {
            Logger.getLogger(SimpleElecInfraAgentFactory.class.getCanonicalName()).log(Level.SEVERE, "Could not load CommunicationsManager");
            throw new ServiceConfigurationError("Could not load a communications manager.");
        }

        this.behaviorFactory = this.context.getInstrumentationComponent(ElecVehicleBehaviorFactory.class);
        if (behaviorFactory == null) {
            Logger.getLogger(SimpleElecInfraAgentFactory.class.getCanonicalName()).log(Level.SEVERE, "Could not load BehaviorFactory");
            throw new ServiceConfigurationError("Could not load a BehaviorFactory.");
        }
        this.behaviorSettings = this.context.getConfiguration().getInt("coordination.vehicle.behavior.config", 0);
    }

    @Override
    public VehicleAgent createAgent(VehicleReference vehicle) {
        return createAgentByBehaviorSettings(vehicle, this.behaviorSettings);
    }

    protected VehicleAgent createAgentByBehaviorSettings(VehicleReference vehicle, int behaviorSettings) {
        switch (behaviorSettings) {
            case -1:
                return createNoBehaviorVehicle(vehicle);
            case 0:
                return createNaiveVehicle(vehicle);
            case 1:
                return createPostponingVehicle(vehicle);
            case 2:
                return createReferenceVehicle(vehicle);
            case 3:
                return createEmptyVehicle(vehicle);
            case 4:
                return createIntentionPostponeDmasVehicle(vehicle);
            case 5:
                return createIntentionReferenceDmasVehicle(vehicle);
            case 6:
                return createAdvisedExplorationDmasVehicle(vehicle);
            default:
                return createNaiveVehicle(vehicle);
        }
    }

    protected VehicleAgent createReferenceVehicle(VehicleReference vehicle) {
        BehavioralElecVehicleAgent agent = new BehavioralElecVehicleAgent(vehicle, context, routingService, commsMan);
        //agent.addBehavior(behaviorFactory.createPostponingChargingBehavior());
        //agent.addBehavior(behaviorFactory.createEmptyTankChargingBehavior());
        //agent.addBehavior(behaviorFactory.createNaiveChargingBehavior());
        agent.addBehavior(behaviorFactory.createReferenceChargingBehavior());
        return agent;
    }

    protected VehicleAgent createPostponingVehicle(VehicleReference vehicle) {
        BehavioralElecVehicleAgent agent = new BehavioralElecVehicleAgent(vehicle, context, routingService, commsMan);
        agent.addBehavior(behaviorFactory.createPostponingChargingBehavior());
        return agent;
    }

    protected VehicleAgent createNaiveVehicle(VehicleReference vehicle) {
        BehavioralElecVehicleAgent agent = new BehavioralElecVehicleAgent(vehicle, context, routingService, commsMan);
        agent.addBehavior(behaviorFactory.createNaiveChargingBehavior());
        return agent;
    }

    protected VehicleAgent createEmptyVehicle(VehicleReference vehicle) {
        BehavioralElecVehicleAgent agent = new BehavioralElecVehicleAgent(vehicle, context, routingService, commsMan);
        agent.addBehavior(behaviorFactory.createEmptyTankChargingBehavior());
        return agent;
    }

    protected VehicleAgent createNoBehaviorVehicle(VehicleReference vehicle) {
        return new BehavioralElecVehicleAgent(vehicle, context, routingService, commsMan);
    }

    protected VehicleAgent createIntentionPostponeDmasVehicle(VehicleReference vehicle) {
        BehavioralElecVehicleAgent agent = (BehavioralElecVehicleAgent) createPostponingVehicle(vehicle);
        agent.addBehavior(behaviorFactory.createIntentionDMASBehavior());
        return agent;
    }

    protected VehicleAgent createIntentionReferenceDmasVehicle(VehicleReference vehicle) {
        BehavioralElecVehicleAgent agent = (BehavioralElecVehicleAgent) createReferenceVehicle(vehicle);
        agent.addBehavior(behaviorFactory.createIntentionDMASBehavior());
        return agent;
    }

    protected VehicleAgent createAdvisedExplorationDmasVehicle(VehicleReference vehicle) {
        BehavioralElecVehicleAgent agent = new BehavioralElecVehicleAgent(vehicle, context, routingService, commsMan);
        agent.addBehavior(behaviorFactory.createExplorationDMASBehavior());
        agent.addBehavior(behaviorFactory.createAdvisedChargingBehavior());
        agent.addBehavior(behaviorFactory.createIntentionDMASBehavior());
        return agent;
    }
}
