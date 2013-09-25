package be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration;

/**
 * No route is available while exploring.
 * 
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class NoRoutePossibleException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 4054701878399983014L;

    /**
     * Default constructor.
     * 
     * @see super
     * @param string
     */
    public NoRoutePossibleException(String string) {
        super(string);
    }

}
