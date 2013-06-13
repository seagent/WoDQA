package tr.edu.ege.seagent.wodqa.exception;

public class VOIDStoreCreationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2795418425538200284L;
	public static final String NO_VOID_STORE = "There is no registered void store! Create void store before using WoDQA!";

	/**
	 * @param message
	 */
	public VOIDStoreCreationException(String message) {
		super(message);
	}

}
