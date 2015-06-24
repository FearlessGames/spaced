package se.ardorgui.components.scroll;

public class ScrollBarData {
	private int value;
	private int max;
	private int size;

	public ScrollBarData(int value, int max, int size) {
		this.value = value;
		this.max = max;
		this.size = size;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public float getSizePercentage() {
		return (float)size / (float)max;
	}

	public float getValuePercentage() {
		return (float)value / (float)(max - 1);
	}

	public void setValuePercentage(float valuePercentage) {
		value = Math.round(valuePercentage * (max - 1));
	}
}