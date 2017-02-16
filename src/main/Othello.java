package main;

import java.awt.Point;

import game.GameState;
import game.HumanPlayer;
import game.Player;

public class Othello {
	
	public static void main(String[] args) {
		GameState game = new GameState(new HumanPlayer(GameState.COUNTER_DARK), new HumanPlayer(GameState.COUNTER_LIGHT));
		
		int nextPlayerNumber = 0;
		
		while (!game.isOver()) {
			
			// Retrieve the player that is next to play.
			Player playerToPlay = game.getPlayer(nextPlayerNumber);
			
			// Ask the player to determine their move.
			Point moveToPlay = playerToPlay.getMove(game);
			
			// Plays the move onto the game board, and stores the new GameState.
			game = game.playMove(playerToPlay, moveToPlay);
			
			// Changes the player to play next and loops back to the start of the game loop.
			nextPlayerNumber = (nextPlayerNumber + 1) % 2;
			
		}
		
	}

}
