package se.spaced.server;

import com.google.common.collect.Lists;
import com.google.inject.Injector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearlessgames.common.log.Slf4jJulBridge;
import se.spaced.server.guice.modules.AppearanceListenerDispatcherConnector;
import se.spaced.server.guice.modules.AuraUpdateListenerDispatcherConnector;
import se.spaced.server.guice.modules.EntityListenerDispatcherConnector;
import se.spaced.server.guice.modules.SpawnListenerDispatcherConnector;
import se.spaced.server.guice.modules.TargetUpdateListenerDispatcherConnector;
import se.spaced.server.mob.MobController;
import se.spaced.server.net.RemoteServer;
import se.spaced.server.persistence.migrator.BotAccountPopulator;
import se.spaced.server.persistence.migrator.DevAccountPopulator;
import se.spaced.server.persistence.migrator.Migrator;
import se.spaced.server.persistence.migrator.MigratorService;
import se.spaced.server.persistence.migrator.ServerContentPopulator;
import se.spaced.server.services.webservices.WebServicePublisher;
import se.spaced.server.services.webservices.external.BroadcastWebService;
import se.spaced.server.services.webservices.external.EntityWebService;
import se.spaced.server.services.webservices.external.KillStatisticsWebService;
import se.spaced.server.services.webservices.external.SpellStatisticsWebService;
import se.spaced.shared.network.webservices.admin.SpellAdminWebService;
import se.spaced.shared.network.webservices.informationservice.InformationWebService;
import se.spaced.shared.util.guice.dependencytool.DependencyTool;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Starter {
	private static final int REMOTE_SERVER_TIMEOUT = 200;

	private Starter() {
	}

	static {
		Slf4jJulBridge.init();
	}

	static final Logger log = LoggerFactory.getLogger(Starter.class);

	public static void main(final String[] args) throws Exception {
		log.info("Starting server...	");
		CommandLineParser commandLineParser = new CommandLineParser();
		commandLineParser.parse(args);

		GuiceFactory guiceFactory = new GuiceFactory();
		guiceFactory.setCommandLineModule(commandLineParser.getModule());

		final Injector injector = guiceFactory.createInjector();

		MigratorService migratorService = injector.getInstance(MigratorService.class);
		List<Migrator> migrators = Lists.newArrayList();
		migrators.add(injector.getInstance(ServerContentPopulator.class));
		migrators.add(injector.getInstance(DevAccountPopulator.class));

		try {
			new DependencyTool(injector,
					getWriter("Starter.dependency.txt"),
					getWriter("Starter.dependency.dot")).process();
		} catch (IOException e) {
			log.error("Failed to run DependencyTool", e);
		}

		setupGuiceWorkarounds(injector);

		startWebServices(injector);

		log.info("Running migrators... ");
		migratorService.runMigrators(migrators);
		log.info("Running migrators... done");

		final SpacedServer spacedServer = injector.getInstance(SpacedServer.class);
		ServerSetup setup = injector.getInstance(ServerSetup.class);

		RemoteServer remoteServer = injector.getInstance(RemoteServer.class);
		remoteServer.setTimeout(REMOTE_SERVER_TIMEOUT);
		remoteServer.startup();

		final Thread t = new Thread(spacedServer, "SpacedMainThread");
		MobController mobController = injector.getInstance(MobController.class);
		Thread mobAi = new Thread(mobController, "MobAI");

		mobAi.start();
		t.start();

		setup.setup();


		migrators.clear();
		migrators.add(injector.getInstance(BotAccountPopulator.class));
		migratorService.runMigrators(migrators);

		log.info("Starter all done!");

	}

	private static void setupGuiceWorkarounds(Injector injector) {
		SpyService spyService = injector.getInstance(SpyService.class);
		EntityListenerDispatcherConnector entityListenerDispatcherConnector = injector.getInstance(
				EntityListenerDispatcherConnector.class);
		AuraUpdateListenerDispatcherConnector auraUpdateListenerDispatcherConnector = injector.getInstance(
				AuraUpdateListenerDispatcherConnector.class);
		AppearanceListenerDispatcherConnector appearanceListenerDispatcherConnector = injector.getInstance(
				AppearanceListenerDispatcherConnector.class);
		TargetUpdateListenerDispatcherConnector targetUpdateListenerDispatcherConnector = injector.getInstance(TargetUpdateListenerDispatcherConnector.class);
		SpawnListenerDispatcherConnector spawnListenerDispatcherConnector = injector.getInstance(SpawnListenerDispatcherConnector.class);
	}

	private static PrintWriter getWriter(String name) throws IOException {
		return new PrintWriter(new FileWriter(name));
	}

	private static void startWebServices(Injector injector) {
		//configure and start a webservice. Maybe this should be done someplace else?

		WebServicePublisher webServicePublisher = injector.getInstance(WebServicePublisher.class);
		webServicePublisher.publish(injector.getInstance(InformationWebService.class), "InformationService");
		webServicePublisher.publish(injector.getInstance(SpellAdminWebService.class), "SpellAdminService");
		webServicePublisher.publish(injector.getInstance(KillStatisticsWebService.class), "KillStatisticsService");
		webServicePublisher.publish(injector.getInstance(EntityWebService.class), "EntityService");
		webServicePublisher.publish(injector.getInstance(SpellStatisticsWebService.class), "SpellStatisticsService");
		webServicePublisher.publish(injector.getInstance(BroadcastWebService.class), "BroadcastService");

		log.info("WebServices4Spaced started");
	}

}
