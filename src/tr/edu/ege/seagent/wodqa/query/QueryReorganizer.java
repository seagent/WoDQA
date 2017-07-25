package tr.edu.ege.seagent.wodqa.query;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import tr.edu.ege.seagent.wodqa.QueryElementOperations;
import tr.edu.ege.seagent.wodqa.exception.InactiveEndpointException;
import tr.edu.ege.seagent.wodqa.exception.QueryHeaderException;
import tr.edu.ege.seagent.wodqa.query.analyzer.QueryAnalyzer;
import tr.edu.ege.seagent.wodqa.query.analyzer.QueryConstructorUtil;
import tr.edu.ege.seagent.wodqa.query.analyzer.VOIDPathSolution;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.DatasetImpl;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;

/**
 * Reorganize query to execute in the new form. TODO : query organize edilirken
 * SPARQL'in ozel syntax'lari dikkate alinmali!!! For example :LIMIT
 * 
 */
// TODO : SILENT keyword'ünün kullanılacağı dışarıdan parametre olarak verilmeli
public class QueryReorganizer {

	private Logger logger = Logger.getLogger(this.getClass());

	private QueryAnalyzer analyzer;

	/**
	 * Basic query before reorganize.
	 */
	private String basicQuery;

	/**
	 * Optional graph triple patterns.
	 */
	private List<Triple> optionalTriples;

	/**
	 * It creates {@link QueryReorganizer} without an agent.
	 */
	public QueryReorganizer() {
		analyzer = new QueryAnalyzer((Model) null);
	}

	/**
	 * Recreates a new SELECT or CONSTRUCT query with the given endpoint list
	 * and triple list.
	 * 
	 * @param voidPathSolutionList
	 *            endpoint list for each triple.
	 * @param tripleList
	 * @throws QueryHeaderException
	 * @throws InactiveEndpointException
	 */
	public String reorganizeWholeQuery(
			List<VOIDPathSolution> voidPathSolutionList, String wholeQuery)
			throws QueryHeaderException, InactiveEndpointException {
		// check sparql endpoints are active
		// checkendpointActivity(endpointList);

		setBasicQuery(wholeQuery);
		optionalTriples = QueryConstructorUtil
				.getTriplesFromOptionalBlock(wholeQuery);
		// extract triple patterns from WHERE clause...
		List<Triple> allTriples = analyzer.fillTriplePatternList(wholeQuery);
		// creates header according to simple query...
		String federatedQuery = "";
		federatedQuery = createQueryHeader(wholeQuery);
		federatedQuery = createWhereClause(voidPathSolutionList, wholeQuery,
				allTriples, federatedQuery);

		logger.info(MessageFormat
				.format("Constructed federated query is \"{0}\" for the given query \"{1}\".",
						federatedQuery, wholeQuery));

		return federatedQuery;
	}

	@SuppressWarnings("unused")
	private void checkendpointActivity(List<String> endpointList)
			throws InactiveEndpointException {
		List<String> uniqueEndpointList = new Vector<String>();
		for (String endpoint : endpointList) {
			if (!endpoint.contains("**")) {
				// add endpoint if it is not in endpoint list.
				addIfUnique(uniqueEndpointList, endpoint);
			} else {
				String[] splitEndpoint = endpoint.split("**");
				for (String splittedEndpoint : splitEndpoint) {
					addIfUnique(uniqueEndpointList, splittedEndpoint);
				}
			}
		}
		// ASK each of them.
		for (String endpointURL : uniqueEndpointList) {
			// create query...
			Query query = QueryFactory.create("ASK {SERVICE <" + endpointURL
					+ "> {?s ?p ?o}}");
			QueryExecution exec = QueryExecutionFactory.create(query,
					new DatasetImpl(ModelFactory.createDefaultModel()));
			try {
				exec.execAsk();
			} catch (Exception e) {
				throw new InactiveEndpointException(endpointURL
						+ " is not accessed!", e);
			}
		}
	}

	public void addIfUnique(List<String> uniqueEndpointList, String endpoint) {
		if (!uniqueEndpointList.contains(endpoint)) {
			uniqueEndpointList.add(endpoint);
		}
	}

