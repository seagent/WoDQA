package tr.edu.ege.seagent.wodqa.voiddocument;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * 
 * It contains void concept operations.
 * 
 */
public class VoidConceptOperations {

	/**
	 * Gets the graph URI of the given resource URI.
	 * 
	 * @param resourceURI
	 * @return
	 */
	public static String getGraphURI(String resourceURI) {
		if (resourceURI.indexOf("#") > 0) {
			resourceURI = resourceURI
					.substring(0, resourceURI.indexOf("#") + 1);
		} else {
			// some URIs are like that: "http://sws.geonames.org/node23243/" and
			// we
			// must ignore last "/".
			resourceURI = resourceURI.substring(0,
					resourceURI.lastIndexOf("/") + 1);
		}
		return resourceURI;
	}

	/**
	 * Gets string valu of the given resource.
	 * 
	 * @param resource
	 * @return
	 */
	public static String getStringValue(Object resource) {
		String datasetURI;
		if (resource instanceof Resource) {
			datasetURI = ((Resource) resource).getURI();
		} else if (resource instanceof Literal) {
			datasetURI = ((Literal) resource).getValue().toString();
		} else if (resource instanceof Individual) {
			datasetURI = ((Individual) resource).getURI();
		} else {
			datasetURI = ((String) resource);
		}
		return datasetURI;
	}

}
