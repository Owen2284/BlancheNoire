package players;

import java.awt.Point;

import game.GameState;

public abstract class Player {
	
	protected int id;			// Determines what counter the player controls.
	protected String type;

	public Player(int id) {
		this.id = id;
		this.type = "";
	}
	
	/*
	 * Returns the player's ID, which must match the number of the counter type
	 * they will control.
	 */
	public int getPlayerID() {return this.id;}
	
	/*
	 * Returns the player's type, which will typically be "Human" or "AI".
	 */
	public String getPlayerType() {return this.type;}
	
	/*
	 * Returns the coordinates the player wishes to play a counter at.
	 */
	public abstract Point getMove(GameState g);
}
