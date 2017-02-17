package players;

import deciders.Decider;
import deciders.RandomDecider;
import evaluators.Evaluator;
import evaluators.ScoreEvaluator;

public final class PlayerFactory {

	public static Player createPlayer(int counter, String type, String decider, String evaluator) {
		
		// Determine the Player object to create first.
		if (type.equals("Player")) {
			return new HumanPlayer(counter);
		} else if (type.equals("AI")) {
			
			// Create variables to hold AI objects.
			Decider d = null; 
			Evaluator e = null;
			
			// Determine the Decider to be created.
			if (decider.equals("Random")) {
				d = new RandomDecider();
			} else {
				throw new IllegalArgumentException("Invalid Decider type.");
			}
			
			// Determine the Evaluator to be created.
			if (evaluator.equals("Score")) {
				e = new ScoreEvaluator();
			} else {
				throw new IllegalArgumentException("Invalid Decider type.");
			}
			
			// Return the AI object.
			return new AIPlayer(counter, d, e);
			
		} else {
			throw new IllegalArgumentException("Player type must be either Human or AI.");
		}
	}

}
