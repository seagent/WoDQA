package tr.edu.ege.seagent.wodqa.query;

import java.util.List;

import com.hp.hpl.jena.graph.Triple;

public class DeterminedTriple {

	private List<String> endpoints;
	private Triple triple;

	public DeterminedTriple(Triple triple, List<String> endpoints) {
		this.endpoints = endpoints;
		this.triple = triple;
	}

	public List<String> getEndpoints() {
		return endpoints;
	}

	public Triple getTriple() {
		return triple;
	}

	public void setEndpoints(List<String> endpoints) {
		this.endpoints = endpoints;
	}

	public void setTriple(Triple triple) {
		this.triple = triple;
	}

}
