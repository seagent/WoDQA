package tr.edu.ege.seagent.wodqa;

import java.net.MalformedURLException;

import tr.edu.ege.seagent.dataset.vocabulary.VOIDIndividualOntology;
import util.TestConstants;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;

public class AbstractWoDQAComponentsTest {

	protected OntModel createDBpediaVOID() throws MalformedURLException {
		// dbpedia ontology
		OntModel dbpediaVOID = VOIDCreator.createVOID(
				QueryVocabulary.DBPEDIA_ENDPOINT_URL,
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA);
		VOIDIndividualOntology dbIndvOn = new VOIDIndividualOntology(
				QueryVocabulary.QUERY_SOLUTION_DBPEDIA, dbpediaVOID);
		VOIDCreator.addVocabularyPropertyToAllIndv(dbpediaVOID, dbIndvOn,
				QueryVocabulary.DBPEDIA_ONTOLOGY_VOCABULARY);
		VOIDCreator.addVocabularyPropertyToAllIndv(dbpediaVOID, dbIndvOn,
				QueryVocabulary.DBPEDIA_PROPERTY_VOCABULARY);
		VOIDCreator.addUrispacePropertyToAllIndv(dbpediaVOID, dbIndvOn,
				QueryVocabulary.DBPEDIA_URISPACE_LITERAL);
		return dbpediaVOID;
	}

	protected OntModel createGeodataVOID() throws MalformedURLException {
		// geodata ontology
		OntModel geodataVOID = VOIDCreator.createVOID(
				QueryVocabulary.GEODATA_ENDPOINT_URL,
				QueryVocabulary.QUERY_SOLUTION_GEODATA);
		VOIDIndividualOntology geodataIndvOn = new VOIDIndividualOntology(
				QueryVocabulary.QUERY_SOLUTION_GEODATA, geodataVOID);
		VOIDCreator.addVocabularyPropertyToAllIndv(geodataVOID, geodataIndvOn,
				QueryVocabulary.GEODATA_ONTOLOGY_VOCABULARY);
		VOIDCreator.addVocabularyPropertyToAllIndv(geodataVOID, geodataIndvOn,
				QueryVocabulary.GEODATA_PROPERTY_VOCABULARY);
		VOIDCreator.addUrispacePropertyToAllIndv(geodataVOID, geodataIndvOn,
				QueryVocabulary.GEODATA_URISPACE_LITERAL);
		return geodataVOID;
	}

	protected OntModel createFacebookDataVOID() throws MalformedURLException {
		OntModel facebookDataVOID = VOIDCreator.createVOID(
				QueryVocabulary.AMAZON_FACEBOOK_ENDPOINT_URL,
				QueryVocabulary.QUERY_SOLUTION_FACEBOOK_DATA);
		VOIDIndividualOntology facebookIndvOnt = new VOIDIndividualOntology(
				QueryVocabulary.QUERY_SOLUTION_FACEBOOK_DATA, facebookDataVOID);
		VOIDCreator.addVocabularyPropertyToAllIndv(facebookDataVOID,
				facebookIndvOnt, QueryVocabulary.FACEBOOK_VOCABULARY);
		return facebookDataVOID;
	}

	/**
	 * creating sample Facebook VOID
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	protected OntModel createFacebookVOID() throws MalformedURLException {
		OntModel facebookVOID = VOIDCreator.createVOID(
				TestConstants.FACEBOOK_ENDPOINT_URL,
				TestConstants.FACEBOOK_VOID_URI);
		VOIDIndividualOntology facebookIndvOnt = new VOIDIndividualOntology(
				TestConstants.FACEBOOK_VOID_URI, facebookVOID);
		VOIDCreator.addVocabularyPropertyToAllIndv(facebookVOID,
				facebookIndvOnt, TestConstants.FACEBOOK_VOCABULARY);
		VOIDCreator.addVocabularyPropertyToAllIndv(facebookVOID,
				facebookIndvOnt, FOAF.getURI());
		VOIDCreator.addUrispacePropertyToAllIndv(facebookVOID, facebookIndvOnt,
				TestConstants.FACEBOOK_URI_SPACE_LITERAL);
		return facebookVOID;
	}

	/**
	 * creating sample Foursquare VOID
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	protected OntModel createFoursquareVOID() throws MalformedURLException {
		OntModel foursquareVOID = VOIDCreator.createVOID(
				TestConstants.FOURSQUARE_ENDPOINT_URL,
				TestConstants.FOURSQUARE_VOID_URI);
		VOIDIndividualOntology foursquareIndvOnt = new VOIDIndividualOntology(
				TestConstants.FOURSQUARE_VOID_URI, foursquareVOID);
		VOIDCreator.addVocabularyPropertyToAllIndv(foursquareVOID,
				foursquareIndvOnt, TestConstants.FOURSQUARE_VOCABULARY);
		VOIDCreator.addVocabularyPropertyToAllIndv(foursquareVOID,
				foursquareIndvOnt, FOAF.getURI());
		VOIDCreator.addUrispacePropertyToAllIndv(foursquareVOID,
				foursquareIndvOnt, TestConstants.FOURSQUARE_URI_SPACE_LITERAL);
		return foursquareVOID;
	}

	/**
	 * creating sample Linkedin VOID
	 * 
	 * @return
	 * @throws MalformedURLException
	 */
	protected OntModel createLinkedinVOID() throws MalformedURLException {
		OntModel linkedinVOID = VOIDCreator.createVOID(
				TestConstants.LINKEDIN_ENDPOINT_URL,
				TestConstants.LINKEDIN_VOID_URI);
		VOIDIndividualOntology linkedinIndvOnt = new VOIDIndividualOntology(
				TestConstants.LINKEDIN_VOID_URI, linkedinVOID);
		VOIDCreator.addVocabularyPropertyToAllIndv(linkedinVOID,
				linkedinIndvOnt, TestConstants.LINKEDIN_VOCABULARY);
		VOIDCreator.addVocabularyPropertyToAllIndv(linkedinVOID,
				linkedinIndvOnt, FOAF.getURI());
		VOIDCreator.addUrispacePropertyToAllIndv(linkedinVOID, linkedinIndvOnt,
				TestConstants.LINKEDIN_URI_SPACE_LITERAL);
		return linkedinVOID;
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

}
