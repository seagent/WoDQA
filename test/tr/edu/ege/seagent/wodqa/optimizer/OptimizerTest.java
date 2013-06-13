package tr.edu.ege.seagent.wodqa.optimizer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Vector;

import org.junit.Ignore;
import org.junit.Test;

import tr.edu.ege.seagent.wodqa.query.analyzer.VOIDPathSolution;
import tr.edu.ege.seagent.wodqa.query.optimizer.BGPOptimizer;
import tr.edu.ege.seagent.wodqa.query.optimizer.TripleEndpointMatch;
import tr.edu.ege.seagent.wodqa.query.optimizer.WodqaQueryOptimizer;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;

@Ignore
public class OptimizerTest {
	public static final String DSI_LOD = "http://dsi.lod-cloud.net/sparql";
	private static final String KEGG_ENDPOINT = "http://kegg.bio2rdf.org/sparql";
	private static final String SEMANTICEUROPA_ENDPOINT = "http://semantic.eea.europa.eu/sparql";
	private static final String LODSZTAKI_ENDPOINT = "http://lod.sztaki.hu/sparql";
	private static final String GEOLINKEDDATA_ENDPOINT = "http://geo.linkeddata.es/sparql";
	private static final String LINKEDGEODATA_ENDPOINT = "http://linkedgeodata.org/sparql/";
	private static final String DBPEDIA_ENDPOINT = "http://dbpedia.org/sparql";

	@Test
	public void heuristicBasedOrderingTest() throws Exception {
		List<Triple> triplePatternsList = new Vector<Triple>();
		List<VOIDPathSolution> endpointList = new Vector<VOIDPathSolution>();
		// add triples
		creaateSixTriplePatterns(triplePatternsList);

		// add endpoints
		createVOIDPathSolutions(endpointList);

		BGPOptimizer optimizer = new BGPOptimizer(triplePatternsList,
				endpointList);
		List<TripleEndpointMatch> rtMatch = optimizer.reorderBasedHeuristics();
		for (TripleEndpointMatch teMatch : rtMatch) {
			System.out.println(teMatch.getTriple() + " Endpoint:"
					+ teMatch.getVOIDPathSolution().getAllEndpoints().get(0));
		}
		assertEquals(rtMatch.get(0).getVOIDPathSolution().getAllEndpoints()
				.get(0), LODSZTAKI_ENDPOINT);
		assertEquals(rtMatch.get(1).getVOIDPathSolution().getAllEndpoints()
				.get(0), DBPEDIA_ENDPOINT);
		assertEquals(rtMatch.get(2).getVOIDPathSolution().getAllEndpoints()
				.get(0), SEMANTICEUROPA_ENDPOINT);
		assertEquals(rtMatch.get(3).getVOIDPathSolution().getAllEndpoints()
				.get(0), KEGG_ENDPOINT);
		assertEquals(rtMatch.get(4).getVOIDPathSolution().getAllEndpoints()
				.get(0), LINKEDGEODATA_ENDPOINT);
		assertEquals(rtMatch.get(5).getVOIDPathSolution().getAllEndpoints()
				.get(0), GEOLINKEDDATA_ENDPOINT);
	}

	@Test
	public void getTripleCountStatisticsTest() throws Exception {
		List<Triple> triplePatternsList = new Vector<Triple>();
		List<VOIDPathSolution> endpointList = new Vector<VOIDPathSolution>();
		// add triples
		creaateSixTriplePatterns(triplePatternsList);

		// add endpoints
		createVOIDPathSolutions(endpointList);

		BGPOptimizer optimizer = new BGPOptimizer(triplePatternsList,
				endpointList);
		List<TripleEndpointMatch> reorderBasedHeuristics = optimizer
				.reorderBasedHeuristics();
		assertEquals(LINKEDGEODATA_ENDPOINT, reorderBasedHeuristics.get(4)
				.getVOIDPathSolution().getAllEndpoints().get(0));
		assertEquals(GEOLINKEDDATA_ENDPOINT, reorderBasedHeuristics.get(5)
				.getVOIDPathSolution().getAllEndpoints().get(0));
		List<TripleEndpointMatch> reorderBasedTripleCountInHeuristicGroups = optimizer
				.reorderBasedTripleCountInHeuristicGroups(DSI_LOD, null);
		assertEquals(GEOLINKEDDATA_ENDPOINT,
				reorderBasedTripleCountInHeuristicGroups.get(4)
						.getVOIDPathSolution().getAllEndpoints().get(0));
		assertEquals(LINKEDGEODATA_ENDPOINT,
				reorderBasedTripleCountInHeuristicGroups.get(5)
						.getVOIDPathSolution().getAllEndpoints().get(0));
	}

