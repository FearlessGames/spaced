package se.spaced.client.ardor.ui.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import se.fearlessgames.common.lua.LuaVm;
import se.fearlessgames.common.util.TimeProvider;
import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.profiler.AggregatingProfiler;
import se.krka.kahlua.profiler.BufferedProfiler;
import se.krka.kahlua.profiler.Sampler;
import se.krka.kahlua.profiler.StacktraceNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Calendar;

@Singleton
public class PerformanceApi {
	private final LuaVm luaVm;
	private final TimeProvider timeProvider;

	private BufferedProfiler profiler;
	private Sampler sampler;

	@Inject
	public PerformanceApi(@Named("gui") LuaVm luaVm, TimeProvider timeProvider) {
		this.luaVm = luaVm;
		this.timeProvider = timeProvider;
	}

	@LuaMethod(global = true, name = "StartProfiler")
	public void startProfiler() {
		if (profiler == null) {
			profiler = new BufferedProfiler();
		}
		if (sampler == null) {
			sampler = new Sampler(luaVm.getThread(), 1, profiler);
		}
		sampler.start();
	}

	@LuaMethod(global = true, name = "StopProfiler")
	public void stopProfiler() {
		if (sampler != null) {
			sampler.stop();
		}
	}

	@LuaMethod(global = true, name = "ResetProfiler")
	public void resetProfiler() {
		stopProfiler();
		sampler = null;
		profiler = null;
	}

	@LuaMethod(global = true, name = "DumpProfiler")
	public void dumpProfiler() {
		if (profiler != null) {
			long now = timeProvider.now();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(now);
			String fileName = "profile-" + String.format("%04d%02d%02d-%02d%02d%02d",
					calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH),
					calendar.get(Calendar.HOUR_OF_DAY),
					calendar.get(Calendar.MINUTE),
					calendar.get(Calendar.SECOND)) + ".txt";
			File f = new File(fileName);
			AggregatingProfiler aggregatedProfiler = new AggregatingProfiler();
			profiler.sendTo(aggregatedProfiler);
			StacktraceNode stacktraceNode = aggregatedProfiler.toTree(100, 0.0, 10);
			try {
				PrintWriter writer = new PrintWriter(f);
				stacktraceNode.output(writer);
				writer.flush();
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}
