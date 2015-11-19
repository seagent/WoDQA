package tr.edu.ege.seagent.wodqa.evaluation;

import java.io.File;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import tr.edu.ege.seagent.dataset.vocabulary.VOIDIndividualOntology;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

public class VOIDFileReader {

	private static Logger logger = Logger.getLogger(VOIDFileReader.class);

	private static int MAX_VOID_SIZE = 73;

	/**
	 * This method reads files under the given path and creates a list with
	 * {@link VOIDIndividualOntology} instances
	 * 
	 * @param directoryName
	 *            directory whic contains VOID files
	 * @return {@link VOIDIndividualOntology} list
	 * @throws MalformedURLException
	 */
	public static List<VOIDIndividualOntology> readFilesIntoModel(String directoryName)
			throws MalformedURLException {
		List<VOIDIndividualOntology> indvOntList = new Vector<VOIDIndividualOntology>();
		// Iterate until the deadline number and get all void models.
		for (int index = 0; index < MAX_VOID_SIZE; index++) {
			// create a file under given directory and consisting given index
			// value
			File voidFile = new File(generateAbsoluteFilePath(directoryName,
					index));
			// check whether the file is exist and read model if so.
			if (voidFile.exists()) {
				OntModel model = ModelFactory
						.createOntologyModel(OntModelSpec.OWL_MEM);
				model.read(voidFile.toURI().toURL().toString());
				String ontURI = model.listOntologies().toList().get(0).getURI();
				VOIDIndividualOntology indvOnt = new VOIDIndividualOntology(
						ontURI, model);
				// add read model into list
				indvOntList.add(indvOnt);
				logger.info(MessageFormat.format(
						"VOID file <{0}> is read and added in the list",
						voidFile.toURI().toURL().toString()));
			} else {
				logger.warn(MessageFormat
						.format("VOID file <{0}> cannot be read because it doesn't exist under the path",
								voidFile.toURI().toURL().toString()));
			}
		}
		return indvOntList;
	}

	/**
	 * This method generates absolute file path, for the file with given index
	 * 
	 * @param directoryName
	 *            directory of file
	 * @param index
	 *            index number of file
	 * @return absolute file path
	 */
	private static String generateAbsoluteFilePath(String directoryName, int index) {
		return directoryName + "/datasets" + index + ".owl";
	}
}
