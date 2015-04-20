package se.fearlessgames.prototyping.ui;

import com.google.common.collect.Lists;

import java.util.List;

public class TickService {
	private List<TickListener> tickListeners = Lists.newArrayList();

	public void tick() {
		try {
			for (TickListener tickListener : tickListeners) {
				tickListener.tick();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public TickHandler addTickListener(TickListener tickListener) {
		TickHandler tickHandler = new TickHandler(tickListener);
		tickListeners.add(tickListener);
		return tickHandler;
	}

	public class TickHandler {
		private final TickListener tickListener;

		public TickHandler(TickListener tickListener) {
			this.tickListener = tickListener;
		}

		void remove() {
			tickListeners.remove(tickListener);
		}
	}

}
