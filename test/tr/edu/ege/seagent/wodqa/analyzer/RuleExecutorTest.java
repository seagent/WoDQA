package tr.edu.ege.seagent.wodqa.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;

import tr.edu.ege.seagent.dataset.vocabulary.VOIDIndividualOntology;
import tr.edu.ege.seagent.dataset.vocabulary.VOIDOntologyVocabulary;
import tr.edu.ege.seagent.wodqa.QueryVocabulary;
import tr.edu.ege.seagent.wodqa.VOIDCreator;
import tr.edu.ege.seagent.wodqa.exception.VOIDDescriptionConsistencyException;
import tr.edu.ege.seagent.wodqa.exception.WrongVOIDEntityConstructionException;
import tr.edu.ege.seagent.wodqa.query.analyzer.RelevantDatasetsForTriple;
import tr.edu.ege.seagent.wodqa.query.analyzer.RelevantType;
import tr.edu.ege.seagent.wodqa.query.analyzer.RuleExecutor;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class RuleExecutorTest {
	private static final String LGDO_SPARQL_ENDPOINT = "http://linkedgeodata.org/sparql/";
	private static final String DBPEDIA_SPARQL_ENDPOINT = "http://dbpedia.org/sparql";

	@Test
	public void executeTypeIndexRuleTest() throws Exception {
		OntModel voidModel = VOIDCreator.createVOID(DBPEDIA_SPARQL_ENDPOINT,
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA);
		VOIDIndividualOntology indvOnt = new VOIDIndividualOntology(
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA, voidModel);
		// add vocabulary property to void model.
		VOIDCreator.addVocabularyPropertyToAllIndv(voidModel, indvOnt,
				QueryVocabulary.FOAF_VOCABULARY);

		// create example triple pattern
		Triple triple = new Triple(Node.createVariable("subject"),
				Node.createURI(QueryVocabulary.RDF_TYPE_URI),
				Node.createURI(QueryVocabulary.FOAF_VOCABULARY + "Person"));
		// create relevant datasets set for the triple pattern.
		RelevantDatasetsForTriple triplePack = new RelevantDatasetsForTriple(
				triple);

		executeSingleStepRulesAndAssert(mergeModels(voidModel), triplePack);
		assertTriplePackInternalExternalState(triplePack, RelevantType.INTERNAL);
	}

	@Test
	public void externalChainingRuleTest() throws Exception {
		// create VOIDs for the initializations
		OntModel dbpediaVoid = VOIDCreator.createVOID(DBPEDIA_SPARQL_ENDPOINT,
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA);
		OntModel geodataVoid = VOIDCreator.createVOID(LGDO_SPARQL_ENDPOINT,
				QueryVocabulary.QUERY_SOLUTION_GEODATA);
		VOIDCreator.createLinksets(dbpediaVoid, geodataVoid,
				QueryVocabulary.OWL_SAME_AS_RSC,
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA);
		OntModel voidA = VOIDCreator.createVOID(
				"http://querySolution/A/sparql", "http://querySolution/A");
		OntModel voidB = VOIDCreator.createVOID(
				"http://querySolution/B/sparql", "http://querySolution/B");
		OntModel voidC = VOIDCreator.createVOID(
				"http://querySolution/C/sparql", "http://querySolution/C");
		Model mainModel = mergeModels(dbpediaVoid, geodataVoid, voidA, voidB,
				voidC);
		// create dataset resources of voids
		Resource dbpediaDataset = getDataset(dbpediaVoid);
		Resource geodataDataset = getDataset(geodataVoid);
		Resource datasetA = getDataset(voidA);
		Resource datasetB = getDataset(voidB);
		Resource datasetC = getDataset(voidC);

		// create triple patterns according to external chaining triple
		// pattern packs...
		RelevantDatasetsForTriple triplePack1 = new RelevantDatasetsForTriple(
				new Triple(Node.createVariable("s"),
						Node.createURI(QueryVocabulary.OWL_SAME_AS_URI),
						Node.createVariable("o")));
		RelevantDatasetsForTriple triplePack2 = new RelevantDatasetsForTriple(
				new Triple(Node.createVariable("o"), Node.createVariable("p"),
						Node.createVariable("o2")));

		// set initial current dataset for triple1..
		List<Resource> relevantDatasetEx1 = new Vector<Resource>();
		addElementResourceList(relevantDatasetEx1, dbpediaDataset, datasetA,
				datasetB);
		List<RelevantType> relevantTypesEx1 = new Vector<RelevantType>();
		addElementRelevantTypeList(relevantTypesEx1, RelevantType.INTERNAL,
				RelevantType.INTERNAL, RelevantType.INTERNAL);

		triplePack1.setNewRelevantDatasets(relevantDatasetEx1);
		triplePack1.setNewRelevantTypes(relevantTypesEx1);
		triplePack1.eliminateWithNewFoundDatasets();
		// set initial current dataset for triple2..
		List<Resource> relevantDatasetEx2 = new Vector<Resource>();
		addElementResourceList(relevantDatasetEx2, geodataDataset, datasetC);
		List<RelevantType> relevantTypesEx2 = new Vector<RelevantType>();
		addElementRelevantTypeList(relevantTypesEx2, RelevantType.INTERNAL,
				RelevantType.INTERNAL);

		triplePack2.setNewRelevantDatasets(relevantDatasetEx2);
		triplePack2.setNewRelevantTypes(relevantTypesEx2);
		triplePack2.eliminateWithNewFoundDatasets();

		// execute and assert...
		RuleExecutor executor = new RuleExecutor(mainModel);
		executor.executeRepetitiveRules(triplePack1, triplePack2);
		assertEquals(1, triplePack1.getCurrentRelevantDatasets().size());
		assertEquals(1, triplePack2.getCurrentRelevantDatasets().size());
		assertContains(triplePack1.getCurrentRelevantDatasets(), dbpediaDataset);
		assertContains(triplePack2.getCurrentRelevantDatasets(), geodataDataset);
		assertTriplePackInternalExternalState(triplePack1,
				RelevantType.EXTERNAL);
		assertTriplePackInternalExternalState(triplePack2,
				RelevantType.EXTERNAL);

	}

	/**
	 * Set triple pattern1's current relevant datasets set: A, B and C. Set
	 * triple pattern2's current relevant datasets set: B, C and D
	 * 
	 * @throws Exception
	 */
	@Test
	public void internalChainingRuleTest() throws Exception {
		RelevantDatasetsForTriple triplePack1 = new RelevantDatasetsForTriple(
				new Triple(Node.createVariable("s"), Node.createVariable("p"),
						Node.createVariable("o")));
		RelevantDatasetsForTriple triplePack2 = new RelevantDatasetsForTriple(
				new Triple(Node.createVariable("o"), Node.createVariable("p2"),
						Node.createVariable("o2")));
		Resource A = createResource("http://A");
		Resource B = createResource("http://B");
		Resource C = createResource("http://C");
		Resource D = createResource("http://D");

		// set some initial values to the current relevant datasets for the
		// triple patterns.
		List<Resource> relevantDatasetEx1 = new Vector<Resource>();
		List<Resource> relevantDatasetEx2 = new Vector<Resource>();

		addElementResourceList(relevantDatasetEx1, A, B, C);
		List<RelevantType> relevantTypesEx1 = new Vector<RelevantType>();
		addElementRelevantTypeList(relevantTypesEx1, RelevantType.INTERNAL,
				RelevantType.INTERNAL, RelevantType.INTERNAL);
		triplePack1.setNewRelevantDatasets(relevantDatasetEx1);
		triplePack1.setNewRelevantTypes(relevantTypesEx1);
		triplePack1.eliminateWithNewFoundDatasets();

		List<RelevantType> relevantTypesEx2 = new Vector<RelevantType>();
		addElementResourceList(relevantDatasetEx2, B, C, D);
		addElementRelevantTypeList(relevantTypesEx2, RelevantType.INTERNAL,
				RelevantType.INTERNAL, RelevantType.INTERNAL);
		triplePack2.setNewRelevantDatasets(relevantDatasetEx2);
		triplePack2.setNewRelevantTypes(relevantTypesEx2);
		triplePack2.eliminateWithNewFoundDatasets();

		// execute and assert...
		RuleExecutor executor = new RuleExecutor((OntModel) null);
		executor.executeRepetitiveRules(triplePack1, triplePack2);
		assertEquals(2, triplePack1.getCurrentRelevantDatasets().size());
		assertEquals(2, triplePack2.getCurrentRelevantDatasets().size());
		assertContains(triplePack1.getCurrentRelevantDatasets(), B, C);
		assertContains(triplePack2.getCurrentRelevantDatasets(), B, C);
		assertTriplePackInternalExternalState(triplePack1,
				RelevantType.INTERNAL, RelevantType.INTERNAL);
		assertTriplePackInternalExternalState(triplePack2,
				RelevantType.INTERNAL, RelevantType.INTERNAL);
	}

	/**
	 * This method merges given dataset {@link Model} list into one
	 * {@link Model}
	 * 
	 * @param datasets
	 * @return
	 */
	private Model mergeModels(Model... datasets) {
		Model mainModel = ModelFactory.createDefaultModel();
		for (Model dataset : datasets) {
			mainModel.add(dataset);
		}
		return mainModel;
	}

	@Test
	public void subjectSharingRuleTest() throws Exception {
		RelevantDatasetsForTriple triplePack1 = new RelevantDatasetsForTriple(
				new Triple(Node.createVariable("s"), Node.createVariable("p"),
						Node.createVariable("o3")));
		RelevantDatasetsForTriple triplePack2 = new RelevantDatasetsForTriple(
				new Triple(Node.createVariable("s"), Node.createVariable("p2"),
						Node.createVariable("o2")));
		Resource A = createResource("http://A");
		Resource B = createResource("http://B");
		Resource C = createResource("http://C");
		Resource D = createResource("http://D");

		// set some initial values to the current relevant datasets for the
		// triple patterns.
		List<Resource> relevantDatasetEx1 = new Vector<Resource>();
		List<Resource> relevantDatasetEx2 = new Vector<Resource>();
		addElementResourceList(relevantDatasetEx1, A, B, C);
		List<RelevantType> relevantTypesEx1 = new Vector<RelevantType>();
		addElementRelevantTypeList(relevantTypesEx1, RelevantType.INTERNAL,
				RelevantType.INTERNAL, RelevantType.INTERNAL);
		triplePack1.setNewRelevantDatasets(relevantDatasetEx1);
		triplePack1.setNewRelevantTypes(relevantTypesEx1);
		triplePack1.eliminateWithNewFoundDatasets();

		addElementResourceList(relevantDatasetEx2, C, D);
		List<RelevantType> relevantTypesEx2 = new Vector<RelevantType>();
		addElementRelevantTypeList(relevantTypesEx2, RelevantType.INTERNAL,
				RelevantType.INTERNAL);
		triplePack2.setNewRelevantDatasets(relevantDatasetEx2);
		triplePack2.setNewRelevantTypes(relevantTypesEx2);
		triplePack2.eliminateWithNewFoundDatasets();

		// execute and assert...
		RuleExecutor executor = new RuleExecutor((OntModel) null);
		executor.executeRepetitiveRules(triplePack1, triplePack2);
		assertEquals(1, triplePack1.getCurrentRelevantDatasets().size());
		assertEquals(1, triplePack2.getCurrentRelevantDatasets().size());
		assertContains(triplePack1.getCurrentRelevantDatasets(), C);
		assertContains(triplePack2.getCurrentRelevantDatasets(), C);
		assertTriplePackInternalExternalState(triplePack1,
				RelevantType.INTERNAL);
		assertTriplePackInternalExternalState(triplePack2,
				RelevantType.INTERNAL);
	}

	@Test
	public void internalObjectSharingRuleTest() throws Exception {
		RelevantDatasetsForTriple triplePack1 = new RelevantDatasetsForTriple(
				new Triple(Node.createVariable("s"), Node.createVariable("p"),
						Node.createVariable("o")));
		RelevantDatasetsForTriple triplePack2 = new RelevantDatasetsForTriple(
				new Triple(Node.createVariable("s2"),
						Node.createVariable("p2"), Node.createVariable("o")));
		Resource A = createResource("http://A");
		Resource B = createResource("http://B");
		Resource C = createResource("http://C");
		Resource D = createResource("http://D");

		// set some initial values to the current relevant datasets for the
		// triple patterns.
		List<Resource> relevantDatasetEx1 = new Vector<Resource>();
		List<Resource> relevantDatasetEx2 = new Vector<Resource>();
		addElementResourceList(relevantDatasetEx1, A, B, C);
		List<RelevantType> relevantTypesEx1 = new Vector<RelevantType>();
		addElementRelevantTypeList(relevantTypesEx1, RelevantType.INTERNAL,
				RelevantType.INTERNAL, RelevantType.INTERNAL);
		triplePack1.setNewRelevantDatasets(relevantDatasetEx1);
		triplePack1.setNewRelevantTypes(relevantTypesEx1);
		triplePack1.eliminateWithNewFoundDatasets();

		addElementResourceList(relevantDatasetEx2, C, D);
		List<RelevantType> relevantTypesEx2 = new Vector<RelevantType>();
		addElementRelevantTypeList(relevantTypesEx2, RelevantType.INTERNAL,
				RelevantType.INTERNAL);
		triplePack2.setNewRelevantDatasets(relevantDatasetEx2);
		triplePack2.setNewRelevantTypes(relevantTypesEx2);
		triplePack2.eliminateWithNewFoundDatasets();

		// execute and assert...
		RuleExecutor executor = new RuleExecutor(
				ModelFactory.createDefaultModel());
		executor.executeRepetitiveRules(triplePack1, triplePack2);
		assertEquals(1, triplePack1.getCurrentRelevantDatasets().size());
		assertEquals(1, triplePack2.getCurrentRelevantDatasets().size());
		assertContains(triplePack1.getCurrentRelevantDatasets(), C);
		assertContains(triplePack2.getCurrentRelevantDatasets(), C);
		assertTriplePackInternalExternalState(triplePack1,
				RelevantType.INTERNAL);
		assertTriplePackInternalExternalState(triplePack2,
				RelevantType.INTERNAL);
	}

	@Test
	public void externalObjectSharingMethodTest() throws Exception {
		// create VOIDs for the initializations
		OntModel dbpediaVoid = VOIDCreator.createVOID(DBPEDIA_SPARQL_ENDPOINT,
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA);
		OntModel geodataVoid = VOIDCreator.createVOID(LGDO_SPARQL_ENDPOINT,
				QueryVocabulary.QUERY_SOLUTION_GEODATA);
		OntModel voidA = VOIDCreator.createVOID(
				"http://querySolution/A/sparql", "http://querySolution/A");
		VOIDCreator.createLinksets(dbpediaVoid, voidA,
				QueryVocabulary.OWL_SAME_AS_RSC,
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA);
		VOIDCreator.createLinksets(geodataVoid, voidA,
				QueryVocabulary.OWL_SAME_AS_RSC,
				QueryVocabulary.QUERY_SOLUTION_GEODATA);
		OntModel voidB = VOIDCreator.createVOID(
				"http://querySolution/B/sparql", "http://querySolution/B");
		OntModel voidC = VOIDCreator.createVOID(
				"http://querySolution/C/sparql", "http://querySolution/C");
		Model mainModel = mergeModels(dbpediaVoid, geodataVoid, voidA, voidB,
				voidC);
		// create dataset resources of voids
		Resource dbpediaDataset = getDataset(dbpediaVoid);
		Resource geodataDataset = getDataset(geodataVoid);
		Resource datasetB = getDataset(voidB);
		Resource datasetC = getDataset(voidC);

		// create triple patterns according to external chaining triple
		// pattern packs...
		RelevantDatasetsForTriple triplePack1 = new RelevantDatasetsForTriple(
				new Triple(Node.createVariable("s"),
						Node.createURI(QueryVocabulary.OWL_SAME_AS_URI),
						Node.createVariable("o")));
		RelevantDatasetsForTriple triplePack2 = new RelevantDatasetsForTriple(
				new Triple(Node.createVariable("s2"),
						Node.createURI(QueryVocabulary.OWL_SAME_AS_URI),
						Node.createVariable("o")));

		// set initial current dataset for triple1..
		List<Resource> relevantDatasetEx1 = new Vector<Resource>();
		addElementResourceList(relevantDatasetEx1, dbpediaDataset, datasetB);
		List<RelevantType> relevantTypesEx1 = new Vector<RelevantType>();
		addElementRelevantTypeList(relevantTypesEx1, RelevantType.INTERNAL,
				RelevantType.INTERNAL);
		triplePack1.setNewRelevantDatasets(relevantDatasetEx1);
		triplePack1.setNewRelevantTypes(relevantTypesEx1);
		triplePack1.eliminateWithNewFoundDatasets();
		// set initial current dataset for triple2..
		List<Resource> relevantDatasetEx2 = new Vector<Resource>();
		addElementResourceList(relevantDatasetEx2, geodataDataset, datasetC);
		List<RelevantType> relevantTypesEx2 = new Vector<RelevantType>();
		addElementRelevantTypeList(relevantTypesEx2, RelevantType.INTERNAL,
				RelevantType.INTERNAL);
		triplePack2.setNewRelevantDatasets(relevantDatasetEx2);
		triplePack2.setNewRelevantTypes(relevantTypesEx2);
		triplePack2.eliminateWithNewFoundDatasets();

		// execute and assert...
		RuleExecutor executor = new RuleExecutor(mainModel);
		executor.executeRepetitiveRules(triplePack1, triplePack2);
		assertEquals(1, triplePack1.getCurrentRelevantDatasets().size());
		assertEquals(1, triplePack2.getCurrentRelevantDatasets().size());
		assertContains(triplePack1.getCurrentRelevantDatasets(), dbpediaDataset);
		assertContains(triplePack2.getCurrentRelevantDatasets(), geodataDataset);
		assertTriplePackInternalExternalState(triplePack1,
				RelevantType.EXTERNAL);
		assertTriplePackInternalExternalState(triplePack2,
				RelevantType.EXTERNAL);
	}

	@Test
	public void linksToURIRulesTest() throws Exception {
		OntModel dbpediaVoid = VOIDCreator.createVOID(DBPEDIA_SPARQL_ENDPOINT,
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA);
		VOIDIndividualOntology dbpediaIndvOnt = new VOIDIndividualOntology(
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA, dbpediaVoid);
		// add urispace property to void model.
		VOIDCreator.addUrispacePropertyToAllIndv(dbpediaVoid, dbpediaIndvOnt,
				QueryVocabulary.DBPEDIA_URISPACE_LITERAL);

		// create example triple pattern
		Triple triple = new Triple(Node.createVariable("s"),
				Node.createURI(QueryVocabulary.OWL_SAME_AS_URI),
				Node.createURI(QueryVocabulary.DBPEDIA_ISTANBUL_RSC_URI));
		// create relevant datasets set for the triple pattern.
		RelevantDatasetsForTriple triplePack = new RelevantDatasetsForTriple(
				triple);
		executeSingleStepRulesAndAssert(mergeModels(dbpediaVoid), triplePack);

		// add a new void model...
		OntModel geodataVoid = VOIDCreator.createVOID(LGDO_SPARQL_ENDPOINT,
				QueryVocabulary.QUERY_SOLUTION_GEODATA);

		// create linkset
		VOIDCreator.createLinksets(geodataVoid, dbpediaVoid,
				QueryVocabulary.OWL_SAME_AS_RSC,
				QueryVocabulary.QUERY_SOLUTION_GEODATA);
		// reset triple pack
		triplePack = new RelevantDatasetsForTriple(triple);
		// add void model to a list..
		Model mainModel = mergeModels(dbpediaVoid, geodataVoid);
		// create rule executor..
		RuleExecutor ruleExecutor = new RuleExecutor(mainModel);

		assertEquals(0, triplePack.getCurrentRelevantDatasets().size());
		assertTrue(triplePack.isAllRelated());
		// rules are executing...
		ruleExecutor.executeSingleStepRules(triplePack);

		// assert...
		assertEquals(2, triplePack.getCurrentRelevantDatasets().size());
		assertTrue(triplePack.getCurrentRelevantDatasets().contains(
				dbpediaVoid.listIndividuals(VOIDOntologyVocabulary.DATASET_rsc)
						.toList().get(0).asResource()));
		assertTrue(triplePack.getCurrentRelevantDatasets().contains(
				geodataVoid.listIndividuals(VOIDOntologyVocabulary.DATASET_rsc)
						.toList().get(0).asResource()));
		assertTriplePackInternalExternalState(triplePack,
				RelevantType.EXTERNAL, RelevantType.INTERNAL);
	}

	@Test
	public void URILinksToRuleTest() throws Exception {
		OntModel dbpediaModel = VOIDCreator.createVOID(DBPEDIA_SPARQL_ENDPOINT,
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA);
		OntModel aModel = VOIDCreator.createVOID(
				"http://querySolution/A/sparql", "http://querySolution/A");
		VOIDIndividualOntology indvOntDbpedia = new VOIDIndividualOntology(
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA, dbpediaModel);
		VOIDIndividualOntology indvOntA = new VOIDIndividualOntology(
				"http://querySolution/A", aModel);
		// add urispace property to void model.
		VOIDCreator.addUrispacePropertyToAllIndv(dbpediaModel, indvOntDbpedia,
				QueryVocabulary.DBPEDIA_URISPACE_LITERAL);
		// add vocabulary to both
		VOIDCreator.addVocabularyPropertyToAllIndv(dbpediaModel,
				indvOntDbpedia, QueryVocabulary.DBPEDIA_ONTOLOGY_VOCABULARY);
		VOIDCreator.addVocabularyPropertyToAllIndv(aModel, indvOntA,
				QueryVocabulary.DBPEDIA_ONTOLOGY_VOCABULARY);
		// create example triple pattern
		Triple triple = new Triple(
				Node.createURI(QueryVocabulary.DBPEDIA_ISTANBUL_RSC_URI),
				Node.createURI(QueryVocabulary.DBPEDIA_ONTOLOGY_VOCABULARY
						+ "name"), Node.createVariable("o"));
		// create relevant datasets set for the triple pattern.
		RelevantDatasetsForTriple triplePack = new RelevantDatasetsForTriple(
				triple);
		Model mainModel = mergeModels(dbpediaModel, aModel);
		executeSingleStepRulesAndAssert(mainModel, triplePack, dbpediaModel);
		assertTriplePackInternalExternalState(triplePack, RelevantType.INTERNAL);
	}

	@Test
	public void vocabularyMatchTest() throws Exception {
		OntModel voidModel = VOIDCreator.createVOID(DBPEDIA_SPARQL_ENDPOINT,
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA);
		VOIDIndividualOntology indvOnt = new VOIDIndividualOntology(
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA, voidModel);
		// add vocabulary property to void model.
		VOIDCreator.addVocabularyPropertyToAllIndv(voidModel, indvOnt,
				QueryVocabulary.FOAF_VOCABULARY);

		// create example triple pattern
		Triple triple = new Triple(Node.createVariable("subject"),
				Node.createURI(QueryVocabulary.FOAF_VOCABULARY + "firstName"),
				Node.createVariable("object"));
		// create relevant datasets set for the triple pattern.
		RelevantDatasetsForTriple triplePack = new RelevantDatasetsForTriple(
				triple);

		executeSingleStepRulesAndAssert(mergeModels(voidModel), triplePack);
		assertTriplePackInternalExternalState(triplePack, RelevantType.INTERNAL);
	}

	/**
	 * Adds the given resources to the given resource list.
	 * 
	 * @param relevantDatasetEx1
	 * @param resources
	 */
	private void addElementResourceList(List<Resource> relevantDatasetEx1,
			Resource... resources) {
		for (Resource resource : resources) {
			relevantDatasetEx1.add(resource);
		}
	}

	/**
	 * Adds the given resources to the given resource list.
	 * 
	 * @param relevantTypesEx1
	 * @param resources
	 */
	private void addElementRelevantTypeList(
			List<RelevantType> relevantTypesEx1, RelevantType... types) {
		for (RelevantType type : types) {
			relevantTypesEx1.add(type);
		}
	}

	private void assertContains(List<Resource> currentRelevantDatasets,
			Resource... datasets) {
		for (Resource resource : datasets) {
			assertTrue(currentRelevantDatasets.contains(resource));
		}
	}

	/**
	 * It creates a resource with the given URI.
	 * 
	 * @param rscURI
	 * @return
	 */
	private Resource createResource(String rscURI) {
		return ResourceFactory.createResource(rscURI);
	}

	/**
	 * It executes single step rules and assert the triple pack. Test data must
	 * be created only execute related rules because it executes all single step
	 * rules.
	 * 
	 * @param mainModel
	 * @param triplePack
	 * @throws VOIDDescriptionConsistencyException
	 * @throws WrongVOIDEntityConstructionException
	 * @throws URISyntaxException 
	 */
	private void executeSingleStepRulesAndAssert(Model mainModel,
			RelevantDatasetsForTriple triplePack)
			throws VOIDDescriptionConsistencyException,
			WrongVOIDEntityConstructionException, URISyntaxException {
		checkBasicProperties(mainModel, triplePack);
		assertTrue(triplePack
				.getCurrentRelevantDatasets()
				.contains(
						mainModel
								.listResourcesWithProperty(
										VOIDOntologyVocabulary.DATASET_sparqlEndpoint_prp)
								.toList().get(0)));
	}

	/**
	 * It executes single step rules and assert the triple pack. Test data must
	 * be created only execute related rules because it executes all single step
	 * rules.
	 * 
	 * @param mainModel
	 * @param triplePack
	 * @throws VOIDDescriptionConsistencyException
	 * @throws WrongVOIDEntityConstructionException
	 * @throws URISyntaxException 
	 */
	private void executeSingleStepRulesAndAssert(Model mainModel,
			RelevantDatasetsForTriple triplePack, Model controlModel)
			throws VOIDDescriptionConsistencyException,
			WrongVOIDEntityConstructionException, URISyntaxException {
		checkBasicProperties(mainModel, triplePack);
		assertTrue(triplePack
				.getCurrentRelevantDatasets()
				.contains(
						controlModel
								.listResourcesWithProperty(
										VOIDOntologyVocabulary.DATASET_sparqlEndpoint_prp)
								.toList().get(0)));
	}

	private void checkBasicProperties(Model mainModel,
			RelevantDatasetsForTriple triplePack)
			throws VOIDDescriptionConsistencyException,
			WrongVOIDEntityConstructionException, URISyntaxException {
		// create rule executor..
		RuleExecutor ruleExecutor = new RuleExecutor(mainModel);

		assertEquals(0, triplePack.getCurrentRelevantDatasets().size());
		assertTrue(triplePack.isAllRelated());
		// rules are executing...
		ruleExecutor.executeSingleStepRules(triplePack);

		// assert...
		assertEquals(1, triplePack.getCurrentRelevantDatasets().size());
	}

	private Resource getDataset(OntModel voidModel) {
		return voidModel.listIndividuals(VOIDOntologyVocabulary.DATASET_rsc)
				.toList().get(0).asResource();
	}

	/**
	 * It asserts the given triplepack and the given internal and external
	 * types.
	 * 
	 * @param triplePack
	 * @param types
	 */
	protected void assertTriplePackInternalExternalState(
			RelevantDatasetsForTriple triplePack, RelevantType... types) {
		if (types.length != triplePack.getCurrentRelevantTypes().size())
			Assert.fail("Dataset count and state count is different!");
		for (int i = 0; i < types.length; i++) {
			if (!triplePack.getCurrentRelevantTypes().get(i).equals(types[i])) {
				Assert.fail("There is an unmatching situation for the endpoint: "
						+ triplePack.getCurrentRelevantDatasets().get(i)
								.getURI());
			}
		}

	}

}
