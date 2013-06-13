package tr.edu.ege.seagent.wodqa;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * This class includes query examples and etc...
 * 
 */
public class QueryVocabulary {

	public static final String DBPEDIA_URISPACE = "http://dbpedia.org/resource/";
	public static final String RDF_TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	private static final String DRUGBANK_DRUGS_URI = "http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/";
	public static final String SALES_PERSON_VARIABLE = "?salesPerson";
	public static final String MOVIE_URI_INSTANCE = "http://data.linkedmdb.org/resource/film/1028";
	/**
	 * Strings for query construction.
	 */
	public static final String SELECT_ALL = "SELECT *";
	public static final String WHERE = "WHERE";
	public static final String END = "}";
	public static final String START = "{";
	public static final String DOT = ".";
	private static final String O = "?o";
	private static final String P = "?p";
	public static final String SPACE = " ";
	public static final String LESS_SIGN = "<";
	public static final String GREAT_SIGN = ">";
	/**
	 * A resource URI example...
	 */
	public static final String COMPANY1_RESOURCE_EXAMPLE = "<http://managerAgentkobar.org/company#company1>";
	/**
	 * A triple pattern example...
	 */
	public static final String FIRST_TRIPLE_PATTERN = COMPANY1_RESOURCE_EXAMPLE
			+ SPACE + P + SPACE + O;
	/**
	 * A resource URI example...
	 */
	public static final String COMPANY2_RESOURCE_EXAMPLE = "<http://managerAgentkobar.org/company#company2>";
	/**
	 * A triple pattern example...
	 */
	public static final String SECOND_TRIPLE_PATTERN = O + SPACE + P + SPACE
			+ COMPANY2_RESOURCE_EXAMPLE;
	/**
	 * Propery URI of foaf:knows...
	 */
	public static final String PROPERTY_OF_FOAF_KNOWS = "<foaf:knows>";

	/**
	 * LinkedMDB void ontology URI.
	 */
	public static final String LINKED_MDB_MOVIE_ONTOLOGY_URI = "http://data.linkedmdb.org/resource/movie/";
	/**
	 * Geonames void ontology URI.
	 */
	public static final String GEONAMES_ONTOLOGY_URI = "http://www.geonames.org/ontology#";
	/**
	 * Seller agent ontology URI.
	 */
	public static final String SELLER_AGENT_ONTOLOGY_URI = "http://sellerAgenttest.org/void";

	/**
	 * Movie topic name.
	 */
	public static final Resource MOVIE_TOPIC = ResourceFactory
			.createResource("http://dbpedia.org/resource/Movie");
	/**
	 * LINKED_MDB common uri space.
	 */
	public static final Literal LINKED_MDB_URISPACE = ResourceFactory
			.createTypedLiteral("http://data.linkedmdb.org/resource/",
					XSDDatatype.XSDstring);
	/**
	 * LINKED_MDB example resource.
	 */
	public static final Resource LINKED_MDB_EXAMPLE_RSC = ResourceFactory
			.createResource(MOVIE_URI_INSTANCE);
	/**
	 * Movie vocabulary URI.
	 */
	public static final String MOVIE_VOCABULARY = "http://data.linkedmdb.org/resource/movie/";

