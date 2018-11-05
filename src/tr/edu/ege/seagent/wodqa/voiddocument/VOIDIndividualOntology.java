package tr.edu.ege.seagent.wodqa.voiddocument;

import java.io.File;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

/**
 * Class to crates individuals of all concepts in VOID ontology.
 */
public class VOIDIndividualOntology {

	/**
	 * Ontology model which includes schema ontology.
	 */
	private OntModel ontModel = null;

	/**
	 * URI of the individual ontology.
	 */
	private String ontURI = null;

	/**
	 * List of primitive types.
	 */
	private Map<Class<?>, XSDDatatype> primitiveTypes;

	/**
	 * Creates a new instance of the individual ontology with given ontology URI and
	 * ontology model that includes/imports schema ontology.
	 * 
	 * @param ontURI           URI of the individual ontology.
	 * @param voidIndvOntModel ontology model to create and read individuals. The
	 *                         schema ontology must be already imported/included.
	 */
	public VOIDIndividualOntology(String ontURI, OntModel voidIndvOntModel) {
		this.ontURI = ontURI;
		this.ontModel = voidIndvOntModel;
	}

	/**
	 * Creates a new instance of the individual ontology with given ontology URI and
	 * ontology model.
	 * 
	 * @param ontURI           URI of the individual ontology.
	 * @param voidIndvOntModel ontology model to create and read individuals. This
	 *                         method imports the schema ontology automatically.
	 * @param schemaLocation   if the schema ontology is not available on the
	 *                         Internet, it is read from this location. If it is
	 *                         available, give the schema location
	 *                         <code>null</code>.
	 */
	public VOIDIndividualOntology(String ontURI, OntModel voidIndvOntModel, File schemaLocation)
			throws MalformedURLException {
		this(ontURI, voidIndvOntModel);
		// create ontology
		Ontology ontology = this.ontModel.createOntology(this.ontURI);

		String URItoImport = VOIDOntologyVocabulary.DEF_URI;

		// add schema to document manager, if required.
		if (schemaLocation != null) {
			this.ontModel.getDocumentManager().addAltEntry(URItoImport, schemaLocation.toURI().toURL().toString());
		}

		// import schema
		this.ontModel.setDynamicImports(true);
		Resource importRsc = this.ontModel.createResource(URItoImport);
		ontology.addImport(importRsc);
	}

