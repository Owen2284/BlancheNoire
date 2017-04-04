package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;

import javax.swing.JFrame;

import game.GameState;
import players.Player;

/**
 * A Swing-based class that aggregates the three Panels that make up the Othello
 * interface into one Frame.
 */
public class OthelloFrame extends JFrame {
	
	public ScorePanel top;
	public GamePanel middle;
	public InfoPanel bottom;
	
	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 600;

	private static final long serialVersionUID = -7230594703974179925L;

	public OthelloFrame() throws HeadlessException {
		
		// Initial setup.
		this.setLayout(new BorderLayout());
		this.setTitle("Othello");
		this.setBackground(Color.BLACK);
		this.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Adding panels.
        this.top = new ScorePanel();
        this.middle = new GamePanel();
        this.bottom = new InfoPanel();
        this.add(this.top, BorderLayout.NORTH);
        this.add(this.middle, BorderLayout.CENTER);
        this.add(this.bottom, BorderLayout.SOUTH);
        this.pack();
		
		// Reveal the frame.
        this.setLocationRelativeTo(null);
		this.setVisible(true);
		
	}
	
	/**
	 * Method that changes the interface to reflect the game state.
	 * @param game - the current GameState of the Othello game.
	 * @param playerToPlay - The player who is currently taking their turn.
	 */
	public void updateUI(GameState game, Player playerToPlay) {
		this.top.updateUI(game);
		this.middle.updateUI(game, playerToPlay);
		this.bottom.updateUI(game, playerToPlay);
		this.top.repaint();
		this.middle.repaint();
		this.bottom.repaint();
		this.repaint();
	}

}
