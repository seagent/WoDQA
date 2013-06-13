package util;

import tr.edu.ege.seagent.wodqa.VOIDStoreConstants;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class TestConstants {

	private static final String LINKEDIN_PATTERN = "linkedin";
	private static final String FOURSQUARE_PATTERN = "foursquare";
	private static final String FACEBOOK_PATTERN = "facebook";
	private static final String RESOURCE_PATTERN = "/resource/";
	private static final String RESOURCE_BASE_URI = "http://155.223.25.235:8180/dataset/socialsemantic/";
	public static final String FACEBOOK_VOID_URI = VOIDStoreConstants.VOID_STORE_PREFIX
			+ FACEBOOK_PATTERN;
	public static final String FACEBOOK_ENDPOINT_URL = "http://155.223.25.235:8180/joseki/service/facebook";
	public static final String FACEBOOK_VOCABULARY = "http://155.223.25.235:8180/sosep/ontology/semanticFB.owl#";
	public static final String FOURSQUARE_ENDPOINT_URL = "http://155.223.25.235:8180/joseki/service/foursquare";
	public static final String FOURSQUARE_VOID_URI = VOIDStoreConstants.VOID_STORE_PREFIX
			+ FOURSQUARE_PATTERN;
	public static final String FOURSQUARE_VOCABULARY = "http://155.223.25.235:8180/sosep/ontology/semanticFS.owl#";
	public static final String LINKEDIN_ENDPOINT_URL = "http://155.223.25.235:8180/joseki/service/linkedin";
	public static final String LINKEDIN_VOID_URI = VOIDStoreConstants.VOID_STORE_PREFIX
			+ LINKEDIN_PATTERN;
	public static final String LINKEDIN_VOCABULARY = "http://155.223.25.235:8180/sosep/ontology/semanticLI.owl#";

	public static final String FACEBOOK_URI_SPACE = RESOURCE_BASE_URI
			+ FACEBOOK_PATTERN + RESOURCE_PATTERN;
	public static final String FOURSQUARE_URI_SPACE = RESOURCE_BASE_URI
			+ FOURSQUARE_PATTERN + RESOURCE_PATTERN;
	public static final String LINKEDIN_URI_SPACE = RESOURCE_BASE_URI
			+ LINKEDIN_PATTERN + RESOURCE_PATTERN;
	public static final Literal FACEBOOK_URI_SPACE_LITERAL = ResourceFactory
			.createPlainLiteral(FACEBOOK_URI_SPACE);
	public static final Literal FOURSQUARE_URI_SPACE_LITERAL = ResourceFactory
			.createPlainLiteral(FOURSQUARE_URI_SPACE);
	public static final Literal LINKEDIN_URI_SPACE_LITERAL = ResourceFactory
			.createPlainLiteral(LINKEDIN_URI_SPACE);
}
