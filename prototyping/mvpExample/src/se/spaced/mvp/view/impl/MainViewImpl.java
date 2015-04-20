package se.spaced.mvp.view.impl;

import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;
import se.spaced.mvp.view.MainView;
import se.spaced.mvp.view.tdi.TdiChildMenu;
import se.spaced.mvp.view.tdi.TdiChildToolbar;
import se.spaced.mvp.view.tdi.TdiChildWindow;

import javax.swing.JFrame;
import java.awt.BorderLayout;

public class MainViewImpl extends JFrame implements MainView {
	private RootWindow rootWindow;
	private TdiChildMenu mainMenu;
	private TdiChildToolbar mainToolbar;
	private TdiChildWindow editView;
	private TdiChildWindow classTreeView;
	private TdiChildWindow propertyListView;


	public MainViewImpl(TdiChildMenu mainMenu, TdiChildToolbar mainToolbar, TdiChildWindow editView, TdiChildWindow classTreeView, TdiChildWindow propertyListView) {
		this.mainMenu = mainMenu;
		this.mainToolbar = mainToolbar;
		this.editView = editView;
		this.classTreeView = classTreeView;
		this.propertyListView = propertyListView;

		buildRootWindow();
		buildDefaultLayout();

		buildFrame();


		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //fugly! should be in some presenter and system.exit instead! need to persist and other stuff, not just close
	}


	private void buildRootWindow() {
		ViewMap viewMap = new ViewMap();
		viewMap.addView(0, editView.getTdiView());
		viewMap.addView(1, classTreeView.getTdiView());
		viewMap.addView(2, propertyListView.getTdiView());

		rootWindow = DockingUtil.createRootWindow(viewMap, true);
		rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getCloseButtonProperties().setVisible(false);


		DockingWindowsTheme theme = new ShapedGradientDockingTheme();
		rootWindow.getRootWindowProperties().addSuperObject(theme.getRootWindowProperties());

	}

	private void buildDefaultLayout() {
		SplitWindow leftColumnSplit = new SplitWindow(false, classTreeView.getTdiView(), propertyListView.getTdiView());
		SplitWindow mainSplit = new SplitWindow(true, 0.3f, leftColumnSplit, editView.getTdiView());

		rootWindow.setWindow(mainSplit);

	}

	private void buildFrame() {
		getContentPane().add(mainToolbar.getToolBar(), BorderLayout.NORTH);
		getContentPane().add(rootWindow, BorderLayout.CENTER);
		setJMenuBar(mainMenu.getMenuBar());
		setSize(900, 700);
	}

	@Override
	public void showView() {
		setVisible(true);
	}
}
