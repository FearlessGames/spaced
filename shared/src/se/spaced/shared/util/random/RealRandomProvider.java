package se.spaced.shared.util.random;

import com.google.inject.Inject;
import se.spaced.shared.util.math.interval.IntervalInt;

import java.util.Random;

public class RealRandomProvider implements RandomProvider {

	private final Random random;

	@Inject
	public RealRandomProvider() {
		this(new Random());
	}
	
	public RealRandomProvider(Random random) {
		this.random = random;
	}

	@Override
	public double getDouble(double min, double max) {
		return min + (random.nextDouble() * (max - min));
	}

	@Override
	public int getInteger(int min, int max) {
		return min + random.nextInt(1 + max - min);
	}

	@Override
	public int getInteger(IntervalInt range) {
		return getInteger(range.getStart(), range.getEnd());
	}


}
