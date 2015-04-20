package se.spaced.server;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import se.spaced.server.guice.modules.PersistanceDaoModule;
import se.spaced.server.guice.modules.ResourcesModule;
import se.spaced.server.guice.modules.ServiceModule;
import se.spaced.server.guice.modules.SystemTimeModule;
import se.spaced.server.guice.modules.XStreamModule;

import java.util.Arrays;
import java.util.Collection;

public class GuiceFactory {
	private Module commandLineModule = new CommandLineParser().getModule();
	private Module serviceModule = new ServiceModule();
	private Module persistanceModule = new PersistanceDaoModule();
	private Module timeModule = new SystemTimeModule();
	private Module resourcesModule = new ResourcesModule();
	private Module xStreamModule = new XStreamModule();
	private final Collection<Module> customModules = Lists.newArrayList();


	public Injector createInjector() {
		Collection<Module> modules = Lists.newArrayList(Arrays.asList(
				commandLineModule,
				serviceModule,
				persistanceModule,
				timeModule,
				resourcesModule,
				xStreamModule));
		modules.addAll(customModules);

		return Guice.createInjector(modules);
	}

	public void setCommandLineModule(Module commandLineModule) {
		this.commandLineModule = commandLineModule;
	}

	public void setServiceModule(Module serviceModule) {
		this.serviceModule = serviceModule;
	}

	public void setPersistanceModule(Module persistanceModule) {
		this.persistanceModule = persistanceModule;
	}

	public void setTimeModule(Module timeModule) {
		this.timeModule = timeModule;
	}

	public void setResourcesModule(Module resourcesModule) {
		this.resourcesModule = resourcesModule;
	}

	public void setxStreamModule(Module xStreamModule) {
		this.xStreamModule = xStreamModule;
	}

	public void addCustomModule(Module customModule) {
		customModules.add(customModule);

	}
}
