package evaluators;

import game.GameState;
import players.Player;

/**
 * Evaluates game states based on the difference between the two player's scores.
 */
public class ScoreEvaluator extends Evaluator {

	public ScoreEvaluator() {}

	public String getType() {
		return "Score";
	}

	public String toFileString() {
		return "Score(" + ")";
	}

	public float evaluate(GameState game, Player p) {
		return game.getScoreOfPlayer(p) - game.getScoreOfPlayer(game.getOpposingPlayer(p));
	}


}
