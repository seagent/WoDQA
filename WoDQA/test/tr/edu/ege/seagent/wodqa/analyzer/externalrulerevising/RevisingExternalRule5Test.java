package tr.edu.ege.seagent.wodqa.analyzer.externalrulerevising;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import tr.edu.ege.seagent.dataset.vocabulary.VOIDIndividualOntology;
import tr.edu.ege.seagent.wodqa.QueryVocabulary;
import tr.edu.ege.seagent.wodqa.query.analyzer.RelevantDatasetsForTriple;
import tr.edu.ege.seagent.wodqa.query.analyzer.RelevantType;
import tr.edu.ege.seagent.wodqa.query.analyzer.RuleExecutor;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.vocabulary.OWL;

public class RevisingExternalRule5Test extends AbstractRuleRevisingTest {

	private RelevantDatasetsForTriple triplePack;
	private OntModel dbpediaDataset;

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

		/**
		 * create example triple pattern
		 */
		Triple triple = new Triple(
				Node.createVariable("s"),
				Node.createURI(OWL.sameAs.getURI()),
				Node.createURI(RuleRevisingTestConstants.ONLY_OBJECT_RESOURCE_URI));
		triplePack = new RelevantDatasetsForTriple(triple);
	}

	@Test
	public void executeExternalRule5AsToAdd() throws Exception {

		// create dbpedia-virtual linkset that points a virtual dataset
		VOIDIndividualOntology dbpediaIndvOnt = new VOIDIndividualOntology(
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA, dbpediaDataset);
		createVirtualDatasetAndLinkset(dbpediaIndvOnt,
				RuleRevisingTestConstants.VIRTUAL_DATASET_URI_SPACE,
				QueryVocabulary.DBPEDIA_URISPACE_LITERAL, OWL.sameAs.getURI());

		// check before executing single step rules.
		assertEquals(0, triplePack.getCurrentRelevantDatasets().size());
		assertTrue(triplePack.isAllRelated());

		Model mainModel = mergeModels(dbpediaDataset);
		// initialize rule executor
		ruleExecutor = new RuleExecutor(mainModel);
		// rules are executing...
		ruleExecutor.executeSingleStepRules(triplePack);

		// check after executing single step rules.
		checkRelevantDatasets(triplePack, 0, RelevantType.EXTERNAL);

	}

	@Test
	public void executeExternalRule5AsNotToAdd() throws Exception {

		Model mainModel = mergeModels(dbpediaDataset);
		// initialize rule executor
		ruleExecutor = new RuleExecutor(mainModel);

		// check before executing single step rules.
		assertEquals(0, triplePack.getCurrentRelevantDatasets().size());
		assertTrue(triplePack.isAllRelated());
		// rules are executing...
		ruleExecutor.executeSingleStepRules(triplePack);

		// check after executing single step rules.
		assertEquals(0, triplePack.getCurrentRelevantDatasets().size());
	}

}
