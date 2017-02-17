package deciders;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import evaluators.Evaluator;
import game.GameState;
import players.Player;

public class RandomDecider extends Decider {

	public RandomDecider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Point decide(GameState g, Evaluator e, Player p) {
		boolean[][] legalMoves = g.getLegalMoves(p);
		ArrayList<Point> possibleMoves = new ArrayList<Point>();
		for (int row = 0; row < g.getBoardDims()[0]; ++row) {
			for (int col = 0; col < g.getBoardDims()[1]; ++row) {
				if (legalMoves[row][col]) {
					possibleMoves.add(new Point(row, col));
				}
			}
		}
		return possibleMoves.get(new Random().nextInt(possibleMoves.size()));
	}

}
