package tr.edu.ege.seagent.wodqa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementService;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;
import com.hp.hpl.jena.sparql.syntax.ElementWalker;

import tr.edu.ege.seagent.wodqa.query.DeterminedTriple;
import tr.edu.ege.seagent.wodqa.query.QueryReorganizer;
import tr.edu.ege.seagent.wodqa.query.TripleGroup;
import tr.edu.ege.seagent.wodqa.query.ValuedTriple;
import tr.edu.ege.seagent.wodqa.query.analyzer.QueryAnalyzer;
import tr.edu.ege.seagent.wodqa.query.analyzer.VOIDPathSolution;
import tr.edu.ege.seagent.wodqa.voiddocument.VOIDCreator;
import util.QueryExampleVocabulary;

public class QueryReorganizerTest extends AbstractWoDQAComponentsTest {

	private static final String DOT = ".";

	private static final String BIND = "BIND";

	private static final String SERVICE = "SERVICE";

	private static final String LGDO_ENDPOINT = "http://linkedgeodata.org/sparql";

	private static final String DBPEDIA_ENDPOINT = "http://dbpedia.org/sparql";

	private static final String LMDB_ENDPOINT = "http://data.linkedmdb.org/sparql";

	private List<OntModel> datasetList;

	private QueryReorganizer reorganizer;

	@Before
	public void before() {
		datasetList = new Vector<OntModel>();
		reorganizer = new QueryReorganizer();
	}

	@Test
	public void dontSplitTriplePatternsHaveMultipleEndpointsAndOPTIONALPatternTest()
			throws Exception {
		String query = "SELECT * WHERE {  <http://dbpedia.org/resource/Istanbul> ?p ?o.  OPTIONAL { ?x ?y ?z. }}";
		List<VOIDPathSolution> voidPathSolutionList = new Vector<VOIDPathSolution>();
		VOIDPathSolution vp1 = new VOIDPathSolution();
		vp1.addEndpoint(DBPEDIA_ENDPOINT);
		vp1.addEndpoint(LGDO_ENDPOINT);
		VOIDPathSolution vp2 = new VOIDPathSolution();
		vp2.addEndpoint(DBPEDIA_ENDPOINT);
		vp2.addEndpoint(LGDO_ENDPOINT);

		voidPathSolutionList.add(vp1);
		voidPathSolutionList.add(vp2);
		String federatedQuery = reorganizer.reorganizeWholeQuery(
				voidPathSolutionList, query);
		System.out.println(federatedQuery);
		assertEquals(145, federatedQuery.indexOf(SERVICE));
	}

	/**
	 * Triple patterns must be grouped when they have only internal relevant
	 * datasets.
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void dontSplitTriplePatternsHaveMultipleEndpointsTest()
			throws Exception {
		String query = "SELECT * WHERE {  ?s ?p ?o.   ?x ?y ?z. }";
		List<VOIDPathSolution> voidPathSolutionList = new Vector<VOIDPathSolution>();
		VOIDPathSolution vp1 = new VOIDPathSolution();
		vp1.addEndpoint(DBPEDIA_ENDPOINT);
		vp1.addEndpoint(LGDO_ENDPOINT);
		VOIDPathSolution vp2 = new VOIDPathSolution();
		vp2.addEndpoint(DBPEDIA_ENDPOINT);
		vp2.addEndpoint(LGDO_ENDPOINT);

		voidPathSolutionList.add(vp1);
		voidPathSolutionList.add(vp2);
		String federatedQuery = reorganizer.reorganizeWholeQuery(
				voidPathSolutionList, query);
		System.out.println(federatedQuery);
		assertEquals(148, federatedQuery.indexOf(SERVICE));
	}

	/**
	 * Triple patterns that have only one endpoint, internal or external doesn't
	 * matter. They must be grouped.
	 * 
	 * @throws Exception
	 */
	@Test
	public void dontSplitTriplePatternsHaveOneEndpointWhenTheyHaveExternalTest()
			throws Exception {
		String query = "SELECT * WHERE {  ?s ?p ?o.   ?s ?y ?z. }";
		List<VOIDPathSolution> voidPathSolutionList = new Vector<VOIDPathSolution>();
		VOIDPathSolution vp1 = new VOIDPathSolution();
		vp1.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp2 = new VOIDPathSolution();
		vp2.addEndpoint(DBPEDIA_ENDPOINT);

		// set external state true
		vp2.setExternalState(0, true);

		voidPathSolutionList.add(vp1);
		voidPathSolutionList.add(vp2);
		String federatedQuery = reorganizer.reorganizeWholeQuery(
				voidPathSolutionList, query);
		System.out.println(federatedQuery);
		assertEquals(30, federatedQuery.indexOf(SERVICE));
		assertEquals(-1, federatedQuery.indexOf(SERVICE, 31));

	}

	@Test
	public void eliminateUnrelatedTripleFromGroupForThreeTripleTwoMismatch()
			throws Exception {
		String query = "SELECT * WHERE { "
				+ "<http://dbpedia.org/resource/Turkey> ?p1 ?o1. "
				+ "<http://data.linkedmdb.org/resource/film/138> ?p2 ?o1. "
				+ "<http://dbpedia.org/resource/Izmir> ?p3 ?o2. " + "}";
		List<VOIDPathSolution> voidPathSolutionList = new Vector<VOIDPathSolution>();
		VOIDPathSolution vp1 = new VOIDPathSolution();
		vp1.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp2 = new VOIDPathSolution();
		vp2.addEndpoint(LMDB_ENDPOINT);
		VOIDPathSolution vp3 = new VOIDPathSolution();
		vp3.addEndpoint(DBPEDIA_ENDPOINT);

		voidPathSolutionList.add(vp1);
		voidPathSolutionList.add(vp2);
		voidPathSolutionList.add(vp3);

		String federatedQuery = reorganizer.reorganizeWholeQuery(
				voidPathSolutionList, query);

		System.out.println(federatedQuery);

		// get service triple blocks and service nodes in federated query
		ArrayList<Element> serviceTripleBlockList = new ArrayList<Element>();
		ArrayList<Node> serviceNodeList = new ArrayList<Node>();
		generateServiceTripleAndNodeList(QueryFactory.create(federatedQuery)
				.getQueryPattern(), serviceTripleBlockList, serviceNodeList,
				new ArrayList<Element>());

		// check size of service node list and service triple block list
		assertEquals(3, serviceNodeList.size());
		assertEquals(3, serviceTripleBlockList.size());

		String tp1 = "<http://dbpedia.org/resource/Turkey> ?p1 ?o1";
		String tp2 = "<http://data.linkedmdb.org/resource/film/138> ?p2 ?o1";
		String tp3 = "<http://dbpedia.org/resource/Izmir> ?p3 ?o2";

		// check for DBPEDIA endpoint and triple block
		checkEndpointAndTriples(generateList(DBPEDIA_ENDPOINT),
				generateList(serviceNodeList.get(0).getURI()),
				serviceTripleBlockList.get(0).toString(), generateList(tp3),
				generateList(tp1, tp2));

		// check for DBPEDIA endpoint and triple block
		checkEndpointAndTriples(generateList(DBPEDIA_ENDPOINT),
				generateList(serviceNodeList.get(1).getURI()),
				serviceTripleBlockList.get(1).toString(), generateList(tp1),
				generateList(tp2, tp3));

		// check for LMDB endpoint and triple block
		checkEndpointAndTriples(generateList(LMDB_ENDPOINT),
				generateList(serviceNodeList.get(2).getURI()),
				serviceTripleBlockList.get(2).toString(), generateList(tp2),
				generateList(tp1, tp3));

	}

