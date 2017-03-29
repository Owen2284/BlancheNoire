package game;

import java.awt.Point;

import players.Player;

/**
 * A class for representing the Othello game state and logic. Provides methods for
 * determining legal moves, future states and game outcomes.
 */
public class GameState {
	
	// Stores 0 (empty), 1 (dark counter) or 2 (light counter).
	private int[][] board;				
	
	// Stores two player object references.
	private Player[] players;
	
	// Tracks the turn number.
	private int turnNumber;
	
	// Stores the size of the game board.
	private int boardSize;
	
	// Caches for holding data that takes a while to calculate.
	private boolean[][][] legalMovesCache;
	private int[] scoreCache;
	
	public static final int COUNTER_EMPTY = 0;
	public static final int COUNTER_DARK = 1;
	public static final int COUNTER_LIGHT = 2;

	/**
	 * Basic constructor.
	 */
	public GameState(Player p1, Player p2) {
		this(p1, p2, 8);
	}
	
	/**
	 * Variable board size constructor.
	 */
	public GameState(Player p1, Player p2, int boardSize) {
		if (boardSize >= 2) {
			this.boardSize = boardSize;
			this.board = new int[boardSize][boardSize];
			for (int row = 0; row < boardSize; ++row) {
				for (int col = 0; col < boardSize; ++col) {
					this.board[row][col] = COUNTER_EMPTY;
				}
			}
			this.board[(boardSize / 2) - 1][(boardSize / 2) - 1] = COUNTER_LIGHT;
			this.board[(boardSize / 2)][(boardSize / 2) - 1] = COUNTER_DARK;
			this.board[(boardSize / 2) - 1][(boardSize / 2)] = COUNTER_DARK;
			this.board[(boardSize / 2)][(boardSize / 2)] = COUNTER_LIGHT;
			this.players = new Player[2];
			this.players[0] = p1;
			this.players[1] = p2;
			this.turnNumber = 1;
			this.legalMovesCache = new boolean[2][this.boardSize][this.boardSize];
			this.legalMovesCache[0] = null;
			this.legalMovesCache[1] = null;
			this.scoreCache = new int[2];
			computeScores();
		} else {
			throw new IllegalArgumentException("Board size must be greater than or equal to 2.");
		}
	}
	
	/**
	 * Cloning constructor.
	 */
	public GameState(GameState that) {
		this.boardSize = that.getBoardDims()[0];		
		
		this.board = that.getBoard();
		this.board = new int[boardSize][boardSize];	
		int[][] thatBoard = that.getBoard();
		for (int row = 0; row < this.boardSize; ++row) {
			for (int col = 0; col < this.boardSize; ++col) {
				this.board[row][col] = thatBoard[row][col];
			}
		}
		
		this.players = new Player[2];
		this.players[0] = that.getPlayer(0);
		this.players[1] = that.getPlayer(1);
		this.turnNumber = that.getTurnNumber();
		this.legalMovesCache = new boolean[2][this.boardSize][this.boardSize];
		this.legalMovesCache[0] = null;
		this.legalMovesCache[1] = null;
		this.scoreCache = new int[2];
		computeScores();
	}
	
	/**
	 * Returns a clone of the game board.
	 */
	public int[][] getBoard() {
		return (int[][]) this.board.clone();
	}
	
	/**
	 * Returns the player object with the specified index in the player array.
	 */
	public Player getPlayer(int playerNumber) {
		try {
			return this.players[playerNumber];
		} catch (Error e) {
			throw new ArrayIndexOutOfBoundsException("Player array index accessed incorrectly, array is zero-indexed and contains two Players.");
		}
	}
	
	/**
	 * Returns the player in the game state that isn't the provided player.
	 * @param p - the player that is already known.
	 * @return the Player object representing the opposing player.
	 */
	public Player getOpposingPlayer(Player p) {
		if (p.equals(this.players[0])) {return this.players[1];}
		else if (p.equals(this.players[1])) {return this.players[0];}
		else {throw new IllegalArgumentException("Provided Player object is not participating in this game.");}
	}
	
	/**
	 * Returns the current turn number. The turn number is at zero when the 
	 * object is initialised.
	 */
	public int getTurnNumber() {
		return this.turnNumber;
	}
	
	/**
	 * Increases the turn number. Only used when a counter is placed onto the board.
	 */
	private void incTurnNumber() {++this.turnNumber;}
	
	/**
	 * Returns the maximum number of turns the game can last for.
	 */
	public int getMaxTurns() {
		return (boardSize * boardSize) - 4;
	}
	
