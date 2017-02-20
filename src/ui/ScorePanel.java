package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import game.GameState;

/*
 * A UI panel for displaying the game scores.
 */
public class ScorePanel extends JPanel implements ActionListener {
	
	private int[] scores;
	
	public static final int PANEL_WIDTH = OthelloFrame.FRAME_WIDTH;
	public static final int PANEL_HEIGHT = 80;

	private static final long serialVersionUID = -3281906619038828564L;

	public ScorePanel() {
		this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		this.setBackground(Color.WHITE);
		this.scores = new int[2];
		this.scores[0] = 0;
		this.scores[1] = 0;
	}
	
	public void updateUI(GameState game) {
		this.scores[0] = game.getScoreOfPlayer(0);
		this.scores[1] = game.getScoreOfPlayer(1);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.drawRect(-1,0,PANEL_WIDTH-1,PANEL_HEIGHT-1);
		String fontName = g.getFont().getFontName();
		g.drawString("Player 1", PANEL_WIDTH/2-95, PANEL_HEIGHT/2 + 25);
		g.drawString("Player 2", PANEL_WIDTH/2+50, PANEL_HEIGHT/2 + 25);
		g.drawString("VS", PANEL_WIDTH/2 - 8, PANEL_HEIGHT/2 + 10);
		g.setFont(new Font(fontName, 0, 32));
		if (this.scores[0] >= 10) {g.drawString(Integer.toString(this.scores[0]), PANEL_WIDTH/2-95, PANEL_HEIGHT/2 + 5);}
		else {g.drawString(Integer.toString(this.scores[0]), PANEL_WIDTH/2-85, PANEL_HEIGHT/2 + 5);}
		if (this.scores[1] >= 10) {g.drawString(Integer.toString(this.scores[1]), PANEL_WIDTH/2+50, PANEL_HEIGHT/2 + 5);}
		else {g.drawString(Integer.toString(this.scores[1]), PANEL_WIDTH/2+60, PANEL_HEIGHT/2 + 5);}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.print("S");
	}

}
