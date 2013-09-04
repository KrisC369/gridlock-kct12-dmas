package be.kuleuven.cs.gridlock.dmas.coordination.delegate.pheromones;

import be.kuleuven.cs.gridlock.configuration.Configuration;
import be.kuleuven.cs.gridlock.simulation.SimulationContext;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import be.kuleuven.cs.gridlock.simulation.instrumentation.InstrumentationComponent;

/**
 *
 * @author s0199831
 */
public class PheromoneFactory implements InstrumentationComponent{
    //TODO read from config
    private float startStrength = 10000;
    private float maxStrength = 10000;
    public static final String STARTSTRENGTH_KEY = "value.dmas.phermones.maxvalue";
    
    public IPheromone createIntentionPheromone(long originRef, NodeReference ref, VirtualTime vt,VirtualTime dvt){
        IRegistration reg = new ChargeReservation(vt,dvt,ref);
        IPheromone ph = new Pheromone(reg, startStrength, maxStrength, originRef, PheroType.INTENTION);
        return ph;
    }

    public PheromoneFactory(Configuration config) {
    }

    @Override
    public void initialize(SimulationContext simulationContext) {
        Configuration config = simulationContext.getConfiguration();
        this.startStrength = config.getFloat(STARTSTRENGTH_KEY, 10000);
        this.maxStrength = config.getFloat(STARTSTRENGTH_KEY, 10000);
    }
}
