package learning;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import game.GameScript;
import game.GameState;
import players.Player;
import util.FileTools;

/**
 * Class containing code used to handle the Othello game data.
 * @author Owen
 */
public class NeuralNetDataHandler {
	
	public static void main(String[] args) {
		// Initialising argument storage variables.
		String[] portionsInit;
		double[] portions = new double[1]; portions[0] = 1.0;
		double portionMax = 1.0;
		boolean prepare = true;
		boolean train = true;
		boolean test = true;
		String name = "";

		// Checking what args are provided by the user.
		if (args.length >= 1) {
			portionsInit = args[0].split(",");
			portions = new double[portionsInit.length];
			portionMax = 0.0;
			int ptr = 0;
			for (String p : portionsInit) {
				double val = Double.parseDouble(p);
				portions[ptr] = val;
				if (val > portionMax) {
					portionMax = val;
				}
				++ptr;
			}
			if (args.length >= 2) {
				prepare = Boolean.parseBoolean(args[1]);
				if (args.length >= 3) {
					train = Boolean.parseBoolean(args[2]);
					if (args.length >= 4) {
						test = Boolean.parseBoolean(args[3]);
						if (args.length >= 4) {
							name = "-" + args[4];
						}
					}
				}
			}
		}
		if (!name.equals("")) {
			System.out.println("The file(s) in this dataset will have \"" + name + "\" added to them to distinguish them.");
		}
		System.out.println("A max of " + portionMax*100 + "% of the training data provided will be used.");
		if (prepare) {prepare();}
		format(train, test, portions, portionMax, name);
	}
	
	public static void prepare() {
		// Convert and split the data. (Uses same random seed so results can be reproduced.)
		String startDir = "games/dataset/";
		String convertedDir = "games/scripts/";
		String trainDir = "games/scripts/train/";
		String testDir = "games/scripts/test/";
		convertDatasetToScripts(startDir, convertedDir);
		splitScripts(convertedDir, trainDir, testDir, 2017);
		mergeDir(trainDir, "train-master.oth");
		mergeDir(testDir, "test-master.oth");
	}
	
	public static void format(boolean train, boolean test, double[] portions, double maxPortion, String name) {
		
		// Defining directories.
		String trainDir = "games/scripts/train/";
		String testDir = "games/scripts/test/";
		String trainFinal = "games/csv/train/";
		String testFinal = "games/csv/test/";
		
		// Initialising other variables.
		ArrayList<String> data = null;
		ArrayList<String> states = null;

		// Constants
		int MAX_PER_CSV = 100000;
		
		System.out.println("-----");
		if (train) {
			
			// Data loading.
			System.out.println("Loading in training data...");
			data = FileTools.readDir(trainDir);
			System.out.println(data.size() + " training game scripts loaded in.");
			
			// Data formatting.
			System.out.println("Formatting training data into CSV format...");
			states = dataFormat2(data, maxPortion, 2017);
			int maxStates = (int) (states.size() / maxPortion);
			
			// Writing to CSV.
			System.out.println("Writing training data...");
			ArrayList<String> csvPortion;
			for (double portion : portions) {
				int stateTotal = (int)(maxStates * portion);
				int fileTotal = (stateTotal / MAX_PER_CSV) + 1;
				System.out.println(" Writing " + stateTotal + " of " + maxStates + " states as a " + (portion * 100) + "% training set (" + fileTotal + " files expected)...");
				for (int fileNum = 0; fileNum < fileTotal; ++fileNum) {
					csvPortion = new ArrayList<String>();
					for (int stateNum = fileNum * MAX_PER_CSV; stateNum < (fileNum + 1) * MAX_PER_CSV; ++stateNum) {
						csvPortion.add(states.get(stateNum));
					}
					FileTools.writeFile(trainFinal + "training" + name + "-" + portion + "-" + fileNum + ".csv", states);
				}
			}
			System.out.println("Training data has been saved as CSV files.");
		}
		
		data = null;
		states = null;
		
		if (test) {
			
			// Data loading.
			System.out.println("Loading in testing data...");
			data = FileTools.readDir(testDir);
			System.out.println(data.size() + " testing game scripts loaded in.");	
			
			// Data formatting.
			System.out.println("Formatting testing data into CSV format...");
			states = dataFormat2(data, 1.0, 2017);
			
			// Writing to CSV.
			System.out.println("Writing testing data...");
			int stateTotal = states.size();
			int fileTotal = (stateTotal / MAX_PER_CSV) + 1;
			ArrayList<String> csvPortion;
			System.out.println(" Writing all " + stateTotal + " states as a 100% training set (" + fileTotal + " files expected)...");
			for (int fileNum = 0; fileNum < fileTotal; ++fileNum) {
				csvPortion = new ArrayList<String>();
				for (int stateNum = fileNum * MAX_PER_CSV; stateNum < (fileNum + 1) * MAX_PER_CSV; ++stateNum) {
					csvPortion.add(states.get(stateNum));
				}
				FileTools.writeFile(trainFinal + "training" + name + "-" + fileNum + ".csv", states);
			}
			System.out.println("Testing data has been saved as CSV files.");
		}
		
		System.out.println("Execution complete.");
		
	}
	
