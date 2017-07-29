package com.le.dts.common.manifests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class FilesMfs implements Mfs {

	/**
     * Files.
     */
    private final transient Collection<File> files;

    /**
     * Ctor.
     * @param file File
     */
    public FilesMfs(final File file) {
        this(Collections.singleton(file));
    }

    /**
     * Ctor.
     * @param list Files
     */
    public FilesMfs(final Collection<File> list) {
        this.files = Collections.unmodifiableCollection(list);
    }

    @Override
    public Collection<InputStream> fetch() throws IOException {
        final Collection<InputStream> streams = new ArrayList<InputStream>(1);
        for (final File file : this.files) {
            streams.add(file.toURI().toURL().openStream());
        }
        return streams;
    }
	
}
