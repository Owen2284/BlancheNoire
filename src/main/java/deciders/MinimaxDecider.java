package deciders;

import java.awt.Point;

import evaluators.Evaluator;
import games.GameState;
import players.Player;

/**
 * Abstract class that implements the methods typically used by a Minimax algorithm on top of the default
 * Decider interface's methods.
 */
public abstract class MinimaxDecider extends Decider {

	protected int depthToSearchTo;
	protected int debugNodesChecked;
	
	public MinimaxDecider(int depthToSearchTo) {
		this.depthToSearchTo = depthToSearchTo;
	}

	public Point decide(GameState game, Evaluator e, Player p, int maxSearchTime) {

		// Initialising variables.
		long startTimestamp = System.currentTimeMillis();

		// Runs the function to analyse the games tree.
		Point bestMove = getMaxMove(game, depthToSearchTo, startTimestamp, maxSearchTime, e, p);
		
		// Returns the move with the best score found by the decider.
		return bestMove;
		
	}
	
	protected abstract Point getMaxMove(GameState current, int depth, long startTimestamp, int timeLimit, Evaluator e, Player p); 
	
	/**
	 * Returns the maximum score obtainable for a player in the sub-tree of the games
	 * tree originating at this point. Supports iterative deepening and cuts of processing
	 * when the time limit is reached.
	 */
	protected float getMaxScore(GameState current, int depth, long startTimestamp, int timeLimit, Evaluator e, Player playerToEvaluate, Player playerToPlay, float alpha, float beta) {
		// Check if the value of the state should be returned immediately.
		if (current.isOver() || depth == 0 || System.currentTimeMillis() - startTimestamp >= timeLimit) {
			return e.evaluate(current, playerToEvaluate);
		}
		
		// Variable for storing the best score found.
		float best = Float.NEGATIVE_INFINITY;
		
		// Checks if the player has any moves to play.
		if (!current.hasLegalMoves(playerToPlay)) {
			GameState child = new GameState(current);
			float childScore = getMinScore(child, depth-1, startTimestamp, timeLimit, e, playerToEvaluate, child.getOpposingPlayer(playerToPlay), alpha, beta);
			best = Math.max(best, childScore);
			++debugNodesChecked;
			return best;
		}
		
		// Else, explores the possible moves.
		boolean[][] lm = current.getLegalMoves(playerToPlay);
		for (int row = 0; row < current.getBoardDims()[0]; ++row) {
			for (int col = 0; col < current.getBoardDims()[1]; ++col) {
				// Checks for move legality.
				if (lm[row][col]) {	
					GameState child = current.playMove(playerToPlay, new Point(row, col));
					float childScore = getMinScore(child, depth-1, startTimestamp, timeLimit, e, playerToEvaluate, child.getOpposingPlayer(playerToPlay), alpha, beta);
					best = Math.max(best, childScore);
					alpha = Math.max(alpha, childScore);
					++debugNodesChecked;
					if (beta <= alpha) {
						return best;
					}
				}
			}
		}
		return best;
		
	}
	
	/**
	 * Returns the minimum score obtainable for a player in the sub-tree of the games
	 * tree originating at this point.
	 */
	protected float getMinScore(GameState current, int depth, long startTimestamp, int timeLimit, Evaluator e, Player playerToEvaluate, Player playerToPlay, float alpha, float beta) {
		// Check if the value of the state should be returned immediately.
		if (current.isOver() || depth == 0 || System.currentTimeMillis() - startTimestamp >= timeLimit) {
			return e.evaluate(current, playerToEvaluate);
		}
		
		// Variable for storing the best score found.
		float worst = Float.POSITIVE_INFINITY;
		
		// Checks if the player has any moves to play.
		if (!current.hasLegalMoves(playerToPlay)) {
			GameState child = new GameState(current);
			float childScore = getMaxScore(child, depth-1, startTimestamp, timeLimit, e, playerToEvaluate, child.getOpposingPlayer(playerToPlay), alpha, beta);
			worst = Math.min(worst, childScore);
			++debugNodesChecked;
			return worst;
		}
		
		// Else, explores the tree deeper.
		boolean[][] lm = current.getLegalMoves(playerToPlay);
		for (int row = 0; row < current.getBoardDims()[0]; ++row) {
			for (int col = 0; col < current.getBoardDims()[1]; ++col) {
				// Checks for move legality.
				if (lm[row][col]) {
					GameState child = current.playMove(playerToPlay, new Point(row, col));
					float childScore = getMaxScore(child, depth-1, startTimestamp, timeLimit, e, playerToEvaluate, child.getOpposingPlayer(playerToPlay), alpha, beta);
					worst = Math.min(worst, childScore);
					beta = Math.min(beta, childScore);
					++debugNodesChecked;
					if (beta <= alpha) {
						return worst;
					}
				}
			}
		}
		return worst;
		
	}

}
