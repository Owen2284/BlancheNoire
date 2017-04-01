package learning;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import game.GameScript;
import game.GameState;
import players.Player;
import util.FileTools;

/**
 * Class containing code used to handle the Othello game data.
 * @author Owen
 */
public class GameLoader {
	
	public static void main(String[] args) {
		//convertExtractedToScripts("games/extracted/", "games/scripts/");
		splitScripts("games/scripts/", "games/training/", "games/test/", System.currentTimeMillis());
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
					if (g.getTurnNumber()-1 == gs.moves.length) {
						if (g.getScoreOfID(GameState.COUNTER_DARK) == gs.scores[0] && g.getScoreOfID(GameState.COUNTER_LIGHT) == gs.scores[1]) {
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
			if (pathCount % 25 == 0) {
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
	
	public static void convertExtractedToScripts(String startDir, String storageDir) {;
	
		System.out.println("Begining coversion process...");
		int totalFiles = 0;
		int totalScripts = 0;
		for (String path : FileTools.getAllFilePathsInDir(startDir)) {
			if (path.endsWith(".oth")) {
				++totalFiles;
				GameScript[] allScripts = loadExtractedGames(path);
				ArrayList<String> strScripts = new ArrayList<String>();
				for (GameScript gs : allScripts) {
					++totalScripts;
					strScripts.add(gs.generateFileString());
				}
				FileTools.writeFile(storageDir+path.substring("games/extracted/".length()), strScripts);
			}
		}
		System.out.println("Conversion complete; " + totalFiles + " files containing " + totalScripts + " games converted.");
	}

	public static GameScript[] loadExtractedGames(String filePath) {
		
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
				int col;
				switch (singleMove.substring(0,1)) {
					case "a":col = 0;break;
					case "b":col = 1;break;
					case "c":col = 2;break;
					case "d":col = 3;break;
					case "e":col = 4;break;
					case "f":col = 5;break;
					case "g":col = 6;break;
					case "h":col = 7;break;
					default:throw new IllegalArgumentException("File has malformed move list.");
				}
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
}
