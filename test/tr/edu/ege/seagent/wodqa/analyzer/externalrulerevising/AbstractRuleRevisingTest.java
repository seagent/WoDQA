package tr.edu.ege.seagent.wodqa.analyzer.externalrulerevising;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.util.List;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import tr.edu.ege.seagent.wodqa.QueryVocabulary;
import tr.edu.ege.seagent.wodqa.analyzer.RuleExecutorTest;
import tr.edu.ege.seagent.wodqa.query.analyzer.RelevantDatasetsForTriple;
import tr.edu.ege.seagent.wodqa.query.analyzer.RelevantType;
import tr.edu.ege.seagent.wodqa.query.analyzer.RuleExecutor;
import tr.edu.ege.seagent.wodqa.voiddocument.VOIDCreator;
import tr.edu.ege.seagent.wodqa.voiddocument.VOIDIndividualOntology;
import tr.edu.ege.seagent.wodqa.voiddocument.VOIDOntologyVocabulary;

public class AbstractRuleRevisingTest extends RuleExecutorTest {

	protected static List<Model> allVoidModels;
	protected RuleExecutor ruleExecutor;

	/**
	 * This method creates sample virtual dataset with URISpace literal value
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	protected OntModel createVirtualDatasetWithURISpaceLiteral()
			throws MalformedURLException {
		/**
		 * creating virtual dataset
		 */
		OntModel virtualDataset = VOIDCreator.createVOID(null,
				RuleRevisingTestConstants.QUERY_SOLUTION_VIRTUAL_DATASET);
		VOIDIndividualOntology virtualIndvOnt = new VOIDIndividualOntology(
				RuleRevisingTestConstants.QUERY_SOLUTION_VIRTUAL_DATASET,
				virtualDataset);
		// add urispace property to void model.
		VOIDCreator.addUrispacePropertyToAllIndv(virtualDataset,
				virtualIndvOnt,
				RuleRevisingTestConstants.VIRTUAL_DATASET_URI_SPACE_LITERAL);
		return virtualDataset;
	}

	/**
	 * This method creates sample virtual dataset without URISpace literal value
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	protected OntModel createVirtualDatasetWithoutURISpaceLiteral()
			throws MalformedURLException {
		/**
		 * creating virtual dataset
		 */
		OntModel virtualDataset = VOIDCreator.createVOID(null,
				RuleRevisingTestConstants.QUERY_SOLUTION_VIRTUAL_DATASET);
		new VOIDIndividualOntology(
				RuleRevisingTestConstants.QUERY_SOLUTION_VIRTUAL_DATASET,
				virtualDataset);
		return virtualDataset;
	}

	/**
	 * This method creates sample DBPedia dataset and its properties
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	protected OntModel createDBpediaDataset() throws MalformedURLException {
		/**
		 * creating dbpedia dataset
		 */
		OntModel dbpediaDataset = VOIDCreator.createVOID(null,
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA);
		VOIDIndividualOntology dbpediaIndvOnt = new VOIDIndividualOntology(
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA, dbpediaDataset);
		// add urispace property to void model.
		VOIDCreator.addUrispacePropertyToAllIndv(dbpediaDataset,
				dbpediaIndvOnt, QueryVocabulary.DBPEDIA_URISPACE_LITERAL);
		VOIDCreator.addVocabularyPropertyToAllIndv(dbpediaDataset,
				dbpediaIndvOnt, QueryVocabulary.DBPEDIA_ONTOLOGY_VOCABULARY);
		// add sparql endpoint property to datasets
		dbpediaDataset
				.listIndividuals(VOIDOntologyVocabulary.DATASET_rsc)
				.toList()
				.get(0)
				.asResource()
				.addProperty(RuleRevisingTestConstants.SPARQL_ENPOINT_PRP,
						"http://155.223.24.47:8897/dbpedia/sparql");
		return dbpediaDataset;
	}

	/**
	 * This method creates sample DBPedia dataset and its properties
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	protected OntModel createLMDBDataset() throws MalformedURLException {
		/**
		 * creating dbpedia dataset
		 */
		OntModel lmdbDataset = VOIDCreator.createVOID(null,
				QueryVocabulary.QUERY_SOLUTION_LINKEDMDB);
		VOIDIndividualOntology lmdbIndvOnt = new VOIDIndividualOntology(
				QueryVocabulary.QUERY_SOLUTION_LINKEDMDB, lmdbDataset);
		// add urispace property to void model.
		VOIDCreator.addUrispacePropertyToAllIndv(lmdbDataset, lmdbIndvOnt,
				QueryVocabulary.LINKED_MDB_URISPACE);
		VOIDCreator.addVocabularyPropertyToAllIndv(lmdbDataset, lmdbIndvOnt,
				QueryVocabulary.LINKED_MDB_MOVIE_ONTOLOGY_URI);
		// add sparql endpoint property to datasets
		lmdbDataset
				.listIndividuals(VOIDOntologyVocabulary.DATASET_rsc)
				.toList()
				.get(0)
				.asResource()
				.addProperty(RuleRevisingTestConstants.SPARQL_ENPOINT_PRP,
						"http://155.223.24.47:8899/lmdb/sparql");
		return lmdbDataset;
	}

	/**
	 * This method checks whether found relevant datasets for given triple pack
	 * is correct or not.
	 * 
	 * @param triplePack
	 * @param indexOfVoidModel
	 * @param relevantType
	 */
	protected void checkRelevantDatasets(RelevantDatasetsForTriple triplePack,
			int indexOfVoidModel, RelevantType relevantType) {

		// check for dataset size
		assertEquals(1, triplePack.getCurrentRelevantDatasets().size());
		// get dataset void model
		Model actualVOIDModel = allVoidModels.get(indexOfVoidModel);
		Resource actualDatasetIndv = actualVOIDModel
				.listResourcesWithProperty(
						VOIDOntologyVocabulary.DATASET_sparqlEndpoint_prp)
				.toList().get(0);
		assertTrue(triplePack.getCurrentRelevantDatasets().contains(
				actualDatasetIndv));
		assertTriplePackInternalExternalState(triplePack, relevantType);
	}

	/**
	 * This method creates virtual dataset with given virtual dataset URISpace
	 * and then creates linkset between main subject dataset and created virtual
	 * dataset.
	 * 
	 * @param individualOntologyOfSubject
	 * @param virtualDatasetURISpace
	 * @param uriSpaceOfSubjectDataset
	 * @param linkPredicate
	 */
	protected void createVirtualDatasetAndLinkset(
			VOIDIndividualOntology individualOntologyOfSubject,
			String virtualDatasetURISpace, Literal uriSpaceOfSubjectDataset,
			String linkPredicate) {
		// create virtual dataset in the subject individual ontology model with
		// given property
		individualOntologyOfSubject.createDataset(null, null,
				virtualDatasetURISpace);
		// get datset resource of subject datset property
		Individual resourceDatasetIndv = getDatasetWithURISpaceValue(
				individualOntologyOfSubject.getOntModel(),
				uriSpaceOfSubjectDataset).as(Individual.class);
		// get dataset resource of object property
		Individual virtualDatasetIndv = null;
		if (virtualDatasetURISpace != null) {
			virtualDatasetIndv = getDatasetWithURISpaceValue(
					individualOntologyOfSubject.getOntModel(),
					ResourceFactory.createPlainLiteral(virtualDatasetURISpace))
					.as(Individual.class);
		} else {
			virtualDatasetIndv = getDatasetWhoHasNoURISpaceProperty(individualOntologyOfSubject);
		}
		// create linkset between subject and object with given linkpredicate
		individualOntologyOfSubject.createLinkset(
				ResourceFactory.createProperty(linkPredicate),
				virtualDatasetIndv, resourceDatasetIndv);
	}

	/**
	 * This method returns datasets of given individual ontology which has no
	 * URISpace property
	 * 
	 * @param individualOntology
	 * @return
	 */
	private Individual getDatasetWhoHasNoURISpaceProperty(
			VOIDIndividualOntology individualOntology) {
		List<Individual> datasets = individualOntology.listDatasets();
		for (Individual dataset : datasets) {
			if (!dataset
					.hasProperty(VOIDOntologyVocabulary.DATASET_uriSpace_prp)) {
				return dataset;
			}
		}
		return null;
	}

	/**
	 * This method gets dataset with given URISPace literal value
	 * 
	 * @param ontModel
	 * @param uriSpaceLiteral
	 * 
	 * @return
	 */
	private Resource getDatasetWithURISpaceValue(OntModel ontModel,
			Literal uriSpaceLiteral) {
		return ontModel
				.listSubjectsWithProperty(
						VOIDOntologyVocabulary.DATASET_uriSpace_prp,
						uriSpaceLiteral).toList().get(0);
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
			allVoidModels.add(dataset);
			mainModel.add(dataset);
		}
		return mainModel;
	}

}
