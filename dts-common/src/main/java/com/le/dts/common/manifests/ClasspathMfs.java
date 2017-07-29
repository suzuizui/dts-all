package com.le.dts.common.manifests;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;

public class ClasspathMfs implements Mfs {

	@Override
    public Collection<InputStream> fetch() throws IOException {
        final Enumeration<URL> resources = Thread.currentThread()
            .getContextClassLoader()
            .getResources("META-INF/MANIFEST.MF");
        final Collection<URI> uris = new LinkedList<URI>();
        while (resources.hasMoreElements()) {
            try {
                uris.add(resources.nextElement().toURI());
            } catch (final URISyntaxException ex) {
                throw new IOException(ex);
            }
        }
        final Collection<InputStream> streams =
            new ArrayList<InputStream>(uris.size());
        for (final URI uri : uris) {
            streams.add(uri.toURL().openStream());
        }
        return streams;
    }
	
}
