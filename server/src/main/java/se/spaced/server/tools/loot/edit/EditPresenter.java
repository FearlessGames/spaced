package se.spaced.server.tools.loot.edit;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import se.fearlessgames.common.util.uuid.UUIDFactory;
import se.spaced.server.loot.EmptyLootTemplate;
import se.spaced.server.loot.KofNLootTemplate;
import se.spaced.server.loot.LootTemplate;
import se.spaced.server.loot.LootTemplateProbability;
import se.spaced.server.loot.MultiLootTemplate;
import se.spaced.server.loot.PersistableLootTemplate;
import se.spaced.server.loot.SingleItemLootTemplate;
import se.spaced.server.model.items.ServerItemTemplate;
import se.spaced.server.persistence.dao.impl.hibernate.TransactionManager;
import se.spaced.server.persistence.dao.interfaces.LootTemplateDao;
import se.spaced.server.tools.loot.MainView;
import se.spaced.server.tools.loot.PersistedLootService;
import se.spaced.server.tools.loot.Presenter;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.SortedSet;


@Singleton
public class EditPresenter implements Presenter, EditView.Presenter {
	private final EditView view;
	private final UUIDFactory uuidFactory;
	private final SessionFactory sessionFactory;
	private final TransactionManager transactionManager;
	private final PersistedLootService persistedLootService;
	private final LootTemplateDao lootTemplateDao;
	private final List<ServerItemTemplate> itemTemplates;

	private final Callback<LootTemplateNodeWithProbabilites> remove;
	private final Callback<LootTemplateNodeWithProbabilites> addSingleItem;
	private final Callback<LootTemplateNodeWithProbabilites> addKofn;
	private final Callback<LootTemplateNodeWithProbabilites> addMultiLoot;
	private final Callback<SingleItemLootTemplateTreeNode> removeSingleLootTemplateAction;
	private final Callback<Void> onSave;
	private PersistableLootTemplate currentTemplate;


	@Inject
	public EditPresenter(
			final EditView view,
			TransactionManager transactionManager,
			PersistedLootService persistedLootService,
			LootTemplateDao lootTemplateDao,
			final UUIDFactory uuidFactory,
			SessionFactory sessionFactory) {
		this.view = view;
		this.transactionManager = transactionManager;
		this.persistedLootService = persistedLootService;
		this.lootTemplateDao = lootTemplateDao;
		this.uuidFactory = uuidFactory;
		this.sessionFactory = sessionFactory;
		view.setPresenter(this);

		itemTemplates = new ArrayList<ServerItemTemplate>();


		remove = new Callback<LootTemplateNodeWithProbabilites>() {
			@Override
			public void onAction(LootTemplateNodeWithProbabilites templateNodeWith) {
				templateNodeWith.removeFromParent();
				view.refreshTree();
			}
		};

		removeSingleLootTemplateAction = new Callback<SingleItemLootTemplateTreeNode>() {
			@Override
			public void onAction(SingleItemLootTemplateTreeNode singleItemLootTemplateTreeNode) {
				singleItemLootTemplateTreeNode.removeFromParent();
				view.refreshTree();
			}
		};


		addKofn = new Callback<LootTemplateNodeWithProbabilites>() {
			@Override
			public void onAction(LootTemplateNodeWithProbabilites templateNodeWith) {
				addTemplate(templateNodeWith,
						new KofNLootTemplate(uuidFactory.combUUID(), "", 1, new HashSet<LootTemplateProbability>()));

			}
		};

		addSingleItem = new Callback<LootTemplateNodeWithProbabilites>() {
			@Override
			public void onAction(LootTemplateNodeWithProbabilites templateNodeWith) {
				addTemplate(templateNodeWith, new SingleItemLootTemplate(uuidFactory.combUUID(), "", null));
			}
		};

		addMultiLoot = new Callback<LootTemplateNodeWithProbabilites>() {
			@Override
			public void onAction(LootTemplateNodeWithProbabilites templateNodeWith) {
				addTemplate(templateNodeWith,
						new MultiLootTemplate(uuidFactory.combUUID(), new HashSet<LootTemplateProbability>(), ""));
			}
		};

		onSave = new Callback<Void>() {
			@Override
			public void onAction(Void aVoid) {
				view.refreshTree();
			}
		};
	}

	private void addTemplate(LootTemplateNodeWithProbabilites rootTemplateNodeWith, PersistableLootTemplate template) {
		LootTemplateProbability lootTemplateProbability = new LootTemplateProbability(uuidFactory.combUUID(),
				template,
				0.0);
		rootTemplateNodeWith.getTemplates().add(lootTemplateProbability);
		rootTemplateNodeWith.add(creatProbabilityNode(lootTemplateProbability));
		view.refreshTree();
	}

