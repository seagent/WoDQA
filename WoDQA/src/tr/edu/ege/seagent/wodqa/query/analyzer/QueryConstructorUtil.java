package tr.edu.ege.seagent.wodqa.query.analyzer;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementBind;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.syntax.ElementVisitor;
import com.hp.hpl.jena.sparql.syntax.ElementVisitorBase;
import com.hp.hpl.jena.sparql.syntax.ElementWalker;

public class QueryConstructorUtil {

	/**
	 * It includes triple List of given Query.
	 */
	protected static List<Element> optionalElementsList = new Vector<Element>();

	public static List<Element> getOptionalBlocks(String sparqlQuery) {
		getOptionalElementsList().clear();
		// create query...
		Query query = QueryFactory.create(sparqlQuery);
		// get query pattern...
		Element queryPatternForFilterExpression = query.getQueryPattern();

		// create a visitor for visit optional blocks...
		ElementVisitorBase unionVisitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementOptional el) {
				getOptionalElementsList().add(el.getOptionalElement());
				super.visit(el);
			}
		};
		ElementWalker.walk(queryPatternForFilterExpression, unionVisitor);
		return getOptionalElementsList();
	}

	/**
	 * @param queryPart
	 *            is a query part may be union graph.
	 * @return
	 */
	public static List<Triple> getTriplesFromOptionalBlock(Element queryPart) {
		getOptionalElementsList().clear();
		// create a visitor for visit optional blocks...
		ElementVisitorBase unionVisitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementOptional el) {
				getOptionalElementsList().add(el.getOptionalElement());
				super.visit(el);
			}
		};
		ElementWalker.walk(queryPart, unionVisitor);
		List<Triple> triplesInOptional = new Vector<Triple>();
		for (Element element : getOptionalElementsList()) {
			ElementWalker.walk(element,
					createOptionalTripleVisitor(triplesInOptional));
		}
		return triplesInOptional;
	}

	/**
	 * Get all triples from optional block.
	 * 
	 * @param query
	 */
	public static List<Triple> getTriplesFromOptionalBlock(String query) {
		List<Element> optionalBlocks = getOptionalBlocks(query);
		List<Triple> triplesInOptional = new Vector<Triple>();
		for (Element element : optionalBlocks) {
			ElementWalker.walk(element,
					createOptionalTripleVisitor(triplesInOptional));
		}
		return triplesInOptional;
	}

	/**
	 * Create a triple visitor for optional graph.
	 * 
	 * @param triplesInOptional
	 * @return
	 */
	private static ElementVisitor createOptionalTripleVisitor(
			final List<Triple> triplesInOptional) {
		ElementVisitorBase tripleVisitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementPathBlock el) {
				Iterator<TriplePath> iterator = el.getPattern().iterator();
				while (iterator.hasNext()) {
					TriplePath triplePath = iterator.next();
					triplesInOptional.add(triplePath.asTriple());
				}
				super.visit(el);
			}
		};
		return tripleVisitor;
	}

	/**
	 * @return the optionalElementsList
	 */
	private static List<Element> getOptionalElementsList() {
		return optionalElementsList;
	}

	public static List<Element> setUnionBlockElementsToGivenList(
			String sparqlQuery, final List<Element> unionElementsList) {
		// create query...
		Query query = QueryFactory.create(sparqlQuery);
		return setUnionBlockElementsToGivenList(unionElementsList, query);
	}

	public static List<Element> setUnionBlockElementsToGivenList(Query query,
			final List<Element> unionElementsList) {
		return setUnionBlockElementsToGivenList(unionElementsList, query);
	}

	private static List<Element> setUnionBlockElementsToGivenList(
			final List<Element> unionElementsList, Query query) {
		// get query pattern...
		Element queryPatternForFilterExpression = query.getQueryPattern();

		// create a visitor for visit filter blocks...
		ElementVisitorBase unionVisitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementUnion el) {
				if (!((ElementGroup) el.getElements().get(0)).getElements()
						.get(0).getClass().equals(ElementBind.class)) {
					unionElementsList.addAll(el.getElements());
				}
				super.visit(el);
			}
		};
		ElementWalker.walk(queryPatternForFilterExpression, unionVisitor);
		return unionElementsList;
	}

	/**
	 * Create a triple visitor to get triple from Element instance.
	 * 
	 * @param visitedTPList
	 *            TODO
	 * 
	 * @return
	 */
	public static ElementVisitorBase createTripleVisitor(
			final List<Triple> visitedTPList) {
		ElementVisitorBase tripleVisitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementPathBlock el) {
				Iterator<TriplePath> iterator = el.getPattern().iterator();
				while (iterator.hasNext()) {
					TriplePath triplePath = iterator.next();
					visitedTPList.add(triplePath.asTriple());
				}
				super.visit(el);
			}
		};
		return tripleVisitor;
	}

	public static void setTriplesToGivenListByWalker(
			Element queryPatternForTriple, List<Triple> triplePatternList) {
		ElementWalker.walk(queryPatternForTriple,
				createTripleVisitor(triplePatternList));
	}

	/**
	 * Create a triple visitor to get triple from Element instance.
	 * 
	 * @param newTripleSet
	 *            TODO
	 * 
	 * @return
	 */
	public static ElementVisitorBase createTripleUpdater(
			final List<Triple> newTripleSet) {
		ElementVisitorBase tripleVisitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementPathBlock el) {
				Iterator<TriplePath> iterator = el.getPattern().iterator();
				while (iterator.hasNext()) {
					iterator.next();
					iterator.remove();
				}
				for (Triple triple : newTripleSet) {
					el.addTriple(triple);
				}
				super.visit(el);
			}
		};
		return tripleVisitor;
	}

	public static void updateTriplesInGivenQueryBlock(
			Element updatedTriplePatternBlock, List<Triple> newTriplePatternList) {
		ElementWalker.walk(updatedTriplePatternBlock,
				createTripleUpdater(newTriplePatternList));
	}

	public static void addGivenElementsToGivenQueryBlock(
			Element updatedTriplePatternBlock,
			List<Triple> optionalTriplePatternList,
			List<Triple> allOptionalTriples) {
		ElementWalker.walk(
				updatedTriplePatternBlock,
				createTripleBlocks(optionalTriplePatternList,
						allOptionalTriples));
	}

	/**
	 * Create a triple visitor to get triple from Element instance.
	 * 
	 * @param newTripleSet
	 *            TODO
	 * @param isOptional
	 *            TODO
	 * 
	 * @return
	 */
	public static ElementVisitorBase createTripleBlocks(
			final List<Triple> newTripleSet, List<Triple> allOptionalTriples) {
		final List<Element> elements = new Vector<Element>();
		int i = 0;
		int j = 0;
		while (i < newTripleSet.size()) {
			ElementTriplesBlock etb = new ElementTriplesBlock();
			ElementOptional eo;
			boolean isOpt = false;
			for (j = i; j < newTripleSet.size(); j++) {
				if (i == j) {
					if (allOptionalTriples.contains(newTripleSet.get(j)))
						isOpt = true;
					etb.addTriple(newTripleSet.get(j));
				} else {
					if (isOpt
							&& allOptionalTriples.contains(newTripleSet.get(j))) {
						etb.addTriple(newTripleSet.get(j));
					} else if (!isOpt
							&& !allOptionalTriples
									.contains(newTripleSet.get(j))) {
						etb.addTriple(newTripleSet.get(j));
					} else
						break;
				}
			}
			i = j;
			if (isOpt) {
				eo = new ElementOptional(etb);
				elements.add(eo);
			} else
				elements.add(etb);
		}

		ElementVisitorBase tripleVisitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementGroup el) {
				for (Element element : elements) {
					el.addElement(element);
				}
				// add el to elOpt
				super.visit(el);
			}

		};
		return tripleVisitor;
	}

	public static List<Triple> checkOptionalBlock(Element queryPattern) {
		List<Triple> optionalTriples = new Vector<Triple>();
		ElementWalker
				.walk(queryPattern, createOptionalChecker(optionalTriples));
		return optionalTriples;
	}

	private static ElementVisitor createOptionalChecker(
			final List<Triple> optionalTriples) {
		ElementVisitorBase optionalVisitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementOptional el) {
				List<Triple> optTrip = new Vector<Triple>();
				ElementWalker.walk(el.getOptionalElement(),
						createTripleVisitor(optTrip));
				optionalTriples.addAll(optTrip);
				super.visit(el);
			}
		};
		return optionalVisitor;
	}

	public static Element deleteAllTriplesFromQuery(Element queryPattern) {
		ElementWalker.walk(queryPattern, clearQueryPattern());
		return queryPattern;
	}

	private static ElementVisitor clearQueryPattern() {
		ElementVisitorBase visitor = new ElementVisitorBase() {
			@Override
			public void visit(ElementGroup el) {
				el.getElements().clear();
				super.visit(el);
			}

			// @Override
			// public void visit(ElementPathBlock el) {
			// ListIterator<TriplePath> iterator = el.getPattern().iterator();
			// while (iterator.hasNext()) {
			// iterator.next();
			// iterator.remove();
			// }
			// super.visit(el);
			// }
		};
		return visitor;
	}
}
