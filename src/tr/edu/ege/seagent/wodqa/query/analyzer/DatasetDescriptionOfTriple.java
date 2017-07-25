package tr.edu.ege.seagent.wodqa.query.analyzer;

import java.util.List;

import tr.edu.ege.seagent.dataset.vocabulary.VOIDIndividualOntology;
import tr.edu.ege.seagent.dataset.vocabulary.VOIDOntologyVocabulary;
import tr.edu.ege.seagent.wodqa.voiddocument.VoidConceptOperations;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * It consists of the elements that belong to triple of a query. These elements
 * shows some values according to the triple that owns this class instance..
 * 
 */
public class DatasetDescriptionOfTriple {
	/**
	 * Shows the dataset has a suitable example resource for the triple.
	 */
	private Boolean hasExampleResource = false;
	/**
	 * Shows the dataset has a suitable uri space for the triple.
	 */
	private Boolean hasUriSpace = false;
	/**
	 * Shows the dataset has a suitable vocabulary for triple.
	 */
	private Boolean hasVocabulary = false;
	/**
	 * Shows the dataset has a suitable topic for triple.
	 */
	private Boolean hasTopic = false;
	/**
	 * Dataset description individual for triple.
	 */
	private Individual datasetDescriptionIndv;
	/**
	 * Triple to querying.
	 */
	private Triple triple;
	/**
	 * Endpoint URL of the dataset.
	 */
	private String endpointURL;
	/**
	 * Topic List for query that includes {@link Triple} instance of this class.
	 */
	private List<RDFNode> topicList;
	/**
	 * This field is used to assign queried dataset for triple using linksets.
	 */
	private Resource reasonedDatasetDescription;

	public DatasetDescriptionOfTriple(Resource reasonedDatasetDescription,
			Triple triple) {
		this.triple = triple;
		this.reasonedDatasetDescription = reasonedDatasetDescription;

	}

	/**
	 * @param datasetDescriptionIndvInVOID
	 */
	public DatasetDescriptionOfTriple(Individual datasetDescriptionIndvInVOID,
			Triple triple, List<RDFNode> topicList) {
		super();
		this.topicList = topicList;
		this.triple = triple;
		this.datasetDescriptionIndv = datasetDescriptionIndvInVOID;
		setEndpointURL("");
		// check...
		createSuitabilityElements();
		// get score...
		checkSuitabilityScore();
		setEndpointOfDataset(this.datasetDescriptionIndv);
	}

	/**
	 * @return the hasExampleResource
	 */
	private Boolean hasExampleResource() {
		return hasExampleResource;
	}

	/**
	 * @param hasExampleResource
	 *            the hasExampleResource to set
	 */
	public void setHasExampleResource(Boolean hasExampleResource) {
		this.hasExampleResource = hasExampleResource;
	}

	/**
	 * @return the hasUriSpace
	 */
	private Boolean hasUriSpace() {
		return hasUriSpace;
	}

	/**
	 * @param hasUriSpace
	 *            the hasUriSpace to set
	 */
	public void setHasUriSpace(Boolean hasUriSpace) {
		this.hasUriSpace = hasUriSpace;
	}

	/**
	 * @return the hasVocabulary
	 */
	private Boolean hasVocabulary() {
		return hasVocabulary;
	}

	/**
	 * @param hasVocabulary
	 *            the hasVocabulary to set
	 */
	public void setHasVocabulary(Boolean hasVocabulary) {
		this.hasVocabulary = hasVocabulary;
	}

	/**
	 * @return the hasTopic
	 */
	private Boolean hasTopic() {
		return hasTopic;
	}

	/**
	 * @param hasTopic
	 *            the hasTopic to set
	 */
	public void setHasTopic(Boolean hasTopic) {
		this.hasTopic = hasTopic;
	}

	/**
	 * @return the datasetDescriptionIndv
	 */
	public Individual getDatasetDescriptionIndv() {
		return datasetDescriptionIndv;
	}

