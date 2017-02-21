package deciders;

import java.awt.Point;

import evaluators.Evaluator;
import game.GameState;
import players.Player;

public class MinimaxDecider extends Decider {

	public MinimaxDecider() {}

	@Override
	public String getType() {
		return "Minimax";
	}

	@Override
	public Point decide(GameState game, Evaluator e, Player p, int maxSearchTime) {
		int playerNumber = 0; if (game.getPlayer(1).equals(p)) {playerNumber = 1;} 
		long startTime = System.currentTimeMillis();
		float bestScore = getMaxScore(game, 8, startTime, maxSearchTime, e, playerNumber, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
		System.out.println("Time taken:- " + (System.currentTimeMillis() - startTime));
		return new RandomDecider().decide(game, e, p, maxSearchTime);
	}
	
	/**
	 * 
	 */
	public Point getMaxMove() {
		for (child of initial state) {
			run max score on it
		}
	}
	
	/**
	 * Returns the maximum score obtainable for a player in the sub-tree of the game
	 * tree originating at this point. Supports iterative deepening and cuts of processing
	 * when the time limit is reached.
	 */
	private float getMaxScore(GameState current, int depth, long startTimestamp, int timeLimit, Evaluator e, int playerNumber, float alpha, float beta) {
		// Check if the value of the state should be returned immediately.
		if (current.isOver() || depth == 0 || System.currentTimeMillis() - startTimestamp >= timeLimit) {
			return e.evaluate(current, current.getPlayer(playerNumber));
		}
		
		// Else, explores the tree deeper.
		float best = Float.NEGATIVE_INFINITY;
		for (int row = 0; row < current.getBoardDims()[0]; ++row) {
			for (int col = 0; col < current.getBoardDims()[1]; ++col) {
				if (current.getLegalMoves(current.getPlayer(playerNumber))[row][col]) {
					GameState child = current.playMove(current.getPlayer(playerNumber), new Point(row, col));
					best = Math.max(best, getMinScore(child, depth-1, startTimestamp, timeLimit, e, 1 - playerNumber, alpha, beta));
					alpha = Math.max(best, alpha);
					if (beta <= alpha) {
						return best;
					}
				}
			}
		}
		return best;
	}
	
	/**
	 * Returns the minimum score obtainable for a player in the sub-tree of the game
	 * tree originating at this point.
	 */
	private float getMinScore(GameState current, int depth, long startTimestamp, int timeLimit, Evaluator e, int playerNumber, float alpha, float beta) {
		// Check if the value of the state should be returned immediately.
		if (current.isOver() || depth <= 0 || System.currentTimeMillis() - startTimestamp >= timeLimit) {
			return e.evaluate(current, current.getPlayer(playerNumber));
		}
		
		// Else, explores the tree deeper.
		float worst = Float.POSITIVE_INFINITY;
		for (int row = 0; row < current.getBoardDims()[0]; ++row) {
			for (int col = 0; col < current.getBoardDims()[1]; ++col) {
				// TODO: Allow non-moves to be evaluated.
				if (current.getLegalMoves(current.getPlayer(playerNumber))[row][col]) {
					GameState child = current.playMove(current.getPlayer(playerNumber), new Point(row, col));
					worst = Math.min(worst, getMaxScore(child, depth-1, startTimestamp, timeLimit, e, 1 - playerNumber, alpha, beta));
					beta = Math.min(worst, beta);
					if (beta <= alpha) {
						return worst;
					}
				}
			}
		}
		return worst;
	}

}
