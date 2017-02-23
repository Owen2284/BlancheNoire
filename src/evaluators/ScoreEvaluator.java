package evaluators;

import game.GameState;
import players.Player;

public class ScoreEvaluator extends Evaluator {

	public ScoreEvaluator() {}

	@Override
	public String getType() {
		return "Score";
	}
	
	@Override
	public float evaluate(GameState game, Player p) {
		return game.getScore(p) - game.getScore(game.getOpposingPlayer(p));
	}


}
