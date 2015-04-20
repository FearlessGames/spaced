package se.fearless.bender.services;

import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static se.mockachino.Mockachino.*;

public class TinyUrlServiceTest {
	private WGet wget;
	private URL tinyUrl;
	private String fearlessUrl;
	private TinyUrlService tinyUrlService;

	@Before
	public void setup() throws MalformedURLException {
		wget = mock(WGet.class);
		fearlessUrl = "https://flexo.fearlessgames.se/websvn/comp.php?repname=Spaced&compare[]=%2F@7717&compare[]=%2F@7718";
		tinyUrl = new URL(
				"http://tinyurl.com/api-create.php?url=https://flexo.fearlessgames.se/websvn/comp.php?repname=Spaced&compare[]=%2F@7717&compare[]=%2F@7718");

		tinyUrlService = new TinyUrlService(wget);
	}

	@Test
	public void testGetShortUrl() throws Exception {
		when(wget.getContent(tinyUrl)).thenReturn("http://tinyurl.com/5rruro3");
		assertEquals("http://tinyurl.com/5rruro3", tinyUrlService.getShortUrl(fearlessUrl));
	}

	@Test
	public void testGetShortUrlWithNewLinePrefix() throws Exception {
		when(wget.getContent(tinyUrl)).thenReturn("\nhttp://tinyurl.com/5rruro3");
		assertEquals("http://tinyurl.com/5rruro3", tinyUrlService.getShortUrl(fearlessUrl));
	}

	@Test
	public void testGetShortUrlWithPrefix() throws Exception {
		when(wget.getContent(tinyUrl)).thenReturn("\nasdasd\r\nhttp://tinyurl.com/5rruro3");
		assertEquals("http://tinyurl.com/5rruro3", tinyUrlService.getShortUrl(fearlessUrl));
	}


}