	@Test
	public void optimizeQueryMoreThanOneEndpointTest() throws Exception {
		List<VOIDPathSolution> analyzedEndpointMatrice = createVOIDPathsolutions(
				LINKEDGEODATA_ENDPOINT, DBPEDIA_ENDPOINT);
		analyzedEndpointMatrice.get(1).addEndpoint(LINKEDGEODATA_ENDPOINT);
		WodqaQueryOptimizer optimizer = new WodqaQueryOptimizer(
				"SELECT * WHERE {?s ?p ?o. ?a ?b ?c. }",
				analyzedEndpointMatrice, null);
		Query optimizedQuery = optimizer.optimizeQuery(DSI_LOD);
		String queryStr = optimizedQuery.serialize();
		System.out.println(queryStr);
		assertTrue(queryStr.indexOf("?s ?p ?o") < queryStr.indexOf("?a ?b ?c"));
	}

	@Test
	public void wodqaOptimizerSimpleQueryTest() throws Exception {
		List<VOIDPathSolution> analyzedEndpointMatrice = createVOIDPathsolutions(
				LINKEDGEODATA_ENDPOINT, DBPEDIA_ENDPOINT, DBPEDIA_ENDPOINT);
		WodqaQueryOptimizer optimizer = new WodqaQueryOptimizer(
				"SELECT * WHERE {?s ?p ?o. ?a ?b ?c. <http://foaf.org/kaynak> ?c ?d. }",
				analyzedEndpointMatrice, null);
		Query optimizedQuery = optimizer.optimizeQuery(DSI_LOD);
		String queryStr = optimizedQuery.serialize();
		System.out.println(queryStr);
		assertTrue(queryStr.indexOf("<http://foaf.org/kaynak>") < queryStr
				.indexOf("?s ?p ?o"));
	}

	/**
	 * Creates a {@link VOIDPathSolution} list with one endpoint.
	 * 
	 * @param endpoints
	 * @return
	 */
	private List<VOIDPathSolution> createVOIDPathsolutions(String... endpoints) {
		List<VOIDPathSolution> analyzedEndpointMatrice = new Vector<VOIDPathSolution>();
		for (String endpoint : endpoints) {
			VOIDPathSolution voidPathSolution = new VOIDPathSolution();
			voidPathSolution.addEndpoint(endpoint);
			analyzedEndpointMatrice.add(voidPathSolution);
		}
		return analyzedEndpointMatrice;

	}

	@Test
	public void wodqaOptimizeUnionQueryTest() throws Exception {
		List<VOIDPathSolution> analyzedEndpointMatrice = createVOIDPathsolutions(
				DBPEDIA_ENDPOINT, DBPEDIA_ENDPOINT, LINKEDGEODATA_ENDPOINT);
		WodqaQueryOptimizer optimizer = new WodqaQueryOptimizer(
				"SELECT * WHERE { {?s ?p ?o.} UNION {?a ?b ?c. <http://foaf.org/kaynak> ?c ?d.} }",
				analyzedEndpointMatrice, null);
		Query optimizedQuery = optimizer.optimizeQuery(DSI_LOD);
		String queryStr = optimizedQuery.serialize();
		System.out.println(queryStr);
		assertTrue(queryStr.indexOf("<http://foaf.org/kaynak>") < queryStr
				.indexOf("?a ?b ?c"));
	}

	@Test
	public void wodqaQueryOptimizeFILTERQueryTest() throws Exception {
		List<VOIDPathSolution> analyzedEndpointMatrice = createVOIDPathsolutions(
				DBPEDIA_ENDPOINT, DBPEDIA_ENDPOINT, LINKEDGEODATA_ENDPOINT);
		WodqaQueryOptimizer optimizer = new WodqaQueryOptimizer(
				"SELECT * WHERE {{?s ?p ?o. FILTER (?c<20)} UNION {?a ?b ?c. <http://foaf.org/kaynak> ?c ?d.}  }",
				analyzedEndpointMatrice, null);
		Query optimizedQuery = optimizer.optimizeQuery(DSI_LOD);
		String queryStr = optimizedQuery.serialize();
		System.out.println(queryStr);
		assertTrue(queryStr.indexOf("FILTER") < queryStr.indexOf("?a ?b ?c"));
	}

	@Test
	public void wodqaQueryOptimizeLIMITQueryTest() throws Exception {
		List<VOIDPathSolution> analyzedEndpointMatrice = createVOIDPathsolutions(
				DBPEDIA_ENDPOINT, DBPEDIA_ENDPOINT, LINKEDGEODATA_ENDPOINT);
		WodqaQueryOptimizer optimizer = new WodqaQueryOptimizer(
				"SELECT * WHERE {{?s ?p ?o.} UNION {?a ?b ?c. <http://foaf.org/kaynak> ?c ?d.}  } LIMIT 15",
				analyzedEndpointMatrice, null);
		Query optimizedQuery = optimizer.optimizeQuery(DSI_LOD);
		String queryStr = optimizedQuery.serialize();
		System.out.println(queryStr);
		assertTrue(queryStr.contains("LIMIT"));
	}

