package main;

import java.awt.Point;
import java.io.PrintWriter;
import java.util.HashMap;

import game.GameState;
import players.Player;
import players.PlayerFactory;
import ui.OthelloFrame;

/**
 * The main class that runs the Othello game.
 */
public class Othello {
	
	private final static boolean DEBUG = false;
	
	/**
	 * Main method.
	 * @param args
	 */
	public static void main(String[] args) {
		
		// Parses the command line variables.
		HashMap<String,String> argMap = new HashMap<String,String>();
		for (int argNum = 0; argNum < args.length - 1; ++argNum) {
			String thisArg = args[argNum];
			String nextArg = args[argNum+1];
			if (thisArg.substring(0,1).equals("-")) {
				argMap.put(thisArg.toUpperCase(), nextArg);
				if (DEBUG) {System.out.println(thisArg.toUpperCase() + " = " + nextArg);}
			}
		}
		
		// Setting default values for game variables and applies command line arguments if they are found.
		boolean useGUI = true;
		boolean showOutput = false;
		int delayBetweenMoves = 100;
		int maxSearchTime = 5000;
		int timesToRun = 1;
		int boardSize = 8;
		try {
			if (argMap.keySet().contains("-useGUI".toUpperCase())) {useGUI = Boolean.parseBoolean(argMap.get("-useGUI".toUpperCase()));}
		} catch (Exception e) {System.out.println("Error parsing -useGUI argument. (" + e.getMessage() + ")");}
		try {
			if (argMap.keySet().contains("-showOutput".toUpperCase())) {showOutput = Boolean.parseBoolean(argMap.get("-showOutput".toUpperCase()));}
		} catch (Exception e) {System.out.println("Error parsing -showOutput argument. (" + e.getMessage() + ")");}	
		try {
			if (argMap.keySet().contains("-moveDelay".toUpperCase())) {delayBetweenMoves = Integer.parseInt(argMap.get("-moveDelay".toUpperCase()));}
		} catch (Exception e) {System.out.println("Error parsing -moveDelay argument. (" + e.getMessage() + ")");}
		try {
			if (argMap.keySet().contains("-AIRunTime".toUpperCase())) {maxSearchTime = Integer.parseInt(argMap.get("-AIRunTime".toUpperCase()));}
		} catch (Exception e) {System.out.println("Error parsing -AIRunTime argument. (" + e.getMessage() + ")");}
		try {	
			if (argMap.keySet().contains("-runCount".toUpperCase())) {timesToRun = Integer.parseInt(argMap.get("-runCount".toUpperCase()));}
		} catch (Exception e) {System.out.println("Error parsing -runCount argument. (" + e.getMessage() + ")");}
		try {
			if (argMap.keySet().contains("-boardSize".toUpperCase())) {boardSize = Integer.parseInt(argMap.get("-boardSize".toUpperCase()));}
		} catch (Exception e) {System.out.println("Error parsing -boardSize argument. (" + e.getMessage() + ")");}
			
		// Runs the game multiple times if required.
		for (int numRuns = 0; numRuns < timesToRun; ++numRuns) {
		
			// Creates the player objects.
			Player p1 = null;
			Player p2 = null;
			if (argMap.keySet().contains("-player1".toUpperCase())) {
				try {
					String argString = argMap.get("-player1".toUpperCase());
					if (argString.startsWith("AI")) {
						String[] aiArgs = argString.substring(3, argString.length() - 1).split(",");
						p1 = PlayerFactory.createPlayer(GameState.COUNTER_DARK, "AI", aiArgs[0], aiArgs[1], useGUI, maxSearchTime, false);
					} else if (argString.startsWith("Human")) {
						p1 = PlayerFactory.createPlayer(GameState.COUNTER_DARK, "Human", "", "", useGUI, maxSearchTime, false);
					}
				} catch (Exception e) {
					System.out.println("Error parsing -player1 argument. (" + e.getMessage() + ")");
					p1 = PlayerFactory.createPlayer(GameState.COUNTER_DARK, "Human", "", "", useGUI, maxSearchTime, false);
				}
			} else {
				p1 = PlayerFactory.createPlayer(GameState.COUNTER_DARK, "Human", "", "", useGUI, maxSearchTime, false);
			}
			if (argMap.keySet().contains("-player2".toUpperCase())) {
				try {
					String argString = argMap.get("-player2".toUpperCase());
					if (argString.startsWith("AI")) {
						String[] aiArgs = argString.substring(3, argString.length() - 1).split(",");
						p2 = PlayerFactory.createPlayer(GameState.COUNTER_LIGHT, "AI", aiArgs[0], aiArgs[1], useGUI, maxSearchTime, false);
					} else if (argString.startsWith("Human")) {
						p2 = PlayerFactory.createPlayer(GameState.COUNTER_LIGHT, "Human", "", "", useGUI, maxSearchTime, false);
					}
				} catch (Exception e) {
					System.out.println("Error parsing -player2 argument. (" + e.getMessage() + ")");
					p2 = PlayerFactory.createPlayer(GameState.COUNTER_LIGHT, "AI", "Random", "Score", useGUI, maxSearchTime, false);
				}
			} else {
				p2 = PlayerFactory.createPlayer(GameState.COUNTER_LIGHT, "AI", "Random", "Score", useGUI, maxSearchTime, false);
			}
			
				
			// Creates basic game.
			GameState game = new GameState(p1, p2, boardSize);
			
			// Storing initial game state in file string storage.
			String fileString = game.toFileString();
					
			// Initialises other variables shared by the two game loops.
			int nextPlayerNumber = 0;
			Player playerToPlay = null;
				
			// Creates game UI.
			OthelloFrame ui = null;
			if (useGUI) {
				ui = new OthelloFrame();
			}
			
			while (!game.isOver()) {
				
				// Retrieve the player that is next to play.
				playerToPlay = game.getPlayer(nextPlayerNumber);
				
				// Display the current game state.
				if (useGUI) {
					ui.updateUI(game, playerToPlay);
				} else {
					System.out.println(game);
				}
				
				// Temporary pause to show game state.
				try {Thread.sleep(delayBetweenMoves);} catch (Exception e) {/* Continue execution. */}
				
				// Determine if the player can make a move or not.
				if (game.hasLegalMoves(playerToPlay)) {
				
					// Ask the player to determine their move.
					Point moveToPlay = playerToPlay.getMove(game, ui.middle);
					
					// Plays the move onto the game board, and stores the new GameState.
					game = game.playMove(playerToPlay, moveToPlay);
					
					// Display any info about the player's move if necessary.
					if (showOutput) {
						System.out.println(playerToPlay.getOutput());
					}
					
					// Add move to file.
					fileString += "P[" + nextPlayerNumber + "]:(" + moveToPlay.x + "," + moveToPlay.y + ")\n";
				
				} else {
					
					// Add non-move to file.
					fileString += "P[" + nextPlayerNumber + "]:NOMOVE\n";
					
				}
				
				// Changes the player to play next and loops back to the start of the game loop.
				nextPlayerNumber = (nextPlayerNumber + 1) % 2;
				
			}
			
			// Display the final game state.
			if (useGUI) {
				ui.updateUI(game, playerToPlay);
			} else {
				System.out.println(game);
			}
			
			// Writes game archive to a file.
			fileString += "END";
			try {
				String fileName = "games/archive/Game" + System.currentTimeMillis() + ".txt";
				PrintWriter writer = new PrintWriter(fileName, "UTF-8");
				for (String s : fileString.split("\n")) {
					writer.println(s);
				}
				writer.close();
				if (showOutput) {
					System.out.println("Game file written to \"" + fileName + "\"");
				}
			} catch(Exception e) {
				System.out.println("Error while writing game file: " + e.getMessage());
			}
		}
			
		
	}

}
