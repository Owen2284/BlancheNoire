package deciders;

import java.awt.Point;

import evaluators.Evaluator;
import game.GameState;
import players.Player;

public class FixedMinimaxDecider extends Decider {
	
	protected int depthToSearchTo;
	protected int debugNodesChecked;

	public FixedMinimaxDecider(int depthToSearchTo) {this.depthToSearchTo = depthToSearchTo;}

	@Override
	public String getType() {
		return "FixedMinimax";
	}

	@Override
	public Point decide(GameState game, Evaluator e, Player p, int maxSearchTime) {

		// Initialising variables.
		int playerNumber = 0; if (game.getPlayer(1).equals(p)) {playerNumber = 1;} 
		long startTimestamp = System.currentTimeMillis();

		// Runs the function to analyse the game tree.
		Point bestMove = getMaxMove(game, depthToSearchTo, startTimestamp, maxSearchTime, e, playerNumber);
		
		// Prints out debug data.
		System.out.println("Turn " + game.getTurnNumber() + ". Time taken:- " + (System.currentTimeMillis() - startTimestamp) + ", Nodes examined:- " + this.debugNodesChecked + ". N/ms:- " + (this.debugNodesChecked / (System.currentTimeMillis() - startTimestamp + 1)));
		
		// Returns the move with the best score found by the decider.
		return bestMove;
		
	}
	
	/**
	 * Evaluates all children of the current game state and returns the most promising one.
	 */
	public Point getMaxMove(GameState current, int depth, long startTimestamp, int timeLimit, Evaluator e, int playerNumber) {
		
		// Resetting debug field for analysing the number of nodes checked.
		this.debugNodesChecked = 0;
		
		// Initialising storage variables.
		Point bestMove = null;
		float bestScore = Float.NEGATIVE_INFINITY;
			
		// Loop to check all board spaces.
		for (int row = 0; row < current.getBoardDims()[0]; ++row) {
			for (int col = 0; col < current.getBoardDims()[1]; ++col) {
				
				// Checks if a legal move is available at this space.
				if (current.getLegalMoves(current.getPlayer(playerNumber))[row][col]) {
					
					// Analyses the game tree that sprouts from playing a move at the current space.
					GameState child = current.playMove(current.getPlayer(playerNumber), new Point(row, col));
					float childScore = getMinScore(child, depth-1, startTimestamp, timeLimit, e, playerNumber, 1-playerNumber, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
					
					// Determines if the child move is the best move found so far.
					if (childScore > bestScore) {
						bestScore = childScore;
						bestMove = new Point(row, col);
					}
					
					// Stores the score in the score HashMap.
					++debugNodesChecked;
					
				}
				
			}
		}	
		
		// Returns the move found with the highest score.
		return bestMove;
		
	}
	
	/**
	 * Returns the maximum score obtainable for a player in the sub-tree of the game
	 * tree originating at this point. Supports iterative deepening and cuts of processing
	 * when the time limit is reached.
	 */
	protected float getMaxScore(GameState current, int depth, long startTimestamp, int timeLimit, Evaluator e, int playerToEvaluate, int playerToPlay, float alpha, float beta) {
		// Check if the value of the state should be returned immediately.
		if (current.isOver() || depth == 0 || System.currentTimeMillis() - startTimestamp >= timeLimit) {
			return e.evaluate(current, current.getPlayer(playerToEvaluate));
		}
		
		// Variable for storing the best score found.
		float best = Float.NEGATIVE_INFINITY;
		
		// Checks if the player has any moves to play.
		if (!current.hasLegalMoves(current.getPlayer(playerToPlay))) {
			GameState child = new GameState(current);
			float childScore = getMinScore(child, depth-1, startTimestamp, timeLimit, e, playerToEvaluate, 1-playerToPlay, alpha, beta);
			best = Math.max(best, childScore);
			alpha = Math.max(alpha, childScore);
			++debugNodesChecked;
			return best;
		}
		
		// Else, explores the possible moves.
		for (int row = 0; row < current.getBoardDims()[0]; ++row) {
			for (int col = 0; col < current.getBoardDims()[1]; ++col) {
				if (current.getLegalMoves(current.getPlayer(playerToPlay))[row][col]) {
					GameState child = current.playMove(current.getPlayer(playerToPlay), new Point(row, col));
					float childScore = getMinScore(child, depth-1, startTimestamp, timeLimit, e, playerToEvaluate, 1-playerToPlay, alpha, beta);
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
	 * Returns the minimum score obtainable for a player in the sub-tree of the game
	 * tree originating at this point.
	 */
	protected float getMinScore(GameState current, int depth, long startTimestamp, int timeLimit, Evaluator e, int playerToEvaluate, int playerToPlay, float alpha, float beta) {
		// Check if the value of the state should be returned immediately.
		if (current.isOver() || depth == 0 || System.currentTimeMillis() - startTimestamp >= timeLimit) {
			return e.evaluate(current, current.getPlayer(playerToEvaluate));
		}
		
		// Variable for storing the best score found.
		float worst = Float.POSITIVE_INFINITY;
		
		// Checks if the player has any moves to play.
		if (!current.hasLegalMoves(current.getPlayer(playerToPlay))) {
			GameState child = new GameState(current);
			float childScore = getMaxScore(child, depth-1, startTimestamp, timeLimit, e, playerToEvaluate, 1-playerToPlay, alpha, beta);
			worst = Math.min(worst, childScore);
			beta = Math.min(beta, childScore);
			++debugNodesChecked;
			return worst;
		}
		
		// Else, explores the tree deeper.
		for (int row = 0; row < current.getBoardDims()[0]; ++row) {
			for (int col = 0; col < current.getBoardDims()[1]; ++col) {
				if (current.getLegalMoves(current.getPlayer(playerToPlay))[row][col]) {
					GameState child = current.playMove(current.getPlayer(playerToPlay), new Point(row, col));
					float childScore = getMaxScore(child, depth-1, startTimestamp, timeLimit, e, playerToEvaluate, 1-playerToPlay, alpha, beta);
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
