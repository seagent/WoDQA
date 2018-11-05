package tr.edu.ege.seagent.wodqa.query.analyzer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementBind;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementService;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;
import com.hp.hpl.jena.sparql.syntax.ElementWalker;
import com.hp.hpl.jena.vocabulary.RDF;

import tr.edu.ege.seagent.wodqa.QueryElementOperations;
import tr.edu.ege.seagent.wodqa.exception.EmptyVoidModelException;
import tr.edu.ege.seagent.wodqa.exception.VOIDDescriptionConsistencyException;
import tr.edu.ege.seagent.wodqa.exception.WrongVOIDEntityConstructionException;
import tr.edu.ege.seagent.wodqa.voiddocument.VOIDOntologyVocabulary;

/**
 * INFO: BestAnalyze yonteminde "UNION" yok. AllAnalyze yonteminde var.
 * 
 * It provides decide mechanism to choose correct datasets for a query. This
 * class belongs agent who plays the decider role.
 * 
 */
public class QueryAnalyzer {

	/**
	 * This property is used by ASK caching mechanism to indicate that a dataset
	 * does not include a triple pattern.
	 */
	public static final Property CACHE_DOESNT_INCLUDE_PRP = ResourceFactory
			.createProperty("http://seagent.ege.edu.tr/wodqa#doesntInclude");

	/**
	 * This property is used by ASK caching mechanism to indicate that a dataset
	 * includes a triple pattern.
	 */
	public static final Property CACHE_INCLUDES_PRP = ResourceFactory
			.createProperty("http://seagent.ege.edu.tr/wodqa#includes");

	/**
	 * Void file path list bracket.
	 */
	private static final String ALL = "ALL";

	/**
	 * Indicates the position name of a node in a triple pattern. It is used by
	 * {@link #convertNode(Node, String)}.
	 */
	private static final String POSITION_OBJECT = "Object";

	/**
	 * Indicates the position name of a node in a triple pattern. It is used by
	 * {@link #convertNode(Node, String)}.
	 */
	private static final String POSITION_PREDICATE = "Predicate";

	/**
	 * Indicates the position name of a node in a triple pattern. It is used by
	 * {@link #convertNode(Node, String)}.
	 */
	private static final String POSITION_SUBJECT = "Subject";

	/**
	 * Flag which indicates that the {@link QueryAnalyzer} will use ASK in
	 * dataset selection.
	 */
	private boolean askOptimization = false;

	/**
	 * It includes FILTER blocks List of given Query.
	 */
	private List<Expr> filterBlockList = new Vector<Expr>();

	/**
	 * Logger instance.
	 */
	private Logger logger = Logger.getLogger(QueryAnalyzer.class);

	/**
	 * Model which contains VOID documents.
	 */
	private Model mainModel;

	/**
	 * Dataset list of the decider's analyzer. Every ontmodel includes
	 * dataset(s) descriptions. For example one ontmodel incl<!-- 		<dependency> -->
<!-- 			<groupId>Seagent</groupId> -->
<!-- 			<artifactId>void</artifactId> -->
<!-- 			<version>0.0.1-20120504</version> -->
<!-- 			<exclusions> -->
<!-- 				<exclusion> -->
<!-- 					<artifactId>arq</artifactId> -->
<!-- 					<groupId>com.hp.hpl.jena</groupId> -->
<!-- 				</exclusion> -->
<!-- 				<exclusion> -->
<!-- 					<artifactId>junit</artifactId> -->
<!-- 					<groupId>junit</groupId> -->
<!-- 				</exclusion> -->
<!-- 			</exclusions> -->
<!-- 		</dependency> -->udes one or more
	 * dataset individuals about dbpedia descriptions, another about geodata
	 * descriptions.
	 */
	private List<RelevantDatasetsForTriple> triplePackList = new Vector<RelevantDatasetsForTriple>();

	/**
	 * It includes triple List of given Query.
	 */
	private List<Triple> triplePatternList = new Vector<Triple>();

	/**
	 * It includes triple List of given Query.
	 */
	private List<Element> unionElementsList = new Vector<Element>();

	private List<VOIDPathSolution> voidPathSolutions = new Vector<VOIDPathSolution>();

	/**
	 * Creates an instance of {@link QueryAnalyzer} with a {@link Model} which
	 * represents the VOID store to distribute queries. This constructor creates
	 * an analyzer which uses ASK in dataset selection as default behavior.
	 * 
	 * @param mainModel
	 *            {@link Model} which represents the VOID store. It contains
	 *            VOID descriptions of a number of datasets.
	 */
	public QueryAnalyzer(Model mainModel) {
		this(mainModel, true);
	}

