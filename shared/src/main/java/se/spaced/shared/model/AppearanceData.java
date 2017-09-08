package se.spaced.shared.model;

import se.ardortech.math.SpacedVector3;

public class AppearanceData {
	private final String modelName;
	private final String portraitName;
	private final SpacedVector3 scale;
	private static final String NO_MODEL = "NoModel";
	private static final String NO_PORTRAIT = "NoPortrait";

	public AppearanceData() {
		modelName = NO_MODEL;
		portraitName = NO_PORTRAIT;
		scale = new SpacedVector3(1, 1, 1);
	}

	public AppearanceData(String modelName, String portraitName) {
		this.modelName = modelName;
		this.portraitName = portraitName;
		scale = new SpacedVector3(1, 1, 1);
	}

	public AppearanceData(String modelName, String portraitName, SpacedVector3 scale) {
		this.modelName = modelName;
		this.portraitName = portraitName;
		this.scale = scale;
	}

	public String getModelName() {
		return modelName;
	}

	public String getPortraitName() {
		return portraitName;
	}

	public SpacedVector3 getScale() {
		return scale;
	}

	@Override
	public String toString() {
		return "AppearanceData{" +
				"modelName='" + modelName + '\'' +
				", portraitName='" + portraitName + '\'' +
				", scale=" + scale +
				'}';
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((modelName == null) ? 0 : modelName.hashCode());
		result = prime * result + ((portraitName == null) ? 0 : portraitName.hashCode());
		result = prime * result + ((scale == null) ? 0 : scale.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!getClass().isInstance(obj)) {
			return false;
		}

		AppearanceData equippedSlot = (AppearanceData) obj;

		if (!modelName.equals(equippedSlot.modelName)) {
			return false;
		}

		if (!portraitName.equals(equippedSlot.portraitName)) {
			return false;
		}

		return scale.equals(equippedSlot.scale);

	}
}