	/**
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
	
	/**
	 * Returns the number of turns remaining before the end of the game.
	 */
	public int getTurnsLeft() {
		int totalTurns = getMaxTurns();
		return totalTurns - this.turnNumber;
	}
	
	/**
	 * Gets the value located at a specific point of the game board.
	 */
	public int getBoardValue(Point p) {
		try {
			return this.board[p.x][p.y];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ArrayIndexOutOfBoundsException("The game board is of size " + boardSize + " by " + boardSize + ". The coordinates entered are out of these bounds.");
		}
	}
	
	/**
	 * Returns the width and height of the game board as an array.
	 */
	public int[] getBoardDims() {
		int[] dims = {boardSize, boardSize};
		return dims;
	}
	
	/**
	 * Returns the total number of counters a player owns,
	 * which requires the player's ID.
	 */
	public int getScore(int id) {
		try {
			return this.scoreCache[id-1];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new IllegalArgumentException("Invalid ID passed to getScore function.");
		}
	}
	
	/**
	 * Shortcut to passing player ID to the function.
	 */
	public int getScoreOfPlayer(Player p) {return getScore(p.getPlayerID());}
	
	/**
	 * Allows score to be retrieved for the player at the specified
	 * index of the player array. 
	 */
	public int getScoreOfPlayer(int i) {return this.getScore(this.getPlayer(i).getPlayerID());}
	
	/**
	 * Counts the scores of both players and stores them in a cache.
	 */
	private void computeScores() {
		this.scoreCache[0] = computeScore(COUNTER_DARK);
		this.scoreCache[1] = computeScore(COUNTER_LIGHT);
	}
	private int computeScore(int id) {
		int sum = 0;
		for (int row = 0; row < boardSize; ++row) {
			for (int col = 0; col < boardSize; ++col) {
				if (this.getBoardValue(new Point(row, col)) == id) {
					++sum;
				}
			}
		}
		return sum;
	}
	
	/**
	 * Determines the number of empty spaces on the game board.
	 */
	public int getEmptySpaces() {
		return (boardSize * boardSize) - getScoreOfPlayer(0) - getScoreOfPlayer(1);
	}
	
	/**
	 * Returns a boolean array of where the provided player can play their counters. (Shortcut)
	 */
	public boolean[][] getLegalMoves(Player p) {
		return getLegalMoves(p.getPlayerID());
	}
	
	/**
	 * Returns a boolean array of where the provided player can play their counters.
	 */
	public boolean[][] getLegalMoves(int id) {
		
		// Checks if an allowed counter type has been passed to the function.
		if (id == COUNTER_DARK || id == COUNTER_LIGHT) {
			
			// Checks the cache to see if the legal moves have already been calculated.
			if (this.legalMovesCache[id-1] != null) {
				return this.legalMovesCache[id-1];
			}
		
			this.legalMovesCache[id-1] = getLegalMovesNoCache(id);
			return this.legalMovesCache[id-1];
			
		} else {
			throw new IllegalArgumentException("Invalid player ID, no counter type that macthes the ID " + id + ".");
		}
		
	}
	
	/**
	 * Returns a boolean array of where the provided player can play their counters, without using the
	 * cache that the GameState uses to store legal move arrays.
	 * @param id - the ID of the counter that the player controls.
	 */
	public boolean[][] getLegalMovesNoCache(int id) {
		
		// Checks if an allowed counter type has been passed to the function.
		if (id == COUNTER_DARK || id == COUNTER_LIGHT) {
			
			boolean[][] legalMoves = new boolean[boardSize][boardSize];

			for (int row = 0; row < boardSize; ++row) {
				for (int col = 0; col < boardSize; ++col) {
					int boardValue = getBoardValue(new Point(row, col));
					// Checks if board space is already occupied.
					if (boardValue == COUNTER_EMPTY) {
						
						// Determines if there is another counter within one space of the position.
						boolean canMove = false;
						for (int localRow = row - 1; localRow <= row + 1; ++localRow) {
							for (int localCol = col - 1; localCol <= col + 1; ++localCol) {
								if (localRow >= 0 && localRow < boardSize && localCol >= 0 && localCol < boardSize && !(localRow == row && localCol == col)) {
									canMove = getFlippedCounters(row, col, localRow - row, localCol - col, id) > 0;
									if (canMove) {
										break;
									}
								}
							}
							if (canMove) {
								break;
							}
						}
						
						legalMoves[row][col] = canMove;
						
					} else {
						legalMoves[row][col] = false;
					}
				}
			}
			
			return legalMoves;		
			
			
		} else {
			throw new IllegalArgumentException("Invalid player ID, no counter type that macthes the ID " + id + ".");
		}
		
	}
	
