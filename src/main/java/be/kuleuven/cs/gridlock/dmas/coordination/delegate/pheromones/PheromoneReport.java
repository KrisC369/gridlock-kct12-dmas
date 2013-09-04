package be.kuleuven.cs.gridlock.dmas.coordination.delegate.pheromones;

import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import java.util.Set;

/**
 * Data wrapper acting as aggregate information packet that smart model delivers to interested parties
 * such as ants or DMAS agents.
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class PheromoneReport {

    private PheroType type;
    private double value;
    private Set<VirtualTime> times;
    private NodeReference direction;

    public PheromoneReport(PheroType type, double value, NodeReference direction,Set<VirtualTime> times) {
        setType(type);
        setValue(value);
        setDirection(direction);
        setTimes(times);
    }

    public PheroType getType() {
        return type;
    }

    private void setType(PheroType type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    private void setValue(double value) {
        this.value = value;
    }

    public NodeReference getGoalLocation() {
        return direction;
    }

    private void setDirection(NodeReference direction) {
        this.direction = direction;
    }

    private void setTimes(Set<VirtualTime> times) {
        this.times = times;
    }
}
