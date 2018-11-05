package tr.edu.ege.seagent.wodqa.query.optimizer;

import com.hp.hpl.jena.graph.Triple;

import tr.edu.ege.seagent.wodqa.query.analyzer.VOIDPathSolution;

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
