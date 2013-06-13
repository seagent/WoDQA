package tr.edu.ege.seagent.wodqa.analyzer;

import java.util.List;
import java.util.Vector;

import org.junit.Ignore;
import org.junit.Test;

import tr.edu.ege.seagent.wodqa.query.analyzer.QueryConstructorUtil;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

@Ignore
public class QueryConstructorUtilTest {

	@Test
	public void updateTriplePatternsInQueryTest() throws Exception {
		String s = "SELECT * WHERE {?s ?p ?o. ?a ?b ?c}";

		List<Triple> tripleList = new Vector<Triple>();
		tripleList.add(new Triple(Node.createVariable("x"), Node
				.createVariable("y"), Node.createVariable("z")));
		tripleList.add(new Triple(Node.createVariable("d"), Node
				.createVariable("e"), Node.createVariable("f")));
		Query query = QueryFactory.create(s);
		System.out.println(query.serialize());
		QueryConstructorUtil.updateTriplesInGivenQueryBlock(
				query.getQueryPattern(), tripleList);
		System.out.println(query.serialize());
	}

}
