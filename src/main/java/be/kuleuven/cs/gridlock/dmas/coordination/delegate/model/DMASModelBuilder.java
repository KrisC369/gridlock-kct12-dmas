package be.kuleuven.cs.gridlock.dmas.coordination.delegate.model;

import be.kuleuven.cs.gridlock.configuration.Configuration;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration.AntBuilder;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration.ExplorationAlgorithm;
import be.kuleuven.cs.gridlock.dmas.coordination.delegate.model.exploration.IExplorationAlgorithm;
import be.kuleuven.cs.gridlock.simulation.api.LinkReference;
import be.kuleuven.cs.gridlock.simulation.api.NodeReference;
import be.kuleuven.cs.gridlock.utilities.graph.Graph;

/**
 * Utility class for building dmas models.
 * @author Kristof Coninx <kristof.coninx at student.kuleuven.be>
 */
public final class DMASModelBuilder {

	private DMASModelBuilder(){
		//no-op
	}
	/**
	 * Constructor for the ModelBuilder.
	 * @param config The configuration to build from.
	 * @param graph The graph to build model on.
	 * @return A dmas model API instance.
	 */
    public static IDMASModelAPI buildModel(Configuration config, Graph<NodeReference, LinkReference> graph) {
        AntBuilder ab = new AntBuilder(config);
        IExplorationAlgorithm algo = new ExplorationAlgorithm(ab);
        return new DMASModel(graph, algo);
    }
}
