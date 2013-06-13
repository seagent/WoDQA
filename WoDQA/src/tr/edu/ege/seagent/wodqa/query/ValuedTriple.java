package tr.edu.ege.seagent.wodqa.query;


public class ValuedTriple {

	private DeterminedTriple determinedTriple;
	private int value;

	public ValuedTriple(DeterminedTriple triple, int value) {
		this.determinedTriple = triple;
		this.value = value;
	}

	public DeterminedTriple getDeterminedTriple() {
		return determinedTriple;
	}

	public int getValue() {
		return value;
	}

}
