package main;

import game.GameState;
import game.Player;

public class Othello {
	
	public static void main(String[] args) {
		GameState game = new GameState(new Player(), new Player());
		System.out.println(game);
		boolean[][] moves = game.getLegalMoves(game.getPlayer(0));
		for (int row = 0; row < game.getBoardDims()[0]; ++row) {
			for (int col = 0; col < game.getBoardDims()[1]; ++col) {
				boolean canUse = moves[row][col];
				if (canUse) {
					System.out.print("T");
				} else {
					System.out.print("F");
				}
			}
			System.out.println("");
		}
	}

}
