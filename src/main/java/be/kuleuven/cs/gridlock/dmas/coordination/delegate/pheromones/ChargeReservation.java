package be.kuleuven.cs.gridlock.dmas.coordination.delegate.pheromones;

import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author s0199831
 */
public class ChargeReservation implements IRegistration {

    private final NodeReference regloc;
    private final VirtualTime arrivalTime;
    private final VirtualTime departureTime;
    
    public ChargeReservation(VirtualTime arrivalTime, VirtualTime depTime, NodeReference regloc) {
        this.arrivalTime = arrivalTime;
        this.regloc = regloc;
        this.departureTime = depTime;
    }

    @Override
    public NodeReference getRegistrationLocation() {
        return regloc;
    }

    @Override
    public VirtualTime getArrivalTime() {
        return VirtualTime.createVirtualTime(arrivalTime.getSeconds());
    }

    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> returnmap = new HashMap<String, Object>();
        returnmap.put("registrationLocation", getRegistrationLocation());
        returnmap.put("arrivalTime", getArrivalTime());
        return returnmap;
    }

    @Override
    public VirtualTime getDepartureTime() {
        return VirtualTime.createVirtualTime(departureTime.getSeconds());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + (this.regloc != null ? this.regloc.hashCode() : 0);
        hash = 61 * hash + (this.arrivalTime != null ? this.arrivalTime.hashCode() : 0);
        hash = 61 * hash + (this.departureTime != null ? this.departureTime.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ChargeReservation other = (ChargeReservation) obj;
        if (this.regloc != other.regloc && (this.regloc == null || !this.regloc.equals(other.regloc))) {
            return false;
        }
        if (this.arrivalTime != other.arrivalTime && (this.arrivalTime == null || !this.arrivalTime.equals(other.arrivalTime))) {
            return false;
        }
        if (this.departureTime != other.departureTime && (this.departureTime == null || !this.departureTime.equals(other.departureTime))) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(IRegistration o) {
        if(this.getArrivalTime().equals(o.getArrivalTime())){
            return this.getDepartureTime().compareTo(o.getDepartureTime());
        }else{
            return this.getArrivalTime().compareTo(o.getArrivalTime());
        }
    }
    
    @Override
    public String toString(){
        return this.getArrivalTime().toString()+"-"+this.getDepartureTime();
    }
}
