package tr.edu.ege.seagent.wodqa.voiddocument;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

import tr.edu.ege.seagent.wodqa.QueryVocabulary;
import tr.edu.ege.seagent.wodqa.evaluation.VOIDFileReader;

public class VoidModelConstructor {

	/**
	 * This method gets non appropriate linksets and datasets that holds exluded
	 * datasets as their objectsTarget.
	 * 
	 * @param model
	 *            TODO
	 * 
	 * @return
	 */
	public static List<Resource> getNonAppropriateLinksets(Model model) {
		List<Resource> linksets = new ArrayList<Resource>();
		String query = QueryVocabulary.RDF_PREFIX_URI + QueryVocabulary.VOID_PREFIX_URI
				+ "SELECT ?linkset ?dataset WHERE{" + "?linkset void:objectsTarget ?dataset."
				+ "FILTER NOT EXISTS{?dataset rdf:type void:Dataset.}}";
		QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
		ResultSet resultSet = queryExecution.execSelect();
		while (resultSet.hasNext()) {
			linksets.add(resultSet.next().getResource("linkset"));
		}
		return linksets;
	}

	public static Model constructVOIDSpaceModel(String voidFilePath) throws MalformedURLException {

		List<Model> readModels = VOIDFileReader.readFilesIntoModel(voidFilePath);
		Model mainModel = ModelFactory.createDefaultModel();
		for (Model model : readModels) {
			mainModel.add(model);
		}

		// retrieve non appropriate linkset
		List<Resource> nonReachableLinksets = getNonAppropriateLinksets(mainModel);
		for (Resource linkset : nonReachableLinksets) {
			mainModel.removeAll(linkset, null, (RDFNode) null);
		}
		return mainModel;
	}

}
