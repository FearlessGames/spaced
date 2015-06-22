package se.spaced.server.tools.loot.xml;

import com.google.inject.Inject;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LootXmlViewImpl extends JPanel implements LootXmlView {
	private final JTextArea textArea;
	private Presenter presenter;

	@Inject
	public LootXmlViewImpl() {
		setLayout(new BorderLayout());
		JPanel upperPanel = new JPanel(new FlowLayout());

		textArea = new JTextArea();


		JButton createButton = new JButton("Create XML");
		createButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (presenter != null) {
					presenter.onCreateAndShowXml();
				}
			}
		});

		upperPanel.add(createButton);

		add(upperPanel, BorderLayout.NORTH);
		add(new JScrollPane(textArea), BorderLayout.CENTER);
	}

	@Override
	public void setXml(String xml) {
		textArea.setText(xml);
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public Component getPanel() {
		return this;
	}
}
