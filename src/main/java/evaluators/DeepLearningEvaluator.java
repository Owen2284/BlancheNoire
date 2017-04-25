package evaluators;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;

import games.GameState;
import players.Player;

/**
 * Evaluator that uses a neural network to determine the worth of a games state.
 */
public class DeepLearningEvaluator extends Evaluator {

	private String netPath;
	private MultiLayerNetwork net;

	public DeepLearningEvaluator(String netPath) {
		this.netPath = netPath;
		try {
			net = ModelSerializer.restoreMultiLayerNetwork(netPath);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid file path for NN: \"" + netPath + "\"");
		}
	}

	public String getType() {
		return "DeepLearning";
	}

	public String toFileString() {return "DeepLearning("+this.netPath+")";}

	public float evaluate(GameState game, Player p) {
		// Get output from the NN.
		INDArray result = net.output(game.toINDArray(p.getPlayerID(), game.getOpposingPlayer(p).getPlayerID()), false);
		// Determine the most likely score from the returned INDArray.
		int mostLikelyScore = -1;
		for (int i = 0; i < result.shape()[1]; ++i) {
			if (mostLikelyScore == -1 || result.getFloat(0, i) > result.getFloat(0, mostLikelyScore)) {
				mostLikelyScore = i;
			}
		}
		return (float)mostLikelyScore;
	}

}
