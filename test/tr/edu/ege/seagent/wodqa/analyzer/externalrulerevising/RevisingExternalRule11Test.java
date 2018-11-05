package tr.edu.ege.seagent.wodqa.analyzer.externalrulerevising;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;

import tr.edu.ege.seagent.wodqa.QueryVocabulary;
import tr.edu.ege.seagent.wodqa.query.analyzer.RelevantDatasetsForTriple;
import tr.edu.ege.seagent.wodqa.query.analyzer.RelevantType;
import tr.edu.ege.seagent.wodqa.query.analyzer.RuleExecutor;
import tr.edu.ege.seagent.wodqa.voiddocument.VOIDIndividualOntology;

public class RevisingExternalRule11Test extends AbstractRuleRevisingTest {

	RelevantDatasetsForTriple triplePackForDBPedia;
	private RelevantDatasetsForTriple triplePackForLMDB;
	private OntModel dbpediaDataset;
	private OntModel lmdbDataset;

	/**
	 * initializing sample datasets.
	 * 
	 * @throws MalformedURLException
	 */
	@Before
	public void before() throws MalformedURLException {
		allVoidModels = new ArrayList<Model>();

		// create dbpedia dataset and initialize it.
		dbpediaDataset = createDBpediaDataset();

		// create lmdb dataset and initialize it.
		lmdbDataset = createLMDBDataset();

		/**
		 * create example triple pattern for being first external resource is
		 * DBPedia
		 */
		Triple tripleForDBpedia = new Triple(
				Node.createVariable("s1"),
				Node.createURI(RuleRevisingTestConstants.DBPEDIA_EDITING_PRP_URI),
				Node.createVariable("o"));

		/**
		 * create example triple pattern for being first external resource is
		 * DBPedia
		 */
		Triple tripleForLMDB = new Triple(Node.createVariable("s2"),
				Node.createURI(RuleRevisingTestConstants.LMDB_EDITOR_PRP_URI),
				Node.createVariable("o"));

		// create triple pack for dbpedia
		triplePackForDBPedia = new RelevantDatasetsForTriple(tripleForDBpedia);
		// create triple pack for lmdb
		triplePackForLMDB = new RelevantDatasetsForTriple(tripleForLMDB);

	}

	@Test
	public void executeExternalRule11AsToAdd() throws Exception {

		// create dbpedia-virtual linkset that points a virtual dataset
		VOIDIndividualOntology dbpediaIndvOnt = new VOIDIndividualOntology(
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA, dbpediaDataset);
		createVirtualDatasetAndLinkset(dbpediaIndvOnt,
				RuleRevisingTestConstants.VIRTUAL_DATASET_URI_SPACE,
				QueryVocabulary.DBPEDIA_URISPACE_LITERAL,
				RuleRevisingTestConstants.DBPEDIA_EDITING_PRP_URI);

		// create lmdb-virtual linkset that points a virtual dataset
		VOIDIndividualOntology lmdbIndvOnt = new VOIDIndividualOntology(
				QueryVocabulary.QUERY_SOLUTION_LINKEDMDB, lmdbDataset);
		createVirtualDatasetAndLinkset(lmdbIndvOnt,
				RuleRevisingTestConstants.VIRTUAL_DATASET_URI_SPACE,
				QueryVocabulary.LINKED_MDB_URISPACE,
				RuleRevisingTestConstants.LMDB_EDITOR_PRP_URI);

		// merge given dataset models into one main model
		Model mainModel = mergeModels(dbpediaDataset, lmdbDataset);
		// initialize rule executor
		ruleExecutor = new RuleExecutor(mainModel);

		// first execute single step rules...
		ruleExecutor.executeSingleStepRules(triplePackForDBPedia);
		ruleExecutor.executeSingleStepRules(triplePackForLMDB);

		// execute repetitive step rules...
		ruleExecutor.executeRepetitiveRules(triplePackForDBPedia,
				triplePackForLMDB);

		// check relevant datasets for dbpedia
		checkRelevantDatasets(triplePackForDBPedia, 0, RelevantType.EXTERNAL);

		// check relevant datasets for lmdb
		checkRelevantDatasets(triplePackForLMDB, 1, RelevantType.EXTERNAL);

	}

	@Test
	public void executeExternalRule11AsNotToAdd() throws Exception {

		// create dbpedia-virtual linkset that points a virtual dataset
		VOIDIndividualOntology dbpediaIndvOnt = new VOIDIndividualOntology(
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA, dbpediaDataset);
		createVirtualDatasetAndLinkset(dbpediaIndvOnt, null,
				QueryVocabulary.DBPEDIA_URISPACE_LITERAL,
				RuleRevisingTestConstants.DBPEDIA_EDITING_PRP_URI);

		// create lmdb-virtual linkset that points a virtual dataset
		VOIDIndividualOntology lmdbIndvOnt = new VOIDIndividualOntology(
				QueryVocabulary.QUERY_SOLUTION_LINKEDMDB, lmdbDataset);
		createVirtualDatasetAndLinkset(lmdbIndvOnt, null,
				QueryVocabulary.LINKED_MDB_URISPACE,
				RuleRevisingTestConstants.LMDB_EDITOR_PRP_URI);

		Model mainModel = mergeModels(dbpediaDataset, lmdbDataset);
		// initialize rule executor
		ruleExecutor = new RuleExecutor(mainModel);

		// first execute single step rules...
		ruleExecutor.executeSingleStepRules(triplePackForDBPedia);
		ruleExecutor.executeSingleStepRules(triplePackForLMDB);

		// execute repetitive step rules...
		ruleExecutor.executeRepetitiveRules(triplePackForDBPedia,
				triplePackForLMDB);

		// check relevant datasets for dbpedia
		checkRelevantDatasets(triplePackForDBPedia, 0, RelevantType.INTERNAL);

		// check relevant datasets for lmdb
		checkRelevantDatasets(triplePackForLMDB, 1, RelevantType.INTERNAL);

	}

}
