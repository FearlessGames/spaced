package se.spaced.spacedit.ui.presenter.display;

public interface ArdorSceneHandler {
	public void moveUp();

	public void moveDown();

	public void moveRight();

	public void moveLeft();

	public void moveIn();

	public void moveOut();

	void scaleTo(double x, double y, double z);
}
