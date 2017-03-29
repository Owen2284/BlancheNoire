package players;

import java.awt.Point;
import java.util.NoSuchElementException;
import java.util.Scanner;

import game.GameState;
import ui.GamePanel;

/**
 * A Player subclass that represents a human-controlled player. Moves that a
 * HumanPlayer makes are determined by the human's input.
 */
public class HumanPlayer extends Player {

	private boolean usingGUI;
	private Scanner scanner;
	
	public HumanPlayer(int id, boolean useGUI) {
		super(id);
		this.usingGUI = useGUI;
		if (!usingGUI) {
			this.scanner = new Scanner(System.in);
		}
	}
	
	@Override
	public String getPlayerType() {
		return "Human";
	}
	
	@Override
	public String toFileString() {
		return "Human(" + this.id + "," + this.usingGUI + ")";
	}
		
	@Override
	public Point getMove(GameState game, GamePanel panel) {
		Point theMove;
		if (!usingGUI) {
			try {
				theMove = getMoveFromCommandPrompt(game);
			} catch (NoSuchElementException e) {
				System.out.println("Error occured, NoSuchElementException caught.");
				return null;
			}
		} else {
			theMove = panel.getPlayerMoveViaUI(game, this);
		}
		this.setOutput("Move played at (" + theMove.x + "," + theMove.y + ").");
		return theMove;
		
	}
	
	/**
	 * Method that obtains the player's move via the command prompt.
	 * (UNUSED - Doesn't work.)
	 * @param g - the current GameState.
	 * @return a Point on the board of the GameState to play a counter on.
	 */
	private Point getMoveFromCommandPrompt(GameState g) {
		
		int[] coord = new int[2];
		coord[0] = -1; coord[1] = -1;
		boolean[][] allowedMoves = g.getLegalMoves(this);
		boolean goodMove = false;
		
		while (!goodMove) {
			System.out.println("Enter the row you wish to place a counter at:");
			coord[0] = scanner.nextInt();
			System.out.println("Enter the column you wish to place a counter at:");
			coord[1] = scanner.nextInt();
			if (coord[0] >= 0 && coord[0] < g.getBoardDims()[0] && coord[1] >= 0 && coord[1] < g.getBoardDims()[1] && allowedMoves[coord[0]][coord[1]]) {
				goodMove = true;
			} else {
				System.out.println("Invalid move.");
			}
		}
		
		Point theMove = new Point(coord[0], coord[1]);
		return theMove;
		
	}



}
