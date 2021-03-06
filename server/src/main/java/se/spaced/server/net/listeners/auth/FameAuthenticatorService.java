package se.spaced.server.net.listeners.auth;

import com.google.inject.Inject;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.fearless.common.uuid.UUID;
import se.spaced.messages.protocol.Salts;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class FameAuthenticatorService implements AuthenticatorService {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final DefaultHttpClient client;
	private static final String HOST = "fame.fearlessgames.se";
	private static final String AUTH_PATH = "/api/auth/spaced.html";
	private static final String REQUEST_SALT_PATH = "/api/auth/requestSalts.html";
	private static final String AUTHENTICATOR_PATH = "/api/auth/spacedauthenticator.html";
	private static final String BASIC_UNAME = "remote";
	private static final String BASIC_PWD = "remote";
	private static final String SERVICE_KEY_SPACED = "omagahd";


	@Inject
	public FameAuthenticatorService(DefaultHttpClient client) {
		this.client = client;
	}

	@Override
	public void requestAuthSalts(String userName, final AuthSaltsCallback callback) {
		try {
			URIBuilder uriBuilder = new URIBuilder();
			uriBuilder
					.setHost(HOST)
					.setPort(-1)
					.setScheme("http")
					.setPath(REQUEST_SALT_PATH)
					.addParameter("userName", userName);
			URI uri = uriBuilder.build();
			Map<Integer, FameHttpResponseHandler> responseMapper = new HashMap<Integer, FameHttpResponseHandler>();

			responseMapper.put(200, body -> callback.receievedSalts(Salts.fromString(body)));

			get(uri, responseMapper);

		} catch (URISyntaxException e) {
			log.error("could not encode parameters", e);
		}

	}

	@Override
	public void authenticateWithAuthenticator(String accountName, String hash, String address, String key, AuthCallback authCallback) {
		sendAuthentication(accountName, hash, address, authCallback, key, AUTHENTICATOR_PATH);
	}

	@Override
	public void authenticate(final String accountName, String hash, String address, final AuthCallback callback) {
		sendAuthentication(accountName, hash, address, callback, null, AUTH_PATH);
	}

	private void sendAuthentication(String accountName, String hash, String address, AuthCallback callback,
	                                String authenticatorKey, String authPath) {

		try {
			URIBuilder uriBuilder = new URIBuilder();
			uriBuilder
					.setScheme("http")
					.setHost(HOST)
					.setPort(-1)
					.setPath(authPath)
					.addParameter("userName", accountName)
					.addParameter("hash", hash)
					.addParameter("serviceKey", SERVICE_KEY_SPACED)
					.addParameter("ip", address);
			if (authenticatorKey != null) {
				uriBuilder.addParameter("authenticatorHash", authenticatorKey);
			}
			URI uri = uriBuilder.build();
			log.info("Auth using {}", uri);

			Map<Integer, FameHttpResponseHandler> responseMapper = new HashMap<Integer, FameHttpResponseHandler>();

			responseMapper.put(200, body -> {
				if (body.contains("/")) {
					String[] split = body.split("/");
					UUID uuid = UUID.fromString(split[0]);
					int type = Integer.parseInt(split[1]);
					callback.successfullyLoggedIn(accountName, new ExternalAccount(uuid, type));
				} else {
					callback.successfullyLoggedIn(accountName, new ExternalAccount(UUID.fromString(body), 0));
				}
			});

			responseMapper.put(401, body -> callback.authenticationFailed(accountName));


			responseMapper.put(403, body -> callback.basicAuthFailed(accountName));

			responseMapper.put(100, body -> callback.needsAuthenticator());

			responseMapper.put(500, body -> {
				throw new RuntimeException(body);
			});


			get(uri, responseMapper);


		} catch (URISyntaxException e) {
			log.error("could not encode parameters", e);
			throw new RuntimeException("could not encode parameters");
		}
	}


	private void get(URI uri, Map<Integer, FameHttpResponseHandler> responseMapper) {
		FameHttpResponse response;
		try {
			HttpGet get = new HttpGet(uri);
			doBasicAuth(get);
			response = doCall(get);
		} catch (IOException e) {
			response = new FameHttpResponse(500, e.getMessage());
		} catch (AuthenticationException e) {
			response = new FameHttpResponse(403, "Basic auth failed");
		} finally {
			client.getConnectionManager().closeExpiredConnections();
		}
		if (!responseMapper.containsKey(response.getCode())) {
			throw new RuntimeException("Encountered unhandled http code " + response.getCode());
		}

		responseMapper.get(response.getCode()).handleResponse(response.getBody());
	}

	private FameHttpResponse doCall(HttpUriRequest request) throws IOException {
		log.info("Trying to auth to fame");
		ResponseHandler<FameHttpResponse> responseHandler = new ResponseHandlerWithCode();
		return client.execute(request, responseHandler);
	}

	private void doBasicAuth(HttpRequest httpRequest) throws AuthenticationException {
		BasicScheme scheme = new BasicScheme();
		Header authHeader = scheme.authenticate(new UsernamePasswordCredentials(BASIC_UNAME, BASIC_PWD), httpRequest, new BasicHttpContext());
		httpRequest.setHeader(authHeader);
	}

	private static class ResponseHandlerWithCode implements ResponseHandler<FameHttpResponse> {
		@Override
		public FameHttpResponse handleResponse(HttpResponse response) throws IOException {
			int code = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();
			return new FameHttpResponse(code, EntityUtils.toString(entity));
		}
	}

	static class FameHttpResponse {
		private final int code;
		private final String body;

		public FameHttpResponse(int code, String body) {
			this.code = code;
			this.body = body;
		}

		public int getCode() {
			return code;
		}

		public String getBody() {
			return body;
		}
	}

	private interface FameHttpResponseHandler {
		void handleResponse(String body);
	}
}