	@Test
	public void eliminateUnrelatedTripleFromGroupForFourTripleOneMismatch()
			throws Exception {
		String query = "SELECT * WHERE { "
				+ "<http://dbpedia.org/resource/Turkey> ?p1 ?o1. "
				+ "?o1 ?p2 <http://dbpedia.org/resource/Istanbul>. "
				+ "<http://data.linkedmdb.org/resource/film/138> ?p3 ?o1. "
				+ "<http://dbpedia.org/resource/Izmir> ?p4 ?o2. " + "}";
		List<VOIDPathSolution> voidPathSolutionList = new Vector<VOIDPathSolution>();
		VOIDPathSolution vp1 = new VOIDPathSolution();
		vp1.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp2 = new VOIDPathSolution();
		vp2.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp3 = new VOIDPathSolution();
		vp3.addEndpoint(LMDB_ENDPOINT);
		VOIDPathSolution vp4 = new VOIDPathSolution();
		vp4.addEndpoint(DBPEDIA_ENDPOINT);

		voidPathSolutionList.add(vp1);
		voidPathSolutionList.add(vp2);
		voidPathSolutionList.add(vp3);
		voidPathSolutionList.add(vp4);

		String federatedQuery = reorganizer.reorganizeWholeQuery(
				voidPathSolutionList, query);

		System.out.println(federatedQuery);

		// get service triple blocks and service nodes in federated query
		ArrayList<Element> serviceTripleBlockList = new ArrayList<Element>();
		ArrayList<Node> serviceNodeList = new ArrayList<Node>();
		generateServiceTripleAndNodeList(QueryFactory.create(federatedQuery)
				.getQueryPattern(), serviceTripleBlockList, serviceNodeList,
				new ArrayList<Element>());

		// check size of service node list and service triple block list
		assertEquals(3, serviceNodeList.size());
		assertEquals(3, serviceTripleBlockList.size());

		String tp1 = "<http://dbpedia.org/resource/Turkey> ?p1 ?o1";
		String tp2 = "?o1 ?p2 <http://dbpedia.org/resource/Istanbul>";
		String tp3 = "<http://data.linkedmdb.org/resource/film/138> ?p3 ?o1";
		String tp4 = "<http://dbpedia.org/resource/Izmir> ?p4 ?o2";

		// check for LMDB endpoint and triple block
		checkEndpointAndTriples(generateList(LMDB_ENDPOINT),
				generateList(serviceNodeList.get(0).getURI()),
				serviceTripleBlockList.get(0).toString(), generateList(tp3),
				generateList(tp1, tp2, tp4));

		// check for DBPEDIA endpoint and triple block
		checkEndpointAndTriples(generateList(DBPEDIA_ENDPOINT),
				generateList(serviceNodeList.get(1).getURI()),
				serviceTripleBlockList.get(1).toString(),
				generateList(tp1, tp2), generateList(tp3, tp4));

		// check for DBPEDIA endpoint and triple block
		checkEndpointAndTriples(generateList(DBPEDIA_ENDPOINT),
				generateList(serviceNodeList.get(2).getURI()),
				serviceTripleBlockList.get(2).toString(), generateList(tp4),
				generateList(tp1, tp2, tp3));

	}

	@Test
	public void dontEliminateUnrelatedTripleFromGroupForFourTripleNoMismatch()
			throws Exception {
		String query = "SELECT * WHERE { "
				+ "<http://dbpedia.org/resource/Turkey> ?p1 ?o1. "
				+ "?o1 ?p2 <http://dbpedia.org/resource/Istanbul>. "
				+ "<http://data.linkedmdb.org/resource/film/138> ?p3 ?o1. "
				+ "<http://dbpedia.org/resource/Izmir> ?p2 ?o2. " + "}";
		List<VOIDPathSolution> voidPathSolutionList = new Vector<VOIDPathSolution>();
		VOIDPathSolution vp1 = new VOIDPathSolution();
		vp1.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp2 = new VOIDPathSolution();
		vp2.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp3 = new VOIDPathSolution();
		vp3.addEndpoint(LMDB_ENDPOINT);
		VOIDPathSolution vp4 = new VOIDPathSolution();
		vp4.addEndpoint(DBPEDIA_ENDPOINT);

		voidPathSolutionList.add(vp1);
		voidPathSolutionList.add(vp2);
		voidPathSolutionList.add(vp3);
		voidPathSolutionList.add(vp4);

		String federatedQuery = reorganizer.reorganizeWholeQuery(
				voidPathSolutionList, query);

		System.out.println(federatedQuery);

		// get service triple blocks and service nodes in federated query
		ArrayList<Element> serviceTripleBlockList = new ArrayList<Element>();
		ArrayList<Node> serviceNodeList = new ArrayList<Node>();
		generateServiceTripleAndNodeList(QueryFactory.create(federatedQuery)
				.getQueryPattern(), serviceTripleBlockList, serviceNodeList,
				new ArrayList<Element>());

		// check size of service node list and service triple block list
		assertEquals(2, serviceNodeList.size());
		assertEquals(2, serviceTripleBlockList.size());

		String tp1 = "<http://dbpedia.org/resource/Turkey> ?p1 ?o1";
		String tp2 = "?o1 ?p2 <http://dbpedia.org/resource/Istanbul>";
		String tp3 = "<http://data.linkedmdb.org/resource/film/138> ?p3 ?o1";
		String tp4 = "<http://dbpedia.org/resource/Izmir> ?p2 ?o2";

		// check for LMDB endpoint and triple block
		checkEndpointAndTriples(generateList(LMDB_ENDPOINT),
				generateList(serviceNodeList.get(0).getURI()),
				serviceTripleBlockList.get(0).toString(), generateList(tp3),
				generateList(tp1, tp2, tp4));

		// check for DBPEDIA endpoint and triple block
		checkEndpointAndTriples(generateList(DBPEDIA_ENDPOINT),
				generateList(serviceNodeList.get(1).getURI()),
				serviceTripleBlockList.get(1).toString(),
				generateList(tp1, tp2, tp4), generateList(tp3));

	}

	@Test
	public void dontEliminateUnrelatedTripleFromGroupForFourTripleThreeMismatch()
			throws Exception {
		String query = "SELECT * WHERE { "
				+ "<http://dbpedia.org/resource/Turkey> ?p1 ?o1. "
				+ "?s1 ?p2 <http://dbpedia.org/resource/Istanbul>. "
				+ "<http://data.linkedmdb.org/resource/film/138> ?p3 ?o3. "
				+ "<http://dbpedia.org/resource/Izmir> ?p4 ?o3. " + "}";
		List<VOIDPathSolution> voidPathSolutionList = new Vector<VOIDPathSolution>();
		VOIDPathSolution vp1 = new VOIDPathSolution();
		vp1.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp2 = new VOIDPathSolution();
		vp2.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp3 = new VOIDPathSolution();
		vp3.addEndpoint(LMDB_ENDPOINT);
		VOIDPathSolution vp4 = new VOIDPathSolution();
		vp4.addEndpoint(DBPEDIA_ENDPOINT);

		voidPathSolutionList.add(vp1);
		voidPathSolutionList.add(vp2);
		voidPathSolutionList.add(vp3);
		voidPathSolutionList.add(vp4);

		String federatedQuery = reorganizer.reorganizeWholeQuery(
				voidPathSolutionList, query);

		System.out.println(federatedQuery);

		// get service triple blocks and service nodes in federated query
		ArrayList<Element> serviceTripleBlockList = new ArrayList<Element>();
		ArrayList<Node> serviceNodeList = new ArrayList<Node>();
		generateServiceTripleAndNodeList(QueryFactory.create(federatedQuery)
				.getQueryPattern(), serviceTripleBlockList, serviceNodeList,
				new ArrayList<Element>());

		// check size of service node list and service triple block list
		assertEquals(4, serviceNodeList.size());
		assertEquals(4, serviceTripleBlockList.size());

		String tp1 = "<http://dbpedia.org/resource/Turkey> ?p1 ?o1";
		String tp2 = "?s1 ?p2 <http://dbpedia.org/resource/Istanbul>";
		String tp3 = "<http://data.linkedmdb.org/resource/film/138> ?p3 ?o3";
		String tp4 = "<http://dbpedia.org/resource/Izmir> ?p4 ?o3";

		// check for DBPEDIA endpoint and triple block
		checkEndpointAndTriples(generateList(DBPEDIA_ENDPOINT),
				generateList(serviceNodeList.get(0).getURI()),
				serviceTripleBlockList.get(0).toString(), generateList(tp4),
				generateList(tp1, tp2, tp3));

		// check for LMDB endpoint and triple block
		checkEndpointAndTriples(generateList(LMDB_ENDPOINT),
				generateList(serviceNodeList.get(1).getURI()),
				serviceTripleBlockList.get(1).toString(), generateList(tp3),
				generateList(tp1, tp2, tp4));

		// check for DBPEDIA endpoint and triple block
		checkEndpointAndTriples(generateList(DBPEDIA_ENDPOINT),
				generateList(serviceNodeList.get(2).getURI()),
				serviceTripleBlockList.get(2).toString(), generateList(tp1),
				generateList(tp2, tp3, tp4));

		// check for DBPEDIA endpoint and triple block
		checkEndpointAndTriples(generateList(DBPEDIA_ENDPOINT),
				generateList(serviceNodeList.get(3).getURI()),
				serviceTripleBlockList.get(3).toString(), generateList(tp2),
				generateList(tp1, tp3, tp4));

	}

