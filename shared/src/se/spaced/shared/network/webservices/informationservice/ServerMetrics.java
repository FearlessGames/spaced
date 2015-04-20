package se.spaced.shared.network.webservices.informationservice;

public class ServerMetrics {
	private long delay;

	public ServerMetrics() {
	}

	public ServerMetrics(long delay) {
		this.delay = delay;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	@Override
	public String toString() {
		return "ServerMetrics{" +
				"delay=" + delay +
				'}';
	}
}
