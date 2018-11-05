package tr.edu.ege.seagent.wodqa.voiddocument;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class ExampleVocabulary {

	public static final String DBPEDIA_SPARQL_ENDPOINT = "http://dbpedia.org/sparql";
	public static final String DBPEDIA_URISPACE = "http://dbpedia.org/resource/";
	public static final String DBPEDIA_ONTOLOGY_PROP = "http://dbpedia.org/ontology/";
	public static final String DBPEDIA_PROPERTY_PROP = "http://dbpedia.org/property/";
	public static final String GEODATA_EXAMPLE_RESOURCE_URI = "http://linkedgeodata.org/triplify/";
	public static final String WIKIMEDIA_URISPACE = "http://upload.wikimedia.org/wikipedia/commons/thumb/";
	public static final Property OWL_SAMEAS_RSC = ResourceFactory
			.createProperty("http://www.w3.org/2002/07/owl#sameAs");
	public static final String LINKED_MDB_VOC = "http://data.linkedmdb.org/resource/movie/";
	public static final String LINKED_MDB_URI_SPACE = "http://data.linkedmdb.org/resource/";
	public static final String GEOPOSITION_VOC = "http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static final String CREATIVE_COMMONS_VOC = "http://creativecommons.org/ns#";
	public static final String DC_TERMS_VOC = "http://purl.org/dc/terms/";
	public static final String GEONAMES_VOC = "http://www.geonames.org/ontology#";
	public static final String GEONAMES_URI_SPACE = "http://sws.geonames.org/";
	public static final String CHEBI_URI_SPACE = "http://bio2rdf.org/";
	public static final String KEGG_URI_SPACE = "http://bio2rdf.org/";
	public static final String DRUGBANK_URI_SPACE = "http://www4.wiwiss.fu-berlin.de/drugbank/";
	public static final String JAMENDO_URI_SPACE = "http://dbtune.org/jamendo/";
	public static final String SW_DOGFOOD_URI_SPACE = "http://data.semanticweb.org/";
	public static final String NYTIMES_URI_SPACE = "http://data.nytimes.com/";
	public static final String SP2B_URI_SPACE = "http://localhost/publications/";
	public static final String SP2B_URI_SPACE_1 = "http://localhost/publications/inprocs/";
	public static final String SP2B_URI_SPACE_2 = "http://localhost/publications/articles/";
	public static final String SP2B_VOC = "http://localhost/vocabulary/bench/";

}
