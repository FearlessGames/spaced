package se.spaced.client.ardor.effect;

import com.ardor3d.math.type.ReadOnlyMatrix3;
import com.ardor3d.math.type.ReadOnlyTransform;
import com.ardor3d.math.type.ReadOnlyVector3;

public interface SpatialEffect extends Effect {
	void setVelocity(ReadOnlyVector3 velocity);

	void setRotation(ReadOnlyMatrix3 rotation);

	void setTranslation(ReadOnlyVector3 translation);

	void setTransform(ReadOnlyTransform transform);
}
