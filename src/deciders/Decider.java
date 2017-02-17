package deciders;

import java.awt.Point;

import evaluators.Evaluator;
import game.GameState;
import players.Player;

public abstract class Decider {

	public Decider() {}
	
	public abstract String getType();
	
	public abstract Point decide(GameState g, Evaluator e, Player p);

}
