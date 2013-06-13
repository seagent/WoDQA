package tr.edu.ege.seagent.wodqa.analyzer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ QueryAnalyzerTest.class, RelevantDatasetsForTripleTest.class,
		RuleExecutorTest.class })
public class AllAnalyzerTests {

}
