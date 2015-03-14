package com.d_project.xprint.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.d_project.xprint.core.Config;

/**
 * DefaultResourceResolver
 * @author Kazuhiko Arase
 */
public class DefaultResourceResolver implements IResourceResolver {
	
	private URL baseUrl;
	
	public DefaultResourceResolver(URL baseUrl) {
		this.baseUrl = baseUrl;
	}

	public InputStream getResourceAsStream(String path) throws IOException {
		boolean allowCrossDomain = Config.getInstance().allowCrossDomain();
		if (!allowCrossDomain) {
			if (baseUrl == null) {
				throw new SecurityException(
					"bad path " + path);
			}
		}
		URL newUrl = buildNewUrl(path);
		if (!allowCrossDomain) {
			if (!baseUrl.getProtocol().equals(newUrl.getProtocol() ) ||
					!baseUrl.getHost().equals(newUrl.getHost() ) ||
					baseUrl.getPort() != newUrl.getPort() ) {
				throw new SecurityException(
					"bad path " + path + " for baseUrl " + baseUrl);
			}
		}
		return newUrl.openStream();
	}

	private URL buildNewUrl(String path) throws IOException {
		if (baseUrl != null) {
			return new URL(baseUrl, path);
		}
		return new URL(path);
	}
}