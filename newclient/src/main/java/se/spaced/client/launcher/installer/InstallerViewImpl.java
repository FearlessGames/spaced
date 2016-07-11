package se.spaced.client.launcher.installer;

import com.google.common.io.ByteSource;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.swing.BasicPanel;
import org.xhtmlrenderer.swing.FSMouseListener;
import org.xhtmlrenderer.swing.LinkListener;
import org.xhtmlrenderer.util.XRRuntimeException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

public class InstallerViewImpl extends JFrame implements InstallerView {
	private static final String TOTAL_DOWNLOAD = "Total downloaded: ";
	private static final double BASE = 1024;
	private static final double KB = BASE;
	private static final double MB = KB * BASE;
	private static final double GB = MB * BASE;

	private final DecimalFormat df = new DecimalFormat("#.00");

	private final JPanel mainPanel;
	private final JPanel subPanel;

	private JProgressBar progressBar;
	private JProgressBar bytesDownloadProgressBar;
	private JLabel progressText;
	private JLabel totalDownloadLabel;
	private JButton startButton;
	private JButton exitButton;
	private String totalDownloadSizeString = "0 bytes";
	private long totalDownloadSize;
	private JPanel buttonPanel;
	private XHTMLPanel xhtmlPanel;
	private final ByteSource noNewsResource;
	private JButton renderOptionsButton;


	public InstallerViewImpl(final Presenter presenter, ByteSource noNewsResource) {
		this.noNewsResource = noNewsResource;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		mainPanel = new JPanel(new BorderLayout(0, 0));
		subPanel = new JPanel(new GridLayout(5, 1));


		JLabel label = new JLabel("Welcome to Spaced - prepare for launch!");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		mainPanel.add(label, BorderLayout.PAGE_START);

		buildBrowserPane(presenter);
		buildProgressBars();
		buildButtons(presenter);

		mainPanel.add(subPanel, BorderLayout.PAGE_END);

		mainPanel.setBorder(BorderFactory.createTitledBorder("Fearlessgames.se presentes: Spaced"));
	}

	private void buildBrowserPane(final Presenter presenter) {
		xhtmlPanel = new XHTMLPanel();
		xhtmlPanel.setAutoscrolls(true);

		for (Object listener : xhtmlPanel.getMouseTrackingListeners()) {
			if (listener instanceof LinkListener) {
				xhtmlPanel.removeMouseTrackingListener((FSMouseListener) listener);
			}

		}
		xhtmlPanel.addMouseTrackingListener(new LinkListener() {
			@Override
			public void linkClicked(BasicPanel panel, String uri) {
				presenter.onHttpLink(uri);
			}
		});


		JScrollPane scroll = new JScrollPane(xhtmlPanel);
		scroll.setPreferredSize(new Dimension(800, 650));
		mainPanel.add(scroll, BorderLayout.CENTER);
	}

	private void buildProgressBars() {
		progressText = new JLabel();
		progressBar = new JProgressBar(0, 100);
		subPanel.add(progressText);
		subPanel.add(progressBar);


		bytesDownloadProgressBar = new JProgressBar(0, 100);
		totalDownloadLabel = new JLabel(TOTAL_DOWNLOAD);
		subPanel.add(totalDownloadLabel);
		subPanel.add(bytesDownloadProgressBar);
	}

	private void buildButtons(final Presenter presenter) {
		buttonPanel = new JPanel(new GridLayout(1, 3));

		startButton = new JButton("Start Spaced");
		startButton.addActionListener(new StartButtonActionListener(presenter));
		startButton.setVisible(false);

		renderOptionsButton = new JButton("Graphics Options");
		renderOptionsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				presenter.onShowRenderProperties();
			}
		});
		renderOptionsButton.setVisible(false);

		exitButton = new JButton("Exit");
		exitButton.setVisible(false);
		exitButton.addActionListener(new ExitButtonActionListener(presenter));

		buttonPanel.add(startButton);
		buttonPanel.add(renderOptionsButton);
		buttonPanel.add(exitButton);

		subPanel.add(buttonPanel);
	}

	@Override
	public void createAndShowUI() {
		add(mainPanel, BorderLayout.CENTER);

		setSize(800, 700);
		setLocationRelativeTo(null);
		setUndecorated(true);
		setVisible(true);
	}


	@Override
	public void close() {
		setVisible(false);
		dispose();
	}

	@Override
	public void setNrOfActions(int i) {
		progressBar.setMaximum(i);
	}

	@Override
	public void updateProgress(int i) {
		progressBar.setValue(i);
	}

	@Override
	public void updateProgressText(String text) {
		progressText.setText(text);
	}

	@Override
	public void updateBytesDownloaded(long total) {
		totalDownloadLabel.setText(TOTAL_DOWNLOAD + formatSize(total) + "/" + totalDownloadSizeString);
		double d = (double) total / (double) totalDownloadSize;
		d *= 100;
		bytesDownloadProgressBar.setValue((int) d);
	}

	@Override
	public void setTotalSize(long totalDownloadSize) {
		this.totalDownloadSizeString = formatSize(totalDownloadSize);
		this.totalDownloadSize = totalDownloadSize;
	}

	@Override
	public void setNewsPage(String url) {
		try {
			xhtmlPanel.setDocument(url);
		} catch (XRRuntimeException e) {
			try {
				xhtmlPanel.setDocument(noNewsResource.openBufferedStream(), ".");
			} catch (Exception e1) {
				showError("Failed to display noNews page", e1);
			}
		}
	}

	@Override
	public void setDone() {
		startButton.setVisible(true);
		renderOptionsButton.setVisible(true);
		exitButton.setVisible(true);

		progressText.setText("Download complete");
		totalDownloadLabel.setText(TOTAL_DOWNLOAD + totalDownloadSizeString);
		progressBar.setMaximum(1);
		progressBar.setValue(1);
		bytesDownloadProgressBar.setMaximum(1);
		bytesDownloadProgressBar.setValue(1);

	}

	@Override
	public void showError(String message, Exception e) {
		JDialog dialog = new JDialog(this, "FAIL", true);
		dialog.add(new JLabel(message));
		if (e != null) {
			dialog.add(new JLabel(e.getMessage()));
		}
		dialog.setSize(100, 100);


		dialog.setVisible(true);
	}

	private String formatSize(double size) {

		if (size >= GB) {
			return df.format(size / GB) + " GB";
		}
		if (size >= MB) {
			return df.format(size / MB) + " MB";
		}
		if (size >= KB) {
			return df.format(size / KB) + " KB";
		}
		return "" + (int) size + " bytes";
	}

	private static class ExitButtonActionListener implements ActionListener {
		private final Presenter presenter;

		private ExitButtonActionListener(Presenter presenter) {
			this.presenter = presenter;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			presenter.onExitButton();
		}
	}

	private class StartButtonActionListener implements ActionListener {
		private final Presenter presenter;

		private StartButtonActionListener(Presenter presenter) {
			this.presenter = presenter;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			subPanel.remove(buttonPanel);
			subPanel.remove(progressBar);
			subPanel.remove(progressText);
			subPanel.remove(totalDownloadLabel);
			subPanel.remove(bytesDownloadProgressBar);
			JLabel label = new JLabel("Starting Spaced - please wait");
			label.setFont(new Font("Arial", Font.BOLD, 20));
			label.setHorizontalAlignment(SwingConstants.CENTER);
			subPanel.add(label);
			subPanel.validate();
			mainPanel.validate();
			mainPanel.repaint();
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					presenter.onStartButton();
				}
			});

		}
	}

}