	@Test
	public void heuristicCostAnalysisWithNoCommonVariable() throws Exception {

		// create triples that for testing all level cost heuristic

		Triple firstLevel = Triple
				.create(Node
						.createURI("http://dbpedia.org/resource/Steven_Spielberg"),
						Node.createVariable("p1"),
						Node.createURI("http://dbpedia.org/resource/Firelight_(1964_film)"));

		Triple secondLevel = Triple.create(
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"),
				Node.createURI("http://www.w3.org/2002/07/owl#sameAs"),
				Node.createVariable("o2"));

		Triple thirdLevel = Triple.create(
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"),
				Node.createVariable("p3"), Node.createLiteral("1946"));

		Triple fourthLevel = Triple.create(
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"),
				Node.createVariable("p4"), Node.createVariable("o4"));

		Triple fifthLevel = Triple.create(Node.createVariable("s5"),
				Node.createURI("http://dbpedia.org/ontology/director"),
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"));

		Triple sixthLevel = Triple.create(Node.createVariable("s6"),
				Node.createURI("http://dbpedia.org/property/dateOfBirth"),
				Node.createLiteral("1946"));

		Triple seventhLevel = Triple.create(Node.createVariable("s7"),
				Node.createVariable("p7"),
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"));

		Triple eighthLevel = Triple.create(Node.createVariable("s8"),
				Node.createURI("http://www.w3.org/2002/07/owl#sameAs"),
				Node.createVariable("o8"));

		Triple ninthLevel = Triple.create(Node.createVariable("s9"),
				Node.createVariable("p9"), Node.createLiteral("1946"));

		Triple tenthLevel = Triple.create(Node.createVariable("s10"),
				Node.createVariable("p10"), Node.createVariable("o10"));

		// define a triple list to be sorted
		List<Triple> tripleList = new ArrayList<Triple>();

		// add all triples to list to be sorted
		tripleList.add(fourthLevel);
		tripleList.add(ninthLevel);
		tripleList.add(sixthLevel);
		tripleList.add(firstLevel);
		tripleList.add(fifthLevel);
		tripleList.add(secondLevel);
		tripleList.add(eighthLevel);
		tripleList.add(tenthLevel);
		tripleList.add(seventhLevel);
		tripleList.add(thirdLevel);

		// generate determined triple list using given triple list
		List<DeterminedTriple> determinedTripleGroup = convertTriplesToDeterminedTriples(tripleList);
		// order(sort) triples
		List<ValuedTriple> valuedTripleGroup = reorganizer
				.orderTriples(determinedTripleGroup);
		// generate triple list using given determined triple list
		List<Triple> orderedTripleList = convertValuedTriplesToTriples(valuedTripleGroup);

		// check triples has been sorted correctly
		assertEquals(10, orderedTripleList.size());
		assertEquals(firstLevel, orderedTripleList.get(0));
		assertEquals(secondLevel, orderedTripleList.get(1));
		assertEquals(thirdLevel, orderedTripleList.get(2));
		assertEquals(fourthLevel, orderedTripleList.get(3));
		assertEquals(fifthLevel, orderedTripleList.get(4));
		assertEquals(sixthLevel, orderedTripleList.get(5));
		assertEquals(seventhLevel, orderedTripleList.get(6));
		assertEquals(eighthLevel, orderedTripleList.get(7));
		assertEquals(ninthLevel, orderedTripleList.get(8));
		assertEquals(tenthLevel, orderedTripleList.get(9));

	}

	@Test
	public void heuristicCostAnalysisWithCommonVariableIncludingFourTriples()
			throws Exception {

		// create triples that for testing all level cost heuristic

		Triple firstTP = Triple.create(
				Node.createURI("http://dbpedia.org/resource/Buca"),
				Node.createURI("http://dbpedia.org/ontology/isPartOf"),
				Node.createVariable("city"));

		Triple secondTP = Triple.create(Node.createVariable("person"),
				Node.createURI("http://dbpedia.org/ontology/birthPlace"),
				Node.createVariable("place"));

		Triple thirdTP = Triple.create(Node.createVariable("city"),
				Node.createURI("http://dbpedia.org/ontology/neighbor"),
				Node.createVariable("place"));

		Triple fourthTP = Triple.create(Node.createVariable("person"),
				Node.createURI("http://xmlns.com/foaf/0.1/knows"),
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"));

		// define a triple list to be sorted
		List<Triple> tripleList = new ArrayList<Triple>();

		// add all triples to list to be sorted
		tripleList.add(firstTP);
		tripleList.add(secondTP);
		tripleList.add(thirdTP);
		tripleList.add(fourthTP);

		// generate determined triple list using given triple list
		List<DeterminedTriple> determinedTripleGroup = convertTriplesToDeterminedTriples(tripleList);
		// order(sort) triples
		List<ValuedTriple> valuedTripleGroup = reorganizer
				.orderTriples(determinedTripleGroup);
		// generate triple list using given determined triple list
		List<Triple> orderedTriples = convertValuedTriplesToTriples(valuedTripleGroup);

		// check triples has been sorted correctly
		assertEquals(4, orderedTriples.size());
		assertEquals(firstTP, orderedTriples.get(0));
		assertEquals(thirdTP, orderedTriples.get(1));
		assertEquals(secondTP, orderedTriples.get(2));
		assertEquals(fourthTP, orderedTriples.get(3));

	}

	@Test
	public void heuristicCostAnalysisWithCommonVariableIncludingFiveTriples()
			throws Exception {

		// create triples that for testing all level cost heuristic

		Triple firstTP = Triple.create(Node.createVariable("person"),
				Node.createURI("http://xmlns.com/foaf/0.1/knows"),
				Node.createVariable("friend"));

		Triple secondTP = Triple.create(Node.createVariable("film"),
				Node.createVariable("predicate"),
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"));

		Triple thirdTP = Triple.create(Node.createVariable("predicate"),
				Node.createURI("http://seagent.ege.edu.tr#owner"),
				Node.createVariable("person"));

		Triple fourthTP = Triple.create(
				Node.createURI("http://dbpedia.org/resource/The_Matrix"),
				Node.createVariable("predicate"),
				Node.createVariable("director"));

		Triple fifthTP = Triple.create(Node.createVariable("director"),
				Node.createURI("http://dbpedia.org/ontology/education"),
				Node.createVariable("education"));

		// define a triple list to be sorted
		List<Triple> tripleList = new ArrayList<Triple>();

		// add all triples to list to be sorted
		tripleList.add(firstTP);
		tripleList.add(secondTP);
		tripleList.add(thirdTP);
		tripleList.add(fourthTP);
		tripleList.add(fifthTP);

		// generate determined triple list using given triple list
		List<DeterminedTriple> determinedTripleGroup = convertTriplesToDeterminedTriples(tripleList);
		// order(sort) triples
		List<ValuedTriple> valuedTripleGroup = reorganizer
				.orderTriples(determinedTripleGroup);
		// generate triple list using given determined triple list
		List<Triple> orderedTriples = convertValuedTriplesToTriples(valuedTripleGroup);

		// check triples has been sorted correctly
		assertEquals(5, orderedTriples.size());
		assertEquals(fourthTP, orderedTriples.get(0));
		assertEquals(secondTP, orderedTriples.get(1));
		assertEquals(thirdTP, orderedTriples.get(2));
		assertEquals(firstTP, orderedTriples.get(3));
		assertEquals(fifthTP, orderedTriples.get(4));

	}

	@Test
	public void reorderThreeGroupsWhenFirstAndLastOneHaveMostCommonVariable()
			throws Exception {
		/**
		 * define 3 groups as reverse order
		 */
		// first group with lowest value
		Triple sixthLevel = Triple.create(Node.createVariable("s"),
				Node.createURI("http://dbpedia.org/property/dateOfBirth"),
				Node.createLiteral("1946"));

		Triple seventhLevel = Triple.create(Node.createVariable("s"),
				Node.createVariable("p"),
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"));

		Triple ninthLevel = Triple.create(Node.createVariable("s"),
				Node.createVariable("p"), Node.createLiteral("1946"));

		// add triples to lowest valued triple group
		TripleGroup groupWithLowestValue = new TripleGroup();
		groupWithLowestValue.add(new ValuedTriple(new DeterminedTriple(
				sixthLevel, null), 24));
		groupWithLowestValue.add(new ValuedTriple(new DeterminedTriple(
				seventhLevel, null), 22));
		groupWithLowestValue.add(new ValuedTriple(new DeterminedTriple(
				ninthLevel, null), 16));

		// second group with middle value
		Triple secondLevel = Triple.create(
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"),
				Node.createURI("http://www.w3.org/2002/07/owl#sameAs"),
				Node.createVariable("o2"));

		Triple thirdLevel = Triple.create(
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"),
				Node.createVariable("p"), Node.createLiteral("1946"));

		Triple fourthLevel = Triple.create(
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"),
				Node.createVariable("p"), Node.createVariable("o4"));

		// add triples to middle valued triple group
		TripleGroup groupWithMiddleValue = new TripleGroup();
		groupWithMiddleValue.add(new ValuedTriple(new DeterminedTriple(
				secondLevel, null), 38));
		groupWithMiddleValue.add(new ValuedTriple(new DeterminedTriple(
				thirdLevel, null), 36));
		groupWithMiddleValue.add(new ValuedTriple(new DeterminedTriple(
				fourthLevel, null), 31));

		// third group with highest value
		Triple firstLevel = Triple
				.create(Node
						.createURI("http://dbpedia.org/resource/Steven_Spielberg"),
						Node.createVariable("p"),
						Node.createURI("http://dbpedia.org/resource/Firelight_(1964_film)"));

		Triple fifthLevel = Triple.create(Node.createVariable("s"),
				Node.createURI("http://dbpedia.org/ontology/director"),
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"));

		// add triples to middle valued triple group
		TripleGroup groupWithHighestValue = new TripleGroup();
		groupWithHighestValue.add(new ValuedTriple(new DeterminedTriple(
				firstLevel, null), 42));
		groupWithHighestValue.add(new ValuedTriple(new DeterminedTriple(
				fifthLevel, null), 30));

		// add created groups to group list
		List<TripleGroup> tripleGroups = new ArrayList<TripleGroup>();
		tripleGroups.add(groupWithLowestValue);
		tripleGroups.add(groupWithMiddleValue);
		tripleGroups.add(groupWithHighestValue);

		// reorder groups
		reorganizer.orderGroups(tripleGroups);

		// check groups after reordering
		assertEquals(groupWithHighestValue, tripleGroups.get(0));
		assertEquals(groupWithLowestValue, tripleGroups.get(1));
		assertEquals(groupWithMiddleValue, tripleGroups.get(2));
	}

	@Test
	public void heuristicCostAnalysisOrderingGroups() throws Exception {

		// define raw query
		String tp1 = "?s <http://dbpedia.org/property/dateOfBirth> \"1946\"";
		String tp2 = "?s ?p <http://dbpedia.org/resource/Steven_Spielberg>";
		String tp3 = "?s ?p \"1946\"";
		String tp4 = "<http://dbpedia.org/resource/Steven_Spielberg> <http://www.w3.org/2002/07/owl#sameAs> ?o2";
		String tp5 = "<http://dbpedia.org/resource/Steven_Spielberg> ?p \"1946\"";
		String tp6 = "<http://dbpedia.org/resource/Steven_Spielberg> ?p ?o2";
		String tp7 = "<http://dbpedia.org/resource/Steven_Spielberg> ?x <http://dbpedia.org/resource/Firelight_(1964_film)>";
		String tp8 = "?x <http://dbpedia.org/ontology/director> <http://dbpedia.org/resource/Steven_Spielberg>";
		String rawQuery = "SELECT * WHERE {" + tp1 + DOT + tp2 + DOT + tp3
				+ DOT + tp4 + DOT + tp5 + DOT + tp6 + DOT + tp7 + DOT + tp8
				+ DOT + "}";

		// generate void path solutions
		List<VOIDPathSolution> voidPathSolutionList = new Vector<VOIDPathSolution>();
		VOIDPathSolution vp1 = new VOIDPathSolution();
		vp1.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp2 = new VOIDPathSolution();
		vp2.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp3 = new VOIDPathSolution();
		vp3.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp4 = new VOIDPathSolution();
		vp4.addEndpoint(LGDO_ENDPOINT);
		VOIDPathSolution vp5 = new VOIDPathSolution();
		vp5.addEndpoint(LGDO_ENDPOINT);
		VOIDPathSolution vp6 = new VOIDPathSolution();
		vp6.addEndpoint(LGDO_ENDPOINT);
		VOIDPathSolution vp7 = new VOIDPathSolution();
		vp7.addEndpoint(LMDB_ENDPOINT);
		VOIDPathSolution vp8 = new VOIDPathSolution();
		vp8.addEndpoint(LMDB_ENDPOINT);

		// add all void path solutions to list
		voidPathSolutionList.add(vp1);
		voidPathSolutionList.add(vp2);
		voidPathSolutionList.add(vp3);
		voidPathSolutionList.add(vp4);
		voidPathSolutionList.add(vp5);
		voidPathSolutionList.add(vp6);
		voidPathSolutionList.add(vp7);
		voidPathSolutionList.add(vp8);

		// reorganize query
		String federatedQuery = reorganizer.reorganizeWholeQuery(
				voidPathSolutionList, rawQuery);
		System.out.println(federatedQuery);

		// get service triple blocks and service nodes in federated query
		ArrayList<Element> serviceTripleBlockList = new ArrayList<Element>();
		ArrayList<Node> serviceNodeList = new ArrayList<Node>();
		generateServiceTripleAndNodeList(QueryFactory.create(federatedQuery)
				.getQueryPattern(), serviceTripleBlockList, serviceNodeList,
				new ArrayList<Element>());

		// check size of service node list and service triple block list
		assertEquals(3, serviceNodeList.size());
		assertEquals(3, serviceTripleBlockList.size());

		// check for LMDB endpoint and triple block
		checkEndpointAndTriples(generateList(LMDB_ENDPOINT),
				generateList(serviceNodeList.get(0).getURI()),
				serviceTripleBlockList.get(0).toString(),
				generateList(tp7, tp8),
				generateList(tp1, tp2, tp3, tp4, tp5, tp6));

		// check for LGDO endpoint and triple block
		checkEndpointAndTriples(generateList(LGDO_ENDPOINT),
				generateList(serviceNodeList.get(1).getURI()),
				serviceTripleBlockList.get(1).toString(),
				generateList(tp4, tp5, tp6),
				generateList(tp1, tp2, tp3, tp7, tp8));

		// check for DBPEDIA endpoint and triple block
		checkEndpointAndTriples(generateList(DBPEDIA_ENDPOINT),
				generateList(serviceNodeList.get(2).getURI()),
				serviceTripleBlockList.get(2).toString(),
				generateList(tp1, tp2, tp3),
				generateList(tp4, tp5, tp6, tp7, tp8));
	}

	@Test
	public void reorderThreeGroupsWhenFirstAndSecondOneHaveMostCommonVariable()
			throws Exception {
		/**
		 * define 3 groups as reverse order
		 */
		// first group with lowest value
		Triple sixthLevel = Triple.create(Node.createVariable("s1"),
				Node.createURI("http://dbpedia.org/property/dateOfBirth"),
				Node.createLiteral("1946"));

		Triple seventhLevel = Triple.create(Node.createVariable("s1"),
				Node.createVariable("p1"),
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"));

		Triple ninthLevel = Triple.create(Node.createVariable("s"),
				Node.createVariable("p1"), Node.createLiteral("1946"));

		// add triples to lowest valued triple group
		TripleGroup groupWithLowestValue = new TripleGroup();
		groupWithLowestValue.add(new ValuedTriple(new DeterminedTriple(
				sixthLevel, null), 24));
		groupWithLowestValue.add(new ValuedTriple(new DeterminedTriple(
				seventhLevel, null), 22));
		groupWithLowestValue.add(new ValuedTriple(new DeterminedTriple(
				ninthLevel, null), 16));

		// second group with middle value
		Triple secondLevel = Triple.create(
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"),
				Node.createURI("http://www.w3.org/2002/07/owl#sameAs"),
				Node.createVariable("o2"));

		Triple thirdLevel = Triple.create(
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"),
				Node.createVariable("p"), Node.createLiteral("1946"));

		Triple fourthLevel = Triple.create(
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"),
				Node.createVariable("p"), Node.createVariable("o4"));

		// add triples to middle valued triple group
		TripleGroup groupWithMiddleValue = new TripleGroup();
		groupWithMiddleValue.add(new ValuedTriple(new DeterminedTriple(
				secondLevel, null), 38));
		groupWithMiddleValue.add(new ValuedTriple(new DeterminedTriple(
				thirdLevel, null), 36));
		groupWithMiddleValue.add(new ValuedTriple(new DeterminedTriple(
				fourthLevel, null), 31));

		// third group with highest value
		Triple firstLevel = Triple
				.create(Node
						.createURI("http://dbpedia.org/resource/Steven_Spielberg"),
						Node.createVariable("p"),
						Node.createURI("http://dbpedia.org/resource/Firelight_(1964_film)"));

		Triple fifthLevel = Triple.create(Node.createVariable("s"),
				Node.createURI("http://dbpedia.org/ontology/director"),
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"));

		// add triples to middle valued triple group
		TripleGroup groupWithHighestValue = new TripleGroup();
		groupWithHighestValue.add(new ValuedTriple(new DeterminedTriple(
				firstLevel, null), 42));
		groupWithHighestValue.add(new ValuedTriple(new DeterminedTriple(
				fifthLevel, null), 30));

		// add created groups to group list
		List<TripleGroup> tripleGroups = new ArrayList<TripleGroup>();
		tripleGroups.add(groupWithLowestValue);
		tripleGroups.add(groupWithMiddleValue);
		tripleGroups.add(groupWithHighestValue);

		// reorder groups
		reorganizer.orderGroups(tripleGroups);

		// check groups after reordering
		assertEquals(groupWithHighestValue, tripleGroups.get(0));
		assertEquals(groupWithMiddleValue, tripleGroups.get(1));
		assertEquals(groupWithLowestValue, tripleGroups.get(2));
	}

	@Test
	public void reorderThreeGroupsWhenNoCommonVariable() throws Exception {

		/**
		 * define 3 groups as reverse order
		 */

		// first group with lowest value
		Triple sixthLevel = Triple.create(Node.createVariable("s6"),
				Node.createURI("http://dbpedia.org/property/dateOfBirth"),
				Node.createLiteral("1946"));

		Triple seventhLevel = Triple.create(Node.createVariable("s7"),
				Node.createVariable("p7"),
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"));

		Triple ninthLevel = Triple.create(Node.createVariable("s9"),
				Node.createVariable("p9"), Node.createLiteral("1946"));

		// add triples to lowest valued triple group
		TripleGroup groupWithLowestValue = new TripleGroup();
		groupWithLowestValue.add(new ValuedTriple(new DeterminedTriple(
				sixthLevel, null), 24));
		groupWithLowestValue.add(new ValuedTriple(new DeterminedTriple(
				seventhLevel, null), 22));
		groupWithLowestValue.add(new ValuedTriple(new DeterminedTriple(
				ninthLevel, null), 16));

		// second group with middle value
		Triple secondLevel = Triple.create(
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"),
				Node.createURI("http://www.w3.org/2002/07/owl#sameAs"),
				Node.createVariable("o2"));

		Triple thirdLevel = Triple.create(
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"),
				Node.createVariable("p3"), Node.createLiteral("1946"));

		Triple fourthLevel = Triple.create(
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"),
				Node.createVariable("p4"), Node.createVariable("o4"));

		// add triples to middle valued triple group
		TripleGroup groupWithMiddleValue = new TripleGroup();
		groupWithMiddleValue.add(new ValuedTriple(new DeterminedTriple(
				secondLevel, null), 38));
		groupWithMiddleValue.add(new ValuedTriple(new DeterminedTriple(
				thirdLevel, null), 36));
		groupWithMiddleValue.add(new ValuedTriple(new DeterminedTriple(
				fourthLevel, null), 31));

		// third group with highest value
		Triple firstLevel = Triple
				.create(Node
						.createURI("http://dbpedia.org/resource/Steven_Spielberg"),
						Node.createVariable("p1"),
						Node.createURI("http://dbpedia.org/resource/Firelight_(1964_film)"));

		Triple fifthLevel = Triple.create(Node.createVariable("s5"),
				Node.createURI("http://dbpedia.org/ontology/director"),
				Node.createURI("http://dbpedia.org/resource/Steven_Spielberg"));

		// add triples to middle valued triple group
		TripleGroup groupWithHighestValue = new TripleGroup();
		groupWithHighestValue.add(new ValuedTriple(new DeterminedTriple(
				firstLevel, null), 42));
		groupWithHighestValue.add(new ValuedTriple(new DeterminedTriple(
				fifthLevel, null), 30));

		// add created groups to group list
		List<TripleGroup> tripleGroups = new ArrayList<TripleGroup>();
		tripleGroups.add(groupWithLowestValue);
		tripleGroups.add(groupWithMiddleValue);
		tripleGroups.add(groupWithHighestValue);

		// reorder groups
		reorganizer.orderGroups(tripleGroups);

		// check groups after reordering
		assertEquals(groupWithHighestValue, tripleGroups.get(0));
		assertEquals(groupWithMiddleValue, tripleGroups.get(1));
		assertEquals(groupWithLowestValue, tripleGroups.get(2));

	}

	/**
	 * This method turns given {@link DeterminedTriple} list into {@link Triple}
	 * list
	 * 
	 * @param valuedTripleGroup
	 * @return
	 */
	private List<Triple> convertValuedTriplesToTriples(
			List<ValuedTriple> valuedTripleGroup) {
		List<Triple> tripleList = new ArrayList<Triple>();
		for (ValuedTriple valuedTriple : valuedTripleGroup) {
			tripleList.add(valuedTriple.getDeterminedTriple().getTriple());
		}
		return tripleList;
	}

	/**
	 * This method turns given {@link Triple} list into {@link DeterminedTriple}
	 * list
	 * 
	 * @param tripleList
	 * @return
	 */
	private List<DeterminedTriple> convertTriplesToDeterminedTriples(
			List<Triple> tripleList) {
		List<DeterminedTriple> determinedTriples = new ArrayList<DeterminedTriple>();
		for (Triple triple : tripleList) {
			determinedTriples.add(new DeterminedTriple(triple, null));
		}
		return determinedTriples;
	}

	@Test
	public void heuristicCostAnalysisOfGivenQuery() throws Exception {

		// create raw query to reorder triples
		String rawQuery = "SELECT * WHERE {<http://dbpedia.org/resource/Buca> <http://dbpedia.org/ontology/isPartOf> ?city. "
				+ "?person <http://dbpedia.org/ontology/birthPlace> ?place. "
				+ "?city <http://dbpedia.org/ontology/neighbor> ?place. "
				+ "?person <http://xmlns.com/foaf/0.1/knows> <http://dbpedia.org/resource/Steven_Spielberg>.}";

		// generate void path solutions
		List<VOIDPathSolution> voidPathSolutionList = new Vector<VOIDPathSolution>();
		VOIDPathSolution vp1 = new VOIDPathSolution();
		vp1.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp2 = new VOIDPathSolution();
		vp2.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp3 = new VOIDPathSolution();
		vp3.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp4 = new VOIDPathSolution();
		vp4.addEndpoint(DBPEDIA_ENDPOINT);

		// add all void path solutions to list
		voidPathSolutionList.add(vp1);
		voidPathSolutionList.add(vp2);
		voidPathSolutionList.add(vp3);
		voidPathSolutionList.add(vp4);

		// reorganize query
		String federatedQuery = reorganizer.reorganizeWholeQuery(
				voidPathSolutionList, rawQuery);

		// construct triple list for raw query
		List<Triple> rawTripleList = generateTripleList(rawQuery);
		// construct triple list for federated query
		List<Triple> organizedTripleList = generateTripleList(federatedQuery);
		// check their size
		assertEquals(rawTripleList.size(), 4);
		assertEquals(organizedTripleList.size(), 4);
		// check triples has been ordered correctly
		assertEquals(rawTripleList.get(0), organizedTripleList.get(0));
		assertEquals(rawTripleList.get(1), organizedTripleList.get(2));
		assertEquals(rawTripleList.get(2), organizedTripleList.get(1));
		assertEquals(rawTripleList.get(3), organizedTripleList.get(3));

		System.out.println(federatedQuery);

	}

	/**
	 * This method gets triples of given query
	 * 
	 * @param queryText
	 * @return
	 */
	private List<Triple> generateTripleList(String queryText) {
		Query query = QueryFactory.create(queryText);
		final List<Triple> tripleList = new Vector<Triple>();
		ElementVisitorBase tripleVisitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementPathBlock el) {
				Iterator<TriplePath> iterator = el.getPattern().iterator();
				while (iterator.hasNext()) {
					TriplePath triplePath = iterator.next();
					tripleList.add(triplePath.asTriple());
				}
				super.visit(el);
			}
		};
		ElementWalker.walk(query.getQueryPattern(), tripleVisitor);
		return tripleList;
	}

	/**
	 * This method fills given service triple block and service node list
	 * 
	 * @param queryBlock
	 * @param serviceTripleBlockList
	 * @param serviceNodeList
	 * @param elementUnionList
	 * @return
	 */
	private void generateServiceTripleAndNodeList(Element queryBlock,
			final List<Element> serviceTripleBlockList,
			final List<Node> serviceNodeList,
			final List<Element> elementUnionList) {
		ElementVisitorBase serviceVisitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementUnion el) {
				List<Element> elements = el.getElements();
				for (Element element : elements) {
					elementUnionList.add(element);
				}
				super.visit(el);
			}

			@Override
			public void visit(ElementService el) {
				serviceNodeList.add(el.getServiceNode());
				serviceTripleBlockList.add(el.getElement());
				super.visit(el);
			}
		};
		ElementWalker.walk(queryBlock, serviceVisitor);
	}

	@Test
	public void groupTriplePatternsWhenTheyAreNotAdjacentAndHaveSingleEndpointToQuery()
			throws Exception {
		// generate raw query
		String tp1 = "?s ?p ?o";
		String tp2 = "?x ?y ?z";
		String tp3 = "?o ?w ?e";
		String tp4 = "?z ?k ?l";
		String query = "SELECT * WHERE {" + tp1 + DOT + tp2 + DOT + tp3 + DOT
				+ tp4 + DOT + "}";
		// generate void path solutions
		List<VOIDPathSolution> voidPathSolutionList = new Vector<VOIDPathSolution>();
		VOIDPathSolution vp1 = new VOIDPathSolution();
		vp1.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp2 = new VOIDPathSolution();
		vp2.addEndpoint(LGDO_ENDPOINT);
		VOIDPathSolution vp3 = new VOIDPathSolution();
		vp3.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp4 = new VOIDPathSolution();
		vp4.addEndpoint(LGDO_ENDPOINT);

		// add all void path solutions to list
		voidPathSolutionList.add(vp1);
		voidPathSolutionList.add(vp2);
		voidPathSolutionList.add(vp3);
		voidPathSolutionList.add(vp4);

		// reorganize query
		String federatedQuery = reorganizer.reorganizeWholeQuery(
				voidPathSolutionList, query);
		System.out.println(federatedQuery);

		// get service triple blocks and service nodes in federated query
		ArrayList<Element> serviceTripleBlockList = new ArrayList<Element>();
		ArrayList<Node> serviceNodeList = new ArrayList<Node>();
		generateServiceTripleAndNodeList(QueryFactory.create(federatedQuery)
				.getQueryPattern(), serviceTripleBlockList, serviceNodeList,
				new ArrayList<Element>());

		assertEquals(2, serviceNodeList.size());
		assertEquals(2, serviceTripleBlockList.size());

		// check for DBPEDIA endpoint and triple block
		checkEndpointAndTriples(generateList(DBPEDIA_ENDPOINT),
				generateList(serviceNodeList.get(0).getURI()),
				serviceTripleBlockList.get(0).toString(),
				generateList(tp1, tp3), generateList(tp2, tp4));

		// check for LGDO endpoint and triple block
		checkEndpointAndTriples(generateList(LGDO_ENDPOINT),
				generateList(serviceNodeList.get(1).getURI()),
				serviceTripleBlockList.get(1).toString(),
				generateList(tp2, tp4), generateList(tp1, tp3));

	}

	@Test
	public void notGroupTriplePatternsWhenTheyAreNotAdjacentAndHaveMultipleEndpointToQuery()
			throws Exception {
		// generate raw query
		String tp1 = "?s ?p ?o";
		String tp2 = "?x ?y ?z";
		String tp3 = "?q ?w ?e";
		String query = "SELECT * WHERE {" + tp1 + DOT + tp2 + DOT + tp3 + DOT
				+ "}";
		// generate void path solutions
		List<VOIDPathSolution> voidPathSolutionList = new Vector<VOIDPathSolution>();
		VOIDPathSolution vp1 = new VOIDPathSolution();
		vp1.addEndpoint(LMDB_ENDPOINT);
		vp1.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp2 = new VOIDPathSolution();
		vp2.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp3 = new VOIDPathSolution();
		vp3.addEndpoint(DBPEDIA_ENDPOINT);
		vp3.addEndpoint(LMDB_ENDPOINT);

		// add all void path solutions to list
		voidPathSolutionList.add(vp1);
		voidPathSolutionList.add(vp2);
		voidPathSolutionList.add(vp3);

		// reorganize query
		String federatedQuery = reorganizer.reorganizeWholeQuery(
				voidPathSolutionList, query);
		System.out.println(federatedQuery);

		// get service triple blocks and service nodes in federated query
		ArrayList<Element> serviceTripleBlockList = new ArrayList<Element>();
		ArrayList<Node> serviceNodeList = new ArrayList<Node>();
		ArrayList<Element> elementUnionList = new ArrayList<Element>();
		generateServiceTripleAndNodeList(QueryFactory.create(federatedQuery)
				.getQueryPattern(), serviceTripleBlockList, serviceNodeList,
				elementUnionList);

		assertEquals(3, serviceNodeList.size());
		assertEquals(3, serviceTripleBlockList.size());
		assertEquals(4, elementUnionList.size());

		// check first piece contains lmdb and dbpedia endpoint
		checkEndpointAndTriples(
				generateList(LMDB_ENDPOINT, DBPEDIA_ENDPOINT),
				generateList(elementUnionList.get(0).toString(),
						elementUnionList.get(1).toString()),
				serviceTripleBlockList.get(0).toString(), generateList(tp1),
				generateList(tp2, tp3));

		// check second piece contains dbpedia endpoint and second piece of
		// triple
		checkEndpointAndTriples(generateList(DBPEDIA_ENDPOINT),
				generateList(serviceNodeList.get(1).getURI()),
				serviceTripleBlockList.get(1).toString(), generateList(tp2),
				generateList(tp1, tp3));

		// check first piece contains lmdb and dbpedia endpoint
		checkEndpointAndTriples(
				generateList(DBPEDIA_ENDPOINT, LMDB_ENDPOINT),
				generateList(elementUnionList.get(2).toString(),
						elementUnionList.get(3).toString()),
				serviceTripleBlockList.get(2).toString(), generateList(tp3),
				generateList(tp1, tp2));

	}

	/**
	 * This method generates a list from given parameters
	 * 
	 * @param parameters
	 *            to generate a list
	 * @return generated parameter {@link List} instance.
	 */
	private List<String> generateList(String... parameters) {
		List<String> parameterList = new ArrayList<String>();
		if (parameters != null && parameters.length != 0) {
			for (String parameter : parameters) {
				parameterList.add(parameter);
			}
		}
		return parameterList;
	}

	private void checkEndpointAndTriples(List<String> expectedEndpointList,
			List<String> actualEndpointList, String triplePiece,
			List<String> triplesContained, List<String> triplesNotContained) {

		for (int i = 0; i < actualEndpointList.size(); i++) {
			String actualEndpoint = actualEndpointList.get(i);
			String expectedEndpoint = expectedEndpointList.get(i);
			assertTrue(actualEndpoint.contains(expectedEndpoint));
		}
		for (String tripleContained : triplesContained) {
			assertTrue(triplePiece.contains(tripleContained));
		}

		for (String tripleNotContained : triplesNotContained) {
			assertFalse(triplePiece.contains(tripleNotContained));
		}
	}

	/**
	 * This method merges given dataset {@link Model} list into one
	 * {@link Model}
	 * 
	 * @param datasets
	 * @return
	 */
	protected Model mergeModels(Model... datasets) {
		Model mainModel = ModelFactory.createDefaultModel();
		for (Model dataset : datasets) {
			mainModel.add(dataset);
		}
		return mainModel;
	}

	@Test
	public void filterInUnionQuerytest() throws Exception {
		String query = "prefix foaf: <http://xmlns.com/foaf/0.1/> Select * where { {?s foaf:name ?l.} UNION {?s ?p ?o} FILTER regex(?l, \"web\", \"i\")  }";
		Model mainModel = mergeModels(createDBpediaVOID());
		List<VOIDPathSolution> voidPathList = new QueryAnalyzer(mainModel,
				false).analyze(query);
		String federatedQuery = reorganizer.reorganizeWholeQuery(voidPathList,
				query);
		System.out.println(federatedQuery);
		assertTrue(165 <= federatedQuery
				.indexOf("FILTER regex(?l, \"web\", \"i\")"));

	}

	@Test
	public void limitandOffsetKeywordTest() throws Exception {
		String query = "prefix foaf: <http://xmlns.com/foaf/0.1/> Select * where { {?s foaf:name ?l. ?s ?p ?o} FILTER (?p!=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>) } LIMIT 20 OFFSET 20";
		Model mainModel = mergeModels(createDBpediaVOID());
		List<VOIDPathSolution> voidPathList = new QueryAnalyzer(mainModel,
				false).analyze(query);
		String federatedQuery = reorganizer.reorganizeWholeQuery(voidPathList,
				query);
		assertTrue(federatedQuery.contains("OFFSET 20"));
		assertTrue(federatedQuery.contains("LIMIT 20"));
	}

	@Test
	public void parseMultipleEndpoints() throws Exception {
		String endp1 = "http://ec2-204-236-199-224.compute-1.amazonaws.com/joseki/service/facebook";
		String endp2 = "http://ec2-204-236-199-224.compute-1.amazonaws.com/joseki/service/foursquare";
		String endp3 = "http://ec2-204-236-199-224.compute-1.amazonaws.com/joseki/service/linkedin";
		List<VOIDPathSolution> list = new Vector<VOIDPathSolution>();
		VOIDPathSolution voidPathSolution = new VOIDPathSolution();
		voidPathSolution.addEndpoint(endp1);
		voidPathSolution.addEndpoint(endp2);
		voidPathSolution.addEndpoint(endp3);
		list.add(voidPathSolution);
		String reorganizedQuery = reorganizer.reorganizeWholeQuery(list,
				"SELECT * WHERE {?s ?p ?o}");
		System.out.println(reorganizedQuery);
	}

	/**
	 * This tests check that the reorganizer does not convert RDF type URI to
	 * 'rdf:type' abbreviated form.
	 * 
	 * @throws Exception
	 */
	@Test
	public void rdfTypePropURIInFilterTest() throws Exception {
		String query = "prefix foaf: <http://xmlns.com/foaf/0.1/> Select * where { {?s foaf:name ?l. ?s ?p ?o} FILTER (?p!=<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>) }";
		System.out.println(QueryFactory.create(query).serialize());
		Model mainModel = mergeModels(createDBpediaVOID());
		List<VOIDPathSolution> voidPathList = new QueryAnalyzer(mainModel,
				false).analyze(query);
		String federatedQuery = reorganizer.reorganizeWholeQuery(voidPathList,
				query);
		assertFalse(federatedQuery.contains("rdf:type"));
	}

	@Test
	public void reorganizeFilterQueryTest() throws Exception {
		String query = "prefix foaf: <http://xmlns.com/foaf/0.1/> Select * Where {?s foaf:name ?l. ?s ?p ?o. FILTER (?l=\"Ennio Morricone\"@en)  } ";
		Model mainModel = mergeModels(createDBpediaVOID());
		List<VOIDPathSolution> voidPathList = new QueryAnalyzer(mainModel,
				false).analyze(query);
		String federatedQuery = reorganizer.reorganizeWholeQuery(voidPathList,
				query);
		System.out.println(federatedQuery);
		assertTrue(104 <= federatedQuery
				.indexOf("FILTER ( ?l = \"Ennio Morricone\"@en )"));

	}

	@Test
	public void reorganizeFilterRegexPatternTest() throws Exception {
		String query = "PREFIX  dc:  <http://purl.org/dc/elements/1.1/> SELECT  ?title WHERE   { { ?x dc:title ?title } FILTER regex(?title, \"web\", \"i\" ) } LIMIT 20";
		Model mainModel = mergeModels(createDBpediaVOID());
		List<VOIDPathSolution> voidPathList = new QueryAnalyzer(mainModel,
				false).analyze(query);
		String federatedQuery = reorganizer.reorganizeWholeQuery(voidPathList,
				query);
		System.out.println(federatedQuery);
		assertTrue(110 <= federatedQuery
				.indexOf("FILTER regex(?title, \"web\", \"i\")"));
	}

	@Test
	public void reorganizeKnownOptionalTripleDifferentSequentialEndpointQueryTest()
			throws Exception {
		String query = "SELECT * WHERE {  <http://dbpedia.org/resource/Istanbul> ?p ?o.   OPTIONAL {   ?x ?y ?z. }}";
		List<VOIDPathSolution> voidPathSolutionList = new Vector<VOIDPathSolution>();
		VOIDPathSolution vp1 = new VOIDPathSolution();
		vp1.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp2 = new VOIDPathSolution();
		vp2.addEndpoint(LGDO_ENDPOINT);
		voidPathSolutionList.add(vp1);
		voidPathSolutionList.add(vp2);
		String federatedQuery = reorganizer.reorganizeWholeQuery(
				voidPathSolutionList, query);
		System.out.println(federatedQuery);
		assertTrue(157 <= federatedQuery.indexOf("OPTIONAL"));
	}

	@Test
	public void reorganizeKnownOptionalTripleSameSequentialEndpointQueryTest()
			throws Exception {
		String query = "SELECT * WHERE { <http://dbpedia.org/resource/Istanbul> ?p ?o.   OPTIONAL {   ?x ?y ?z. } }";
		List<VOIDPathSolution> voidPathSolutionList = new Vector<VOIDPathSolution>();
		VOIDPathSolution vp1 = new VOIDPathSolution();
		vp1.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp2 = new VOIDPathSolution();
		vp2.addEndpoint(DBPEDIA_ENDPOINT);
		voidPathSolutionList.add(vp1);
		voidPathSolutionList.add(vp2);
		String federatedQuery = reorganizer.reorganizeWholeQuery(
				voidPathSolutionList, query);
		System.out.println(federatedQuery);
		assertTrue(113 <= federatedQuery.indexOf("OPTIONAL"));
	}

	@Test
	public void reorganizeOneTripleFromMultipleEndpoints() throws Exception {
		String query = "SELECT * WHERE {?s ?p ?o.}";
		Model mainModel = mergeModels(createDBpediaVOID(), createGeodataVOID(),
				VOIDCreator.createVOID("http://anyendpoint.org/sparql",
						"http://querySolution/anyVOID"));
		List<VOIDPathSolution> voidPathList = new QueryAnalyzer(mainModel,
				false).analyze(query);
		String federatedQuery = reorganizer.reorganizeWholeQuery(voidPathList,
				query);
		System.out.println(federatedQuery);
		assertTrue(federatedQuery
				.contains("{ BIND(<http://linkedgeodata.org/sparql> AS ?ser"));
		assertTrue(federatedQuery
				.contains("{ BIND(<http://dbpedia.org/sparql> AS ?ser"));
		assertTrue(federatedQuery
				.contains("{ BIND(<http://anyendpoint.org/sparql> AS ?ser"));
	}

	@Test
	public void reorganizeOptionalQueryTest() throws Exception {
		String query = "SELECT * WHERE { <http://dbpedia.org/resource/Istanbul> ?p ?o.   OPTIONAL {   ?x ?y ?z. } }";
		List<VOIDPathSolution> voidSolutionPathList = new Vector<VOIDPathSolution>();
		VOIDPathSolution vp1 = new VOIDPathSolution();
		vp1.addEndpoint(DBPEDIA_ENDPOINT);
		VOIDPathSolution vp2 = new VOIDPathSolution();
		vp2.addEndpoint("OPTIONAL");
		voidSolutionPathList.add(vp1);
		voidSolutionPathList.add(vp2);
		String federatedQuery = reorganizer.reorganizeWholeQuery(
				voidSolutionPathList, query);
		System.out.println(federatedQuery);
		assertTrue(federatedQuery.indexOf("OPTIONAL {") > federatedQuery
				.indexOf("SERVICE ?ser"));
	}

	@Test
	public void reorganizeServiceQueryTripleFromDifferentEndpointTest()
			throws Exception {
		String query = "PREFIX geodata: <"
				+ QueryVocabulary.GEODATA_URISPACE
				+ ">"
				+ "PREFIX dbpedia: <http://dbpedia.org/resource>"
				+ "Select * WHERE { SERVICE <http://dbpedia.org/sparql> {dbpedia:Istanbul ?p ?o.} geodata:node424313451 ?x ?z.  }";
		Model mainModel = mergeModels(createDBpediaVOID(), createGeodataVOID());
		List<VOIDPathSolution> voidPathList = new QueryAnalyzer(mainModel,
				false).analyze(query);
		String federatedQuery = reorganizer.reorganizeWholeQuery(voidPathList,
				query);
		System.out.println(federatedQuery);
		assertTrue(federatedQuery.contains("<http://linkedgeodata.org/sparql>"));
		assertTrue(federatedQuery.contains("<http://dbpedia.org/sparql>"));
	}

	@Test
	public void reorganizeServiceQueryTripleFromSameEndpointTest()
			throws Exception {
		String query = "PREFIX dbpedia: <http://dbpedia.org/resource/>"
				+ "Select * WHERE { SERVICE <http://dbpedia.org/sparql> {?s ?p ?o.} dbpedia:Izmir ?y ?o.  }";
		Model mainModel = mergeModels(createDBpediaVOID(), createGeodataVOID());
		List<VOIDPathSolution> voidPathList = new QueryAnalyzer(mainModel,
				false).analyze(query);
		String federatedQuery = reorganizer.reorganizeWholeQuery(voidPathList,
				query);
		System.out.println(federatedQuery);
		assertEquals(-1, federatedQuery.indexOf(SERVICE, 31));
	}

	@Test
	public void reorganizeSimpleQuery() throws Exception {
		Model mainModel = mergeModels(createDBpediaVOID());
		List<VOIDPathSolution> voidPathList = new QueryAnalyzer(mainModel,
				false).analyze(QueryExampleVocabulary.SIMPLE_DBPEDIA_QUERY);
		String federatedQuery = reorganizer.reorganizeWholeQuery(voidPathList,
				QueryExampleVocabulary.SIMPLE_DBPEDIA_QUERY);
		System.out.println(federatedQuery);
		assertTrue(federatedQuery.indexOf("UNION") == -1);
	}

	@Test
	public void reorganizeTriplesFromSameEndpointButNotSequenced()
			throws Exception {
		String query = "PREFIX geodata: <"
				+ QueryVocabulary.GEODATA_URISPACE
				+ ">"
				+ "PREFIX dbpedia: <http://dbpedia.org/resource/>"
				+ "Select * WHERE { dbpedia:Istanbul ?p ?o. geodata:454 ?a ?b. dbpedia:122 ?y ?o.  }";
		Model mainModel = mergeModels(createDBpediaVOID(), createGeodataVOID());
		List<VOIDPathSolution> voidPathList = new QueryAnalyzer(mainModel,
				false).analyze(query);
		String federatedQuery = reorganizer.reorganizeWholeQuery(voidPathList,
				query);
		System.out.println(federatedQuery);
		assertTrue(federatedQuery.indexOf("<http://dbpedia.org/sparql>") != -1);
		assertTrue(federatedQuery.indexOf("<http://linkedgeodata.org/sparql>") != -1);
	}

	@Test
	public void reorganizeUnionQueryWithMultipleServiceTest() throws Exception {
		Model mainModel = mergeModels(createDBpediaVOID(), createGeodataVOID());
		String query = "Select * where { {<http://dbpedia.org/resource/Istanbul> ?p ?o} UNION {<http://dbpedia.org/resource/Istanbul> ?x ?o. ?s ?p ?z } }";
		List<VOIDPathSolution> voidPathList = new QueryAnalyzer(mainModel,
				false).analyze(query);
		String federatedQuery = reorganizer.reorganizeWholeQuery(voidPathList,
				query);
		System.out.println(federatedQuery);
		assertTrue(federatedQuery.indexOf(BIND) > 209);
	}

	@Test
	public void reorganizeUnionTest() throws Exception {
		Model mainModel = mergeModels(createDBpediaVOID());
		List<VOIDPathSolution> voidPathList = new QueryAnalyzer(mainModel,
				false)
				.analyze(QueryExampleVocabulary.reasonableDBPediaUnionQuery);
		String federatedQuery = reorganizer.reorganizeWholeQuery(voidPathList,
				QueryExampleVocabulary.reasonableDBPediaUnionQuery);
		System.out.println(federatedQuery);
		assertTrue(federatedQuery.indexOf("UNION") > 164);
		assertTrue(federatedQuery.indexOf(SERVICE, 180) == -1);
	}

	@Test
	public void reorganizeUnknownOptionalTripleQueryTest() throws Exception {
		String query = "SELECT * WHERE { <http://dbpedia.org/resource/Istanbul> ?p ?o.   OPTIONAL {   ?x ?y ?z. } }";
		Model mainModel = mergeModels(createDBpediaVOID());
		List<VOIDPathSolution> voidPathList = new QueryAnalyzer(mainModel,
				false).analyze(query);
		String federatedQuery = reorganizer.reorganizeWholeQuery(voidPathList,
				query);
		System.out.println(federatedQuery);
		assertTrue(113 <= federatedQuery.indexOf("OPTIONAL"));
	}

	@Test
	public void splitTriplePatternsHaveMultipleEndpointsTest() throws Exception {
		String query = "SELECT * WHERE {  ?s ?p ?o.   ?x ?y ?z. }";
		List<VOIDPathSolution> voidPathSolutionList = new Vector<VOIDPathSolution>();
		VOIDPathSolution vp1 = new VOIDPathSolution();
		vp1.addEndpoint(DBPEDIA_ENDPOINT);
		vp1.addEndpoint(LGDO_ENDPOINT);
		VOIDPathSolution vp2 = new VOIDPathSolution();
		vp2.addEndpoint(DBPEDIA_ENDPOINT);
		vp2.addEndpoint(LGDO_ENDPOINT);

		// set external state true
		vp2.setExternalState(1, true);

		voidPathSolutionList.add(vp1);
		voidPathSolutionList.add(vp2);
		String federatedQuery = reorganizer.reorganizeWholeQuery(
				voidPathSolutionList, query);
		System.out.println(federatedQuery);
		assertEquals(294, federatedQuery.indexOf(SERVICE, 200));
	}

	@Test
	public void unionAndLimitInAQueryTest() throws Exception {
		String query = "SELECT  ?s ?l " + "WHERE"
				+ "{   { ?s <http://www.w3.org/2000/01/rdf-schema#label> ?l }"
				+ "UNION" + "{ ?s <http://xmlns.com/foaf/0.1/name> ?l }"
				+ "FILTER regex(?l, \"Burak\", \"i\")" + "}" + "LIMIT   20";
		Model mainModel = mergeModels(createDBpediaVOID());
		List<VOIDPathSolution> voidPathList = new QueryAnalyzer(mainModel,
				false).analyze(query);
		String federatedQuery = reorganizer.reorganizeWholeQuery(voidPathList,
				query);
		System.out.println("\n" + federatedQuery);
	}

}
