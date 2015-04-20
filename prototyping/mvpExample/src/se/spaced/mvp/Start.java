package se.spaced.mvp;

import se.spaced.mvp.presenter.MainPresenter;
import se.spaced.mvp.presenter.impl.MainPresenterImpl;
import se.spaced.mvp.view.MainView;
import se.spaced.mvp.view.impl.ClassTreeViewImpl;
import se.spaced.mvp.view.impl.EditViewImpl;
import se.spaced.mvp.view.impl.MainMenuViewImpl;
import se.spaced.mvp.view.impl.MainToolBarViewImpl;
import se.spaced.mvp.view.impl.MainViewImpl;
import se.spaced.mvp.view.impl.PropertyListViewImpl;

import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

public class Start {

	public static void main(String[] args) throws UnsupportedLookAndFeelException {
		final MainMenuViewImpl mainMenuView = new MainMenuViewImpl();
		final MainToolBarViewImpl mainToolBarView = new MainToolBarViewImpl();
		final EditViewImpl editView = new EditViewImpl();
		final ClassTreeViewImpl classTreeView = new ClassTreeViewImpl();
		final PropertyListViewImpl propertyListView = new PropertyListViewImpl();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				MainView mainView = new MainViewImpl(mainMenuView, mainToolBarView, editView, classTreeView, propertyListView);

				final MainPresenter mainPresenter = new MainPresenterImpl(mainView);


				mainPresenter.show();
			}
		});


	}
}
