package deciders;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import evaluators.Evaluator;
import game.GameState;
import players.Player;

public class MonteCarloTreeSearchDecider extends Decider {

	private Decider internalDecider;
	private boolean useMaxSims;
	private int maxSims;
	
	private int simulationsRun = 0;
	
	public MonteCarloTreeSearchDecider(boolean useMaxSims, int maxSims, Decider internalDecider) {
		this.useMaxSims = useMaxSims;
		this.maxSims = maxSims;
		this.internalDecider = internalDecider;
	}

	@Override
	public String getType() {return "MCTS";}
	
	@Override
	public String toFileString() {
		return "MCTS(" + this.useMaxSims + "," + this.maxSims + "," + this.internalDecider.toFileString() + ")";
	}

	@Override
	public Point decide(GameState game, Evaluator e, Player p, int maxSearchTime) {
		
		// Checks if a decision needs to be made, or if the only move can be returned.
		int moveCount = 0;
		for (int row = 0; row < game.getBoardDims()[0]; row++) {
			for (int col = 0; col < game.getBoardDims()[1]; col++) {
				if (game.getLegalMoves(p)[row][col]) {++moveCount;}
			}
		}
		if (moveCount == 1) {
			for (int row = 0; row < game.getBoardDims()[0]; row++) {
				for (int col = 0; col < game.getBoardDims()[1]; col++) {
					if (game.getLegalMoves(p)[row][col]) {return new Point(row, col);}
				}
			}
		}
		
		simulationsRun = 0;
		
		// Storing start time.
		long startTime = System.currentTimeMillis();
		
		// Create the root node of the tree.
		TreeNode root = new TreeNode();
		
		// Runs an initial simulation from the root node to set up the tree correctly.
		int rootResult = simulate(game, p, p, e);
		root.total += 1;
		root.wins += rootResult;
		
		// Starting the MCTS loop.
		Player playerIAm = p;
		while ( (System.currentTimeMillis() - startTime < maxSearchTime) && (!useMaxSims || simulationsRun < maxSims) ) {
			
			// Begins search at the root node, with an empty path list.
			TreeNode currentNode = root;
			GameState currentGame = new GameState(game);
			Player currentPlayer = p;
			
			ArrayList<TreeNode> path = new ArrayList<TreeNode>();
			path.add(currentNode);
			
			// Moves through the tree until a leaf node is found. (SELECTION)
			while (currentNode.children.size() > 0) {
				
				// Stores the best move found so far.
				TreeNode bestMove = null;
				double bestUCB1 = Double.NEGATIVE_INFINITY;
				
				// Checks each child node to determine the next move to be made.
				for (TreeNode childNode : currentNode.children) {
					double childUCB1 = getUCB1Score(childNode.wins, childNode.total, currentNode.total);
					if (childUCB1 > bestUCB1) {
						bestUCB1 = childUCB1;
						bestMove = childNode;
					}
				}
				
				// Add next move to the path, and update current variables.
				path.add(bestMove);
				currentNode = bestMove;
				if (currentNode.moveMade.x != -1 || currentNode.moveMade.y != -1) {
					// A Point with coords (-1,-1) is used to show a move where the player cannot play.
					// The if statement here ensures that this lack of change in the game state is correctly handled.
					currentGame = currentGame.playMove(currentPlayer, currentNode.moveMade);
				}
				currentPlayer = currentGame.getOpposingPlayer(currentPlayer);
				
			}
				
			// Adds children to the current node if it has none.
			if (currentNode.children.size() == 0) {

				// Code for creating nodes for each legal move.
				if (currentGame.hasLegalMoves(currentPlayer) && (!currentGame.isOver())) {
					
					// Gets the legal move array, and loops through all legal moves.
					boolean[][] legalMoves = currentGame.getLegalMoves(currentPlayer);
					int totalMoves = 0;
					int totalWins = 0;
					for (int row = 0; row < currentGame.getBoardDims()[0]; ++row) {
						for (int col = 0; col < currentGame.getBoardDims()[1]; ++col) {
							if (legalMoves[row][col]) {
								
								// Creating new child nodes of the tree (EXPANSION)
								TreeNode newChildNode = new TreeNode();
								newChildNode.moveMade = new Point(row, col);
								currentNode.children.add(newChildNode);
								
								// Children must have one simulation complete for UCB1 calculations to work correctly. (SIMULATION)
								GameState childState = currentGame.playMove(currentPlayer, new Point(row, col));
								int childResult = simulate(childState, childState.getOpposingPlayer(currentPlayer), playerIAm, e);
								newChildNode.total += 1;
								newChildNode.wins += childResult;
								totalMoves += 1;
								totalWins += childResult;
								
							}
						}
					}
					
					// Updating all of the nodes in the path with the simulated info (BACKPROPOGATION)
					for (int nodeNum = path.size() - 1; nodeNum >= 0; --nodeNum) {
						TreeNode theNode = path.get(nodeNum);
						theNode.total += totalMoves;
						theNode.wins += totalWins;
					}
					
				}
				
				// Code for when the game is over.
				else if (currentGame.isOver()) {

					// Get the result of the game and update the tree accordingly. (SIMULATION + BACKPROPOGATION)
					int gameResult = simulate(currentGame, currentPlayer, playerIAm, e);
					for (int nodeNum = path.size() - 1; nodeNum >= 0; --nodeNum) {
						TreeNode theNode = path.get(nodeNum);
						theNode.total += 1;
						theNode.wins += gameResult;
					}
					
				}
				
				// Code for when no legal moves exist.
				else {
					
					// Create a tree node representing no move made. (EXPANSION)
					TreeNode newChildNode = new TreeNode();
					newChildNode.moveMade = new Point(-1, -1);
					currentNode.children.add(newChildNode);
					
					// Run a simulation from this node onward. (SIMULATION)
					GameState childState = new GameState(currentGame);
					int childResult = simulate(childState, childState.getOpposingPlayer(currentPlayer), playerIAm, e);
					newChildNode.total += 1;
					newChildNode.wins += childResult;
					
					// Updating the counts of the nodes on the path to this node. (BACKPROPOGATION)
					for (int nodeNum = path.size() - 1; nodeNum >= 0; --nodeNum) {
						TreeNode theNode = path.get(nodeNum);
						theNode.total += 1;
						theNode.wins += childResult;
					}
					
				}
			}
		}
		
		// Storing end time.
		//long endTime = System.currentTimeMillis();
		
		// Returns the best move found.
		//System.out.println("---");
		//System.out.println("Moves:");
		TreeNode bestChild = null;
		double bestScore = Double.NEGATIVE_INFINITY;
		float bestChildEval = Float.NEGATIVE_INFINITY;
		int depth = 6;
		
		for (TreeNode rootChild : root.children) {
			GameState rootChildGameState = game.playMove(playerIAm, rootChild.moveMade);
			float rootChildEvaluation = e.evaluate(rootChildGameState, playerIAm);
			PercentScoreData rootChildPercentScore = getMinChance(rootChildGameState, playerIAm, game.getOpposingPlayer(playerIAm), depth, rootChild, 20, e, new PercentScoreData(0.0, Float.NEGATIVE_INFINITY), new PercentScoreData(0.0, Float.POSITIVE_INFINITY));
			double rootChildScore = rootChildPercentScore.percent;
			//System.out.println(" Move (" + rootChild.moveMade.x + "," + rootChild.moveMade.y + ") has minimum win percentage of " + rootChildScore + "%. (" + rootChildEvaluation + ")");
			if (rootChildScore > bestScore) {
				bestScore = rootChildScore;
				bestChild = rootChild;
				bestChildEval = rootChildEvaluation;
			} else if (rootChildScore == bestScore && rootChildEvaluation > bestChildEval) {
				bestScore = rootChildScore;
				bestChild = rootChild;
				bestChildEval = rootChildEvaluation;
			}
		}
		
		p.setOutput("Move chosen: (" + bestChild.moveMade.x + "," + bestChild.moveMade.y + "). Score: " + bestChildEval + ". Minimum win percentage: " + bestScore + "%.");
		//System.out.println(simulationsRun + " sims run over " + (endTime - startTime) + " milliseconds; " + ((double) simulationsRun / (endTime - startTime)) + " S/ms.");
		
		return bestChild.moveMade;
		
	}
	
