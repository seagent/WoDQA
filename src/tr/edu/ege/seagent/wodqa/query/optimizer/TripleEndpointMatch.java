package tr.edu.ege.seagent.wodqa.query.optimizer;

import tr.edu.ege.seagent.wodqa.query.analyzer.VOIDPathSolution;

import com.hp.hpl.jena.graph.Triple;

public class TripleEndpointMatch {

	private Triple triple;

	public TripleEndpointMatch(Triple triple, VOIDPathSolution vp) {
		this.triple = triple;
		this.vp = vp;
	}

	public Triple getTriple() {
		return triple;
	}

	public VOIDPathSolution getVOIDPathSolution() {
		return vp;
	}

	private VOIDPathSolution vp;

}
