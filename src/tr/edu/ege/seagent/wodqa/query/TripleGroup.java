package tr.edu.ege.seagent.wodqa.query;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

public class TripleGroup {

	private double averageValue;
	private TripleGroup mostCommonVariableGroup;
	private List<ValuedTriple> valuedTriples;
	private List<Node> variables;

	public TripleGroup() {
		valuedTriples = new ArrayList<ValuedTriple>();
		variables = new ArrayList<Node>();
		this.averageValue = (double) 0;
	}

	public TripleGroup(List<ValuedTriple> valuedTriples) {
		this.valuedTriples = valuedTriples;
		variables = new ArrayList<Node>();
		averageValue = (double) 0;
		calculateAverageValue();
		for (ValuedTriple valuedTriple : valuedTriples) {
			updateVariableList(valuedTriple);
		}
	}

	/**
	 * This method adds {@link ValuedTriple} instance to list and perform
	 * updates
	 * 
	 * @param valuedTriple
	 */
	public void add(ValuedTriple valuedTriple) {
		valuedTriples.add(valuedTriple);
		calculateAverageValue();
		updateVariableList(valuedTriple);
	}

	public double getAverageValue() {
		return averageValue;
	}

	public TripleGroup getMostCommonVariableGroup() {
		return mostCommonVariableGroup;
	}

	public List<ValuedTriple> getValuedTriples() {
		return valuedTriples;
	}

	public List<Node> getVariables() {
		return variables;
	}

	public void setMostCommonVariableGroup(TripleGroup mostCommonVariableGroup) {
		this.mostCommonVariableGroup = mostCommonVariableGroup;
	}

	public void setValuedTriples(List<ValuedTriple> valuedTriples) {
		this.valuedTriples = valuedTriples;
	}

	/**
	 * This method calculates average value of triple group
	 */
	private void calculateAverageValue() {
		int sumValue = 0;
		for (ValuedTriple valuedTriple : valuedTriples) {
			sumValue += valuedTriple.getValue();
		}
		this.averageValue = new Double(sumValue)
				/ new Double(valuedTriples.size());
	}

	/**
	 * This method checks whether given node is variable and not contained in
	 * the list, it stores if so.
	 * 
	 * @param node
	 */
	private void checkForVariable(Node node) {
		if (node != null && node.isVariable() && !variables.contains(node)) {
			variables.add(node);
		}
	}

	/**
	 * This method updates variable list if given {@link ValuedTriple} instance
	 * includes a variable that is not contained before.
	 * 
	 * @param valuedTriple
	 */
	private void updateVariableList(ValuedTriple valuedTriple) {
		// get triple value
		Triple triple = valuedTriple.getDeterminedTriple().getTriple();
		// check whether subject, predicate, and object of triple is variable
		// and not to contained variable list
		checkForVariable(triple.getSubject());
		checkForVariable(triple.getPredicate());
		checkForVariable(triple.getObject());
	}

}
