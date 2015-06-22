package se.spaced.server.tools.loot.edit;

import se.spaced.server.loot.SingleItemLootTemplate;
import se.spaced.server.model.items.ServerItemTemplate;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

public class SingleItemLootTemplateTreeNode extends DefaultMutableTreeNode implements HasLootTemplateEditor, HasPopupMenu {
	private final SingleItemLootTemplate lootTemplate;
	private final JPopupMenu popupMenu;
	private final Callback<SingleItemLootTemplateTreeNode> removeAction;
	private final List<ServerItemTemplate> itemTemplates;
	private final Callback<Void> onSave;

	public SingleItemLootTemplateTreeNode(
			SingleItemLootTemplate lootTemplate,
			Callback<SingleItemLootTemplateTreeNode> removeAction,
			List<ServerItemTemplate> itemTemplates, Callback<Void> onSave) {
		super(lootTemplate, false);
		this.lootTemplate = lootTemplate;
		this.removeAction = removeAction;
		this.itemTemplates = itemTemplates;
		this.onSave = onSave;
		popupMenu = new JPopupMenu();
		buildPopupMenu();


	}

	private void buildPopupMenu() {
		popupMenu.add(new JMenuItem("Remove")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeAction.onAction(SingleItemLootTemplateTreeNode.this);
			}
		});
	}

	public SingleItemLootTemplate getLootTemplate() {
		return lootTemplate;
	}

	@Override
	public String toString() {
		if (lootTemplate.getItemTemplate() == null) {
			return "Undefined item";
		}
		return lootTemplate.getItemTemplate().getName();
	}

	@Override
	public JPanel getEditPanel() {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(new JLabel("PK"), c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		panel.add(new JLabel(lootTemplate.getPk().toString()), c);


		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		panel.add(new JLabel("Name"), c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		final JTextField nameTextField = new JTextField(lootTemplate.getName());
		panel.add(nameTextField, c);


		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		panel.add(new JLabel("ItemTemplate"), c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 2;
		final JLabel itemLabel = new JLabel("None selected");
		if (lootTemplate.getItemTemplate() != null) {
			itemLabel.setText(lootTemplate.getItemTemplate().getName());
		}
		panel.add(itemLabel, c);


		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		panel.add(new JLabel(""), c);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 3;
		JButton button = new JButton("Save");
		panel.add(button, c);


		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 40;
		c.gridx = 0;
		c.gridy = 4;
		c.gridwidth = 2;
		final JList itemTemplateList = new JList(new Vector<Object>(itemTemplates));
		panel.add(itemTemplateList, c);

		itemTemplateList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				Object value = itemTemplateList.getSelectedValue();
				if (value != null) {
					ServerItemTemplate itemTemplate = (ServerItemTemplate) value;
					itemLabel.setText(itemTemplate.getName());
				}
			}
		});

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object value = itemTemplateList.getSelectedValue();
				if (value != null) {
					ServerItemTemplate itemTemplate = (ServerItemTemplate) value;
					lootTemplate.setItemTemplate(itemTemplate);
				}

				onSave.onAction(null);

			}
		});


		return panel;
	}

	@Override
	public JPopupMenu getPopupMenu() {
		return popupMenu;
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
