package be.kuleuven.cs.gridlock.dmas.coordination.delegate.pheromones;

/**
 * Enum for different types of pheromones.
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public enum PheroType {
    INTENTION(),
	FEASABILITY(),
	EXPLORATION(),
	ALL();
	PheroType(){
        }	
}
