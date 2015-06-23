package se.spaced.client.tools;

import se.krka.kahlua.j2se.Kahlua;
import se.krka.kahlua.j2se.interpreter.InteractiveShell;
import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.LuaCallFrame;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;
import java.awt.ScrollPane;

public class LuaTableView extends JPanel {

	private final JTree tree;
	private final DefaultTreeModel treeModel;

	public LuaTableView(KahluaTable table) {
		setLayout(new BorderLayout());
		treeModel = new DefaultTreeModel(new LuaTableTreeNode(table.toString(), table));
		tree = new JTree(treeModel);
		ScrollPane scrollPane = new ScrollPane();
		scrollPane.add(tree);
		add(scrollPane, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		final JFrame f1 = new JFrame();
		f1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final Kahlua kahlua = new Kahlua();
		InteractiveShell shell = new InteractiveShell(kahlua, f1);
		f1.setSize(800, 600);
		f1.setVisible(true);

		kahlua.getEnvironment().rawset("Browse", new JavaFunction() {
			@Override
			public int call(LuaCallFrame luaCallFrame, int i) {
				Object table = luaCallFrame.get(0);
				JFrame f2 = new JFrame(table.toString());
				f2.setSize(300, 800);
				f2.setLocation((int) f1.getLocation().getX() + f1.getWidth(), (int) f1.getLocation().getY());
				LuaTableView tableView = new LuaTableView((KahluaTable) table);
				f2.getContentPane().add(new JScrollPane(tableView));
				f2.setVisible(true);
				return 0;
			}
		});

	}
}


