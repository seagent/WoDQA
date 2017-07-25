package tr.edu.ege.seagent.wodqa.query.analyzer;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Resource;

public class ExternalDatasetsForTriples {

	private List<Resource> forTriple1;

	private List<Resource> forTriple2;

	public ExternalDatasetsForTriples(List<Resource> forTriple1,
			List<Resource> forTriple2) {
		this.forTriple1 = forTriple1;
		this.forTriple2 = forTriple2;
	}

	public List<Resource> getExternalsForTriple1() {
		return forTriple1;
	}

	public List<Resource> getExternalsForTriple2() {
		return forTriple2;
	}

}
