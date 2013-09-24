package be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.IExplorationContext;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.util.ChargeAwareItinerary;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.util.Itinerary;
import be.kuleuven.cs.gridlock.dmas.coordination.vehicle.elec.behavioral.IVehicleContext;
import be.kuleuven.cs.gridlock.routing.Path;
import be.kuleuven.cs.gridlock.simulation.api.LinkReference;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.simulation.api.VirtualTime;
import be.kuleuven.cs.gridlock.utilities.graph.Graph;

/**
 * 
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class ExplorationAlgorithm implements IExplorationAlgorithm {

    private IExplorationContext context;
    private AntBuilder antbuilder;
    private final Graph<NodeReference, LinkReference> graph;

    /**
     * Constructor.
     * 
     * @param ab
     *            the antbuilder to use.
     * @param graph
     *            the graph instance to use.
     */
    public ExplorationAlgorithm(AntBuilder ab,
            Graph<NodeReference, LinkReference> graph) {
        this.antbuilder = ab;
        this.graph = graph;
    }

    @Override
    public void initialize(IExplorationContext context) {
        this.context = context;
    }

    @Override
    public Itinerary<NodeReference, VirtualTime> getSequenceOfStations(
            IVehicleContext vehContext, VirtualTime currentTime)
            throws NoRoutePossibleException {
        if (vehContext == null) {
            throw new IllegalStateException("Not initialized yet");
        }
        NodeReference currentPosition = vehContext.getCurrentPosition();
        NodeReference destination = vehContext.getEV()
                .getIntendedFinalDestination();
        Path route = this.context.getRouting().route(currentPosition,
                destination, graph);

        ExplorationAnt initialAnt = antbuilder.buildExplorationAnt(currentTime,
                vehContext, route);

        List<ExplorationAnt> ants = new ArrayList<ExplorationAnt>();
        ants.add(initialAnt);
        //
        doExploration(route, ants);
        //
        ExplorationAnt ant = chooseBestAnt(ants);
        //
        List<NodeReference> stationsequence = ant.getVisitedStations();
        ChargeAwareItinerary<NodeReference, VirtualTime> retmap = new ChargeAwareItinerary<NodeReference, VirtualTime>();
        for (NodeReference ref : stationsequence) {
            retmap.add(ref, ant.getTimeStamp(ref), ant.getChargeLevelAt(ref),
                    ant.getWaitingTimeAt(ref));

        }
        return retmap;
    }

    /**
     * The exploration Step
     * 
     * @param route
     * @param ants
     */
    private void doExploration(Path route, List<ExplorationAnt> ants) {
        for (NodeReference routeElem : route.getNodes()) {
            if (context.getComponent(routeElem) == null) {
                throw new IllegalStateException("node should be registered");
            }
            context.getComponent(routeElem).exploreNode(ants, context);
            prune(ants);
        }
    }

    /**
     * Prune ants that have already become empty in charge.
     * 
     * @param ants
     */
    private void prune(List<ExplorationAnt> ants) {
        List<ExplorationAnt> toDel = new ArrayList<ExplorationAnt>();
        for (ExplorationAnt a : ants) {
            if (a.isObsolete()) {
                a.destroy();
                toDel.add(a);
            }
        }
        for (ExplorationAnt a : toDel) {
            ants.remove(a);
        }
    }

    /**
     * Return ant that scores best in time or cost.
     * 
     * @param ants
     * @return
     */
    private ExplorationAnt chooseBestAnt(List<ExplorationAnt> ants)
            throws NoRoutePossibleException {
        if (ants == null) {
            throw new IllegalArgumentException();
        }
        if (ants.isEmpty()) {
            throw new NoRoutePossibleException("Empty ant list");
        }
        ExplorationAnt minant = ants.get(0);
        double mincost = ants.get(0).getCost();
        for (ExplorationAnt a : ants) {
            if (a.getCost() < mincost) {
                minant = a;
                mincost = minant.getCost();
            }
        }
        return minant;
    }
}
