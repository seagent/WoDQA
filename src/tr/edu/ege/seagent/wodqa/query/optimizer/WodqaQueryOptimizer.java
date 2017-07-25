package tr.edu.ege.seagent.wodqa.query.optimizer;

import java.util.List;
import java.util.Vector;

import tr.edu.ege.seagent.wodqa.query.analyzer.QueryConstructorUtil;
import tr.edu.ege.seagent.wodqa.query.analyzer.VOIDPathSolution;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.syntax.Element;

public class WodqaQueryOptimizer {

	private String initialQuery;
	private List<VOIDPathSolution> initialVOIDPaths;
	private List<Triple> initialTriplePatterns = new Vector<Triple>();

	private String orderedQuery;
	private List<VOIDPathSolution> orderedVOIDPaths = new Vector<VOIDPathSolution>();
	private List<Triple> orderedTriples = new Vector<Triple>();
	/**
	 * It is hold to get triple statistics from voids.
	 */
	private List<OntModel> voidModels;

	public List<OntModel> getVoidModels() {
		return voidModels;
	}

	public String getOrderedQuery() {
		return orderedQuery;
	}

	public List<VOIDPathSolution> getOrderedVOIDPathSolutions() {
		return orderedVOIDPaths;
	}

	public WodqaQueryOptimizer(String simpleQuery,
			List<VOIDPathSolution> analyzedEndpointMatrice,
			List<OntModel> voidModels) {
		this.initialQuery = simpleQuery;
		this.initialVOIDPaths = analyzedEndpointMatrice;
		this.voidModels = voidModels;
		QueryConstructorUtil.setTriplesToGivenListByWalker(
				QueryFactory.create(simpleQuery).getQueryPattern(),
				this.initialTriplePatterns);
	}

	public Query optimizeQuery(String voidstoreURLForStatistics) {
		Query allQuery = QueryFactory.create(initialQuery);
		List<Element> unionElementsList = new Vector<Element>();
		QueryConstructorUtil.setUnionBlockElementsToGivenList(allQuery,
				unionElementsList);
		List<Integer> unionBlockSizes = new Vector<Integer>();
		// optimize union parts individually...
		Element queryPattern = allQuery.getQueryPattern();
		if (unionElementsList.size() > 1) {
			for (Element element : unionElementsList) {
				List<Triple> triplesInUnionBlock = new Vector<Triple>();
				QueryConstructorUtil.setTriplesToGivenListByWalker(element,
						triplesInUnionBlock);
				unionBlockSizes.add(triplesInUnionBlock.size());
				// set endpoints for the triples in union block
				List<VOIDPathSolution> tempVOIDPaths = new Vector<VOIDPathSolution>();
				for (int i = 0; i < triplesInUnionBlock.size(); i++) {
					tempVOIDPaths.add(initialVOIDPaths
							.get(initialTriplePatterns
									.indexOf(triplesInUnionBlock.get(i))));
				}
				// create bgp optimizer and optimize
				optimizeGivenTriples(voidstoreURLForStatistics,
						triplesInUnionBlock, tempVOIDPaths);
			}
		} else {
			List<Triple> allTriples = new Vector<Triple>();
			QueryConstructorUtil.setTriplesToGivenListByWalker(queryPattern,
					allTriples);
			optimizeGivenTriples(voidstoreURLForStatistics, allTriples,
					initialVOIDPaths);
		}
		// all triples are reordered in orderedTriples and orderedEndpoints and
		// we have to construct new ordered simple
		// query.
		if (unionBlockSizes.size() > 0) {
			int tripleSize = 0;
			for (int i = 0; i < unionBlockSizes.size(); i++) {
				List<Triple> blockOrderedTriples = new Vector<Triple>();
				for (int j = 0 + tripleSize; j < unionBlockSizes.get(i)
						+ tripleSize; j++) {
					blockOrderedTriples.add(orderedTriples.get(j));
				}
				tripleSize += unionBlockSizes.get(i);

				List<Triple> allOptionalTriples = QueryConstructorUtil
						.checkOptionalBlock(unionElementsList.get(i));
				if (allOptionalTriples.size() > 0) {
					Element deleteAllTriplesFromQuery = QueryConstructorUtil
							.deleteAllTriplesFromQuery(unionElementsList.get(i));
					// create all elements and add them at once.
					QueryConstructorUtil.addGivenElementsToGivenQueryBlock(
							deleteAllTriplesFromQuery, blockOrderedTriples,
							allOptionalTriples);
				} else
					QueryConstructorUtil.updateTriplesInGivenQueryBlock(
							unionElementsList.get(i), blockOrderedTriples);

			}
		} else {
			List<Triple> allOptionalTriples = QueryConstructorUtil
					.checkOptionalBlock(queryPattern);
			if (allOptionalTriples.size() > 0) {
				Element deleteAllTriplesFromQuery = QueryConstructorUtil
						.deleteAllTriplesFromQuery(queryPattern);
				// create all elements and add them at once.
				QueryConstructorUtil.addGivenElementsToGivenQueryBlock(
						deleteAllTriplesFromQuery, orderedTriples,
						allOptionalTriples);
			} else
				// update triple patterns in query
				QueryConstructorUtil.updateTriplesInGivenQueryBlock(
						queryPattern, orderedTriples);
		}

		this.orderedQuery = allQuery.serialize();
		return allQuery;
	}

	/**
	 * It reorders the given triples
	 * 
	 * @param voidstoreURLForStatistics
	 * @param tripleList
	 * @param voidPathList
	 */
	private void optimizeGivenTriples(String voidstoreURLForStatistics,
			List<Triple> tripleList, List<VOIDPathSolution> voidPathList) {
		BGPOptimizer bgpOptimizer = new BGPOptimizer(tripleList, voidPathList);
		bgpOptimizer.reorderBasedHeuristics();
		List<TripleEndpointMatch> reorderedBlock = bgpOptimizer
				.reorderBasedTripleCountInHeuristicGroups(
						voidstoreURLForStatistics, getVoidModels());
		// set new ordered endpoints and triples
		for (TripleEndpointMatch te : reorderedBlock) {
			this.orderedVOIDPaths.add(te.getVOIDPathSolution());
			this.orderedTriples.add(te.getTriple());
		}
	}
}
