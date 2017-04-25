package players;

import java.awt.Point;

import deciders.Decider;
import evaluators.Evaluator;
import games.GameState;
import ui.GamePanel;

/**
 * A Player subclass that represents an AI player. It's behaviour is determined
 * by the Decider and Evaluator objects it is composed of.
 */
public class AIPlayer extends Player {
	
	private Decider d;
	private Evaluator e;
	
	int maxSearchTime;

	/**
	 * Constructor for the AIPlayer, when given a counter ID, a Decider and Evaluator to use, and a search time limit.
	 */
	public AIPlayer(int id, Decider d, Evaluator e, int maxSearchTime) {
		super(id);
		this.d = d;
		this.e = e;
		this.maxSearchTime = maxSearchTime;
	}

	public String getPlayerType() {
		return "AI(" + d.getType() + "," + e.getType() + ")"; 
	}

	public String toFileString() {
		return "AI(" + d.toFileString() + "," + e.toFileString() + ")";
	}

	public Point getMove(GameState game, GamePanel panel) {
		return d.decide(game, e, this, this.maxSearchTime);
	}

}
