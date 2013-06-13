package tr.edu.ege.seagent.wodqa;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Stack;

/**
 * This class finds uriSpaces of a given URI
 * 
 * @author etmen
 * 
 */
public class URISpaceFinder {

	/**
	 * Full URI to be fragmented into urispaces.
	 */
	private String fullURI;

	public URISpaceFinder(String fullURI) {
		this.fullURI = fullURI;
	}

	/**
	 * This method reduces uriSpaces of {@link #fullURI}
	 * 
	 * @return
	 * @throws URISyntaxException
	 */
	public Stack<String> reduceURISpaces() throws URISyntaxException {
		// define a stack that holds URISpaces
		Stack<String> stack = new Stack<String>();

		// create an URI object to part full URI
		URI uri = new URI(fullURI);
		// get scheme part
		String scheme = uri.getScheme();
		// get host part
		String host = uri.getHost();
		// get path part
		String path = uri.getPath();
		// get fragment part
		String fragment = uri.getFragment();

		// define minimum final URI
		String finalURI = scheme + "://" + host;

		if (fragment != null) {
			// hash URI
			finalURI += path + "#";
			// push final URI to the stack
			stack.push(finalURI);
		} else {
			// 303 URI

			// check whether URIspace finding operation has been ended.
			char[] pathChars = path.toCharArray();
			String cutPart = "";
			for (int index = 0; index < pathChars.length; index++) {
				char nextChar = pathChars[index];
				if (nextChar == '/' || nextChar == ':') {
					int cuttingIndex = index;
					// split path
					String part = path.substring(cutPart.length(), cuttingIndex + 1);
					// update cut part...
					cutPart += part;
					// add path to final text
					finalURI += part;
					// push final URI to the stack
					stack.push(finalURI);
				}
			}
		}
		return stack;
	}
}