	/**
	 * Prepare query header according to the given simple query...
	 * 
	 * @param wholeQuery
	 * @return
	 * @throws QueryHeaderException
	 */
	public String createQueryHeader(String wholeQuery)
			throws QueryHeaderException {
		Query query = QueryFactory.create(wholeQuery);
		// SELECT...
		if (query.isSelectType()) {
			return createHeaderWithVars(query);
		}
		// CONSTRUCT...
		else if (query.isConstructType()) {
			return "CONSTRUCT " + query.getConstructTemplate() + " WHERE {";
		}
		// ASK...
		else if (query.isAskType()) {
			return "ASK WHERE {";
		}
		// DESCRIBE
		else if (query.isDescribeType()) {
			return createHeaderWithVars(query);
		}

		throw new QueryHeaderException("Invalid Query Type:" + wholeQuery);
	}

	/**
	 * Creates a query header with given Query and fills the headers variables.
	 * 
	 * @param query
	 *            query to get variables.
	 * @return created header string.
	 */
	private String createHeaderWithVars(Query query) {
		String queryKey = null;
		if (query.isSelectType()) {
			queryKey = "SELECT";
		} else if (query.isConstructType()) {
			queryKey = "CONSTRUCT";
		} else if (query.isAskType()) {
			queryKey = "ASK";
		} else if (query.isDescribeType()) {
			queryKey = "DESCRIBE";
		}

		// check distinct type...
		String isDistinct = "";
		if (query.isDistinct())
			isDistinct = "DISTINCT ";

		List<String> resultVars = query.getResultVars();
		String vars = "";
		for (String var : resultVars) {
			vars += " ?" + var;
		}

		return queryKey + " " + isDistinct + vars + " WHERE {";
	}

	private boolean isThereUnion = false;

	/**
	 * Creates WHERE clause of a simple query...
	 * 
	 * @param voidPathSolutionList
	 * @param wholeQuery
	 * @param allTriples
	 * @param federatedQuery
	 * @return
	 */
	private String createWhereClause(
			List<VOIDPathSolution> voidPathSolutionList, String wholeQuery,
			List<Triple> allTriples, String federatedQuery) {
		// CHECK UNION BLOCKS...
		List<Element> unionBlocks = analyzer.getUnionBlocks(wholeQuery);
		List<Triple> tripleList = new Vector<Triple>();
		if (unionBlocks.size() == 0) {
			tripleList = allTriples;
			federatedQuery += groupTriplesAccordingToServices(
					voidPathSolutionList, tripleList);
		} else {
			isThereUnion = true;
			// IF THERE ARE UNION BLOCKS...
			for (int i = 0; i < unionBlocks.size(); i++) {
				// find triples in every union blocks.
				tripleList = analyzer.discoverTripleInQuery(unionBlocks.get(i));
				List<VOIDPathSolution> endpointListPart = cutUnionPartEndpoints(
						voidPathSolutionList, tripleList.size());
				federatedQuery += "{"
						+ groupTriplesAccordingToServices(endpointListPart,
								tripleList) + "}";
				if (i < unionBlocks.size() - 1)
					federatedQuery += " UNION ";
			}
		}
		federatedQuery += "}";
		// add filter block if there is...
		federatedQuery = addFilterBlock(getBasicQuery(), federatedQuery);
		// add limit block if there is...
		federatedQuery = addLimitAndOffsetBlock(getBasicQuery(), federatedQuery);
		return federatedQuery;
	}

	/**
	 * It adds the limit and offset keywords if there exists.
	 * 
	 * @param basicQuery
	 * @param federatedQuery
	 * @return
	 */
	private String addLimitAndOffsetBlock(String basicQuery,
			String federatedQuery) {
		Query query = QueryFactory.create(basicQuery);
		long limit = query.getLimit();
		long offset = query.getOffset();
		if (limit > -1)
			federatedQuery += " LIMIT " + limit;
		if (offset > -1)
			federatedQuery += " OFFSET " + offset;
		return federatedQuery;
	}

	/**
	 * It copies union part's triples' endpoints' to a temporary list.
	 * 
	 * @param voidPathSolutionList
	 *            Whole endpoint list.
	 * @return
	 */
	private List<VOIDPathSolution> cutUnionPartEndpoints(
			List<VOIDPathSolution> voidPathSolutionList, int size) {
		List<VOIDPathSolution> partOfVOIDPathSolutionList = new Vector<VOIDPathSolution>();
		for (int i = 0; i < size; i++) {
			partOfVOIDPathSolutionList.add(voidPathSolutionList.get(0));
			voidPathSolutionList.remove(0);
		}
		return partOfVOIDPathSolutionList;
	}

