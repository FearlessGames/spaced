package se.spaced.server.net.listeners.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ardortech.math.SpacedVector3;
import se.spaced.messages.protocol.Entity;
import se.spaced.messages.protocol.SpacedItem;
import se.spaced.messages.protocol.c2s.ClientVendorMessages;
import se.spaced.messages.protocol.s2c.S2CProtocol;
import se.spaced.server.model.Player;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.items.ServerItem;
import se.spaced.server.model.vendor.VendorService;
import se.spaced.server.net.ClientConnection;
import se.spaced.server.net.broadcast.SmrtBroadcaster;
import se.spaced.shared.RangeConstants;

import java.util.Collection;

public class ClientVendorMessagesAuth implements ClientVendorMessages {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final ClientConnection clientConnection;
	private final VendorService vendorService;
	private final SmrtBroadcaster<S2CProtocol> smrtBroadcaster;


	public ClientVendorMessagesAuth(
			ClientConnection clientConnection,
			VendorService vendorService,
			SmrtBroadcaster<S2CProtocol> smrtBroadcaster) {
		this.clientConnection = clientConnection;
		this.vendorService = vendorService;
		this.smrtBroadcaster = smrtBroadcaster;
	}

	@Override
	public void requestVendorStock(Entity vendor) {
		Player player = clientConnection.getPlayer();

		AssertResult assertResult = assertInParameters(player, vendor);
		switch (assertResult) {

			case IS_NO_VENDOR:
			case NULL_ELEMENTS:
				log.warn("Bad parameters in requestVendorStock {}", vendor);
				return;
			case VENDOR_OOR:
				clientConnection.getReceiver().vendor().vendorOutOfRange(vendor);
				break;
			case OK:
				vendorService.initVendoring(player, vendor);
				clientConnection.getReceiver().vendor().vendorStockItems(vendor,
						vendorService.getWares(vendor.getPk(), player));
				break;
			default:
				throw new IllegalStateException("Bad enum" + assertResult);
		}

	}

	private boolean isVendorInRange(Player player, Entity serverVendor) {
		double rangeSq = SpacedVector3.distanceSq(player.getPosition(), ((ServerEntity) serverVendor).getPosition());
		return rangeSq <= RangeConstants.INTERACTION_RANGE_SQ;
	}

	@Override
	public void playerBuysItemFromVendor(Entity vendor, SpacedItem item) {
		ServerItem serverItem = (ServerItem) item;
		Player player = clientConnection.getPlayer();
		AssertResult assertResult = assertInParameters(player, vendor, serverItem);

		switch (assertResult) {

			case IS_NO_VENDOR:
			case NULL_ELEMENTS:
				log.warn("Bad parameters in playerBuysItemFromVendor {}, {}", vendor, serverItem);
				return;
			case VENDOR_OOR:
				clientConnection.getReceiver().vendor().vendorOutOfRange(vendor);
				break;
			case OK:
				if (serverItem.getSellsFor() == null || serverItem.getSellsFor().isZero()) {
					log.warn("{} trying to buy an item without a price {} ", player, serverItem);
					return;
				}
				vendorService.playerBuysItemFromVendor(vendor, player, serverItem);
				break;
			default:
				throw new IllegalStateException("Bad enum" + assertResult);
		}

	}

	@SuppressWarnings(value = "unchecked")
	@Override
	public void playerSellsItemsToVendor(Entity vendor, Collection<? extends SpacedItem> items) {
		Player player = clientConnection.getPlayer();


		AssertResult assertResult = assertInParameters(player, vendor, items.toArray());
		switch (assertResult) {
			case IS_NO_VENDOR:
			case NULL_ELEMENTS:
				return;
			case VENDOR_OOR:
				clientConnection.getReceiver().vendor().vendorOutOfRange(vendor);
				break;
			case OK:
				performSaleFromPlayerToVendor(vendor, player, (Collection<ServerItem>) items);
				break;
			default:
				throw new IllegalStateException("Bad enum" + assertResult);
		}
	}

	private void performSaleFromPlayerToVendor(Entity vendor, Player player, Iterable<ServerItem> items) {
		for (ServerItem serverItem : items) {
			if (serverItem.getSellsFor() == null || serverItem.getSellsFor().isZero()) {
				break;
			}
			ServerItem newItem = vendorService.playerSellsItemToVendor(player, vendor, serverItem);
			if (newItem != null) {
				smrtBroadcaster.create().to(vendorService.getCurrentPeopleVendoring(vendor)).send().vendor().vendorAddedItem(
						newItem);
			}
		}
	}

	@Override
	public void endVendoring(Entity vendor) {
		Player player = clientConnection.getPlayer();
		AssertResult assertResult = assertInParameters(player, vendor);
		if (assertResult.equals(AssertResult.NULL_ELEMENTS) || assertResult.equals(AssertResult.IS_NO_VENDOR)) {
			return;
		}
		vendorService.endVendoring(player, vendor);
	}

	@Override
	public void startVendoring(Entity vendor) {
		Player player = clientConnection.getPlayer();

		AssertResult assertResult = assertInParameters(player, vendor);

		if (assertResult.equals(AssertResult.NULL_ELEMENTS) || assertResult.equals(AssertResult.IS_NO_VENDOR)) {
			return;
		}

		if (assertResult.equals(AssertResult.VENDOR_OOR)) {
			clientConnection.getReceiver().vendor().vendorOutOfRange(vendor);
		} else if (assertResult.equals(AssertResult.OK)) {
			vendorService.initVendoring(player, vendor);
		}
	}

	private AssertResult assertInParameters(Player player, Entity vendor, Object... nullCheckObjects) {
		if (player == null) {
			String message = "Tried to initiate trade when not logged in";
			throw new IllegalStateException(message);
		}

		if (vendor == null) {
			return AssertResult.NULL_ELEMENTS;
		}

		if (!vendorService.isVendor(vendor)) {
			return AssertResult.IS_NO_VENDOR;
		}

		for (Object nullCheckObject : nullCheckObjects) {
			if (nullCheckObject == null) {
				return AssertResult.NULL_ELEMENTS;
			}
		}

		if (!isVendorInRange(player, vendor)) {
			return AssertResult.VENDOR_OOR;
		}

		return AssertResult.OK;
	}

	private enum AssertResult {
		IS_NO_VENDOR,
		NULL_ELEMENTS,
		VENDOR_OOR,
		OK
	}


}