package se.spaced.client.model.control;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import se.spaced.client.net.smrt.ServerConnection;

@Singleton
public class GroundImpactListenerImpl implements GroundImpactListener {
	private final ServerConnection serverConnection;

	@Inject
	public GroundImpactListenerImpl(ServerConnection serverConnection) {
		this.serverConnection = serverConnection;
	}


	@Override
	public void notifyHit(double impactSpeed) {
		serverConnection.getReceiver().movement().hitGround((float) impactSpeed);
	}
}
