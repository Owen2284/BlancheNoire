package players;

import java.awt.Point;

import deciders.Decider;
import evaluators.Evaluator;
import game.GameState;
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
	 * Constructor for the AIPlayer.
	 * @param id - the id number of the counter that this player will control.
	 * @param d - the Decider object that the AIPlayer will use when deciding moves to play.
	 * @param e - the Evaluator object that the AIPlayer will use when evaluating moves.
	 */
	public AIPlayer(int id, Decider d, Evaluator e, int maxSearchTime) {
		super(id);
		this.d = d;
		this.e = e;
		this.maxSearchTime = maxSearchTime;
	}
	
	@Override
	public String getPlayerType() {
		return "AI(" + d.getType() + "," + e.getType() + ")"; 
	}
	
	@Override
	public String toFileString() {
		return "AI(" + d.toFileString() + "," + e.toFileString() + ")";
	}

	@Override
	public Point getMove(GameState game, GamePanel panel) {
		return d.decide(game, e, this, this.maxSearchTime);
	}

}
