package tr.edu.ege.seagent.wodqa.voiddocument;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

/**
 * Class to access all concepts and properties in VOID ontology.
 */
public class VOIDOntologyVocabulary {
	/**
	 * FOAF ontology URI.
	 */
	public static final String FOAF_ONTOLOGY_URI = "http://xmlns.com/foaf/0.1/";

	/**
	 * Dublin Core ontology URI
	 */
	public static final String DUBLIN_CORE_ONTOLOGY_URI = "http://purl.org/dc/terms/";

	/**
	 * SCOVO ontology URI.
	 */
	public static final String SCOVO_ONTOLOGY_URI = "http://purl.org/NET/scovo";

	/**
	 * AKT ontology URI.
	 */
	public static final String AKT_ONTOLOGY_URI = "http://www.aktors.org/ontology/portal";

	/**
	 * VOID ontology URI.
	 */
	public static final String VOID_ONTOLOGY_URI = "http://rdfs.org/ns/void";

	/**
	 * URI for defined concepts.
	 */
	public static final String DEF_URI = VOID_ONTOLOGY_URI + "#";

	/**
	 * Class URI of Dataset concept.
	 */
	public static final String DATASET = DEF_URI + "Dataset";

	/**
	 * Resource of Dataset concept.
	 */
	public static final Resource DATASET_rsc = ResourceFactory
			.createResource(DATASET);

	/**
	 * Property URI of sparqlEndpoint of Dataset concept.
	 */
	public static final String DATASET_sparqlEndpoint = DEF_URI
			+ "sparqlEndpoint";

	/**
	 * sparqlEndpoint property of Dataset concept.
	 */
	public static final Property DATASET_sparqlEndpoint_prp = ResourceFactory
			.createProperty(DATASET_sparqlEndpoint);

	/**
	 * Property URI of vocabulary of Dataset concept.
	 */
	public static final String DATASET_vocabulary = DEF_URI + "vocabulary";

	/**
	 * vocabulary property of Dataset concept.
	 */
	public static final Property DATASET_vocabulary_prp = ResourceFactory
			.createProperty(DATASET_vocabulary);

	/**
	 * Property URI of class of Dataset concept.
	 */
	public static final String DATASET_class = DEF_URI + "class";

	/**
	 * class property of Dataset concept.
	 */
	public static final Property DATASET_class_prp = ResourceFactory
			.createProperty(DATASET_class);

	/**
	 * Property URI of classPartition of Dataset concept.
	 */
	public static final String DATASET_classPartition = DEF_URI
			+ "classPartition";

	/**
	 * classPartition property of Dataset concept.
	 */
	public static final Property DATASET_classPartition_prp = ResourceFactory
			.createProperty(DATASET_classPartition);

	/**
	 * Property URI of classes of Dataset concept.
	 */
	public static final String DATASET_classes = DEF_URI + "classes";

	/**
	 * classes property of Dataset concept.
	 */
	public static final Property DATASET_classes_prp = ResourceFactory
			.createProperty(DATASET_classes);

	/**
	 * Property URI of dataDump of Dataset concept.
	 */
	public static final String DATASET_dataDump = DEF_URI + "dataDump";

	/**
	 * dataDump property of Dataset concept.
	 */
	public static final Property DATASET_dataDump_prp = ResourceFactory
			.createProperty(DATASET_dataDump);

	/**
	 * Property URI of distinctObjects of Dataset concept.
	 */
	public static final String DATASET_distinctObjects = DEF_URI
			+ "distinctObjects";

	/**
	 * distinctObjects property of Dataset concept.
	 */
	public static final Property DATASET_distinctObjects_prp = ResourceFactory
			.createProperty(DATASET_distinctObjects);

	/**
	 * Property URI of distinctSubjects of Dataset concept.
	 */
	public static final String DATASET_distinctSubjects = DEF_URI
			+ "distinctSubjects";

	/**
	 * distinctSubjects property of Dataset concept.
	 */
	public static final Property DATASET_distinctSubjects_prp = ResourceFactory
			.createProperty(DATASET_distinctSubjects);

