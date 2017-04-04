package evaluators;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;

import game.GameState;
import players.Player;

public class DeepLearningEvaluator extends Evaluator {
	
	private MultiLayerNetwork net;

	public DeepLearningEvaluator(String netPath) {
		try {
			net = ModelSerializer.restoreMultiLayerNetwork(netPath);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid file path for NN.");
		}
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
		INDArray result = net.output(game.toINDArray(p.getPlayerID(), game.getOpposingPlayer(p).getPlayerID()), false);
		System.out.println(result);
		return 0;
	}

}
