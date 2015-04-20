package se.fearless.bender.services;

import java.net.URL;

public interface UrlService {
	String getShortUrl(String longUrl);

	String getShortUrl(URL longUrl);
}
