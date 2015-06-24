package se.spaced.client.net.smrt;

import com.google.inject.Inject;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.SpacedInventory;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.Spell;
import se.spaced.messages.protocol.c2s.remote.C2SAbstractRequiredWriteCodecImpl;
import se.spaced.messages.protocol.c2s.remote.C2SRequiredWriteCodec;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.Gender;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.network.protocol.codec.SharedCodec;
import se.spaced.shared.playback.RecordingPoint;

import java.io.IOException;
import java.io.OutputStream;

public class ClientToServerWriteCodec extends C2SAbstractRequiredWriteCodecImpl implements C2SRequiredWriteCodec {
	private final SharedCodec sharedCodec;

	@Inject
	public ClientToServerWriteCodec(SharedCodec sharedCodec) {
		this.sharedCodec = sharedCodec;
	}

	@Override
	public void writeCooldown(OutputStream output, se.spaced.messages.protocol.Cooldown cooldown) throws IOException {
		sharedCodec.writeUUID(this, output, cooldown.getPk());

	}

	@Override
	public void writeEntity(OutputStream output, Entity entity) throws IOException {
		UUID uuid = UUID.ZERO;
		if (entity != null) {
			uuid = entity.getPk();
		}
		sharedCodec.writeUUID(this, output, uuid);
	}

	@Override
	public void writeSpacedInventory(OutputStream output, SpacedInventory inventory) throws IOException {
		writeUUID(output, inventory.getPk());
	}

	@Override
	public void writeGender(OutputStream output, Gender gender) throws IOException {
		writeByte(output, (byte) gender.ordinal());
	}

	@Override
	public void writeSpacedItem(OutputStream output, SpacedItem spacedItem) throws IOException {
		sharedCodec.writeUUID(this, output, spacedItem.getPk());
	}

	@Override
	public void writeSpell(OutputStream output, Spell spell) throws IOException {
		writeUUID(output, spell.getPk());
	}

	@Override
	public void writeAnimationState(OutputStream output, AnimationState animationState) throws IOException {
		sharedCodec.writeAnimationState(this, output, animationState);
	}

	@Override
	public void writeContainerType(OutputStream output, ContainerType containerType) throws IOException {
		sharedCodec.writeContainerType(this, output, containerType);
	}

	@Override
	public void writeRecordingPointOfAnimationState(
			OutputStream output, RecordingPoint<AnimationState> recordingPoint) throws IOException {
		sharedCodec.writeRecordingPoint(this, output, recordingPoint);
	}

	@Override
	public void writeUUID(OutputStream output, UUID uuid) throws IOException {
		sharedCodec.writeUUID(this, output, uuid);
	}
}
