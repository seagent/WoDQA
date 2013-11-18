package tr.edu.ege.seagent.wodqa.query.analyzer;

import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;

import tr.edu.ege.seagent.wodqa.QueryVocabulary;
import tr.edu.ege.seagent.wodqa.URISpaceFinder;
import tr.edu.ege.seagent.wodqa.exception.VOIDDescriptionConsistencyException;
import tr.edu.ege.seagent.wodqa.exception.WrongVOIDEntityConstructionException;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.ResultSetRewindable;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

public class RuleExecutor {
	/**
	 * All void models to analyze triple patterns.
	 */
	private List<OntModel> voidModels;

	private Logger logger = Logger.getLogger(RuleExecutor.class);

	/**
	 * Main model that holds all information of the void models.
	 */
	private Model mainModel;

	public Model getMainModel() {
		return mainModel;
	}

	public void setMainModel(Model mainModel) {
		this.mainModel = mainModel;
	}

	/**
	 * It executes relevant dataset discovery rules on triple patterns with the
	 * given main model.
	 * 
	 * @param mainModel
	 */
	public RuleExecutor(Model mainModel) {
		this.mainModel = mainModel;
	}

	/**
	 * It executes all single step rules once for the given triple pattern.
	 * 
	 * @param triple
	 * @throws VOIDDescriptionConsistencyException
	 * @throws WrongVOIDEntityConstructionException
	 * @throws URISyntaxException
	 */
	public void executeSingleStepRules(RelevantDatasetsForTriple triplePack)
			throws VOIDDescriptionConsistencyException,
			WrongVOIDEntityConstructionException, URISyntaxException {
		Triple triplePattern = triplePack.getTriple();
		// get s p o
		Node subject = triplePattern.getSubject();
		Node predicate = triplePattern.getPredicate();
		Node object = triplePattern.getObject();
		List<Resource> matchedDatasets = new ArrayList<Resource>();
		// VOCABULARY MATCH RULE
		if (predicate.isURI()
				&& !predicate.getURI().toString()
						.equals(QueryVocabulary.RDF_TYPE_URI)) {
			// find urispaces
			Stack<String> uriSpaceStack = new URISpaceFinder(
					predicate.toString()).reduceURISpaces();
			while (!uriSpaceStack.isEmpty() && matchedDatasets.isEmpty()) {
				matchedDatasets.addAll(executeVocabularyMatchRule(uriSpaceStack
						.pop()));
			}
			// set with new relevant datasets.
			performChanges(triplePack, matchedDatasets, null);
		}
		// TYPE INDEX RULE
		if (object.isURI()
				&& predicate.isURI()
				&& predicate.getURI().toString()
						.equals(QueryVocabulary.RDF_TYPE_URI)) {
			// find urispaces
			Stack<String> uriSpaceStack = new URISpaceFinder(object.toString())
					.reduceURISpaces();
			while (!uriSpaceStack.isEmpty() && matchedDatasets.isEmpty()) {
				matchedDatasets.addAll(executeVocabularyMatchRule(uriSpaceStack
						.pop()));
			}
			// set with new relevant datasets.
			performChanges(triplePack, matchedDatasets, null);
		}
		// URI LINKS TO RULE
		if (subject.isURI()) {
			// INTERNAL
			matchedDatasets = executeInternalURILinksToRule(subject.getURI()
					.toString(), triplePack);
			// set with new relevant datasets.
			performChanges(triplePack, matchedDatasets, null);
		}
		// LINKS TO URI RULES -- object must be URI and predicate can not be
		// rdf:type, because it is handled by the type index rule.
		if (object.isURI()
				&& (predicate.isVariable() || (predicate.isURI() && !predicate
						.getURI().toString()
						.equals(QueryVocabulary.RDF_TYPE_URI)))) {
			// INTERNAL
			List<Resource> internalMatchedDataset = executeInternalLinksToURIRule(
					object.getURI().toString(), triplePack);

			// EXTERNAL
			List<Resource> externalMatchedDataset = executeExternalLinksToURIRule(
					triplePattern, internalMatchedDataset);
			// union them
			matchedDatasets = unionInternalAndExternal(internalMatchedDataset,
					externalMatchedDataset);
			// set with union
			performChanges(triplePack, matchedDatasets, externalMatchedDataset);
		}

	}

