package com.le.dts.common.manifests;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServletMfs implements Mfs {

	private static final Log logger = LogFactory.getLog(ServletMfs.class);
	
	/**
     * Servlet context.
     */
    private final transient ServletContext ctx;

    /**
     * Ctor.
     * @param context Context
     */
    public ServletMfs(final ServletContext context) {
        this.ctx = context;
    }

    @Override
    public Collection<InputStream> fetch() throws IOException {
        final URL main = this.ctx.getResource("/META-INF/MANIFEST.MF");
        final Collection<InputStream> streams = new ArrayList<InputStream>(1);
        if (main == null) {
        	logger.warn("MANIFEST.MF not found in WAR package");
        } else {
            streams.add(main.openStream());
        }
        return streams;
    }
	
}