	/**
	 * Creates an instance of {@link QueryAnalyzer} with a {@link Model} which
	 * represents the VOID store and choice of using ASK queries in dataset
	 * selection.
	 * 
	 * @param mainModel
	 *            {@link Model} which represents the VOID store. It contains
	 *            VOID descriptions of a number of datasets.
	 * @param askOpt
	 *            choice of using ASK query to eliminate more datasets in
	 *            dataset selection. If this choice is <code>true</code>, query
	 *            analyzer sends ASK queries to relevant endpoints after
	 *            single-step analysis. It may increase the analysis time, but
	 *            it cahces the results of ASK queries to overcome this
	 *            situation.
	 */
	public QueryAnalyzer(Model mainModel, boolean askOpt) {
		this.mainModel = mainModel;
		this.askOptimization = askOpt;
//		logger.setLevel(Level.TRACE);
	}

	/**
	 * Analyze query and decide to all available Void documents
	 * 
	 * @param allQuery
	 *            simple query.
	 * @return
	 * @throws Exception
	 */
	public List<VOIDPathSolution> analyze(String allQuery) throws Exception {

		checkVoIDModel();

		// the DECIDE MECHANISM according to the void documents.
		List<Element> unionBlocks = getUnionBlocks(allQuery);
		if (unionBlocks.size() == 0) {
			findVoidsForSubQuery(allQuery);
		} else {
			findVoidsForUnionParts(unionBlocks);
		}

		return getVoidPathSolutions();
	}

	/**
	 * Walks on element and discovers triples in the given element.
	 * 
	 * @param queryPatternForTriple
	 * @param tripleVisitor
	 */
	public List<Triple> discoverTripleInQuery(Element queryPatternForTriple) {
		getTriplePatternList().clear();
		QueryConstructorUtil.setTriplesToGivenListByWalker(
				queryPatternForTriple, getTriplePatternList());
		return getTriplePatternList();
	}

	/**
	 * Gets the triple list from the query...
	 * 
	 * @param sparqlQuery
	 *            Query that its triples are found.
	 * @return {@link List} instance.
	 */
	public List<Triple> fillTriplePatternList(String sparqlQuery) {
		// clear triple list...
		getTriplePatternList().clear();
		// create query...
		Query query = QueryFactory.create(sparqlQuery);
		// get query pattern...
		Element queryPatternForTriple = query.getQueryPattern();
		// find the triple...
		discoverTripleInQuery(queryPatternForTriple);
		return getTriplePatternList();
	}