	@Test
	public void separateOptionalExpressionsTest() throws Exception {
		List<VOIDPathSolution> analyzedEndpointMatrice = createVOIDPathsolutions(
				LINKEDGEODATA_ENDPOINT, DBPEDIA_ENDPOINT,
				LINKEDGEODATA_ENDPOINT, DBPEDIA_ENDPOINT);
		WodqaQueryOptimizer optimizer = new WodqaQueryOptimizer(
				"SELECT * WHERE {  ?x ?y ?z. ?d ?e ?f. OPTIONAL{?a ?b ?c.?s ?p ?o.}}",
				analyzedEndpointMatrice, null);
		Query optimizedQuery = optimizer.optimizeQuery(DSI_LOD);
		String queryStr = optimizedQuery.serialize();
		System.out.println(queryStr);
	}

	@Test
	public void wodqaQueryOptimizeOPTIONALQueryTest() throws Exception {
		List<VOIDPathSolution> analyzedEndpointMatrice = createVOIDPathsolutions(
				DBPEDIA_ENDPOINT, DBPEDIA_ENDPOINT, LINKEDGEODATA_ENDPOINT);
		WodqaQueryOptimizer optimizer = new WodqaQueryOptimizer(
				"SELECT * WHERE {?s ?p ?o. OPTIONAL {?a ?b ?c.} <http://foaf.org/kaynak> ?c ?d. }",
				analyzedEndpointMatrice, null);
		Query optimizedQuery = optimizer.optimizeQuery(DSI_LOD);
		String queryStr = optimizedQuery.serialize();
		System.out.println(queryStr);
		assertTrue(queryStr.indexOf("<http://foaf.org/kaynak>") < queryStr
				.indexOf("OPTIONAL"));
	}

	@Test
	public void wodqaQueryOptimizeOPTIONALandUNIONQueryTest() throws Exception {
		List<VOIDPathSolution> analyzedEndpointMatrice = createVOIDPathsolutions(
				DBPEDIA_ENDPOINT, DBPEDIA_ENDPOINT, LINKEDGEODATA_ENDPOINT);
		WodqaQueryOptimizer optimizer = new WodqaQueryOptimizer(
				"SELECT * WHERE { {?s ?p ?o.} UNION { OPTIONAL {?a ?b ?c.} <http://foaf.org/kaynak> ?c ?d.} }",
				analyzedEndpointMatrice, null);
		Query optimizedQuery = optimizer.optimizeQuery(DSI_LOD);
		String queryStr = optimizedQuery.serialize();
		System.out.println(queryStr);
		assertTrue(queryStr.indexOf("<http://foaf.org/kaynak>") < queryStr
				.indexOf("OPTIONAL"));
	}

	/**
	 * Use ? before variables and use http before URIs.
	 * 
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return
	 */
	private Triple createTriple(String subject, String predicate, String object) {
		Node s;
		Node p;
		Node o;
		// create subject
		if (subject.startsWith("http"))
			s = Node.createURI(subject);
		else
			s = Node.createVariable(subject.substring(1));
		// create predicate
		if (predicate.startsWith("http"))
			p = Node.createURI(predicate);
		else
			p = Node.createVariable(predicate.substring(1));
		// create object..
		if (object.startsWith("?"))
			o = Node.createVariable(object.substring(1));
		else if (object.startsWith("http"))
			o = Node.createURI(object);
		else
			o = Node.createLiteral(object);
		return new Triple(s, p, o);
	}

	private void createVOIDPathSolutions(List<VOIDPathSolution> endpointList) {
		VOIDPathSolution vp1 = new VOIDPathSolution();
		vp1.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp2 = new VOIDPathSolution();
		vp2.addEndpoint(LINKEDGEODATA_ENDPOINT);
		VOIDPathSolution vp3 = new VOIDPathSolution();
		vp3.addEndpoint(GEOLINKEDDATA_ENDPOINT);
		VOIDPathSolution vp4 = new VOIDPathSolution();
		vp4.addEndpoint(LODSZTAKI_ENDPOINT);
		VOIDPathSolution vp5 = new VOIDPathSolution();
		vp5.addEndpoint(SEMANTICEUROPA_ENDPOINT);
		VOIDPathSolution vp6 = new VOIDPathSolution();
		vp6.addEndpoint(KEGG_ENDPOINT);
		endpointList.add(vp1);
		endpointList.add(vp2);
		endpointList.add(vp3);
		endpointList.add(vp4);
		endpointList.add(vp5);
		endpointList.add(vp6);
	}

	private void creaateSixTriplePatterns(List<Triple> triplePatternsList) {
		triplePatternsList.add(createTriple(
				"http://dbpedia.org/resource/Izmir", "?p", "?o"));
		triplePatternsList.add(createTriple("?s", "?a", "?b"));
		triplePatternsList.add(createTriple("?x", "?y", "?z"));
		triplePatternsList.add(createTriple(
				"http://dbpedia.org/resource/Izmir", "http://foaf.org/name",
				"?name"));
		triplePatternsList.add(createTriple("?x", "http://foaf.orgyer", "?z"));
		triplePatternsList.add(createTriple("?x", "?prop", "45"));
	}

}
