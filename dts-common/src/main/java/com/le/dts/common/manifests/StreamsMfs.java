package com.le.dts.common.manifests;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

public class StreamsMfs implements Mfs {

	/**
     * Streams.
     */
    private final transient Collection<InputStream> streams;

    /**
     * Ctor.
     * @param stream Stream
     */
    public StreamsMfs(final InputStream stream) {
        this(Collections.singleton(stream));
    }

    /**
     * Ctor.
     * @param list Files
     */
    public StreamsMfs(final Collection<InputStream> list) {
        this.streams = Collections.unmodifiableCollection(list);
    }

    @Override
    public Collection<InputStream> fetch() {
        return Collections.unmodifiableCollection(this.streams);
    }
	
}