	/**
	 * It executes the repetitive discovery rules.
	 * 
	 * @throws VOIDDescriptionConsistencyException
	 */
	public void executeRepetitiveRules(RelevantDatasetsForTriple triplePack1,
			RelevantDatasetsForTriple triplePack2)
			throws VOIDDescriptionConsistencyException {
		Triple tp1 = triplePack1.getTriple();
		Triple tp2 = triplePack2.getTriple();

		// internal matched datasets...
		List<Resource> internalMatchedDataset = new Vector<Resource>();

		// CHAINING MATCH RULES
		if (tp1.getObject().isVariable()
				&& tp2.getSubject().isVariable()
				&& tp1.getObject().toString()
						.equals(tp2.getSubject().toString())) {
			// INTERNAL RULE
			internalMatchedDataset = intersectionOfSets(
					triplePack1.getCurrentRelevantDatasets(),
					triplePack2.getCurrentRelevantDatasets());

			// EXTERNAL RULE
			ExternalDatasetsForTriples externalDatasetsForTriples = executeExternalChainingTriplesRule(
					triplePack1, triplePack2);
			// update
			updateWithInternalAndExternal(triplePack1, triplePack2,
					internalMatchedDataset, externalDatasetsForTriples);
		}
		// OBJECT SHARING RULES
		if (tp1.getObject().isVariable()
				&& tp2.getObject().isVariable()
				&& tp1.getObject().toString()
						.equals(tp2.getObject().toString())) {
			// INTERNAL RULE
			internalMatchedDataset = intersectionOfSets(
					triplePack1.getCurrentRelevantDatasets(),
					triplePack2.getCurrentRelevantDatasets());
			// EXTERNAL RULE
			ExternalDatasetsForTriples externalDatasetsForTriples = executeExternalObjectSharingTriplesRule(
					triplePack1, triplePack2);
			// update
			updateWithInternalAndExternal(triplePack1, triplePack2,
					internalMatchedDataset, externalDatasetsForTriples);
		}
		// SUBJECT SHARING RULE
		if (tp1.getSubject().isVariable()
				&& tp2.getSubject().isVariable()
				&& tp1.getSubject().toString()
						.equals(tp2.getSubject().toString())) {
			List<Resource> matchedDatasets = new Vector<Resource>();
			matchedDatasets = intersectionOfSets(
					triplePack1.getCurrentRelevantDatasets(),
					triplePack2.getCurrentRelevantDatasets());
			// update
			performChanges(triplePack1, matchedDatasets, null);
			performChanges(triplePack2, matchedDatasets, null);
		}
	}

	/**
	 * It performs union operation and update current datasets set for the given
	 * triple pattern pair..
	 * 
	 * @param triplePack1
	 * @param triplePack2
	 * @param internalMatchedDataset
	 * @param externalDatasetsForTriples
	 * @throws VOIDDescriptionConsistencyException
	 */
	private void updateWithInternalAndExternal(
			RelevantDatasetsForTriple triplePack1,
			RelevantDatasetsForTriple triplePack2,
			List<Resource> internalMatchedDataset,
			ExternalDatasetsForTriples externalDatasetsForTriples)
			throws VOIDDescriptionConsistencyException {
		List<Resource> matchedDatasets;
		// union for triple 1
		matchedDatasets = unionInternalAndExternal(internalMatchedDataset,
				externalDatasetsForTriples.getExternalsForTriple1());
		// update current relevant...
		performChanges(triplePack1, matchedDatasets,
				externalDatasetsForTriples.getExternalsForTriple1());

		// union for triple 2
		matchedDatasets = unionInternalAndExternal(internalMatchedDataset,
				externalDatasetsForTriples.getExternalsForTriple2());
		// update current relevant...
		performChanges(triplePack2, matchedDatasets,
				externalDatasetsForTriples.getExternalsForTriple2());
	}