	/**
	 * Property URI of documents of Dataset concept.
	 */
	public static final String DATASET_documents = DEF_URI + "documents";

	/**
	 * documents property of Dataset concept.
	 */
	public static final Property DATASET_documents_prp = ResourceFactory
			.createProperty(DATASET_documents);

	/**
	 * Property URI of entities of Dataset concept.
	 */
	public static final String DATASET_entities = DEF_URI + "entities";

	/**
	 * entities property of Dataset concept.
	 */
	public static final Property DATASET_entities_prp = ResourceFactory
			.createProperty(DATASET_entities);

	/**
	 * Property URI of exampleResource of Dataset concept.
	 */
	public static final String DATASET_exampleResource = DEF_URI
			+ "exampleResource";

	/**
	 * exampleResource property of Dataset concept.
	 */
	public static final Property DATASET_exampleResource_prp = ResourceFactory
			.createProperty(DATASET_exampleResource);

	/**
	 * Property URI of feature of Dataset concept.
	 */
	public static final String DATASET_feature = DEF_URI + "feature";

	/**
	 * feature property of Dataset concept.
	 */
	public static final Property DATASET_feature_prp = ResourceFactory
			.createProperty(DATASET_feature);

	/**
	 * Property URI of openSearchDescription of Dataset concept.
	 */
	public static final String DATASET_openSearchDescription = DEF_URI
			+ "openSearchDescription";

	/**
	 * openSearchDescription property of Dataset concept.
	 */
	public static final Property DATASET_openSearchDescription_prp = ResourceFactory
			.createProperty(DATASET_openSearchDescription);

	/**
	 * Property URI of properties of Dataset concept.
	 */
	public static final String DATASET_properties = DEF_URI + "properties";

	/**
	 * properties property of Dataset concept.
	 */
	public static final Property DATASET_properties_prp = ResourceFactory
			.createProperty(DATASET_properties);

	/**
	 * Property URI of property of Dataset concept.
	 */
	public static final String DATASET_property = DEF_URI + "property";

	/**
	 * property property of Dataset concept.
	 */
	public static final Property DATASET_property_prp = ResourceFactory
			.createProperty(DATASET_property);

	/**
	 * Property URI of propertyPartition of Dataset concept.
	 */
	public static final String DATASET_propertyPartition = DEF_URI
			+ "propertyPartition";

	/**
	 * propertyPartition property of Dataset concept.
	 */
	public static final Property DATASET_propertyPartition_prp = ResourceFactory
			.createProperty(DATASET_propertyPartition);

	/**
	 * Property URI of rootResource of Dataset concept.
	 */
	public static final String DATASET_rootResource = DEF_URI + "rootResource";

	/**
	 * rootResource property of Dataset concept.
	 */
	public static final Property DATASET_rootResource_prp = ResourceFactory
			.createProperty(DATASET_rootResource);

	/**
	 * Property URI of subset of Dataset concept.
	 */
	public static final String DATASET_subset = DEF_URI + "subset";

	/**
	 * subset property of Dataset concept.
	 */
	public static final Property DATASET_subset_prp = ResourceFactory
			.createProperty(DATASET_subset);

	/**
	 * Property URI of triples of Dataset concept.
	 */
	public static final String DATASET_triples = DEF_URI + "triples";

	/**
	 * triples property of Dataset concept.
	 */
	public static final Property DATASET_triples_prp = ResourceFactory
			.createProperty(DATASET_triples);

	/**
	 * Property URI of uriLookupEndpoint of Dataset concept.
	 */
	public static final String DATASET_uriLookupEndpoint = DEF_URI
			+ "uriLookupEndpoint";

	/**
	 * uriLookupEndpoint property of Dataset concept.
	 */
	public static final Property DATASET_uriLookupEndpoint_prp = ResourceFactory
			.createProperty(DATASET_uriLookupEndpoint);

