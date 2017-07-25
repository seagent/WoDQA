package tr.edu.ege.seagent.wodqa.query;

import java.util.List;
import java.util.Vector;

import query.UndefinedQueryTypeException;
import query.UnsupportedNodeTypeException;

import tr.edu.ege.seagent.boundarq.filterbound.QueryEngineFilter;
import tr.edu.ege.seagent.dataset.crawler.VoidCrawler;
import tr.edu.ege.seagent.wodqa.exception.InactiveEndpointException;
import tr.edu.ege.seagent.wodqa.exception.QueryAnalyzerException;
import tr.edu.ege.seagent.wodqa.exception.QueryHeaderException;
import tr.edu.ege.seagent.wodqa.query.analyzer.QueryAnalyzer;
import tr.edu.ege.seagent.wodqa.query.analyzer.VOIDPathSolution;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.DatasetImpl;
import com.hp.hpl.jena.sparql.engine.QueryEngineBase;

import core.Querier;

public class WodqaEngine {

	private String federatedQuery;
	private Querier querier;
	private boolean isCacheActive;

	public boolean isCacheActive() {
		return isCacheActive;
	}

	public WodqaEngine() {
		super();
		QueryEngineFilter.register();
		isCacheActive = false;
	}

	public WodqaEngine(boolean nestedLoop) {
		super();
		if (!nestedLoop) {
			QueryEngineFilter.register();
		}
		isCacheActive = false;
	}

	public WodqaEngine(boolean nestedLoop, boolean isCacheActive) {
		super();
		if (!nestedLoop) {
			QueryEngineFilter.register();
		}
		this.isCacheActive = isCacheActive;
		if (isCacheActive) {
			activateCache();
		}
	}

	private void activateCache() {
		querier = new Querier();
	}

	// private void deactivateCache() {
	// querier = null;
	// }

