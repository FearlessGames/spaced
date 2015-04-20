package se.spaced.mvp.presenter.impl;

import se.spaced.mvp.presenter.MainPresenter;
import se.spaced.mvp.view.MainView;

public class MainPresenterImpl implements MainPresenter {
	private MainView mainView;

	public MainPresenterImpl(MainView mainView) {
		this.mainView = mainView;
	}


	@Override
	public void show() {
		mainView.showView();
	}
}
