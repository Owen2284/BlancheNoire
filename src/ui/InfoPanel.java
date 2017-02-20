package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import game.GameState;
import players.Player;

/*
 * A UI panel for displaying additional information about the game.
 */
public class InfoPanel extends JPanel implements ActionListener {
	
	private String info;
	
	public static final int PANEL_WIDTH = OthelloFrame.FRAME_WIDTH;
	public static final int PANEL_HEIGHT = 40;

	private static final long serialVersionUID = 1895792502596588154L;

	public InfoPanel() {
		this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		this.setBackground(Color.WHITE);
		this.info = "";
	}
	
	public void updateUI(GameState game, Player playerToPlay) {
		if (game.isOver()) {
			int[] scores = {game.getScore(1), game.getScore(2)};
			if (scores[0] > scores[1]) {
				this.info = "The game is over. The winning player is Player 1.";
			} else if (scores[0] < scores[1]) {
				this.info = "The game is over. The winning player is Player 2.";
			} else {
				this.info = "The game is over. The two players have drawn.";
			}
		} else {
			this.info = "It is Player " + playerToPlay.getPlayerID() + "'s turn.";
			if (!game.hasLegalMoves(playerToPlay)) {
				this.info = this.info.substring(0, this.info.length() - 1);
				this.info += ", but has no moves to play.";
			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.drawRect(-1,0,PANEL_WIDTH-1,PANEL_HEIGHT-1);
		g.drawString(this.info, PANEL_WIDTH/2-(this.info.length() * 2), PANEL_HEIGHT/2);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.print("I");
	}

}
