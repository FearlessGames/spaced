package se.spaced.server.trade;

import org.junit.Before;
import org.junit.Test;
import se.fearless.common.time.MockTimeProvider;
import se.fearless.common.time.TimeProvider;
import se.fearless.common.uuid.UUIDMockFactory;
import se.mockachino.Mockachino;
import se.mockachino.Settings;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.server.net.broadcast.SmrtBroadcasterImpl;

import static se.mockachino.Mockachino.mock;
import static se.mockachino.Mockachino.verifyOnce;

public class SmrtTradeCallbackTest {
	private SmrtTradeCallback tradeCallback;

	private ServerEntity initiator;
	private ServerEntity collaborator;
	private SmrtBroadcaster<S2CProtocol> broadcaster;

	@Before
	public void setUp() throws Exception {
		TimeProvider timeProvider = new MockTimeProvider();
		PlayerMockFactory playerMockFactory = new PlayerMockFactory.Builder(timeProvider, new UUIDMockFactory()).build();
		initiator = playerMockFactory.createPlayer("Mr Initiator");
		collaborator = playerMockFactory.createPlayer("Mr Collaborator");
		broadcaster = mock(SmrtBroadcasterImpl.class, Settings.fallback(Mockachino.DEEP_MOCK));
		tradeCallback = new SmrtTradeCallback(broadcaster, mock(TradeExecutor.class));
	}

	@Test
	public void testInitiated() throws Exception {
		String checksum = "0xCheckSum";
		tradeCallback.initiated(initiator, collaborator, checksum);
		verifyOnce().on(broadcaster.create().to(initiator).send().trade()).tradeInitiated(collaborator, checksum);
		verifyOnce().on(broadcaster.create().to(collaborator).send().trade()).tradeInitiated(initiator, checksum);
	}

	@Test
	public void testInitiatorAccepted() throws Exception {
		tradeCallback.initiatorAccepted(initiator, collaborator);
		verifyOnce().on(broadcaster.create().to(collaborator).send().trade()).accepted(initiator);
	}

	@Test
	public void testCollaboratorAccepted() throws Exception {
		tradeCallback.collaboratorAccepted(initiator, collaborator);
		verifyOnce().on(broadcaster.create().to(initiator).send().trade()).accepted(collaborator);

	}

	@Test
	public void testItemAdded() throws Exception {
		String checksum = "0xCheckSum";
		ServerItem item = mock(ServerItem.class);
		tradeCallback.itemAdded(initiator, collaborator, item, checksum);
		verifyOnce().on(broadcaster.create().to(initiator).to(collaborator).send().trade()).itemAdded(initiator,
				item,
				checksum);
	}

	@Test
	public void testNegotiating() throws Exception {
		tradeCallback.negotiating(initiator, collaborator);
		verifyOnce().on(broadcaster.create().to(initiator).to(collaborator).send().trade()).negotiating();
	}

	@Test
	public void testCollaboratorClosed() throws Exception {
		tradeCallback.collaboratorRejected(initiator, collaborator);
		verifyOnce().on(broadcaster.create().to(initiator).send().trade()).rejected(collaborator);
	}

	@Test
	public void testInitiatorClosed() throws Exception {
		tradeCallback.initiatorRejected(initiator, collaborator);
		verifyOnce().on(broadcaster.create().to(collaborator).send().trade()).rejected(initiator);
	}

	@Test
	public void testAborted() throws Exception {
		tradeCallback.aborted(initiator, collaborator);
		verifyOnce().on(broadcaster.create().to(initiator).send().trade()).aborted(collaborator);
		verifyOnce().on(broadcaster.create().to(collaborator).send().trade()).aborted(initiator);
	}
}
