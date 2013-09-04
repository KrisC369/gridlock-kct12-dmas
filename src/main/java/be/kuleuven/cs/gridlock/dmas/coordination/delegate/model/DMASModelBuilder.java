package be.kuleuven.cs.gridlock.dmas.coordination.delegate.model;

import be.kuleuven.cs.gridlock.configuration.Configuration;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration.AntBuilder;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration.ExplorationAlgorithm;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration.IExplorationAlgorithm;
import be.kuleuven.cs.gridlock.simulation.api.LinkReference;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.utilities.graph.Graph;

/**
 *
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public class DMASModelBuilder {

    public static IDMASModelAPI buildModel(Configuration config, Graph<NodeReference, LinkReference> graph) {
        AntBuilder ab = new AntBuilder(config);
        IExplorationAlgorithm algo = new ExplorationAlgorithm(ab);
        IDMASModelAPI model = new DMASModel(graph, algo);
        return model;
    }
}
