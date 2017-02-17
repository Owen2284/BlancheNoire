package main;

import game.GameState;
import players.HumanPlayer;

public class Test {

	public static void main(String[] args) {
		GameState game = new GameState(new HumanPlayer(GameState.COUNTER_DARK), new HumanPlayer(GameState.COUNTER_LIGHT));
		System.out.println(game);
		boolean[][] moves = game.getLegalMoves(game.getPlayer(0));
		for (int row = 0; row < game.getBoardDims()[0]; ++row) {
			for (int col = 0; col < game.getBoardDims()[1]; ++col) {
				boolean canUse = moves[row][col];
				if (canUse) {System.out.print("T");} 
				else {System.out.print("F");}
			}
			System.out.println("");
		}
		System.out.println(game.getScore(game.getPlayer(0)));
		System.out.println(game.getScoreOfPlayer(1));
		game.printLinesFrom(2,3,game.getPlayer(0).getPlayerID());
		game.printLinesFrom(7,7,game.getPlayer(0).getPlayerID());
	}

}
