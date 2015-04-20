package se.spaced.shared.model.xmo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import se.ardortech.math.SpacedVector3;

@XStreamAlias(value = "metanode")
public class XmoMetaNode implements MetaNode {
	private final SpacedVector3 position;
	private final SpacedVector3 size;
	private final SpacedVector3 rotation;

	@XStreamOmitField
	public static final MetaNode NULL = new MetaNode() {
		@Override
		public SpacedVector3 getPosition() {
			return SpacedVector3.ZERO;
		}

		@Override
		public SpacedVector3 getSize() {
			return SpacedVector3.ZERO;
		}

		@Override
		public SpacedVector3 getRotation() {
			return SpacedVector3.ZERO;
		}
	};

	// TODO: Remake constructor/fields to be ReadOnlyVector3, as XStream can't deserialize that, we have to create a XStream Converter for it too
	public XmoMetaNode(SpacedVector3 position, SpacedVector3 size, SpacedVector3 rotation) {
		this.position = position;
		this.size = size;
		this.rotation = rotation;
	}

	@Override
	public SpacedVector3 getPosition() {
		return position;
	}

	@Override
	public SpacedVector3 getSize() {
		return size;
	}

	@Override
	public SpacedVector3 getRotation() {
		return rotation;
	}
}
