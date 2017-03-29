package game;

import java.awt.Point;

import players.ScriptPlayer;

public class GameScript {
	
	public int[] scores;
	public Point[] moves;
	public int[] dims;

	public GameScript(int[] scores, Point[] moves, int[] dims) {
		this.scores = (int[]) scores.clone();
		this.moves = (Point[]) moves.clone();
		this.dims = (int[]) dims.clone();
	}
	
	public GameScript(String fileString) {
		
		// Moves
		String movesString = fileString.split(":")[0];
		String[] moveStrings = movesString.split(";");
		this.moves = new Point[moveStrings.length];
		for (int i = 0; i < this.moves.length; ++i) {
			this.moves[i] = new Point(Integer.parseInt(moveStrings[i].split(",")[0]),Integer.parseInt(moveStrings[i].split(",")[1]));
		}
		
		// Scores
		this.scores = new int[2];
		String scoresString = fileString.split(":")[1];
		this.scores[0] = Integer.parseInt(scoresString.split(";")[0]);
		this.scores[1] = Integer.parseInt(scoresString.split(";")[1]);
		
		// Dimensions
		this.dims = new int[2];
		String dimsString = fileString.split(":")[2];
		this.dims[0] = Integer.parseInt(dimsString.split(";")[0]);
		this.dims[1] = Integer.parseInt(dimsString.split(";")[1]);
		
	}
	
	public GameState generateStartState() {
		GameState startState = new GameState(new ScriptPlayer(GameState.COUNTER_DARK, this), new ScriptPlayer(GameState.COUNTER_LIGHT, this), dims[0]);
		return startState;
	}
	
	public String generateFileString() {
		String fileString = "";
		for (Point move : moves) {
			fileString += move.x + "," + move.y + ";";
		}
		fileString = fileString.substring(0, fileString.length()-1) + ":";
		fileString += scores[0] + ";" + scores[1] + ":";
		fileString += dims[0] + ";" + dims[1];
		return fileString;
	}

}
