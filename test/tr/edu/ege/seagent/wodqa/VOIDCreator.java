package tr.edu.ege.seagent.wodqa;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Vector;

import tr.edu.ege.seagent.dataset.vocabulary.VOIDIndividualOntology;
import tr.edu.ege.seagent.dataset.vocabulary.VOIDOntologyVocabulary;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class VOIDCreator {

	public static OntModel createVOID(String endpointURL,
			String ontURI) throws MalformedURLException {
		OntModel voidModel = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_MEM);
		VOIDIndividualOntology indvOnt = new VOIDIndividualOntology(ontURI,
				voidModel, null);
		indvOnt.createDataset(endpointURL, null, null);
		return voidModel;
	}

	/**
	 * Create linkset between given dataset individual.
	 * 
	 * @param subjectIndv
	 * @param objectIndv
	 * @param linkPredicate
	 * @param subjectIndvOnt
	 */
	public static Individual createLinkset(Individual subjectIndv, Individual objectIndv, Resource linkPredicate,
			VOIDIndividualOntology subjectIndvOnt) {
				Individual createdLinkset = subjectIndvOnt.createLinkset(null, null,
						null, null, 0, null, 0, 0, 0, 0, null, null, null, 0, null,
						null, null, subjectIndv, 0, null, null, null, linkPredicate,
						objectIndv, subjectIndv, null);
				return createdLinkset;
			}

	/**
	 * Get all dataset individual of the given void model.
	 * 
	 * @param datasetModel
	 * @return
	 */
	public static List<Individual> getAllDatasetIndv(OntModel datasetModel) {
		List<Individual> datasetIndvList = datasetModel.listIndividuals(
				VOIDOntologyVocabulary.DATASET_rsc).toList();
		return datasetIndvList;
	}

	/**
	 * It defines a linkset in the subject model that links to the object model
	 * with given linkPredicate.
	 * 
	 * @param subjectModel
	 * @param objectModel
	 * @param linkPredicate
	 * @param subjectOntologyURI
	 */
	public static List<Individual> createLinksets(OntModel subjectModel, OntModel objectModel, Resource linkPredicate,
			String subjectOntologyURI) {
				VOIDIndividualOntology subjectIndvOnt = new VOIDIndividualOntology(
						subjectOntologyURI, subjectModel);
				// create link from all dataset in subject model to all dataset in
				// object model...
				List<Individual> subjectDatasets = getAllDatasetIndv(subjectModel);
				List<Individual> objectDatasets = getAllDatasetIndv(objectModel);
			
				List<Individual> linksetIndvs = new Vector<Individual>();
				for (Individual subjectIndv : subjectDatasets) {
					for (Individual objectIndv : objectDatasets) {
						Individual createdLinkset = createLinkset(subjectIndv,
								objectIndv, linkPredicate, subjectIndvOnt);
						linksetIndvs.add(createdLinkset);
					}
				}
				return linksetIndvs;
			
			}

	/**
	 * It adds given void:vocabulary property value to all dataset concepts in
	 * given model.
	 * 
	 * @param datasetModel
	 * @param indvOnt
	 * @param exampleRsc
	 */
	public static void addExRscPropertyToAllIndv(OntModel datasetModel,
			VOIDIndividualOntology indvOnt, Resource exampleRsc) {
		List<Individual> datasetIndvList = getAllDatasetIndv(datasetModel);
		for (Individual individual : datasetIndvList) {
			indvOnt.addDatasetExampleResource(individual, exampleRsc);
		}
	}

	/**
	 * It adds given void:vocabulary property value to all dataset concepts in
	 * given model.
	 * 
	 * @param datasetModel
	 * @param indvOnt
	 * @param vocabularyURI
	 */
	public static void addVocabularyPropertyToAllIndv(OntModel datasetModel,
			VOIDIndividualOntology indvOnt, String vocabularyURI) {
		List<Individual> datasetIndvList = getAllDatasetIndv(datasetModel);
		for (Individual individual : datasetIndvList) {
			indvOnt.addDatasetVocabularyProperty(individual, vocabularyURI);
		}
	}

	/**
	 * It adds given void:vocabulary property value to all dataset concepts in
	 * given model.
	 * 
	 * @param datasetModel
	 * @param indvOnt
	 * @param uriSpace
	 */
	public static void addUrispacePropertyToAllIndv(OntModel datasetModel,
			VOIDIndividualOntology indvOnt, RDFNode uriSpace) {
		List<Individual> datasetIndvList = getAllDatasetIndv(datasetModel);
		for (Individual individual : datasetIndvList) {
			indvOnt.addDatasetUriSpace(individual, uriSpace);
		}
	}

	/**
	 * It adds given void:vocabulary property value to all dataset concepts in
	 * given model.
	 * 
	 * @param datasetModel
	 * @param indvOnt
	 * @param exampleRsc
	 */
	public static void setExRscPropertyToAllIndv(OntModel datasetModel,
			VOIDIndividualOntology indvOnt, Resource exampleRsc) {
		List<Individual> datasetIndvList = getAllDatasetIndv(datasetModel);
		for (Individual individual : datasetIndvList) {
			indvOnt.setAllDatasetExampleResource(individual, exampleRsc);
		}
	}

	/**
	 * It adds given void:vocabulary property value to all dataset concepts in
	 * given model.
	 * 
	 * @param datasetModel
	 * @param indvOnt
	 * @param sparqlEndpointURL
	 */
	public static void setEndpointPropertyToAllIndv(OntModel datasetModel,
			VOIDIndividualOntology indvOnt, String sparqlEndpointURL) {
		List<Individual> datasetIndvList = getAllDatasetIndv(datasetModel);
		for (Individual individual : datasetIndvList) {
			indvOnt.setDatasetSparqlEndpoint(individual, sparqlEndpointURL);
		}
	}

}