	/**
	 * This method creates {@link DeterminedTriple} objects using given
	 * {@link VOIDPathSolution} list and {@link Triple} list which means that
	 * triple and its endpoints to query this triple
	 * 
	 * @param voidSolutionPathList
	 * @param tripleList
	 * @return {@link List} of {@link DeterminedTriple}s
	 */
	private List<DeterminedTriple> createDeterminedTriples(
			List<VOIDPathSolution> voidSolutionPathList, List<Triple> tripleList) {
		// create an empty determined triple list
		List<DeterminedTriple> determinedTriples = new ArrayList<DeterminedTriple>();
		// check first size of two list are equal
		if (voidSolutionPathList.size() == tripleList.size()) {
			// create determined triple instances
			for (int i = 0; i < tripleList.size(); i++) {
				determinedTriples.add(new DeterminedTriple(tripleList.get(i),
						voidSolutionPathList.get(i).getAllEndpoints()));
			}
		}
		return determinedTriples;
	}

	/**
	 * This method organizes triple which will be queried from same endpoint.
	 * 
	 * @param voidSolutionPathList
	 * @param tripleList
	 * @return
	 */
	private String groupTriplesAccordingToServices(
			List<VOIDPathSolution> voidSolutionPathList, List<Triple> tripleList) {
		// initialize single and group statement
		String allStmt = "";
		// create DeterminedTriple list
		List<DeterminedTriple> determinedTriples = createDeterminedTriples(
				voidSolutionPathList, tripleList);

		List<TripleGroup> tripleGroups = generateTripleGroups(determinedTriples);
		orderGroups(tripleGroups);

		for (TripleGroup tripleGroup : tripleGroups) {
			Statement statement = new Statement("", false);
			addTripleGroupToStatement(tripleGroup, statement);
			// add group statement to all statements
			allStmt += statement.getStatementText();
		}

		return allStmt;
	}

	private void addTripleGroupToStatement(TripleGroup tripleGroup,
			Statement statement) {
		List<ValuedTriple> valuedTriples = tripleGroup.getValuedTriples();
		if (valuedTriples != null && !valuedTriples.isEmpty()) {
			// get endpoint list
			List<String> endpoints = valuedTriples.get(0).getDeterminedTriple()
					.getEndpoints();
			// add endpoint block for triple group
			String endpointBlock = constructMultipleEndpointWithBINDKeyword(endpoints);
			statement.setStatementText(endpointBlock + " {");

			// add triples to statement...
			for (int i = 0; i < valuedTriples.size(); i++) {
				// get ordered valued triple
				ValuedTriple orderedValuedTP = valuedTriples.get(i);
				// check for whether neighbor is optional
				checkForOptional(statement,
						orderedValuedTP.getDeterminedTriple());
				// put optional finding endpoints
				addTripleToStatement(statement,
						orderedValuedTP.getDeterminedTriple());
				// close if neighbor is optional
				closeOptionalClause(statement, valuedTriples, i);
			}

		}
		statement.addPattern("}");

	}

	/**
	 * This method generates triple group according to endpoints of determined
	 * triple and its neighbors.
	 * 
	 * @param determinedTriples
	 *            all {@link DeterminedTriple} instances
	 * 
	 * @return new group statement
	 */
	private List<TripleGroup> generateTripleGroups(
			List<DeterminedTriple> determinedTriples) {
		// define triple group list
		List<TripleGroup> tripleGroups = new ArrayList<TripleGroup>();
		// construct triple groups
		while (!determinedTriples.isEmpty()) {
			tripleGroups.add(generateTripleGroup(determinedTriples));
		}
		return tripleGroups;
	}

	/**
	 * This method checks whether optional triple has been added for closing the
	 * OPTIONAL block
	 * 
	 * @param statement
	 * @param valuedTriples
	 *            {@link DeterminedTriple} list to control its size.
	 * @param index
	 *            index of current location of {@link DeterminedTriple} list.
	 */
	private void closeOptionalClause(Statement statement,
			List<ValuedTriple> valuedTriples, int index) {
		if (statement.isFirstOptional() == true
				&& (valuedTriples.size() == index + 1 || (valuedTriples.size() > index + 1 && !optionalTriples
						.contains(valuedTriples.get(index + 1)
								.getDeterminedTriple().getTriple())))) {
			statement.setFirstOptional(false);
			statement.addPattern("}");
		}
	}

