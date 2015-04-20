package se.spaced.spacedit.ui.view.frame;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import net.infonode.docking.RootWindow;
import net.infonode.docking.SplitWindow;
import net.infonode.docking.theme.DockingWindowsTheme;
import net.infonode.docking.theme.ShapedGradientDockingTheme;
import net.infonode.docking.util.DockingUtil;
import net.infonode.docking.util.ViewMap;
import net.infonode.util.Direction;
import se.spaced.spacedit.ui.tdi.TdiChildToolbar;
import se.spaced.spacedit.ui.tdi.TdiChildWindow;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

/**
 * The main view is a JFrame with a split pane.
 */
public class MainViewSwingImpl extends JFrame implements MainView {
	private se.fearlessgames.common.ui.Action quitButtonAction;
	private final TdiChildWindow displayView;
	private final TdiChildToolbar toolBarView;
	private RootWindow rootWindow;
	private final TdiChildWindow classTreeView;
	private final TdiChildWindow propertyView;

	@Inject
	public MainViewSwingImpl(@Named("displayTdiChildWindow") final TdiChildWindow displayView, @Named("mainToolbar") final TdiChildToolbar toolBarView, @Named("classTreeTdiChildWindow") final TdiChildWindow classTreeView, @Named("propertyEditorTdiChildWindow") final TdiChildWindow propertyView) {
		this.displayView = displayView;
		this.toolBarView = toolBarView;
		this.classTreeView = classTreeView;
		this.propertyView = propertyView;
		this.setTitle("Spaceditor");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				if (quitButtonAction != null) {
					quitButtonAction.act();
				}
			}
		});

		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					buildGui();
				}
			});
		} catch (InterruptedException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void buildGui() {
		setLayout(new BorderLayout());
		buildRootWindow();
		buildDefaultLayout();
		buildFrame();
	}


	private void buildRootWindow() {
		ViewMap viewMap = new ViewMap();
		viewMap.addView(0, displayView.getTdiView());
		viewMap.addView(1, classTreeView.getTdiView());
		viewMap.addView(2, propertyView.getTdiView());

		rootWindow = DockingUtil.createRootWindow(viewMap, true);
		rootWindow.getWindowBar(Direction.DOWN).setEnabled(true);
		rootWindow.getRootWindowProperties().getTabWindowProperties().getCloseButtonProperties().setVisible(false);


		DockingWindowsTheme theme = new ShapedGradientDockingTheme();
		rootWindow.getRootWindowProperties().addSuperObject(theme.getRootWindowProperties());

	}

	private void buildDefaultLayout() {
		SplitWindow leftColumnSplit = new SplitWindow(false, propertyView.getTdiView(), classTreeView.getTdiView());
		SplitWindow mainSplit = new SplitWindow(true, 0.3f, leftColumnSplit, displayView.getTdiView());
		rootWindow.setWindow(mainSplit);

	}

	private void buildFrame() {
		getContentPane().add(toolBarView.getToolBar(), BorderLayout.NORTH);
		getContentPane().add(rootWindow, BorderLayout.CENTER);
		setSize(1024, 768);
	}

	@Override
	public void setQuitButtonAction(se.fearlessgames.common.ui.Action quitButtonAction) {
		this.quitButtonAction = quitButtonAction;
	}

	@Override
	public void start() throws Exception {
		//pack();
		setVisible(true);
	}

	@Override
	public void shutDown() {

	}

}
