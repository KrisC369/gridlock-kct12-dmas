package be.kuleuven.cs.gridlock.dmas.coordination.delegate.model;

/**
 * Interface for objects you can register yourself to.
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
interface IRegisterable<T> {
    public void register(T t);
    public void unregister(T t);
}
