package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import game.GameState;
import players.Player;

/**
 * A UI panel for displaying additional information about the game.
 */
public class InfoPanel extends JPanel {
	
	private String info;
	
	public static final int PANEL_WIDTH = OthelloFrame.FRAME_WIDTH;
	public static final int PANEL_HEIGHT = 40;

	private static final long serialVersionUID = 1895792502596588154L;

	/**
	 * Default constructor.
	 */
	public InfoPanel() {
		this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		this.setBackground(Color.WHITE);
		this.info = "";
	}
	
	/**
	 * Method that caches information about the game for use by the drawing method.
	 */
	public void updateUI(GameState game, Player playerToPlay) {
		if (game.isOver()) {
			int[] scores = {game.getScoreOfID(GameState.COUNTER_DARK), game.getScoreOfID(GameState.COUNTER_LIGHT)};
			if (scores[0] > scores[1]) {
				this.info = "The game is over. The winning player is Player 1.";
			} else if (scores[0] < scores[1]) {
				this.info = "The game is over. The winning player is Player 2.";
			} else {
				this.info = "The game is over. The two players have drawn.";
			}
		} else {
			if (playerToPlay.getPlayerType().equals("Human")) {
				this.info = "Player " + playerToPlay.getPlayerID() + ", it is your turn.";
			} else {
				this.info = "Player " + playerToPlay.getPlayerID() + " is considering what move to play...";
			}
			if (!game.hasLegalMoves(playerToPlay)) {
				this.info = "It is Player " + playerToPlay.getPlayerID() + "'s turn, but they have no moves they can play. Their turn will be skipped.";
			}
		}
	}

	/*
	 * Allows for the information string for the panel to be manually set.
	 */
	public void setInfo(String s) {this.info = s;}

	/**
	 * Method that paints the panel with the necessary graphics.
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.drawRect(-1,0,PANEL_WIDTH-1,PANEL_HEIGHT-1);
		g.drawString(this.info, PANEL_WIDTH/2-(this.info.length() * 2), PANEL_HEIGHT/2);
	}
	
}
