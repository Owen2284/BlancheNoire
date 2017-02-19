package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import game.GameState;

/*
 * A Swing-based class that aggregates the three Panels that make up the Othello
 * interface into one Frame.
 */
public class OthelloFrame extends JFrame implements ActionListener {
	
	private ScorePanel top;
	private GamePanel middle;
	private InfoPanel bottom;
	
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
		this.setLocationRelativeTo(null);
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
		this.setVisible(true);
	}
	
	/*
	 * Method that changes the interface to reflect the game state.
	 */
	public void updateUI(GameState game) {
		this.top.updateUI(game);
		this.middle.updateUI(game);
		this.bottom.updateUI(game);
		this.top.repaint();
		this.middle.repaint();
		this.bottom.repaint();
		this.repaint();
	}
	
	/*
	 * Method called when an action is detected which repaints the
	 * components using their cached information.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.top.repaint();
		this.middle.repaint();
		this.bottom.repaint();
		this.repaint();
	}
	

	

}
