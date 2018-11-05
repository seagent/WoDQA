package tr.edu.ege.seagent.wodqa.voiddocument;

import java.util.List;
import java.util.Vector;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.shared.JenaException;

/**
 * @author etmen
 * 
 */
public class VoidCrawler {

	/**
	 * Topic resource query variable name.
	 */
	private static final String TOPIC_RESOURCE_VAR_NAME = "topicResource";

	/**
	 * 
	 */
	public VoidCrawler() {
		dataSetType = VoidDatasetType.CKAN;
	}

	/**
	 * Variable name of a sparql query.
	 */
	private static final String SPARQL_ENDPOINT_VAR_NAME = "sparqlEndpoint";
	/**
	 * Dataset service type.
	 */
	private VoidDatasetType dataSetType;

	/**
	 * It crawls the datasets according to the given keyword. DEFAULT
	 * 
	 * @param descriptionForDataset
	 *            Keyword for dataset.
	 * @param ontologyURI
	 *            Ontology URI.
	 * @param sparqlEndpointSearchType
	 *            Searching endp store.
	 * @return
	 * @throws CrawlerException
	 */
	public OntModel crawlDatasetAccordingToDatasetName(
			String descriptionForDataset, String ontologyURI,
			VoidDatasetType datasetSearchType,
			VoidDatasetType sparqlEndpointSearchType) throws CrawlerException {
		if (datasetSearchType != null)
			setDatasetType(datasetSearchType);
		String queryStr = createDatasetSearchQueryAccordingToName(descriptionForDataset);
		// now creating query object
		Query query = QueryFactory.create(queryStr);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				getVoidStoreURL(sparqlEndpointSearchType), query);
		Resource sparqlEndpointURI = null;
		ResultSet execSelect;
		Model queryModel;

		// gets sparql endpoint URL from CKAN with given description
		// keyword.
		execSelect = qexec.execSelect();
		// ResultSetFormatter.out(System.out, execSelect, query);

		String queryOfDatasetModel;
		try {
			sparqlEndpointURI = execSelect.nextSolution().getResource(
					SPARQL_ENDPOINT_VAR_NAME);
			qexec.close();
			queryOfDatasetModel = constructQueryWithEndpointURL(sparqlEndpointURI
					.getURI());
		} catch (Exception e) {
			qexec.close();
			queryOfDatasetModel = constructQueryWithoutEndpointURL(descriptionForDataset);
		}
		// get void models from CKAN or VoiD store or anywhere else
		queryModel = getDatasetPropertiesFromVoidStore(queryOfDatasetModel);

