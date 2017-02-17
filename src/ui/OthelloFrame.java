package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;

import javax.swing.JFrame;

/*
 * A Swing-based class that aggregates the three Panels that make up the Othello
 * interface into one Frame.
 */
public class OthelloFrame extends JFrame {
	
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
        this.add(new ScorePanel(), BorderLayout.NORTH);
        this.add(new GamePanel(), BorderLayout.CENTER);
        this.add(new InfoPanel(), BorderLayout.SOUTH);
        this.pack();
		
		// Reveal the frame.
		this.setVisible(true);
	}

}
