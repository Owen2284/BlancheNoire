package deciders;

import java.awt.Point;

import evaluators.Evaluator;
import game.GameState;
import players.Player;

public abstract class Decider {
	
	private String type = "";

	public Decider() {}
	
	public String getType() {return this.type;}
	
	public abstract Point decide(GameState g, Evaluator e, Player p);

}
