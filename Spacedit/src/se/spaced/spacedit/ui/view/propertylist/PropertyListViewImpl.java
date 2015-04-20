package se.spaced.spacedit.ui.view.propertylist;

import com.google.inject.Singleton;
import net.infonode.docking.View;
import se.spaced.spacedit.ui.tdi.TdiChildWindow;
import se.spaced.spacedit.ui.view.utils.swing.SwingThread;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.Container;
import java.awt.GridLayout;

@Singleton
public class PropertyListViewImpl extends JPanel implements PropertyListView, TdiChildWindow {
	private final View view;

	public PropertyListViewImpl() {
		super(new GridLayout(1, 0));
		view = new View("Props", null, this);
		view.getWindowProperties().setCloseEnabled(false);
	}

	@Override
	public View getTdiView() {
		return view;
	}

	@SwingThread
	@Override
	public void changePropertyList(PropertyListBuilder propertyListBuilder) {
		this.removeAll();

		Container container = propertyListBuilder.build();
		add(new JScrollPane(container));
		this.updateUI();
	}
}