package evaluators;

import game.GameState;
import players.Player;

public abstract class Evaluator {
	
	private String type = "";

	public Evaluator() {}
	
	public String getType() {return this.type;}
	
	public abstract int Evaluate(GameState g, Player p);

}
