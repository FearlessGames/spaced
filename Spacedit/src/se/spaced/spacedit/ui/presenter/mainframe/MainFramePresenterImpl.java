package se.spaced.spacedit.ui.presenter.mainframe;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.fearlessgames.common.ui.Action;
import se.spaced.spacedit.ui.presenter.display.ArdorPresenter;
import se.spaced.spacedit.ui.presenter.propertyeditor.PropertyListPresenter;
import se.spaced.spacedit.ui.presenter.toolbar.ToolBarPresenter;
import se.spaced.spacedit.ui.presenter.xmotree.XmoTreePresenter;
import se.spaced.spacedit.ui.view.frame.MainView;

@Singleton
public class MainFramePresenterImpl implements MainFramePresenter {
	private final MainView view;
	private final XmoTreePresenter xmoTreePresenter;
	private final ArdorPresenter ardorPresenter;
	private final ToolBarPresenter toolBarPresenter;
	private final PropertyListPresenter propertyListPresenter;
	private Action exitAction;

	/**
	 * Classes needs to be injected here to be initialized is that good?
	 *
	 * @param view
	 * @param xmoTreePresenter
	 * @param ardorPresenter
	 * @param toolBarPresenter
	 * @param propertyListPresenter
	 */
	@Inject
	public MainFramePresenterImpl(final MainView view, final XmoTreePresenter xmoTreePresenter, final ArdorPresenter ardorPresenter, final ToolBarPresenter toolBarPresenter, final PropertyListPresenter propertyListPresenter) {
		this.view = view;
		this.xmoTreePresenter = xmoTreePresenter;
		this.ardorPresenter = ardorPresenter;
		this.toolBarPresenter = toolBarPresenter;
		this.propertyListPresenter = propertyListPresenter;


		view.setQuitButtonAction(new Action() {
			@Override
			public void act() {
				view.shutDown();
				if (exitAction != null) {
					exitAction.act();
				}
			}
		});
	}

	@Override
	public void setExitAction(Action action) {
		this.exitAction = action;
	}

	@Override
	public void start() {
		try {
			view.start();
		} catch (Exception e) {
		}
	}
}
