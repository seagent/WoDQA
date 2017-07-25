package util;

import tr.edu.ege.seagent.wodqa.QueryVocabulary;
import tr.edu.ege.seagent.wodqa.SocialDatasetsExampleVocabulary;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class QueryExampleVocabulary {

	public static String reasonableDBPediaUnionQuery = "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
			+ "SELECT ?predicate ?object WHERE {"
			+ "{?subject dbpedia:sameAs dbpedia:Barack_Obama.}"
			+ "UNION {"
			+ "?subject dbpedia:sameAs dbpedia:Barack_Obama."
			+ "?subject ?predicate ?object.}}";
	public static String unreasonableUnionQuery = "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
			+ "SELECT ?predicate ?object WHERE {"
			+ "{?subject dbpedia:sameAs dbpedia:Barack_Obama.}"
			+ "UNION {"
			+ "?subject ?predicate ?object.}}";
	public static String SIMPLE_DBPEDIA_QUERY = "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
			+ "CONSTRUCT {dbpedia:Barack_Obama ?predicate ?object} WHERE "
			+ "{dbpedia:Barack_Obama ?predicate ?object.}";
	/**
	 * Query to find a movie seller...
	 */
	public static final String MOVIE_SELLER_QUERY = QueryVocabulary.SELECT_ALL
			+ QueryVocabulary.SPACE + QueryVocabulary.WHERE
			+ QueryVocabulary.SPACE + QueryVocabulary.START
			+ QueryVocabulary.SALES_PERSON_VARIABLE + QueryVocabulary.SPACE
			+ QueryVocabulary.LESS_SIGN + QueryVocabulary.SALE_PROPERTY
			+ QueryVocabulary.GREAT_SIGN + QueryVocabulary.SPACE
			+ QueryVocabulary.LESS_SIGN + QueryVocabulary.MOVIE_URI_INSTANCE
			+ QueryVocabulary.GREAT_SIGN + QueryVocabulary.DOT
			+ QueryVocabulary.SPACE + QueryVocabulary.STORE_VARIABLE
			+ QueryVocabulary.SPACE + QueryVocabulary.LESS_SIGN
			+ QueryVocabulary.NEARBY_PROPERTY + QueryVocabulary.GREAT_SIGN
			+ QueryVocabulary.SPACE + QueryVocabulary.LESS_SIGN
			+ QueryVocabulary.GEONAMES_EXAMPLE_RSC + QueryVocabulary.GREAT_SIGN
			+ QueryVocabulary.DOT + QueryVocabulary.SPACE
			+ QueryVocabulary.SALES_PERSON_VARIABLE + QueryVocabulary.SPACE
			+ QueryVocabulary.LESS_SIGN + QueryVocabulary.HAS_STORE_PROPERTY
			+ QueryVocabulary.GREAT_SIGN + QueryVocabulary.SPACE
			+ QueryVocabulary.STORE_VARIABLE + QueryVocabulary.DOT
			+ QueryVocabulary.END;
	/**
	 * Query with one statement.
	 */
	public static final String ONE_STATEMENT_QUERY = QueryVocabulary.SELECT_ALL
			+ QueryVocabulary.SPACE + QueryVocabulary.WHERE
			+ QueryVocabulary.SPACE + QueryVocabulary.START
			+ QueryVocabulary.FIRST_TRIPLE_PATTERN + QueryVocabulary.DOT
			+ QueryVocabulary.END;
	/**
	 * Query with two statement.
	 */
	public static final String TWO_STATEMENT_QUERY = QueryVocabulary.SELECT_ALL
			+ QueryVocabulary.SPACE + QueryVocabulary.WHERE
			+ QueryVocabulary.SPACE + QueryVocabulary.START
			+ QueryVocabulary.FIRST_TRIPLE_PATTERN + QueryVocabulary.DOT
			+ QueryVocabulary.SPACE + QueryVocabulary.SECOND_TRIPLE_PATTERN
			+ QueryVocabulary.DOT + QueryVocabulary.END;
	public static final String CLOSER_HOTELS_TO_THE_AIRPORT_IN_GIVEN_CITY_QUERY_WITH_SERVICE = "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
			+ "PREFIX lgdo: <http://linkedgeodata.org/ontology/>"
			+ "SELECT DISTINCT * WHERE {"
			+ "SERVICE <http://dbpedia.org/sparql>{"
			+ "?city rdf:type dbpedia-owl:Place."
			+ "?city rdfs:label \""
			+ QueryVocabulary.Istanbul
			+ "\"@en."
			+ "?airport dbpedia-owl:city ?city."
			+ "?airport rdf:type dbpedia-owl:Airport."
			+ "?airport geo:lat ?airLat."
			+ "?airport geo:long ?airLong.}"
			+ "SERVICE <http://linkedgeodata.org/sparql>{"
			+ "?hotel lgdo:directType lgdo:TourismHotel."
			+ "?hotel geo:lat ?hotelLat."
			+ "?hotel geo:long ?hotelLong."
			+ "?hotel rdfs:label ?HotelName."
			+ "FILTER(?hotelLat-?airLat<0.4 && ?hotelLat-?airLat>-0.4 && ?hotelLong-?airLong>-0.4 && ?hotelLong-?airLong<0.4)"
			+ "}}";
	public static final String CLOSER_HOTELS_TO_THE_AIRPORT_IN_GIVEN_CITY_SELECT_QUERY = "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
			+ "PREFIX lgdo: <http://linkedgeodata.org/ontology/>"
			+ "SELECT DISTINCT * WHERE {"
			+ "?city rdf:type dbpedia-owl:Place."
			+ "?city rdfs:label \""
			+ QueryVocabulary.Istanbul
			+ "\"@en."
			+ "?airport dbpedia-owl:city ?city."
			+ "?airport rdf:type dbpedia-owl:Airport."
			+ "?airport geo:lat ?airLat."
			+ "?airport geo:long ?airLong."
			+ "?hotel lgdo:directType lgdo:TourismHotel."
			+ "?hotel geo:lat ?hotelLat."
			+ "?hotel geo:long ?hotelLong."
			+ "?hotel rdfs:label ?HotelName."
			+ "FILTER(?hotelLat-?airLat<0.4 && ?hotelLat-?airLat>-0.4 && ?hotelLong-?airLong>-0.4 && ?hotelLong-?airLong<0.4)"
			+ "}";
	public static final String CLOSER_HOTELS_TO_THE_AIRPORT_IN_GIVEN_CITY_CONSTRUCT_QUERY = "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
			+ "PREFIX lgdo: <http://linkedgeodata.org/ontology/>"
			+ "CONSTRUCT {"
			+ "?city rdf:type dbpedia-owl:Place."
			+ "?city rdfs:label \""
			+ QueryVocabulary.Istanbul
			+ "\"@en."
			+ "?airport dbpedia-owl:city ?city."
			+ "?airport rdf:type dbpedia-owl:Airport."
			+ "?airport geo:lat ?airLat."
			+ "?airport geo:long ?airLong."
			+ "?hotel lgdo:directType lgdo:TourismHotel."
			+ "?hotel geo:lat ?hotelLat."
			+ "?hotel geo:long ?hotelLong."
			+ "?hotel rdfs:label ?HotelName."
			+ "} WHERE {"
			+ "?city rdf:type dbpedia-owl:Place."
			+ "?city rdfs:label \""
			+ QueryVocabulary.Istanbul
			+ "\"@en."
			+ "?airport dbpedia-owl:city ?city."
			+ "?airport rdf:type dbpedia-owl:Airport."
			+ "?airport geo:lat ?airLat."
			+ "?airport geo:long ?airLong."
			+ "?hotel lgdo:directType lgdo:TourismHotel."
			+ "?hotel geo:lat ?hotelLat."
			+ "?hotel geo:long ?hotelLong."
			+ "?hotel rdfs:label ?HotelName."
			+ "FILTER(?hotelLat-?airLat<0.4 && ?hotelLat-?airLat>-0.4 && ?hotelLong-?airLong>-0.4 && ?hotelLong-?airLong<0.4)"
			+ "}";
	public static final String FILM_SELLER_QUERY = "PREFIX agent:<"
			+ QueryVocabulary.SALES_VOCABULARY
			+ "> SELECT * WHERE {?store agent:sell <http://data.linkedmdb.org/page/film/1028>. ?store agent:in ?city. ?city <http://linkedgeodata.org/property/is_in> \"Turkey\"}";
	public static final String LINKSET_PATTERN_1 = "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
			+ "SELECT ?predicate ?object WHERE {"
			+ "?subject owl:sameAs dbpedia:Barack_Obama."
			+ "?subject ?predicate ?object.}";
	public static final String LINKSET_PATTERN_2 = "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
			+ "SELECT ?predicate ?object2 WHERE {"
			+ "?subject owl:sameAs ?object." + "?subject ?predicate ?object2.}";
	public static final String LINKSET_PATTERN_3 = "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
			+ "SELECT ?predicate ?object2 WHERE {"
			+ "?subject owl:sameAs ?object." + "?object ?predicate ?object2.}";
	public static final String LINKSET_PATTERN_4 = "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
			+ "SELECT ?predicate ?object2 WHERE {"
			+ "<"
			+ QueryVocabulary.NYTIMES_EXAMPLE_RESOURCE_URI
			+ "> owl:sameAs ?object." + "?object ?predicate ?object2.}";
	public static final String LINKSET_PATTERN_5 = "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/>  "
			+ "SELECT * WHERE {"
			+ "?jamendoResource foaf:name ?o."
			+ "?subject owl:sameAs ?jamendoResource.}";
	public static final String LINKSET_PATTERN_6 = "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "SELECT * WHERE {"
			+ "<"
			+ QueryVocabulary.LINKED_MDB_EXAMPLE_RSC.getURI()
			+ "> owl:sameAs ?dbpediaResource."
			+ "?dbpediaResource owl:sameAs ?x."
			+ "?z owl:sameAs ?x. ?x ?p ?o.}";
	public static final String LINKSET_PATTERN_7 = "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "SELECT * WHERE {"
			+ "dbpedia:Berlin owl:sameAs ?x."
			+ "?z owl:sameAs ?x. ?x ?p ?o.}";
	public static final String COUNTRIES_IN_EUROPE_CONSTRUCT_QUERY = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "Prefix lgdo: <http://linkedgeodata.org/ontology/>"
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
			+ "CONSTRUCT {?country rdf:type lgdo:Country. ?country <http://linkedgeodata.org/property/is_in> \"Europe\". ?country owl:sameAs ?countryInDbpedia. ?countryInDbpedia ?properties ?o2.} WHERE {"
			+ "?country rdf:type lgdo:Country."
			+ "?country <http://linkedgeodata.org/property/is_in> \"Europe\"."
			+ "?country owl:sameAs ?countryInDbpedia."
			+ "?countryInDbpedia ?properties ?o2." + "}";
	public static final String NEAREST_AIRPORTS_TO_THE_GIVEN_CITY_SELECT_QUERY = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "Prefix lgdo: <http://linkedgeodata.org/ontology/>"
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
			+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>"
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/>"
			+ "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>"
			+ "SELECT DISTINCT ?dbpediaAirport ?props ?values WHERE {"
			+ "<http://dbpedia.org/resource/Edinburgh> geo:long ?cityLong."
			+ "<http://dbpedia.org/resource/Edinburgh> geo:lat ?cityLat."
			+ "?airport rdf:type lgdo:Airport."
			+ "?airport geo:long ?airLong."
			+ "?airport geo:lat ?airLat."
			+ "?airport owl:sameAs ?dbpediaAirport."
			+ "?dbpediaAirport ?props ?values."
			+ "FILTER(?cityLat-?airLat<1.5 && ?cityLat-?airLat>-1.5 && ?cityLong-?airLong>-1.5 && ?cityLong-?airLong<1.5)"
			+ "}";
	public static final String NEAREST_AIRPORTS_TO_THE_GIVEN_CITY_CONSTRUCT_QUERY = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "Prefix lgdo: <http://linkedgeodata.org/ontology/>"
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
			+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>"
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/>"
			+ "PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>"
			+ "CONSTRUCT {?dbpediaAirport ?props ?values} WHERE {"
			+ "<http://dbpedia.org/resource/Edinburgh> geo:long ?cityLong."
			+ "<http://dbpedia.org/resource/Edinburgh> geo:lat ?cityLat."
			+ "?airport rdf:type lgdo:Airport."
			+ "?airport geo:long ?airLong."
			+ "?airport geo:lat ?airLat."
			+ "?airport owl:sameAs ?dbpediaAirport."
			+ "?dbpediaAirport ?props ?values."
			+ "FILTER(?cityLat-?airLat<1.5 && ?cityLat-?airLat>-1.5 && ?cityLong-?airLong>-1.5 && ?cityLong-?airLong<1.5)"
			+ "}";
	public static final String SIMPLE_GEODATA_QUERY = "CONSTRUCT {<http://linkedgeodata.org/triplify/node10666817> ?d ?s.} WHERE {<http://linkedgeodata.org/triplify/node10666817> ?d ?s.}";
	/**
	 * DBPedia Edinburgh airport URI.
	 */
	public static final String DBPEDIA_EDINBURGH_RSC_URI = "http://dbpedia.org/resource/Edinburgh_Airport";
	/**
	 * DBPedia Edinburgh airport resource.
	 */
	public static final Resource DBPEDIA_EDINBURGH_RSC = ResourceFactory
			.createResource(QueryExampleVocabulary.DBPEDIA_EDINBURGH_RSC_URI);
	public static String LIFE_SCIENCE_7_OPTIONAL = "PREFIX drugbank: <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/> "
			+ "PREFIX bio2rdf: <http://bio2rdf.org/ns/bio2rdf#> "
			+ "SELECT ?drug ?transform ?mass WHERE {"
			+ "?drug drugbank:affectedOrganism \"Humans and other mammals\"."
			+ "?drug drugbank:casRegistryNumber ?cas."
			+ "?keggDrug bio2rdf:xRef ?cas."
			+ "?keggDrug bio2rdf:mass ?mass FILTER (?mass > \"5\") "
			+ "OPTIONAL  {?drug drugbank:biotransformation ?transform.} }";
	public static final String QUERY_GERMAN_FILM_PRODUCER = "PREFIX owl: <http://www.w3.org/2002/07/owl#> PREFIX dbpo: <http://dbpedia.org/property/>"
			+ " PREFIX linkedMDB: <http://data.linkedmdb.org/resource/movie/>"
			+ "PREFIX facebook: <"
			+ SocialDatasetsExampleVocabulary.SOCSEM_ONTOLOGY_URI
			+ ">"
			+ "PREFIX dbpedia: <http://dbpedia.org/resource/>"
			+ " SELECT ?faceUser ?movie WHERE {"
			+ "?faceUser facebook:likes ?movie."
			+ "?dbProducer dbpo:birthPlace dbpedia:Germany."
			+ "?anyMovie dbpo:producer ?dbProducer. "
			+ "?dbProducer owl:sameAs ?producer."
			+ "?movie linkedMDB:producer ?producer. " + "}";

}