	/**
	 * This method is used to create an individual of Dataset concept.
	 * 
	 * @param agentName      Name of the belonging agent.
	 * @param commonUriSpace Common uri space of resource.
	 * @param topics         Related topics that associated with dataset.
	 */
	public Individual createDataset(String sparqlEndpoint, String vocabulary, Resource class1,
			Individual classPartition, int classes, Resource resource, int distinctObjects, int distinctSubjects,
			int documents, int entities, Resource exampleResource, Individual feature, Individual openSearchDescription,
			int properties, Resource property, Individual propertyPartition, Resource rootResource, Individual subset,
			int triples, String uriLookupEndpoint, String uriRegexPattern, Resource literal, String agentName,
			RDFNode commonUriSpace, RDFNode... topics) {
		OntClass ontClass = ontModel.getOntClass(VOIDOntologyVocabulary.DATASET);
		Individual indv = ontModel.createIndividual(getIndividualURI(), ontClass);
		if (sparqlEndpoint != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_sparqlEndpoint, sparqlEndpoint);
		}
		if (vocabulary != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_vocabulary, vocabulary);
		}
		if (class1 != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_class, class1);
		}
		if (classPartition != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_classPartition, classPartition);
		}
		this.addProperty(indv, VOIDOntologyVocabulary.DATASET_classes, classes);
		if (resource != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_dataDump, resource);
		}
		this.addProperty(indv, VOIDOntologyVocabulary.DATASET_distinctObjects, distinctObjects);
		this.addProperty(indv, VOIDOntologyVocabulary.DATASET_distinctSubjects, distinctSubjects);
		this.addProperty(indv, VOIDOntologyVocabulary.DATASET_documents, documents);
		this.addProperty(indv, VOIDOntologyVocabulary.DATASET_entities, entities);
		if (exampleResource != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_exampleResource, exampleResource);
		}
		if (feature != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_feature, feature);
		}
		if (openSearchDescription != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_openSearchDescription, openSearchDescription);
		}
		this.addProperty(indv, VOIDOntologyVocabulary.DATASET_properties, properties);
		if (property != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_property, property);
		}
		if (propertyPartition != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_propertyPartition, propertyPartition);
		}
		if (rootResource != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_rootResource, rootResource);
		}
		if (subset != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_subset, subset);
		}
		this.addProperty(indv, VOIDOntologyVocabulary.DATASET_triples, triples);
		if (uriLookupEndpoint != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_uriLookupEndpoint, uriLookupEndpoint);
		}
		if (uriRegexPattern != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_uriRegexPattern, uriRegexPattern);
		}
		if (literal != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_uriSpace, literal);
		}
		if (agentName != null) {
			this.addProperty(indv, AvoidOntologyVocabulary.DATASET_AGENTNAME, agentName);
		}
		if (topics != null) {
			for (RDFNode topic : topics) {
				this.addProperty(indv, AvoidOntologyVocabulary.DATASET_SUBJECT, topic);
			}
		}
		if (commonUriSpace != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_uriSpace, commonUriSpace);
		}
		return indv;
	}

	public Individual createDataset(String sparqlEndpoint, Resource vocabulary, String exampleResource) {
		OntClass ontClass = ontModel.getOntClass(VOIDOntologyVocabulary.DATASET);
		Individual indv = ontModel.createIndividual(getIndividualURI(), ontClass);

		if (sparqlEndpoint != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_sparqlEndpoint, sparqlEndpoint);
		}
		if (exampleResource != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_uriSpace, exampleResource);
		}
		if (vocabulary != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_vocabulary, vocabulary);
		}
		return indv;

	}

	/**
	 * This method is used to create an individual of Linkset concept.
	 */
	public Individual createLinkset(String sparqlEndpoint, String vocabulary, Resource class1,
			Individual classPartition, int classes, Resource resource, int distinctObjects, int distinctSubjects,
			int documents, int entities, Resource resource2, Individual feature, Individual openSearchDescription,
			int properties, Resource property, Individual propertyPartition, Resource resource3, Individual subset,
			int triples, String uriLookupEndpoint, String uriRegexPattern, Resource literal, Resource linkPredicate,
			Individual objectsTarget, Individual subjectsTarget, Individual target) {
		OntClass ontClass = ontModel.getOntClass(VOIDOntologyVocabulary.LINKSET);
		Individual indv = ontModel.createIndividual(getIndividualURI(), ontClass);
		if (sparqlEndpoint != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_sparqlEndpoint, sparqlEndpoint);
		}
		if (vocabulary != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_vocabulary, vocabulary);
		}
		if (class1 != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_class, class1);
		}
		if (classPartition != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_classPartition, classPartition);
		}
		this.addProperty(indv, VOIDOntologyVocabulary.DATASET_classes, classes);
		if (resource != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_dataDump, resource);
		}
		this.addProperty(indv, VOIDOntologyVocabulary.DATASET_distinctObjects, distinctObjects);
		this.addProperty(indv, VOIDOntologyVocabulary.DATASET_distinctSubjects, distinctSubjects);
		this.addProperty(indv, VOIDOntologyVocabulary.DATASET_documents, documents);
		this.addProperty(indv, VOIDOntologyVocabulary.DATASET_entities, entities);
		if (resource2 != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_exampleResource, resource2);
		}
		if (feature != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_feature, feature);
		}
		if (openSearchDescription != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_openSearchDescription, openSearchDescription);
		}
		this.addProperty(indv, VOIDOntologyVocabulary.DATASET_properties, properties);
		if (property != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_property, property);
		}
		if (propertyPartition != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_propertyPartition, propertyPartition);
		}
		if (resource3 != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_rootResource, resource3);
		}
		if (subset != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_subset, subset);
		}
		this.addProperty(indv, VOIDOntologyVocabulary.DATASET_triples, triples);
		if (uriLookupEndpoint != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_uriLookupEndpoint, uriLookupEndpoint);
		}
		if (uriRegexPattern != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_uriRegexPattern, uriRegexPattern);
		}
		if (literal != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.DATASET_uriSpace, literal);
		}
		if (linkPredicate != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.LINKSET_linkPredicate, linkPredicate);
		}
		if (objectsTarget != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.LINKSET_objectsTarget, objectsTarget);
		}
		if (subjectsTarget != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.LINKSET_subjectsTarget, subjectsTarget);
		}
		if (target != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.LINKSET_target, target);
		}
		return indv;
	}

	public Individual createLinkset(Resource linkPredicate, Individual objectsTarget, Individual subjectsTarget) {
		OntClass ontClass = ontModel.getOntClass(VOIDOntologyVocabulary.LINKSET);
		Individual linksetIndv = ontModel.createIndividual(getIndividualURI(), ontClass);
		if (linkPredicate != null) {
			this.addProperty(linksetIndv, VOIDOntologyVocabulary.LINKSET_linkPredicate, linkPredicate);
		}
		if (objectsTarget != null) {
			this.addProperty(linksetIndv, VOIDOntologyVocabulary.LINKSET_objectsTarget, objectsTarget);
		}
		if (subjectsTarget != null) {
			this.addProperty(linksetIndv, VOIDOntologyVocabulary.LINKSET_subjectsTarget, subjectsTarget);
			this.addProperty(linksetIndv, VOIDOntologyVocabulary.DATASET_subset, subjectsTarget);
		}
		return linksetIndv;
	}

	/**
	 * This method is used to create an individual of DatasetDescription concept.
	 */
	public Individual createDatasetDescription() {
		OntClass ontClass = ontModel.getOntClass(VOIDOntologyVocabulary.DATASETDESCRIPTION);
		Individual indv = ontModel.createIndividual(getIndividualURI(), ontClass);
		return indv;
	}

	/**
	 * This method is used to create an individual of TechnicalFeature concept.
	 */
	public Individual createTechnicalFeature() {
		OntClass ontClass = ontModel.getOntClass(VOIDOntologyVocabulary.TECHNICALFEATURE);
		Individual indv = ontModel.createIndividual(getIndividualURI(), ontClass);
		return indv;
	}

	/**
	 * This method is used to create an individual of FoafDoc concept.
	 */
	public Individual createFoafDoc(Individual inDataset) {
		OntClass ontClass = ontModel.getOntClass(VOIDOntologyVocabulary.FOAFDOC);
		Individual indv = ontModel.createIndividual(getIndividualURI(), ontClass);
		if (inDataset != null) {
			this.addProperty(indv, VOIDOntologyVocabulary.FOAF_inDataset, inDataset);
		}
		return indv;
	}

	/**
	 * Returns the literal of the given dataset individual's sparqlEndpoint property
	 * value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return literal object of the property statement.
	 */
	public Literal getDatasetSparqlEndpoint(Individual datasetIndv) {
		return (Literal) datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_sparqlEndpoint_prp);
	}

	/**
	 * Returns the literal of the given dataset individual's vocabulary property
	 * value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return literal object of the property statement.
	 */
	public Literal getDatasetVocabulary(Individual datasetIndv) {
		return (Literal) datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_vocabulary_prp);
	}

	/**
	 * Returns the individual of the given dataset individual's class property
	 * value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return individual object of the property statement.
	 */
	public Individual getDatasetClass(Individual datasetIndv) {
		RDFNode value = datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_class_prp);
		if (value == null) {
			return null;
		} else {
			return (Individual) value.as(Individual.class);
		}
	}

	/**
	 * Returns the individual of the given dataset individual's classPartition
	 * property value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return individual object of the property statement.
	 */
	public Individual getDatasetClassPartition(Individual datasetIndv) {
		RDFNode value = datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_classPartition_prp);
		if (value == null) {
			return null;
		} else {
			return (Individual) value.as(Individual.class);
		}
	}

	/**
	 * Returns the literal of the given dataset individual's classes property value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return literal object of the property statement.
	 */
	public Literal getDatasetClasses(Individual datasetIndv) {
		return (Literal) datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_classes_prp);
	}

	/**
	 * Returns the individual of the given dataset individual's dataDump property
	 * value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return individual object of the property statement.
	 */
	public Individual getDatasetDataDump(Individual datasetIndv) {
		RDFNode value = datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_dataDump_prp);
		if (value == null) {
			return null;
		} else {
			return (Individual) value.as(Individual.class);
		}
	}

	/**
	 * Returns the literal of the given dataset individual's distinctObjects
	 * property value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return literal object of the property statement.
	 */
	public Literal getDatasetDistinctObjects(Individual datasetIndv) {
		return (Literal) datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_distinctObjects_prp);
	}

	/**
	 * Returns the literal of the given dataset individual's distinctSubjects
	 * property value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return literal object of the property statement.
	 */
	public Literal getDatasetDistinctSubjects(Individual datasetIndv) {
		return (Literal) datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_distinctSubjects_prp);
	}

	/**
	 * Returns the literal of the given dataset individual's documents property
	 * value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return literal object of the property statement.
	 */
	public Literal getDatasetDocuments(Individual datasetIndv) {
		return (Literal) datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_documents_prp);
	}

	/**
	 * Returns the literal of the given dataset individual's entities property
	 * value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return literal object of the property statement.
	 */
	public Literal getDatasetEntities(Individual datasetIndv) {
		return (Literal) datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_entities_prp);
	}

	/**
	 * Returns the individual of the given dataset individual's exampleResource
	 * property value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return individual object of the property statement.
	 */
	public Individual getDatasetExampleResource(Individual datasetIndv) {
		RDFNode value = datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_exampleResource_prp);
		if (value == null) {
			return null;
		} else {
			return (Individual) value.as(Individual.class);
		}
	}

	/**
	 * Gets list of example resources of given individual.
	 * 
	 * @param datasetIndv
	 * @return
	 * 
	 */
	public List<Resource> getListOfExampleResource(Individual datasetIndv) {
		List<Resource> exampleRscList = new Vector<Resource>();
		List<Statement> stmtList = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_exampleResource_prp)
				.toList();
		for (Statement statement : stmtList) {
			RDFNode value = statement.getObject();
			if (value != null)
				exampleRscList.add((Individual) value.as(Individual.class));
		}
		return exampleRscList;
	}

	/**
	 * Returns the individual of the given dataset individual's feature property
	 * value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return individual object of the property statement.
	 */
	public Individual getDatasetFeature(Individual datasetIndv) {
		RDFNode value = datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_feature_prp);
		if (value == null) {
			return null;
		} else {
			return (Individual) value.as(Individual.class);
		}
	}

	/**
	 * Returns the individual of the given dataset individual's
	 * openSearchDescription property value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return individual object of the property statement.
	 */
	public Individual getDatasetOpenSearchDescription(Individual datasetIndv) {
		RDFNode value = datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_openSearchDescription_prp);
		if (value == null) {
			return null;
		} else {
			return (Individual) value.as(Individual.class);
		}
	}

	/**
	 * Returns the literal of the given dataset individual's properties property
	 * value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return literal object of the property statement.
	 */
	public Literal getDatasetProperties(Individual datasetIndv) {
		return (Literal) datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_properties_prp);
	}

	/**
	 * Returns the individual of the given dataset individual's property property
	 * value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return individual object of the property statement.
	 */
	public Individual getDatasetProperty(Individual datasetIndv) {
		RDFNode value = datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_property_prp);
		if (value == null) {
			return null;
		} else {
			return (Individual) value.as(Individual.class);
		}
	}

	/**
	 * Returns the individual of the given dataset individual's propertyPartition
	 * property value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return individual object of the property statement.
	 */
	public Individual getDatasetPropertyPartition(Individual datasetIndv) {
		RDFNode value = datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_propertyPartition_prp);
		if (value == null) {
			return null;
		} else {
			return (Individual) value.as(Individual.class);
		}
	}

	/**
	 * Returns the individual of the given dataset individual's rootResource
	 * property value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return individual object of the property statement.
	 */
	public Individual getDatasetRootResource(Individual datasetIndv) {
		RDFNode value = datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_rootResource_prp);
		if (value == null) {
			return null;
		} else {
			return (Individual) value.as(Individual.class);
		}
	}

	/**
	 * Returns the individual of the given dataset individual's subset property
	 * value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return individual object of the property statement.
	 */
	public Individual getDatasetSubset(Individual datasetIndv) {
		RDFNode value = datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_subset_prp);
		if (value == null) {
			return null;
		} else {
			return (Individual) value.as(Individual.class);
		}
	}

	/**
	 * Returns the literal of the given dataset individual's triples property value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return literal object of the property statement.
	 */
	public Literal getDatasetTriples(Individual datasetIndv) {
		return (Literal) datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_triples_prp);
	}

	/**
	 * Returns the literal of the given dataset individual's uriLookupEndpoint
	 * property value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return literal object of the property statement.
	 */
	public Literal getDatasetUriLookupEndpoint(Individual datasetIndv) {
		return (Literal) datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_uriLookupEndpoint_prp);
	}

	/**
	 * Returns the literal of the given dataset individual's uriRegexPattern
	 * property value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return literal object of the property statement.
	 */
	public Literal getDatasetUriRegexPattern(Individual datasetIndv) {
		return (Literal) datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_uriRegexPattern_prp);
	}

	/**
	 * Returns the individual of the given dataset individual's uriSpace property
	 * value.
	 * 
	 * @param datasetIndv dataset individual.
	 * @return individual object of the property statement.
	 */
	public RDFNode getDatasetUriSpace(Individual datasetIndv) {
		RDFNode value = datasetIndv.getPropertyValue(VOIDOntologyVocabulary.DATASET_uriSpace_prp);
		if (value == null) {
			return null;
		} else {
			if (value.isLiteral()) {
				return (Literal) value.as(Literal.class);
			} else {
				return (Individual) value.as(Individual.class);
			}
		}
	}

	/**
	 * Get lists of Uri space...
	 * 
	 * @param datasetIndv
	 * @return
	 * 
	 */
	public List<RDFNode> getListOfUriSpace(Individual datasetIndv) {
		List<RDFNode> uriSpaceList = new Vector<RDFNode>();
		List<Statement> uriSpaceStmtList = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_uriSpace_prp)
				.toList();
		for (Statement statement : uriSpaceStmtList) {
			RDFNode object = statement.getObject();
			uriSpaceList.add(object);
		}
		return uriSpaceList;
	}

	/**
	 * Gets the dataset's topic property.
	 * 
	 * @param datasetIndv
	 * @return
	 */
	public RDFNode getDatasetTopic(Individual datasetIndv) {
		Property subjectProp = datasetIndv.getOntModel().getProperty(AvoidOntologyVocabulary.DATASET_SUBJECT);
		RDFNode value = datasetIndv.getPropertyValue(subjectProp);
		if (value == null) {
			return null;
		} else {
			if (value.isLiteral()) {
				return (Literal) value.as(Literal.class);
			} else
				return (Resource) value.as(Resource.class);
		}
	}

	/**
	 * Gets the agent name property.
	 * 
	 * @param datasetIndv {@link Dataset} individual.
	 * @return
	 */
	public Literal getAgentname(Individual datasetIndv) {
		return (Literal) datasetIndv.getPropertyValue(AvoidOntologyVocabulary.DATASET_agentname_prp);
	}

	/**
	 * Updates the sparqlEndpoint property of the given dataset individual as given
	 * sparqlEndpoint. If sparqlEndpoint property does not exist, this method adds a
	 * new one.
	 * 
	 * @param datasetIndv    dataset individual.
	 * @param sparqlEndpoint new value for the sparqlEndpoint property.
	 */
	public void setDatasetSparqlEndpoint(Individual datasetIndv, String sparqlEndpoint) {
		Literal sparqlEndpointLiteral = ontModel.createTypedLiteral(sparqlEndpoint,
				getPrimitiveTypes().get(sparqlEndpoint));
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_sparqlEndpoint_prp);
		if (property != null) {
			property.changeObject(sparqlEndpointLiteral);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_sparqlEndpoint_prp, sparqlEndpointLiteral);
		}
	}

	/**
	 * Updates the vocabulary property of the given dataset individual as given
	 * vocabulary. If vocabulary property does not exist, this method adds a new
	 * one.
	 * 
	 * @param datasetIndv dataset individual.
	 * @param vocabulary  new value for the vocabulary property.
	 */
	public void setDatasetVocabulary(Individual datasetIndv, String vocabulary) {
		Literal vocabularyLiteral = ontModel.createTypedLiteral(vocabulary, getPrimitiveTypes().get(vocabulary));
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_vocabulary_prp);
		if (property != null) {
			property.changeObject(vocabularyLiteral);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_vocabulary_prp, vocabularyLiteral);
		}
	}

	/**
	 * Updates the class property of the given dataset individual as given class
	 * individual. If class property does not exist, this method adds a new one.
	 * 
	 * @param datasetIndv dataset individual.
	 * @param             class new value for the class property.
	 */
	public void setDatasetClass(Individual datasetIndv, Individual clazz) {
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_class_prp);
		if (property != null) {
			property.changeObject(clazz);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_class_prp, clazz);
		}
	}

	/**
	 * Updates the classPartition property of the given dataset individual as given
	 * classPartition individual. If classPartition property does not exist, this
	 * method adds a new one.
	 * 
	 * @param datasetIndv    dataset individual.
	 * @param classPartition new value for the classPartition property.
	 */
	public void setDatasetClassPartition(Individual datasetIndv, Individual classPartition) {
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_classPartition_prp);
		if (property != null) {
			property.changeObject(classPartition);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_classPartition_prp, classPartition);
		}
	}

	/**
	 * Updates the classes property of the given dataset individual as given
	 * classes. If classes property does not exist, this method adds a new one.
	 * 
	 * @param datasetIndv dataset individual.
	 * @param classes     new value for the classes property.
	 */
	public void setDatasetClasses(Individual datasetIndv, int classes) {
		Literal classesLiteral = ontModel.createTypedLiteral(classes, getPrimitiveTypes().get(classes));
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_classes_prp);
		if (property != null) {
			property.changeObject(classesLiteral);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_classes_prp, classesLiteral);
		}
	}

	/**
	 * Updates the dataDump property of the given dataset individual as given
	 * dataDump individual. If dataDump property does not exist, this method adds a
	 * new one.
	 * 
	 * @param datasetIndv dataset individual.
	 * @param dataDump    new value for the dataDump property.
	 */
	public void setDatasetDataDump(Individual datasetIndv, Individual dataDump) {
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_dataDump_prp);
		if (property != null) {
			property.changeObject(dataDump);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_dataDump_prp, dataDump);
		}
	}

	/**
	 * Updates the distinctObjects property of the given dataset individual as given
	 * distinctObjects. If distinctObjects property does not exist, this method adds
	 * a new one.
	 * 
	 * @param datasetIndv     dataset individual.
	 * @param distinctObjects new value for the distinctObjects property.
	 */
	public void setDatasetDistinctObjects(Individual datasetIndv, int distinctObjects) {
		Literal distinctObjectsLiteral = ontModel.createTypedLiteral(distinctObjects,
				getPrimitiveTypes().get(distinctObjects));
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_distinctObjects_prp);
		if (property != null) {
			property.changeObject(distinctObjectsLiteral);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_distinctObjects_prp, distinctObjectsLiteral);
		}
	}

	/**
	 * Updates the distinctSubjects property of the given dataset individual as
	 * given distinctSubjects. If distinctSubjects property does not exist, this
	 * method adds a new one.
	 * 
	 * @param datasetIndv      dataset individual.
	 * @param distinctSubjects new value for the distinctSubjects property.
	 */
	public void setDatasetDistinctSubjects(Individual datasetIndv, int distinctSubjects) {
		Literal distinctSubjectsLiteral = ontModel.createTypedLiteral(distinctSubjects,
				getPrimitiveTypes().get(distinctSubjects));
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_distinctSubjects_prp);
		if (property != null) {
			property.changeObject(distinctSubjectsLiteral);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_distinctSubjects_prp, distinctSubjectsLiteral);
		}
	}

	/**
	 * Updates the documents property of the given dataset individual as given
	 * documents. If documents property does not exist, this method adds a new one.
	 * 
	 * @param datasetIndv dataset individual.
	 * @param documents   new value for the documents property.
	 */
	public void setDatasetDocuments(Individual datasetIndv, int documents) {
		Literal documentsLiteral = ontModel.createTypedLiteral(documents, getPrimitiveTypes().get(documents));
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_documents_prp);
		if (property != null) {
			property.changeObject(documentsLiteral);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_documents_prp, documentsLiteral);
		}
	}

	/**
	 * Updates the entities property of the given dataset individual as given
	 * entities. If entities property does not exist, this method adds a new one.
	 * 
	 * @param datasetIndv dataset individual.
	 * @param entities    new value for the entities property.
	 */
	public void setDatasetEntities(Individual datasetIndv, int entities) {
		Literal entitiesLiteral = ontModel.createTypedLiteral(entities, getPrimitiveTypes().get(entities));
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_entities_prp);
		if (property != null) {
			property.changeObject(entitiesLiteral);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_entities_prp, entitiesLiteral);
		}
	}

	/**
	 * Updates the exampleResource property of the given dataset individual as given
	 * exampleResource individual. If exampleResource property does not exist, this
	 * method adds a new one.
	 * 
	 * @param datasetIndv     dataset individual.
	 * @param exampleResource new value for the exampleResource property.
	 */
	public void setDatasetExampleResource(Individual datasetIndv, Resource exampleResource) {
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_exampleResource_prp);
		if (property != null) {
			property.changeObject(exampleResource);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_exampleResource_prp, exampleResource);
		}
	}

	/**
	 * Updates the exampleResource property of the given dataset individual as given
	 * exampleResource individual. If exampleResource property does not exist, this
	 * method adds a new one.
	 * 
	 * @param datasetIndv     dataset individual.
	 * @param exampleResource new value for the exampleResource property.
	 */
	public void setAllDatasetExampleResource(Individual datasetIndv, Resource exampleResource) {
		List<Statement> list = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_exampleResource_prp).toList();
		for (Statement statement : list) {
			statement.changeObject(exampleResource);

		}
		if (list == null || list.size() == 0) {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_exampleResource_prp, exampleResource);
		}
	}

	/**
	 * Updates the feature property of the given dataset individual as given feature
	 * individual. If feature property does not exist, this method adds a new one.
	 * 
	 * @param datasetIndv dataset individual.
	 * @param feature     new value for the feature property.
	 */
	public void setDatasetFeature(Individual datasetIndv, Individual feature) {
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_feature_prp);
		if (property != null) {
			property.changeObject(feature);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_feature_prp, feature);
		}
	}

	/**
	 * Updates the openSearchDescription property of the given dataset individual as
	 * given openSearchDescription individual. If openSearchDescription property
	 * does not exist, this method adds a new one.
	 * 
	 * @param datasetIndv           dataset individual.
	 * @param openSearchDescription new value for the openSearchDescription
	 *                              property.
	 */
	public void setDatasetOpenSearchDescription(Individual datasetIndv, Individual openSearchDescription) {
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_openSearchDescription_prp);
		if (property != null) {
			property.changeObject(openSearchDescription);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_openSearchDescription_prp,
					openSearchDescription);
		}
	}

	/**
	 * Updates the properties property of the given dataset individual as given
	 * properties. If properties property does not exist, this method adds a new
	 * one.
	 * 
	 * @param datasetIndv dataset individual.
	 * @param properties  new value for the properties property.
	 */
	public void setDatasetProperties(Individual datasetIndv, int properties) {
		Literal propertiesLiteral = ontModel.createTypedLiteral(properties, getPrimitiveTypes().get(properties));
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_properties_prp);
		if (property != null) {
			property.changeObject(propertiesLiteral);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_properties_prp, propertiesLiteral);
		}
	}

	/**
	 * Updates the property property of the given dataset individual as given
	 * property individual. If property property does not exist, this method adds a
	 * new one.
	 * 
	 * @param datasetIndv dataset individual.
	 * @param property    new value for the property property.
	 */
	public void setDatasetProperty(Individual datasetIndv, Individual property_) {
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_property_prp);
		if (property != null) {
			property.changeObject(property_);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_property_prp, property_);
		}
	}

	/**
	 * Updates the propertyPartition property of the given dataset individual as
	 * given propertyPartition individual. If propertyPartition property does not
	 * exist, this method adds a new one.
	 * 
	 * @param datasetIndv       dataset individual.
	 * @param propertyPartition new value for the propertyPartition property.
	 */
	public void setDatasetPropertyPartition(Individual datasetIndv, Individual propertyPartition) {
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_propertyPartition_prp);
		if (property != null) {
			property.changeObject(propertyPartition);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_propertyPartition_prp, propertyPartition);
		}
	}

	/**
	 * Updates the rootResource property of the given dataset individual as given
	 * rootResource individual. If rootResource property does not exist, this method
	 * adds a new one.
	 * 
	 * @param datasetIndv  dataset individual.
	 * @param rootResource new value for the rootResource property.
	 */
	public void setDatasetRootResource(Individual datasetIndv, Individual rootResource) {
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_rootResource_prp);
		if (property != null) {
			property.changeObject(rootResource);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_rootResource_prp, rootResource);
		}
	}

	/**
	 * Updates the subset property of the given dataset individual as given subset
	 * individual. If subset property does not exist, this method adds a new one.
	 * 
	 * @param datasetIndv dataset individual.
	 * @param subset      new value for the subset property.
	 */
	public void setDatasetSubset(Individual datasetIndv, Individual subset) {
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_subset_prp);
		if (property != null) {
			property.changeObject(subset);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_subset_prp, subset);
		}
	}

	/**
	 * Updates the triples property of the given dataset individual as given
	 * triples. If triples property does not exist, this method adds a new one.
	 * 
	 * @param datasetIndv dataset individual.
	 * @param triples     new value for the triples property.
	 */
	public void setDatasetTriples(Individual datasetIndv, long triples) {
		Literal triplesLiteral = ontModel.createTypedLiteral(triples, getPrimitiveTypes().get(triples));
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_triples_prp);
		if (property != null) {
			property.changeObject(triplesLiteral);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_triples_prp, triplesLiteral);
		}
	}

	/**
	 * Updates the uriLookupEndpoint property of the given dataset individual as
	 * given uriLookupEndpoint. If uriLookupEndpoint property does not exist, this
	 * method adds a new one.
	 * 
	 * @param datasetIndv       dataset individual.
	 * @param uriLookupEndpoint new value for the uriLookupEndpoint property.
	 */
	public void setDatasetUriLookupEndpoint(Individual datasetIndv, String uriLookupEndpoint) {
		Literal uriLookupEndpointLiteral = ontModel.createTypedLiteral(uriLookupEndpoint,
				getPrimitiveTypes().get(uriLookupEndpoint));
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_uriLookupEndpoint_prp);
		if (property != null) {
			property.changeObject(uriLookupEndpointLiteral);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_uriLookupEndpoint_prp,
					uriLookupEndpointLiteral);
		}
	}

	/**
	 * Updates the uriRegexPattern property of the given dataset individual as given
	 * uriRegexPattern. If uriRegexPattern property does not exist, this method adds
	 * a new one.
	 * 
	 * @param datasetIndv     dataset individual.
	 * @param uriRegexPattern new value for the uriRegexPattern property.
	 */
	public void setDatasetUriRegexPattern(Individual datasetIndv, String uriRegexPattern) {
		Literal uriRegexPatternLiteral = ontModel.createTypedLiteral(uriRegexPattern,
				getPrimitiveTypes().get(uriRegexPattern));
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_uriRegexPattern_prp);
		if (property != null) {
			property.changeObject(uriRegexPatternLiteral);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_uriRegexPattern_prp, uriRegexPatternLiteral);
		}
	}

	/**
	 * Updates the uriSpace property of the given dataset individual as given
	 * uriSpace individual. If uriSpace property does not exist, this method adds a
	 * new one.
	 * 
	 * @param datasetIndv dataset individual.
	 * @param uriSpace    new value for the uriSpace property.
	 */
	public void setDatasetUriSpace(Individual datasetIndv, RDFNode uriSpace) {
		Statement property = datasetIndv.getProperty(VOIDOntologyVocabulary.DATASET_uriSpace_prp);
		if (property != null) {
			property.changeObject(uriSpace);
		} else {
			datasetIndv.setPropertyValue(VOIDOntologyVocabulary.DATASET_uriSpace_prp, uriSpace);
		}
	}

	/**
	 * Deletes the sparqlEndpoint property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetSparqlEndpoint(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_sparqlEndpoint_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the vocabulary property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetVocabulary(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_vocabulary_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the class property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetClass(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_class_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the classPartition property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetClassPartition(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_classPartition_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the classes property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetClasses(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_classes_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the dataDump property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetDataDump(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_dataDump_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the distinctObjects property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetDistinctObjects(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_distinctObjects_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the distinctSubjects property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetDistinctSubjects(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_distinctSubjects_prp)
				.toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the documents property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetDocuments(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_documents_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the entities property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetEntities(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_entities_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the exampleResource property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetExampleResource(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_exampleResource_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the feature property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetFeature(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_feature_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the openSearchDescription property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetOpenSearchDescription(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_openSearchDescription_prp)
				.toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the properties property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetProperties(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_properties_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the property property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetProperty(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_property_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the propertyPartition property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetPropertyPartition(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_propertyPartition_prp)
				.toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the rootResource property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetRootResource(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_rootResource_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the subset property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetSubset(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_subset_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the triples property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetTriples(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_triples_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the uriLookupEndpoint property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetUriLookupEndpoint(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_uriLookupEndpoint_prp)
				.toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the uriRegexPattern property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetUriRegexPattern(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_uriRegexPattern_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the uriSpace property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove name property.
	 */
	public void deleteDatasetUriSpace(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_uriSpace_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the agent name property of the given dataset individual.
	 * 
	 * @param datasetIndv dataset individual to remove agentS name property.
	 */
	public void deleteDatasetAgentname(Individual datasetIndv) {
		List<Statement> stmts = datasetIndv.listProperties(AvoidOntologyVocabulary.DATASET_agentname_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the subject property of the given dataset individual.
	 * 
	 * @param datasetIndv
	 */
	public void deleteDatasetTopic(Individual datasetIndv) {
		Property subjectProperty = datasetIndv.getOntModel().getProperty(AvoidOntologyVocabulary.DATASET_SUBJECT);
		List<Statement> stmts = datasetIndv.listProperties(subjectProperty).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Returns the individual of the given linkset individual's linkPredicate
	 * property value.
	 * 
	 * @param linksetIndv linkset individual.
	 * @return individual object of the property statement.
	 */
	public Individual getLinksetLinkPredicate(Individual linksetIndv) {
		RDFNode value = linksetIndv.getPropertyValue(VOIDOntologyVocabulary.LINKSET_linkPredicate_prp);
		if (value == null) {
			return null;
		} else {
			return (Individual) value.as(Individual.class);
		}
	}

	/**
	 * Returns the individual of the given linkset individual's objectsTarget
	 * property value.
	 * 
	 * @param linksetIndv linkset individual.
	 * @return individual object of the property statement.
	 */
	public Individual getLinksetObjectsTarget(Individual linksetIndv) {
		RDFNode value = linksetIndv.getPropertyValue(VOIDOntologyVocabulary.LINKSET_objectsTarget_prp);
		if (value == null) {
			return null;
		} else {
			return (Individual) value.as(Individual.class);
		}
	}

	/**
	 * Returns the individual of the given linkset individual's subjectsTarget
	 * property value.
	 * 
	 * @param linksetIndv linkset individual.
	 * @return individual object of the property statement.
	 */
	public Individual getLinksetSubjectsTarget(Individual linksetIndv) {
		RDFNode value = linksetIndv.getPropertyValue(VOIDOntologyVocabulary.LINKSET_subjectsTarget_prp);
		if (value == null) {
			return null;
		} else {
			return (Individual) value.as(Individual.class);
		}
	}

	/**
	 * Returns the individual of the given linkset individual's target property
	 * value.
	 * 
	 * @param linksetIndv linkset individual.
	 * @return individual object of the property statement.
	 */
	public Individual getLinksetTarget(Individual linksetIndv) {
		RDFNode value = linksetIndv.getPropertyValue(VOIDOntologyVocabulary.LINKSET_target_prp);
		if (value == null) {
			return null;
		} else {
			return (Individual) value.as(Individual.class);
		}
	}

	/**
	 * Updates the linkPredicate property of the given linkset individual as given
	 * linkPredicate individual. If linkPredicate property does not exist, this
	 * method adds a new one.
	 * 
	 * @param linksetIndv   linkset individual.
	 * @param linkPredicate new value for the linkPredicate property.
	 */
	public void setLinksetLinkPredicate(Individual linksetIndv, Resource linkPredicate) {
		Statement property = linksetIndv.getProperty(VOIDOntologyVocabulary.LINKSET_linkPredicate_prp);
		if (property != null) {
			property.changeObject(linkPredicate);
		} else {
			linksetIndv.setPropertyValue(VOIDOntologyVocabulary.LINKSET_linkPredicate_prp, linkPredicate);
		}
	}

	/**
	 * Updates the objectsTarget property of the given linkset individual as given
	 * objectsTarget individual. If objectsTarget property does not exist, this
	 * method adds a new one.
	 * 
	 * @param linksetIndv   linkset individual.
	 * @param objectsTarget new value for the objectsTarget property.
	 */
	public void setLinksetObjectsTarget(Individual linksetIndv, Individual objectsTarget) {
		Statement property = linksetIndv.getProperty(VOIDOntologyVocabulary.LINKSET_objectsTarget_prp);
		if (property != null) {
			property.changeObject(objectsTarget);
		} else {
			linksetIndv.setPropertyValue(VOIDOntologyVocabulary.LINKSET_objectsTarget_prp, objectsTarget);
		}
	}

	/**
	 * Updates the subjectsTarget property of the given linkset individual as given
	 * subjectsTarget individual. If subjectsTarget property does not exist, this
	 * method adds a new one.
	 * 
	 * @param linksetIndv    linkset individual.
	 * @param subjectsTarget new value for the subjectsTarget property.
	 */
	public void setLinksetSubjectsTarget(Individual linksetIndv, Individual subjectsTarget) {
		Statement property = linksetIndv.getProperty(VOIDOntologyVocabulary.LINKSET_subjectsTarget_prp);
		if (property != null) {
			property.changeObject(subjectsTarget);
		} else {
			linksetIndv.setPropertyValue(VOIDOntologyVocabulary.LINKSET_subjectsTarget_prp, subjectsTarget);
		}
	}

	/**
	 * Updates the target property of the given linkset individual as given target
	 * individual. If target property does not exist, this method adds a new one.
	 * 
	 * @param linksetIndv linkset individual.
	 * @param target      new value for the target property.
	 */
	public void setLinksetTarget(Individual linksetIndv, Individual target) {
		Statement property = linksetIndv.getProperty(VOIDOntologyVocabulary.LINKSET_target_prp);
		if (property != null) {
			property.changeObject(target);
		} else {
			linksetIndv.setPropertyValue(VOIDOntologyVocabulary.LINKSET_target_prp, target);
		}
	}

	/**
	 * Deletes the linkPredicate property of the given linkset individual.
	 * 
	 * @param linksetIndv linkset individual to remove name property.
	 */
	public void deleteLinksetLinkPredicate(Individual linksetIndv) {
		List<Statement> stmts = linksetIndv.listProperties(VOIDOntologyVocabulary.LINKSET_linkPredicate_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the objectsTarget property of the given linkset individual.
	 * 
	 * @param linksetIndv linkset individual to remove name property.
	 */
	public void deleteLinksetObjectsTarget(Individual linksetIndv) {
		List<Statement> stmts = linksetIndv.listProperties(VOIDOntologyVocabulary.LINKSET_objectsTarget_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the subjectsTarget property of the given linkset individual.
	 * 
	 * @param linksetIndv linkset individual to remove name property.
	 */
	public void deleteLinksetSubjectsTarget(Individual linksetIndv) {
		List<Statement> stmts = linksetIndv.listProperties(VOIDOntologyVocabulary.LINKSET_subjectsTarget_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Deletes the target property of the given linkset individual.
	 * 
	 * @param linksetIndv linkset individual to remove name property.
	 */
	public void deleteLinksetTarget(Individual linksetIndv) {
		List<Statement> stmts = linksetIndv.listProperties(VOIDOntologyVocabulary.LINKSET_target_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Returns the individual of the given foafdoc individual's inDataset property
	 * value.
	 * 
	 * @param foafdocIndv foafdoc individual.
	 * @return individual object of the property statement.
	 */
	public Individual getFoafDocInDataset(Individual foafdocIndv) {
		RDFNode value = foafdocIndv.getPropertyValue(VOIDOntologyVocabulary.FOAFDOC_inDataset_prp);
		if (value == null) {
			return null;
		} else {
			return (Individual) value.as(Individual.class);
		}
	}

	/**
	 * Updates the inDataset property of the given foafdoc individual as given
	 * inDataset individual. If inDataset property does not exist, this method adds
	 * a new one.
	 * 
	 * @param foafdocIndv foafdoc individual.
	 * @param inDataset   new value for the inDataset property.
	 */
	public void setFoafDocInDataset(Individual foafdocIndv, Individual inDataset) {
		Statement property = foafdocIndv.getProperty(VOIDOntologyVocabulary.FOAFDOC_inDataset_prp);
		if (property != null) {
			property.changeObject(inDataset);
		} else {
			foafdocIndv.setPropertyValue(VOIDOntologyVocabulary.FOAFDOC_inDataset_prp, inDataset);
		}
	}

	/**
	 * Deletes the inDataset property of the given foafdoc individual.
	 * 
	 * @param foafdocIndv foafdoc individual to remove name property.
	 */
	public void deleteFoafDocInDataset(Individual foafdocIndv) {
		List<Statement> stmts = foafdocIndv.listProperties(VOIDOntologyVocabulary.FOAFDOC_inDataset_prp).toList();
		ontModel.remove(stmts);
	}

	/**
	 * Removes the given individual from individual ontology.
	 * 
	 * @param indv individual to remove.
	 */
	public void deleteIndividual(Individual indv) {
		List<Statement> statements = new Vector<Statement>();
		statements.addAll(this.ontModel.listStatements(null, null, indv).toList());
		statements.addAll(indv.listProperties().toList());
		this.ontModel.remove(statements);
	}

	/**
	 * Returns the current individual ontology model.
	 * 
	 * @return the current individual ontology model.
	 */
	public OntModel getOntModel() {
		return ontModel;
	}

	/**
	 * Writes the current individual ontology to the {@link System#out}.
	 */
	public void printConsole() {
		this.ontModel.write(System.out);
	}

	/**
	 * Adds the given property value of given individual.
	 */
	private void addProperty(Individual indv, String propertyURI, Object value) {
		Property prop = ontModel.getProperty(propertyURI);
		if (value instanceof RDFNode) {
			// individual values
			indv.addProperty(prop, (RDFNode) value);
		} else {
			XSDDatatype xsdDatatype = this.getPrimitiveTypes().get(value.getClass());
			if (xsdDatatype != null) {
				// supported primitive types
				indv.addProperty(prop, value.toString(), xsdDatatype);
			} else {
				// unsupported primitive types
				indv.addProperty(prop, value.toString());
			}
		}
	}

	/**
	 * Returns an auto-generated indivudual URI.
	 */
	private String getIndividualURI() {
		return this.ontURI + "#indv_" + new Random().nextDouble();
	}

	/**
	 * Returns the map of primitive types and corresponding XSDDataTypes. * @return
	 * primitive type map.
	 */
	private Map<Class<?>, XSDDatatype> getPrimitiveTypes() {
		if (this.primitiveTypes == null) {
			// initialize list.
			this.primitiveTypes = new Hashtable<Class<?>, XSDDatatype>();
			// add primitive types to the list.
			primitiveTypes.put(String.class, XSDDatatype.XSDstring);
			primitiveTypes.put(int.class, XSDDatatype.XSDint);
			primitiveTypes.put(double.class, XSDDatatype.XSDdouble);
			primitiveTypes.put(long.class, XSDDatatype.XSDlong);
			primitiveTypes.put(boolean.class, XSDDatatype.XSDboolean);
			primitiveTypes.put(float.class, XSDDatatype.XSDfloat);
			primitiveTypes.put(Integer.class, XSDDatatype.XSDinteger);
			primitiveTypes.put(Double.class, XSDDatatype.XSDdouble);
			primitiveTypes.put(Long.class, XSDDatatype.XSDlong);
			primitiveTypes.put(Boolean.class, XSDDatatype.XSDboolean);
			primitiveTypes.put(Float.class, XSDDatatype.XSDfloat);
			primitiveTypes.put(BigInteger.class, XSDDatatype.XSDnonNegativeInteger);
			primitiveTypes.put(Date.class, XSDDatatype.XSDdate);
		}
		return primitiveTypes;
	}

	/**
	 * Sets the agent name property of dataset concept.
	 * 
	 * @param datasetIndv Dataset concept.
	 * @param agentName   Agent name property value.
	 */
	public void setDatasetAgentname(Individual datasetIndv, String agentName) {
		Literal agentNameLiteral = ontModel.createTypedLiteral(agentName, getPrimitiveTypes().get(agentName));
		Statement property = datasetIndv.getProperty(AvoidOntologyVocabulary.DATASET_agentname_prp);
		if (property != null) {
			property.changeObject(agentNameLiteral);
		} else {
			datasetIndv.setPropertyValue(AvoidOntologyVocabulary.DATASET_agentname_prp, agentNameLiteral);
		}

	}

	/**
	 * Sets the specific topic property of dataset concept.
	 * 
	 * @param datasetIndv Dataset individual.
	 * @param topic       Specific concept of dataset.
	 */
	public void setDatasetTopic(Individual datasetIndv, RDFNode topic) {
		Property subjectProperty = datasetIndv.getOntModel().getProperty(AvoidOntologyVocabulary.DATASET_SUBJECT);
		if (topic.isResource()) {
			Statement property = datasetIndv.getProperty(subjectProperty);
			if (property != null) {
				property.changeObject(topic.asResource());
			} else {
				datasetIndv.setPropertyValue(subjectProperty, topic.asResource());
			}
		} else if (topic.isLiteral()) {
			Literal topicLiteral = ontModel.createTypedLiteral(topic, getPrimitiveTypes().get(topic));
			Statement property = datasetIndv.getProperty(subjectProperty);
			if (property != null) {
				property.changeObject(topicLiteral);
			} else {
				datasetIndv.setPropertyValue(subjectProperty, topicLiteral);
			}
		}
		// TODO: if isAnon()
	}

	/**
	 * Gets the vocabulary property list of given dataset individual.
	 * 
	 * @param datasetIndv Given dataset instance.
	 * @return List of Vocabulary property
	 * 
	 */
	public List<String> getListOfVocabulary(Individual datasetIndv) {
		List<Statement> vocabStmtList = datasetIndv.listProperties(VOIDOntologyVocabulary.DATASET_vocabulary_prp)
				.toList();
		List<String> vocabList = new Vector<String>();
		for (Statement statement : vocabStmtList) {
			RDFNode object = statement.getObject();
			if (object.isLiteral()) {
				vocabList.add(object.as(Literal.class).getValue().toString());
			}
		}
		return vocabList;
	}

	/**
	 * Get topic list of the given dataset individual.
	 * 
	 * @param datasetIndv
	 * @return
	 * 
	 */
	public List<RDFNode> getListOfTopic(Individual datasetIndv) {
		List<Statement> topicStmtList = datasetIndv.listProperties(AvoidOntologyVocabulary.DATASET_topic_prp).toList();
		List<RDFNode> topicList = new Vector<RDFNode>();
		for (Statement statement : topicStmtList) {
			RDFNode object = statement.getObject();
			topicList.add(object);
		}
		return topicList;

	}

	/**
	 * Add topic property to the dataset...
	 * 
	 * @param datasetIndv Dataset instance...
	 * @param topic       Topic parameter...
	 */
	public void addDatasetTopicProperty(Individual datasetIndv, RDFNode topic) {
		Property subjectProperty = datasetIndv.getOntModel().getProperty(AvoidOntologyVocabulary.DATASET_SUBJECT);
		datasetIndv.addProperty(subjectProperty, topic);

	}

	/**
	 * Adds the given vocabulary vocabulary uri.
	 * 
	 * @param datasetIndv
	 * @param vocabularyUri
	 */
	public void addDatasetVocabularyProperty(Individual datasetIndv, String vocabularyUri) {
		datasetIndv.addProperty(VOIDOntologyVocabulary.DATASET_vocabulary_prp, vocabularyUri);
	}

	/**
	 * Adds the given uri space property.
	 * 
	 * @param datasetIndv to added dataset individual.
	 * @param anyUriSpace adding property.
	 */
	public void addDatasetUriSpace(Individual datasetIndv, RDFNode anyUriSpace) {
		datasetIndv.addProperty(VOIDOntologyVocabulary.DATASET_uriSpace_prp, anyUriSpace);
	}

	/**
	 * Adds the given example resource property .
	 * 
	 * @param datasetIndv
	 * @param exampleResource
	 */
	public void addDatasetExampleResource(Individual datasetIndv, Resource exampleResource) {
		datasetIndv.addProperty(VOIDOntologyVocabulary.DATASET_exampleResource_prp, exampleResource);
	}

	/**
	 * Lists the datasets resources.
	 * 
	 * @return
	 */
	public List<Individual> listDatasets() {
		return getOntModel().listIndividuals(VOIDOntologyVocabulary.DATASET_rsc).toList();
	}

}
