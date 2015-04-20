package se.spaced.server.model;

import se.ardortech.math.SpacedVector3;
import se.spaced.server.persistence.dao.impl.PersistableBase;
import se.spaced.shared.model.AppearanceData;

import javax.persistence.Entity;

@Entity
public class PersistedAppearanceData extends PersistableBase {
	private String modelName;
	private String portraitName;
	private SpacedVector3 scale = new SpacedVector3(1, 1, 1);

	public PersistedAppearanceData() {
		modelName = "noModelName";
		portraitName = "noPortraitName";
	}

	public PersistedAppearanceData(String modelName, String portraitName) {
		this.modelName = modelName;
		this.portraitName = portraitName;
	}

	public PersistedAppearanceData(String modelName, String portraitName, SpacedVector3 scale) {
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
		return "PersistedAppearanceData{" +
				"modelName='" + modelName + '\'' +
				", portraitName='" + portraitName + '\'' +
				", scale=" + scale +
				'}';
	}

	public AppearanceData asSharedAppearanceData() {
		return new AppearanceData(modelName, portraitName, scale);
	}
}
