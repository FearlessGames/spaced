package se.spaced.server.tools.loot.edit;

import se.spaced.server.loot.KofNLootTemplate;
import se.spaced.server.loot.LootTemplateProbability;

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

public class KofNLootTemplateTreeNode extends DefaultMutableTreeNode implements HasLootTemplateEditor, HasPopupMenu, LootTemplateNodeWithProbabilites {
	private final KofNLootTemplate lootTemplate;
	private final JPopupMenu popupMenu;
	private final Callback<LootTemplateNodeWithProbabilites> addSingleItemAction;
	private final Callback<LootTemplateNodeWithProbabilites> addKofnAction;
	private final Callback<LootTemplateNodeWithProbabilites> addMultiAction;
	private final Callback<LootTemplateNodeWithProbabilites> removeAction;
	private final Callback<Void> onSave;

	public KofNLootTemplateTreeNode(
			KofNLootTemplate lootTemplate,
			Callback<LootTemplateNodeWithProbabilites> addSingleItemAction,
			Callback<LootTemplateNodeWithProbabilites> addKofnAction,
			Callback<LootTemplateNodeWithProbabilites> addMultiAction,
			Callback<LootTemplateNodeWithProbabilites> removeAction, Callback<Void> onSave) {
		super(lootTemplate, true);
		this.lootTemplate = lootTemplate;
		this.addSingleItemAction = addSingleItemAction;
		this.addKofnAction = addKofnAction;
		this.addMultiAction = addMultiAction;
		this.removeAction = removeAction;
		this.onSave = onSave;
		popupMenu = new JPopupMenu();
		buildPopupMenu();
	}

	private void buildPopupMenu() {

		popupMenu.add(new JMenuItem("Add SingleItem Template")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addSingleItemAction.onAction(KofNLootTemplateTreeNode.this);
			}
		});

		popupMenu.add(new JMenuItem("Add KofN Template")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addKofnAction.onAction(KofNLootTemplateTreeNode.this);
			}
		});

		popupMenu.add(new JMenuItem("Add MultiLoot Template")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addMultiAction.onAction(KofNLootTemplateTreeNode.this);
			}
		});

		popupMenu.add(new JMenuItem("Remove")).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeAction.onAction(KofNLootTemplateTreeNode.this);
			}
		});
	}

	public KofNLootTemplate getLootTemplate() {
		return lootTemplate;
	}

	@Override
	public JPanel getEditPanel() {
		JPanel panel = new JPanel(new GridLayout(4, 2));
		panel.add(new JLabel("PK"));
		panel.add(new JLabel(lootTemplate.getPk().toString()));

		panel.add(new JLabel("Name"));
		final JTextField nameTextField = new JTextField(lootTemplate.getName());
		panel.add(nameTextField);

		panel.add(new JLabel("K"));
		final JTextField kTextField = new JTextField(String.valueOf(lootTemplate.getK()));
		panel.add(kTextField);
		panel.add(new JLabel(""));

		JButton button = new JButton("Save");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int k = Integer.parseInt(kTextField.getText());
					lootTemplate.setK(k);
					lootTemplate.setName(nameTextField.getText());
					onSave.onAction(null);
				} catch (NumberFormatException nfe) {
					kTextField.setText(String.valueOf(lootTemplate.getK()));
				}
			}
		});
		panel.add(button);


		return panel;
	}

	@Override
	public String toString() {

		return "KofN " + (lootTemplate.getName() == null ? "" : lootTemplate.getName()) + " : " + lootTemplate.getK();
	}

	@Override
	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}

	@Override
	public Set<LootTemplateProbability> getTemplates() {
		return lootTemplate.getTemplates();
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
