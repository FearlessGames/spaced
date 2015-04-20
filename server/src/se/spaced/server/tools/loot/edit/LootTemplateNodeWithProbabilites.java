package se.spaced.server.tools.loot.edit;

import se.spaced.server.loot.LootTemplateProbability;

import javax.swing.tree.MutableTreeNode;
import java.util.Set;

public interface LootTemplateNodeWithProbabilites {
	Set<LootTemplateProbability> getTemplates();

	void add(MutableTreeNode newChild);

	void removeFromParent();
}
