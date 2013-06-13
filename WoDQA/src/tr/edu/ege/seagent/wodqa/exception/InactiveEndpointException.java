package tr.edu.ege.seagent.wodqa.exception;

public class InactiveEndpointException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * That indicates the sparql endpoint address is not be accessed.
	 * 
	 * @param message
	 * @param cause
	 */
	public InactiveEndpointException(String message, Throwable cause) {
		super(message, cause);
	}

}