	/**
	 * Returns filter block to add query...
	 * 
	 * @param sparqlQuery
	 * @return
	 */
	public List<Expr> getFilterBlocks(String sparqlQuery) {
		getFilterBlockList().clear();
		// create query...
		Query query = QueryFactory.create(sparqlQuery);
		// get query pattern...
		Element queryPatternForFilterExpression = query.getQueryPattern();

		// create a visitor for visit filter blocks...
		ElementVisitorBase filterVisitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementFilter el) {
				Expr expr = el.getExpr();
				getFilterBlockList().add(expr);
				super.visit(el);
			}
		};
		ElementWalker.walk(queryPatternForFilterExpression, filterVisitor);
		return getFilterBlockList();
	}

	public void getServiceBlock(Element queryBlock,
			final List<Element> serviceTripleBlockList,
			final List<Node> serviceNodeList) {
		// create a visitor for visit filter blocks...
		ElementVisitorBase serviceVisitor = new ElementVisitorBase() {

			@Override
			public void visit(ElementService el) {
				serviceTripleBlockList.add(el.getElement());
				serviceNodeList.add(el.getServiceNode());
				super.visit(el);
			}
		};
		ElementWalker.walk(queryBlock, serviceVisitor);
	}

	public List<RelevantDatasetsForTriple> getTriplePackList() {
		return triplePackList;
	}

	/**
	 * @return the triplePathList
	 */
	public List<Triple> getTriplePatternList() {
		return triplePatternList;
	}

	/**
	 * Returns filter block to add query...
	 * 
	 * @param sparqlQuery
	 * @return
	 */
	public List<Element> getUnionBlocks(String sparqlQuery) {
		getUnionElementsList().clear();
		QueryConstructorUtil.setUnionBlockElementsToGivenList(sparqlQuery,
				getUnionElementsList());
		return getUnionElementsList();
	}

	public List<VOIDPathSolution> getVoidPathSolutions() {
		return voidPathSolutions;
	}

	public void setExplicitServiceURIs(Element queryBlock) {
		final List<Element> serviceTripleBlockList = new Vector<Element>();
		final List<Node> serviceNodeList = new Vector<Node>();

		// fills service graph block elements into the given parameters...
		getServiceBlock(queryBlock, serviceTripleBlockList, serviceNodeList);
		// IF there is a SERVICE keyword in the initial query, find the service
		// URLs.
		if (serviceNodeList.size() > 0) {
			List<String> endpoints = getServiceEndpointList(serviceNodeList,
					queryBlock);
			// set the specified endpoints for the triples that are in the
			// service graph pattern in the initial query.
			for (int i = 0; i < serviceTripleBlockList.size(); i++) {
				List<Triple> triplesInServiceBlock = getTriplesFromElement(serviceTripleBlockList
						.get(i));
				// set endpoints of the specified triples...
				for (Triple triple : triplesInServiceBlock) {
					int updatedIndex = getTriplePatternList().indexOf(triple);
					getVoidPathSolutions().remove(updatedIndex);
					VOIDPathSolution vps = new VOIDPathSolution();
					for (String endp : endpoints) {
						vps.addEndpoint(endp);
					}
					getVoidPathSolutions().add(updatedIndex, vps);
				}
			}
		}
	}

	public void setVoidPathSolutions(List<VOIDPathSolution> voidPathSolutions) {
		this.voidPathSolutions = voidPathSolutions;
	}

	/**
	 * Adds the bracket between the each triple's void file paths.
	 */
	private void addBracketToVoidPathList() {
		// getVoidPathList().add(BRACKET);
		VOIDPathSolution voidPathSolution = new VOIDPathSolution();
		voidPathSolution.addEndpoint(ALL);
		getVoidPathSolutions().add(voidPathSolution);
	}

	/**
	 * Adds endpoint URL to the void path list.
	 * 
	 * @param index
	 * @param endpointURL
	 */
	private void addEndpointURLToVOIDPathList(int index, String endpointURL,
			RelevantType relevantType) {
		boolean relevantState = true;
		if (relevantType.equals(RelevantType.INTERNAL))
			relevantState = false;
		if (getVoidPathSolutions().size() > index) {
			getVoidPathSolutions().get(index).addEndpoint(endpointURL,
					relevantState);
		} else {
			VOIDPathSolution voidPathSolution = new VOIDPathSolution();
			voidPathSolution.addEndpoint(endpointURL, relevantState);
			getVoidPathSolutions().add(voidPathSolution);
		}
	}

	/**
	 * Triple is not contained in the model and this method add this information
	 * to the model.
	 * 
	 * @param rdft
	 * @param dataset
	 */
	private void addNegativeContainmentSituation(
			RelevantDatasetsForTriple rdft, Resource dataset) {
		Triple convertedTriple = convertTripleToGeneric(rdft.getTriple());
		Literal literalTriple = ResourceFactory
				.createPlainLiteral(convertedTriple.toString());
		mainModel.add(dataset, CACHE_DOESNT_INCLUDE_PRP, literalTriple);
		// logger.debug(MessageFormat
		// .format("\"{0}\" triple doesn't exist in \"{1}\" dataset and added to the main model as \"{2}\" format.",
		// rdft.getTriple(), dataset, literalTriple));
	}

	/**
	 * Triple is contained in the model and this method add this information to
	 * the model
	 * 
	 * @param rdft
	 * @param dataset
	 */
	private void addPositiveContainmentSituation(
			RelevantDatasetsForTriple rdft, Resource dataset) {
		Triple convertedTriple = convertTripleToGeneric(rdft.getTriple());
		Literal literalTriple = ResourceFactory
				.createPlainLiteral(convertedTriple.toString());
		mainModel.add(dataset, CACHE_INCLUDES_PRP, literalTriple);
		// logger.debug(MessageFormat
		// .format("\"{0}\" triple is exist in \"{1}\" dataset and added to the main model as \"{2}\" format.",
		// rdft.getTriple(), dataset, literalTriple));
	}

	/**
	 * It checks that any change occurs in repetitive step rules.
	 * 
	 * @param relevantDatasetsSizeBefore
	 * @param relevantDatasetsSizeAfter
	 * @return
	 */
	private int anyChangesInRepetitive(
			List<Integer> relevantDatasetsSizeBefore,
			List<Integer> relevantDatasetsSizeAfter) {
		for (int i = 0; i < relevantDatasetsSizeAfter.size(); i++) {
			if (!relevantDatasetsSizeBefore.get(i).equals(
					relevantDatasetsSizeAfter.get(i)))
				return i;
		}
		return -1;
	}

	/**
	 * This method checks that whether all nodes of a given {@link Triple} are
	 * variables.
	 * 
	 * @param triple
	 * @return
	 */
	private boolean areAllNodesVariables(Triple triple) {
		return triple.getSubject().isVariable()
				&& triple.getPredicate().isVariable()
				&& triple.getObject().isVariable();
	}

	/**
	 * This method asks {@link Triple} contained in
	 * {@link RelevantDatasetsForTriple} instance to the all related datasets
	 * contained in this {@link RelevantDatasetsForTriple}.
	 * 
	 * @param rdft
	 *            {@link RelevantDatasetsForTriple} instance that contains
	 *            {@link Triple} and all related datasets of this triple
	 * @throws WrongVOIDEntityConstructionException
	 * @throws Exception
	 */
	private void askTripleToAllRelatedEndpoints(RelevantDatasetsForTriple rdft)
			throws WrongVOIDEntityConstructionException, Exception {
		// ask triple to relevant datasets
		for (int i = 0; i < rdft.getCurrentRelevantDatasets().size(); i++) {
			Resource dataset = rdft.getCurrentRelevantDatasets().get(i);
			String endpointURL = getSparqlEndpoint(dataset, mainModel);
			if (!isVirtualDataset(endpointURL)
					&& !areAllNodesVariables(rdft.getTriple())) {
				if (doesDatasetContainTriple(dataset, rdft.getTriple())) {
					logger.debug(MessageFormat
							.format("Triple \"{0}\" is contained in model, and won't be asked to the service",
									rdft.getTriple()));
				} else if (doesDatasetNotContainTriple(dataset,
						rdft.getTriple())) {
					rdft.getCurrentRelevantDatasets().set(i,
							ResourceFactory.createResource());
					rdft.getCurrentRelevantTypes().set(i, RelevantType.EMPTY);
					logger.debug(MessageFormat
							.format("Triple \"{0}\" is doesn't contained in model, and won't be asked to the service",
									rdft.getTriple()));
				} else {
					// if there is no information about the triple
					// and the dataset in the ASK cache.
					askTripleToEndpoint(rdft, i, dataset, endpointURL);
				}
			}
		}
	}

	/**
	 * This method asks {@link Triple} of given
	 * {@link RelevantDatasetsForTriple} to given endpoint, and adds positive
	 * containment statement to the ASK cache if result is true, or adds
	 * negative one if result is negative.
	 * 
	 * @param rdft
	 *            {@link RelevantDatasetsForTriple} instance that contains
	 *            triple and its related datasets and types
	 * @param index
	 *            index of endpoint
	 * @param askCacheDataset
	 *            dataset that contains ASK cache results
	 * @param endpointURL
	 *            endpoint URL, that triple will be asked to.
	 */
	private void askTripleToEndpoint(RelevantDatasetsForTriple rdft, int index,
			Resource askCacheDataset, String endpointURL) {
		long beforeAsk = System.currentTimeMillis();
		// ask triple to service
		boolean isExist = askTripleToService(
				QueryElementOperations.convertTripleToString(rdft.getTriple()),
				endpointURL);
		long afterAsk = System.currentTimeMillis();
		logger.debug(MessageFormat.format(
				"Asking to \"{0}\" service longed \"{1}\" seconds",
				endpointURL, afterAsk - beforeAsk));

		// remove dataset that doesn't include triple
		// pattern.
		if (!isExist) {
			rdft.getCurrentRelevantDatasets().set(index,
					ResourceFactory.createResource());
			rdft.getCurrentRelevantTypes().set(index, RelevantType.EMPTY);

			addNegativeContainmentSituation(rdft, askCacheDataset);
		} else {
			addPositiveContainmentSituation(rdft, askCacheDataset);
		}
	}

	/**
	 * It queries the given triple to the given service.
	 * 
	 * @param triple
	 * @param service
	 * @return
	 * 
	 */
	private boolean askTripleToService(String triple, String service) {
		Query askQuery = QueryFactory.create("ASK {" + triple + "}");
		QueryExecution execution;
		boolean isTripleExist;
		execution = QueryExecutionFactory.sparqlService(service, askQuery);
		try {
			isTripleExist = execution.execAsk();
		} catch (Exception e) {
			// If there is an exception, assume that triple is not in the
			// service
			execution.close();
			return false;
		}
		execution.close();
		return isTripleExist;
	}

	/**
	 * It fills the current relevant dataset by all voids of triple patterns
	 * that can not be eliminated in single step analysis.
	 * 
	 * @throws VOIDDescriptionConsistencyException
	 * 
	 */
	private void assignAllDataset() throws VOIDDescriptionConsistencyException {

		List<Resource> allDatasets = new Vector<Resource>();
		List<RelevantType> allRelevantTypes = new Vector<RelevantType>();
		// get the dataset which has a sparql endpoint in the VOID model.
		List<Resource> datasetsInVOID = mainModel.listSubjectsWithProperty(
				VOIDOntologyVocabulary.DATASET_sparqlEndpoint_prp,
				(RDFNode) null).toList();
		for (int i = 0; i < datasetsInVOID.size(); i++) {
			allRelevantTypes.add(RelevantType.INTERNAL);
		}
		allDatasets.addAll(datasetsInVOID);
		for (RelevantDatasetsForTriple triplePack : triplePackList) {
			if (triplePack.isAllRelated()) {
				triplePack.setNewRelevantDatasets(allDatasets);
				triplePack.setNewRelevantTypes(allRelevantTypes);
				triplePack.eliminateWithNewFoundDatasets();
			}
		}
	}

	private void checkVoIDModel() throws EmptyVoidModelException {
		if (!mainModel.contains(null, RDF.type,
				VOIDOntologyVocabulary.DATASET_rsc)) {
			throw new EmptyVoidModelException(
					"Model that contains VoID descriptions is empty to be analyzed");
		}
	}

	/**
	 * It checks whether given {@link Node} is variable and if so, converts it
	 * to a generic named variable.
	 * 
	 * @param node
	 *            node to change its name if required.
	 * @param position
	 *            Position of the node in the triple pattern. This word is added
	 *            at the end of generic variable name.
	 * @return
	 */
	private Node convertNode(Node node, String position) {
		if (node.isVariable()) {
			return Node.createVariable("var" + position);
		}
		return node;
	}

	/**
	 * It converts the given {@link Triple} into a generic form. Generic triple
	 * form is needed to check the ASK cache. Because, different variable names
	 * can be included by two overlapping triples. In the generic form, all
	 * variable names are converted to "var".
	 * 
	 * @param triple
	 *            the triple to create the generic form.
	 * @return generic triple for the given triple.
	 */
	private Triple convertTripleToGeneric(Triple triple) {
		// convert subject, predicate and object nodes of triple
		Node newSubject = convertNode(triple.getSubject(), POSITION_SUBJECT);
		Node newPredicate = convertNode(triple.getPredicate(),
				POSITION_PREDICATE);
		Node newObject = convertNode(triple.getObject(), POSITION_OBJECT);
		return Triple.create(newSubject, newPredicate, newObject);
	}

	/**
	 * This method searches the ASK cache for {@link #mainModel} an information
	 * about that the given dataset contains the given {@link Triple}.
	 * 
	 * @param dataset
	 *            dataset which is asked about containing the given triple.
	 * @param triple
	 *            triple which is asked about contained by the given dataset.
	 * @return
	 */
	private boolean doesDatasetContainTriple(Resource dataset, Triple triple) {
		Triple convertedTriple = convertTripleToGeneric(triple);
		Literal tripleLiteral = ResourceFactory
				.createPlainLiteral(convertedTriple.toString());
		return mainModel.contains(dataset, CACHE_INCLUDES_PRP, tripleLiteral);
	}

	/**
	 * This method searches the ASK cache for {@link #mainModel} an information
	 * about that the given dataset doesn't containF the given {@link Triple}.
	 * 
	 * @param dataset
	 *            dataset which is asked about containing the given triple.
	 * @param triple
	 *            triple which is asked about contained by the given dataset.
	 * @return
	 */
	private boolean doesDatasetNotContainTriple(Resource dataset, Triple triple) {
		Triple convertedTriple = convertTripleToGeneric(triple);
		Literal tripleLiteral = ResourceFactory
				.createPlainLiteral(convertedTriple.toString());
		return mainModel.contains(dataset, CACHE_DOESNT_INCLUDE_PRP,
				tripleLiteral);
	}

	/**
	 * It executes ASK queries for the triple patterns that are eliminated by
	 * single step rules.
	 * 
	 * @throws Exception
	 */
	private void executeAskQueries() throws Exception {
		// int askCount = 0;
		if (this.askOptimization) {
			logger.info("Executing ASK queries...");
			long before = System.currentTimeMillis();
			for (int index = 0; index < triplePackList.size(); index++) {
				RelevantDatasetsForTriple rdft = triplePackList.get(index);
				if (!rdft.isAllRelated()) {
					logger.debug(MessageFormat
							.format("Triple pattern\"{0}\" is asking to the services... ",
									rdft.getTriple()));
					// ask triple to all relevant datasets
					askTripleToAllRelatedEndpoints(rdft);
					// System.out.println();
					if (isAllTypesEmpty(rdft)) {
						voidPathSolutions.set(index, new VOIDPathSolution());
					}
				}
			}
			// System.out.println("ASK Count: " + askCount);
			long after = System.currentTimeMillis();
			logger.info(MessageFormat.format(
					"Ask queries executed in \"{0}\" miliseconds", after
							- before));
			printRelevantDatasetCount("ASK Sorgularindan Sonra:");
		}

	}

	/**
	 * Executes the repetitive discovery rules to eliminate each possible
	 * dataset for triple patterns.
	 * 
	 * @throws VOIDDescriptionConsistencyException
	 */
	private void executeRepetitiveAnalysis(RuleExecutor executor)
			throws VOIDDescriptionConsistencyException {
		logger.info("Executing repetitive step analysis...");
		long beforeRepetitiveStep = System.currentTimeMillis();
		List<Integer> relevantDatasetsSizeBefore;
		List<Integer> relevantDatasetsSizeAfter;
		for (int i = 0; i < triplePackList.size(); i++) {
			relevantDatasetsSizeBefore = getRelevantSizeOfTriples();
			for (int j = 0; j < triplePackList.size(); j++) {
				if (i == j)
					continue;
				else {
					executor.executeRepetitiveRules(triplePackList.get(i),
							triplePackList.get(j));
				}
			}
			relevantDatasetsSizeAfter = getRelevantSizeOfTriples();
			// check is there any elimination between size of relevant datasets
			// after elimination.
			int firstChangedTripleIndex = anyChangesInRepetitive(
					relevantDatasetsSizeBefore, relevantDatasetsSizeAfter);
			if (firstChangedTripleIndex > -1)
				i = firstChangedTripleIndex - 1;
		}
		long afterRepetitiveStep = System.currentTimeMillis();
		logger.info(MessageFormat
				.format("Repetitive step analysis has been executed in \"{0}\" miliseconds...",
						afterRepetitiveStep - beforeRepetitiveStep));
	}

	/**
	 * It fills endpoints of datasets in the void path list uniquely.
	 * 
	 * @throws Exception
	 */
	private void fillEndpointsOfDatasets() throws Exception {
		getVoidPathSolutions().clear();
		for (int i = 0; i < triplePackList.size(); i++) {
			RelevantDatasetsForTriple triplePack = triplePackList.get(i);
			for (int j = 0; j < triplePack.getCurrentRelevantDatasets().size(); j++) {
				// if (!triplePack.getCurrentRelevantTypes().get(j)
				// .equals(RelevantType.EMPTY)) {
				Resource dataset = triplePack.getCurrentRelevantDatasets().get(
						j);
				String endpointURL = getSparqlEndpoint(dataset, mainModel);
				isVirtualDataset(endpointURL);
				addEndpointURLToVOIDPathList(i, endpointURL, triplePack
						.getCurrentRelevantTypes().get(j));
				// }
			}
		}

	}

	/**
	 * Finds Voids for the given subquery
	 * 
	 * @param subQuery
	 * @throws Exception
	 */
	private void findVoidsForSubQuery(String subQuery) throws Exception {
		triplePackList.clear();
		// fill triple pattern list...
		fillTriplePatternList(subQuery);

		RuleExecutor executor = new RuleExecutor(mainModel);
		// execute single step analysis for each triple pattern...
		logger.info("Executing single step analysis...");
		long beforeSingleStep = System.currentTimeMillis();
		for (int i = 0; i < getTriplePatternList().size(); i++) {
			RelevantDatasetsForTriple triplePack = new RelevantDatasetsForTriple(
					getTriplePatternList().get(i));
			triplePackList.add(triplePack);
			executor.executeSingleStepRules(triplePack);
		}
		long afterSingleStep = System.currentTimeMillis();
		logger.info(MessageFormat
				.format("Single step analysis has been executed in \"{0}\" miliseconds...",
						afterSingleStep - beforeSingleStep));

		// fill the void path list...
		showAllAvailableDatasetList();

		if (logger.isDebugEnabled()) {
			ArrayList<String> endpointListsSingleStep = generateEndpointList();
			logger.debug("Current queried datasets after single step analysis: "
					+ endpointListsSingleStep);
		}

		// print eliminated dataset count
		printRelevantDatasetCount("Tek Adim Analizden Sonra");

		// execute ASK queries for triple patterns that were eliminated by
		// single step rules.
		executeAskQueries();

		// set all voids to triple patterns that have not been eliminated any
		// single step rule.
		assignAllDataset();

		// execute complex rules
		executeRepetitiveAnalysis(executor);

		// print relevant dataset count
		printRelevantDatasetCount("Yinelemeli Analizden Sonra:");

		if (logger.isInfoEnabled()) {
			ArrayList<String> endpointListsRepetitiveStep = generateEndpointList();
			logger.info("Current queried datasets after repetitive step analysis: "
					+ endpointListsRepetitiveStep);
		}

		// fill the void path list with endpoints of datasets...
		fillEndpointsOfDatasets();

		// set explicit service URIs
		setExplicitServiceURIs(QueryFactory.create(subQuery).getQueryPattern());
	}

	/**
	 * FIXME bu metot {@link #findVoidsForSubQuery(String)} metoduna benziyor.
	 * Yeniden yapilandirma gerekli.
	 * 
	 * @param unionBlocks
	 * @throws Exception
	 */
	private void findVoidsForUnionParts(List<Element> unionBlocks)
			throws Exception {
		List<VOIDPathSolution> tempVoidList = new Vector<VOIDPathSolution>();
		RuleExecutor executor = new RuleExecutor(mainModel);
		for (int i = 0; i < unionBlocks.size(); i++) {
			// reset lists..
			getVoidPathSolutions().clear();
			triplePackList.clear();
			// fill triples in subquery...
			List<Triple> triplesInFirstBlock = discoverTripleInQuery(unionBlocks
					.get(i));
			// examine proper void fileS for each triple pattern...
			logger.info("Executing single step analysis...");
			long beforeSingleStep = System.currentTimeMillis();
			for (int x = 0; x < triplesInFirstBlock.size(); x++) {
				triplePackList.add(new RelevantDatasetsForTriple(
						getTriplePatternList().get(x)));
				executor.executeSingleStepRules(triplePackList.get(x));
			}
			long afterSingleStep = System.currentTimeMillis();
			logger.info(MessageFormat
					.format("Single step analysis has been executed in \"{0}\" miliseconds...",
							afterSingleStep - beforeSingleStep));

			// print relevant dataset count
			// printRelevantDatasetCount("Tek Adim Analizden Sonra:");

			// fill the void path list...
			showAllAvailableDatasetList();

			// ArrayList<String> endpointListsSingleStep =
			// generateEndpointList();
			// logger.info("Current queried datasets after single step analysis: "
			// + endpointListsSingleStep);

			// execute ASK queries...
			executeAskQueries();

			// set all voids to triple patterns that have not been eliminated
			// any single step rule.
			assignAllDataset();
			// execute complex rules
			executeRepetitiveAnalysis(executor);

			// print relevant dataset count
			// printRelevantDatasetCount("Yinelemeli Analizden Sonra:");

			// fill the void path list with endpoints of datasets...
			fillEndpointsOfDatasets();
			// set explicit service triple patterns
			setExplicitServiceURIs(unionBlocks.get(i));
			tempVoidList.addAll(getVoidPathSolutions());
		}
		getVoidPathSolutions().clear();
		setVoidPathSolutions(tempVoidList);
	}

	/**
	 * generate endpoint list to log
	 * 
	 * @return
	 */
	private ArrayList<String> generateEndpointList() {
		ArrayList<String> endpointLists = new ArrayList<String>();
		for (VOIDPathSolution voidPathSolution : getVoidPathSolutions()) {
			endpointLists.add(voidPathSolution.getAllEndpoints().toString());
		}
		return endpointLists;
	}

	/**
	 * @return the triplePathList
	 */
	private List<Expr> getFilterBlockList() {
		return filterBlockList;
	}

	/**
	 * It returns the relevant dataset size related with each triple pattern.
	 * 
	 * @return
	 */
	private List<Integer> getRelevantSizeOfTriples() {
		List<Integer> relevantDatasetsSize = new Vector<Integer>();
		for (RelevantDatasetsForTriple triplePack : triplePackList) {
			relevantDatasetsSize.add(triplePack.getCurrentRelevantDatasets()
					.size());
		}
		return relevantDatasetsSize;
	}

	/**
	 * It founds service URI(s) that is in the given query element. If there is
	 * a SERVICE keyword in the given query, mentioned endpoints are used for
	 * querying.
	 * 
	 * @param serviceNodeList
	 * @param queryElement
	 * @return
	 */
	private List<String> getServiceEndpointList(
			final List<Node> serviceNodeList, Element queryElement) {
		List<String> endpoints = new Vector<String>();
		// all service nodes and service triples are found.
		for (int i = 0; i < serviceNodeList.size(); i++) {
			final Node node = serviceNodeList.get(i);
			if (node.isVariable()) {
				final List<Expr> exprList = new Vector<Expr>();
				// create a visitor for visit BIND blocks...
				ElementVisitorBase bindVisitor = new ElementVisitorBase() {
					@Override
					public void visit(ElementBind el) {
						if (el.getVar().toString().equals(node.toString()))
							exprList.add(el.getExpr());
						super.visit(el);
					}
				};
				ElementWalker.walk(queryElement, bindVisitor);
				String s = "";
				for (Expr expr : exprList) {
					String endp = expr.toString();
					if (s == "")
						s += endp.substring(1, endp.length() - 1);
					else
						s += "**" + endp.substring(1, endp.length() - 1);
				}
				endpoints.add(s);

			} else
				endpoints.add(node.getURI());
		}
		return endpoints;
	}

	/**
	 * Retrieves the SPARQL endpoint of the given dataset. This method uses
	 * {@link VOIDOntologyVocabulary#DATASET_sparqlEndpoint} property.
	 * 
	 * @param dataset
	 *            dataset to retrieve its sparql endpoint URL.
	 * @param mainModel
	 * @return sparql endpoint URL.
	 * @throws WrongVOIDEntityConstructionException
	 */
	private String getSparqlEndpoint(Resource dataset, Model mainModel)
			throws WrongVOIDEntityConstructionException {
		if (mainModel == null || mainModel.isEmpty()) {
			return "";
		}
		List<Statement> endpointStmts = mainModel.listStatements(dataset,
				VOIDOntologyVocabulary.DATASET_sparqlEndpoint_prp,
				(RDFNode) null).toList();
		if (endpointStmts.isEmpty()) {
			return null;
		} else {
			RDFNode endpointValue = endpointStmts.get(0).getObject();
			if (endpointValue.isLiteral()) {
				return endpointValue.asLiteral().getString();
			} else {
				throw new WrongVOIDEntityConstructionException(
						MessageFormat
								.format("\"{0}\" is not a Literal! Sparql endpoint must be Literal in VOID documents",
										endpointValue));
			}
		}
	}

	private List<Triple> getTriplesFromElement(Element serviceTripleBlock) {
		final List<Triple> subTripleList = new Vector<Triple>();
		ElementVisitorBase tripleVisitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementPathBlock el) {
				Iterator<TriplePath> iterator = el.getPattern().iterator();
				while (iterator.hasNext()) {
					TriplePath triplePath = iterator.next();
					subTripleList.add(triplePath.asTriple());
				}
				super.visit(el);
			}
		};
		ElementWalker.walk(serviceTripleBlock, tripleVisitor);
		return subTripleList;
	}

	/**
	 * @return the unionElementsList
	 */
	private List<Element> getUnionElementsList() {
		return unionElementsList;
	}

	private boolean isAllTypesEmpty(RelevantDatasetsForTriple rdft) {
		for (int i = 0; i < rdft.getCurrentRelevantTypes().size(); i++) {
			if (!rdft.getCurrentRelevantTypes().get(i)
					.equals(RelevantType.EMPTY)) {
				return false;
			}
		}
		return true;
	}

	private boolean isVirtualDataset(String endpointURL) throws Exception {
		return endpointURL == null;
	}

	private void printRelevantDatasetCount(String anlayzePhaseInfo) {
		if (!logger.isTraceEnabled()) {
			return;
		}
		logger.trace(anlayzePhaseInfo);
		for (int i = 0; i < triplePackList.size(); i++) {
			RelevantDatasetsForTriple triplePack = triplePackList.get(i);
			String relevantCount = "";
			if (triplePack.isAllRelated()) {
				relevantCount = "[ALL]";
			} else {
				relevantCount = Integer.toString(triplePack
						.getCurrentRelevantDatasets().size());
			}
			logger.trace(MessageFormat.format(
					"Triple pattern {0}: {1} relevant datasets.", i + 1,
					relevantCount));
		}
	}

	/**
	 * Fills found datasets to the void path list in single step analysis.
	 */
	private void showAllAvailableDatasetList() {
		getVoidPathSolutions().clear();
		int index = 0;
		for (RelevantDatasetsForTriple triplePack : triplePackList) {
			int datasetIndex = 0;
			for (Resource dataset : triplePack.getCurrentRelevantDatasets()) {
				RelevantType relevantType = triplePack
						.getCurrentRelevantTypes().get(datasetIndex);
				addEndpointURLToVOIDPathList(index, dataset.getURI(),
						relevantType);
			}
			if (triplePack.getCurrentRelevantDatasets() == null
					|| triplePack.getCurrentRelevantDatasets().size() <= 0)
				addBracketToVoidPathList();
			index++;

		}
	}
}