	/**
	 * Property URI of uriRegexPattern of Dataset concept.
	 */
	public static final String DATASET_uriRegexPattern = DEF_URI
			+ "uriRegexPattern";

	/**
	 * uriRegexPattern property of Dataset concept.
	 */
	public static final Property DATASET_uriRegexPattern_prp = ResourceFactory
			.createProperty(DATASET_uriRegexPattern);

	/**
	 * Property URI of uriSpace of Dataset concept.
	 */
	public static final String DATASET_uriSpace = DEF_URI + "uriSpace";

	/**
	 * uriSpace property of Dataset concept.
	 */
	public static final Property DATASET_uriSpace_prp = ResourceFactory
			.createProperty(DATASET_uriSpace);

	/**
	 * Class URI of Linkset concept.
	 */
	public static final String LINKSET = DEF_URI + "Linkset";

	/**
	 * Resource of Linkset concept.
	 */
	public static final Resource LINKSET_rsc = ResourceFactory
			.createResource(LINKSET);

	/**
	 * Property URI of linkPredicate of Linkset concept.
	 */
	public static final String LINKSET_linkPredicate = DEF_URI
			+ "linkPredicate";

	/**
	 * linkPredicate property of Linkset concept.
	 */
	public static final Property LINKSET_linkPredicate_prp = ResourceFactory
			.createProperty(LINKSET_linkPredicate);

	/**
	 * Property URI of objectsTarget of Linkset concept.
	 */
	public static final String LINKSET_objectsTarget = DEF_URI
			+ "objectsTarget";

	/**
	 * objectsTarget property of Linkset concept.
	 */
	public static final Property LINKSET_objectsTarget_prp = ResourceFactory
			.createProperty(LINKSET_objectsTarget);

	/**
	 * Property URI of subjectsTarget of Linkset concept.
	 */
	public static final String LINKSET_subjectsTarget = DEF_URI
			+ "subjectsTarget";

	/**
	 * subjectsTarget property of Linkset concept.
	 */
	public static final Property LINKSET_subjectsTarget_prp = ResourceFactory
			.createProperty(LINKSET_subjectsTarget);

	/**
	 * Property URI of target of Linkset concept.
	 */
	public static final String LINKSET_target = DEF_URI + "target";

	/**
	 * target property of Linkset concept.
	 */
	public static final Property LINKSET_target_prp = ResourceFactory
			.createProperty(LINKSET_target);

	/**
	 * Class URI of DatasetDescription concept.
	 */
	public static final String DATASETDESCRIPTION = DEF_URI
			+ "DatasetDescription";

	/**
	 * Resource of DatasetDescription concept.
	 */
	public static final Resource DATASETDESCRIPTION_rsc = ResourceFactory
			.createResource(DATASETDESCRIPTION);

	/**
	 * Class URI of TechnicalFeature concept.
	 */
	public static final String TECHNICALFEATURE = DEF_URI + "TechnicalFeature";

	/**
	 * Resource of TechnicalFeature concept.
	 */
	public static final Resource TECHNICALFEATURE_rsc = ResourceFactory
			.createResource(TECHNICALFEATURE);

	/**
	 * Class URI of FoafDoc concept.
	 */
	public static final String FOAFDOC = "http://xmlns.com/foaf/spec/Document";

	/**
	 * Property URI of inDataset of FoafDoc concept.
	 */
	public static final String FOAF_inDataset = DEF_URI + "inDataset";

	/**
	 * inDataset property of FoafDoc concept.
	 */
	public static final Property FOAFDOC_inDataset_prp = ResourceFactory
			.createProperty(FOAF_inDataset);

	/**
	 * Queried model URI.
	 */
	public static final String QUERY_MODEL_URI = "http://querySolution";

	public static final String VOID_STORE_SPARQL_SERVICE_URI = "http://void.rkbexplorer.com/sparql/";

	public static final String CKAN_SERVICE = "http://semantic.ckan.net/sparql";

	public static final String DBPEDIA_SERVICE = "http://dbpedia.org/sparql";

}
