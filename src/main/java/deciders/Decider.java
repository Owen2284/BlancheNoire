package deciders;

import java.awt.Point;

import evaluators.Evaluator;
import games.GameState;
import players.Player;

/**
 * Abstract class representing the interface that classes that are used
 * as deciders must use.
 */
public abstract class Decider {

	public Decider() {}
	
	/**
	 * Returns the type of decider this class represents.
	 */
	public abstract String getType();

	/**
	 * Function that creates a file string that accurately represents the Decider object.
	 */
	public abstract String toFileString();
	
	/**
	 * Has the decider produce a move that the AI player can play, based on the GameState, Evaluator,
	 * Player and search time (im ms) provided.
	 */
	public abstract Point decide(GameState game, Evaluator e, Player p, int maxSearchTime);

}
