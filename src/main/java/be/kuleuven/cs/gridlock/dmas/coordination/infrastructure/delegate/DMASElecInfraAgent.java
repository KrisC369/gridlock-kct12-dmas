package be.kuleuven.cs.gridlock.dmas.coordination.infrastructure.delegate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import be.kuleuven.cs.gridlock.coordination.infrastructure.InfrastructureAgent;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.IDMASModelComponent;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.IDMASModelAPI;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.IExplorationContext;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration.ExplorationAnt;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.pheromones.IPheromone;
import be.kuleuven.cs.gridlock.simulation.SimulationComponent;
import be.kuleuven.cs.gridlock.simulation.SimulationContext;
import be.kuleuven.cs.gridlock.simulation.api.InfrastructureReference;
import be.kuleuven.cs.gridlock.simulation.api.LinkReference;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VehicleReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import be.kuleuven.cs.gridlock.simulation.events.EventController;
import be.kuleuven.cs.gridlock.simulation.model.SimulationModel;
import be.kuleuven.cs.gridlock.simulation.model.charging.ElectricalQueueNode;
import be.kuleuven.cs.gridlock.simulation.model.charging.IChargeable;
import be.kuleuven.cs.gridlock.simulation.model.charging.station.IChargingStationReference;
import be.kuleuven.cs.gridlock.simulation.model.charging.station.StationManager;
import be.kuleuven.cs.gridlock.simulation.model.infrastructure.InfrastructureManager;
import be.kuleuven.cs.gridlock.simulation.model.vehicle.VehicleManager;
import be.kuleuven.cs.gridlock.simulation.timeframe.TimeFrameConsumer;
import be.kuleuven.cs.gridlock.utilities.graph.Edge;
import be.kuleuven.cs.gridlock.utilities.graph.Graph;

/**
 * 
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class DMASElecInfraAgent implements InfrastructureAgent,
	SimulationComponent, IDMASModelComponent {

    private SimulationContext context;
    private final InfrastructureReference reference;
    private final Set<IPheromone> registrations;

    public DMASElecInfraAgent(InfrastructureReference reference) {
	this.reference = reference;
	this.registrations = new HashSet<IPheromone>();
    }

    private Graph<NodeReference, LinkReference> getGraph() {
	return this.context.getSimulationComponent(SimulationModel.class)
		.getGraph();
    }

    protected ElectricalQueueNode getNode() {
	return (ElectricalQueueNode) this.context.getSimulationComponent(
		InfrastructureManager.class)
		.getInfrastructureElement(reference);
    }

    @Override
    public void consume(VirtualTime currentTime, double timeFrameDuration) {
	// Leave evaporation to DMASModel.
    }

    @Override
    public boolean continueSimulation() {
	return false;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void initialize(SimulationContext simulationContext) {
	this.context = simulationContext;
	this.context.getSimulationComponent(IDMASModelAPI.class).register(this);
    }

    @Override
    public Collection<? extends TimeFrameConsumer> getConsumers() {
	return Collections.singleton(this);
    }

    @Override
    public Collection<? extends SimulationComponent> getSubComponents() {
	return Collections.EMPTY_LIST;
    }

    @Override
    public InfrastructureReference getInfrastructureReference() {
	return this.reference;
    }

    @Override
    public IChargingStationReference getStationDelegate() {
	return context.getSimulationComponent(StationManager.class).getStation(
		new NodeReference((Long) this.reference.getId()));
    }

    public EventController getEventController() {
	return this.context.getEventController();
    }

    @Override
    public void dropRegistration(IPheromone phero) {
	this.registrations.add(phero);
	publishNewPhero(phero.getRegistration().getRegistrationLocation(),
		phero);
    }

    @Override
    public void evaporateDelegate(double timespan) {
	for (IPheromone p : this.getRegistrations()) {
	    boolean remove = p.evaporateStep(timespan);
	    if (remove) {
		removePhero(p);
	    }
	}
    }

    @Override
    public Set<IPheromone> getRegistrations() {
	return new HashSet<IPheromone>(this.registrations);
    }

    private void removePhero(IPheromone p) {
	publishEvapPhero(new NodeReference((Long) getInfrastructureReference()
		.getId()), p);
	this.registrations.remove(p);
    }

    private void publishNewPhero(NodeReference location, IPheromone phero) {
	this.getEventController().publishEvent("dmas:pheromone:dropped",
		"location", location.getId(), "pheromonetype", phero.getType());
    }

    private void publishEvapPhero(NodeReference location, IPheromone phero) {
	this.getEventController().publishEvent("dmas:pheromone:evaporated",
		"location", location.getId(), "pheromonetype", phero.getType());
    }

    @Override
    public void exploreNode(List<ExplorationAnt> ants,
	    IExplorationContext context) {
	List<ExplorationAnt> origAnts = new ArrayList<ExplorationAnt>(ants);
	for (ExplorationAnt a : origAnts) {
	    // getStationDelegate().getCurrentPriceRate();
	    updateTravelStats(a, context);
	    if (getStationDelegate().isChargingStation() && !a.isObsolete()) {
		ExplorationAnt chargeClone = a.clone();
		ants.add(chargeClone);
		doChargeExploration(chargeClone, context);
	    }
	}
    }

    private void updateTravelStats(ExplorationAnt ant,
	    IExplorationContext context) {
	NodeReference ref = new NodeReference((Long) this
		.getInfrastructureReference().getId());
	double distance = 0;
	VirtualTime duration = VirtualTime.createVirtualTime(0);
	NodeReference last = ant.getLastVisitedNode();
	if (last != null) {
	    Edge edge = getGraph().getEdge(new LinkReference(last + ":" + ref));
	    try {
		distance = (Float) edge.getAnnotations().get("length");
	    } catch (NullPointerException e) {
		System.out.println(e.getCause());
	    }
	    int speed = (Integer) edge.getAnnotations().get("speed");
	    duration = VirtualTime.createVirtualTime((distance / speed) * 3600);
	}
	ant.addTraveledNode(ref, distance, duration);
    }

    private void doChargeExploration(ExplorationAnt chargeClone,
	    IExplorationContext context) {
	NodeReference ref = new NodeReference((Long) this
		.getInfrastructureReference().getId());
	// chargeClone.addTimeStamp(context.getTimeUntilCountLessThan(ref,
	// getStationDelegate().getTotalChargingSpots(),
	// chargeClone.getTravelTime()));
	chargeClone.addCharge(getStationDelegate().getChargeRate(), ref,
		context.getWaitingTimeForSpot(ref, getStationDelegate()
			.getTotalChargingSpots(), chargeClone.getTravelTime(),
			chargeClone.getVehicleReference()));
    }

    private IChargeable getVehicle(VehicleReference reference) {
	return (IChargeable) this.context.getSimulationComponent(
		VehicleManager.class).getVehicle(reference);
    }
}
