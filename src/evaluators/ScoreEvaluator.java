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
	public int evaluate(GameState g, Player p) {
		int id = p.getPlayerID();
		return g.getScore(id) - g.getScore(1+(1-id));
	}


}
