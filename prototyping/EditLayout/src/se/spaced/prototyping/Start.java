package se.spaced.prototyping;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;

public class Start {

	public static void main(String[] args){
		JPanel drawingPanel = new JPanel();
		drawingPanel.setEnabled(true);
		drawingPanel.setVisible(true);
		drawingPanel.setBackground(Color.BLUE);
		drawingPanel.setPreferredSize(new Dimension(640,480));

		JPanel toolBar = new JPanel();
		toolBar.setEnabled(true);
		toolBar.setVisible(true);
		toolBar.setBackground(Color.RED);
		JButton b1 = new JButton("OPEN");
		toolBar.add(b1);
		MainFrame mainFrame = new MainFrame(drawingPanel, toolBar);
		
	}
}
