package se.spaced.client.tools;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.fearless.common.lifetime.LifetimeListener;
import se.fearless.common.lifetime.LifetimeManager;
import se.fearless.common.lua.LuaVm;
import se.krka.kahlua.j2se.interpreter.InteractiveShell;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@Singleton
public class Interpreter implements LifetimeListener {
	private final LuaVm luaVm;

	private InteractiveShell shell;
	private JFrame frame;

	@Inject
	public Interpreter(@Named("gui") LuaVm luaVm, LifetimeManager lifetimeManager) {
		this.luaVm = luaVm;
		lifetimeManager.addListener(this);
	}

	public synchronized void showInterpreter() {
		if (shell == null) {
			frame = new JFrame("GUI Interpreter");
			frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			shell = new InteractiveShell(luaVm.getKahlua(), frame);
			shell.getExit().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionEvent) {
					frame.setVisible(false);
				}
			});
		}
		frame.setVisible(true);
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onShutdown() {
		if (frame != null) {
			frame.setVisible(false);
			frame.dispose();
		}
	}
}
