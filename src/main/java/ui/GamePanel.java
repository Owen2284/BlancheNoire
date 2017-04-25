package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import games.GameState;
import players.Player;

/**
 * A UI panel for displaying the current games state.
 */
public class GamePanel extends JPanel implements MouseListener {
	
	// Fields for caching games data.
	private int[][] board;
	private boolean[][] legalMoves;
	private Player[] players;
	private int[] dimensions;
	private int playerPlaying;
	
	// Fields for allowing player move selection.
	private Point lastMousePos = null;

	// JPanel measurements.
	public static final int PANEL_WIDTH = OthelloFrame.FRAME_WIDTH;
	public static final int PANEL_HEIGHT = 480;
	
	// Variables for sizing the games board.
	private final int boardSize = (PANEL_HEIGHT - 80);
	private final int gridX = (PANEL_WIDTH / 2) - (boardSize / 2);
	private final int gridY = 20;
	private int squareSize = boardSize / 8;
	
	private static final long serialVersionUID = -5194744397408657473L;

	/**
	 * Default constructor.
	 */
	public GamePanel() {
		
		// Performs Swing-related setup.
		this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		this.setBackground(new Color(72, 196, 19));
		
		// Adds a mouse listener to the panel.
		this.addMouseListener(this);
		
		// Initialising cache data.
		this.board = new int[8][8];
		this.legalMoves = new boolean[8][8];
		this.players = new Player[2];
		this.players[0] = null;
		this.players[1] = null;
		this.dimensions = new int[2];
		this.dimensions[0] = 8;
		this.dimensions[1] = 8;
		for (int row = 0; row < this.dimensions[0]; ++row) {
			for (int col = 0; col < this.dimensions[1]; ++col) {
				this.board[row][col] = 0;
				this.legalMoves[row][col] = false;
			}
		}
		
	}
	
	/**
	 * Method that caches information about the games for use by drawing methods.
	 */
	public void updateUI(GameState game, Player playerToPlay) {
		
		// Caching data.
		this.board = game.getBoard();
		this.legalMoves = game.getLegalMoves(playerToPlay);
		this.players[0] = game.getPlayerByIndex(0);
		this.players[1] = game.getPlayerByIndex(1);
		this.dimensions = game.getBoardDims();
		this.playerPlaying = playerToPlay.getPlayerID();
		
		// Recalculating square size.
		squareSize = boardSize / this.dimensions[0];
		
	}
	
	/**
	 * Method that returns a move obtained via user input.
	 */
	public Point getPlayerMoveViaUI(GameState game, Player player) {

		while(true) {
			
			// Hold execution until a click position is passed.
			while(this.lastMousePos == null) {
				this.repaint();
			}
			
			// Analyse the obtained mouse position.
			int panelX = this.lastMousePos.x;
			int panelY = this.lastMousePos.y;
			if (panelX > this.gridX && panelX < this.gridX + (this.squareSize * game.getBoardDims()[0]) && panelY > this.gridY && panelY < this.gridY + (this.squareSize * game.getBoardDims()[1])) {
				int boardCol = (panelX - this.gridX) / squareSize;
				int boardRow = (panelY - this.gridY) / squareSize;
				this.lastMousePos = null;
				if (this.legalMoves[boardRow][boardCol]) {
					return new Point(boardRow, boardCol);
				}
			}
			
			this.lastMousePos = null;
			
		}
		
	}

