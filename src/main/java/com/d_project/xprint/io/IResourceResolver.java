package com.d_project.xprint.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * IResourceResolver
 * @author Kazuhiko Arase
 */
public interface IResourceResolver {
	InputStream getResourceAsStream(String path) throws IOException;
}