	private void createSuitabilityElements() {
		VOIDIndividualOntology indvOnt = getIndividualOntologyForDataset();
		Individual datasetIndv = getDatasetDescriptionIndv();

		// vocabulary checking...
		vocabularyCheck(triple.getPredicate(), indvOnt, datasetIndv);
		// Boolean isSameAs = isSameAsProperty(triple.getPredicate());

		// check rdf type property...
		checkRdfTypeProperty(indvOnt, datasetIndv);

		// SUBJECT checking...
		if (!triple.getSubject().isVariable()) {
			// search example resource and urispace for subject...
			examineResourceNodeSuitability(indvOnt, datasetIndv,
					triple.getSubject());
		}
		// OBJECT checking...
		// if (!triple.getObject().isVariable() &&
		// !triple.getObject().isLiteral()
		// && !isSameAs) {
		// // search example resource and urispace for object
		// examineResourceNodeSuitability(indvOnt, datasetIndv, triple
		// .getObject());
		// }
		// TOPIC checking...
		if (getTopicList() != null && getTopicList().size() != 0) {
			examineTopics(indvOnt, datasetIndv);
		}

	}

	// private Boolean isSameAsProperty(Node predicate) {
	// if (!predicate.isVariable()
	// && predicate.getURI().equals(
	// "http://www.w3.org/2002/07/owl#sameAs"))
	// return true;
	// else
	// return false;
	//
	// }

	/**
	 * Checks if the triple contains rdf:type property, and looks up
	 * void:vocabulary for objectNode
	 * 
	 * @param indvOnt
	 * @param datasetIndv
	 */
	private void checkRdfTypeProperty(VOIDIndividualOntology indvOnt,
			Individual datasetIndv) {
		// if predicate is type check object's uri as void:vocabulary.
		if (!triple.getPredicate().isVariable()
				&& triple
						.getPredicate()
						.getURI()
						.toString()
						.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
			List<String> listOfVocabulary = indvOnt
					.getListOfVocabulary(datasetIndv);
			if (decideGraphURI(triple.getObject(), listOfVocabulary))
				setHasVocabulary(true);
		}
	}

	/**
	 * Examines topic list with given dataset's topic list.
	 * 
	 * @param indvOnt
	 * @param datasetIndv
	 */
	private void examineTopics(VOIDIndividualOntology indvOnt,
			Individual datasetIndv) {
		List<RDFNode> topicListOfDataset = indvOnt.getListOfTopic(datasetIndv);
		for (RDFNode rdfNode : topicListOfDataset) {
			if (getTopicList().contains(rdfNode)) {
				setHasTopic(true);
			}
		}
	}

	/**
	 * Compare given triple node with the given dataset description instance. If
	 * it is suitable for dataset, dataset description will be noticed for
	 * querying.
	 * 
	 * @param indvOntOfDataset
	 *            Individual ontology for dataset description instance.
	 * @param datasetInstance
	 *            Dataset instance.
	 * @param examiningTripleNode
	 *            Examining triple node.
	 */
	private void examineResourceNodeSuitability(
			VOIDIndividualOntology indvOntOfDataset,
			Individual datasetInstance, Node examiningTripleNode) {
		// search for example resource...
		List<Resource> listOfExampleResource = indvOntOfDataset
				.getListOfExampleResource(datasetInstance);
		if (decideGraphURI(examiningTripleNode, listOfExampleResource)) {
			setHasExampleResource(true);
		}
		// search for urispace...
		List<RDFNode> listOfUrispace = indvOntOfDataset
				.getListOfUriSpace(datasetInstance);
		if (decideGraphURI(examiningTripleNode, listOfUrispace)) {
			setHasUriSpace(true);
		}
	}

	/**
	 * Sets the sparql endpoint of the given dataset individual.
	 * 
	 * @param datasetInstance
	 *            Dataset individual.
	 */
	private void setEndpointOfDataset(Individual datasetInstance) {
		if (getTotalPoint() > 0) {
			RDFNode endpointObj = datasetInstance.getProperty(
					VOIDOntologyVocabulary.DATASET_sparqlEndpoint_prp)
					.getObject();
			String endpointURL;
			if (endpointObj.isLiteral())
				endpointURL = endpointObj.as(Literal.class).getValue()
						.toString();
			else
				endpointURL = endpointObj.as(Resource.class).getURI();
			setEndpointURL(endpointURL);
		}
	}

