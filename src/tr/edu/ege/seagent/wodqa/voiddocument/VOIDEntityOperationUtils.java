package tr.edu.ege.seagent.wodqa.voiddocument;

import java.util.List;
import java.util.Vector;

import tr.edu.ege.seagent.triplestoremanager.SDBHandler;
import tr.edu.ege.seagent.wodqa.QueryVocabulary;
import tr.edu.ege.seagent.wodqa.exception.VOIDStoreCreationException;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

public class VOIDEntityOperationUtils {

	private static VOIDEntityOperationUtils instance;

	private static String VOID_PREFIX = "PREFIX void: <http://rdfs.org/ns/void#>";

	public static VOIDEntityOperationUtils getInstance(SDBHandler handler) {
		if (instance == null) {
			instance = new VOIDEntityOperationUtils(handler);
		}
		return instance;
	}

	private SDBHandler handler;

	/**
	 * Void model graph URIs that contains void models' graph URIs.
	 */
	public static final String VOID_MODEL_GRAPH_PATH_EXTENSION = "/VoidModelGraphs";

	public VOIDEntityOperationUtils(SDBHandler handler) {
		super();
		this.handler = handler;
	}

	/**
	 * Adds the given Void model's graph URI to the store to find after.
	 * 
	 * @param graphUriOfVOid
	 *            Graph URI of Void Model.
	 * @param VoidSubjectURI
	 *            Ontology URI of model to create VoidIndv ont.
	 */
	public void addVoidModelGraphURIToStore(String graphUriOfVOid,
			String VoidSubjectURI, String crawlerAgentFullName) {
		getHandler()
				.newGraphManager()
				.addTriple(
						"http://"
								+ crawlerAgentFullName
								+ VOIDEntityOperationUtils.VOID_MODEL_GRAPH_PATH_EXTENSION,
						new Triple(Node.createURI(VoidSubjectURI), Node
								.createURI("voidModelGraphUri"), Node
								.createURI(graphUriOfVOid)), false);
	}

	/**
	 * Get all dataset has void:exampleResource property and void:sparqlEndpoint
	 * property.
	 */
	public ResultSet getAllDatasetHasExampleResource() {
		ResultSet resulSet = getHandler()
				.execSelect(
						"PREFIX void: <http://rdfs.org/ns/void#>"
								+ "SELECT DISTINCT ?endpointURL ?exampleResource ?dataset WHERE { GRAPH ?graph {"
								+ "?dataset a void:Dataset. "
								+ "?dataset void:exampleResource ?exampleResource. "
								+ "?dataset void:sparqlEndpoint ?endpointURL."
								+ "}}");
		return resulSet;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// ///////////////////////////////// END OF LINKSET FINDER
	// ////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Get endpoints and examplreresource from resultset.
	 * 
	 * @param resultSet
	 */
	public void getExampleResourceAndDatasetFromResultSet(ResultSet resultSet,
			List<Resource> exRscs, List<Resource> datasets) {
		while (resultSet.hasNext()) {
			QuerySolution next = resultSet.next();
			Resource datasetRsc = next.getResource("dataset");
			datasets.add(datasetRsc);
			Resource exrsc = next.getResource("exampleResource");
			exRscs.add(exrsc);
		}
	}

	/**
	 * Get Linkset objects' with the given parameters from given dataset list.
	 * 
	 * @param crawlerAgentKnowledgeManager
	 * @param agentSimpleName
	 * @param subjectsTargetDataset
	 * @param linkPredicateURI
	 * @return
	 */
	public void getLinksetObjects(List<OntModel> datasetList,
			Resource subjectsTargetDataset, String linkPredicateURI,
			List<String> endpointList, List<Resource> objectList) {

		OntModel emptyOntModel = ModelFactory.createOntologyModel();
		// find all object datasets...
		for (OntModel ontModel : datasetList) {
			emptyOntModel.add(ontModel);
		}
		Dataset currentDataset = DatasetFactory.create(emptyOntModel);
		// get searched subject URI.
		String subjectTargetURI = prepareTripleObject(subjectsTargetDataset);

		Query query = QueryFactory
				.create(getObjectWithExplicitSubjectAndPredicateFromModels(
						linkPredicateURI, subjectTargetURI));
		// create the query execution on dataset...
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				currentDataset);
		// execute select query...
		ResultSet resultSet = queryExecution.execSelect();
		// set results to the given lists...
		getObjectDatasetAndEndpointFromResultSet(resultSet, endpointList,
				objectList);
	}

