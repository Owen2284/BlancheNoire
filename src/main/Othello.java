package main;

import java.awt.Point;

import javax.swing.JFrame;

import game.GameState;
import players.Player;
import players.PlayerFactory;
import ui.OthelloFrame;
import ui.ScorePanel;

public class Othello {
	
	public static void main(String[] args) {
		
		// Creates the player objects.
		//Player p1 = PlayerFactory.createPlayer(GameState.COUNTER_DARK, "Human", "", "");
		Player p1 = PlayerFactory.createPlayer(GameState.COUNTER_DARK, "AI", "Random", "Score");
		Player p2 = PlayerFactory.createPlayer(GameState.COUNTER_LIGHT, "AI", "Random", "Score");
		
		// Creates basic game.
		GameState game = new GameState(p1, p2);
		
		// Creates game UI.
		OthelloFrame ui = new OthelloFrame();
		
		// Initialises other variable(s).
		int nextPlayerNumber = 0;
		
		while (!game.isOver()) {
			
			// Display the current game state.
			System.out.println(game);
			
			// Retrieve the player that is next to play.
			Player playerToPlay = game.getPlayer(nextPlayerNumber);
			
			// Determine if the player can make a move or not.
			if (game.hasLegalMoves(playerToPlay)) {
			
				// Ask the player to determine their move.
				Point moveToPlay = playerToPlay.getMove(game);
				
				// Plays the move onto the game board, and stores the new GameState.
				game = game.playMove(playerToPlay, moveToPlay);
			
			}
			
			// Changes the player to play next and loops back to the start of the game loop.
			nextPlayerNumber = (nextPlayerNumber + 1) % 2;
			
		}
		
		// Display the final game state.
		System.out.println(game);
		
	}

}