	/**
	 * It searches proper linksets that has referrer dataset is included by both
	 * triple patterns' relevant datasets sets.
	 * 
	 * @param triplePack1
	 * @param triplePack2
	 * @return
	 */
	private ExternalDatasetsForTriples executeExternalObjectSharingTriplesRule(
			RelevantDatasetsForTriple triplePack1,
			RelevantDatasetsForTriple triplePack2) {
		logger.debug("Executing external object sharing triples rule...");
		long before = System.currentTimeMillis();
		List<Resource> referrerDatasets1 = new Vector<Resource>();
		List<Resource> referrerDatasets2 = new Vector<Resource>();
		// get predicates
		Node predicate1 = triplePack1.getTriple().getPredicate();
		Node predicate2 = triplePack2.getTriple().getPredicate();

		String query = QueryVocabulary.RDF_PREFIX_URI
				+ QueryVocabulary.VOID_PREFIX_URI
				+ "SELECT DISTINCT ?referrerDataset1 ?referrerDataset2 WHERE {"
				+ "?referrerDataset1 rdf:type void:Dataset.";
		query += " VALUES ?referrerDataset1 {";
		query = fillValuesBlock(query, triplePack1.getCurrentRelevantDatasets())
				+ "}. ";
		query += "?linkset1 void:subjectsTarget ?referrerDataset1. "
				+ "?linkset1 void:linkPredicate "
				+ constructQueryNodeForm(predicate1) + ". "
				+ "?linkset1 void:objectsTarget ?referencedDataset. "
				+ "?referrerDataset2 rdf:type void:Dataset. ";
		query += "VALUES ?referrerDataset2 {";
		query = fillValuesBlock(query, triplePack2.getCurrentRelevantDatasets())
				+ "}." + "?linkset2 void:subjectsTarget ?referrerDataset2.";
		query += "?linkset2 void:linkPredicate "
				+ constructQueryNodeForm(predicate2) + ". "
				+ "?linkset2 void:objectsTarget ?referencedDataset.";
		query += "}";
		
		QueryExecution execution = QueryExecutionFactory.create(query,
				mainModel);
		ResultSet resultSet = execution.execSelect();
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			Resource referrerDataset1 = querySolution
					.getResource("referrerDataset1");
			checkDatasetToAdd(referrerDatasets1, referrerDataset1);
			Resource referrerDataset2 = querySolution
					.getResource("referrerDataset2");
			checkDatasetToAdd(referrerDatasets2, referrerDataset2);
		}

		executeVirtualLinksetRule(referrerDatasets1, triplePack1,
				referrerDatasets2, triplePack2);

