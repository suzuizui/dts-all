package com.le.dts.common.manifests;

import java.io.IOException;
import java.util.Map;

public interface MfMap extends Map<String, String> {

	/**
     * Append this collection of MANIFEST.MF files.
     * @param streams Files to append
     * @return This
     * @since 1.0
     * @throws IOException If fails on I/O problem
     */
    MfMap append(Mfs streams) throws IOException;
	
}
