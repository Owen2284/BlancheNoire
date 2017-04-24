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
	private String output;
	
	/**
	 * Basic constructor, which takes a counter ID.
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
	
	/**
	 * Function that returns the player's most recent output.
	 */
	public String getOutput() {
		return this.output;
	}
	
	/**
	 * Function that sets the output text to a standard format.
	 */
	public void setOutput(String message) {
		this.output = this.getPlayerType() + ": " + message;
	}
	
	/**
	 * Function that creates a file string that accurately represents the player object.
	 */
	public abstract String toFileString();
	
	/**
	 * Returns the coordinates the player wishes to play a counter at.
	 */
	public abstract Point getMove(GameState game, GamePanel panel);
}
