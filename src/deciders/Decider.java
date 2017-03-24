package deciders;

import java.awt.Point;

import evaluators.Evaluator;
import game.GameState;
import players.Player;

/**
 * Abstract class representing the interface that classes that are used
 * as deciders must use.
 */
public abstract class Decider {
	
	protected String output = "";

	public Decider() {}
	
	/**
	 * Returns the type of decider this class represents.
	 */
	public abstract String getType();
	
	public abstract String toFileString();
	
	/**
	 * Has the decider produce a move that the AI player can play.
	 * @param g - the GameState to evaluate.
	 * @param e - the Evaluator to use.
	 * @param p - the Player making the move.
	 * @return a Point on the game board that the Decider wants to play a counter at.
	 */
	public abstract Point decide(GameState game, Evaluator e, Player p, int maxSearchTime);

}