	public ResultSet executeSelect(String federatedQuery)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		DatasetImpl dataset = new DatasetImpl(ModelFactory.createDefaultModel());
		if (isCacheActive) {
			return querier.select(federatedQuery, dataset);
		}
		return QueryExecutionFactory.create(federatedQuery, dataset)
				.execSelect();
	}

	public Model executeConstruct(String federatedQuery)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		DatasetImpl dataset = new DatasetImpl(ModelFactory.createDefaultModel());
		if (isCacheActive) {
			return querier.construct(federatedQuery, dataset);
		}
		return QueryExecutionFactory.create(federatedQuery, dataset)
				.execConstruct();
	}

	public Model executeDescribe(String federatedQuery)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		DatasetImpl dataset = new DatasetImpl(ModelFactory.createDefaultModel());
		if (isCacheActive) {
			return querier.describe(federatedQuery, dataset);
		}
		return QueryExecutionFactory.create(federatedQuery, dataset)
				.execDescribe();
	}

	public boolean executeAsk(String federatedQuery)
			throws UndefinedQueryTypeException, UnsupportedNodeTypeException {
		DatasetImpl dataset = new DatasetImpl(ModelFactory.createDefaultModel());
		if (isCacheActive) {
			return querier.ask(federatedQuery, dataset);
		}
		return QueryExecutionFactory.create(federatedQuery, dataset).execAsk();
	}

	public ResultSet select(Model mainModel, String simpleQuery, boolean askOpt)
			throws QueryHeaderException, InactiveEndpointException, Exception {
		String federatedQuery = federateQuery(mainModel, simpleQuery, askOpt);

		return executeSelect(federatedQuery);
	}

	public Model construct(Model mainModel, String simpleQuery, boolean askOpt)
			throws QueryHeaderException, InactiveEndpointException, Exception {
		String federatedQuery = federateQuery(mainModel, simpleQuery, askOpt);
		return executeConstruct(federatedQuery);
	}

	public Model describe(Model mainModel, String simpleQuery, boolean askOpt)
			throws QueryHeaderException, InactiveEndpointException, Exception {
		String federatedQuery = federateQuery(mainModel, simpleQuery, askOpt);
		return executeDescribe(federatedQuery);
	}

	public boolean ask(Model mainModel, String simpleQuery, boolean askOpt)
			throws QueryHeaderException, InactiveEndpointException, Exception {
		String federatedQuery = federateQuery(mainModel, simpleQuery, askOpt);
		return executeAsk(federatedQuery);
	}

	/**
	 * This method anayzes the query and the given datasets. Then, it constructs
	 * a query which includes SERVICE expressions for appropriate datasets, and
	 * creates a {@link QueryExecution} for the reorganized query.
	 * 
	 * @param mainModel
	 * @param simpleQuery
	 *            raw query to handle.
	 * @param askOpt
	 * @return {@link QueryExecution} for the query which contains SERVICE
	 *         expressions with appropriate datasets.
	 * @throws Exception
	 */
	public QueryExecution prepareToExecute(Model mainModel, String simpleQuery,
			boolean askOpt) throws Exception {
		String federatedQuery = federateQuery(mainModel, simpleQuery, askOpt);
		QueryExecution exec = QueryExecutionFactory.create(federatedQuery,
				ModelFactory.createDefaultModel());
		return exec;
	}

	/**
	 * Execute given construct query.
	 * 
	 * @param federatedQuery
	 * @return
	 * @throws QueryHeaderException
	 * @Deprecated It must be moved to WODQA API.
	 */
	@Deprecated
	public QueryResult executeGenericQuery(String federatedQuery)
			throws QueryHeaderException {
		QueryResult qr = new QueryResult();
		QueryExecution exec1 = QueryExecutionFactory.create(federatedQuery,
				ModelFactory.createDefaultModel());
		// get query execution
		QueryExecution exec = exec1;
		qr.setExec(exec);
		// check query type...
		int querytype = checkQueryType(federatedQuery,
				QueryHeaderException.CONSTRUCT_TYPE_ERROR_MESSAGE);
		if (querytype == Query.QueryTypeConstruct) {
			qr.setConstructedModel(executeConstructQuery(exec));
			qr.setQueryType(Query.QueryTypeConstruct);
		} else {
			qr.setSelectedSet(executeSelectQuery(exec));
			qr.setQueryType(Query.QueryTypeSelect);
		}
		return qr;
	}

	/**
	 * It executes query on linked data endpoints, it doesn't distribute query
	 * to the agent endpoints.
	 * 
	 * @param mainModel
	 *            TODO
	 * @param simpleQuery
	 *            simple query without dataset endpoints.
	 * @throws Exception
	 * @Deprecated It must be moved to WoDQA API. Use
	 *             {@link #reorganizeQueryAndCreateExecution(Model, String)}
	 *             method.
	 */
	@Deprecated
	public QueryResult executeQuery(Model mainModel, String simpleQuery)
			throws Exception {
		String federatedQuery = justReorganizeQuery(mainModel, simpleQuery,
				false);
		return executeGenericQuery(federatedQuery);
	}

	public String getFederatedQuery() {
		return federatedQuery;
	}

	/**
	 * @param mainModel
	 *            TODO
	 * @param simpleQuery
	 * @param askOpt
	 * @return
	 * @throws Exception
	 * @throws QueryHeaderException
	 * @throws InactiveEndpointException
	 * 
	 * @Deprecated Use {@link #federateQuery(List, String, boolean)} instead.
	 */
	@Deprecated
	public String justReorganizeQuery(Model mainModel, String simpleQuery,
			boolean askOpt) throws Exception, QueryHeaderException,
			InactiveEndpointException {
		return federateQuery(mainModel, simpleQuery, askOpt);
	}

	/**
	 * This method analyzes the query and reorganizes it according to analysis
	 * result.
	 * 
	 * @param mainModel
	 * @param simpleQuery
	 *            raw query.
	 * @param askOpt
	 *            option of the user which indicates whether ASK queries will be
	 *            used during selection or not.
	 * 
	 * @return Reorganized query string which contains appropriate SERVICE
	 *         expressions.
	 * @throws Exception
	 * @throws QueryHeaderException
	 * @throws InactiveEndpointException
	 */
	public String federateQuery(Model mainModel, String simpleQuery,
			boolean askOpt) throws Exception, QueryHeaderException,
			InactiveEndpointException {
		List<VOIDPathSolution> analyzedEndpointMatrice = new Vector<VOIDPathSolution>();
		try {
			analyzedEndpointMatrice = new QueryAnalyzer(mainModel, askOpt)
					.analyze(simpleQuery);
		} catch (QueryAnalyzerException e) {
			// TODO bu istisna ne bicim isleniyor. Bu incelenmeli.
			e.printStackTrace();
		}
		QueryReorganizer queryReorganizer = new QueryReorganizer();
		String federatedQuery = queryReorganizer.reorganizeWholeQuery(
				analyzedEndpointMatrice, simpleQuery);
		setFederatedQuery(federatedQuery);
		return federatedQuery;
	}

	/**
	 * @param mainModel
	 * @param simpleQuery
	 * @return
	 * @throws Exception
	 * 
	 * @Deprecated Use {@link #prepareToExecute(List, boolean, boolean)}
	 *             instead.
	 */
	@Deprecated
	public QueryExecution reorganizeQueryAndCreateExecution(Model mainModel,
			String simpleQuery) throws Exception {
		return prepareToExecute(mainModel, simpleQuery, true);
	}

	public void setFederatedQuery(String federatedQuery) {
		this.federatedQuery = federatedQuery;
	}

	/**
	 * Execute the given construct query.
	 * 
	 * @return
	 * @Deprecated It must be moved to WODQA API.
	 */
	@Deprecated
	protected OntModel executeConstructQuery(QueryExecution exec) {
		OntModel resultModel = null;
		// create construct query...
		Model model = exec.execConstruct();
		// create new model...
		resultModel = VoidCrawler.createQueryModel(model,
				"http://newQueryResultSet");
		return resultModel;
	}

	/**
	 * Execute given select query.
	 * 
	 * @return
	 * @throws QueryHeaderException
	 * @Deprecated It must be moved to WODQA API.
	 */
	@Deprecated
	protected ResultSet executeSelectQuery(QueryExecution exec)
			throws QueryHeaderException {
		return exec.execSelect();
	}

	/**
	 * It checks the query type.
	 * 
	 * @param federatedQuery
	 * @throws QueryHeaderException
	 * @Deprecated It must be moved to WODQA API.
	 */
	@Deprecated
	private int checkQueryType(String federatedQuery, String errorMessage)
			throws QueryHeaderException {
		Query query = QueryFactory.create(federatedQuery);
		if (query.getQueryType() != Query.QueryTypeConstruct
				&& query.getQueryType() != Query.QueryTypeSelect) {
			throw new QueryHeaderException(errorMessage + federatedQuery,
					new Exception());

		} else
			return query.getQueryType();
	}
}