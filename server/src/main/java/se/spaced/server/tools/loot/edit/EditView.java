package se.spaced.server.tools.loot.edit;

import se.spaced.server.loot.PersistableLootTemplate;
import se.spaced.server.tools.loot.HasPanel;

import javax.swing.JPanel;
import javax.swing.tree.TreeNode;

public interface EditView extends HasPanel {

	void setPresenter(Presenter presenter);

	void setTemplates(PersistableLootTemplate[] persistableLootTemplates);

	void setTreeRootNode(TreeNode rootNode);

	void setEditorPanel(JPanel editorPanel);

	void refreshTree();

	public interface Presenter {

		void onViewTemplate(PersistableLootTemplate selectedItem);

		void onReload();

		void onNodeSelected(Object lastSelectedPathComponent);

		void onPersist();

		void createNewKofN(String name);

		void createNewMulti(String name);

		void createNewSingle(String name);
	}
}