	/**
	 * Get Linkset objects' with the given parameters from store.
	 * 
	 * @param databaseName
	 * @param subjectsTargetDataset
	 * @param linkPredicateURI
	 * 
	 * @return
	 */
	public ResultSet getLinksetObjects(String databaseName,
			Resource subjectsTargetDataset, String linkPredicateURI) {
		// set searched graph URI.
		String graphURI = makeGraphURI(databaseName,
				getOntologyURIofResource(subjectsTargetDataset));

		String subjectTargetURI = prepareTripleObject(subjectsTargetDataset);
		// execute select query...
		String query = getLinksetObjectsWithGivenExplicitSubjectAndPredicateFromStore(
				linkPredicateURI, graphURI, subjectTargetURI);
		ResultSet resultSet = getHandler().execSelect(query);
		return resultSet;

	}

	/**
	 * Gets referenced datasets by a specified dataset.
	 * 
	 * @param subjectsTargetIndv
	 * @param linkPredicateURI
	 * @param databaseName
	 *            name of the database.
	 * @return a {@link ResultSet} instance. You can access results to use
	 *         variable names: "linkPredicate", "datasetObject", "linkset",
	 *         "objectDatasetGraph".
	 */
	public ResultSet getLinksetObjectsFromStore(Individual subjectsTargetIndv,
			String linkPredicateURI, String databaseName) {
		// set searched graph URI.
		String graphURI = makeGraphURI(databaseName, subjectsTargetIndv
				.getOntModel().listOntologies().toList().get(0).getURI());

		// get searched subject URI.
		String subjectTargetURI = subjectsTargetIndv.getURI();
		if (subjectTargetURI == null) {
			subjectTargetURI = "_:" + subjectsTargetIndv.getId().toString();
		}
		linkPredicateURI = prepareLinkPredicate(linkPredicateURI);
		// execute select query...
		ResultSet resultSet = getHandler()
				.execSelect(
						"PREFIX void: <http://rdfs.org/ns/void#>"
								+ "SELECT * WHERE { GRAPH <"
								+ graphURI
								+ "> {"
								+ "?linkset a void:Linkset. "
								+ "?linkset void:subset <"
								+ subjectTargetURI
								+ ">."
								+ "?linkset void:subjectsTarget <"
								+ subjectTargetURI
								+ ">. "
								+ "?linkset void:objectsTarget ?datasetObject."
								+ "?linkset void:linkPredicate "
								+ linkPredicateURI
								+ ".} GRAPH ?objectDatasetGraph {?datasetObject void:sparqlEndpoint ?endpointURL.}}");
		return resultSet;

	}

	/**
	 * It returns the subject targets endpoints of linksets that datasets has
	 * given linkpredicate in its linksets.
	 * 
	 * @param linkPredicateURI
	 * @return
	 */
	public ResultSet getLinksetsObjectTargetEndpointsAccordingToPredicate(
			String linkPredicateURI) {
		ResultSet resulSet = getHandler().execSelect(
				"PREFIX void: <http://rdfs.org/ns/void#>"
						+ "SELECT DISTINCT ?endpointURL WHERE { GRAPH ?graph {"
						+ "?linkset a void:Linkset. "
						+ "?linkset void:subset ?datasetSubject."
						+ "?linkset void:subjectsTarget ?datasetSubject. "
						+ "?linkset void:objectsTarget ?datasetObject."
						+ "?linkset void:linkPredicate <" + linkPredicateURI
						+ ">.} GRAPH ?objectGraph {"
						+ "?datasetObject void:sparqlEndpoint ?endpointURL.}}");
		return resulSet;
	}

