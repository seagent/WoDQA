package tr.edu.ege.seagent.wodqa.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Vector;

import org.junit.Test;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import tr.edu.ege.seagent.wodqa.exception.VOIDDescriptionConsistencyException;
import tr.edu.ege.seagent.wodqa.query.analyzer.RelevantDatasetsForTriple;
import tr.edu.ege.seagent.wodqa.query.analyzer.RelevantType;

public class RelevantDatasetsForTripleTest {

	private static final String DATASET_RSC_EXAMPLE_1 = "http://datasetIndv1.com";
	private static final String DATASET_RSC_EXAMPLE_2 = "http://datasetIndv2.com";
	private static final String DATASET_RSC_EXAMPLE_3 = "http://datasetIndv3.com";
	private static final String DATASET_RSC_EXAMPLE_4 = "http://datasetIndv4.com";

	@Test
	public void checkChangeOnRelevantDatasetsTest() throws Exception {
		RelevantDatasetsForTriple relevantDatasetsForTriple = new RelevantDatasetsForTriple(
				null);
		assertFalse(relevantDatasetsForTriple.checkChangeOnRelevantDatasets());
		// create some relevant datasets...
		List<Resource> newRelevantDatasets = new Vector<Resource>();
		// add example relevant datasets...
		newRelevantDatasets.add(createResource(DATASET_RSC_EXAMPLE_1));
		newRelevantDatasets.add(createResource(DATASET_RSC_EXAMPLE_2));
		newRelevantDatasets.add(createResource(DATASET_RSC_EXAMPLE_3));

		List<RelevantType> relevantTypes = new Vector<RelevantType>();
		relevantTypes.add(RelevantType.INTERNAL);
		relevantTypes.add(RelevantType.INTERNAL);
		relevantTypes.add(RelevantType.INTERNAL);
		relevantDatasetsForTriple.setNewRelevantTypes(relevantTypes);

		// update, initialize...
		relevantDatasetsForTriple.setNewRelevantDatasets(newRelevantDatasets);
		// assert
		assertTrue(relevantDatasetsForTriple.checkChangeOnRelevantDatasets());
		assignAndCheck(relevantDatasetsForTriple);

		// update, set new relevant dataset with two, 3 in current now...
		newRelevantDatasets.remove(1);
		relevantDatasetsForTriple.setNewRelevantDatasets(newRelevantDatasets);
		// assert
		assertTrue(relevantDatasetsForTriple.checkChangeOnRelevantDatasets());
		assignAndCheck(relevantDatasetsForTriple);

		// update, set new relevatn datasets with 2 but one is different, 2 in
		// current now...
		newRelevantDatasets.remove(1);
		newRelevantDatasets.add(createResource(DATASET_RSC_EXAMPLE_4));
		relevantDatasetsForTriple.setNewRelevantDatasets(newRelevantDatasets);
		// assert
		assertTrue(relevantDatasetsForTriple.checkChangeOnRelevantDatasets());
		assignAndCheck(relevantDatasetsForTriple);
		assertEquals(1, relevantDatasetsForTriple.getCurrentRelevantDatasets()
				.size());
	}

	@Test
	public void assignAndCheckTest() throws Exception {
		RelevantDatasetsForTriple rdft = new RelevantDatasetsForTriple(null);
		// create some relevant datasets...
		List<Resource> newRelevantDatasets = new Vector<Resource>();
		// add example relevant datasets...
		Resource exDataset1 = createResource(DATASET_RSC_EXAMPLE_1);
		newRelevantDatasets.add(exDataset1);
		Resource exDataset2 = createResource(DATASET_RSC_EXAMPLE_2);
		newRelevantDatasets.add(exDataset2);
		// set new relevant dataset
		rdft.setNewRelevantDatasets(newRelevantDatasets);
		List<RelevantType> relevantTypes = new Vector<RelevantType>();
		relevantTypes.add(RelevantType.INTERNAL);
		relevantTypes.add(RelevantType.INTERNAL);
		rdft.setNewRelevantTypes(relevantTypes);

		assertTrue(rdft.isAllRelated());
		rdft.eliminateWithNewFoundDatasets();

		// assert
		assertFalse(rdft.isAllRelated());
		List<Resource> currentRelevantDatasets = rdft
				.getCurrentRelevantDatasets();
		assertEquals(2, currentRelevantDatasets.size());
		assertTrue(currentRelevantDatasets.contains(exDataset1));
		assertTrue(currentRelevantDatasets.contains(exDataset2));

		// remove from new current dataset and assign a different one...
		currentRelevantDatasets.remove(1);
		Resource exDataset3 = createResource(DATASET_RSC_EXAMPLE_3);
		currentRelevantDatasets.add(exDataset3);
		rdft.setNewRelevantDatasets(newRelevantDatasets);
		rdft.setNewRelevantTypes(relevantTypes);
		rdft.eliminateWithNewFoundDatasets();

		// assert
		assertEquals(1, currentRelevantDatasets.size());
		assertTrue(currentRelevantDatasets.contains(exDataset1));
	}

	/**
	 * @param relevantDatasetsForTriple
	 * @throws VOIDDescriptionConsistencyException
	 */
	public void assignAndCheck(
			RelevantDatasetsForTriple relevantDatasetsForTriple)
			throws VOIDDescriptionConsistencyException {
		relevantDatasetsForTriple.eliminateWithNewFoundDatasets();
		assertFalse(relevantDatasetsForTriple.checkChangeOnRelevantDatasets());
	}

	/**
	 * It creates a resource with the given URI.
	 * 
	 * @param rscURI
	 * @return
	 */
	public Resource createResource(String rscURI) {
		return ResourceFactory.createResource(rscURI);
	}

}
