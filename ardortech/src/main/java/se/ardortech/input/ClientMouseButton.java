package se.ardortech.input;

public enum ClientMouseButton {
	LEFT(0),
	RIGHT(1),
	UNUSED(-1);

	private final int id;

	ClientMouseButton(int id) {
		this.id = id;
	}

	public static ClientMouseButton fromId(int fromId) {
		for (ClientMouseButton mouseButton : values()) {
			if (mouseButton.id == fromId) {
				return mouseButton;
			}
		}
		return ClientMouseButton.UNUSED;
	}

	public int getId() {
		return id;
	}
}
