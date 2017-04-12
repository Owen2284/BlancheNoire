package evaluators;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;

import game.GameState;
import players.Player;

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
		for (int i = 0; i < result.shape()[1]; ++i) {

		}
		return 0;
	}

}
