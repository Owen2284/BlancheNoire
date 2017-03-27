package game;

import java.awt.Point;

public class GameScript {
	
	public int[] scores;
	public Point[] moves;
	public int[] dims;

	public GameScript(int[] scores, Point[] moves, int[] dims) {
		this.scores = (int[]) scores.clone();
		this.moves = (Point[]) moves.clone();
		this.dims = (int[]) dims.clone();
	}
	
	public GameState generateStartState() {
		
	}

}