	/**
	 * Determines how many counters would be flipped in a certain direction
	 * if a counter is placed at the provided coordinates.
	 */
	private int getFlippedCounters(int initRow, int initCol, int deltaRow, int deltaCol, int counterType) {
		// Initialising counter and coordinate variables.
		int lineLength = 0;
		int row = initRow + deltaRow;
		int col = initCol + deltaCol;
		
		// Loop to travel along the line specified by the delta parameters.
		while (row >= 0 && row < boardSize && col >= 0 && col < boardSize) {

			// Determine what counter is at the current locaation in the line.
			if (getBoardValue(new Point(row,col)) == counterType) {
				// Player's counter found, return number of counters 
				// between initial counter and this counter.
				return lineLength;
			} else if (getBoardValue(new Point(row,col)) != COUNTER_EMPTY) {
				// Opponent counter found, increment number of counters on line
				// that can be flipped.
				++lineLength;
			} else {
				// Empty space found, no bracketing possible.
				return 0;
			}
			
			// Move to next location.
			row += deltaRow;
			col += deltaCol;
			
		}
		
		// Edge of board reached, no bracketing possible.
		return 0;
		
	}
	
	/**
	 * Gets values of getFlippedCounters() in each direction from a provided
	 * point for a specified player.
	 */
	public int[][] getLinesFrom(int row, int col, int counterType) {
		int[][] lines = new int[3][3];
		lines[0][0] = getFlippedCounters(row, col, -1, -1, counterType);
		lines[0][1] = getFlippedCounters(row, col, -1, 0, counterType);
		lines[0][2] = getFlippedCounters(row, col, -1, 1, counterType);
		lines[1][0] = getFlippedCounters(row, col, 0, -1, counterType);
		lines[1][1] = 0;
		lines[1][2] = getFlippedCounters(row, col, 0, 1, counterType);
		lines[2][0] = getFlippedCounters(row, col, 1, -1, counterType);
		lines[2][1] = getFlippedCounters(row, col, 1, 0, counterType);
		lines[2][2] = getFlippedCounters(row, col, 1, 1, counterType);
		return lines;
	}
	
	/**
	 * Plays a player's counter at the provided point (shortcut function).
	 */
	public GameState playMove(Player p, Point move) {
		return playMove(p.getPlayerID(), move);
	}
	
	/**
	 * Plays a player's counter at the provided point.
	 */
	public GameState playMove(int id, Point move) {
		
		// Check that a valid move has been passed to the function.
		if (move == null) {
			throw new IllegalArgumentException("Null object passed to playMove function, requires Point object.");
		}
		
		// Creates a copy of the current GameState to return.
		GameState newState = new GameState(this);
		
		// Gets the row and column number, plus the lengths of the lines of
		// counters that can be flipped from this point.
		int row = move.x;
		int col = move.y;
		int[][] lines = getLinesFrom(row, col, id);
		
		// Checks that the move passed to the function is legal.
		if (this.getLegalMovesNoCache(id)[row][col]) {
		
			// Places the counter the player want to play.
			newState.placeCounter(id, move);
			
			// Processes the flipping of counters from this point.
			for (int linesRow = 0; linesRow < 3; ++linesRow) {
				for (int linesCol = 0; linesCol < 3; ++linesCol) {
					int lineLength = lines[linesRow][linesCol];
					int localRow = row;
					int localCol = col;
					while (lineLength > 0) {
						localRow += (linesRow - 1);
						localCol += (linesCol - 1);
						newState.placeCounter(id, new Point(localRow, localCol));
						--lineLength;
					}
				}
			}
			
			// Increment state turn number and return the GameState
			newState.incTurnNumber();
			newState.computeScores();
			return newState;
		
		} else {
			throw new IllegalArgumentException("No legal move at (" + row + "," + col + ").");
		}
		
	}
	
	private void placeCounter(int id, Point move) {
		this.board[move.x][move.y] = id;
	}
	
	/**
	 * Gets the number of moves available for a player.
	 */
	public int getNumberOfMoves(Player p) {
		int sum = 0;
		boolean[][] legalMoves = getLegalMoves(p);
		for (int row = 0; row < boardSize; ++row) {
			for (int col = 0; col < boardSize; ++col) {
				if (legalMoves[row][col]) {++sum;}
			}
		}
		return sum;
	}
	
