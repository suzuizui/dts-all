package com.le.dts.common.manifests;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public interface Mfs {

	/**
     * Find and fetch them all.
     * @return Iterator of found resources
     * @throws IOException If fails
     */
    Collection<InputStream> fetch() throws IOException;
	
}
