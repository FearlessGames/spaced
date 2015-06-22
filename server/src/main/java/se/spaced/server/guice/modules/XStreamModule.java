package se.spaced.server.guice.modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import se.fearlessgames.common.io.StreamLocator;
import se.spaced.client.resources.zone.ZoneXmlFileHandler;
import se.spaced.client.resources.zone.ZoneXmlReader;
import se.spaced.server.persistence.util.ServerXStreamRegistry;
import se.spaced.shared.xml.XStreamIO;
import se.spaced.shared.xml.XmlIO;

import javax.inject.Singleton;

public class XStreamModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(ZoneXmlReader.class).to(ZoneXmlFileHandler.class).in(Scopes.SINGLETON);
	}

	@Provides
	@Singleton
	public XStream getXStream() {
		ServerXStreamRegistry serverXStreamRegistry = new ServerXStreamRegistry();
		XStream xStream = new XStream(new DomDriver());
		serverXStreamRegistry.registerDefaultsOn(xStream);
		return xStream;
	}

	@Provides
	@Singleton
	public XmlIO getXmlIO(XStream xStream, StreamLocator streamLocator) {
		return new XStreamIO(xStream, streamLocator);
	}
}
