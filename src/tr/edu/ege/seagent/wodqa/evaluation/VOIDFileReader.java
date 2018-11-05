package tr.edu.ege.seagent.wodqa.evaluation;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import tr.edu.ege.seagent.wodqa.voiddocument.VOIDIndividualOntology;


public class VOIDFileReader {

	private static Logger logger = Logger.getLogger(VOIDFileReader.class);

	/**
	 * This method reads files under the given path and creates a list with
	 * {@link VOIDIndividualOntology} instances
	 * 
	 * @param directoryName
	 *            directory whic contains VOID files
	 * @return {@link VOIDIndividualOntology} list
	 * @throws MalformedURLException
	 */
	public static List<Model> readFilesIntoModel(String directoryName) throws MalformedURLException {
		List<Model> modelList = new ArrayList<Model>();
		try (Stream<Path> paths = Files.walk(Paths.get(directoryName))) {
			paths.filter(Files::isRegularFile).forEach(file -> addVoidFileToList(modelList, file));
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return modelList;

	}


	private static void addVoidFileToList(List<Model> modelList, Path path) {
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
		String filePath = "";
		try {
			filePath = path.toUri().toURL().toString();
			model.read(filePath);
			modelList.add(model);
		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
		}
		logger.info(MessageFormat.format("VOID file <{0}> is read and added in the list", filePath));
	}

}
