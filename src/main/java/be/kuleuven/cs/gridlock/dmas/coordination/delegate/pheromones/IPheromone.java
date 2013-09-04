package be.kuleuven.cs.gridlock.dmas.coordination.delegate.pheromones;

/**
 * Interface specifying the general context of pheromones.
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public interface IPheromone {

    public IRegistration getRegistration();

    public boolean evaporateStep(double timeDuration);
    
    public PheroType getType();
    
    public long getOriginRef();
}