		return createQueryModel(queryModel, ontologyURI);
	}

	public OntModel crawlDatasetAccordingToEndPointURL(String endpointURL,
			VoidDatasetType storeType) {
		String serviceURl = "";
		if (storeType.equals(VoidDatasetType.CKAN))
			serviceURl = VOIDOntologyVocabulary.CKAN_SERVICE;
		else if (storeType.equals(VoidDatasetType.RKB_EXPLORER))
			serviceURl = VOIDOntologyVocabulary.VOID_STORE_SPARQL_SERVICE_URI;
		String query = "PREFIX void: <http://rdfs.org/ns/void#> Construct {?ds ?p ?o} WHERE {"
				+ "?ds void:sparqlEndpoint <" + endpointURL + ">. ?ds ?p ?o.}";
		QueryExecution exec = QueryExecutionFactory.sparqlService(serviceURl,
				QueryFactory.create(query));
		Model model = exec.execConstruct();
		OntModel voidModel = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_MEM);
		voidModel.add(model);
		return voidModel;
	}

	/**
	 * Get dataset model with only title.
	 * 
	 * @param descriptionForDataset
	 * @return
	 */
	private String constructQueryWithoutEndpointURL(String descriptionForDataset) {
		return "PREFIX void: <http://rdfs.org/ns/void#> "
				+ "PREFIX dc: <http://purl.org/dc/terms/>"
				+ " CONSTRUCT {?ds ?p ?o} WHERE { ?ds a void:Dataset . "
				+ "?ds dc:title ?title FILTER regex(?title, \""
				+ descriptionForDataset + "\", \"i\" ). ?ds ?p ?o.}";

	}

	/**
	 * Decide searching void stores.
	 * 
	 * @param sparqlEndpointSearchType
	 * @return
	 */
	public static String getVoidStoreURL(
			VoidDatasetType sparqlEndpointSearchType) {
		if (sparqlEndpointSearchType.equals(VoidDatasetType.CKAN))
			return VOIDOntologyVocabulary.CKAN_SERVICE;
		else if (sparqlEndpointSearchType.equals(VoidDatasetType.RKB_EXPLORER))
			return VOIDOntologyVocabulary.VOID_STORE_SPARQL_SERVICE_URI;
		return "";
	}

	/**
	 * Crawls datasets according to the given topic description. Always use big
	 * letter in first letter of description keyword.
	 * 
	 * @param descriptionForTopic
	 * @param ontologyURI
	 * @param datasetType
	 * @return
	 * @throws CrawlerException
	 */
	public OntModel crawlDatasetAccordingToTopic(String descriptionForTopic,
			String ontologyURI, VoidDatasetType datasetType)
			throws CrawlerException {
		if (datasetType != null)
			setDatasetType(datasetType);
		descriptionForTopic = descriptionForTopic.substring(0, 1).toUpperCase()
				+ descriptionForTopic
						.substring(1, descriptionForTopic.length());
		String queryForTopicSearch = createDatasetSearchQueryAccordingToTopic(descriptionForTopic);
		// now creating query object
		Query query = QueryFactory.create(queryForTopicSearch);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				VOIDOntologyVocabulary.DBPEDIA_SERVICE, query);
		Resource topicResource = null;
		ResultSet execSelect;
		Model queryModel = null;
		try {
			// gets topic resources from DBPEDIA with given description
			// keyword.
			execSelect = qexec.execSelect();
			// ResultSetFormatter.out(System.out, execSelect, query);
			topicResource = execSelect.nextSolution().getResource(
					TOPIC_RESOURCE_VAR_NAME);
		} finally {
			qexec.close();
			if (topicResource != null) {
				String queryOfSearchingWithTopic = constructQueryWithTopicURI(topicResource
						.getURI());
				// get void models from CKAN or VoiD store or anywhere else
				queryModel = getDatasetPropertiesFromVoidStore(queryOfSearchingWithTopic);
			}
		}
		return createQueryModel(queryModel, ontologyURI);
	}

	/**
	 * Creates query for retrieving topic resources from dbpedia.
	 * 
	 * @param descriptionForTopic
	 *            description keyword for topic resource.
	 * @return {@link String}.
	 */
	private String createDatasetSearchQueryAccordingToTopic(
			String descriptionForTopic) {
		return "PREFIX rdfs:     <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX void:     <http://rdfs.org/ns/void#> "
				+ "SELECT DISTINCT ?topicResource WHERE { ?topicResource rdfs:label \""
				+ descriptionForTopic + "\"@en. }";

	}

	/**
	 * Sets the dataset's type with the given parameter.
	 */
	private void setDatasetType(VoidDatasetType datasetType) {
		this.dataSetType = datasetType;
	}

	/**
	 * Creates an ontmodel with the given query model.
	 * 
	 * @param queryModel
	 *            Result of query.
	 * @param ontologyURI
	 *            Ontology URI of the created ontmodel instance.
	 * @return OntModel instance.
	 */
	public static OntModel createQueryModel(Model queryModel, String ontologyURI) {
		OntModel ontModel = ModelFactory
				.createOntologyModel(OntModelSpec.OWL_MEM);
		// create ontology tag...
		Ontology ontology = ontModel.createOntology(ontologyURI);
		ontModel.setDynamicImports(true);
		addImport(ontology, VOIDOntologyVocabulary.VOID_ONTOLOGY_URI);
		// XXX : acilabilir
		// addImport(ontology, VOIDOntologyVocabulary.AKT_ONTOLOGY_URI);
		// addImport(ontology, VOIDOntologyVocabulary.SCOVO_ONTOLOGY_URI);
		// addImport(ontology, VOIDOntologyVocabulary.DUBLIN_CORE_ONTOLOGY_URI);
		// addImport(ontology, VOIDOntologyVocabulary.FOAF_ONTOLOGY_URI);
		ontModel.add(queryModel);
		return ontModel;
	}

	/**
	 * Add imported ontology to the given ontology...
	 * 
	 * @param ontology
	 *            Actual ontology.
	 * @param importedOntology
	 *            Improted ontology.
	 */
	private static void addImport(Ontology ontology, String importedOntology) {
		ontology.addImport(ResourceFactory.createResource(importedOntology));
	}

	/**
	 * Gets dataset properties from VOID store with given dataset URI.
	 * 
	 * @param sparqlEndpointURI
	 *            Dataset URI
	 * @throws CrawlerException
	 */
	private Model getDatasetPropertiesFromVoidStore(String queryStr)
			throws CrawlerException {
		// now creating query object
		Query query = QueryFactory.create(queryStr);
		QueryExecution qexec = null;
		if (dataSetType == VoidDatasetType.RKB_EXPLORER) {
			qexec = QueryExecutionFactory
					.sparqlService(
							VOIDOntologyVocabulary.VOID_STORE_SPARQL_SERVICE_URI,
							query);
		} else if (dataSetType == VoidDatasetType.CKAN) {
			qexec = QueryExecutionFactory.sparqlService(
					VOIDOntologyVocabulary.CKAN_SERVICE, query);
		}
		Model execConstruct = null;
		try {
			execConstruct = qexec.execConstruct();
			// ResultSet execSelect = qexec.execSelect();
			// ResultSetFormatter.out(execSelect);
		} catch (JenaException e) {
			throw new CrawlerException(dataSetType.toString()
					+ " is not available now", e);
		} finally {
			qexec.close();
		}
		return execConstruct;
	}

	/**
	 * Creates a query for searching datasets according to the dataset name.
	 * 
	 * @param sparqlEndpointURI
	 * @return
	 */
	private String constructQueryWithEndpointURL(String sparqlEndpointURI) {
		return "PREFIX void: <http://rdfs.org/ns/void#> "
				+ "CONSTRUCT {?ds ?p ?o} WHERE { ?ds void:sparqlEndpoint <"
				+ sparqlEndpointURI + ">. ?ds ?p ?o}";
	}

	/**
	 * Creates a query for searching datasets according to the dataset's topic
	 * property.
	 * 
	 * @param sparqlEndpointURI
	 * @return
	 */
	private String constructQueryWithTopicURI(String topicURI) {
		return "PREFIX dc: <http://purl.org/dc/terms/> "
				+ "CONSTRUCT {?ds ?p ?o.} WHERE { ?ds dc:subject <" + topicURI
				+ ">. ?ds ?p ?o}";
	}

	/**
	 * Create sparql query to get void dataset from the VOID STORE.
	 * 
	 * @param descriptionForDataset
	 *            Keyword parameter.
	 * @return Query string for sparql query.
	 */
	private String createDatasetSearchQueryAccordingToName(
			String descriptionForDataset) {
		return "PREFIX rdfs:     <http://www.w3.org/2000/01/rdf-schema#> "
				+ "PREFIX void:     <http://rdfs.org/ns/void#> "
				+ "PREFIX dc: <http://purl.org/dc/terms/>"
				+ "SELECT DISTINCT ?sparqlEndpoint WHERE { "
				+ "?ds a void:Dataset . " + "?ds dc:identifier \""
				+ descriptionForDataset + "\". "
				// + "FILTER regex(?title, \"" + descriptionForDataset
				// + "\", \"i\" )."
				+ "?ds void:sparqlEndpoint ?sparqlEndpoint . }";
	}

	public static void main(String[] args) throws CrawlerException {
		new VoidCrawler().crawlDatasetAccordingToDatasetName("web science",
				"http://querySolution", null, VoidDatasetType.CKAN);
	}

	/**
	 * It retrieves all voids that includes void:sparqlEndpoint property in it
	 * from the given service.
	 * 
	 * @param serviceType
	 * @param count
	 *            count of retrieved voids. If it is 0, all datasets are
	 *            retrieved.
	 */
	public List<OntModel> crawlAllDatasetFromService(
			VoidDatasetType serviceType, int count) {
		String voidStoreURL = getVoidStoreURL(serviceType);
		String query = "PREFIX void: <http://rdfs.org/ns/void#> SELECT ?voidURI WHERE {SERVICE <"
				+ voidStoreURL + "> {?voidURI void:sparqlEndpoint ?url.}}";
		QueryExecution getAllVoidQuery = QueryExecutionFactory.create(query,
				ModelFactory.createOntologyModel());
		ResultSet execSelect = getAllVoidQuery.execSelect();
		List<String> voidURIs = new Vector<String>();
		// get all void URIs that has a sparql endpoint.
		while (execSelect.hasNext()) {
			QuerySolution next = execSelect.next();
			RDFNode rdfNode = next.get("voidURI");
			voidURIs.add(rdfNode.asResource().getURI());
		}
		System.out.println(voidURIs.size() + " void URIs retrieved from "
				+ serviceType);
		List<OntModel> voidModels = new Vector<OntModel>();
		int index = 0;
		// get all void models
		for (String voidURI : voidURIs) {
			index++;
			OntModel voidModel = getVoidModel(voidURI, serviceType);
			// get linksets for the given void uri
			getLinksets(voidModel, voidURI, serviceType);
			System.out.println(index + ") VOID that has void URI:" + voidURI
					+ " is retrieved.");
			voidModels.add(voidModel);
			if (index == count)
				break;
		}
		return voidModels;
	}

	/**
	 * Get linksets of void.
	 * 
	 * @param voidModel
	 * @param voidURI
	 * @param serviceType
	 */
	public void getLinksets(OntModel voidModel, String voidURI,
			VoidDatasetType serviceType) {
		String query = "PREFIX void: <http://rdfs.org/ns/void#> CONSTRUCT {<"
				+ voidURI + "> ?p ?o} WHERE { <" + voidURI
				+ "> void:subset ?linkset. ?linkset ?p ?o.}";
		QueryExecution getVoidModelQuery = QueryExecutionFactory.sparqlService(
				getVoidStoreURL(serviceType), query);
		Model linksetModel = getVoidModelQuery.execConstruct();
		voidModel.add(linksetModel);

	}

	/**
	 * It returns an ontmodel that includes information about VOID that has
	 * given void URI from given service..
	 * 
	 * @param voidURI
	 * @param serviceType
	 * @return
	 */
	private OntModel getVoidModel(String voidURI, VoidDatasetType serviceType) {
		String query = "CONSTRUCT {<" + voidURI + "> ?p ?o} WHERE { <"
				+ voidURI + "> ?p ?o.}";
		QueryExecution getVoidModelQuery = QueryExecutionFactory.sparqlService(
				getVoidStoreURL(serviceType), query);
		Model voidModel = getVoidModelQuery.execConstruct();
		OntModel ontModel = createQueryModel(voidModel, "http://querysolution/"
				+ voidURI);
		return ontModel;
	}

	/**
	 * Crawl linksets of dataset that has the given dataset URI.
	 * 
	 * @param voidDatasetURI
	 * @param voidStoreType
	 * @param voidModel
	 * @return
	 */
	private OntModel addLinksetsToGivenVoidModel(String voidDatasetURI,
			VoidDatasetType voidStoreType, OntModel voidModel) {
		String linksetQuery = "CONSTRUCT {?linkset ?p ?o.} WHERE {<"
				+ voidDatasetURI + "> <"
				+ VOIDOntologyVocabulary.DATASET_subset
				+ "> ?linkset. ?linkset ?p ?o. }";
		QueryExecution linksetExec = QueryExecutionFactory.sparqlService(
				getVoidStoreURL(voidStoreType), linksetQuery);
		Model linksetModel = linksetExec.execConstruct();
		voidModel.add(linksetModel);
		return voidModel;
	}

	/**
	 * Change targets to subejctTarget and objectTarget of Linkset concept.
	 * 
	 * @param voidDatasetURI
	 * @param ontURI
	 * @param voidModel
	 * @return
	 */
	private OntModel specifyLinksetDirections(String voidDatasetURI,
			String ontURI, OntModel voidModel) {
		VOIDIndividualOntology indvOnt = new VOIDIndividualOntology(ontURI,
				voidModel);
		// get subject targets...
		List<Statement> targets = voidModel.listStatements(null,
				VOIDOntologyVocabulary.LINKSET_target_prp, (RDFNode) null)
				.toList();
		// set object and subject targets
		for (Statement stmt : targets) {
			Individual linksetIndv = stmt.getSubject().as(Individual.class);
			Individual target = stmt.getObject().as(Individual.class);
			if (target.getURI().equals(voidDatasetURI)) {
				indvOnt.setLinksetSubjectsTarget(linksetIndv, target);
			} else {
				indvOnt.setLinksetObjectsTarget(linksetIndv, target);
			}
			indvOnt.getOntModel().remove(stmt);
		}
		return indvOnt.getOntModel();
	}

	/**
	 * Crawls linksets of the dataset that has the given datasetURI. Void model
	 * should be crawled before.
	 * 
	 * @param voidDatasetURI
	 * @param ontURI
	 * @param voidStoreType
	 * @param voidModel
	 * @return
	 */
	public OntModel crawlVoidLinksets(String voidDatasetURI, String ontURI,
			VoidDatasetType voidStoreType, OntModel voidModel) {
		OntModel modelHasNonDirectionLinksets = addLinksetsToGivenVoidModel(
				voidDatasetURI, voidStoreType, voidModel);
		return specifyLinksetDirections(voidDatasetURI, ontURI,
				modelHasNonDirectionLinksets);

	}

	public static OntModel crawlDatasetAccordingToURI(String voidDatasetURI,
			VoidDatasetType voidStoreType, String ontURI) {
		String query = "CONSTRUCT {<" + voidDatasetURI + "> ?p ?o} WHERE {<"
				+ voidDatasetURI + "> ?p ?o}";
		QueryExecution execution = QueryExecutionFactory.sparqlService(
				getVoidStoreURL(voidStoreType), query);
		Model voidModel = execution.execConstruct();
		OntModel voidOntModel = createQueryModel(voidModel, ontURI);
		return voidOntModel;
	}
}
