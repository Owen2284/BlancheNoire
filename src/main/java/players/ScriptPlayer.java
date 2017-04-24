package players;

import java.awt.Point;

import game.GameScript;
import game.GameState;
import ui.GamePanel;

/**
 * Player class used by GameScript objects.
 */
public class ScriptPlayer extends Player {
	
	private GameScript gs;

	/**
	 * Default constructor. Requires a GameScript object and a counter ID.
	 */
	public ScriptPlayer(int id, GameScript gs) {
		super(id);
		this.gs = gs;
	}

	public String getPlayerType() {
		return "Script";
	}

	public String toFileString() {
		return "Script";
	}

	public Point getMove(GameState game, GamePanel panel) {
		return gs.getMove(game.getTurnNumber()-1);
	}

}
