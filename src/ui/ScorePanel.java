package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import game.GameState;

/*
 * A UI panel for displaying the game scores.
 */
public class ScorePanel extends JPanel {
	
	private String info;
	
	public static final int PANEL_WIDTH = OthelloFrame.FRAME_WIDTH;
	public static final int PANEL_HEIGHT = 80;

	private static final long serialVersionUID = -3281906619038828564L;

	public ScorePanel() {
		this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		this.setBackground(Color.WHITE);
		this.info = "";
	}
	
	public void updateUI(GameState game) {
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.drawString("SCORE", PANEL_WIDTH, PANEL_HEIGHT);
	}

}
