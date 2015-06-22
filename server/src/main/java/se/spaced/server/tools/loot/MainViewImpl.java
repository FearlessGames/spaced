package se.spaced.server.tools.loot;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

public class MainViewImpl extends JFrame implements MainView {
	private final JTabbedPane tabbedPane;
	private final JPanel mainPanel;

	public MainViewImpl() {
		super("Loot Creator");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		mainPanel = new JPanel(new GridLayout(1, 1));
		tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

	}

	@Override
	public void addTabPanel(String title, Component component) {
		tabbedPane.addTab(title, component);
	}

	@Override
	public void createAndShowUI() {
		mainPanel.add(tabbedPane);
		add(mainPanel, BorderLayout.CENTER);
		setSize(900, 600);
		setVisible(true);
	}
}
