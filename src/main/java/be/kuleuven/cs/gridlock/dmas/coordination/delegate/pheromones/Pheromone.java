package be.kuleuven.cs.gridlock.dmas.coordination.delegate.pheromones;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class Pheromone implements IPheromone {

    private final float MAXSTRENGTH;
    private final IRegistration registration;
    private float strength;
    private final long originRef;
    private final PheroType type;
    private final int evapStep;

    public Pheromone(IRegistration reg, float startStrength, float MAX, long originRef, PheroType type) {
        this.registration = reg;
        this.strength = startStrength;
        this.originRef = originRef;
        this.type = type;
        this.MAXSTRENGTH = MAX;
        this.evapStep = 1;
    }

    @Override
    public IRegistration getRegistration() {
        return this.registration;
    }

    @Override
    public boolean evaporateStep(double duration) {
        int decrement = (int) (evapStep * duration);
        decrementStrength(decrement);
        if (this.strength <= 0) {
            return true;
        }
        return false;
    }

    @Override
    public PheroType getType() {
        return this.type;
    }

    @Override
    public long getOriginRef() {
        return this.originRef;
    }

    /**
     * @return the strength
     */
    public float getStrength() {
        return strength;
    }

    /**
     * @param strength the strength to set
     */
    private void setStrength(float strength) {
        if (getStrength() + strength > MAXSTRENGTH) {
            this.strength = MAXSTRENGTH;
        }
        this.strength = strength;
    }
    
     private void decrementStrength(int decrement) {
        setStrength(this.getStrength()-decrement);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Float.floatToIntBits(MAXSTRENGTH);
        result = prime * result + (int) (originRef ^ (originRef >>> 32));
//        result = prime * result
//                + ((registration == null) ? 0 : registration.hashCode());
//        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Pheromone other = (Pheromone) obj;
        if (Float.floatToIntBits(MAXSTRENGTH) != Float.floatToIntBits(other.MAXSTRENGTH)) {
            return false;
        }
        if (originRef != other.originRef) {
            return false;
        }
        if (registration == null) {
            if (other.registration != null) {
                return false;
            }
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }
}
