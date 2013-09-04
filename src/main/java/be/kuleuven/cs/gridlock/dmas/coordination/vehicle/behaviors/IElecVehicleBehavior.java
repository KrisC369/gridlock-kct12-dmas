package be.kuleuven.cs.gridlock.dmas.coordination.vehicle.behaviors;

import be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral.IVehicleContext;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;

/**
 * Interface for electrical vehicle behaviors. 
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public interface IElecVehicleBehavior {
    
    /**
     * execute the behavior of this instance.
     */
    void executeBehavior(VirtualTime currentTime, double timeFrameDuration,IVehicleContext context);
}