	/**
	 * This method checks for adding OPTIONAL block.
	 * 
	 * @param statement
	 * @param determinedTriple
	 *            {@link DeterminedTriple} instance to control whether optional
	 *            triples contains its triple
	 * 
	 * @return
	 */
	private void checkForOptional(Statement statement,
			DeterminedTriple determinedTriple) {
		if (statement.isFirstOptional() == false
				&& optionalTriples.contains(determinedTriple.getTriple())) {
			statement.setFirstOptional(true);
			statement.addPattern("OPTIONAL {");
		}
	}

	/**
	 * This method adds neighbor triples of given {@link DeterminedTriple}
	 * instance to group statement
	 * 
	 * @param determinedTriples
	 *            all {@link DeterminedTriple}s
	 * 
	 * @return
	 * 
	 * @return new group statement.
	 */
	private TripleGroup generateTripleGroup(
			List<DeterminedTriple> determinedTriples) {
		// get neighbor determined triples
		List<DeterminedTriple> neighborTriples = getTriplesWithSameEndpoint(determinedTriples);

		// find unrelated triples that has no common variable with the other
		// triples in the group
		List<DeterminedTriple> unrelatedTriples = findUnrelatedTriples(neighborTriples);
		// remove unrelated triples from nighbors
		neighborTriples.removeAll(unrelatedTriples);
		// remove added triples
		determinedTriples.removeAll(neighborTriples);

		// order determined triple group
		List<ValuedTriple> orderedValuedTriples = orderTriples(neighborTriples);

		TripleGroup tripleGroup = new TripleGroup(orderedValuedTriples);

		return tripleGroup;

	}

	/**
	 * It looks into the triple group and searches triples such have not any
	 * common variable with other triples.
	 * 
	 * @param neighborTriples
	 *            triples that will be queried from same dataset
	 * @return {@link List} of {@link DeterminedTriple}s that have no common
	 *         variable with other triples.
	 */
	private List<DeterminedTriple> findUnrelatedTriples(
			List<DeterminedTriple> neighborTriples) {
		List<DeterminedTriple> unrelatedTriples = new ArrayList<DeterminedTriple>();
		// iterate on each neighbor triple
		for (int i = 0; i < neighborTriples.size(); i++) {
			// set found a common variabled triple to false.
			boolean found = false;
			// iterate on other triples
			for (int j = 0; j < neighborTriples.size(); j++) {
				// check whether two triple have any common variable
				if (i != j
						&& hasCommonVariable(
								neighborTriples.get(i).getTriple(),
								neighborTriples.get(j).getTriple())) {
					// if found set found true and break loop.
					found = true;
					break;
				}
			}
			// if there is not found any common variable add it to the unrelated
			// list.
			if (!found && neighborTriples.size() > unrelatedTriples.size() + 1) {
				unrelatedTriples.add(neighborTriples.get(i));
			}
		}
		return unrelatedTriples;
	}

	/**
	 * This method adds {@link DeterminedTriple} instance to given group
	 * statement, and marks this {@link DeterminedTriple} as added.
	 * 
	 * @param statement
	 * @param determinedTriple
	 *            {@link DeterminedTriple} instance whose triple will be added
	 *            to group statement.
	 * @return new group statement.
	 */
	private void addTripleToStatement(Statement statement,
			DeterminedTriple determinedTriple) {
		statement.addPattern(QueryElementOperations
				.convertTripleToString(determinedTriple.getTriple()));
	}

	/**
	 * This method retrieves neighbor {@link DeterminedTriple}s which has same
	 * Endpoints with base {@link DeterminedTriple} instance.
	 * 
	 * @param determinedTriples
	 * @return neighbor {@link DeterminedTriple} list
	 */
	private List<DeterminedTriple> getTriplesWithSameEndpoint(
			List<DeterminedTriple> determinedTriples) {
		// define neighbor determined triple list
		List<DeterminedTriple> neighborDTs = new ArrayList<DeterminedTriple>();
		// check whether determined triple list contains at least 1 element
		if (determinedTriples != null && !determinedTriples.isEmpty()) {
			// get base determined triple and add it to list
			DeterminedTriple baseDT = determinedTriples.get(0);
			neighborDTs.add(baseDT);
			// iterate starting next determined triple and check for
			// neighborhood
			for (int i = 1; i < determinedTriples.size(); i++) {
				DeterminedTriple currentDT = determinedTriples.get(i);
				if (!baseDT.equals(currentDT)
						&& isEndpointsAreSame(baseDT, currentDT)) {
					neighborDTs.add(currentDT);
				}
			}
		}
		return neighborDTs;
	}

