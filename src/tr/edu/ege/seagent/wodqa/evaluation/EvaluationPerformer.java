package tr.edu.ege.seagent.wodqa.evaluation;

public class EvaluationPerformer {
	public static void main(String[] args) {
		try {
			WoDQAEvaluation woDQAEvaluation = new WoDQAEvaluation(
					"evaluation/voids", "evaluation/result.txt");
			woDQAEvaluation.evaluateFedBench();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
