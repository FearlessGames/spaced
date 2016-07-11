package se.spaced.server.net.listeners.auth;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;
import se.fearless.common.uuid.UUID;
import se.mockachino.annotations.Mock;
import se.mockachino.matchers.matcher.ArgumentCatcher;
import se.spaced.messages.protocol.Salts;
import se.spaced.shared.util.ListenerDispatcher;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;
import static se.mockachino.matchers.Matchers.any;
import static se.mockachino.matchers.Matchers.match;
import static se.mockachino.matchers.MatchersBase.mAny;

@SuppressWarnings("unchecked")
public class FameAuthenticatorServiceTest {
	@Mock
	private ListenerDispatcher<AuthCallback> dispatcher;
	@Mock
	private DefaultHttpClient client;
	@Mock
	private ClientConnectionManager conMgrMock;
	@Mock
	private AuthCallback authCallback;
	private final String address = "127.0.0.1";

	@Before
	public void setUp() throws Exception {
		setupMocks(this);
		when(client.getConnectionManager()).thenReturn(conMgrMock);
		when(dispatcher.trigger()).thenReturn(authCallback);
	}

	@Test
	public void testRequestAuthSaltsWithResponse200ShouldReturnAuthToken() throws IOException {
		AuthSaltsCallback callback = mock(AuthSaltsCallback.class);
		FameAuthenticatorService authService = new FameAuthenticatorService(client);
		when(client.execute(any(HttpUriRequest.class),
				any(ResponseHandler.class))).thenReturn(new FameAuthenticatorService.FameHttpResponse(200,
				"USERSALT:ONETIMESALT"));
		authService.requestAuthSalts("username", callback);
		ArgumentCatcher<Salts> catcher = ArgumentCatcher.create(mAny(Salts.class));
		verifyOnce().on(callback).receievedSalts(match(catcher));
		verifyOnce().on(conMgrMock).closeExpiredConnections();
		assertEquals("ONETIMESALT", catcher.getValue().getOneTimeSalt());
		assertEquals("USERSALT", catcher.getValue().getUserSalt());
	}

	@Test
	public void test200responseOnAuthShouldTriggerOkWithType0ExternalAccount() throws IOException {
		FameAuthenticatorService authService = new FameAuthenticatorService(client);
		String expectedUUID = "a33900c3-5562-4be9-ad76-9f56df68d79d";
		when(client.execute(any(HttpUriRequest.class),
				any(ResponseHandler.class))).thenReturn(new FameAuthenticatorService.FameHttpResponse(200, expectedUUID));
		authService.authenticate("accountName", "hashish", address, authCallback);
		ArgumentCatcher<ExternalAccount> catcher = ArgumentCatcher.create(mAny(ExternalAccount.class));
		verifyOnce().on(authCallback).successfullyLoggedIn("accountName", match(catcher));
		assertEquals(0, catcher.getValue().getType());
		assertEquals(UUID.fromString(expectedUUID), catcher.getValue().getUuid());

	}

	@Test
	public void test200responseOnAuthShouldTriggerOkWithDifferentTypeExternalAccount() throws IOException {
		FameAuthenticatorService authService = new FameAuthenticatorService(client);
		String expectedUUID = "a33900c3-5562-4be9-ad76-9f56df68d79d";
		String expectedUUIDWithType = expectedUUID + "/1";
		when(client.execute(any(HttpUriRequest.class),
				any(ResponseHandler.class))).thenReturn(new FameAuthenticatorService.FameHttpResponse(200,
				expectedUUIDWithType));
		authService.authenticate("accountName", "hashish", address, authCallback);
		ArgumentCatcher<ExternalAccount> catcher = ArgumentCatcher.create(mAny(ExternalAccount.class));
		verifyOnce().on(authCallback).successfullyLoggedIn("accountName", match(catcher));
		assertEquals(1, catcher.getValue().getType());
		assertEquals(UUID.fromString(expectedUUID), catcher.getValue().getUuid());
	}

	@Test
	public void test401ShouldTriggerLoginFailed() throws IOException {
		FameAuthenticatorService authService = new FameAuthenticatorService(client);
		when(client.execute(any(HttpUriRequest.class),
				any(ResponseHandler.class))).thenReturn(new FameAuthenticatorService.FameHttpResponse(401,
				"FOOL"));
		authService.authenticate("accountName", "hashish", address, authCallback);
		verifyNever().on(authCallback).successfullyLoggedIn(any(String.class), any(ExternalAccount.class));
		verifyOnce().on(authCallback).authenticationFailed("accountName");
	}

	@Test
	public void test403ShouldTriggerBasicAuthFailed() throws IOException {
		FameAuthenticatorService authService = new FameAuthenticatorService(client);
		when(client.execute(any(HttpUriRequest.class),
				any(ResponseHandler.class))).thenReturn(new FameAuthenticatorService.FameHttpResponse(403,
				"FOOL"));
		authService.authenticate("accountName", "hashish", address, authCallback);
		verifyNever().on(authCallback).successfullyLoggedIn(any(String.class), any(ExternalAccount.class));
		verifyOnce().on(authCallback).basicAuthFailed("accountName");
	}

}
