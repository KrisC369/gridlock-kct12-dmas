package be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration;

import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VehicleReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class ExplorationAnt implements Cloneable {

    private long carId;
    private final Map<NodeReference, VirtualTime> chargeArrival;
    private final Map<NodeReference, VirtualTime> chargeDuration;
    private final Map<NodeReference, VirtualTime> chargeWait;
    private final Map<NodeReference, Float> chargeLevels;
    private float maxEnergy;
    private VirtualTime currentTime;
    private VirtualTime startTime;
    private float currentEnergy;
    private float requiredEnergy;
    private final Map<NodeReference, VirtualTime> passedNodes;
    private final List<NodeReference> nodelist;
    private VehicleReference vehReference;
    private float consumption;
    private float traveltimecost;
    private float waittimecost;
    private float chargetimecost;

    /**
     * General constructor for the antbuilder.
     * @param carId 
     * @param currentTime
     * @param currentEnergy
     * @param requiredEnergy to reach destination.
     * @param maxEnergy maximum charge represented by battery.
     * @param consumption consumption of the represented vehicle.
     */
    public ExplorationAnt(Long carId, VirtualTime currentTime, float currentEnergy, float requiredEnergy, float maxEnergy, float consumption,
            float traveltimecost, float waittimecost, float chargetimecost) {
        this.carId = carId;
        this.chargeArrival = new HashMap<NodeReference, VirtualTime>();
        this.chargeDuration = new HashMap<NodeReference, VirtualTime>();
        this.chargeWait = new HashMap<NodeReference, VirtualTime>();
        this.chargeLevels = new HashMap<NodeReference, Float>();
        this.currentTime = currentTime;
        this.startTime = currentTime;
        this.currentEnergy = currentEnergy;
        this.requiredEnergy = requiredEnergy;
        if (requiredEnergy <= 0) {
            throw new IllegalArgumentException();
        }
        this.vehReference = new VehicleReference(carId);
        this.passedNodes = new HashMap<NodeReference, VirtualTime>();
        this.consumption = consumption;
        this.maxEnergy = maxEnergy;
        this.nodelist = new ArrayList<NodeReference>();
        this.traveltimecost = traveltimecost;
        this.waittimecost = waittimecost;
        this.chargetimecost = chargetimecost;
    }

    /**
     * Private constructor for the cloning operation only.
     * @param carId
     * @param chargeArrival
     * @param chargeDuration
     * @param maxEnergy
     * @param costs
     * @param currentTime
     * @param currentEnergy
     * @param requiredEnergy
     * @param passedNodes
     * @param lastNode
     * @param vehReference
     * @param consumption 
     */
    private ExplorationAnt(long carId, Map<NodeReference, VirtualTime> chargeArrival, Map<NodeReference, VirtualTime> chargeDuration, Map<NodeReference, VirtualTime> chargeWait, Map<NodeReference, Float> chargeLevels, float maxEnergy, VirtualTime currentTime, VirtualTime startTime, float currentEnergy, float requiredEnergy,
            Map<NodeReference, VirtualTime> passedNodes, List<NodeReference> lastNode, VehicleReference vehReference, float consumption, float traveltimecost, float waittimecost, float chargetimecost) {
        this.carId = carId;
        this.chargeArrival = new HashMap<NodeReference, VirtualTime>(chargeArrival);
        this.chargeDuration = new HashMap<NodeReference, VirtualTime>(chargeDuration);
        this.chargeWait = new HashMap<NodeReference, VirtualTime>(chargeWait);
        this.chargeLevels = new HashMap<NodeReference, Float>(chargeLevels);
        this.maxEnergy = maxEnergy;
        this.currentTime = currentTime;
        this.startTime = startTime;
        this.currentEnergy = currentEnergy;
        this.requiredEnergy = requiredEnergy;
        this.passedNodes = new HashMap<NodeReference, VirtualTime>(passedNodes);
        this.nodelist = new ArrayList<NodeReference>(lastNode);
        this.vehReference = vehReference;
        this.consumption = consumption;
        this.traveltimecost = traveltimecost;
        this.waittimecost = waittimecost;
        this.chargetimecost = chargetimecost;
    }

    public List<NodeReference> getVisitedStations() {
        List<NodeReference> ret = new ArrayList<NodeReference>();
        for (NodeReference r : nodelist) {
            if (chargeArrival.containsKey(r)) {
                ret.add(r);
            }
        }
        return ret;
    }

    public boolean isObsolete() {
        return testEnergeryLevels();
    }

    public void destroy() {
//        this.chargeArrival = null;
//        this.chargeDuration = null;
        this.currentTime = null;
        this.startTime = null;
//        this.chargeLevels = null;
//        this.nodelist = null;
//        this.passedNodes=null;
        this.vehReference = null;
    }

    private boolean testEnergeryLevels() {
        return currentEnergy <= 0;
    }

    public float getCost() {
        float cost = 0;
        float scaleA = traveltimecost;
        float scaleB = chargetimecost;
        float scaleC = waittimecost;
        cost += (currentTime.sub(startTime).getSeconds()) * scaleA;
        for (VirtualTime t : chargeDuration.values()) {
            cost += t/*.sub(startTime)*/.getSeconds() * scaleB;
        }
        for (VirtualTime t : chargeWait.values()) {
            cost += t/*.sub(startTime)*/.getSeconds() * scaleC;
        }
        //cost += timeUntilC100(48);

//        if(chargeArrival.containsKey(new NodeReference(-1l))){
//            return 1;
//        } else {
//            return 2;
//        }
        return cost;
    }

    public VirtualTime getTravelTime() {
        return this.currentTime;
    }

    @Override
    public ExplorationAnt clone() {
        return this.doClone();
    }

    private ExplorationAnt doClone() {
        return new ExplorationAnt(carId, chargeArrival, chargeDuration, chargeWait, chargeLevels,
                maxEnergy, currentTime, startTime, currentEnergy, requiredEnergy, passedNodes,
                nodelist, vehReference, consumption, traveltimecost, waittimecost, chargetimecost);
    }

    public VehicleReference getVehicleReference() {
        return this.vehReference;
    }

    VirtualTime getTimeStamp(NodeReference ref) {
        return passedNodes.get(ref);
    }

    public void addCharge(float chargeRate, NodeReference ref, VirtualTime timeUntilCountLessThan) {
        if (!passedNodes.containsKey(ref)) {
            throw new IllegalArgumentException("Not passed this node yet.");
        }
        chargeArrival.put(ref, passedNodes.get(ref));
        double dur = /*timeUntilCountLessThan.getSeconds() +*/ timeToFullCharge(chargeRate);
        chargeDuration.put(ref, VirtualTime.createVirtualTime(dur));
        chargeWait.put(ref, timeUntilCountLessThan);
        currentEnergy = maxEnergy;
        currentTime = currentTime.add(dur+timeUntilCountLessThan.getSeconds());
    }

    public void addTraveledNode(NodeReference ref, double distance, VirtualTime duration) {
        double secs = duration.getSeconds();
        VirtualTime stamp = currentTime.add(secs);
        passedNodes.put(ref, stamp);
        this.nodelist.add(ref);
        currentTime = currentTime.add(secs);
        float consumed = (float) (consumption * distance);
        subtractCharge(consumed);
        this.chargeLevels.put(ref, this.currentEnergy);
    }

    public boolean hasAchievedGoal() {
        return this.currentEnergy >= this.requiredEnergy;
    }

    private void subtractCharge(float consumed) {
        this.currentEnergy -= consumed;
    }

    private double timeToFullCharge(float chargeRate) {
        return ((maxEnergy - currentEnergy) / chargeRate) * 3600;
    }

    public NodeReference getLastVisitedNode() {
        if (nodelist.size() < 1) {
            return null;
        }
        return nodelist.get(nodelist.size() - 1);
    }

    public float getChargeLevelAt(NodeReference ref) {
        return chargeLevels.get(ref);
    }

    public double getWaitingTimeAt(NodeReference ref) {
        return this.chargeWait.get(ref).getSeconds();
    }
}