	/**
	 * Method that paints the panel with the necessary graphics.
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Stores colours for the UI.
		Color borderColour = Color.BLACK;
		Color internalColour;
		
		// Grid and counter drawing code.
		int currentX = gridX;
		int currentY = gridY;
		for (int row = 0; row < this.dimensions[0]; ++row) {
			for (int col = 0; col < this.dimensions[1]; ++col) {
				
				// Draws grid.
				if (this.legalMoves[row][col]) {
					// Set colour to possible move square colour.
					internalColour = new Color(45, 84, 29);
				} else {
					// Set colour to default square colour.
					internalColour = new Color(57, 104, 37);
				}
				g.setColor(internalColour);
				g.fillRect(currentX, currentY, squareSize, squareSize);
				g.setColor(borderColour);
				g.drawRect(currentX, currentY, squareSize, squareSize);
				
				// Draws the counter.
				if (this.board[row][col] != GameState.COUNTER_EMPTY) {				
					if (this.board[row][col] == GameState.COUNTER_LIGHT) {
						g.setColor(Color.WHITE);
						g.fillOval(currentX + 2, currentY + 2, squareSize - 4, squareSize - 4);
						g.setColor(Color.BLACK);
						g.drawOval(currentX + 2, currentY + 2, squareSize - 4, squareSize - 4);
					} else {
						g.setColor(Color.BLACK);
						g.fillOval(currentX + 2, currentY + 2, squareSize - 4, squareSize - 4);
					}
				}
				
				// Moves to the next square.
				currentX += squareSize;
			}
			
			// Moves to the next row.
			currentY += squareSize;
			currentX = gridX;
			
		}
		
		// Additional visuals.
		if (this.dimensions[0] % 4 == 0) {
			int DOT_SIZE = 6;
			g.setColor(Color.BLACK);
			g.fillOval(gridX + squareSize*(this.dimensions[0] / 4) - DOT_SIZE/2, gridY + squareSize*(this.dimensions[1] / 4) - DOT_SIZE/2, DOT_SIZE, DOT_SIZE);
			g.fillOval(gridX + squareSize*(3*this.dimensions[0] / 4) - DOT_SIZE/2, gridY + squareSize*(this.dimensions[1] / 4) - DOT_SIZE/2, DOT_SIZE, DOT_SIZE);
			g.fillOval(gridX + squareSize*(this.dimensions[0] / 4) - DOT_SIZE/2, gridY + squareSize*(3*this.dimensions[1] / 4) - DOT_SIZE/2, DOT_SIZE, DOT_SIZE);
			g.fillOval(gridX + squareSize*(3*this.dimensions[0] / 4) - DOT_SIZE/2, gridY + squareSize*(3*this.dimensions[1] / 4) - DOT_SIZE/2, DOT_SIZE, DOT_SIZE);
		}
		int DIST_FROM_EDGE = 40;
		int BIG_OVAL_RADIUS = 50;
		int BIG_OVAL_HEIGHT = 160;
		g.setColor(Color.WHITE);
		g.fillOval(PANEL_WIDTH - DIST_FROM_EDGE - (2 * BIG_OVAL_RADIUS), BIG_OVAL_HEIGHT, 2 * BIG_OVAL_RADIUS, 2 * BIG_OVAL_RADIUS);		
		g.setColor(Color.BLACK);
		g.fillOval(DIST_FROM_EDGE, BIG_OVAL_HEIGHT, 2 * BIG_OVAL_RADIUS, 2 * BIG_OVAL_RADIUS);
		g.drawOval(PANEL_WIDTH - DIST_FROM_EDGE - (2 * BIG_OVAL_RADIUS), BIG_OVAL_HEIGHT, 2 * BIG_OVAL_RADIUS, 2 * BIG_OVAL_RADIUS);
		g.setColor(Color.BLACK);
		if (this.playerPlaying == 1) {
			int[] triX = {DIST_FROM_EDGE + BIG_OVAL_RADIUS - 10, DIST_FROM_EDGE + BIG_OVAL_RADIUS, DIST_FROM_EDGE + BIG_OVAL_RADIUS + 10};
			int[] triY = {BIG_OVAL_HEIGHT - 70, BIG_OVAL_HEIGHT - 50, BIG_OVAL_HEIGHT - 70};
			g.drawPolygon(triX, triY, 3);
			g.fillPolygon(triX, triY, 3);
		} else if (this.playerPlaying == 2) {
			int[] triX = {PANEL_WIDTH - DIST_FROM_EDGE - BIG_OVAL_RADIUS - 10, PANEL_WIDTH - DIST_FROM_EDGE - BIG_OVAL_RADIUS, PANEL_WIDTH - DIST_FROM_EDGE - BIG_OVAL_RADIUS + 10};
			int[] triY = {BIG_OVAL_HEIGHT - 70, BIG_OVAL_HEIGHT - 50, BIG_OVAL_HEIGHT - 70};
			g.drawPolygon(triX, triY, 3);
			g.fillPolygon(triX, triY, 3);
		}
		String displayString = "";
		g.setColor(Color.BLACK);
		g.drawString("Player 1", DIST_FROM_EDGE + (BIG_OVAL_RADIUS / 2) + 4, BIG_OVAL_HEIGHT - 20);
		if (players[0] != null) {
			displayString = players[0].getPlayerType();
		}
		g.drawString(displayString, DIST_FROM_EDGE + (BIG_OVAL_RADIUS / 2) - (int) (displayString.length() * 1.5), BIG_OVAL_HEIGHT + (2 * BIG_OVAL_RADIUS) + 20);
		displayString = "";
		g.drawString("Player 2", PANEL_WIDTH - DIST_FROM_EDGE - (3 * BIG_OVAL_RADIUS / 2) + 4, BIG_OVAL_HEIGHT - 20);
		if (players[1] != null) {
			displayString = players[1].getPlayerType();
		}
		g.drawString(displayString, PANEL_WIDTH - DIST_FROM_EDGE - (3 * BIG_OVAL_RADIUS / 2) - (int) (displayString.length() * 1.5), BIG_OVAL_HEIGHT + (2 * BIG_OVAL_RADIUS) + 20);
	
	}

	/**
	 * MouseListener method that allows for obtaining Human player moves.
	 */
	public void mouseClicked(MouseEvent e) {this.lastMousePos = e.getPoint();}

	public void mousePressed(MouseEvent e) {/* Do nothing. */}

	public void mouseReleased(MouseEvent e) {/* Do nothing. */}

	public void mouseEntered(MouseEvent e) {/* Do nothing. */}

	public void mouseExited(MouseEvent e) {/* Do nothing. */}

}