	/**
	 * Method that simulates a game from the given GameState, and returns whether or not the provided player won.
	 */
	private int simulate(GameState startState, Player startPlayer, Player playerIAm, Evaluator e) {
		
		// Creating variables needed for simulation.
		GameState currentState = startState;
		Player currentPlayer = startPlayer;
		
		// Run the simulation loop until the game is completed.
		while (!currentState.isOver()) {
			
			// Checks the player can play a move.
			if (currentState.hasLegalMoves(currentPlayer)) {

				double r = new Random().nextDouble();
				Point moveToPlay;

				// Occasionally use a RandomDecider to make an imperfect move.
				if (r < 0.1) {
					moveToPlay = new RandomDecider().decide(currentState, e, currentPlayer, 0);
				} 
				// Otherwise use the move specified by the internal decider to simulate moves.
				else {
					moveToPlay = internalDecider.decide(currentState, e, currentPlayer, 0);
				}
				
				// Play the move onto the current state.
				currentState = currentState.playMove(currentPlayer, moveToPlay);
				
			}
			
			// Change to the next player.
			currentPlayer = currentState.getOpposingPlayer(currentPlayer);
			
		}
		
		// Inc debug var.
		++simulationsRun;
		
		// Determine the int to return based on if the player's score was higher then the opponent's or not.
		if (currentState.getScore(playerIAm) > currentState.getScore(currentState.getOpposingPlayer(playerIAm))) {
			return 1;
		} else {
			return 0;
		}
		
	}
	
