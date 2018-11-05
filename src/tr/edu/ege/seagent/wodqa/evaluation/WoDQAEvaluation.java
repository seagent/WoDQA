package tr.edu.ege.seagent.wodqa.evaluation;

import java.io.FileWriter;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

import tr.edu.ege.seagent.wodqa.exception.InactiveEndpointException;
import tr.edu.ege.seagent.wodqa.exception.QueryHeaderException;
import tr.edu.ege.seagent.wodqa.query.WodqaEngine;
import tr.edu.ege.seagent.wodqa.voiddocument.VoidModelConstructor;

public class WoDQAEvaluation {

	private static final boolean CACHE_ASK_QUERIES = true;

	private static final int QUERY_EVALUATION_COUNT = 20;

	private static final String LINKED_DATA_PREFIX = "LINKED DATA QUERY-";
	
	private static final String SP2B_PREFIX = "SP2B QUERY-";
	
	private static final String LIFE_SCIENCES_PREFIX = "LIFE SCIENCES QUERY-";

	private static final String CROSS_DOMAIN_PREFIX = "CROSS DOMAIN QUERY-";

	private static final String DEFAULT_RESULT_FILE_PATH = "evaluation/result.txt";

	private static final String DEFAULT_VOID_FILE_DIRECTORY = "evaluation/voids";

	private WodqaEngine wodqaEngine;

	private Model mainModel;

	private Logger logger;

	private String queryResults;

	private String voidFileDir;

	private List<String> avreageAnalyisTimes;
	private List<String> avreageExecutionTimes;

	public WoDQAEvaluation(String voidFileDir) throws MalformedURLException {
		if (voidFileDir == null) {
			voidFileDir = DEFAULT_VOID_FILE_DIRECTORY;
		}
		this.queryResults = "";
		this.voidFileDir = voidFileDir;
		this.logger = Logger.getLogger(WoDQAEvaluation.class);
		this.logger.setLevel(Level.DEBUG);

		// initialize average analyis and execution times...
		this.avreageAnalyisTimes = new ArrayList<String>();
		this.avreageExecutionTimes = new ArrayList<String>();

		mainModel = VoidModelConstructor.constructVOIDSpaceModel(voidFileDir);
		this.wodqaEngine = new WodqaEngine(false, false);
	}

	public void evaluateOnFedBench(String resultFilePath) throws Exception {
		evaluateCrossDomain();
		evaluateLifeSciences();
//		evaluateSP2B();
//		evaluateLinkedData();
		calculateFinalResults(resultFilePath);
	}

	private void evaluateCrossDomain() throws Exception {
		executeQuery(Queries.CROSS_DOMAIN_QUERY_1,
				getQueryName(CROSS_DOMAIN_PREFIX, 1), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.CROSS_DOMAIN_QUERY_2,
				getQueryName(CROSS_DOMAIN_PREFIX, 2), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.CROSS_DOMAIN_QUERY_3,
				getQueryName(CROSS_DOMAIN_PREFIX, 3), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.CROSS_DOMAIN_QUERY_4,
				getQueryName(CROSS_DOMAIN_PREFIX, 4), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.CROSS_DOMAIN_QUERY_5,
				getQueryName(CROSS_DOMAIN_PREFIX, 5), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.CROSS_DOMAIN_QUERY_6,
				getQueryName(CROSS_DOMAIN_PREFIX, 6), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.CROSS_DOMAIN_QUERY_7,
				getQueryName(CROSS_DOMAIN_PREFIX, 7), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);
	}

	private void evaluateLifeSciences() throws Exception {
		executeQuery(Queries.LIFE_SCIENCES_QUERY_1,
				getQueryName(LIFE_SCIENCES_PREFIX, 1), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.LIFE_SCIENCES_QUERY_2,
				getQueryName(LIFE_SCIENCES_PREFIX, 2), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.LIFE_SCIENCES_QUERY_3,
				getQueryName(LIFE_SCIENCES_PREFIX, 3), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.LIFE_SCIENCES_QUERY_4,
				getQueryName(LIFE_SCIENCES_PREFIX, 4), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.LIFE_SCIENCES_QUERY_5,
				getQueryName(LIFE_SCIENCES_PREFIX, 5), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.LIFE_SCIENCES_QUERY_6,
				getQueryName(LIFE_SCIENCES_PREFIX, 6), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.LIFE_SCIENCES_QUERY_7,
				getQueryName(LIFE_SCIENCES_PREFIX, 7), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);
	}
	
