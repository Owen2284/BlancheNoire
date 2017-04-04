package game;

import java.awt.Point;

import players.Player;
import players.ScriptPlayer;

public class GameScript {
	
	private int[] scores;
	private Point[] moves;
	private int[] dims;

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
	
	public GameState generateStateAfterTurn(int turnNum) {
		GameState g = this.generateStartState();		
		if (turnNum <= 0) {
			return g;
		}
		Player p = g.getPlayerByIndex(0);
		while ((g.getTurnNumber() <= turnNum) && (!g.isOver())) {
			if (g.hasLegalMoves(p)) {
				g = g.playMove(p, p.getMove(g, null));
			}
			p = g.getOpposingPlayer(p);
		}
		return g;
	}
	
	public GameState generateEndState() {
		return generateStateAfterTurn(getTotalMoves());
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
	
	public Point getMove(int i) {
		try {
			return this.moves[i];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Invalid move number.");
		}
	}
	
	/**
	 * Returns the total number of moves played during the game.
	 */
	public int getTotalMoves() {
		return this.moves.length;
	}
	
	/**
	 * Returns the score of the specified ID.
	 */
	public int getScoreOfCounter(int counterID) {
		try {
			return this.scores[counterID-1];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Invalid counter ID.");
		}
	}
	
	/**
	 * Determines if the dark player won, drew or lost.
	 */
	public int darkResult() {
		if (scores[0] > scores[1]) {
			return 1;
		} else if (scores[0] == scores[1]) {
			return 0;
		} else {
			return -1;
		}		
	}
	
	/**
	 * Determines the difference between the dark and light players' scores.
	 */
	public int darkDiff() {
		return scores[0] - scores[1];
	}

}
