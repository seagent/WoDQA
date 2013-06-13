package tr.edu.ege.seagent.wodqa.query;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;

public class QueryResult {
	private QueryExecution exec;

	public QueryExecution getExec() {
		return exec;
	}

	public void setExec(QueryExecution exec) {
		this.exec = exec;
	}

	private OntModel constructedModel;
	private ResultSet selectedSet;

	public OntModel getConstructedModel() {
		return constructedModel;
	}

	public void setConstructedModel(OntModel constructedModel) {
		this.constructedModel = constructedModel;
	}

	public ResultSet getSelectedSet() {
		return selectedSet;
	}

	public void setSelectedSet(ResultSet selectedSet) {
		this.selectedSet = selectedSet;
	}

	private int queryType;

	public int getQueryType() {
		return queryType;
	}

	public void setQueryType(int queryType) {
		this.queryType = queryType;
	}

}
