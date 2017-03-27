package game;

import java.awt.Point;
import java.util.ArrayList;

import util.FileTools;

public class GameConverter {
	
	public static void main(String[] args) {
		loadExtractedGames("games/extracted/1977-1.oth");
		loadArchivedGame("games/archive/Game1489364584546.txt");
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
			//if (moves.length() % 2 == 1) {throw new Exception("Game move list has odd-numbered length, please fix.");}
			Point[] movesList = new Point[moves.length() / 2];
			for (int pos = 0; pos < moves.length(); pos += 2) {
				String singleMove = moves.substring(pos, pos + 2);
				Point singlePoint = null;
				movesList[pos / 2] = singlePoint;
			}
			// Creating the score array.
			String[] scores = line[1].split(":");
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
		for (int n = 0; n < boardSizes[0] + 3; n++) {
			file.remove(1);
		}
		for (String line : file) {
			System.out.println(line);
		}
		return null;
	}
}