	/**
	 * Returns the UCB1 score for the provided arguments.
	 */
	private double getUCB1Score(int w, int n, int t) {
		final double C = Math.sqrt(2);
		return ((double)w/(double)n) + (C * Math.sqrt(Math.log(t) / (double)n));
	}
	
	protected PercentScoreData getMaxChance(GameState currentState, Player playerToEvaluate, Player playerToPlay, int depth, TreeNode currentNode, int minRuns, Evaluator e, PercentScoreData alpha, PercentScoreData beta) {
		
		// Variable for storing the best score found.
		PercentScoreData best = new PercentScoreData(0.0, Float.NEGATIVE_INFINITY);		
		
		// Checks if the node has the appropriate number of evaluations to it.
		if (currentNode.total < minRuns) {
			return new PercentScoreData(currentNode.getWinPercent(), e.evaluate(currentState, playerToEvaluate));
		}
		
		// Check if the current node is a leaf node.
		if (currentNode.children.size() <= 0 || depth == 0) {
			return new PercentScoreData(currentNode.getWinPercent(), e.evaluate(currentState, playerToEvaluate));
		}
		
		// Else, explores the possible moves.
		for (TreeNode childNode : currentNode.children) {
			GameState childState;
			if (childNode.moveMade.x == -1 && childNode.moveMade.y == -1) {
				childState = currentState;
			} else {
				childState = currentState.playMove(playerToPlay, childNode.moveMade);
			}
			PercentScoreData childPercentScore = getMinChance(childState, playerToEvaluate, currentState.getOpposingPlayer(playerToPlay), depth-1, childNode, minRuns, e, alpha, beta);
			if (childPercentScore.score >= best.score) {best = childPercentScore;}
			if (childPercentScore.score >= alpha.score) {alpha = childPercentScore;}
			if (beta.score <= alpha.score) {
				return best;
			}
		}
		return best;
		
	}

	protected PercentScoreData getMinChance(GameState currentState, Player playerToEvaluate, Player playerToPlay, int depth, TreeNode currentNode, int minRuns, Evaluator e, PercentScoreData alpha, PercentScoreData beta) {
		
		// Variable for storing the best score found.
		PercentScoreData worst = new PercentScoreData(0.0, Float.POSITIVE_INFINITY);
		
		// Checks if the node has the appropriate number of evaluations to it.
		if (currentNode.total < minRuns) {
			return new PercentScoreData(currentNode.getWinPercent(), e.evaluate(currentState, playerToEvaluate));
		}
		
		// Check if the current node is a leaf node.
		if (currentNode.children.size() <= 0 || depth == 0) {
			return new PercentScoreData(currentNode.getWinPercent(), e.evaluate(currentState, playerToEvaluate));
		}
		
		// Else, explores the tree deeper.
		for (TreeNode childNode : currentNode.children) {
			GameState childState;
			if (childNode.moveMade.x == -1 && childNode.moveMade.y == -1) {
				childState = currentState;
			} else {
				childState = currentState.playMove(playerToPlay, childNode.moveMade);
			}
			PercentScoreData childPercentScore = getMaxChance(childState, playerToEvaluate, currentState.getOpposingPlayer(playerToPlay), depth-1, childNode, minRuns, e, alpha, beta);
			if (childPercentScore.score <= worst.score) {worst = childPercentScore;}
			if (childPercentScore.score <= beta.score) {beta = childPercentScore;}
			if (beta.score <= alpha.score) {
				return worst;
			}
		}
		return worst;
		
	}
	
	// Internal class for storing statistics on simulations.
	private class TreeNode {
		
		public int wins = 0;
		public int total = 0;
		public Point moveMade = null;
		public ArrayList<TreeNode> children = new ArrayList<TreeNode>();
		
		public TreeNode() {}
		
		public double getWinPercent() {
			return (wins * 100) / ( (double) total);
		}
		
	}
	
	// Internal class to store win percentage and evaluator score data.
	private class PercentScoreData {
		
		public double percent;
		public float score;
		
		public PercentScoreData(double percent, float score) {
			this.percent = percent;
			this.score = score;
		}
		
	}

}