	public static void mergeDir(String dir, String newFileName) {
		System.out.println("Merging all files in \"" + dir + "\"...");
		FileTools.mergeDir(dir, newFileName);
		System.out.println("Merging complete, data stored in \"" + dir + newFileName + "\".");
	}
	
	public static void splitScripts(String initDir, String trainingDir, String testDir, long seed) {
		
		// Initialisation.
		Random r = new Random(seed);
		ArrayList<String> trainingData = new ArrayList<String>();
		ArrayList<String> testData = new ArrayList<String>();
		int trainingFilesMade = 0;
		int testFilesMade = 0;
		int trainingGames = 0;
		int testGames = 0;
		String[] allPaths = FileTools.getAllFilePathsInDir(initDir);
		int maxPaths = allPaths.length;
		int pathCount = 0;
		int failedGames = 0;
		int badScoreGames = 0;
		int illegalMoveGames = 0;
		int ranOutOfMovesGames = 0;
		int movesLeftOverGames = 0;
		int otherFailGames = 0;
		
		// Cleaning output directories.
		System.out.println("Begining splitting operation:");
		for (String path : FileTools.getAllFilePathsInDir(trainingDir)) {
			File f = new File(path);
			f.delete();
		}
		for (String path : FileTools.getAllFilePathsInDir(testDir)) {
			File f = new File(path);
			f.delete();
		}
		
		// Loop through all files.
		for (String path : allPaths) {
			
			// Load in all games.
			ArrayList<String> file = FileTools.readFile(path);
			
			// Loop through all games.
			for (String game : file) {
				
				// Check the game correctly simulates all the way through.
				GameScript gs = new GameScript(game);
				GameState g = gs.generateStartState();
				Player p = g.getPlayerByIndex(0);
				try {
					while (!g.isOver()) {
						if (g.hasLegalMoves(p)) {
							g = g.playMove(p, p.getMove(g, null));
						}
						p = g.getOpposingPlayer(p);
					}
					if (g.getTurnNumber()-1 == gs.getTotalMoves()) {
						if (g.getScoreOfID(GameState.COUNTER_DARK) == gs.getScoreOfCounter(GameState.COUNTER_DARK) && 
								g.getScoreOfID(GameState.COUNTER_LIGHT) == gs.getScoreOfCounter(GameState.COUNTER_LIGHT)) {
							if (r.nextBoolean()) {
								trainingData.add(game);
								++trainingGames;
							} else {
								testData.add(game);
								++testGames;
							}
						} else {
							++failedGames;
							++badScoreGames;
						}
					} else {
						++failedGames;
						++movesLeftOverGames;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					++failedGames;
					++ranOutOfMovesGames;
				} catch (IllegalArgumentException e) {
					++failedGames;
					++illegalMoveGames;
				} catch (Exception e) {
					++failedGames;
					++otherFailGames;
				}
				
			}
			
			// Write games to a file if >= 100 games in a ArrayList.
			ArrayList<String> subData;
			if (trainingData.size() >= 100) {
				subData = new ArrayList<String>();
				for (int i = 0; i < 100; ++i) {
					subData.add(trainingData.get(0));
					trainingData.remove(0);
				}
				FileTools.writeFile(trainingDir + "train-"+trainingFilesMade+".oth", subData);
				++trainingFilesMade;
			}
			if (testData.size() >= 100) {
				subData = new ArrayList<String>();
				for (int i = 0; i < 100; ++i) {
					subData.add(testData.get(0));
					testData.remove(0);
				}
				FileTools.writeFile(testDir + "test-"+testFilesMade+".oth", subData);
				++testFilesMade;
			}
			
			++pathCount;
			if (pathCount % 100 == 0) {
				System.out.println(" " + pathCount + "/" + maxPaths + " files split...");
			}
			
		}
		
		// Write remaining data.
		ArrayList<String> subData;
		if (trainingData.size() > 0) {
			subData = new ArrayList<String>();
			for (int i = 0; i < trainingData.size(); ++i) {
				subData.add(trainingData.get(i));
			}
			FileTools.writeFile(trainingDir + "train-"+(trainingFilesMade)+".oth", subData);
			++trainingFilesMade;
		}
		if (testData.size() > 0) {
			subData = new ArrayList<String>();
			for (int i = 0; i < testData.size(); ++i) {
				subData.add(testData.get(i));
			}
			FileTools.writeFile(testDir + "test-"+(testFilesMade)+".oth", subData);
			++testFilesMade;
		}
		
		// Confirmation to user.
		System.out.println("Splitting complete:");
		System.out.println(" Training data: " + trainingFilesMade + " files with " + trainingGames + " games within.");
		System.out.println(" Test data: " + testFilesMade + " files with " + testGames + " games within.");
		System.out.println(failedGames + " of the " + (trainingGames + testGames + failedGames) + " could not be resimulated accurately, so have not been included in the training/test datasets.");
		System.out.println(" Failures from not matching the provided score: " + badScoreGames + ".");
		System.out.println(" Failures from playing an illegal move: " + illegalMoveGames + ".");
		System.out.println(" Failures from running out of scripted moves: " + ranOutOfMovesGames + ".");
		System.out.println(" Failures from having scripted moves left over: " + movesLeftOverGames + ".");
		System.out.println(" Uncategoried failures: " + otherFailGames + ".");
		System.out.println("Successful simulation rate: " + ((trainingGames + testGames) * ((float) 100) / (trainingGames + testGames + failedGames)) + "%");
		
	}
	
	public static void convertDatasetToScripts(String startDir, String storageDir) {;
	
		System.out.println("Begining coversion process...");
		int totalFiles = 0;
		int totalScripts = 0;
		for (String path : FileTools.getAllFilePathsInDir(startDir)) {
			if (path.endsWith(".oth")) {
				++totalFiles;
				GameScript[] allScripts = loadDatasetGames(path);
				ArrayList<String> strScripts = new ArrayList<String>();
				for (GameScript gs : allScripts) {
					++totalScripts;
					strScripts.add(gs.generateFileString());
				}
				FileTools.writeFile(storageDir+"batch"+(totalFiles-1)+".oth", strScripts);
			}
		}
		System.out.println("Conversion complete; " + totalFiles + " files containing " + totalScripts + " games converted.");
	}

	public static GameScript[] loadDatasetGames(String filePath) {
		
		ArrayList<String> file = FileTools.readFile(filePath);
		GameScript[] scripts = new GameScript[file.size()];
		int[] boardSizes = {8,8};
		for (int i = 0; i < scripts.length; ++i) {
			
			// Gets the parts of the move.
			String[] line = file.get(i).split(",");
			
			// Creating the list of moves.
			String moves = line[0];
			if (moves.length() % 2 == 1) {throw new IllegalArgumentException("Game move list has odd-numbered length, please fix.");}
			Point[] movesList = new Point[moves.length() / 2];
			for (int pos = 0; pos < moves.length(); pos += 2) {
				String singleMove = moves.substring(pos, pos + 2);
				int row = Integer.parseInt(singleMove.substring(1)) - 1;
				int col = -1;
				String[] colLabels = {"a", "b", "c", "d", "e", "f", "g", "h"};
				for (int l = 0; l < colLabels.length; ++l) {
					if (singleMove.substring(0,1).equals(colLabels[l])) {
						col = l;
						break;
					}
				}
				if (col == -1) throw new IllegalArgumentException("File has malformed move list.");
				Point singlePoint = new Point(row,col);
				movesList[pos / 2] = singlePoint;
			}
			
			// Creating the score array.
			String[] scores = line[1].substring(Math.max(line[1].indexOf(":", line[1].length()-5)-2, 0)).trim().split(":");
			int[] scoreList = {Integer.parseInt(scores[0]), Integer.parseInt(scores[1])};
			
			// Creating the script object.
			GameScript newScript = new GameScript(scoreList, movesList, boardSizes);
			scripts[i] = newScript;
			
		}
		
		return scripts;
	}
	
	public static GameScript loadArchivedGame(String filePath) {
		
		ArrayList<String> file = FileTools.readFile(filePath);
		
		// Get board's size.
		int[] boardSizes = {Integer.parseInt(file.get(0).split(",")[0]), Integer.parseInt(file.get(0).split(",")[1])};
		
		// Removes unneeded data.
		for (int n = 0; n < boardSizes[0] + 4; n++) {
			file.remove(0);
		}
		
		// Loops through remaining move data.
		Point[] movesList = new Point[file.size()-1];
		int ptr = 0;
		for (String line : file) {
			if (line.equals("END")) {
				break;
			}
			String pointStr = line.split(":")[1].substring(1,4);
			Point nextMove = new Point(Integer.parseInt(pointStr.substring(0,1)), Integer.parseInt(pointStr.substring(2)));
			movesList[ptr] = nextMove;
			++ptr;
		}
		
		// Doesn't bother to calculate the score.
		int[] scoresList = {-1,-1};
		
		return new GameScript(scoresList, movesList, boardSizes);
		
	}
	
	/**
	 * Creates data that is composed of a GameState and a label representing the % of wins resulting from that state.
	 */
	@SuppressWarnings("unused")
	private static ArrayList<String> dataFormat1(ArrayList<String> in, double fractionToUse, long seed) {
		
		ArrayList<String> allScripts = new ArrayList<String>(in);
		int gameCounter = 1;
		int scriptsToUse = Math.min(allScripts.size(), (int)(allScripts.size() * fractionToUse));
		Random r = new Random(seed);
		Map<String, Integer> plays = new HashMap<String, Integer>();
		Map<String, Integer> wins = new HashMap<String, Integer>();
		
		System.out.println(" " + scriptsToUse + " of the " + allScripts.size() + " scripts provided will be used.");
		
		// Loop for all scripts provided.
		for (int n = 0; n < scriptsToUse; ++n) {
			
			// Fetch GameScript and store necessary values.
			String game = allScripts.get(r.nextInt(allScripts.size()));
			allScripts.remove(game);
			GameScript gs = new GameScript(game);
			int darkResult = Math.max(0, gs.darkResult());
			
			// Loop through all game states in the script.
			for (int turn = 0; turn < gs.getTotalMoves(); ++turn) {
						
				// Stores the data about the GameState in the maps.
				GameState g = gs.generateStateAfterTurn(turn);
				String[] allRotations = {
						g.toFlatString(GameState.COUNTER_DARK, GameState.COUNTER_LIGHT, ","), 
						g.rotate(1).toFlatString(GameState.COUNTER_DARK, GameState.COUNTER_LIGHT, ","), 
						g.rotate(2).toFlatString(GameState.COUNTER_DARK, GameState.COUNTER_LIGHT, ","), 
						g.rotate(3).toFlatString(GameState.COUNTER_DARK, GameState.COUNTER_LIGHT, ",")
				};
				
				boolean placed = false;
				for (String rotatedState : allRotations) {
					if (plays.containsKey(rotatedState)) { 
						plays.put(rotatedState, plays.get(rotatedState) + 1);
						wins.put(rotatedState, wins.get(rotatedState) + darkResult);
						placed = true;
						break;
					}
				}
				if (!placed) {
					plays.put(allRotations[0], 1);
					wins.put(allRotations[0], darkResult);
				}
			}
			if (gameCounter % 1000 == 0) {
				System.out.println(" Game " + gameCounter + "/" + scriptsToUse + " complete.");
			}
			++gameCounter;
		}
		
		// Pray that Java didn't run out of memory, then analyse the maps.
		System.out.println(" Analysing " + plays.keySet().size() + " games.");
		ArrayList<String> csvLines = new ArrayList<String>();
		for (String gamestate : plays.keySet()) {
			double winRatio = (double)wins.get(gamestate)/(double)plays.get(gamestate);
			String newline = winRatio + "," + gamestate;
			csvLines.add(newline);
		}
		
		return csvLines;
	}
	
	/**
	 * Creates data that is composed of a GameState and a label representing whether or not the black player wins.
	 */
	private static ArrayList<String> dataFormat2(ArrayList<String> in, double fractionToUse, long seed) {
		
		ArrayList<String> allScripts = new ArrayList<String>(in);
		int gameCounter = 1;
		int scriptsToUse = Math.min(allScripts.size(), (int)(allScripts.size() * fractionToUse));
		Random r = new Random(seed);
		Map<String, Integer> results = new HashMap<String, Integer>();
		
		System.out.println(" " + scriptsToUse + " of the " + allScripts.size() + " scripts provided will be used.");
		
		// Loop for all scripts provided.
		for (int n = 0; n < scriptsToUse; ++n) {
			
			// Fetch GameScript and store necessary values.
			String game = allScripts.get(r.nextInt(allScripts.size()));
			allScripts.remove(game);
			GameScript gs = new GameScript(game);
			int darkResult = Math.max(0, gs.darkResult());
			
			// Loop through all game states in the script.
			for (int turn = 0; turn < gs.getTotalMoves(); ++turn) {
						
				// Stores the data about the GameState in the maps.
				GameState g = gs.generateStateAfterTurn(turn);
				String gamestate = g.toFlatString(GameState.COUNTER_DARK, GameState.COUNTER_LIGHT, ",");
				results.put(gamestate, darkResult);
				
			}
			if (gameCounter % 1000 == 0) {
				System.out.println(" Game " + gameCounter + "/" + scriptsToUse + " complete.");
			}
			++gameCounter;
		}
		
		// Pray that Java didn't run out of memory, then analyse the maps.
		System.out.println(" Analysing " + results.keySet().size() + " states.");
		ArrayList<String> csvLines = new ArrayList<String>();
		for (String gamestate : results.keySet()) {
			String newline = results.get(gamestate) + "," + gamestate;
			csvLines.add(newline);
		}
		
		return csvLines;
	}
	
}
