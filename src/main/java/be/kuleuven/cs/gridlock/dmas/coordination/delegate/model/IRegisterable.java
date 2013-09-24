package be.kuleuven.cs.gridlock.dmas.coordination.delegate.model;

/**
 * Interface for objects you can register yourself to.
 * 
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
interface IRegisterable<T> {

    /**
     * Register this object T.
     * 
     * @param t
     *            The object.
     */
    public void register(T t);

    /**
     * Unregister this object.
     * 
     * @param t
     *            The object.
     */
    public void unregister(T t);
}
