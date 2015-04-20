package se.spaced.server.tools.loot.edit;

import se.spaced.server.loot.LootTemplateProbability;
import se.spaced.server.loot.MultiLootTemplate;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

public class MultiLootTemplateTreeNode extends DefaultMutableTreeNode implements HasLootTemplateEditor, HasPopupMenu, LootTemplateNodeWithProbabilites {
	private final MultiLootTemplate template;
	private final Callback<LootTemplateNodeWithProbabilites> addSingleItemAction;
	private final Callback<LootTemplateNodeWithProbabilites> addKofnAction;
	private final Callback<LootTemplateNodeWithProbabilites> addMultiAction;
	private final Callback<LootTemplateNodeWithProbabilites> removeAction;

	private final JPopupMenu popupMenu;
	private final Callback<Void> onSave;

	public MultiLootTemplateTreeNode(
			MultiLootTemplate template,
			Callback<LootTemplateNodeWithProbabilites> addSingleItemAction,
			Callback<LootTemplateNodeWithProbabilites> addKofnAction,
			Callback<LootTemplateNodeWithProbabilites> addMultiAction,
			Callback<LootTemplateNodeWithProbabilites> removeAction, Callback<Void> onSave) {
		super(template, true);
		this.template = template;
		this.addSingleItemAction = addSingleItemAction;
		this.addKofnAction = addKofnAction;
		this.addMultiAction = addMultiAction;
		this.removeAction = removeAction;
		this.onSave = onSave;
		popupMenu = new JPopupMenu();
		buildPopupMenu();
	}

	public MultiLootTemplate getTemplate() {
		return template;
	}

	@Override
	public JPanel getEditPanel() {
		JPanel panel = new JPanel(new GridLayout(4, 2));
		panel.add(new JLabel("PK"));
		panel.add(new JLabel(template.getPk().toString()));

		panel.add(new JLabel("Name"));
		final JTextField nameTextField = new JTextField(template.getName());
		panel.add(nameTextField);

		JButton button = new JButton("Save");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				template.setName(nameTextField.getText());
				onSave.onAction(null);

			}
		});
		panel.add(button);


		return panel;
	}

	@Override
	public String toString() {
		return "Multi";
	}

	private void buildPopupMenu() {

		popupMenu.add(new JMenuItem("Add SingleItem Template")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addSingleItemAction.onAction(MultiLootTemplateTreeNode.this);
			}
		});

		popupMenu.add(new JMenuItem("Add KofN Template")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addKofnAction.onAction(MultiLootTemplateTreeNode.this);
			}
		});

		popupMenu.add(new JMenuItem("Add MultiLoot Template")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addMultiAction.onAction(MultiLootTemplateTreeNode.this);
			}
		});

		popupMenu.add(new JMenuItem("Remove")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeAction.onAction(MultiLootTemplateTreeNode.this);
			}
		});
	}

	@Override
	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}

	@Override
	public Set<LootTemplateProbability> getTemplates() {
		return template.getLootTemplates();
	}

	@Override
	public void removeFromParent() {
		if (getParent() == null) {
			return;
		}

		LootTemplateProbabilityTreeNode lootTemplateProbabilityNode = (LootTemplateProbabilityTreeNode) getParent();
		LootTemplateNodeWithProbabilites templateNode = (LootTemplateNodeWithProbabilites) getParent().getParent();
		templateNode.getTemplates().remove(lootTemplateProbabilityNode.getProbability());
		((DefaultMutableTreeNode) getParent()).removeFromParent();
	}
}