		long after = System.currentTimeMillis();
		logger.debug(MessageFormat.format(
				"Object sharing triples rule executed in \"{0}\" miliseconds",
				after - before));
		return new ExternalDatasetsForTriples(referrerDatasets1,
				referrerDatasets2);
	}

	private void executeVirtualLinksetRule(List<Resource> referrerDatasets1,
			RelevantDatasetsForTriple triplePack1,
			List<Resource> referrerDatasets2,
			RelevantDatasetsForTriple triplePack2) {

		String query = QueryVocabulary.RDF_PREFIX_URI
				+ QueryVocabulary.VOID_PREFIX_URI
				+ "SELECT DISTINCT ?referrerDataset1 ?referrerDataset2 WHERE {"
				+ "?referrerDataset1 rdf:type void:Dataset. ";
		query += " VALUES ?referrerDataset1 {";
		query = fillValuesBlock(query, triplePack1.getCurrentRelevantDatasets())
				+ "}. "
				+ "?linkset1 void:subjectsTarget ?referrerDataset1. "
				+ "?linkset1 void:linkPredicate "
				+ constructQueryNodeForm(triplePack1.getTriple().getPredicate())
				+ ". "
				+ "{?linkset1 void:objectsTarget ?referencedDataset1. "
				+ "FILTER NOT EXISTS {?referencedDataset1 void:sparqlEndpoint ?endpoint1.}} "
				+ "?referencedDataset1 void:uriSpace ?uriSpace. "
				+ "{?referencedDataset2 void:uriSpace ?uriSpace. "
				+ "FILTER NOT EXISTS {?referencedDataset2 void:sparqlEndpoint ?endpoint2.}} "
				+ "?referrerDataset2 rdf:type void:Dataset. ";
		query += "VALUES ?referrerDataset2 {";
		query = fillValuesBlock(query, triplePack2.getCurrentRelevantDatasets())
				+ "}. ";
		query += "?linkset2 void:subjectsTarget ?referrerDataset2. "
				+ "?linkset2 void:objectsTarget ?referencedDataset2. "
				+ "?linkset2 void:linkPredicate "
				+ constructQueryNodeForm(triplePack2.getTriple().getPredicate())
				+ ". " + "}";

		QueryExecution execution = QueryExecutionFactory.create(query,
				mainModel);
		ResultSet resultSet = execution.execSelect();
		while (resultSet.hasNext()) {
			QuerySolution querySolution = (QuerySolution) resultSet.next();
			Resource referrerDataset1 = querySolution
					.getResource("referrerDataset1");
			checkDatasetToAdd(referrerDatasets1, referrerDataset1);
			Resource referrerDataset2 = querySolution
					.getResource("referrerDataset2");
			checkDatasetToAdd(referrerDatasets2, referrerDataset2);
		}
	}

	/**
	 * This method turns given node into query appropriate form.
	 * 
	 * @param node
	 * @return
	 */
	private String constructQueryNodeForm(Node node) {
		if (node.isVariable()) {
			return "?" + node;
		} else if (node.isURI()) {
			return "<" + node.getURI() + ">";
		} else if (node.isLiteral()) {
			return "\"" + node.getLiteral() + "\"";
		}
		return "";
	}

	/**
	 * This method checks referrer dataset list contains given referrer dataset
	 * candidate
	 * 
	 * @param referrerDatasets
	 * @param referrerCandidate
	 */
	private void checkDatasetToAdd(List<Resource> referrerDatasets,
			Resource referrerCandidate) {
		if (!referrerDatasets.contains(referrerCandidate)) {
			referrerDatasets.add(referrerCandidate);
		}
	}

	/**
	 * It searches common datasets that referenced by subject of first triple
	 * pattern and that are relevant datasets for the second triple pattern.
	 * 
	 * @param triplePack1
	 * @param triplePack2
	 * @return
	 * @return
	 * @throws VOIDDescriptionConsistencyException
	 */
	private ExternalDatasetsForTriples executeExternalChainingTriplesRule(
			RelevantDatasetsForTriple triplePack1,
			RelevantDatasetsForTriple triplePack2)
			throws VOIDDescriptionConsistencyException {
		logger.debug("Executing external chaining triples rule...");
		long before = System.currentTimeMillis();
		List<Resource> properReferencedDatasets = new Vector<Resource>();
		List<Resource> properReferrerDatasets = new Vector<Resource>();
		Node predicate = triplePack1.getTriple().getPredicate();
		if (predicate.isURI()) {
			String query = QueryVocabulary.RDF_PREFIX_URI
					+ QueryVocabulary.VOID_PREFIX_URI
					+ "SELECT ?subjectsTarget ?objectsTarget WHERE {";
			query += "VALUES ?subjectsTarget {";
			query = fillValuesBlock(query,
					triplePack1.getCurrentRelevantDatasets());
			query += "}. ";
			query += "VALUES ?objectsTarget {";
			query = fillValuesBlock(query,
					triplePack2.getCurrentRelevantDatasets());
			query += "}.";
			query += "?linkset void:objectsTarget ?objectsTarget."
					+ "?linkset void:subjectsTarget ?subjectsTarget."
					+ "?linkset void:linkPredicate <" + predicate.getURI()
					+ ">." + "}";
			QueryExecution exec = QueryExecutionFactory
					.create(query, mainModel);
			ResultSet set = exec.execSelect();
			while (set.hasNext()) {
				QuerySolution querySolution = set.next();
				Resource referrerDataset = querySolution
						.getResource("subjectsTarget");
				Resource referencedDataset = querySolution
						.getResource("objectsTarget");
				properReferrerDatasets.add(referrerDataset);
				properReferencedDatasets.add(referencedDataset);
			}
			exec.close();
		}
		long after = System.currentTimeMillis();
		logger.debug(MessageFormat
				.format("External chaining triples rule executed in \"{0}\" miliseconds",
						after - before));
		return new ExternalDatasetsForTriples(properReferrerDatasets,
				properReferencedDatasets);
	}

	/**
	 * This method fills filter block.
	 * 
	 * @param query
	 * @param relevantDatasets
	 * 
	 * @return
	 */
	private String fillValuesBlock(String query, List<Resource> relevantDatasets) {
		for (int i = 0; i < relevantDatasets.size(); i++) {
			Resource dataset = relevantDatasets.get(i);
			query += " <" + dataset.getURI() + ">";
		}
		return query;
	}

	/**
	 * It intersects the given sets.
	 * 
	 * @param currentRelevantDatasets
	 * @param currentRelevantDatasets2
	 * @return
	 */
	private List<Resource> intersectionOfSets(
			List<Resource> currentRelevantDatasets,
			List<Resource> currentRelevantDatasets2) {
		List<Resource> intersectedSet = new Vector<Resource>();
		for (Resource resource : currentRelevantDatasets) {
			if (currentRelevantDatasets2.contains(resource))
				intersectedSet.add(resource);
		}
		return intersectedSet;
	}

	/**
	 * It merges given sets.
	 * 
	 * @param internalMatchedDataset
	 * @param externalMatchedDataset
	 * @return
	 */
	private List<Resource> unionInternalAndExternal(
			List<Resource> internalMatchedDataset,
			List<Resource> externalMatchedDataset) {
		List<Resource> unionOfThem = new Vector<Resource>();
		for (Resource externalOne : externalMatchedDataset) {
			unionOfThem.add(externalOne);
		}
		for (Resource internalOne : internalMatchedDataset) {
			if (!unionOfThem.contains(internalOne))
				unionOfThem.add(internalOne);
		}
		return unionOfThem;

	}

	/**
	 * It searches linkset that includes referrer dataset which links to given
	 * any referenced dataset by linkpredicate equals to predicate of the given
	 * triple pattern.
	 * 
	 * @param triplePattern
	 * @param internalMatchedDatasets
	 * @param internalMatchedDatasets
	 * @return
	 */
	private List<Resource> executeExternalLinksToURIRule(Triple triplePattern,
			List<Resource> internalMatchedDatasets) {
		logger.debug("Executing external links to URI rule...");
		long before = System.currentTimeMillis();
		List<Resource> relatedDatasets = new Vector<Resource>();
		if (triplePattern.getPredicate().isURI()) {
			String query = QueryVocabulary.RDF_PREFIX_URI
					+ QueryVocabulary.VOID_PREFIX_URI
					+ "SELECT DISTINCT ?referrerDataset WHERE {"
					+ "OPTIONAL{ VALUES ?referencedDataset {";
			query = fillValuesBlock(query, internalMatchedDatasets);
			query += "}}"
					+ "{OPTIONAL{?referencedDataset void:uriSpace ?referencedUrispace."
					+ "FILTER regex(\"" + triplePattern.getObject().getURI()
					+ "\", ?referencedUrispace,\"i\")}}"
					+ "?linkset void:objectsTarget ?referencedDataset."
					+ "?linkset void:linkPredicate <"
					+ triplePattern.getPredicate() + ">."
					+ "?linkset void:subjectsTarget ?referrerDataset." + "}";

			// get URISpaces of referenced datasets and referrer datasets
			// with link predicate of given triple pattern
			QueryExecution exec = QueryExecutionFactory
					.create(query, mainModel);
			ResultSet set = exec.execSelect();
			while (set.hasNext()) {
				QuerySolution solution = set.next();
				// get referrer dataset
				Resource referrerDataset = solution
						.getResource("referrerDataset");
				checkDatasetToAdd(relatedDatasets, referrerDataset);
			}
			exec.close();
		}
		long after = System.currentTimeMillis();
		logger.debug(MessageFormat.format(
				"External links to URI rule executed in \"{0}\" miliseconds",
				after - before));
		return relatedDatasets;
	}

	/**
	 * This method checks whether external link to URI rule is available
	 * 
	 * @param triplePattern
	 * @param relatedDatasets
	 * @param referrerDataset
	 * @param uriSpaceValue
	 * @param internalMatchedDatasets
	 * @param referencedDataset
	 * @return
	 */
	private boolean isExternalLinkToURIAvailable(Triple triplePattern,
			List<Resource> relatedDatasets, Resource referrerDataset,
			String uriSpaceValue, List<Resource> internalMatchedDatasets,
			Resource referencedDataset) {
		// if referenced dataset is virtual, means uriSpace property value of
		// this virtual dataset contained in virtual linkset description because
		// of virtual dataset itself is not contained in any void document
		// seperately.
		if (uriSpaceValue != null) {
			return referrerDataset != null
					&& triplePattern.getObject().getURI()
							.startsWith(uriSpaceValue)
					&& !relatedDatasets.contains(referrerDataset);
		} else
		// if referenced dataset is not virtual
		{
			return referrerDataset != null
					&& internalMatchedDatasets.contains(referencedDataset)
					&& !relatedDatasets.contains(referrerDataset);
		}
	}

	/**
	 * It finds the urispace of the object in all VOIDs.
	 * 
	 * @param objectURI
	 * @return
	 * @throws WrongVOIDEntityConstructionException
	 */
	private List<Resource> executeInternalLinksToURIRule(String objectURI,
			RelevantDatasetsForTriple triplePack)
			throws WrongVOIDEntityConstructionException {
		logger.debug("Executing internal links to URI rule...");
		long before = System.currentTimeMillis();
		List<Resource> datasets = searchURISpaceOfURI(objectURI, triplePack);
		long after = System.currentTimeMillis();
		logger.debug(MessageFormat
				.format("Executing internal links to URI rule executed in \"{0}\" miliseconds",
						after - before));
		return datasets;
	}

	/**
	 * It finds relevant datasets that includes the urispace of triple pattern's
	 * subject.
	 * 
	 * @param subjectURI
	 * @throws WrongVOIDEntityConstructionException
	 */
	private List<Resource> executeInternalURILinksToRule(String subjectURI,
			RelevantDatasetsForTriple triplePack)
			throws WrongVOIDEntityConstructionException {
		logger.debug("Executing internal URI links to rule...");
		long before = System.currentTimeMillis();
		List<Resource> datasets = searchURISpaceOfURI(subjectURI, triplePack);
		long after = System.currentTimeMillis();
		logger.debug(MessageFormat
				.format("Executing internal URI links to rule executed in \"{0}\" miliseconds",
						after - before));
		return datasets;
	}

	/**
	 * It searches urispace of the given URI in all VOIDs.
	 * 
	 * @param URI
	 * @return
	 * @throws WrongVOIDEntityConstructionException
	 */
	private List<Resource> searchURISpaceOfURI(String URI,
			RelevantDatasetsForTriple triplePack)
			throws WrongVOIDEntityConstructionException {

		String query = constructDatasetUrispaceQuery(URI, triplePack);

		List<Resource> relatedDatasets = new Vector<Resource>();
		// get urispaces and datasets from ontmodel.
		QueryExecution execution = QueryExecutionFactory.create(query,
				mainModel);
		ResultSet set = execution.execSelect();
		while (set.hasNext()) {
			QuerySolution solution = set.next();
			Resource dataset = solution.getResource("dataset");
			relatedDatasets.add(dataset);
		}
		execution.close();
		return relatedDatasets;
	}

	/**
	 * This method constructs {@link Query} to query datasets in main model
	 * 
	 * @param triplePack
	 * @return
	 */
	private String constructDatasetQuery(RelevantDatasetsForTriple triplePack) {
		// define initial query
		String query = QueryVocabulary.VOID_PREFIX_URI
				+ QueryVocabulary.RDF_PREFIX_URI
				+ "SELECT ?dataset ?urispace WHERE {?dataset rdf:type void:Dataset. ?dataset void:uriSpace ?urispace. ";

		// get current relevant datasets
		List<Resource> currentRelevantDatasets = triplePack
				.getCurrentRelevantDatasets();
		// check whether current relevant datasets is not null and not empty
		if (currentRelevantDatasets != null
				&& !currentRelevantDatasets.isEmpty()) {
			// add values to restrict solution set
			query += "VALUES ?dataset {";
			for (Resource resource : currentRelevantDatasets) {
				query += "<" + resource.getURI() + "> ";
			}
			query += "}";
		}
		query += "}";
		return query;
	}

	/**
	 * This method constructs {@link Query} to query datasets in main model
	 * 
	 * @param uri
	 * 
	 * @param triplePack
	 * @return
	 */
	private String constructDatasetUrispaceQuery(String uri,
			RelevantDatasetsForTriple triplePack) {
		// // define initial query
		String query = QueryVocabulary.VOID_PREFIX_URI
				+ QueryVocabulary.RDF_PREFIX_URI
				+ "SELECT DISTINCT ?dataset ?urispace WHERE {";

		// get current relevant datasets
		List<Resource> currentRelevantDatasets = triplePack
				.getCurrentRelevantDatasets();
		// check whether current relevant datasets is not null and not empty
		if (currentRelevantDatasets != null
				&& !currentRelevantDatasets.isEmpty()) {

			query += "VALUES ?dataset {";
			query = fillValuesBlock(query,
					triplePack.getCurrentRelevantDatasets());
			query += "}.";
		}
		query += "{?dataset void:uriSpace ?urispace.";
		query += "FILTER regex(\"" + uri + "\",?urispace,\"i\") }";
		query += "?dataset void:sparqlEndpoint ?endpoint. }";
		return query;

	}

	/**
	 * It performs changes on the relevant datasets set.
	 * 
	 * @param triplePack
	 * @param matchedDatasets
	 * @throws VOIDDescriptionConsistencyException
	 */
	public void performChanges(RelevantDatasetsForTriple triplePack,
			List<Resource> matchedDatasets, List<Resource> externalDatasets)
			throws VOIDDescriptionConsistencyException {
		triplePack.setNewRelevantDatasets(matchedDatasets);
		List<RelevantType> newRelevantTypes = new Vector<RelevantType>();
		if (externalDatasets != null) {
			for (Resource dataset : matchedDatasets) {
				if (externalDatasets.contains(dataset))
					newRelevantTypes.add(RelevantType.EXTERNAL);
				else
					newRelevantTypes.add(RelevantType.INTERNAL);
			}
		}
		triplePack.setNewRelevantTypes(newRelevantTypes);
		triplePack.eliminateWithNewFoundDatasets();
	}

	/**
	 * Executes vocabulary match rule and returns the relevant datasets that
	 * include vocabulary used by the triple pattern.
	 * 
	 * @param vocabularyNode
	 *            vocabulary node. It can be predicate or object when the
	 *            predicate is rdf:type.
	 * @return
	 */
	private List<Resource> executeVocabularyMatchRule(String searchedVoc) {
		logger.debug("Executing vocabulary match rule...");
		long before = System.currentTimeMillis();
		String query = QueryVocabulary.VOID_PREFIX_URI
				+ QueryVocabulary.RDF_PREFIX_URI
				+ "SELECT ?dataset WHERE {?dataset rdf:type void:Dataset. ?dataset void:vocabulary \""
				+ searchedVoc + "\".}";
		List<Resource> relatedDatasets = new Vector<Resource>();
		// get vocabulary and datasets from ontmodel.
		// execute query on main void model...
		QueryExecution execution = QueryExecutionFactory.create(query,
				mainModel);
		ResultSetRewindable setRewindable = ResultSetFactory
				.copyResults(execution.execSelect());
		while (setRewindable.hasNext()) {
			QuerySolution solution = setRewindable.next();
			Resource dataset = solution.getResource("dataset");
			if (!relatedDatasets.contains(dataset)) {
				relatedDatasets.add(dataset);
			}
		}
		execution.close();
		long after = System.currentTimeMillis();
		logger.debug(MessageFormat.format(
				"Vocabulary match rule executed in \"{0}\" miliseconds", after
						- before));
		return relatedDatasets;
	}

	public List<OntModel> getVoidModels() {
		return voidModels;
	}

	public void setVoidModels(List<OntModel> voidModels) {
		this.voidModels = voidModels;
	}

}
