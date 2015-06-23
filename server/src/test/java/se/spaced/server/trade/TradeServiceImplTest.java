package se.spaced.server.trade;

import org.junit.Before;
import org.junit.Test;
import se.ardortech.math.SpacedRotation;
import se.ardortech.math.SpacedVector3;
import se.fearless.common.uuid.UUID;
import se.mockachino.matchers.matcher.ArgumentCatcher;
import se.spaced.server.ScenarioTestBase;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.shared.RangeConstants;
import se.spaced.shared.model.AnimationState;
import se.spaced.shared.model.items.ItemType;
import se.spaced.shared.playback.MovementPoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;
import static se.mockachino.matchers.Matchers.match;
import static se.mockachino.matchers.MatchersBase.mAny;

public class TradeServiceImplTest extends ScenarioTestBase {
	private ServerEntity initiator;
	private ServerEntity collaborator;
	private ServerItem toyCar;
	private ServerItem screw;

	@Before
	public void setUp() throws Exception {
		PlayerMockFactory playerMockFactory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		initiator = playerMockFactory.createPlayer("Mr Initiator");
		collaborator = playerMockFactory.createPlayer("Mr Collaborator");

		visibilityService.entityAdded(initiator);
		visibilityService.entityAdded(collaborator);

		ServerItemTemplate carTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Toy car",
				ItemType.JETPACK).build();
		ServerItemTemplate screwTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Rusty screw",
				ItemType.MAIN_HAND_ITEM).build();
		toyCar = carTemplate.create();
		toyCar.setPk(uuidFactory.combUUID());
		screw = screwTemplate.create();
		screw.setPk(uuidFactory.combUUID());
	}

	@Test
	public void testInitiateTrade() throws Exception {
		movementService.moveAndRotateEntity(initiator,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(0, 100, 100), SpacedRotation.IDENTITY));
		movementService.moveAndRotateEntity(collaborator,
				new MovementPoint<AnimationState>(timeProvider.now(),
						AnimationState.IDLE,
						new SpacedVector3(0, 100 + RangeConstants.INTERACTION_RANGE / 2, 100),
						SpacedRotation.IDENTITY));

		TradeInitResult result = tradeService.initiateTrade(initiator, collaborator);
		assertEquals(TradeInitResult.SUCCESS, result);
	}

	@Test
	public void testInitiateTradeTooFarAway() throws Exception {
		movementService.moveAndRotateEntity(initiator,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(0, 100, 100), SpacedRotation.IDENTITY));
		movementService.moveAndRotateEntity(collaborator,
				new MovementPoint<AnimationState>(timeProvider.now(),
						AnimationState.IDLE,
						new SpacedVector3(0, 100 + RangeConstants.INTERACTION_RANGE + 1e-3, 100),
						SpacedRotation.IDENTITY));

		TradeInitResult result = tradeService.initiateTrade(initiator, collaborator);
		assertEquals(TradeInitResult.TOO_FAR_AWAY, result);
	}

	@Test
	public void testInitiateTradeTwice() throws Exception {
		movementService.moveAndRotateEntity(initiator,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(0, 100, 100), SpacedRotation.IDENTITY));
		movementService.moveAndRotateEntity(collaborator,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(0, 100, 100), SpacedRotation.IDENTITY));

		tradeService.initiateTrade(initiator, collaborator);
		TradeInitResult result = tradeService.initiateTrade(initiator, collaborator);

		assertEquals(TradeInitResult.TRADE_ALREADY_IN_PROGRESS, result);
	}

	@Test
	public void testInitiateTradeAfterCompletion() throws Exception {
		movementService.moveAndRotateEntity(initiator,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(0, 100, 100), SpacedRotation.IDENTITY));
		movementService.moveAndRotateEntity(collaborator,
				new MovementPoint<AnimationState>(timeProvider.now(), AnimationState.IDLE, new SpacedVector3(0, 100, 100), SpacedRotation.IDENTITY));

		tradeService.initiateTrade(initiator, collaborator);
		ArgumentCatcher<String> checksum = ArgumentCatcher.create(mAny(String.class));
		verifyOnce().on(tradeCallback).initiated(any(ServerEntity.class), any(ServerEntity.class), match(checksum));

		tradeService.acceptOffer(initiator, checksum.getValue());
		tradeService.acceptOffer(collaborator, checksum.getValue());

		TradeInitResult result = tradeService.initiateTrade(initiator, collaborator);

		assertEquals(TradeInitResult.SUCCESS, result);
	}


	@Test
	public void testInitiatorAccepts() throws Exception {
		tradeService.initiateTrade(initiator, collaborator);
		ArgumentCatcher<String> checksum = ArgumentCatcher.create(mAny(String.class));
		verifyOnce().on(tradeCallback).initiated(any(ServerEntity.class), any(ServerEntity.class), match(checksum));

		TradeAcceptResult tradeAcceptResult = tradeService.acceptOffer(initiator, checksum.getValue());

		assertEquals(TradeAcceptResult.SUCCESS, tradeAcceptResult);

	}

	@Test
	public void testInitiatorAcceptsBadChecksum() throws Exception {
		tradeService.initiateTrade(initiator, collaborator);
		ArgumentCatcher<String> checksum = ArgumentCatcher.create(mAny(String.class));
		verifyOnce().on(tradeCallback).initiated(any(ServerEntity.class), any(ServerEntity.class), match(checksum));

		TradeAcceptResult tradeAcceptResult = tradeService.acceptOffer(initiator, checksum.getValue() + "bad");

		assertEquals(TradeAcceptResult.ACCEPT_FAILED, tradeAcceptResult);

	}

	@Test
	public void testCollaboratorAcceptsBadChecksum() throws Exception {
		tradeService.initiateTrade(initiator, collaborator);
		ArgumentCatcher<String> checksum = ArgumentCatcher.create(mAny(String.class));
		verifyOnce().on(tradeCallback).initiated(any(ServerEntity.class), any(ServerEntity.class), match(checksum));

		TradeAcceptResult tradeAcceptResult = tradeService.acceptOffer(collaborator, checksum.getValue() + "bad");

		assertEquals(TradeAcceptResult.ACCEPT_FAILED, tradeAcceptResult);

	}

	@Test
	public void testCloseTrade() {

		ServerItem item = mock(ServerItem.class);
		when(item.getPk()).thenReturn(UUID.ZERO);

		assertEquals(tradeService.initiateTrade(initiator, collaborator), TradeInitResult.SUCCESS);
		assertEquals(tradeService.initiateTrade(initiator, collaborator), TradeInitResult.TRADE_ALREADY_IN_PROGRESS);
		assertEquals(tradeService.addItem(initiator, item), TradeAddItemResult.SUCCESS);
		tradeService.closeTrade(initiator);
		assertEquals(tradeService.addItem(initiator, item), TradeAddItemResult.TRADE_NOT_INITIALIZED);
		assertEquals(tradeService.initiateTrade(initiator, collaborator), TradeInitResult.SUCCESS);
		assertEquals(tradeService.addItem(initiator, item), TradeAddItemResult.SUCCESS);

	}

	@Test
	public void testTradeCompleteCallback() throws Exception {
		tradeService.initiateTrade(initiator, collaborator);

		tradeService.addItem(initiator, toyCar);

		tradeService.addItem(collaborator, screw);

		ArgumentCatcher<String> checksum = ArgumentCatcher.create(mAny(String.class));
		verifyOnce().on(tradeCallback).itemAdded(collaborator, initiator, screw, match(checksum));

		tradeService.acceptOffer(collaborator, checksum.getValue());
		tradeService.acceptOffer(initiator, checksum.getValue());

		ArgumentCatcher<TradeTransaction> trade = ArgumentCatcher.create(mAny(TradeTransaction.class));
		verifyOnce().on(tradeCallback).tradeCompleted(match(trade));

		assertEquals(initiator, trade.getValue().getInitiator());
		assertEquals(collaborator, trade.getValue().getCollaborator());
		assertTrue(trade.getValue().getItemsFromInitiator().contains(toyCar));
		assertEquals(1, trade.getValue().getItemsFromInitiator().size());
		assertTrue(trade.getValue().getItemsFromCollaborator().contains(screw));
		assertEquals(1, trade.getValue().getItemsFromCollaborator().size());
	}

	@Test
	public void initiatorDisconnects() throws Exception {
		tradeService.initiateTrade(initiator, collaborator);

		entityServiceListenerDispatcher.trigger().entityRemoved(initiator);

		verifyOnce().on(tradeCallback).aborted(initiator, collaborator);
	}

	@Test
	public void collaboratorDisconnects() throws Exception {
		tradeService.initiateTrade(initiator, collaborator);

		ArgumentCatcher<String> checksum = ArgumentCatcher.create(mAny(String.class));
		verifyOnce().on(tradeCallback).initiated(initiator, collaborator, match(checksum));


		tradeService.acceptOffer(collaborator, checksum.getValue());

		entityServiceListenerDispatcher.trigger().entityRemoved(initiator);

		verifyOnce().on(tradeCallback).aborted(initiator, collaborator);
	}
}