	/**
	 * This method checks endpoints of two {@link DeterminedTriple} are same
	 * 
	 * @param firstDT
	 * @param secondDT
	 * @return true if they are all equal, false otherwise
	 */
	private boolean isEndpointsAreSame(DeterminedTriple firstDT,
			DeterminedTriple secondDT) {
		if (firstDT.getEndpoints().size() == 1
				&& secondDT.getEndpoints().size() == 1
				&& firstDT.getEndpoints().get(0)
						.equals(secondDT.getEndpoints().get(0))) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether there are multiplies endpointurl or not for a given
	 * triple. "**" is the indicator of the end of one endpoint URL.
	 * 
	 * @param allEndpoints
	 * 
	 * @return
	 */
	private String constructMultipleEndpointWithBINDKeyword(
			List<String> allEndpoints) {
		// If no endpoint was found for optional triple, endpoint name is
		// "OPTIONAL"
		if (allEndpoints.contains("OPTIONAL"))
			return "OPTIONAL";
		String bindKeyword = "";
		if (allEndpoints.size() > 1) {
			String randomServiceVariable = String.valueOf(
					new Random().nextDouble()).substring(2, 8);
			for (int i = 0; i < allEndpoints.size() - 1; i++) {
				bindKeyword += "{ BIND(<" + allEndpoints.get(i) + "> AS ?ser"
						+ randomServiceVariable + ")} ";
				// cut **
				if (allEndpoints.size() > (i + 2)) {
					bindKeyword += " UNION ";
				} else {
					bindKeyword += "UNION { BIND(<" + allEndpoints.get(i + 1)
							+ "> AS ?ser" + randomServiceVariable + ")} ";
				}
			}
			// bindKeyword += " SERVICE SILENT ?ser" + randomServiceVariable;
			bindKeyword += " SERVICE ?ser" + randomServiceVariable;
		} else {
			// bindKeyword = "SERVICE SILENT <"
			// + voidPathSolution.getAllEndpoints().get(0) + ">";
			if (allEndpoints != null && !allEndpoints.isEmpty()) {
				String endpoint = allEndpoints.get(0);
				if (endpoint != null && endpoint != "") {
					bindKeyword = "SERVICE <" + endpoint + ">";
				}
			}
		}
		return bindKeyword;
	}

	/**
	 * Adds filter block to the federated query.
	 * 
	 * @param filterBlocks
	 * @param federatedQuery
	 */
	public String addFilterBlock(String basicQuery, String federatedQuery) {
		List<Expr> filterBlocks = analyzer.getFilterBlocks(basicQuery);

		if (filterBlocks != null && filterBlocks.size() > 0) {
			for (int i = 0; i < filterBlocks.size(); i++) {
				Expr filterExpr = filterBlocks.get(i);
				if (!isThereUnion) {
					Set<Var> vars = filterExpr.getVarsMentioned();
					String lastVarible = findLastVarible(vars, federatedQuery);
					federatedQuery = addFilterBlockAfterLastVariable(
							new ElementFilter(filterExpr), federatedQuery,
							lastVarible, vars);
				} else {
					federatedQuery = addFilterBlockToLast(federatedQuery,
							new ElementFilter(filterExpr));
				}
			}
		}
		return federatedQuery;
	}

	/**
	 * It adds the filter block to the end of the query block.
	 * 
	 * @param federatedQuery
	 * @param elementFilter
	 * @return
	 */
	private String addFilterBlockToLast(String federatedQuery,
			ElementFilter elementFilter) {
		return federatedQuery.substring(0, federatedQuery.lastIndexOf("}"))
				+ " " + elementFilter.toString() + "}";
	}

	/**
	 * Adds filter block to the end of BGP that has last any variable used in
	 * filter block.
	 * 
	 * @param elementFilter
	 * @param federatedQuery
	 * @param lastVarible
	 * @param filterVars
	 * @return
	 */
	private String addFilterBlockAfterLastVariable(ElementFilter elementFilter,
			String federatedQuery, String lastVarible, Set<Var> filterVars) {
		String filterTriple = findFilterExpressionGroupingTriple(
				federatedQuery, lastVarible, filterVars);
		int startPoint = federatedQuery.indexOf(filterTriple);
		int addPoint = startPoint + filterTriple.length();
		String secondPart = federatedQuery.substring(addPoint);
		String firstPart = federatedQuery.substring(0, startPoint);
		return firstPart + " { " + filterTriple + elementFilter.toString()
				+ " } " + secondPart;
	}

	private String findFilterExpressionGroupingTriple(String federatedQuery,
			String lastVarible, Set<Var> filterVars) {
		List<Var> currentSeenVars = new ArrayList<Var>();
		List<Triple> triplePatternList = getTriplePatternList(getBasicQuery());
		Triple lastTriple = null;
		for (Triple triple : triplePatternList) {
			addVariablesToCurrentSeens(triple, currentSeenVars);
			if (triple.getSubject().isVariable()
					&& triple.getSubject().toString().equals(lastVarible)) {
				lastTriple = triple;
			} else if (triple.getPredicate().isVariable()
					&& triple.getPredicate().toString().equals(lastVarible)) {
				lastTriple = triple;
			} else if (triple.getObject().isVariable()
					&& triple.getObject().toString().equals(lastVarible)) {
				lastTriple = triple;
			}
			if (lastTriple != null
					&& !hasFilterNotSeenVar(filterVars, currentSeenVars)) {
				return QueryElementOperations.convertTripleToString(lastTriple);
			}
		}
		return QueryElementOperations.convertTripleToString(lastTriple);
	}

	/**
	 * This method checks whether any variable of filter block is other than
	 * variables that have seen so far.
	 * 
	 * @param filterVars
	 * @param currentSeenVars
	 * @return
	 */
	private boolean hasFilterNotSeenVar(Set<Var> filterVars,
			List<Var> currentSeenVars) {
		for (Var filterVar : filterVars) {
			if (!currentSeenVars.contains(filterVar)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * It checks all nodes of triple to add its variables to the seen variable
	 * list
	 * 
	 * @param triple
	 * @param currentSeenVars
	 */
	private void addVariablesToCurrentSeens(Triple triple,
			List<Var> currentSeenVars) {
		checkToAddVariableList(triple.getSubject(), currentSeenVars);
		checkToAddVariableList(triple.getPredicate(), currentSeenVars);
		checkToAddVariableList(triple.getObject(), currentSeenVars);
	}

	/**
	 * It checks given node is variable and not seen before, if so adds variable
	 * to the seen variable list
	 * 
	 * @param node
	 * @param currentSeenVars
	 */
	private void checkToAddVariableList(Node node, List<Var> currentSeenVars) {
		if (node.isVariable() && !currentSeenVars.contains(node)) {
			currentSeenVars.add((Var) node);
		}
	}

	private List<Triple> triplePatternList;

	private List<Triple> getTriplePatternList(String federatedQuery) {
		if (triplePatternList == null) {
			triplePatternList = analyzer.fillTriplePatternList(federatedQuery);
		}
		return triplePatternList;
	}

	/**
	 * Searches for the last position of given variables in the given query. The
	 * variable which occurs after all other variables is returned.
	 * 
	 * @param vars
	 * @param federatedQuery
	 * @return
	 */
	private String findLastVarible(Set<Var> vars, String federatedQuery) {
		int biggestIndex = -1;
		String biggestVar = "";
		for (Var var : vars) {
			String varString = var.toString();
			if (federatedQuery.lastIndexOf(varString) > biggestIndex) {
				biggestIndex = federatedQuery.lastIndexOf(varString);
				biggestVar = varString;
			}
		}
		return biggestVar;
	}

	/**
	 * Finds variable in filter block list.
	 * 
	 * @param filterBlock
	 */
	public List<String> findVariables(String filterBlock) {
		List<String> varList = new Vector<String>();

		while (filterBlock.indexOf("?") > 0) {
			int indexOfQuestion = filterBlock.indexOf("?");
			int lastIndexOfVariable = filterBlock.indexOf(" ", indexOfQuestion);
			String var = filterBlock.substring(indexOfQuestion,
					lastIndexOfVariable);
			if (!varList.contains(var))
				varList.add(var);
			filterBlock = filterBlock.substring(lastIndexOfVariable);
		}
		return varList;
	}

	/**
	 * @return the basicQuery
	 */
	private String getBasicQuery() {
		return basicQuery;
	}

	/**
	 * @param basicQuery
	 *            the basicQuery to set
	 */
	private void setBasicQuery(String basicQuery) {
		this.basicQuery = basicQuery;
	}

	/**
	 * This method changes order of triples according to their heuristic cost
	 * 
	 * @param determinedTripleList
	 *            {@link Triple} list to be ordered according to their cost
	 * @return ordered {@link Triple} {@link List}
	 */
	public List<ValuedTriple> orderTriples(
			List<DeterminedTriple> determinedTripleList) {

		// define valuedTriples to be sorted according their values
		List<ValuedTriple> valuedTriples = new ArrayList<ValuedTriple>();

		// calculate triple values and calculate valued triple instances
		for (DeterminedTriple determinedTriple : determinedTripleList) {
			int value = calculateValue(determinedTriple.getTriple());
			ValuedTriple valuedTriple = new ValuedTriple(determinedTriple,
					value);
			valuedTriples.add(valuedTriple);
		}

		// sort valued triple list
		valuedTriples = sortValuedTripleList(valuedTriples);

		// sort all variables for common variable
		return sortForCommonVariables(valuedTriples);

	}

	/**
	 * This method finds triple with most common variable to base
	 * {@link ValuedTriple} if there exist any
	 * 
	 * @param valuedTriples
	 * @param i
	 * @param baseVT
	 */
	private ValuedTriple findTripleWithCommonVariable(
			List<ValuedTriple> valuedTriples, int i, ValuedTriple baseVT) {
		// iterate triples after itself base triple
		for (int j = i + 1; j < valuedTriples.size(); j++) {

			// get next valued triple
			ValuedTriple currentVT = valuedTriples.get(j);

			if (hasCommonVariable(baseVT.getDeterminedTriple().getTriple(),
					currentVT.getDeterminedTriple().getTriple())) {
				return currentVT;
			}
		}
		return null;
	}

	private boolean hasCommonVariable(Triple firstTriple, Triple secondTriple) {
		// get subject predicate and object nodes of base triple
		Node baseSubject = firstTriple.getSubject();
		Node basePredicate = firstTriple.getPredicate();
		Node baseObject = firstTriple.getObject();

		// get subject predicate and object nodes of current triple
		Node currentSubject = secondTriple.getSubject();
		Node currentPredicate = secondTriple.getPredicate();
		Node currentObject = secondTriple.getObject();

		// check whether there is common variable among
		// subjects-predicates-objects combinations
		if (areEqualVariables(baseSubject, currentSubject)
				|| areEqualVariables(baseSubject, currentPredicate)
				|| areEqualVariables(baseSubject, currentObject)
				|| areEqualVariables(basePredicate, currentSubject)
				|| areEqualVariables(basePredicate, currentPredicate)
				|| areEqualVariables(basePredicate, currentObject)
				|| areEqualVariables(baseObject, currentSubject)
				|| areEqualVariables(baseObject, currentPredicate)
				|| areEqualVariables(baseObject, currentObject)) {
			return true;
		}
		return false;
	}

	private boolean areEqualVariables(Node baseNode, Node currentNode) {
		if (baseNode.isVariable() && currentNode.isVariable()
				&& baseNode.equals(currentNode)) {
			return true;
		}
		return false;
	}

	/**
	 * This method sorts given {@link ValuedTriple} list
	 * 
	 * @param valuedTriples
	 * @return sorted {@link ValuedTriple} list
	 */
	private List<ValuedTriple> sortValuedTripleList(
			List<ValuedTriple> valuedTriples) {
		for (int i = 0; i < valuedTriples.size(); i++) {
			for (int j = i + 1; j < valuedTriples.size(); j++) {
				if (valuedTriples.get(i).getValue() < valuedTriples.get(j)
						.getValue()) {
					ValuedTriple temp = valuedTriples.get(i);
					valuedTriples.set(i, valuedTriples.get(j));
					valuedTriples.set(j, temp);
				}
			}
		}
		return valuedTriples;
	}

	/**
	 * This method calculates value ot given {@link Triple} instance
	 * 
	 * @param triple
	 *            {@link Triple} to check its value
	 * @return value of {@link Triple}
	 */
	private int calculateValue(Triple triple) {
		return getTypeValue(triple.getSubject()) * 5
				+ getTypeValue(triple.getPredicate()) * 2
				+ getTypeValue(triple.getObject()) * 3;
	}

	/**
	 * This method checks type of given {@link Node} instance and returns a
	 * value according its type.
	 * 
	 * @param node
	 *            {@link Node} instance to check its value
	 * @return value of {@link Node}
	 */
	private int getTypeValue(Node node) {
		if (node.isVariable()) {
			return 1;
		} else if (node.isLiteral()) {
			return 3;
		} else if (node.isURI()) {
			return 5;
		}
		return 0;
	}

	/**
	 * This method reorders given {@link TripleGroup} list first according to
	 * most average value, then according to most common variable.
	 * 
	 * @param tripleGroups
	 */
	public void orderGroups(List<TripleGroup> tripleGroups) {
		sortTriplePatternGroups(tripleGroups);
		sortForMostCommonVarialbes(tripleGroups);
	}

	/**
	 * This method resorts value sorted triples according whether having common
	 * variables
	 * 
	 * @param valuedTriples
	 *            value sorted triples
	 * @return common variable sorted triples
	 */
	private List<ValuedTriple> sortForCommonVariables(
			List<ValuedTriple> valuedTriples) {

		for (int i = 0; i < valuedTriples.size(); i++) {
			// get current valued triple
			ValuedTriple currentVT = valuedTriples.get(i);
			// find common variable triple if there exist any
			ValuedTriple commonVariableVT = findTripleWithCommonVariable(
					valuedTriples, i, currentVT);

			// get common variable triple and operate if it is not null
			if (commonVariableVT != null && (i + 1) < valuedTriples.size()) {

				// get index of valued common variable valued triple
				int indexOfCommonsVariableVT = valuedTriples
						.indexOf(commonVariableVT);
				// add common variable valued triple
				valuedTriples.add(i + 1, commonVariableVT);
				// remove old one
				valuedTriples.remove(indexOfCommonsVariableVT + 1);
			}
		}
		return valuedTriples;
	}

	/**
	 * This method sorts given {@link TripleGroup} list according to including
	 * most common variable
	 * 
	 * @param tripleGroups
	 */
	private void sortForMostCommonVarialbes(List<TripleGroup> tripleGroups) {
		for (int i = 0; i < tripleGroups.size(); i++) {
			// calculate most common variable group of given index group
			calculateMostCommonVariableGroupOfGivenGroupIndex(tripleGroups, i);
			// get most common variable group
			TripleGroup mostCommonVariableGroup = tripleGroups.get(i)
					.getMostCommonVariableGroup();
			// update location of most common variable group
			if (mostCommonVariableGroup != null) {
				int oldCommonGroupIndex = tripleGroups
						.indexOf(mostCommonVariableGroup);
				tripleGroups.add(i + 1, mostCommonVariableGroup);
				tripleGroups.remove(oldCommonGroupIndex + 1);
			}
		}
	}

	/**
	 * This method calculates most common variable group of {@link TripleGroup}
	 * with given index value
	 * 
	 * @param tripleGroups
	 *            all {@link TripleGroup} list
	 * @param index
	 *            {@link TripleGroup} index whose most common variable group
	 *            will be calculated
	 */
	private void calculateMostCommonVariableGroupOfGivenGroupIndex(
			List<TripleGroup> tripleGroups, int index) {
		int maxCommonVariableCount = 0;
		for (int j = index + 1; j < tripleGroups.size(); j++) {
			// calculate common variable count
			int commonVariableCount = calculateCommonVariableCount(
					tripleGroups.get(index), tripleGroups.get(j));
			// check common variable count is greater than maximum value
			if (commonVariableCount > maxCommonVariableCount) {
				// update maximum common vartaible count
				maxCommonVariableCount = commonVariableCount;
				// update most common variable of upper group
				tripleGroups.get(index).setMostCommonVariableGroup(
						tripleGroups.get(j));
			}
		}
	}

	/**
	 * This method finds common varaible count of given two {@link TripleGroup}
	 * 
	 * @param upperGroup
	 *            first {@link TripleGroup} whose value is higher
	 * @param lowerGroup
	 *            second {@link TripleGroup} whose value is lower
	 * @return common variable count of given two {@link TripleGroup}s
	 */
	private int calculateCommonVariableCount(TripleGroup upperGroup,
			TripleGroup lowerGroup) {
		// define common variable count
		int commonVariableCount = 0;
		// calculate common variable count of two groups
		for (Node upperVariable : upperGroup.getVariables()) {
			if (lowerGroup.getVariables().contains(upperVariable)) {
				commonVariableCount++;
			}
		}
		return commonVariableCount;
	}

	/**
	 * This method sorts given {@link TripleGroup} list from highest to lowest
	 * 
	 * @param tripleGroups
	 */
	private void sortTriplePatternGroups(List<TripleGroup> tripleGroups) {
		for (int i = 0; i < tripleGroups.size(); i++) {
			for (int j = i + 1; j < tripleGroups.size(); j++) {
				if (tripleGroups.get(i).getAverageValue() < tripleGroups.get(j)
						.getAverageValue()) {
					TripleGroup temp = tripleGroups.get(i);
					tripleGroups.set(i, tripleGroups.get(j));
					tripleGroups.set(j, temp);
				}
			}
		}
	}

}