	/**
	 * Gets the number of moves available for a player.
	 */
	public boolean hasLegalMoves(Player p) {
		boolean[][] legalMoves = getLegalMoves(p);
		for (int row = 0; row < boardSize; ++row) {
			for (int col = 0; col < boardSize; ++col) {
				if (legalMoves[row][col]) {return true;}
			}
		}
		return false;
	}
	
	/**
	 * Determines if the game has progressed as far as it possibly can.
	 */
	public boolean isOver() {
		
		// Determine if any moves are available for each player.
		return (!(hasLegalMoves(getPlayer(0)) || hasLegalMoves(getPlayer(1))));

	}
	
	/**
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
	
	/**
	 * Rotates the board 90 degrees clockwise for the number of times specified.
	 * Directly changes the game state.
	 */
	private void rotateAndChange() {
		int[][] oldBoard = this.getBoard();
		for (int row = 0; row < boardSize; ++row) {
			for (int col = 0; col < boardSize; ++col) {
				this.board[row][col] = oldBoard[col][boardSize - row - 1];
			}
		}
	}

	/**
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
	
	/**
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
	
	/**
	 * Checks if two GameStates have identical boards.
	 */
	public boolean hasSameBoardAs(GameState that) {
		int[] thatDims = that.getBoardDims();
		if (boardSize == thatDims[0]) {
			for (int row = 0; row < boardSize; ++row) {
				for (int col = 0; col < boardSize; ++col) {
					if (this.getBoardValue(new Point(row, col)) != that.getBoardValue(new Point(row, col))) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Method for easily displaying the game state.
	 */
	public String toString() {
		
		String divider = " -";
		for (int i = 0; i < boardSize; ++i) {
			divider += "--";
		}
		
		String theString = "Board:\n" + divider + "\n |";
		
		for (int row = 0; row < boardSize; ++row) {
			for (int col = 0; col < boardSize; ++col) {
				theString += this.getBoardValue(new Point(row, col)) + "|";
			}
			theString += "\n" + divider + "\n |";
		}
		
		theString = theString.substring(0, theString.length() - 1) + "\n";
		
		theString += "Players:\n";
		theString += " 1. " + this.players[0].getPlayerType() + " - " + this.getScoreOfPlayer(this.players[0]) + "\n";
		theString += " 2. " + this.players[1].getPlayerType() + " - " + this.getScoreOfPlayer(this.players[1]) + "\n\n";
		
		theString += "Turn Number: " + this.turnNumber;
		
		return theString;
		
	}
	
	/**
	 * Method for getting a string that can be used to construct a GameState.
	 */
	public String toFileString() {
		
		String theString = this.boardSize + "," + this.boardSize + "\n";
		
		for (int row = 0; row < boardSize; ++row) {
			for (int col = 0; col < boardSize; ++col) {
				theString += this.getBoardValue(new Point(row, col));
			}
			theString += "\n";
		}
		
		theString += this.players[0].toFileString() + "\n";
		theString += this.players[1].toFileString() + "\n";
		
		theString += this.turnNumber + "\n";
		
		return theString;
		
	}
	
	/**
	 * Debug method for determining correctness of Othello logic.
	 */
	public void printLinesFrom(int row, int col, int counterType) {
		String lines = "";
		lines += getFlippedCounters(row, col, -1, -1, counterType) + ",";
		lines += getFlippedCounters(row, col, -1, 0, counterType) + ",";
		lines += getFlippedCounters(row, col, -1, 1, counterType);
		lines += "\n";
		lines += getFlippedCounters(row, col, 0, -1, counterType) + ",";
		lines += "-,";
		lines += getFlippedCounters(row, col, 0, 1, counterType);
		lines += "\n";
		lines += getFlippedCounters(row, col, 1, -1, counterType) + ",";
		lines += getFlippedCounters(row, col, 1, 0, counterType) + ",";
		lines += getFlippedCounters(row, col, 1, 1, counterType);
		System.out.println(lines);
	}
	
	/**
	 * Debug method for viewing the game board.
	 */
	public void printBoard(Player p) {
		for (int row = 0; row < getBoardDims()[0]; ++row) {
			for (int col = 0; col < getBoardDims()[1]; ++col) {
				System.out.print(this.board[row][col]);
			}
			System.out.println("");
		}
	}
	
	/**
	 * Debug method for viewing the legal moves board.
	 */
	public void printLegalMoves(Player p) {
		for (int lRow = 0; lRow < getBoardDims()[0]; ++lRow) {
			for (int lCol = 0; lCol < getBoardDims()[1]; ++lCol) {
				if (getLegalMoves(p)[lRow][lCol]) {
					System.out.print("T");
				} else {
					System.out.print("F");
				}
			}
			System.out.println("");
		}
	}
	
}