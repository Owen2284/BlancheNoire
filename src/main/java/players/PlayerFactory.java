package players;

import deciders.Decider;
import deciders.FixedMinimaxDecider;
import deciders.IterativeMinimaxDecider;
import deciders.MonteCarloTreeSearchDecider;
import deciders.RandomDecider;
import evaluators.DeepLearningEvaluator;
import evaluators.Evaluator;
import evaluators.PositionalEvaluator;
import evaluators.ScoreEvaluator;

public final class PlayerFactory {

	/**
	 * A method that produces a Player object based on the input parameters.
	 * @param counter - An integer number representing the counter that the player will control (similar to the id param on other methods).
	 * @param type - The type of player that this player will be (e.g. either Human or AI).
	 * @param decider - The name of the decider class that the player will use (e.g. Random, Minimax, MCTS, etc.)
	 * @param evaluator - The name of the evaluator class that the player will use (e.g. Score, DeepLearning, etc.)
	 * @return a Player object.
	 */
	public static Player createPlayer(int counter, String type, String decider, String evaluator, boolean usingGUI, int maxSearchTime, boolean defaultToNull) {
		
		// Determine the Player object to create first.
		if (type.equals("Human")) {
			
			// Returns a HumanPlayer object
			return new HumanPlayer(counter, usingGUI);
			
		} else if (type.equals("AI")) {
			
			// Create variables to hold AI objects.
			Decider d = null; 
			Evaluator e = null;
			
			// Create variables to store arguments for specific AI.
			int searchDepth = 6;	
			
			// Determine the Decider to be created.
			if (decider.startsWith("Random")) {
				d = new RandomDecider();
			} else if (decider.startsWith("FixedMinimax")) {
				if (decider.length() > "FixedMinimax".length()) {
					String end = decider.substring("FixedMinimax".length());
					String[] miniArgs = end.split("-");
					for (String miniArg : miniArgs) {
						if (!miniArg.equals("")) {
							if (miniArg.startsWith("D")) {
								try {
									searchDepth = Integer.parseInt(miniArg.substring(1));
								} catch(Exception e2) {
									System.out.println("Invalid D argument for FixedMinimax decider.");
								}
							}
						}
					}
				}
				d = new FixedMinimaxDecider(searchDepth);
			} else if (decider.startsWith("IterativeMinimax")) {
				if (decider.length() > "IterativeMinimax".length()) {
					String end = decider.substring("IterativeMinimax".length());
					String[] miniArgs = end.split("-");
					for (String miniArg : miniArgs) {
						if (!miniArg.equals("")) {
							if (miniArg.startsWith("D")) {
								try {
									searchDepth = Integer.parseInt(miniArg.substring(1));
								} catch(Exception e2) {
									System.out.println("Invalid D argument for IterativeMinimax decider.");
								}
							}
						}
					}
				}
				d = new IterativeMinimaxDecider(searchDepth);
			} else if (decider.startsWith("MCTS")) {
				boolean useMaxSims = false;
				int maxSims = 10000;
				boolean useMinimax = false;
				int depthLim = 6;
				double prob = 0.10;
				int internalSimMS = 10;
				if (decider.length() > "MCTS".length()) {
					String end = decider.substring("MCTS".length());
					String[] miniArgs = end.split("-");
					for (String miniArg : miniArgs) {
						if (!miniArg.equals("")) {
							if (miniArg.startsWith("R")) {
								useMinimax = false;
							} else if (miniArg.startsWith("M")) {
								try {
									depthLim = Integer.parseInt(miniArg.substring(1));
									useMinimax = true;
								} catch(Exception e2) {
									System.out.println("Invalid M argument for MCTS decider.");
								}
							} else if (miniArg.startsWith("S")) {
								try {
									maxSims = Integer.parseInt(miniArg.substring(1));
									useMaxSims = true;
								} catch(Exception e2) {
									System.out.println("Invalid S argument for MCTS decider.");
								}
							} else if (miniArg.startsWith("T")) {
								try {
									internalSimMS = Integer.parseInt(miniArg.substring(1));
								} catch(Exception e2) {
									System.out.println("Invalid T argument for MCTS decider.");
								}
							} else if (miniArg.startsWith("P")) {
								try {
									prob = Double.parseDouble(miniArg.substring(1));
								} catch(Exception e2) {
									System.out.println("Invalid P argument for MCTS decider.");
								}
							} else {
								System.out.println("Unused argument for MCTS decider: " + miniArg);
							}
						}
					}
				}
				d = new MonteCarloTreeSearchDecider(useMaxSims, maxSims, useMinimax, depthLim, prob, internalSimMS);
			} else {
				if (!defaultToNull) {d = new RandomDecider();}				
				throw new IllegalArgumentException("Invalid Decider type.");
			}
			
			// Determine the Evaluator to be created.
			if (evaluator.startsWith("Score")) {
				e = new ScoreEvaluator();
			} else if (evaluator.startsWith("Positional")) {
				e = new PositionalEvaluator();
			} else if (evaluator.startsWith("DeepLearning")) {
				String path = "nn/net.txt";
				if (evaluator.length() > "DeepLearning".length()) {
					String end = decider.substring("DeepLearning".length());
					String[] miniArgs = end.split("-");
					for (String miniArg : miniArgs) {
						if (!miniArg.equals("")) {
							if (miniArg.startsWith("F")) {
								path = miniArg.substring(2, miniArg.length()-1);
							}
						}
					}
				}
				e = new DeepLearningEvaluator(path);
			} else {
				if (!defaultToNull) {e = new ScoreEvaluator();}				
				throw new IllegalArgumentException("Invalid Evaluator type.");
			}
			
			// Return the AIPlayer object.
			return new AIPlayer(counter, d, e, maxSearchTime);
			
		} else {
			throw new IllegalArgumentException("Player type must be either Human or AI.");
		}
		
	}

}
