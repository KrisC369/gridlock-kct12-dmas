package be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral;

import be.kuleuven.cs.gridlock.dmas.coordination.vehicle.behaviors.IElecVehicleBehavior;
import java.util.Collection;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public interface IBehavingEntity {
    
    public void addBehavior(IElecVehicleBehavior behavior);
    
    public Collection<IElecVehicleBehavior> getBehaviors();
    
    public void replaceBehaviorInstance(IElecVehicleBehavior behavior);
}
