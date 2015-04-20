package se.spaced.server.model.items;

import org.hibernate.Transaction;
import org.junit.Test;
import se.fearlessgames.common.util.MockTimeProvider;
import se.fearlessgames.common.util.TimeProvider;
import se.spaced.server.model.PersistedAppearanceData;
import se.spaced.server.model.ServerEntity;
import se.spaced.server.model.currency.PersistedCurrency;
import se.spaced.server.model.currency.PersistedMoney;
import se.spaced.server.model.player.PlayerMockFactory;
import se.spaced.server.persistence.dao.impl.hibernate.PersistentTestBase;
import se.spaced.shared.model.items.ContainerType;
import se.spaced.shared.model.items.ItemType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class EquippedItemsPersistenceTest extends PersistentTestBase {
	@Test
	public void testPersisteanceOfEquipment() {
		Transaction tx = transactionManager.beginTransaction();
		PersistedCurrency euroTrashDollars = new PersistedCurrency("EuroTrashDollars");
		euroTrashDollars.setPk(uuidFactory.randomUUID());
		ServerItemTemplate bootsTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Boots of death",
				ItemType.SHOES).appearance(new PersistedAppearanceData("boots", "boots")).sellsFor(new PersistedMoney(
				euroTrashDollars,
				10l)).build();
		ServerItemTemplate swordTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Sword of doom", ItemType.MAIN_HAND_ITEM, ItemType.OFF_HAND_ITEM).appearance(new PersistedAppearanceData(
				"sword",
				"sword")).sellsFor(new PersistedMoney(euroTrashDollars, 10l)).build();
		ServerItemTemplate pewPewTemplate = new ServerItemTemplate.Builder(uuidFactory.combUUID(),
				"Lazor pew pew rifle", ItemType.TWO_HAND_ITEM).appearance(new PersistedAppearanceData("lazor",
				"lazor")).sellsFor(new PersistedMoney(euroTrashDollars, 10l)).build();

		TimeProvider timeProvider = new MockTimeProvider();
		PlayerMockFactory factory = new PlayerMockFactory.Builder(timeProvider, uuidFactory).build();
		ServerEntity entity = factory.createPlayer("Bob");
		entity.setPk(null);
		sessionFactory.getCurrentSession().saveOrUpdate(euroTrashDollars);
		sessionFactory.getCurrentSession().saveOrUpdate(entity);
		sessionFactory.getCurrentSession().saveOrUpdate(bootsTemplate);
		sessionFactory.getCurrentSession().saveOrUpdate(swordTemplate);
		sessionFactory.getCurrentSession().saveOrUpdate(pewPewTemplate);

		ServerItem bootOfDeath = bootsTemplate.create();
		bootOfDeath.setOwner(entity);

		ServerItem swordOfDoom = swordTemplate.create();
		swordOfDoom.setOwner(entity);

		ServerItem lazorRifle = pewPewTemplate.create();
		lazorRifle.setOwner(entity);

		sessionFactory.getCurrentSession().saveOrUpdate(bootOfDeath);
		sessionFactory.getCurrentSession().saveOrUpdate(swordOfDoom);
		sessionFactory.getCurrentSession().saveOrUpdate(lazorRifle);

		EquippedItems equippedItems = new EquippedItems(entity);
		equippedItems.put(bootOfDeath, ContainerType.FEET);
		equippedItems.put(swordOfDoom, ContainerType.MAIN_HAND);


		sessionFactory.getCurrentSession().saveOrUpdate(equippedItems);
		tx.commit();
		assertEquals(2, equippedItems.getEquippedItems().size());
		assertEquals(equippedItems.getOwnerPk(), entity.getPk());


		tx = transactionManager.beginTransaction();
		sessionFactory.getCurrentSession().clear();
		equippedItems = (EquippedItems) sessionFactory.getCurrentSession().get(EquippedItems.class,
				equippedItems.getPk());
		assertNotNull(equippedItems.getEquippedItems());
		assertEquals(2, equippedItems.getEquippedItems().size());
		tx.commit();


		equippedItems.put(lazorRifle, ItemType.TWO_HAND_ITEM.getMainSlot());
		assertEquals(2, equippedItems.getEquippedItems().size());


	}
}