	/**
	 * FOAF vocabulary URI.
	 */
	public static final String FOAF_VOCABULARY = "http://xmlns.com/foaf/0.1/";
	/**
	 * Geography topic resource.
	 */
	public static final RDFNode GEOGRAPHY_TOPIC = ResourceFactory
			.createResource("http://dbpedia.org/resource/Geography");
	/**
	 * Sale topic resource.
	 */
	public static final RDFNode SALE_TOPIC = ResourceFactory
			.createResource("http://dbpedia.org/resource/Sale");
	/**
	 * SALES vocabulary URI.
	 */
	public static final String SALES_VOCABULARY = "http://seagent.ege.edu.tr/sales.owl#";
	/**
	 * Geonames example resource instance.
	 */
	public static final Resource GEONAMES_EXAMPLE_RSC = ResourceFactory
			.createResource("http://sws.geonames.org/2985244/");
	/**
	 * Geonames example resource instance.
	 */
	public static final Resource GEODATA_EXAMPLE_RSC = ResourceFactory
			.createResource("http://linkedgeodata.org/triplify/user355347");
	/**
	 * GEO vocabulary URI.
	 */
	public static final String GEO_VOCABULARY = "http://www.geonames.org/ontology#";
	/**
	 * GEONAMES common urispace...
	 */
	public static final Literal GEONAMES_URISPACE = ResourceFactory
			.createTypedLiteral("http://sws.geonames.org/",
					XSDDatatype.XSDstring);
	/**
	 * Sales resource example...
	 */
	public static final Resource SALES_EXAMPLE_RSC = ResourceFactory
			.createResource("http://salesAgenttest.org/sales#indv0.9024");
	/**
	 * Sale property URI.
	 */
	public static final String SALE_PROPERTY = SALES_VOCABULARY + "sale";
	/**
	 * Nearby property of closureness of a place.
	 */
	public static final String NEARBY_PROPERTY = GEO_VOCABULARY + "nearby";
	/**
	 * Store variable.
	 */
	public static final String STORE_VARIABLE = "?storeVariable";
	/**
	 * hasStore property URI.
	 */
	public static final String HAS_STORE_PROPERTY = SALES_VOCABULARY
			+ "hasStore";
	/**
	 * Linked_mdb sparql endpoint URI.
	 */
	public static final String LINKED_MDB_SPARQL_ENDPOINT = "http://data.linkedmdb.org/sparql";
	/**
	 * Geonames sparql endpoint URI.
	 */
	public static final String GEONAMES_SPARQL_ENDPOINT_URL = "http://geosparql.appspot.com/query";
	/**
	 * Seller agent endpoint URI.
	 */
	public static final String MOVIE_SELLER_AGENT_ENDPOINT_URI = "http://sellerAgent.seagent/sparql/";
	public static final String DBPEDIA_ONTOLOGY_VOCABULARY = "http://dbpedia.org/ontology/";
	public static final String DBPEDIA_PROPERTY_VOCABULARY = "http://dbpedia.org/property/";

	public static final String GEODATA_PROPERTY_VOCABULARY = "http://linkedgeodata.org/property/";
	public static final String GEODATA_ONTOLOGY_VOCABULARY = "http://linkedgeodata.org/ontology/";

	public static final String COMPANY_INDIVIDUAL_RSC = "http://companyAgent@seagent.org/resource#own";
	public static final String DBPEDIA_ISTANBUL_RSC_URI = "http://dbpedia.org/resource/Istanbul";
	public static final Resource DBPEDIA_EXAMPLE_RESOURCE = ResourceFactory
			.createResource(DBPEDIA_ISTANBUL_RSC_URI);
	public static final Literal DBPEDIA_URISPACE_LITERAL = ResourceFactory
			.createPlainLiteral(DBPEDIA_URISPACE);
	public static final String PROPERTY_OF_LOCATED_IN = "http://companyTest.seagent.owl#Located_in";
	private static final String AGENT_FILM_SELLING_URI = SALES_VOCABULARY
			+ "sell";
	public static final Property AGENT_FILM_SELLING_PROPERTY = ResourceFactory
			.createProperty(AGENT_FILM_SELLING_URI);
	public static final String GENERAL_GEO_VOCABULARY = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static final String Ankara = "Ankara";
	public static final String Istanbul = "Istanbul";
	public static final String DBPEDIA_ENDPOINT_URL = "http://dbpedia.org/sparql";
	public static final String GEODATA_ENDPOINT_URL = "http://linkedgeodata.org/sparql";
	public static final Resource FACEBOOK_LIKES_PROPERTY = ResourceFactory
			.createProperty("http://155.223.25.235:8180/FLE/ontology/socsem.owl#likes");
	public static final Property RDF_TYPE_PROPERTY = ResourceFactory
			.createProperty(RDF_TYPE_URI);
	public static final String QUERY_SOLUTION_DBPEDIA = VOIDStoreConstants.VOID_STORE_PREFIX
			+ "dbpedia";
	public static final String QUERY_SOLUTION_CKAN_DBPEDIA = VOIDStoreConstants.VOID_STORE_PREFIX
			+ "http://thedatahub.org/dataset/dbpedia";

