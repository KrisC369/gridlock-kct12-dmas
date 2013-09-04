package be.kuleuven.cs.gridlock.dmas.coordination.vehicle.commtest;

import be.kuleuven.cs.gridlock.communication.CommunicationManager;
import be.kuleuven.cs.gridlock.communication.Message;
import be.kuleuven.cs.gridlock.coordination.implementation.simple.SimpleVehicleAgent;
import be.kuleuven.cs.gridlock.routing.RoutingService;
import be.kuleuven.cs.gridlock.simulation.SimulationContext;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VehicleReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import be.kuleuven.cs.gridlock.simulation.model.charging.ElectricalQueueVehicle;
import be.kuleuven.cs.gridlock.simulation.model.infrastructure.NodeElement;
import be.kuleuven.cs.gridlock.simulation.model.queue.QueueModel;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class SimpleCommTestAgent extends SimpleVehicleAgent {

     private final CommunicationManager comms;

    public SimpleCommTestAgent(VehicleReference reference, SimulationContext context, RoutingService routing, CommunicationManager comms) {
     super(reference, context, routing);
     this.comms = comms;
    }


    private ElectricalQueueVehicle getEV() {
        try {
            return (ElectricalQueueVehicle) (this.getVehicle());
        } catch (ClassCastException cce) {
            return null;
        }
    }


    private CommunicationManager getComms() {
        return this.comms;
    }

    @Override
    public void consume(VirtualTime currentTime, double timeFrameDuration) {
        sendPing(3L);
    }

    private void sendPing(long destinationID) {
        NodeElement elem = getContext().getSimulationComponent(QueueModel.class).getNodeElement(new NodeReference(destinationID));
        NodeElement lastPosition = getContext().getSimulationComponent(QueueModel.class).getNodeElement(this.getVehicle().getLastPosition());
        if (elem.equals(lastPosition)) {
            Message mess = new Message("test:communication", this.getReference().getId(), new NodeReference(destinationID).getId());
            getComms().sendMessage(mess);
        }
        Message retmess;
        if (getComms().hasMessageFor(getReference().getId())) {
            retmess = getComms().poll(this.getReference().getId());
            System.out.println("pong");
        }
    }
}
