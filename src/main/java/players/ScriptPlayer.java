package players;

import java.awt.Point;

import game.GameScript;
import game.GameState;
import ui.GamePanel;

public class ScriptPlayer extends Player {
	
	private GameScript gs;

	public ScriptPlayer(int id, GameScript gs) {
		super(id);
		this.gs = gs;
	}

	@Override
	public String getPlayerType() {
		return "Script";
	}

	@Override
	public String toFileString() {
		return "Script";
	}

	@Override
	public Point getMove(GameState game, GamePanel panel) {
		return gs.getMove(game.getTurnNumber()-1);
	}

}
