package tr.edu.ege.seagent.wodqa.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import tr.edu.ege.seagent.wodqa.AbstractWoDQAComponentsTest;
import tr.edu.ege.seagent.wodqa.QueryVocabulary;
import tr.edu.ege.seagent.wodqa.query.analyzer.QueryAnalyzer;
import tr.edu.ege.seagent.wodqa.query.analyzer.VOIDPathSolution;
import tr.edu.ege.seagent.wodqa.voiddocument.VOIDCreator;
import tr.edu.ege.seagent.wodqa.voiddocument.VOIDIndividualOntology;

public class QueryAnalyzerTest extends AbstractWoDQAComponentsTest {
	private static final String DBPEDIA_PREFIXES = "PREFIX dbpedia: <http://dbpedia.org/resource/> \nPREFIX dbp-ont:<http://dbpedia.org/ontology/>";

	private static final String POPULATION_QUERY = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "prefix xsd: <http://www.w3.org/2001/XMLSchema#> SELECT ?subject ?population WHERE {"
			+ "?subject rdf:type <http://dbpedia.org/ontology/City>."
			+ "?subject <http://dbpedia.org/ontology/populationUrban> ?population."
			+ "FILTER (xsd:integer(?population) > 10000000)" + "}";

	@Test
	public void analyzeQueryTest() throws Exception {
		OntModel dbpediaVOID = createDBpediaVOID();
		OntModel geodataVOID = createGeodataVOID();
		// create linksets
		VOIDCreator.createLinksets(geodataVOID, dbpediaVOID,
				QueryVocabulary.OWL_SAME_AS_RSC,
				QueryVocabulary.QUERY_SOLUTION_GEODATA);
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "Prefix lgdo: <http://linkedgeodata.org/ontology/>"
				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
				+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>"
				+ DBPEDIA_PREFIXES
				+ "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>"
				+ "SELECT DISTINCT ?dbpediaAirport ?props ?values WHERE {"
				+ "<http://dbpedia.org/resource/Edinburgh> geo:long ?cityLong."
				+ "<http://dbpedia.org/resource/Edinburgh> geo:lat ?cityLat."
				+ "?airport rdf:type lgdo:Airport."
				+ "?airport geo:long ?airLong."
				+ "?airport geo:lat ?airLat."
				+ "?airport owl:sameAs ?dbpediaAirport."
				+ "?dbpediaAirport ?props ?values." + "}";
		Model mainModel = mergeModels(dbpediaVOID, geodataVOID);
		QueryAnalyzer analyzer = new QueryAnalyzer(mainModel, false);
		List<VOIDPathSolution> voidPathSolutionList = analyzer.analyze(query);
		assertEquals(7, voidPathSolutionList.size());

		/**
		 * assert void path solutions that is constructed by Relevant datasets
		 * of triple objects.
		 */

		// creating first control void solution that indicates dbpedia dataset
		// as internal relevant
		VOIDPathSolution solution1 = new VOIDPathSolution();
		solution1.addEndpoint(QueryVocabulary.DBPEDIA_ENDPOINT_URL, false);

		assertEquals(solution1, voidPathSolutionList.get(0));
		assertEquals(solution1, voidPathSolutionList.get(1));

		// creating second control void solution that indicates geodata dataset
		// as internal relevant
		VOIDPathSolution solution2 = new VOIDPathSolution();
		solution2.addEndpoint(QueryVocabulary.GEODATA_ENDPOINT_URL, false);
		assertEquals(solution2, voidPathSolutionList.get(2));
		assertEquals(solution2, voidPathSolutionList.get(3));
		assertEquals(solution2, voidPathSolutionList.get(4));

		// creating second control void solution that indicates geodata as
		// external relevant
		VOIDPathSolution solution3 = new VOIDPathSolution();
		solution3.addEndpoint(QueryVocabulary.GEODATA_ENDPOINT_URL, true);
		assertEquals(solution3, voidPathSolutionList.get(5));

		// creating third control void solution that indicates dbpedia as
		// external and geoadata as internal relevant
		VOIDPathSolution solution4 = new VOIDPathSolution();
		solution4.addEndpoint(QueryVocabulary.DBPEDIA_ENDPOINT_URL, true);
		solution4.addEndpoint(QueryVocabulary.GEODATA_ENDPOINT_URL, false);
		assertEquals(solution4, voidPathSolutionList.get(6));
	}

	@Test
	public void cacheASKResultForTriplePatternWhichHasVariableSubject()
			throws Exception {
		// create a VOID store model which solely contains DBpedia dataset...
		OntModel voidStore = createDBpediaVOID();
		// create an analyzer with ASK elimination...
		QueryAnalyzer analyzer = new QueryAnalyzer(voidStore);
		analyzer.analyze(DBPEDIA_PREFIXES
				+ "SELECT * { ?s dbp-ont:isPartOf dbpedia:Aegean_Region. }");

		// check that caching statement is added to the VOID store model...
		Literal expectedGenericTriplePattern = ResourceFactory
				.createPlainLiteral("?varSubject @http://dbpedia.org/ontology/isPartOf http://dbpedia.org/resource/Aegean_Region");
		assertTrue(voidStore.contains(null, QueryAnalyzer.CACHE_INCLUDES_PRP,
				expectedGenericTriplePattern));
	}