	/**
	 * It returns the datasets' endpoints that datasets has given linkpredicate
	 * in its linksets.
	 * 
	 * @param crawlerAgent
	 * @param linkPredicateURI
	 * @return
	 */
	public void getLinksetsSubjectsAccordingToPredicate(
			List<OntModel> datasetList, String linkPredicateURI,
			List<String> endpoints, List<Resource> datasets) {
		OntModel emptyOntModel = ModelFactory.createOntologyModel();
		for (OntModel ontModel : datasetList) {
			emptyOntModel.add(ontModel);
		}
		Dataset currentDataset = DatasetFactory.create(emptyOntModel);
		Query query = QueryFactory
				.create(getSubjectDatasetAccordingToGivenPredicateFromModels(linkPredicateURI));
		// create the query execution on dataset...
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				currentDataset);
		// execute select query...
		ResultSet resultSet = queryExecution.execSelect();
		getSubjectDatasetAndEndpointFromResultSet(resultSet, endpoints,
				datasets);

	}

	/**
	 * It returns the datasets' endpoints that datasets has given linkpredicate
	 * in its linksets.
	 * 
	 * @param linkPredicateURI
	 * @param crawlerAgent
	 * 
	 * @return
	 */
	public ResultSet getLinksetsSubjectsAccordingToPredicate(
			String linkPredicateURI) {
		ResultSet resulSet = getHandler()
				.execSelect(
						getSubjectDatasetAccordingToGivenPredicateFromStore(linkPredicateURI));
		return resulSet;
	}

	/**
	 * Its duty is same with etLinksetSubjectsFromStore( KnowledgeManager
	 * crawlerKnowledgeManager, Resource objectTargetIndv, String
	 * linkPredicateURI) method. But it searches on given dataset list instead
	 * of triple store.
	 * <p>
	 * FIXME Yalnizca testte kullaniliyor.
	 * 
	 * @param datasetList
	 *            holds all datasets.
	 * @param objectTargetIndv
	 *            represents referenced dataset.
	 * @param linkPredicateURI
	 *            is Linkset's predicate.
	 * @param endpointList
	 *            are Subject datasets' endpoints.
	 * @param subjectList
	 *            are subject datasets.
	 */
	public void getLinksetSubjects(List<OntModel> datasetList,
			Resource objectTargetIndv, String linkPredicateURI,
			List<String> endpointList, List<Resource> subjectList) {
		OntModel emptyOntModel = ModelFactory.createOntologyModel();
		// federate all ontmodels.
		for (OntModel ontModel : datasetList) {
			emptyOntModel.add(ontModel);
		}
		Dataset currentDataset = DatasetFactory.create(emptyOntModel);
		// get searched subject URI.
		String objectTargetDatasetURI = prepareTripleObject(objectTargetIndv);
		
		String queryStr = getSubjectWithExplicitPredicateAndObjectFromModels(
				linkPredicateURI, objectTargetDatasetURI);
		System.out.println("QUERY: " + queryStr);
		
		// create the query...
		Query query = QueryFactory.create(queryStr);
		// create the query execution on dataset...
		QueryExecution queryExecution = QueryExecutionFactory.create(query,
				currentDataset);
		// execute select query...
		ResultSet resultSet = queryExecution.execSelect();
		getSubjectDatasetAndEndpointFromResultSet(resultSet, endpointList,
				subjectList);

	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////
	// //////////////////// LINKSET FINDER
	// //////////////////////////////////////////////////
	// ///////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Gets linksets that references a specified dataset from store.
	 * 
	 * @param linkPredicateURI
	 *            link predicate URI. If it is null, it will be transform to
	 *            variable like "?linkPredicate".
	 * @param crawlerAgent
	 *            Queried crawler agent.
	 * @param objectTargetDatasetURI
	 *            specified datasetURI to referenced.
	 * 
	 * @return It returns {@link ResultSet} instance. You can access the result
	 *         through using variables name: "linkset", "datasetSubject",
	 *         "endpointURL", "linkPredicate", "graph".
	 */
	public ResultSet getLinksetSubjects(Resource objectTargetIndv,
			String linkPredicateURI) {
		// prepare object target...
		String objectTargetDatasetURI = prepareTripleObject(objectTargetIndv);
		// prepare query...
		String query = getSubjectWithExplicitPredicateAndObjectFromStore(
				linkPredicateURI, objectTargetDatasetURI);
		// execute query on store...
		ResultSet resulSet = getHandler().execSelect(query);
		return resulSet;
	}

	/**
	 * Get endpoints and subjectDataset from resultset.
	 * 
	 * @param resultSet
	 */
	public void getObjectDatasetAndEndpointFromResultSet(ResultSet resultSet,
			List<String> endpoints, List<Resource> datasets) {
		while (resultSet.hasNext()) {
			QuerySolution next = resultSet.next();
			Resource datasetRsc = next.getResource("datasetObject");
			datasets.add(datasetRsc);
			RDFNode endpointObject = next.get("endpointURL");
			String value = getEndpointString(endpointObject);
			if (value != null)
				endpoints.add(value);
		}
	}

	/**
	 * It gets the ontology URI of given resource.
	 * 
	 * @param subjectsTargetDataset
	 * @param crawlerAgent
	 * @return
	 */
	public String getOntologyURIofResource(Resource subjectsTargetDataset) {
		if (subjectsTargetDataset.getClass().equals(Individual.class))
			return ((Individual) subjectsTargetDataset).getOntModel()
					.listOntologies().toList().get(0).getURI();
		else {
			String subjectTargetURI = prepareTripleObject(subjectsTargetDataset);
			String query = "PREFIX void: <http://rdfs.org/ns/void#> "
					+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
					+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
					+ "SELECT * WHERE { " + "GRAPH ?g { <" + subjectTargetURI
					+ "> a void:Dataset. ?ontology rdf:type owl:Ontology.}}";
			ResultSet execSelect = getHandler().execSelect(query);
			while (execSelect.hasNext()) {
				Resource ontology = execSelect.next().getResource("ontology");
				if (ontology.getURI()
						.indexOf(QueryVocabulary.VOID_ONTOLOGY_URI) == -1) {
					return ontology.getURI();
				}
			}
		}
		return null;
	}

	/**
	 * Get endpoints and subjectDataset from resultset.
	 * 
	 * @param resultSet
	 */
	public void getSubjectDatasetAndEndpointFromResultSet(ResultSet resultSet,
			List<String> endpoints, List<Resource> datasets) {
		while (resultSet.hasNext()) {
			QuerySolution next = resultSet.next();
			Resource datasetRsc = next.getResource("datasetSubject");
			datasets.add(datasetRsc);
			RDFNode endpointObject = next.get("endpointURL");
			String value = getEndpointString(endpointObject);
			if (value != null)
				endpoints.add(value);
		}
	}

	public OntModel getVOIDModelFromStoreAccordingToEndpointURI(
			SDBHandler handler, String endpURI) {
		OntModel voidModel = null;
		String getDatasetUri = VOID_PREFIX
				+ " SELECT * WHERE {GRAPH ?g {?s void:sparqlEndpoint <"
				+ endpURI + ">.}}";
		ResultSet resultset = handler.execSelect(getDatasetUri);
		String graphURI = null;
		if (resultset.hasNext()) {
			QuerySolution solution = resultset.nextSolution();
			graphURI = solution.getResource("g").getURI();
			voidModel = handler.newGraphManager().getGraphModel(graphURI);
		}
		return voidModel;
	}

	/**
	 * Get VOID models graph URIs from crawler agent's sepecified graph.
	 * 
	 * @return
	 */
	public OntModel getVOIDModelPaths(String dbName) {
		return getHandler()
				.newGraphManager()
				.getGraphModel(
						"http://"
								+ dbName
								+ VOIDEntityOperationUtils.VOID_MODEL_GRAPH_PATH_EXTENSION);
	}

	/**
	 * Gets the void models from the crawler agent store.s
	 * 
	 * @param dbName
	 *            agent name for the graph uri paths.
	 * 
	 * @return
	 */
	public List<OntModel> getVOIDModels(String dbName) {
		// get void Model paths
		OntModel modelPathsModel = getVOIDModelPaths(dbName);
		List<OntModel> voidModelList = new Vector<OntModel>();
		if (modelPathsModel != null) {
			List<Statement> modelPathStatements = modelPathsModel
					.listStatements().toList();
			// get every model from its graph...
			for (int i = 0; i < modelPathStatements.size(); i++) {
				String modelUri = modelPathStatements.get(i).getObject()
						.asNode().getURI().toString();
				OntModel voidModel = getHandler().newGraphManager()
						.getGraphModel(modelUri);
				voidModelList.add(voidModel);
			}
		}
		return voidModelList;
	}

	/**
	 * Gets the void models from the given handler's store.
	 * 
	 * @param handler
	 *            it holds the void models.
	 * @return VOID Model list.
	 * @throws VOIDStoreCreationException
	 */
	public List<OntModel> getVOIDModels(VOIDRegisterer registerer)
			throws VOIDStoreCreationException {
		// get void Model paths
		OntModel modelPathsModel = getVOIDModelPaths(registerer);
		List<OntModel> voidModelList = new Vector<OntModel>();
		if (modelPathsModel != null) {
			List<Statement> modelPathStatements = modelPathsModel
					.listStatements().toList();
			// get every model from its graph...
			for (int i = 0; i < modelPathStatements.size(); i++) {
				String modelUri = modelPathStatements.get(i).getObject()
						.asNode().getURI().toString();
				OntModel voidModel = registerer.getHandler().newGraphManager()
						.getGraphModel(modelUri);
				voidModelList.add(voidModel);
			}
		}
		return voidModelList;
	}

	/**
	 * Makes graph name of saved void model.
	 * 
	 * @param voidModelGraphPrefixName
	 * @param querySolutionForDataset
	 * @return
	 */
	public String makeGraphURI(String voidModelGraphPrefixName,
			String querySolutionForDataset) {
		return "http://" + voidModelGraphPrefixName + "/"
				+ querySolutionForDataset.substring(7);
	}

	/**
	 * It prepares link predicate as a variable or an explicit URI.
	 * 
	 * @param linkPredicateURI
	 * @return
	 * @throws Exception
	 */
	public String prepareLinkPredicate(Node linkPredicate) throws Exception {
		if (linkPredicate.isVariable()) {
			return "?linkPredicate";
		} else if (linkPredicate.isURI())
			return "<" + linkPredicate.getURI() + ">";
		else
			throw new Exception("Predicate cant be blank node in query");
	}

	/**
	 * It prepares link predicate as a variable or an explicit URI.
	 * 
	 * @param linkPredicateURI
	 * @return
	 */
	public String prepareLinkPredicate(String linkPredicateURI) {
		if (linkPredicateURI == null) {
			linkPredicateURI = "?linkPredicate";
		} else
			linkPredicateURI = "<" + linkPredicateURI + ">";
		return linkPredicateURI;
	}

	/**
	 * Saves the given linkset individual to the given grapgh URI in the agent's
	 * store.
	 * 
	 * @param linksetIndv
	 * @param graphURI
	 */
	public void saveLinksetIndvToStore(Individual linksetIndv, String graphURI) {
		getHandler().addIndividual(graphURI, linksetIndv);
	}

	private SDBHandler getHandler() {
		return handler;
	}

	/**
	 * It saves linkset list to the graph that is constrcuted from
	 * querySolutionURI in the given crawler agent's store.
	 * 
	 * @param linksetList
	 * @param querySolutionURI
	 * @param databaseName
	 *            name of the database.
	 */
	public void saveLinksetList(List<Individual> linksetList,
			String querySolutionURI, String databaseName) {
		String graphURI = makeGraphURI(databaseName, querySolutionURI);
		for (Individual linksetIndv : linksetList) {
			saveLinksetIndvToStore(linksetIndv, graphURI);
		}
	}

	/**
	 * Saves given dataset to the graph and returns saved graph URI.
	 * 
	 * @param voidModel
	 *            VOID model
	 * @param datasetOntologyURI
	 *            VOID model ontology URI.
	 * @param dbName
	 *            database name.
	 * @return saved Graph URI
	 */
	public String saveVOIDModelToGraph(OntModel voidModel,
			String datasetOntologyURI, String dbName) {
		String graphURI = makeGraphURI(dbName, datasetOntologyURI);
		getHandler().addModel(voidModel, graphURI);
		return graphURI;
	}

	public void saveVOIDModelWithHandler(OntModel voidModel,
			VOIDRegisterer registerer, String datasetOntologyURI)
			throws VOIDStoreCreationException {
		String graphUriOfVoid = saveVOIDModelToGraph(voidModel, registerer,
				datasetOntologyURI);
		addVoidModelGraphURIToStore(graphUriOfVoid, registerer,
				datasetOntologyURI);
	}

	/**
	 * Adds the given Void model's graph URI to the store to find after.
	 * 
	 * @param graphUriOfVOid
	 *            Graph URI of Void Model.
	 * @param knowledgeManager
	 *            knowledgeManager of crawler agent of store.
	 * @param VoidSubjectURI
	 *            Ontology URI of model to create VoidIndv ont.
	 */
	private void addVoidModelGraphURIToStore(String graphUriOfVOid,
			VOIDRegisterer registerer, String VoidSubjectURI) {
		try {
			registerer
					.getHandler()
					.newGraphManager()
					.addTriple(
							"http://"
									+ registerer.getDbName()
									+ VOIDEntityOperationUtils.VOID_MODEL_GRAPH_PATH_EXTENSION,
							new Triple(Node.createURI(VoidSubjectURI), Node
									.createURI("voidModelGraphUri"), Node
									.createURI(graphUriOfVOid)), false);
		} catch (VOIDStoreCreationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get endpoint URL string from the endpoint rdfnode.
	 * 
	 * @param endpointObject
	 * @return
	 */
	private String getEndpointString(RDFNode endpointObject) {
		String value = null;
		if (endpointObject.isResource()) {
			value = endpointObject.asResource().getURI().toString();
		} else if (endpointObject.isLiteral()) {
			value = endpointObject.asLiteral().getValue().toString();
		}
		return value;
	}

	/**
	 * Creates a query that gets Linksets' object target datasets from store.
	 * 
	 * @param linkPredicateURI
	 * @param graphURI
	 * @param subjectTargetURI
	 * @return
	 */
	private String getLinksetObjectsWithGivenExplicitSubjectAndPredicateFromStore(
			String linkPredicateURI, String graphURI, String subjectTargetURI) {
		return "PREFIX void: <http://rdfs.org/ns/void#>"
				+ "SELECT DISTINCT ?endpointURL ?datasetObject WHERE { {GRAPH <"
				+ graphURI
				+ "> {"
				+ "?linkset a void:Linkset. "
				+ "?linkset void:subjectsTarget <"
				+ subjectTargetURI
				+ ">. "
				+ "?linkset void:objectsTarget ?datasetObject."
				+ "?linkset void:linkPredicate "
				+ linkPredicateURI
				+ ".}} UNION {GRAPH <"
				+ graphURI
				+ "> {"
				+ "?linkset a void:Linkset. "
				+ "?linkset void:subjectsTarget <"
				+ subjectTargetURI
				+ ">. "
				+ "?linkset void:objectsTarget ?datasetObject."
				+ "FILTER NOT EXISTS {?linkset void:linkPredicate ?x.} }}"
				+ " GRAPH ?objectDatasetGraph {?datasetObject void:sparqlEndpoint ?endpointURL.}}";
	}

	/**
	 * Gets Linksets' object targets with given parameters.
	 * 
	 * @param predicateURI
	 * @param subjectTargetURI
	 * @return
	 */
	private String getObjectWithExplicitSubjectAndPredicateFromModels(
			String predicateURI, String subjectTargetURI) {
		return "PREFIX void: <http://rdfs.org/ns/void#>"
				+ "SELECT DISTINCT ?datasetObject ?endpointURL WHERE { "
				+ "{?linkset a void:Linkset. "
				+ "?linkset void:subjectsTarget <"
				+ subjectTargetURI
				+ ">. "
				+ "?linkset void:objectsTarget ?datasetObject."
				+ "?linkset void:linkPredicate "
				+ predicateURI
				+ "."
				+ " ?datasetObject void:sparqlEndpoint ?endpointURL.} UNION {?linkset a void:Linkset. "
				+ "?linkset void:subjectsTarget <"
				+ subjectTargetURI
				+ ">. "
				+ "?linkset void:objectsTarget ?datasetObject."
				+ " ?datasetObject void:sparqlEndpoint ?endpointURL. FILTER NOT EXISTS {?linkset void:linkPredicate ?x}}}";
	}

	/**
	 * Gets subject datasets according to the given link predicate from store.
	 * 
	 * @param linkPredicateURI
	 * @return
	 */
	private String getSubjectDatasetAccordingToGivenPredicateFromModels(
			String linkPredicateURI) {
		return VOID_PREFIX
				+ "SELECT DISTINCT ?endpointURL ?datasetSubject WHERE { "
				+ "{?linkset a void:Linkset. "
				+ "?linkset void:subjectsTarget ?datasetSubject. "
				+ "?linkset void:objectsTarget ?datasetObject. "
				+ "?linkset void:linkPredicate "
				+ linkPredicateURI
				+ ". ?datasetSubject void:sparqlEndpoint ?endpointURL.} UNION {?linkset a void:Linkset. "
				+ "?linkset void:subjectsTarget ?datasetSubject. "
				+ "?linkset void:objectsTarget ?datasetObject. ?datasetSubject void:sparqlEndpoint ?endpointURL."
				+ " FILTER NOT EXISTS {?linkset void:linkPredicate ?x.}}" + "}";
	}

	/**
	 * Gets subject datasets according to the given link predicate from store.
	 * 
	 * @param linkPredicateURI
	 * @return
	 */
	private String getSubjectDatasetAccordingToGivenPredicateFromStore(
			String linkPredicateURI) {
		return "PREFIX void: <http://rdfs.org/ns/void#>"
				+ "SELECT DISTINCT ?endpointURL ?datasetSubject WHERE { {GRAPH ?graph {"
				+ "?linkset a void:Linkset. "
				+ "?linkset void:subjectsTarget ?datasetSubject. "
				+ "?linkset void:objectsTarget ?datasetObject. "
				+ "?linkset void:linkPredicate "
				+ linkPredicateURI
				+ ". ?datasetSubject void:sparqlEndpoint ?endpointURL."
				+ "}} UNION {GRAPH ?graph {"
				+ "?linkset a void:Linkset. "
				+ "?linkset void:subjectsTarget ?datasetSubject. "
				+ "?linkset void:objectsTarget ?datasetObject. ?datasetSubject void:sparqlEndpoint ?endpointURL."
				+ "FILTER NOT EXISTS {?linkset void:linkPredicate ?x}}"
				+ "}  }";
	}

	/**
	 * 
	 * Prepare a query with given parameters that queries Linkset Subjects that
	 * has given explicit parameters. It queries from local dataset...
	 * 
	 * @param predicateURI
	 * @param objectTargetDatasetURI
	 * @return
	 */
	private String getSubjectWithExplicitPredicateAndObjectFromModels(
			String predicateURI, String objectTargetDatasetURI) {

		return "PREFIX void: <http://rdfs.org/ns/void#>"
				+ "SELECT DISTINCT ?endpointURL ?datasetSubject WHERE {"
				+ "{?linkset a void:Linkset. "
				+ "?linkset void:subjectsTarget ?datasetSubject. "
				+ "?linkset void:objectsTarget <" + objectTargetDatasetURI
				+ ">.?datasetSubject void:sparqlEndpoint ?endpointURL."
				+ "?linkset void:linkPredicate " + predicateURI
				+ ".} UNION {?linkset a void:Linkset. "
				+ "?linkset void:subjectsTarget ?datasetSubject. "
				+ "?linkset void:objectsTarget <" + objectTargetDatasetURI
				+ ">.?datasetSubject void:sparqlEndpoint ?endpointURL."
				+ "FILTER NOT EXISTS {?linkset void:linkPredicate ?x} }" + "}";
	}

	/**
	 * Prepare a query with given parameters that queries Linkset Subjects that
	 * has given explicit parameters.It queries from store...
	 * 
	 * @param linkPredicateURI
	 * @param objectTargetDatasetURI
	 * @return
	 */
	private String getSubjectWithExplicitPredicateAndObjectFromStore(
			String linkPredicateURI, String objectTargetDatasetURI) {
		return "PREFIX void: <http://rdfs.org/ns/void#>"
				+ "SELECT DISTINCT ?endpointURL ?datasetSubject WHERE { {GRAPH ?graph {"
				+ "?linkset a void:Linkset. "
				+ "?linkset void:subjectsTarget ?datasetSubject. "
				+ "?linkset void:objectsTarget <" + objectTargetDatasetURI
				+ ">.?datasetSubject void:sparqlEndpoint ?endpointURL."
				+ "?linkset void:linkPredicate " + linkPredicateURI + "."
				+ "}}" + " UNION {GRAPH ?graph {?linkset a void:Linkset. "
				+ "?linkset void:subjectsTarget ?datasetSubject. "
				+ "?linkset void:objectsTarget <" + objectTargetDatasetURI
				+ ">.?datasetSubject void:sparqlEndpoint ?endpointURL."
				+ " FILTER NOT EXISTS {?linkset void:linkPredicate ?x.}}  }  }";
	}

	/**
	 * Gets the void model graph uri paths.
	 * 
	 * @param handler
	 * @return
	 */
	private OntModel getVOIDModelPaths(VOIDRegisterer registerer) {
		try {
			return registerer
					.getHandler()
					.newGraphManager()
					.getGraphModel(
							"http://"
									+ registerer.getDbName()
									+ VOIDEntityOperationUtils.VOID_MODEL_GRAPH_PATH_EXTENSION);
		} catch (VOIDStoreCreationException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * It prepares object with URI or ID. Because object may be a blank node. If
	 * it is a blank node, object is used with ID.
	 * 
	 * @param objectTargetIndv
	 * @return
	 */
	private String prepareTripleObject(Resource objectTargetIndv) {
		String objectTargetDatasetURI = objectTargetIndv.getURI();
		if (objectTargetDatasetURI == null) {
			objectTargetDatasetURI = "_:" + objectTargetIndv.getId().toString();
		}
		return objectTargetDatasetURI;
	}

	/**
	 * Saves given dataset to the graph and returns saved graph URI.
	 * 
	 * @param voidModel
	 *            VOID model
	 * @param crawlerAgent
	 *            crawler agent
	 * @param datasetOntologyURI
	 *            VOID model ontology URI.
	 * @return saved Graph URI
	 * @throws VOIDStoreCreationException
	 */
	private String saveVOIDModelToGraph(OntModel voidModel,
			VOIDRegisterer registerer, String datasetOntologyURI)
			throws VOIDStoreCreationException {
		String graphURI = "";
		try {
			graphURI = makeGraphURI(registerer.getDbName(), datasetOntologyURI);
		} catch (VOIDStoreCreationException e) {
			e.printStackTrace();
		}
		registerer.getHandler().addModel(voidModel, graphURI);
		return graphURI;
	}
}