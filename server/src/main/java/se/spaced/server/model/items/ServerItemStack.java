package se.spaced.server.model.items;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.hibernate.annotations.Type;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.ItemTemplate;
import se.spaced.server.persistence.dao.impl.PersistableBase;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class ServerItemStack extends PersistableBase {


	@OneToMany(fetch = FetchType.EAGER)
	private List<ServerItem> stack = Lists.newArrayList();

	@Type(type = "uuid")
	private UUID itemTemplatePk;

	private int maxStackSize;

	protected ServerItemStack() {
		maxStackSize = 1;
	}

	public ItemTemplate getItemTemplate() {
		if (itemTemplatePk == null) {
			return null;
		}
		return selectItem().getItemTemplate();
	}

	public boolean contains(ServerItem item) {
		return stack.contains(item);
	}

	public ServerItem get(final UUID itemPk) {
		return Iterables.tryFind(stack, new Predicate<ServerItem>() {
			@Override
			public boolean apply(ServerItem serverItem) {
				return serverItem.getPk().equals(itemPk);
			}
		}).orNull();

	}

	public ImmutableCollection<ServerItem> getAll() {
		return ImmutableList.copyOf(stack);
	}

	public boolean add(ServerItem serverItem) {

		if (!isOfType(serverItem.getTemplate())) {
			return false;
		}

		if (isFull()) {
			return false;
		}

		itemTemplatePk = serverItem.getTemplate().getPk();
		maxStackSize = serverItem.getTemplate().getMaxStackSize();

		stack.add(serverItem);
		return true;
	}

	public ServerItem selectItem() {
		return stack.get(0);
	}

	public boolean removeItem(ServerItem item) {
		boolean remove = stack.remove(item);
		if (remove) {
			if (stack.isEmpty()) {
				itemTemplatePk = null;
			}
		}
		return remove;

	}

	public void clear() {
		stack.clear();
		itemTemplatePk = null;
		maxStackSize = 1;
	}

	public void addAll(ImmutableCollection<ServerItem> stack) {
		for (ServerItem serverItem : stack) {
			add(serverItem);
		}
	}

	public boolean isFull() {
		if (stack.isEmpty()) {
			return false;
		}

		return stack.size() == maxStackSize;

	}

	public int size() {
		return stack.size();
	}

	public boolean isOfType(ServerItemTemplate template) {
		if (itemTemplatePk == null) {
			return true;
		}

		return itemTemplatePk.equals(template.getPk());
	}

	public int getMaxStackSize() {
		return maxStackSize;
	}
}
