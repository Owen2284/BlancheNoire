package players;

import java.awt.Point;
import java.util.Scanner;

import game.GameState;

public class HumanPlayer extends Player {

	public HumanPlayer(int id) {
		super(id);
	}
	
	@Override
	public String getPlayerType() {
		return "Human";
	}
		
	@Override
	public Point getMove(GameState g) {
		return getMoveFromCommandPrompt(g);
	}
	
	public Point getMoveFromCommandPrompt(GameState g) {
		
		Scanner sc = new Scanner(System.in);
		int[] coord = new int[2];
		coord[0] = -1; coord[1] = -1;
		boolean[][] allowedMoves = g.getLegalMoves(this);
		boolean goodMove = false;
		
		while (!goodMove) {
			System.out.println("Enter the row you wish to place a counter at:");
			coord[0] = sc.nextInt();
			System.out.println("Enter the column you wish to place a counter at:");
			coord[1] = sc.nextInt();
			if (coord[0] >= 0 && coord[0] < g.getBoardDims()[0] && coord[1] >= 0 && coord[1] < g.getBoardDims()[1] && allowedMoves[coord[0]][coord[1]]) {
				goodMove = true;
			} else {
				System.out.println("Invalid move.");
			}
		}
		
		sc.close();
		
		return new Point(coord[0], coord[1]);
		
	}



}
