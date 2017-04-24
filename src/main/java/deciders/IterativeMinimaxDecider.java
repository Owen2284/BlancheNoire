package deciders;

import java.awt.Point;
import java.util.HashMap;

import evaluators.Evaluator;
import game.GameState;
import players.Player;

/**
 * A Minimax decider that iteratively increases the depth it searches to up to a specified maximum.
 */
public class IterativeMinimaxDecider extends MinimaxDecider {

	public IterativeMinimaxDecider(int depthToSearchTo) {super(depthToSearchTo);}

	public String getType() {
		return "IterativeMinimax";
	}

	public String toFileString() {
		return "IterativeMinimax(" + this.depthToSearchTo + ")";
	}
	
	/**
	 * Evaluates all children of the current game state and returns the most promising one.
	 */
	public Point getMaxMove(GameState current, int maxDepth, long startTimestamp, int timeLimit, Evaluator e, Player p) {
		
		// Resetting debug field for analysing the number of nodes checked.
		this.debugNodesChecked = 0;
		
		// Initialising storage variables.
		HashMap<Point, Float> moveScores = new HashMap<Point, Float>();
		int currentDepth = 1;
		
		// Runs a loop to analyse each move from the current game state.
		while (System.currentTimeMillis() - startTimestamp <= timeLimit && currentDepth <= maxDepth) {
			
			// Loop to check all board spaces.
			for (int row = 0; row < current.getBoardDims()[0]; ++row) {
				for (int col = 0; col < current.getBoardDims()[1]; ++col) {
					
					// Checks if a legal move is available at this space.
					if (current.getLegalMoves(p)[row][col]) {
						
						// Analyses the game tree that sprouts from playing a move at the current space.
						GameState child = current.playMove(p, new Point(row, col));
						float childScore = getMinScore(child, currentDepth-1, startTimestamp, timeLimit, e, p, child.getOpposingPlayer(p), Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
						
						// Stores the score in the score HashMap, if the time limit has not been exceeded.
						// This is so that any move placed in the HashMap has been fully evaluated.
						if (System.currentTimeMillis() - startTimestamp <= timeLimit) {
							moveScores.put(new Point(row, col), childScore);
							++debugNodesChecked;
						}
						
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

		// Setting output message.
		p.setOutput("Move chosen: (" + highScoringMove.x + "," + highScoringMove.y + "). Score: " + moveScores.get(highScoringMove) + ". Depth reached: " + (currentDepth - 1) + ". (N/ms:- " + (this.debugNodesChecked / (System.currentTimeMillis() - startTimestamp + 1)) + ")");

		return highScoringMove;
		
	}

}
