package be.kuleuven.cs.gridlock.dmas.coordination.delegate;

import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.IDMASModelAPI;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
interface APIUser {
    /**
     * Inject the API in this component.
     * @param api 
     */
    public void initializeAPI(IDMASModelAPI api);
}
