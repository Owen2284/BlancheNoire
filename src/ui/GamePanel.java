package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

/*
 * A UI panel for displaying the current game state.
 */
public class GamePanel extends JPanel implements MouseListener {
	
	public static final int PANEL_WIDTH = OthelloFrame.FRAME_WIDTH;
	public static final int PANEL_HEIGHT = 480;

	private static final long serialVersionUID = -5194744397408657473L;

	public GamePanel() {
		
		// Performs Swing-related setup.
		this.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		this.setBackground(Color.GREEN);
		
		// Adds a mouse listener to the panel.
		this.addMouseListener(this);
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawString("GAME", PANEL_WIDTH, PANEL_HEIGHT);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
