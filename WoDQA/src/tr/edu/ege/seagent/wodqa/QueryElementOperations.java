package tr.edu.ege.seagent.wodqa;

import java.util.List;
import java.util.Vector;

import com.hp.hpl.jena.graph.Triple;

public class QueryElementOperations {

	/**
	 * Convert the given triple to the SPARQL statement form.
	 * 
	 * @param triple
	 */
	public static String convertTripleToString(Triple triple) {
		String s;
		String p;
		String o;
		if (triple.getSubject().isVariable())
			s = triple.getSubject().toString();
		else
			s = "<" + triple.getSubject().getURI() + ">";
		// get predicate...
		if (triple.getPredicate().isVariable())
			p = triple.getPredicate().toString();
		else
			p = "<" + triple.getPredicate().getURI() + ">";
		// get object...
		if (triple.getObject().isVariable())
			o = triple.getObject().toString();
		else if (triple.getObject().isURI())
			o = "<" + triple.getObject().getURI() + ">";
		else
			o = triple.getObject().toString();
		return s + " " + p + " " + o + ". ";
	}

	/**
	 * It splits endpoints according to "**" characters.
	 * 
	 * @param string
	 * @return
	 */
	public static List<String> splitEndpoints(String endpoints) {
		List<String> endpointListForOneTriple = new Vector<String>();
		if (endpoints.indexOf("**") == -1)
			endpointListForOneTriple.add(endpoints);
		else {
			while (endpoints.indexOf("**") > 5) {
				String newEndpoint = endpoints.substring(0,
						endpoints.indexOf("**"));
				endpoints = endpoints.substring(endpoints.indexOf("**") + 2);
				endpointListForOneTriple.add(newEndpoint);
			}
			endpointListForOneTriple.add(endpoints);
		}
		return endpointListForOneTriple;
	}

	/**
	 * It concats given endpoint list in a String variable with "**" character.
	 */
	public static String concatEndpoints(List<String> endpointList) {
		String endpoints = "";
		for (String string : endpointList) {
			endpoints += string + "**";
		}
		endpoints = endpoints.substring(0, endpoints.length() - 2);
		return endpoints;
	}
}
