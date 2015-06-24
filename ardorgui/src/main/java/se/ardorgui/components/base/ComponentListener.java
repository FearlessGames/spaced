package se.ardorgui.components.base;

public interface ComponentListener {
	void onHide(Component component);
	void onShow(Component component);
	void onEnable(Component component);
	void onDisable(Component component);
	void onMove(Component component);
	void onResize(Component component);
	void onChangeFade(Component component);
	void onChangeColor(Component component);
	void onReleaseResources(Component component);
}