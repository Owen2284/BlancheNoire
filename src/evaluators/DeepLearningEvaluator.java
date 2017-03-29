package evaluators;

import game.GameState;
import players.Player;

public class DeepLearningEvaluator extends Evaluator {
	
	// Neural network field.

	public DeepLearningEvaluator() {
		// Create the neural network / load the neural network.
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
		// Get the neural network.
		// Pass the current game state through it.
		// Return output.
		return 0;
	}

}
