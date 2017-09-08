package se.spaced.client.settings.ui;

class SampleValue {
	private final int samples;

	SampleValue(int samples) {
		this.samples = samples;
	}

	public int getValue() {
		return samples;
	}

	@Override
	public String toString() {
		return String.valueOf(samples) + 'x';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof SampleValue)) {
			return false;
		}

		SampleValue that = (SampleValue) o;

		return samples == that.samples;
	}

	@Override
	public int hashCode() {
		return samples;
	}
}