	@Test
	public void cacheASKResultForTriplePatternWhichHasVariableObject()
			throws Exception {
		// create a VOID store model which solely contains DBpedia dataset...
		OntModel voidStore = createDBpediaVOID();
		// create an analyzer with ASK elimination...
		QueryAnalyzer analyzer = new QueryAnalyzer(voidStore);
		analyzer.analyze(DBPEDIA_PREFIXES
				+ "SELECT * { dbpedia:Izmir dbp-ont:isPartOf ?o. }");

		// check that caching statement is added to the VOID store model...
		Literal expectedGenericTriplePattern = ResourceFactory
				.createPlainLiteral("http://dbpedia.org/resource/Izmir @http://dbpedia.org/ontology/isPartOf ?varObject");
		assertTrue(voidStore.contains(null, QueryAnalyzer.CACHE_INCLUDES_PRP,
				expectedGenericTriplePattern));
	}

	@Test
	public void cacheASKResultForTriplePatternWhichHasVariablePredicate()
			throws Exception {
		// create a VOID store model which solely contains DBpedia dataset...
		OntModel voidStore = createDBpediaVOID();
		// create an analyzer with ASK elimination...
		QueryAnalyzer analyzer = new QueryAnalyzer(voidStore);
		analyzer.analyze(DBPEDIA_PREFIXES
				+ "SELECT * { dbpedia:Izmir ?p dbpedia:Aegean_Region. }");

		// check that caching statement is added to the VOID store model...
		Literal expectedGenericTriplePattern = ResourceFactory
				.createPlainLiteral("http://dbpedia.org/resource/Izmir @?varPredicate http://dbpedia.org/resource/Aegean_Region");
		assertTrue(voidStore.contains(null, QueryAnalyzer.CACHE_INCLUDES_PRP,
				expectedGenericTriplePattern));
	}

	@Test
	public void optionalQueryTest() throws Exception {
		String optionalQuery = DBPEDIA_PREFIXES
				+ "SELECT * WHERE { dbpedia:Ankara ?p ?o OPTIONAL {?s ?p ?o} }";
		// analyze
		QueryAnalyzer analyzer = new QueryAnalyzer(createDBpediaVOID());
		List<VOIDPathSolution> voidPathSolutionList = analyzer
				.analyze(optionalQuery);
		assertEquals(2, voidPathSolutionList.size());
	}

	@Test
	public void serviceQueryTest() throws Exception {
		String query = DBPEDIA_PREFIXES
				+ "SELECT * WHERE { SERVICE <http://dbpedia.org/sparql> {dbpedia:Ankara ?p ?o. ?s ?x ?o. } ?s ?p ?o. }";
		Model mainModel = mergeModels(createDBpediaVOID());
		QueryAnalyzer analyzer = new QueryAnalyzer(mainModel);
		List<VOIDPathSolution> voidPathSolutionList = analyzer.analyze(query);
		System.out.println(voidPathSolutionList);
		assertEquals(3, voidPathSolutionList.size());
	}

	@Test
	public void unionQueryTest() throws Exception {
		String unionQuery = DBPEDIA_PREFIXES
				+ "SELECT * WHERE { {dbpedia:Ankara ?p ?o }UNION {?s ?p ?o} }";
		OntModel dbpediaVOID = createDBpediaVOID();
		OntModel geodataVOID = createGeodataVOID();
		// create linksets
		VOIDCreator.createLinksets(geodataVOID, dbpediaVOID,
				QueryVocabulary.OWL_SAME_AS_RSC,
				QueryVocabulary.QUERY_SOLUTION_GEODATA);
		Model mainModel = mergeModels(dbpediaVOID, geodataVOID);
		QueryAnalyzer analyzer = new QueryAnalyzer(mainModel);
		List<VOIDPathSolution> voidPathSolutionList = analyzer
				.analyze(unionQuery);
		System.out.println(voidPathSolutionList);
		assertEquals(2, voidPathSolutionList.size());
	}

	@Test
	public void filterQueryTest() throws Exception {
		Model mainModel = mergeModels(createDBpediaVOID());
		QueryAnalyzer analyzer = new QueryAnalyzer(mainModel);
		List<VOIDPathSolution> voidPathSolutionList = analyzer
				.analyze(POPULATION_QUERY);
		assertEquals(2, voidPathSolutionList.size());
	}

	@Test
	public void optimizeWithASKQueryTest() throws Exception {
		OntModel dbpediaVOID = createDBpediaVOID();
		OntModel geodataVOID = createGeodataVOID();
		// add dbpedia vocabulary to geodata to eliminate with ask query...
		// create linksets
		VOIDIndividualOntology geodataIndvOn = new VOIDIndividualOntology(
				QueryVocabulary.QUERY_SOLUTION_GEODATA, geodataVOID);
		VOIDCreator.addVocabularyPropertyToAllIndv(geodataVOID, geodataIndvOn,
				QueryVocabulary.DBPEDIA_ONTOLOGY_VOCABULARY);

		Model mainModel = mergeModels(dbpediaVOID, geodataVOID);

		QueryAnalyzer analyzerWihtoutASK = new QueryAnalyzer(mainModel, false);
		List<VOIDPathSolution> voidPathSolutionList = analyzerWihtoutASK
				.analyze(POPULATION_QUERY);
		assertTrue(voidPathSolutionList.get(0).getEndpointTypeList().size() == 2);

		QueryAnalyzer analyzerWithASK = new QueryAnalyzer(mainModel, true);
		voidPathSolutionList = analyzerWithASK.analyze(POPULATION_QUERY);
		assertTrue(voidPathSolutionList.get(0).getEndpointTypeList().size() == 1);
	}
}
