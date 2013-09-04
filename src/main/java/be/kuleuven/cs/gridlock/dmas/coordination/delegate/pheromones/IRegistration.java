/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.kuleuven.cs.gridlock.dmas.coordination.delegate.pheromones;

import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import java.util.Map;


public interface IRegistration extends Comparable<IRegistration> {

    VirtualTime getArrivalTime();
    
    VirtualTime getDepartureTime();

    NodeReference getRegistrationLocation();

    Map<String, Object> getAttributes();
    
}
