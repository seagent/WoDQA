package tr.edu.ege.seagent.wodqa.query.optimizer;

import java.util.List;
import java.util.Vector;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;

import tr.edu.ege.seagent.wodqa.query.analyzer.VOIDPathSolution;

/**
 * This class provides heuristic based triple reordering and using VOID
 * statistics for triple reordering.
 * 
 */
public class BGPOptimizer {
	public static final String DSI_LOD = "http://dsi.lod-cloud.net/sparql";
	/**
	 * Reordered triple patterns.
	 */
	private List<Triple> triplePatternsList;
	/**
	 * Endpoints for each triple pattern.
	 */
	private List<VOIDPathSolution> voidPathSolutionList;

	/**
	 * It holds <IRI,IRI,var> and <IRI,var,IRI> triple patterns.
	 */
	private List<TripleEndpointMatch> firstLevelTPs = new Vector<TripleEndpointMatch>();
	/**
	 * It holds <IRI,var,var> triple patterns.
	 */
	private List<TripleEndpointMatch> secondLevelTPs = new Vector<TripleEndpointMatch>();
	/**
	 * It holds <var,IRI,IRI> triple patterns.
	 */
	private List<TripleEndpointMatch> thirdLevelTPs = new Vector<TripleEndpointMatch>();
	/**
	 * It holds <var,IRI,var> and <vat,var,IRI> triple patterns.
	 */
	private List<TripleEndpointMatch> fourthLevelTPs = new Vector<TripleEndpointMatch>();
	/**
	 * It holds <var,var,var> triple patterns.
	 */
	private List<TripleEndpointMatch> fifthLevelTPs = new Vector<TripleEndpointMatch>();

	/**
	 * {@link BGPOptimizer} constructor.
	 * 
	 * @param triplePatternsList
	 * @param voidPathSolutionList
	 */
	public BGPOptimizer(List<Triple> triplePatternsList,
			List<VOIDPathSolution> voidPathSolutionList) {
		super();
		this.triplePatternsList = triplePatternsList;
		this.voidPathSolutionList = voidPathSolutionList;
	}

	public List<Triple> getTriplePatternsList() {
		return triplePatternsList;
	}

	public List<VOIDPathSolution> getVOIDPathSolutionList() {
		return voidPathSolutionList;
	}

	/**
	 * Reorder triple patterns by heuristics.
	 */
	public List<TripleEndpointMatch> reorderBasedHeuristics() {
		List<TripleEndpointMatch> orderedTriplePatterns = new Vector<TripleEndpointMatch>();
		for (int i = 0; i < getTriplePatternsList().size(); i++) {
			Triple triple = getTriplePatternsList().get(i);
			VOIDPathSolution vp = getVOIDPathSolutionList().get(i);
			TripleEndpointMatch tripleEndpointMatch = new TripleEndpointMatch(
					triple, vp);
			if (triple.getSubject().isURI()) {
				if (triple.getPredicate().isVariable()
						&& triple.getObject().isVariable()) {
					secondLevelTPs.add(tripleEndpointMatch);
				} else
					firstLevelTPs.add(tripleEndpointMatch);
			} else {
				if (triple.getPredicate().isURI()
						&& !triple.getObject().isVariable()) {
					thirdLevelTPs.add(tripleEndpointMatch);
				} else if (triple.getPredicate().isVariable()
						&& triple.getObject().isVariable()) {
					fifthLevelTPs.add(tripleEndpointMatch);
				} else
					fourthLevelTPs.add(tripleEndpointMatch);
			}
		}
		orderedTriplePatterns.addAll(firstLevelTPs);
		orderedTriplePatterns.addAll(secondLevelTPs);
		orderedTriplePatterns.addAll(thirdLevelTPs);
		orderedTriplePatterns.addAll(fourthLevelTPs);
		orderedTriplePatterns.addAll(fifthLevelTPs);
		return orderedTriplePatterns;
	}

	public List<TripleEndpointMatch> reorderBasedTripleCountInHeuristicGroups(
			String voidStoreURL, List<OntModel> voidModels) {
		reorderHeuristicGroupByTripleCountStatistics(firstLevelTPs,
				voidStoreURL, voidModels);
		reorderHeuristicGroupByTripleCountStatistics(secondLevelTPs,
				voidStoreURL, voidModels);
		reorderHeuristicGroupByTripleCountStatistics(thirdLevelTPs,
				voidStoreURL, voidModels);
		reorderHeuristicGroupByTripleCountStatistics(fourthLevelTPs,
				voidStoreURL, voidModels);
		reorderHeuristicGroupByTripleCountStatistics(fifthLevelTPs,
				voidStoreURL, voidModels);
		List<TripleEndpointMatch> orderedTriplePatterns = new Vector<TripleEndpointMatch>();
		orderedTriplePatterns.addAll(firstLevelTPs);
		orderedTriplePatterns.addAll(secondLevelTPs);
		orderedTriplePatterns.addAll(thirdLevelTPs);
		orderedTriplePatterns.addAll(fourthLevelTPs);
		orderedTriplePatterns.addAll(fifthLevelTPs);
		return orderedTriplePatterns;

	}

	/**
	 * Reorder the given triples according to their dataset's triple count.
	 * 
	 * @param heuristicGroup
	 * @param voidStoreURL
	 * @param voidModels
	 *            TODO
	 */
	private void reorderHeuristicGroupByTripleCountStatistics(
			List<TripleEndpointMatch> heuristicGroup, String voidStoreURL,
			List<OntModel> voidModels) {
		List<Long> tripleCountList = new Vector<Long>();
		if (heuristicGroup.size() > 1) {
			boolean moreThanOneDataset = false;
			for (TripleEndpointMatch te : heuristicGroup) {
				if (te.getVOIDPathSolution().getAllEndpoints().size() > 1)
					moreThanOneDataset = true;
			}
			// sort based triple count if there is only one dataset for each
			// triple pattern in a heuristic group.
			if (!moreThanOneDataset) {
				for (TripleEndpointMatch te : heuristicGroup) {
					// search for each endpoint
					for (OntModel voidModel : voidModels) {
						String query = "prefix void: <http://rdfs.org/ns/void#> SELECT ?count WHERE {?dataset void:sparqlEndpoint \""
								+ te.getVOIDPathSolution().getAllEndpoints()
										.get(0)
								+ "\". ?dataset void:triples ?count.}";
						QueryExecution execution = QueryExecutionFactory
								.create(QueryFactory.create(query), voidModel);
						ResultSet set = execution.execSelect();
						if (set.hasNext()) {
							tripleCountList.add(set.next().getLiteral("count")
									.getLong());
							execution.close();
							break;
						}
						execution.close();
					}
				}
				// reorder according to triple counts
				bubbleSort(tripleCountList, heuristicGroup);
			}
		}
	}

	private void bubbleSort(List<Long> tripleCountList,
			List<TripleEndpointMatch> heuristicGroup) {
		boolean swapped = true;
		while (swapped) {
			swapped = false;
			for (int i = 0; i < tripleCountList.size() - 1; i++) {
				if (tripleCountList.get(i + 1) < tripleCountList.get(i)) {
					long tempCount = tripleCountList.get(i + 1);
					tripleCountList.set(i + 1, tripleCountList.get(i));
					tripleCountList.set(i, tempCount);
					TripleEndpointMatch tempTEMatch = heuristicGroup.get(i + 1);
					heuristicGroup.set(i + 1, heuristicGroup.get(i));
					heuristicGroup.set(i, tempTEMatch);
					swapped = true;
				}
			}
		}

	}
}
