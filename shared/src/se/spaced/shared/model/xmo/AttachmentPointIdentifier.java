package se.spaced.shared.model.xmo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value = "attachmentpointidentifier")
public enum AttachmentPointIdentifier {
	SKIN_FEET,
	SKIN_LEGS,
	SKIN_CHEST,
	BACK,
	VEHICLE,
	HEAD,
	RIGHT_HAND,
	RIGHT_WRIST,	
	LEFT_HAND,
	LEFT_WRIST
}