	private void evaluateSP2B() throws Exception {
		executeQuery(Queries.SP2B_Q1,
				getQueryName(SP2B_PREFIX, 1), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		//
		executeQuery(Queries.SP2B_Q2,
				getQueryName(SP2B_PREFIX, 2), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.SP2B_Q3a,
				getQueryName(SP2B_PREFIX, 3), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.SP2B_Q3b,
				getQueryName(SP2B_PREFIX, 4), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);
		
		executeQuery(Queries.SP2B_Q3c,
				getQueryName(SP2B_PREFIX, 5), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		//
		executeQuery(Queries.SP2B_Q4a,
				getQueryName(SP2B_PREFIX, 6), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);
		
		//
		executeQuery(Queries.SP2B_Q4b,
				getQueryName(SP2B_PREFIX, 7), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		//
		executeQuery(Queries.SP2B_Q5,
				getQueryName(SP2B_PREFIX, 8), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);
		
		executeQuery(Queries.SP2B_Q6,
				getQueryName(SP2B_PREFIX, 9), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.SP2B_Q7,
				getQueryName(SP2B_PREFIX, 10), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);
		
		//
		executeQuery(Queries.SP2B_Q8,
				getQueryName(SP2B_PREFIX, 11), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		//
		executeQuery(Queries.SP2B_Q9,
				getQueryName(SP2B_PREFIX, 12), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);
		
		executeQuery(Queries.SP2B_Q10,
				getQueryName(SP2B_PREFIX, 13), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		//
		executeQuery(Queries.SP2B_Q11,
				getQueryName(SP2B_PREFIX, 14), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);
	}
	
	private void evaluateLinkedData() throws Exception {
		executeQuery(Queries.LINKED_DATA_1,
				getQueryName(LINKED_DATA_PREFIX, 1), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.LINKED_DATA_2,
				getQueryName(LINKED_DATA_PREFIX, 2), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);
		
		executeQuery(Queries.LINKED_DATA_3,
				getQueryName(LINKED_DATA_PREFIX, 3), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.LINKED_DATA_4,
				getQueryName(LINKED_DATA_PREFIX, 4), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);
		
		executeQuery(Queries.LINKED_DATA_5,
				getQueryName(LINKED_DATA_PREFIX, 5), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.LINKED_DATA_6,
				getQueryName(LINKED_DATA_PREFIX, 6), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);
		
		executeQuery(Queries.LINKED_DATA_7,
				getQueryName(LINKED_DATA_PREFIX, 7), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.LINKED_DATA_8,
				getQueryName(LINKED_DATA_PREFIX, 8), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);
		
		executeQuery(Queries.LINKED_DATA_9,
				getQueryName(LINKED_DATA_PREFIX, 9), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

		executeQuery(Queries.LINKED_DATA_10,
				getQueryName(LINKED_DATA_PREFIX, 10), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);
		
		executeQuery(Queries.LINKED_DATA_11,
				getQueryName(LINKED_DATA_PREFIX, 11), CACHE_ASK_QUERIES,
				QUERY_EVALUATION_COUNT);

	}

	public String getQueryName(String queryPrefix, int queryNo) {
		return queryPrefix + queryNo;
	}

	/**
	 * It executes the given query.
	 * 
	 * @param query
	 * @param queryName
	 * @param askOpt
	 * @param executionCount
	 * @throws Exception
	 * @throws QueryHeaderException
	 * @throws InactiveEndpointException
	 */
	public String executeQuery(String query, String queryName, boolean askOpt,
			int executionCount) throws Exception, QueryHeaderException,
			InactiveEndpointException {
		String subResult = "";
		String executionTimes = "";
		String analyzeTimes = "";
		String totalTimes = "";
		List<Long> totalAnalysisTimes = new ArrayList<Long>();
		List<Long> totalExecutionTimes = new ArrayList<Long>();
		List<Long> totalQueryTimes = new ArrayList<Long>();
		for (int count = 0; count < executionCount; count++) {
			long analysisStartTime = System.currentTimeMillis();
			String federatedQuery = wodqaEngine.federateQuery(mainModel, query,
					askOpt);
			long analysisTime = System.currentTimeMillis() - analysisStartTime;
			analyzeTimes += analysisTime + ", ";

			long executionTime = 0;
			long executionStartTime = System.currentTimeMillis();
			ResultSet res = wodqaEngine.executeSelect(federatedQuery);
			// get the only resultset or parse it too
			iterateOnResults(res, true);
			executionTime = System.currentTimeMillis() - executionStartTime;
			executionTimes += executionTime + ", ";
			logger.info(MessageFormat.format("Anaysis time: {0}", analysisTime));
			logger.info(MessageFormat.format("Execution time: {0}",
					executionTime));
			long totalTime = analysisTime + executionTime;
			totalTimes += totalTime + ", ";
			logger.info(MessageFormat.format("Total time: {0}", totalTime));
			totalAnalysisTimes.add(analysisTime);
			totalExecutionTimes.add(executionTime);
			totalQueryTimes.add(totalTime);

		}

		subResult += "\n#####################################" + queryName
				+ "#####################################";

		long averageAnalysisTime = calculateAverageTime(totalAnalysisTimes);
		subResult += "\n All Analysis Times: " + analyzeTimes
				+ " Average Analysis Time: " + averageAnalysisTime;
		long averageExecutionTime = calculateAverageTime(totalExecutionTimes);
		subResult += "\n All Execution Times: " + executionTimes
				+ ", Average Execution Time: " + averageExecutionTime;
		subResult += "\n All Total Times: " + totalTimes
				+ " Average Total Time: "
				+ calculateAverageTime(totalQueryTimes);
		subResult += "\n##############################################################################################";

		logger.debug(subResult);
		queryResults += subResult;

		avreageAnalyisTimes.add(queryName + ": " + averageAnalysisTime);
		avreageExecutionTimes.add(queryName + ": " + averageExecutionTime);

		return analyzeTimes;
	}

	private void iterateOnResults(ResultSet res, boolean parseAllResults) {
		if (parseAllResults) {
			while (res.hasNext()) {
				res.next();
			}
		} else {
			res.hasNext();
		}
	}

	/**
	 * This method calculates average time except from three maximum time.
	 * 
	 * @param totalTimes
	 * @return
	 */
	private long calculateAverageTime(List<Long> totalTimes) {
		long totalTime = 0;
		if (totalTimes.size() > 3) {
			// remove max two element
			int maxIndex = findMaxTimeIndex(totalTimes);
			totalTimes.remove(maxIndex);
			maxIndex = findMaxTimeIndex(totalTimes);
			totalTimes.remove(maxIndex);
			maxIndex = findMaxTimeIndex(totalTimes);
			totalTimes.remove(maxIndex);
		}
		// calculate total time
		for (long time : totalTimes) {
			totalTime += time;
		}
		// return average time
		return totalTime / totalTimes.size();
	}

	/**
	 * Find index of maximum element contained in total times.
	 * 
	 * @param totalTimes
	 * @return
	 */
	private int findMaxTimeIndex(List<Long> totalTimes) {
		long maxTime = 0;
		int maxIndex = 0;
		for (int i = 0; i < totalTimes.size(); i++) {
			long time = totalTimes.get(i);
			if (time > maxTime) {
				maxTime = time;
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	private void calculateFinalResults(String resultFilePath) throws Exception {

		if (resultFilePath == null) {
			resultFilePath = DEFAULT_RESULT_FILE_PATH;
		}

		String averageAnalysisTimeResults = "\n Average Analysis Times: ";
		averageAnalysisTimeResults += "\n****************************************\n";
		String averageExecutionTimeResults = "\n Average Execution Times: ";
		averageExecutionTimeResults += "\n****************************************\n";

		for (String averageAnalysisTime : avreageAnalyisTimes) {
			averageAnalysisTimeResults += averageAnalysisTime + "\n";
		}
		averageAnalysisTimeResults += "****************************************\n";

		for (String averageExecutionTime : avreageExecutionTimes) {
			averageExecutionTimeResults += averageExecutionTime + "\n";
		}
		averageExecutionTimeResults += "****************************************\n";

		queryResults += averageAnalysisTimeResults;
		queryResults += averageExecutionTimeResults;

		logger.info(queryResults);
		FileWriter fileWriter = new FileWriter(resultFilePath);
		fileWriter.write(queryResults);
		fileWriter.close();
	}

}
