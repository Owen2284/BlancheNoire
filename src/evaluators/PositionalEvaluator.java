package evaluators;

import java.util.HashMap;

import game.GameState;
import players.Player;

public class PositionalEvaluator extends Evaluator {
	
	private boolean useCache;
	private HashMap<GameState, Float> cache;
	
	private final double EDGE_WEIGHT = 1.5;
	private final double CORNER_WEIGHT = 2.0;

	public PositionalEvaluator(boolean useCache) {
		this.useCache = useCache;
		this.cache = new HashMap<GameState, Float>();
	}

	@Override
	public String getType() {
		return "Positional";
	}

	@Override
	public float evaluate(GameState game, Player p) {
		
		// Check the cache first before running the evaluation.
		if (this.useCache) {
			for (GameState key : this.cache.keySet()) {
				if (key.isRotationOf(game)) {
					return this.cache.get(key);
				}
			}
		}
		
		// Initialisation
		float score = 0;
		int[][] board = game.getBoard();
		int myID = p.getPlayerID();
		int opponentID = game.getOpposingPlayer(p).getPlayerID();
		
		// Checks if the game is won or lost.
		if (game.isOver()) {
			if (game.getScore(myID) > game.getScore(opponentID)) {
				score = game.getBoardDims()[0] * game.getBoardDims()[1] * (float) EDGE_WEIGHT * (float) CORNER_WEIGHT;
				if (this.useCache) {this.cache.put(game, score);}
				return score;
			} else if (game.getScore(myID) < game.getScore(opponentID)) {
				score = (-1 * game.getBoardDims()[0] * game.getBoardDims()[1]) * (float) EDGE_WEIGHT * (float) CORNER_WEIGHT;
				if (this.useCache) {this.cache.put(game, score);}
				return score;
			}
		}
		
		// Calculates the board's worth based on the position of counters.
		for (int row = 0; row < game.getBoardDims()[0]; ++row) {
			for (int col = 0; col < game.getBoardDims()[1]; ++col) {
				int current = board[row][col];
				// Determines if the current space is a corner or edge.
				double multiplier = 1;
				if ((row == 0 || row == game.getBoardDims()[0] - 1) && (col == 0 || col == game.getBoardDims()[1] - 1)) {
					multiplier = CORNER_WEIGHT;
				} else if ((row == 0 || row == game.getBoardDims()[0] - 1) || (col == 0 || col == game.getBoardDims()[1] - 1)) {
					multiplier = EDGE_WEIGHT;
				}
				// Determines if the score should be increased or decreased, based on the ownership of the space.
				if (current == myID) {
					score = score + (float) multiplier;
				} else if (current == opponentID) {
					score = score - (float) multiplier;
				}
			}
		}
		if (this.useCache) {this.cache.put(game, score);}
		return score;
	}

}
