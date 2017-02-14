package game;

/*
 * A class for representing the Othello game state and logic. Provides methods for
 * determining 
 */
public class GameState {
	
	// Stores 0 (empty), 1 (player 1 counter) or 2 (player 2 counter).
	private int[][] board;				
	
	// Stores two player object references.
	private Player[] players;
	
	// Tracks the turn number.
	private int turnNumber;
	
	private final int BOARD_SIZE = 8;
	
	public static final int COUNTER_DARK = 1;
	public static final int COUNTER_LIGHT = 2;

	/*
	 * Basic constructor.
	 */
	public GameState(Player p1, Player p2) {
		this.board = new int[BOARD_SIZE][BOARD_SIZE];
		this.board[(BOARD_SIZE / 2) - 1][(BOARD_SIZE / 2) - 1] = COUNTER_LIGHT;
		this.board[(BOARD_SIZE / 2)][(BOARD_SIZE / 2) - 1] = COUNTER_DARK;
		this.board[(BOARD_SIZE / 2) - 1][(BOARD_SIZE / 2)] = COUNTER_DARK;
		this.board[(BOARD_SIZE / 2)][(BOARD_SIZE / 2)] = COUNTER_LIGHT;
		this.players = new Player[2];
		this.players[0] = p1;
		this.players[1] = p2;
		this.turnNumber = 1;
	}
	
	/*
	 * Cloning constructor.
	 */
	public GameState(GameState that) {
		this.board = that.getBoard();
		this.players = new Player[2];
		this.players[0] = that.getPlayer(0);
		this.players[1] = that.getPlayer(1);
		this.turnNumber = that.getTurnNumber();
	}
	
	/*
	 * Returns a clone of the game board.
	 */
	public int[][] getBoard() {
		return (int[][]) this.board.clone();
	}
	
	/*
	 * Returns the player object with the specified index.
	 */
	public Player getPlayer(int playerNumber) {
		try {
			return this.players[playerNumber];
		} catch (Error e) {
			throw new ArrayIndexOutOfBoundsException("Player array index accessed incorrectly, array is zero-indexed.");
		}
	}
	
	/*
	 * Returns the current turn number. The turn number is at zero when the 
	 * object is initialised.
	 */
	public int getTurnNumber() {
		return this.turnNumber;
	}
	
	/*
	 * Returns the maximum number of turns the game can last for.
	 */
	public int getMaxTurns() {
		return (BOARD_SIZE * BOARD_SIZE) - 4;
	}
	
	/*
	 * Returns the "phase" of the game, which can be "Opening" (first 20 turns),
	 * "Midgame" (middle 20 turns) or "Endgame" (last 20 turns).
	 */
	public String getGamePhase() {
		int totalTurns = getMaxTurns();
		if (this.turnNumber <= totalTurns / 3) {
			return "Opening";
		} else if (this.turnNumber <= 2 * (totalTurns / 3)) {
			return "Midgame";
		} else {
			return "Endgame";
		}
	}
	
	/*
	 * Returns the number of turns remaining before the end of the game.
	 */
	public int getTurnsLeft() {
		int totalTurns = getMaxTurns();
		return totalTurns - this.turnNumber;
	}
	
	/*
	 * Gets the value located at a specific point of the game board.
	 */
	public int getBoardValue(int row, int col) {
		try {
			return this.board[row][col];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("The game board is of size " + BOARD_SIZE + " by " + BOARD_SIZE + ". The coordinates entered are out of these bounds.");
		}
	}
	
	/*
	 * Returns a boolean array of where the provided player can play their counters.
	 */
	public boolean[][] getLegalMoves(Player p) {
		boolean[][] legalMoves = new boolean[BOARD_SIZE][BOARD_SIZE];
		for (int row = 0; row < BOARD_SIZE; ++row) {
			for (int col = 0; col < BOARD_SIZE; ++col) {
				int boardValue = getBoardValue(row, col);
				legalMoves[row][col] = false;
			}
		}
		return legalMoves;
	}

}
