package be.kuleuven.cs.gridlock.dmas.coordination.delegate;

import java.util.List;
import java.util.Set;

import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.IExplorationContext;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration.ExplorationAnt;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.pheromones.IPheromone;
import be.kuleuven.cs.gridlock.simulation.api.InfrastructureReference;
import be.kuleuven.cs.gridlock.simulation.model.charging.station.IChargingStationReference;

/**
 * Interface representing Dmas components, ie. charging stations or other
 * infrastructure elements.
 * 
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public interface IDMASModelComponent {

    /**
     * Default getter for infrastructure reference.
     * 
     * @return The infrastructure ref.
     */
    InfrastructureReference getInfrastructureReference();

    /**
     * Default getter for delegated station instance.
     * 
     * @return The station delegate.
     */
    IChargingStationReference getStationDelegate();

    /**
     * Drop a registration pheromone.
     * 
     * @param phero
     *            The phero to drop.
     */
    void dropRegistration(IPheromone phero);

    /**
     * Get all registrations.
     * 
     * @return The set of registration pheros.
     */
    Set<IPheromone> getRegistrations();

    /**
     * Evaporate all information.
     * 
     * @param timespan
     *            The timespan to lapse.
     */
    void evaporateDelegate(double timespan);

    /**
     * Explore this infrastructure node.
     * 
     * @param ants
     *            The ants to explore this node.
     * @param context
     *            The context to explore them in.
     */
    void exploreNode(List<ExplorationAnt> ants, IExplorationContext context);
}
