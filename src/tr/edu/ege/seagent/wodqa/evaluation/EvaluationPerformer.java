package tr.edu.ege.seagent.wodqa.evaluation;

public class EvaluationPerformer {
	private static final String RESULT_FILE_PATH = "evaluation/result.txt";
	private static final String VOID_FILE_DIRECTORY_PATH = "evaluation/voids";

	public static void main(String[] args) {
		try {
			WoDQAEvaluation woDQAEvaluation = new WoDQAEvaluation(
					VOID_FILE_DIRECTORY_PATH);
			woDQAEvaluation.evaluateOnFedBench(RESULT_FILE_PATH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
