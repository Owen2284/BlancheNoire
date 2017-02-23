package deciders;

import java.awt.Point;
import java.util.HashMap;

import evaluators.Evaluator;
import game.GameState;
import players.Player;

public class MinimaxDecider extends Decider {
	
	private int debugNodesChecked;

	public MinimaxDecider() {}

	@Override
	public String getType() {
		return "Minimax";
	}

	@Override
	public Point decide(GameState game, Evaluator e, Player p, int maxSearchTime) {

		// Initialising variables.
		int playerNumber = 0; if (game.getPlayer(1).equals(p)) {playerNumber = 1;} 
		long startTimestamp = System.currentTimeMillis();

		// Runs the function to analyse the game tree
		Point bestMove = getMaxMove(game, 1, startTimestamp, maxSearchTime, e, playerNumber);
		System.out.println("Time taken:- " + (System.currentTimeMillis() - startTimestamp) + ", Nodes examined:- " + this.debugNodesChecked + ".");
		return bestMove;
	}
	
	/**
	 * Evaluates all children of the current game state and returns the most promising one.
	 */
	public Point getMaxMove(GameState current, int maxDepth, long startTimestamp, int timeLimit, Evaluator e, int playerNumber) {
		
		// Resetting debug field for analysing the number of nodes checked.
		this.debugNodesChecked = 0;
		
		// Initialising storage variables.
		HashMap<Point, Float> moveScores = null;
		int currentDepth = 1;
		
		// Runs a loop to analyse each move from the current game state.
		while ((System.currentTimeMillis() - startTimestamp <= timeLimit && currentDepth <= maxDepth) || (moveScores == null)) {
			
			// Create new HashMap for the current depth.
			moveScores = new HashMap<Point, Float>();
			
			// Loop to check all board spaces.
			for (int row = 0; row < current.getBoardDims()[0]; ++row) {
				for (int col = 0; col < current.getBoardDims()[1]; ++col) {
					
					// Checks if a legal move is available at this space.
					if (current.getLegalMoves(current.getPlayer(playerNumber))[row][col]) {
						
						// Analyses the game tree that sprouts from playing a move at the current space.
						GameState child = current.playMove(current.getPlayer(playerNumber), new Point(row, col));
						float childScore = getMinScore(child, currentDepth-1, startTimestamp, timeLimit, e, 1-playerNumber, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
						
						// Stores the score in the score HashMap.
						moveScores.put(new Point(row, col), childScore);
						++debugNodesChecked;
						
					}
					
				}
			}
			
			// Increases the depth to explore to.
			++currentDepth;
			
		}
		
		// Returns the move found with the highest score.
		Point highScoringMove = null;
		for (Point key : moveScores.keySet()) {
			if (highScoringMove == null || moveScores.get(key) > moveScores.get(highScoringMove)) {
				highScoringMove = key;
			}
		}
		return highScoringMove;
		
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
		
		// Variable for storing the best score found.
		float best = Float.NEGATIVE_INFINITY;
		
		// Checks if the player has any moves to play.
		if (!current.hasLegalMoves(current.getPlayer(playerNumber))) {
			GameState child = new GameState(current);
			best = Math.max(best, getMinScore(child, depth-1, startTimestamp, timeLimit, e, 1 - playerNumber, alpha, beta));
			alpha = Math.max(best, alpha);
			++debugNodesChecked;
			return best;
		}
		
		// Else, explores the possible moves.
		for (int row = 0; row < current.getBoardDims()[0]; ++row) {
			for (int col = 0; col < current.getBoardDims()[1]; ++col) {
				if (current.getLegalMoves(current.getPlayer(playerNumber))[row][col]) {
					GameState child = current.playMove(current.getPlayer(playerNumber), new Point(row, col));
					best = Math.max(best, getMinScore(child, depth-1, startTimestamp, timeLimit, e, 1 - playerNumber, alpha, beta));
					alpha = Math.max(best, alpha);
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
	private float getMinScore(GameState current, int depth, long startTimestamp, int timeLimit, Evaluator e, int playerNumber, float alpha, float beta) {
		// Check if the value of the state should be returned immediately.
		if (current.isOver() || depth <= 0 || System.currentTimeMillis() - startTimestamp >= timeLimit) {
			return e.evaluate(current, current.getPlayer(playerNumber));
		}
		
		// Variable for storing the best score found.
		float worst = Float.POSITIVE_INFINITY;
		
		// Checks if the player has any moves to play.
		if (!current.hasLegalMoves(current.getPlayer(playerNumber))) {
			GameState child = new GameState(current);
			worst = Math.min(worst, getMinScore(child, depth-1, startTimestamp, timeLimit, e, 1 - playerNumber, alpha, beta));
			alpha = Math.min(worst, beta);
			++debugNodesChecked;
			return worst;
		}
		
		// Else, explores the tree deeper.
		for (int row = 0; row < current.getBoardDims()[0]; ++row) {
			for (int col = 0; col < current.getBoardDims()[1]; ++col) {
				// TODO: Allow non-moves to be evaluated.
				if (current.getLegalMoves(current.getPlayer(playerNumber))[row][col]) {
					GameState child = current.playMove(current.getPlayer(playerNumber), new Point(row, col));
					worst = Math.min(worst, getMaxScore(child, depth-1, startTimestamp, timeLimit, e, 1 - playerNumber, alpha, beta));
					beta = Math.min(worst, beta);
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
