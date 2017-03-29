package evaluators;

import game.GameState;
import players.Player;

/**
 * Abstract class representing the interface that classes that are used
 * as evaluators must use.
 */
public abstract class Evaluator {

	public Evaluator() {}
	
	public abstract String getType();
	
	public abstract String toFileString();
	
	public abstract float evaluate(GameState game, Player p);

}
