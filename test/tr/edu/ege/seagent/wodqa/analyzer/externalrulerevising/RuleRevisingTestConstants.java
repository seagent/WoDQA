package tr.edu.ege.seagent.wodqa.analyzer.externalrulerevising;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class RuleRevisingTestConstants {
	// sparql endpoint property
	static final Property SPARQL_ENPOINT_PRP = ResourceFactory
			.createProperty("http://rdfs.org/ns/void#sparqlEndpoint");
	/**
	 * constants
	 */
	public static final String VIRTUAL_DATASET_URI_SPACE = "http://virtuality.org/";
	public static final String QUERY_SOLUTION_VIRTUAL_DATASET = "http://querysolution/virtualdataset";
	// virtual dataset constants
	public static final String ONLY_OBJECT_RESOURCE_URI = "http://virtuality.org/virtual-resource";
	// dbpedia sample property constant
	public static final String DBPEDIA_EDITING_PRP_URI = "http://dbpedia.org/ontology/editing";
	public static final Literal VIRTUAL_DATASET_URI_SPACE_LITERAL = ResourceFactory
			.createPlainLiteral(VIRTUAL_DATASET_URI_SPACE);
	public static final String LMDB_EDITOR_PRP_URI = "http://data.linkedmdb.org/resource/movie/editor";

}
