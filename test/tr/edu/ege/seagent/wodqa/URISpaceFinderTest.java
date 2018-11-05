package tr.edu.ege.seagent.wodqa;

import static org.junit.Assert.assertEquals;

import java.util.Stack;

import org.junit.Test;

public class URISpaceFinderTest {

	/**
	 * This test checks reducing and constructing URISpaces of a 303 type URI
	 * 
	 * @throws Exception
	 */
	@Test
	public void reduceURISpacesOf303URI() throws Exception {

		// define an 303 URI to be reduced
		String uriText = "http://dbpedia.org/ontology/Drug/meltingPoint";
		// create a URISpaceFinder object to reduce URI
		URISpaceFinder uriSpaceFinder = new URISpaceFinder(uriText);

		// reduce URI
		Stack<String> stack = uriSpaceFinder.reduceURISpaces();

		// control returned stack that contains reduced URIs

		// check stack size
		assertEquals(3, stack.size());

		// check first element
		assertEquals("http://dbpedia.org/ontology/Drug/", stack.pop());

		// check second element
		assertEquals("http://dbpedia.org/ontology/", stack.pop());

		// check third element
		assertEquals("http://dbpedia.org/", stack.pop());

	}
	
	/**
	 * This test checks reducing and constructing URISpaces of a two dot type URI
	 * 
	 * @throws Exception
	 */
	@Test
	public void reduceURISpacesOfTwoDotURI() throws Exception {

		// define an two dot URI to be reduced
		String uriText = "http://dbpedia.org/ontology:Drug/meltingPoint";
		// create a URISpaceFinder object to reduce URI
		URISpaceFinder uriSpaceFinder = new URISpaceFinder(uriText);

		// reduce URI
		Stack<String> stack = uriSpaceFinder.reduceURISpaces();

		// control returned stack that contains reduced URIs

		// check stack size
		assertEquals(3, stack.size());

		// check first element
		assertEquals("http://dbpedia.org/ontology:Drug/", stack.pop());

		// check second element
		assertEquals("http://dbpedia.org/ontology:", stack.pop());

		// check third element
		assertEquals("http://dbpedia.org/", stack.pop());

	}

	/**
	 * This test checks reducing and constructing URISpaces of a hash type URI
	 * 
	 * @throws Exception
	 */
	@Test
	public void reduceURISpacesOfHashURI() throws Exception {
		
		// define an hash URI to be reduced
		String uriText = "http://dbpedia.org/ontology#Drug/meltingPoint";
		// create a URISpaceFinder object to reduce URI
		URISpaceFinder uriSpaceFinder = new URISpaceFinder(uriText);

		// reduce URI
		Stack<String> stack = uriSpaceFinder.reduceURISpaces();

		// control returned stack that contains reduced URIs

		// check stack size
		assertEquals(1, stack.size());

		// check stack element
		assertEquals("http://dbpedia.org/ontology#", stack.pop());
	}
}
