package util;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import tr.edu.ege.seagent.wodqa.AbstractWoDQAComponentsTest;
import tr.edu.ege.seagent.wodqa.QueryVocabulary;
import tr.edu.ege.seagent.wodqa.VOIDCreator;
import tr.edu.ege.seagent.wodqa.query.WodqaEngine;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.rdf.model.Model;

/**
 * This class contains test for executing a parsable reorganized query on ARQ.
 * 
 * @author etmen
 * 
 */
public class GeneratingParsableReorganizedQueryTest extends
		AbstractWoDQAComponentsTest {

	public static final String SAMPLE_GENERIC_QUERY = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>"
			+ "SELECT * WHERE {"
			+ "?person rdf:type foaf:Person." + "?person ?predicate ?object}";

	public static final String SAMPLE_REGEX_QUERY = "SELECT  ?s ?l WHERE { "
			+ "{?s <http://www.w3.org/2000/01/rdf-schema#label> ?l}" + "UNION"
			+ "{?s <http://xmlns.com/foaf/0.1/name> ?l}"
			+ "FILTER regex(?l, \"Burak\", \"i\") }" + "LIMIT   20";

	/**
	 * This test controls ARQ parses reorganized query correctly.
	 * 
	 * @throws Exception
	 */
	@Test
	public void executeReorganizedQueryOnARQ() throws Exception {
		// creating facebook, foursquare and linkedin VOIDs.
		OntModel facebookVOID = createFacebookVOID();
		OntModel foursquareVOID = createFoursquareVOID();
		OntModel linkedinVOID = createLinkedinVOID();
		// creating linkesets between facebook, foursquare and linkedin VOIDs.
		createLinksetsBetweenTwoDatasets(facebookVOID, linkedinVOID,
				TestConstants.FACEBOOK_VOID_URI,
				TestConstants.LINKEDIN_VOID_URI);
		createLinksetsBetweenTwoDatasets(facebookVOID, foursquareVOID,
				TestConstants.FACEBOOK_VOID_URI,
				TestConstants.FOURSQUARE_VOID_URI);
		createLinksetsBetweenTwoDatasets(foursquareVOID, linkedinVOID,
				TestConstants.FOURSQUARE_VOID_URI,
				TestConstants.LINKEDIN_VOID_URI);
		// generating an example generic query.
		// creating list of voids
		List<OntModel> voidList = new ArrayList<OntModel>();
		voidList.add(facebookVOID);
		voidList.add(foursquareVOID);
		voidList.add(linkedinVOID);
		Model mainModel = mergeModels(facebookVOID, foursquareVOID,
				linkedinVOID);
		// using queryText and voids tries to reorganize and create execution
		// using QueryExecutor instance.
		boolean isExecutable = checkFederatedQueryIsExecutable(
				SAMPLE_REGEX_QUERY, mainModel);
		assertTrue(isExecutable);
	}

	/**
	 * This method uses
	 * {@link WodqaEngine#reorganizeQueryAndCreateExecution(List, boolean)}
	 * method to reorganize given query with given voidList and tries to create
	 * {@link QueryExecution} instance.
	 * 
	 * @param queryText
	 *            raw query to be federated
	 * @param mainModel
	 * @throws Exception
	 */
	private boolean checkFederatedQueryIsExecutable(String queryText,
			Model mainModel) throws Exception {
		// creating new QueryExecutor to construct QueryExecution instance using
		// federated query after analyzing and reorganizing given query text.
		WodqaEngine wodqaEngine = new WodqaEngine();
		try {
			wodqaEngine.reorganizeQueryAndCreateExecution(mainModel,
					queryText);
			return true;
		} catch (QueryParseException qpe) {
			return false;
		}
	}

	/**
	 * Creates linkesets between given voids both as given void URIs.
	 * 
	 * @param firstVOID
	 * @param secondVOID
	 * @param firstVoidURI
	 * @param secondVoidURI
	 */
	private void createLinksetsBetweenTwoDatasets(OntModel firstVOID,
			OntModel secondVOID, String firstVoidURI, String secondVoidURI) {
		if (firstVOID != null && secondVOID != null && firstVoidURI != null
				&& secondVoidURI != null) {
			VOIDCreator.createLinksets(firstVOID, secondVOID,
					QueryVocabulary.OWL_SAME_AS_RSC, firstVoidURI);
			VOIDCreator.createLinksets(secondVOID, firstVOID,
					QueryVocabulary.OWL_SAME_AS_RSC, secondVoidURI);
		}
	}

}
