package players;

import java.awt.Point;

import game.GameState;

public abstract class Player {
	
	protected int id;			// Determines what counter the player controls.

	public Player(int id) {
		this.id = id;
	}
	
	/*
	 * Returns the player's ID, which must match the number of the counter type
	 * they will control.
	 */
	public int getPlayerID() {return this.id;}
	
	/*
	 * Returns the player's type, which will typically be "Human" or "AI".
	 */
	public abstract String getPlayerType();
	
	/*
	 * Returns the coordinates the player wishes to play a counter at.
	 */
	public abstract Point getMove(GameState g);
}
