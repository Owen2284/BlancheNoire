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

	/**
	 * Constructor that takes an initial game state to allow for the interface to be drawn.
	 */
	public OthelloFrame(GameState game) throws HeadlessException {
		
		// Initial setup.
		this.setLayout(new BorderLayout());
		this.setTitle("BlancheNoire");
		this.setBackground(Color.BLACK);
		this.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Adding panels.
        this.top = new ScorePanel();
        this.top.updateUI(game);
        this.middle = new GamePanel();
        this.middle.updateUI(game, game.getPlayerByID(GameState.COUNTER_DARK));
        this.bottom = new InfoPanel();
        this.bottom.updateUI(game, game.getPlayerByID(GameState.COUNTER_DARK));
        this.bottom.setInfo("Initialising...");
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
