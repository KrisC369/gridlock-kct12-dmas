package be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration;

import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VehicleReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representation of ants that explore the network for information about
 * charging.
 (cfr. delegateMAS)
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class ExplorationAnt implements Cloneable {

    private final long carId;
    private final Map<NodeReference, VirtualTime> chargeArrival;
    private final Map<NodeReference, VirtualTime> chargeDuration;
    private final Map<NodeReference, VirtualTime> chargeWait;
    private final Map<NodeReference, Float> chargeLevels;
    private final float maxEnergy;
    private VirtualTime currentTime;
    private VirtualTime startTime;
    private float currentEnergy;
    private final float requiredEnergy;
    private final Map<NodeReference, VirtualTime> passedNodes;
    private final List<NodeReference> nodelist;
    private VehicleReference vehReference;
    private float consumption;
    private float traveltimecost;
    private final float waittimecost;
    private final float chargetimecost;

    /**
     * General constructor for the antbuilder.
     *
     * @param carId the id of the car.
     * @param currentTime the current time at creation.
     * @param currentEnergy the current energy of the ant.
     * @param requiredEnergy to reach destination.
     * @param maxEnergy maximum charge represented by battery.
     * @param consumption consumption of the represented vehicle.
     */
    public ExplorationAnt(Long carId, VirtualTime currentTime, float currentEnergy, float requiredEnergy, float maxEnergy, float consumption,
            float traveltimecost, float waittimecost, float chargetimecost) {
        this.carId = carId;
        this.chargeArrival = new HashMap<>();
        this.chargeDuration = new HashMap<>();
        this.chargeWait = new HashMap<>();
        this.chargeLevels = new HashMap<>();
        this.currentTime = currentTime;
        this.startTime = currentTime;
        this.currentEnergy = currentEnergy;
        this.requiredEnergy = requiredEnergy;
        if (requiredEnergy <= 0) {
            throw new IllegalArgumentException();
        }
        this.vehReference = new VehicleReference(carId);
        this.passedNodes = new HashMap<>();
        this.consumption = consumption;
        this.maxEnergy = maxEnergy;
        this.nodelist = new ArrayList<>();
        this.traveltimecost = traveltimecost;
        this.waittimecost = waittimecost;
        this.chargetimecost = chargetimecost;
    }

    /**
     * Private constructor for the cloning operation only.
     *
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
        this.chargeArrival = new HashMap<>(chargeArrival);
        this.chargeDuration = new HashMap<>(chargeDuration);
        this.chargeWait = new HashMap<>(chargeWait);
        this.chargeLevels = new HashMap<>(chargeLevels);
        this.maxEnergy = maxEnergy;
        this.currentTime = currentTime;
        this.startTime = startTime;
        this.currentEnergy = currentEnergy;
        this.requiredEnergy = requiredEnergy;
        this.passedNodes = new HashMap<>(passedNodes);
        this.nodelist = new ArrayList<>(lastNode);
        this.vehReference = vehReference;
        this.consumption = consumption;
        this.traveltimecost = traveltimecost;
        this.waittimecost = waittimecost;
        this.chargetimecost = chargetimecost;
    }

    public List<NodeReference> getVisitedStations() {
        List<NodeReference> ret = new ArrayList<>();
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

    /**
     * Default getter for the time traveled.
     *
     * @return a virtualtime instance.
     */
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

    /**
     * Default getter for the vehicle reference.
     *
     * @return a vehiclereference instance.
     */
    public VehicleReference getVehicleReference() {
        return this.vehReference;
    }

    /**
     * Default getter for the timestamp.
     *
     * @return a virtualtime instance.
     */
    VirtualTime getTimeStamp(NodeReference ref) {
        return passedNodes.get(ref);
    }

    /**
     * Add energy charge to this ant.
     *
     * @param chargeRate the rate of charge.
     * @param ref the node to charge at.
     * @param timeUntilCountLessThan the time to wait before being able to start
     * charging.
     */
    public void addCharge(float chargeRate, NodeReference ref, VirtualTime timeUntilCountLessThan) {
        if (!passedNodes.containsKey(ref)) {
            throw new IllegalArgumentException("Not passed this node yet.");
        }
        chargeArrival.put(ref, passedNodes.get(ref));
        double dur = /*timeUntilCountLessThan.getSeconds() +*/ timeToFullCharge(chargeRate);
        chargeDuration.put(ref, VirtualTime.createVirtualTime(dur));
        chargeWait.put(ref, timeUntilCountLessThan);
        currentEnergy = maxEnergy;
        currentTime = currentTime.add(dur + timeUntilCountLessThan.getSeconds());
    }

    /**
     * Add a node to the visited node list.
     *
     * @param ref the reference of the node.
     * @param distance the distance covered.
     * @param duration the duration of the travel.
     */
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

    private void subtractCharge(float consumed) {
        this.currentEnergy -= consumed;
    }

    private double timeToFullCharge(float chargeRate) {
        return ((maxEnergy - currentEnergy) / chargeRate) * 3600;
    }

    /**
     * Returns the last node visited.
     *
     * @return a nodereference instance.
     */
    public NodeReference getLastVisitedNode() {
        if (nodelist.size() < 1) {
            return null;
        }
        return nodelist.get(nodelist.size() - 1);
    }

    /**
     * Get the chargelevel at a certain node passed.
     *
     * @param ref the nodereference.
     * @return a float representing a chargelevel or null if ref not in passed
     * nodes list.
     */
    public float getChargeLevelAt(NodeReference ref) {
        return chargeLevels.get(ref);
    }

    /**
     * Get the time waited at a certain node passed.
     *
     * @param ref the nodereference.
     * @return a float representing a chargelevel or -!F if ref not in passed
     * nodes list.
     */
    public double getWaitingTimeAt(NodeReference ref) {
        if (this.chargeWait.get(ref) == null) {
            return -1F;
        }
        return this.chargeWait.get(ref).getSeconds();
    }
}
