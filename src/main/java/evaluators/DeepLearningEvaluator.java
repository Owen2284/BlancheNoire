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
		// Get output from the ANN.
		INDArray result = net.output(game.toINDArray(p.getPlayerID(), game.getOpposingPlayer(p).getPlayerID()), false);
		int numLabels = result.shape()[1];
		if (numLabels > 1) {
			// Creates a composite score by multiplying the label by the probability that it is that label.
			float weightedScore = 0;
			for (int i = 1; i < numLabels; ++i) {
				weightedScore += (result.getFloat(0, i) * i);
			}
			return (weightedScore * (100/(numLabels-1)));
		} else {
			return (result.getFloat(0, 0) * 100);
		}
	}

}
