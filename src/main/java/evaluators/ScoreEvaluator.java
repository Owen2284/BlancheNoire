package evaluators;

import games.GameState;
import players.Player;

/**
 * Evaluates games states based on the difference between the two player's scores.
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
