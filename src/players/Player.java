package players;

import java.awt.Point;

import game.GameState;
import ui.GamePanel;

/**
 * An abstract class that represents an interface that Player classes must meet
 * to be used as a player in the Othello GameState.
 */
public abstract class Player {
	
	protected int id;			// Determines what counter the player controls.

	/**
	 * Constructor.
	 * @param id - The id number of the counter that the player will control. 
	 */
	public Player(int id) {
		this.id = id;
	}
	
	/**
	 * Returns the player's ID, which must match the number of the counter type
	 * they will control.
	 */
	public int getPlayerID() {return this.id;}
	
	/**
	 * Returns the player's type, which will typically be "Human" or "AI".
	 */
	public abstract String getPlayerType();
	
	public abstract String toFileString();
	
	/**
	 * Returns the coordinates the player wishes to play a counter at.
	 */
	public abstract Point getMove(GameState game, GamePanel panel);
}
