package evaluators;

import game.GameState;
import players.Player;

public class ScoreEvaluator extends Evaluator {

	public ScoreEvaluator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int Evaluate(GameState g, Player p) {
		int id = p.getPlayerID();
		return g.getScore(id) - g.getScore(1+(1-id));
	}

}
