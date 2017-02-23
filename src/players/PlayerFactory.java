package players;

import deciders.Decider;
import deciders.MinimaxDecider;
import deciders.RandomDecider;
import evaluators.Evaluator;
import evaluators.PositionalEvaluator;
import evaluators.ScoreEvaluator;

public final class PlayerFactory {

	/**
	 * A method that produces a Player object based on the input parameters.
	 * @param counter - An integer number representing the counter that the player will control (similar to the id param on other methods).
	 * @param type - The type of player that this player will be (e.g. either Human or AI).
	 * @param decider - The name of the decider class that the player will use (e.g. Random, Minimax, MCTS, etc.)
	 * @param evaluator - The name of the evaluator class that the player will use (e.g. Score, DeepLearning, etc.)
	 * @return a Player object.
	 */
	public static Player createPlayer(int counter, String type, String decider, String evaluator, boolean usingGUI, int maxSearchTime, boolean defaultToNull) {
		
		// Determine the Player object to create first.
		if (type.equals("Human")) {
			
			// Returns a HumanPlayer object
			return new HumanPlayer(counter, usingGUI);
			
		} else if (type.equals("AI")) {
			
			// Create variables to hold AI objects.
			Decider d = null; 
			Evaluator e = null;
			
			// Determine the Decider to be created.
			if (decider.equals("Random")) {
				d = new RandomDecider();
			} else if (decider.equals("Minimax")) {
				d = new MinimaxDecider();
			} else {
				if (!defaultToNull) {d = new RandomDecider();}				
				throw new IllegalArgumentException("Invalid Decider type.");
			}
			
			// Determine the Evaluator to be created.
			if (evaluator.equals("Score")) {
				e = new ScoreEvaluator();
			} else if (evaluator.equals("Positional")) {
				e = new PositionalEvaluator();
			} else {
				if (!defaultToNull) {e = new ScoreEvaluator();}				
				throw new IllegalArgumentException("Invalid Decider type.");
			}
			
			// Return the AIPlayer object.
			return new AIPlayer(counter, d, e, maxSearchTime);
			
		} else {
			throw new IllegalArgumentException("Player type must be either Human or AI.");
		}
		
	}

}
