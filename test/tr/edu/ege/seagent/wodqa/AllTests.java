package tr.edu.ege.seagent.wodqa;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import tr.edu.ege.seagent.wodqa.analyzer.AllAnalyzerTests;
import tr.edu.ege.seagent.wodqa.analyzer.externalrulerevising.RevisingExternalRule11Test;
import tr.edu.ege.seagent.wodqa.analyzer.externalrulerevising.RevisingExternalRule5Test;
import util.GeneratingParsableReorganizedQueryTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ RevisingExternalRule5Test.class,
		RevisingExternalRule11Test.class, AllAnalyzerTests.class,
		QueryReorganizerTest.class, URISpaceFinderTest.class,
		GeneratingParsableReorganizedQueryTest.class })
public class AllTests {

}
