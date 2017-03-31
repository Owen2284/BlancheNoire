package evaluators;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import game.GameState;
import players.Player;

public class DeepLearningEvaluator extends Evaluator {
	
	private MultiLayerNetwork net;

	public DeepLearningEvaluator(String netPath) {
		// TODO: Read in the fitted neural network.
		net = null;
	}

	@Override
	public String getType() {
		return "DeepLearning";
	}

	@Override
	public String toFileString() {
		return "DeepLearning("+")";
	}

	@Override
	public float evaluate(GameState game, Player p) {
		// TODO: Convert GameState to appropriate data format.
		//net.output(null, false);
		return 0;
	}

}