	/**
	 * It checks the using vocabulary in the dataset with given triple
	 * predicate. If dataset hasn't given vocabulary, it ignores for querying.
	 * 
	 * @param predicateNode
	 * @param indvOnt
	 * @param datasetIndv
	 * @return It returns true for evaluate the other triple elements, returns
	 *         false because dataset is not suite for the triple, because
	 *         dataset isn't using vocabulary that is included in the predicate.
	 */
	private void vocabularyCheck(Node predicateNode,
			VOIDIndividualOntology indvOnt, Individual datasetIndv) {
		// search vocabulary of dataset...
		List<String> listOfVocabulary = indvOnt
				.getListOfVocabulary(datasetIndv);
		boolean propertyIsSuit = decideGraphURI(predicateNode, listOfVocabulary);
		if (!predicateNode.isVariable() && listOfVocabulary.size() > 0
				&& propertyIsSuit)
			setHasVocabulary(true);
		else if (!predicateNode.isVariable()) {
			examineResourceNodeSuitability(indvOnt, datasetIndv, predicateNode);
		}
	}

	/**
	 * Decide example resource and urispace properties.
	 * 
	 * @param objectNode
	 * @param listOfExampleResource
	 * @return true if least one triple pattern matches with the dataset
	 *         description, otherwise false.
	 * 
	 */
	public boolean decideGraphURI(Node objectNode, List<?> listOfExampleResource) {
		if (objectNode.isVariable())
			return false;
		for (Object resource : listOfExampleResource) {
			String queryURI = VoidConceptOperations.getGraphURI(objectNode
					.getURI());
			// get list element's URI...
			String datasetURI = "";
			datasetURI = VoidConceptOperations.getStringValue(resource);
			// TODO : get graph URI if it must be taken... if it is urispace it
			// mustn't
			datasetURI = VoidConceptOperations.getGraphURI(datasetURI);
			// compare triple pattern with dataset description...
			if (queryURI.equals(datasetURI))
				return true;
		}
		return false;
	}

	/**
	 * Gets the existing before void individual ontology for the dataset
	 * description individual.
	 * 
	 * @return {@link VOIDIndividualOntology} instance.
	 */
	public VOIDIndividualOntology getIndividualOntologyForDataset() {
		String ontURI = getDatasetDescriptionIndv().getOntModel()
				.listOntologies().toList().get(0).getURI();
		return new VOIDIndividualOntology(ontURI, getDatasetDescriptionIndv()
				.getOntModel());
	}

	/**
	 * Suitability score of dataset for the triple.
	 */
	private int totalPoint;

	/**
	 * Calculates the suitability score of the dataset for triple.
	 * 
	 * @return
	 */
	private void checkSuitabilityScore() {
		int totalPoint = 0;
		if (hasExampleResource())
			totalPoint += 4;
		if (hasUriSpace())
			totalPoint += 4;
		if (hasVocabulary())
			totalPoint += 3;
		if (hasTopic())
			totalPoint += 2;
		setTotalPoint(totalPoint);
	}

	/**
	 * @return the totalPoint
	 */
	public int getTotalPoint() {
		return totalPoint;
	}

	/**
	 * @param totalPoint
	 *            the totalPoint to set
	 */
	private void setTotalPoint(int totalPoint) {
		this.totalPoint = totalPoint;
	}

	/**
	 * @return the endpointURL
	 */
	public String getEndpointURL() {
		return endpointURL;
	}

	/**
	 * @param endpointURL
	 *            the endpointURL to set
	 */
	private void setEndpointURL(String endpointURL) {
		this.endpointURL = endpointURL;
	}

	/**
	 * @return the topicList
	 */
	private List<RDFNode> getTopicList() {
		return topicList;
	}

	/**
	 * @return the reasonedDatasetDescription
	 */
	public Resource getReasonedDatasetDescription() {
		return reasonedDatasetDescription;
	}

}
