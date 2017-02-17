package players;

import java.awt.Point;

import deciders.Decider;
import evaluators.Evaluator;
import game.GameState;

public class AIPlayer extends Player {
	
	private Decider d;
	private Evaluator e;

	public AIPlayer(int id, Decider d, Evaluator e) {
		super(id);
		this.d = d;
		this.e = e;
	}
	
	@Override
	public String getPlayerType() {
		return "AI(" + d.getType() + "," + e.getType() + ")"; 
	}

	@Override
	public Point getMove(GameState g) {
		return d.decide(g, e, this);
	}

}
