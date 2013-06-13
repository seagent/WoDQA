package tr.edu.ege.seagent.wodqa.exception;

public class VOIDDescriptionConsistencyException extends Exception {

	/**
	 * It is thrown when VOID documents don't include required definitions.
	 * 
	 * @param string
	 */
	public VOIDDescriptionConsistencyException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3859952873187366007L;

}
