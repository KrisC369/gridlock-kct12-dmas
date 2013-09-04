package be.kuleuven.cs.gridlock.dmas.coordination.delegate;

import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.IExplorationContext;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration.ExplorationAnt;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.pheromones.IPheromone;
import be.kuleuven.cs.gridlock.simulation.api.InfrastructureReference;
import be.kuleuven.cs.gridlock.simulation.model.charging.station.IChargingStationReference;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public interface IDMASModelComponent extends APIUser{
    
     public InfrastructureReference getInfrastructureReference();
     
     public IChargingStationReference getStationDelegate();
     
     public void dropRegistration(IPheromone phero);
     
     public Set<IPheromone> getRegistrations();
     
     public void evaporateDelegate(double timespan);

    public void exploreNode(List<ExplorationAnt> ants, IExplorationContext context);
}
