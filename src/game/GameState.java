package game;

import java.awt.Point;

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
		for (int row = 0; row < BOARD_SIZE; ++row) {
			for (int col = 0; col < BOARD_SIZE; ++col) {
				this.board[row][col] = 0;
			}
		}
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
	 * Increases the turn number. Only used when a counter is placed onto the board.
	 */
	private void incTurnNumber() {++this.turnNumber;}
	
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
	public int getBoardValue(Point p) {
		try {
			return this.board[p.x][p.y];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("The game board is of size " + BOARD_SIZE + " by " + BOARD_SIZE + ". The coordinates entered are out of these bounds.");
		}
	}
	
	/*
	 * Returns the width and height of the game board as an array.
	 */
	public int[] getBoardDims() {
		int[] dims = {BOARD_SIZE, BOARD_SIZE};
		return dims;
	}
	
	/*
	 * Returns the total number of counters a player owns,
	 * which requires the player's ID.
	 */
	public int getScore(int i) {
		int sum = 0;
		for (int row = 0; row < BOARD_SIZE; ++row) {
			for (int col = 0; col < BOARD_SIZE; ++col) {
				if (this.getBoardValue(new Point(row, col)) == i) {
					++sum;
				}
			}
		}
		return sum;
	}
	
	/*
	 * Shortcut to passing player ID to the function.
	 */
	public int getScore(Player p) {return getScore(p.getPlayerID());}
	
	/*
	 * Allows score to be retrieved for the player at the specified
	 * index of the player array. 
	 */
	public int getScoreOfPlayer(int i) {return this.getScore(this.getPlayer(i));}
	
	/*
	 * Determines the number of empty spaces on the game board.
	 */
	public int getEmptySpaces() {
		return (BOARD_SIZE * BOARD_SIZE) - getScoreOfPlayer(0) - getScoreOfPlayer(1);
	}
	
	/*
	 * Returns a boolean array of where the provided player can play their counters.
	 */
	public boolean[][] getLegalMoves(Player p) {
		
		boolean[][] legalMoves = new boolean[BOARD_SIZE][BOARD_SIZE];
		
		for (int row = 0; row < BOARD_SIZE; ++row) {
			for (int col = 0; col < BOARD_SIZE; ++col) {
				int boardValue = getBoardValue(new Point(row, col));
				// Checks if board space is already occupied.
				if (boardValue == 0) {
					
					// Determines if there is another counter within one space of the position.
					boolean canMove = false;
					for (int localRow = row - 1; localRow <= row + 1; ++localRow) {
						for (int localCol = col - 1; localCol <= col + 1; ++localCol) {
							if (localRow >= 0 && localRow < BOARD_SIZE && localCol >= 0 && localCol < BOARD_SIZE) {
								if (getBoardValue(new Point(localRow, localCol)) != 0) {
									canMove = true;
								}
							}
						}
					}
					
					legalMoves[row][col] = canMove;
					
				} else {
					legalMoves[row][col] = false;
				}
			}
		}
		
		return legalMoves;
		
	}
	
	/*
	 * Plays a player's counter at the provided point (shortcut function).
	 */
	public GameState playMove(Player p, Point move) {
		return playMove(p.getPlayerID(), move);
	}
	
	/*
	 * Plays a player's counter at the provided point.
	 */
	public GameState playMove(int id, Point move) {
		GameState newState = new GameState(this);
		newState.placeCounter(id, move);
		newState.incTurnNumber();
		return newState;
	}
	
	private void placeCounter(int id, Point move) {
		this.board[move.x][move.y] = id;
	}
	
	/*
	 * Gets the number of moves available for a player.
	 */
	public int getNumberOfMoves(Player p) {
		int sum = 0;
		boolean[][] legalMoves = getLegalMoves(p);
		for (int row = 0; row < BOARD_SIZE; ++row) {
			for (int col = 0; col < BOARD_SIZE; ++col) {
				if (legalMoves[row][col]) {++sum;}
			}
		}
		return sum;
	}
	
	/*
	 * Gets the number of moves available for a player.
	 */
	public boolean hasLegalMoves(Player p) {
		boolean[][] legalMoves = getLegalMoves(p);
		for (int row = 0; row < BOARD_SIZE; ++row) {
			for (int col = 0; col < BOARD_SIZE; ++col) {
				if (legalMoves[row][col]) {return true;}
			}
		}
		return false;
	}
	
	/*
	 * Determines if the game has progressed as far as it possibly can.
	 */
	public boolean isOver() {
		
		// Determine if board is at capacity.
		boolean boardFull = getEmptySpaces() == 0;
		
		// Determine if any moves are available for each player.
		boolean noMovesLeft = !(hasLegalMoves(getPlayer(0)) || hasLegalMoves(getPlayer(1)));
		
		return (boardFull || noMovesLeft);

	}
	
	/*
	 * Creates a GameState object that has been rotated by 90 degrees a 
	 * certain number of times from this GameState.
	 */
	public GameState rotate(int numRotates) {
		GameState newState = new GameState(this);
		for (int i = 0; i < numRotates; ++i) {
			newState.rotateAndChange();
		}
		return newState;
	}
	
	/*
	 * Rotates the board 90 degrees clockwise for the number of times specified.
	 * Directly changes the game state.
	 */
	private void rotateAndChange() {
		int[][] oldBoard = this.getBoard();
		for (int row = 0; row < BOARD_SIZE; ++row) {
			for (int col = 0; col < BOARD_SIZE; ++col) {
				this.board[row][col] = oldBoard[col][BOARD_SIZE - row];
			}
		}
	}

	/*
	 * Checks to see if the provided game state is identical to this one.
	 */
	public boolean isEqual(GameState that) {
		boolean result = true;
		result = result && (this.hasSameBoardAs(that));
		result = result && (this.getPlayer(0).equals(that.getPlayer(0)));
		result = result && (this.getPlayer(1).equals(that.getPlayer(1)));
		result = result && (this.turnNumber == that.getTurnNumber());
		return result;
		
	}
	
	/*
	 * Checks if a GameState is a rotation of another GameState.
	 * Only checks the board of the game, not players or turn number.
	 */
	public boolean isRotationOf(GameState that) {
		for (int rotation = 0; rotation < 4; ++rotation) {
			GameState thatRotated = that.rotate(rotation);
			if (this.hasSameBoardAs(thatRotated)) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Checks if two GameStates have identical boards.
	 */
	public boolean hasSameBoardAs(GameState that) {
		int[] thatDims = that.getBoardDims();
		if (BOARD_SIZE == thatDims[0]) {
			for (int row = 0; row < BOARD_SIZE; ++row) {
				for (int col = 0; col < BOARD_SIZE; ++col) {
					if (this.getBoardValue(new Point(row, col)) != that.getBoardValue(new Point(row, col))) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}
	
	/*
	 * Method for easily displaying the game state.
	 */
	public String toString() {
		
		String divider = " -";
		for (int i = 0; i < BOARD_SIZE; ++i) {
			divider += "--";
		}
		
		String theString = "Board:\n" + divider + "\n |";
		
		for (int row = 0; row < BOARD_SIZE; ++row) {
			for (int col = 0; col < BOARD_SIZE; ++col) {
				theString += this.getBoardValue(new Point(row, col)) + "|";
			}
			theString += "\n" + divider + "\n |";
		}
		
		theString = theString.substring(0, theString.length() - 1) + "\n";
		
		theString += "Players:\n";
		theString += " 1. " + this.players[0].toString() + "\n";
		theString += " 2. " + this.players[1].toString() + "\n\n";
		
		theString += "Turn Number: " + this.turnNumber;
		
		return theString;
		
	}
	
}
