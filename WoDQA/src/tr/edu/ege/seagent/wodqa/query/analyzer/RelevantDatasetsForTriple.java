package tr.edu.ege.seagent.wodqa.query.analyzer;

import java.text.MessageFormat;
import java.util.List;
import java.util.Vector;

import tr.edu.ege.seagent.wodqa.exception.VOIDDescriptionConsistencyException;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Resource;

public class RelevantDatasetsForTriple {

	private List<RelevantType> currentRelevantTypes;
	private List<RelevantType> newRelevantTypes;

	public List<RelevantType> getCurrentRelevantTypes() {
		return currentRelevantTypes;
	}

	public List<RelevantType> getNewRelevantTypes() {
		return newRelevantTypes;
	}

	/**
	 * Resource list of relevant datasets of the triple pattern before
	 * performing a rule.
	 */
	private List<Resource> currentRelevantDatasets;
	/**
	 * It holds true if the triple is relevance with all datasets.
	 */
	private boolean isAllRelated;

	/**
	 * Resource list of relevant datasets of the triple pattern after updating
	 * with rules.
	 */
	private List<Resource> newRelevantDatasets;
	/**
	 * Analyzed triple pattern.
	 */
	private Triple triple;

	/**
	 * It creates relevant datasets for the triple pattern. It is related all
	 * relevant datasets, but it is probably eliminated, for this reason all
	 * relevant datasets is hold by a field. When end of the analyze phase, if
	 * this field is still true, assign all datasets as relevant.
	 * 
	 * @param triple
	 *            is the probed {@link Triple} instance.
	 */
	public RelevantDatasetsForTriple(Triple triple) {
		this.triple = triple;
		currentRelevantDatasets = new Vector<Resource>();
		currentRelevantTypes = new Vector<RelevantType>();
		newRelevantDatasets = new Vector<Resource>();
		newRelevantTypes = new Vector<RelevantType>();
		setAllRelated(true);
	}

	/**
	 * Update current relevant datasets by elimination using updated relevant
	 * datasets. After this method current and new relevant datasets will be
	 * equal.
	 * 
	 * @throws VOIDDescriptionConsistencyException
	 */
	public void eliminateWithNewFoundDatasets()
			throws VOIDDescriptionConsistencyException {
		if (getNewRelevantDatasets().size() <= 0)
			return;
		if (isAllRelated) {
			this.currentRelevantDatasets.addAll(getNewRelevantDatasets());
			setAllDatasetsRelevantTypeInternal();
			this.currentRelevantTypes.addAll(getNewRelevantTypes());
			setAllRelated(false);
		} else {
			for (int i = 0; i < getCurrentRelevantDatasets().size(); i++) {
				if (!getNewRelevantDatasets().contains(
						getCurrentRelevantDatasets().get(i))) {
					getCurrentRelevantDatasets().remove(i);
					getCurrentRelevantTypes().remove(i);
					i--;
				} else {
					if (getNewRelevantTypes().size() > 0) {
						int index = getNewRelevantDatasets().indexOf(
								getCurrentRelevantDatasets().get(i));
						// set included dataset's external state
						getCurrentRelevantTypes().set(i,
								getNewRelevantTypes().get(index));
					}
				}
			}
			this.newRelevantDatasets.clear();
			this.newRelevantTypes.clear();
			this.newRelevantDatasets.addAll(getCurrentRelevantDatasets());
			this.newRelevantTypes.addAll(getCurrentRelevantTypes());
		}
		if (getCurrentRelevantDatasets().size() == 0) {
			throw new VOIDDescriptionConsistencyException(
					"VOID documents must be include all defined vocabulary and urispaces, and must reflect all linkage states.");
		}
	}

	/**
	 * It checks changes by looking difference between current and udpated
	 * relevant datasets. It returns true if there is a change, otherwise
	 * returns false.
	 */
	public boolean checkChangeOnRelevantDatasets() {
		if (getCurrentRelevantDatasets().size() != getNewRelevantDatasets()
				.size())
			return true;
		else {
			// size can be equal but updated relevant can contain a different
			// one.
			for (Resource updatedRelevant : getNewRelevantDatasets()) {
				if (getCurrentRelevantDatasets().contains(updatedRelevant))
					continue;
				else
					return true;
			}
		}
		return false;
	}

	public List<Resource> getCurrentRelevantDatasets() {
		return currentRelevantDatasets;
	}

	public List<Resource> getNewRelevantDatasets() {
		return newRelevantDatasets;
	}

	public Triple getTriple() {
		return triple;
	}

	public boolean isAllRelated() {
		return isAllRelated;
	}

	/**
	 * Sets the all relevant datasets as relevant datasets.
	 */
	public void setAllDatasetsRelevantTypeInternal() {
		if (getNewRelevantTypes().size() == 0) {
			for (int i = 0; i < getCurrentRelevantDatasets().size(); i++) {
				getNewRelevantTypes().add(RelevantType.INTERNAL);
			}
		}

	}

	public void setNewRelevantDatasets(List<Resource> newRelevantDatasets) {
		this.newRelevantDatasets.clear();
		this.newRelevantDatasets.addAll(newRelevantDatasets);
	}

	public void setNewRelevantTypes(List<RelevantType> newRelevantTypes) {
		this.newRelevantTypes.clear();
		this.newRelevantTypes.addAll(newRelevantTypes);
	}

	public void setAllRelated(boolean isAllRelated) {
		this.isAllRelated = isAllRelated;
	}

	public boolean isEmpty() {
		return currentRelevantDatasets.isEmpty()
				&& currentRelevantTypes.isEmpty()
				&& newRelevantDatasets.isEmpty() && newRelevantTypes.isEmpty();
	}

	@Override
	public String toString() {
		return MessageFormat.format("TriplePack[Triple={0},Datasets={1}]",
				getTriple(), getCurrentRelevantDatasets());
	}
}
