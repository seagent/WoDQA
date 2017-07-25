package tr.edu.ege.seagent.wodqa.query;

public class Statement {

	private String statementText;
	private boolean firstOptional;

	public Statement(String statementText, boolean firstOptional) {
		this.statementText = statementText;
		this.firstOptional = firstOptional;
	}

	public String getStatementText() {
		return statementText;
	}

	public void setStatementText(String statementText) {
		this.statementText = statementText;
	}

	public boolean isFirstOptional() {
		return firstOptional;
	}

	public void setFirstOptional(boolean firstOptional) {
		this.firstOptional = firstOptional;
	}

	public void addPattern(String pattern) {
		if (statementText != null) {
			statementText += pattern;
		}

	}

}
