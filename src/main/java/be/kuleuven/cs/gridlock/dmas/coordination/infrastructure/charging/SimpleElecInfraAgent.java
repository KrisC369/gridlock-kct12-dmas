package be.kuleuven.cs.gridlock.dmas.coordination.infrastructure.charging;

import be.kuleuven.cs.gridlock.communication.CommunicationManager;
import be.kuleuven.cs.gridlock.communication.Message;
import be.kuleuven.cs.gridlock.coordination.infrastructure.InfrastructureAgent;
import be.kuleuven.cs.gridlock.simulation.SimulationContext;
import be.kuleuven.cs.gridlock.simulation.api.InfrastructureReference;
import be.kuleuven.cs.gridlock.simulation.api.LinkReference;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import be.kuleuven.cs.gridlock.simulation.model.SimulationModel;
import be.kuleuven.cs.gridlock.utilities.graph.Graph;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class SimpleElecInfraAgent implements InfrastructureAgent {

    private final SimulationContext context;
    private final InfrastructureReference reference;
    private final CommunicationManager comms;

    public SimpleElecInfraAgent(InfrastructureReference reference, SimulationContext context, CommunicationManager comms) {
        this.context = context;
        this.reference = reference;
        this.comms = comms;

    }

//    private Vehicle getVehicle() {
//        return this.context.getSimulationComponent( VehicleManager.class ).getVehicle( reference );
//    }
    private Graph<NodeReference, LinkReference> getGraph() {
        return this.context.getSimulationComponent(SimulationModel.class).getGraph();
    }

    @Override
    public void consume(VirtualTime currentTime, double timeFrameDuration) {
        returnPing();
    }

    private void returnPing(){
        Message mess;
        if (getComms().hasMessageFor(reference.getId())) {
            mess = this.context.getSimulationComponent(CommunicationManager.class).poll(reference.getId());
            System.out.println("ping");
            Message newmess = new Message("test:communication", mess.getReceiver(), mess.getSender());
            this.context.getSimulationComponent(CommunicationManager.class).sendMessage(newmess);
        }
    }
    
    private CommunicationManager getComms() {
        return this.comms;
    }

    @Override
    public boolean continueSimulation() {
        return false;
    }

    @Override
    // TODO it should not be the node's agents responsibility to purge the vehicle information
    public void destroy() {
        // this.context.getSimulationComponent( VehicleManager.class ).purge( this.reference );
    }
}