	@Override
	public void addTabOn(MainView mainView) {
		populateTemplates();
		mainView.addTabPanel("Templates", view.getPanel());

		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = transactionManager.beginTransaction();
		List<ServerItemTemplate> templates = session.createQuery("from ServerItemTemplate").list();
		itemTemplates.addAll(templates);
		transaction.commit();

	}

	@Override
	public void onViewTemplate(PersistableLootTemplate template) {
		if (template == null) {
			view.setTreeRootNode(new DefaultMutableTreeNode("-"));
			currentTemplate = null;
			return;
		}
		Transaction transaction = transactionManager.beginTransaction();
		currentTemplate = lootTemplateDao.findByPk(template.getPk());
		TreeNode rootNode = createLootTemplateNode(currentTemplate);
		view.setTreeRootNode(rootNode);
		transaction.commit();
	}

	private MutableTreeNode createLootTemplateNode(LootTemplate template) {
		if (template instanceof EmptyLootTemplate) {
			// Do nothing
		} else if (template instanceof KofNLootTemplate) {
			return createKofNTemplateNode((KofNLootTemplate) template);
		} else if (template instanceof MultiLootTemplate) {
			return createMultiTemplateNode((MultiLootTemplate) template);
		} else if (template instanceof SingleItemLootTemplate) {
			return createSingleTemplateNode((SingleItemLootTemplate) template);
		}
		return null;
	}

	private MutableTreeNode createMultiTemplateNode(MultiLootTemplate template) {
		MultiLootTemplateTreeNode treeNode = new MultiLootTemplateTreeNode(template,
				addSingleItem,
				addKofn,
				addMultiLoot,
				remove, onSave);
		for (LootTemplateProbability lootTemplateProbability : template.getLootTemplates()) {
			treeNode.add(creatProbabilityNode(lootTemplateProbability));
		}
		return treeNode;
	}

	private MutableTreeNode createSingleTemplateNode(SingleItemLootTemplate template) {
		return new SingleItemLootTemplateTreeNode(template, removeSingleLootTemplateAction, itemTemplates, onSave);
	}

	private MutableTreeNode createKofNTemplateNode(KofNLootTemplate template) {
		KofNLootTemplateTreeNode treeNode = new KofNLootTemplateTreeNode(template,
				addSingleItem,
				addKofn,
				addMultiLoot,
				remove, onSave);
		for (LootTemplateProbability lootTemplateProbability : template.getTemplates()) {
			treeNode.add(creatProbabilityNode(lootTemplateProbability));
		}
		return treeNode;
	}

	private MutableTreeNode creatProbabilityNode(LootTemplateProbability lootTemplateProbability) {
		LootTemplateProbabilityTreeNode treeNode = new LootTemplateProbabilityTreeNode(lootTemplateProbability, onSave);
		treeNode.add(createLootTemplateNode(lootTemplateProbability.getLootTemplate()));
		return treeNode;
	}

	@Override
	public void onReload() {
		populateTemplates();
	}

	@Override
	public void onNodeSelected(Object lastSelectedPathComponent) {
		if (lastSelectedPathComponent == null) {
			return;
		}

		HasLootTemplateEditor hasEditor = (HasLootTemplateEditor) lastSelectedPathComponent;
		JPanel editorPanel = hasEditor.getEditPanel();
		view.setEditorPanel(editorPanel);

	}

	@Override
	public void onPersist() {
		if (currentTemplate != null) {
			Transaction transaction = transactionManager.beginTransaction();
			lootTemplateDao.persist(currentTemplate);
			transaction.commit();

			populateTemplates();

			onViewTemplate(null);
		}
	}

	private void populateTemplates() {
		Transaction tx = transactionManager.beginTransaction();
		SortedSet<PersistableLootTemplate> lootTemplates = persistedLootService.getSortedLootTemplates();
		view.setTemplates(lootTemplates.toArray(new PersistableLootTemplate[lootTemplates.size()]));
		tx.commit();
	}

	@Override
	public void createNewKofN(String name) {
		if (name != null && !name.isEmpty()) {
			KofNLootTemplate lootTemplate = new KofNLootTemplate(uuidFactory.combUUID(),
					name,
					1,
					new HashSet<LootTemplateProbability>());
			createNewTemplate(lootTemplate);
		}
	}

	@Override
	public void createNewMulti(String name) {
		if (name != null && !name.isEmpty()) {
			MultiLootTemplate lootTemplate = new MultiLootTemplate(uuidFactory.combUUID(),
					new HashSet<LootTemplateProbability>(),
					name);
			createNewTemplate(lootTemplate);
		}
	}

	@Override
	public void createNewSingle(String name) {
		if (name != null && !name.isEmpty()) {
			SingleItemLootTemplate lootTemplate = new SingleItemLootTemplate(uuidFactory.combUUID(), name, null);
			createNewTemplate(lootTemplate);
		}
	}

	private void createNewTemplate(PersistableLootTemplate lootTemplate) {
		lootTemplateDao.persist(lootTemplate);
		populateTemplates();
		onViewTemplate(lootTemplate);
	}
}