	public static final String QUERY_SOLUTION_GEODATA = "http://querySolution/geodata";
	public static final String QUERY_SOLUTION_CKAN_GEODATA = VOIDStoreConstants.VOID_STORE_PREFIX
			+ "http://thedatahub.org/dataset/linkedgeodata";
	public static final String AGENT_GEODATA_ENDPOINT_URL = "seagent://geodataAgent.seagent.org/sparql";
	public static final String QUERY_SOLUTION_AGENT_GEODATA = "http://querySolution/agentGeodata";
	public static final String QUERY_SOLUTION_PRODUCTDB = "http://querySolution/productdb";
	public static final String OPEN_VOCAB_ONTOLOGY_VOCABULARY = "http://open.vocab.org/terms/";
	public static final String GOODRELATIONS_ONTOLOGY_VOCABULARY = "http://purl.org/goodrelations/v1#";
	/**
	 * Simple agent name of the querior agent.
	 */
	public static final String QUERIER_AGENT_NAME = "queriorAgent";
	/**
	 * 
	 */
	public static final String AGENT_PRODUCTDB_QUERIOR_AGENT_ENDPOINT_URL = "http://"
			+ QUERIER_AGENT_NAME + ".seagent.org/sparql";
	/**
	 * 
	 */
	public static final String PRODUCTDB_QUERIOR_AGENT_VOCABULARY = "http://seagent.ege.edu.tr/seagent-schema#";
	public static final String QUERY_ONE_STATEMENT_FROM_DBPEDIA = "SELECT * WHERE {<http://dbpedia.org/resource/Bursa> ?p ?o.}";
	public static final String QUERY_SOLUTION_LINKEDMDB = "http://querySolution/linkedmdb";
	public static final String QUERY_SOLUTION_GEONAMES = "http://querySolution/geonames";
	public static final String QUERY_SOLUTION_JAMENDO = "http://querySolution/jamendo";
	public static final String QUERY_SOLUTION_NYTIMES = "http://querySolution/nytimes";
	public static final String QUERY_SOLUTION_KEGG_REACTIONS = "http://querySolution/keggreactions";
	public static final String QUERY_SOLUTION_KEGG_DRUGS = "http://querySolution/keggdrugs";
	public static final String NYTIMES_ENDPOINT_URL = "http://api.talis.com/stores/nytimes/services/sparql";
	public static final String NYTIMES_ONTOLOGY_URI = "http://data.nytimes.com/elements/";
	public static final String QUERY_SOLUTION_DRUGBANK = "http://querySolution/drugbank";
	public static final String DRUGBANK_ENDPOINT_URL = "http://www4.wiwiss.fu-berlin.de/drugbank/sparql";
	public static final String DRUGBANK_ONTOLOGY_URI = "http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/";
	public static final Resource DRUGBANK_EXAMPLE_RSC1 = ResourceFactory
			.createResource(DRUGBANK_DRUGS_URI + "DB00201");
	private static final String DRUGBANK_CATEGORY_URI = "http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/";
	public static final Resource DRUGBANK_EXAMPLE_RSC2 = ResourceFactory
			.createResource(DRUGBANK_CATEGORY_URI + "cathartics");
	public static final String QUERY_SOLUTION_KEGG_ENZYME = "http://querySolution/keggenzymes";
	public static final String KEGG_ONTOLOGY_URI = "http://bio2rdf.org/ns/kegg#";
	public static final String QUERY_SOLUTION_KEGG_COMPOUNDS = "http://querySolution/keggcompounds";
	public static final String QUERY_SOLUTION_CHEMICAL_ENTITIES = "http://querySolution/chemicalentities";
	public static final String CHEMICAL_ENTITIES_ONTOLOGY_URI = "http://bio2rdf.org/ns/bio2rdf#";
	public static final String KEGG_SPARQL_ENDPOINT_URL = "http://kegg.bio2rdf.org/sparql";
	public static final String KEGG_ONTOLOGY_URI2 = "http://bio2rdf.org/ns/bio2rdf#";
	public static final String DBPEDIA_DRUG_ONTOLOGY_VOCABULARY = "http://dbpedia.org/ontology/drug/";
	public static final String OWL_SAME_AS_URI = "http://www.w3.org/2002/07/owl#sameAs";
	public static final Resource OWL_SAME_AS_RSC = ResourceFactory
			.createResource(OWL_SAME_AS_URI);
	public static final String JAMENDO_ENDPOINT_URL = "http://dbtune.org/jamendo/sparql/";
	public static final String VOID_ONTOLOGY_URI = "http://vocab.deri.ie/void";
	public static final String NYTIMES_EXAMPLE_RESOURCE_URI = "http://data.nytimes.com/81558600101368540943";
	public static final Resource NYTIMES_EXAMPLE_RESOURCE = ResourceFactory
			.createResource(NYTIMES_EXAMPLE_RESOURCE_URI);
	private static final String KEGG_COMPOUND_ID_URI = "http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/keggCompounId";
	public static final Resource KEGG_COMPOUND_ID_RSC = ResourceFactory
			.createResource(KEGG_COMPOUND_ID_URI);
	private static final String DRUGBANK_GENERIC_NAME_URI = "http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/genericName";
	public static final Resource DRUGBANK_GENERIC_NAME_RSC = ResourceFactory
			.createResource(DRUGBANK_GENERIC_NAME_URI);
	private static final String X_SUBSTRATE_URI = "http://bio2rdf.org/ns/kegg#xSubstrate";
	public static final Resource X_SUBSTRATE_RSC = ResourceFactory
			.createResource(X_SUBSTRATE_URI);
	private static final String X_ENZYME_URI = "http://bio2rdf.org/ns/kegg#xEnzyme";
	public static final Resource X_ENZYME_RSC = ResourceFactory
			.createResource(X_ENZYME_URI);
	private static final String PURL_TITLE_URI = "http://purl.org/dc/elements/1.1/title";
	public static final Resource PURL_TITLE_RSC = ResourceFactory
			.createResource(PURL_TITLE_URI);
	public static final String QUERY_SOLUTION_DBPEDIA_ARTICLES = "http://querySolution/DBPediaArticles";
	public static final String ARTICLES_ENDPOINT_URL = "http://155.223.24.8:8893/articles";
	public static final String QUERY_SOLUTION_DBPEDIA_CATEGORIES = "http://querySolution/DBPediaCategories";
	public static final String CATEGORIES_ENDPOINT_URL = "http://155.223.24.8:8895/categories";
	public static final String QUERY_SOLUTION_DBPEDIA_INFOBOXES = "http://querySolution/Infoboxproperties";
	public static final String INFOBOXES_ENDPOINT_URL = "http://155.223.24.8:8894/infoboxes";
	public static final String QUERY_SOLUTION_DBPEDIA_PERSONS = "http://querySolution/Persons";
	public static final String PERSONS_ENDPOINT_URL = "http://155.223.24.8:8892/persondata";
	public static final String QUERY_SOLUTION_DBPEDIA_YAGO2 = "http://querySolution/YAGO2";
	public static final String YAGO2_ENDPOINT_URL = "http://155.223.24.8:8891/yago2";
	public static final Object GEONAMES_ENDPOINT_URL = "http://geosparql.appspot.com/query";
	public static final String QUERY_SOLUTION_DISEASOME = "http://querySolution/diseasome";
	public static final String DISEASOME_ENDPOINT_URL = "http://www4.wiwiss.fu-berlin.de/diseasome/sparql";
	public static final String VOID_LINKSET_URI = "http://rdfs.org/ns/void#Linkset";
	public static final String QUERY_SOLUTION_CKAN_LINKEDMDB = VOIDStoreConstants.VOID_STORE_PREFIX
			+ "http://thedatahub.org/dataset/linkedmdb";
	/**
	 * Urispace URL of facebook.
	 */
	private static final String FACEBOOK_URI_SPACE_URL = "http://socialsemantic.ege.edu.tr/facebook/individual#";
	/**
	 * Example resource of facebook.
	 */
	public static final Resource FACEBOOK_EXAMPLE_RSC = ResourceFactory
			.createResource(FACEBOOK_URI_SPACE_URL + "any");
	/**
	 * Urispace URL of foursquare
	 */
	private static final String FOURSQUARE_URI_SPACE_URL = "http://socialsemantic.ege.edu.tr/foursquare/individual#";
	/**
	 * Example resource of foursquare.
	 */
	public static final Resource FOURSQUARE_EXAMPLE_RSC = ResourceFactory
			.createResource(FOURSQUARE_URI_SPACE_URL + "any");
	/**
	 * Linkedin uri space URL.
	 */
	private static final String LINKED_URI_SPACE_URL = "http://socialsemantic.ege.edu.tr/linkedin/individual#";
	/**
	 * Linkedin example resource.
	 */
	public static final Resource LINKEDIN_EXAMPLE_RSC = ResourceFactory
			.createResource(LINKED_URI_SPACE_URL + "any");
	/**
	 * VOID prefix URI for queries.
	 */
	public static final String VOID_PREFIX_URI = "prefix void: <http://rdfs.org/ns/void#> ";
	/**
	 * RDF prefix URI.
	 */
	public static final String RDF_PREFIX_URI = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> ";
	public static final String GEODATA_URISPACE = "http://linkedgeodata.org/triplify/";
	public static final RDFNode GEODATA_URISPACE_LITERAL = ResourceFactory
			.createPlainLiteral(GEODATA_URISPACE);
	public static final String QUERY_SOLUTION_FACEBOOK_DATA = VOIDStoreConstants.VOID_STORE_PREFIX
			+ "FacebookData";
	public static final String AMAZON_FACEBOOK_ENDPOINT_URL = "http://23.23.193.63:8080/joseki/service/facebookData";
	public static final String FACEBOOK_VOCABULARY = "http://155.223.25.235:8180/FLE/ontology/socsem.owl#";
	public static final String SEAGENTDEV_FACEBOOK_ENDPOINT_URL = "http://155.223.25.235:8180/joseki/service/facebookData";

}
