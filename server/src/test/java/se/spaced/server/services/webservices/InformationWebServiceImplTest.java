package se.spaced.server.services.webservices;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Test;
import se.fearlessgames.common.lifetime.LifetimeManager;
import se.fearlessgames.common.util.SystemTimeProvider;
import se.mockachino.annotations.*;
import se.spaced.server.account.AccountService;
import se.spaced.server.model.action.ActionScheduler;
import se.spaced.server.net.RemoteServer;
import se.spaced.server.services.PlayerConnectedServiceImpl;
import se.spaced.shared.network.webservices.informationservice.InformationWebService;
import se.spaced.shared.network.webservices.informationservice.ServerInfo;
import se.spaced.shared.network.webservices.informationservice.ServerStatus;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;


public class InformationWebServiceImplTest {

	@Mock
	private AccountService accountService;

	@Test
	public void testWebServices() throws InterruptedException {
		setupMocks(this);
		WebServicePublisher webServicePublisher = new WebServicePublisherImpl("http://localhost:9100/",
				9100,
				mock(LifetimeManager.class));
		RemoteServer remoteServer = mock(RemoteServer.class);
		when(remoteServer.isRunning()).thenReturn(true);
		InformationWebServiceImpl impl = new InformationWebServiceImpl(new SystemTimeProvider(),
				new PlayerConnectedServiceImpl(),
				accountService,
				remoteServer,
				mock(ActionScheduler.class),
				1234);
		webServicePublisher.publish(impl, "InformationService");

		//first try directly with the class. No web
		ServerInfo localServerInfo = impl.getServerStatus();
		assertEquals(ServerStatus.ONLINE, localServerInfo.getServerStatus());
		//now try web
		ServerInfo result = call();
		System.out.println("ServerStatus: " + result.getServerStatus());
		assertEquals(ServerStatus.ONLINE, result.getServerStatus());

		assertEquals(1234, result.getGameServerPort());
	}


	private ServerInfo call() {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(InformationWebService.class);
		factory.setAddress("http://localhost:9100/InformationService");
		InformationWebService client = (InformationWebService) factory.create();
		ServerInfo result = client.getServerStatus();
		return result;
	}

	public static void main(String[] args) throws InterruptedException {
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(InformationWebService.class);
		factory.setAddress("http://localhost:9000/InformationService");
		InformationWebService client = (InformationWebService) factory.create();

		while (true) {
			System.out.println(client.getServerStatus());
			System.out.println(client.getServerMetrics());
			Thread.sleep(500);
		}
	}
}
