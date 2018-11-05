package tr.edu.ege.seagent.wodqa.voiddocument;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * 
 * This class includes VoiD vocabulary constants.
 * 
 */
public class AvoidOntologyVocabulary {

	/**
	 * URI of the VOID schema.
	 */
	public static final String VOID_URI = "http://rdfs.org/ns/void";

	/**
	 * Void prefix.
	 */
	private static final String voidPrefix = VOID_URI + "#";

	/**
	 * Dataset class URI.
	 */
	public static final String DATASET = voidPrefix + "Dataset";

	/**
	 * Seagent avoid schema prefix URI.
	 */
	private static final String SEAGENT_AVOID_PREFIX = "http://seagent.ege.edu.tr/avoidSchema#";
	/**
	 * Dataset agent name URI.
	 */
	public static final String DATASET_AGENTNAME = SEAGENT_AVOID_PREFIX
			+ "Dataset.Agentname";

	/**
	 * URI of the avoid schema.
	 */
	public static final String URI = "http://etmen.ege.edu.tr/etmen/ontologies/avoid.owl";

	/**
	 * Dataset subject property URI.
	 */
	public static final String DATASET_SUBJECT = "http://purl.org/dc/terms/subject";

	/**
	 * Agent Name property of Dataset concept.
	 */
	public static final Property DATASET_agentname_prp = ResourceFactory
			.createProperty(DATASET_AGENTNAME);

	/**
	 * Topic property of dataset concept.
	 */
	public static final Property DATASET_topic_prp = ResourceFactory
			.createProperty(DATASET_SUBJECT);

}
