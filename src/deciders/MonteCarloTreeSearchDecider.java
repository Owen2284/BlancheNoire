package deciders;

import java.awt.Point;

import evaluators.Evaluator;
import game.GameState;
import players.Player;

public class MonteCarloTreeSearchDecider extends Decider {
	
	private int debugSimulationsRun = 0;

	public MonteCarloTreeSearchDecider() {}

	@Override
	public String getType() {
		return "MCTS";
	}

	@Override
	public Point decide(GameState game, Evaluator e, Player p, int maxSearchTime) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Method that simulates a game from the given GameState, and returns whether or not the provided player won.
	 */
	private int simulate(GameState start, Player player, long startTimestamp, int maxSearchTime) {
		++debugSimulationsRun;
		return 1;
	}
	
	/**
	 * Returns the UCB1 score for the provided arguments.
	 */
	private double getUCB1Score(int w, int n, int t) {
		final double C = Math.sqrt(2);
		return ((double)w/(double)n) + (C * Math.sqrt(Math.log(t) / (double)n));
	}
	
	private class TreeNode {
		
		public int wins;
		public int total;
		
		public double getWinChance() {
			return wins / ( (double) total);
		}
		
	}

}
