package tr.edu.ege.seagent.wodqa.exception;

public class QueryHeaderException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 58148971665334652L;
	/**
	 * 
	 */
	public static final String CONSTRUCT_TYPE_ERROR_MESSAGE = "Constructed query is not a CONSTRUCT query!";
	/**
	 * 
	 */
	public static final String SELECT_TYPE_ERROR_MESSAGE = "Constructed query is not a SELECT query!";

	/**
	 * @param message
	 * @param cause
	 */
	public QueryHeaderException(String message, Throwable cause) {
		super(message, cause);
	}

	public QueryHeaderException(String message) {
		super(message);
	}
}
