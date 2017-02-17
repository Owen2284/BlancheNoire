package evaluators;

import game.GameState;
import players.Player;

public abstract class Evaluator {

	public Evaluator() {}
	
	public abstract String getType();
	
	public abstract int evaluate(GameState g, Player p);

}
