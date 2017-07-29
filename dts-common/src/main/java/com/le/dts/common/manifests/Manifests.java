package com.le.dts.common.manifests;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Manifests implements MfMap {

	private static final Log logger = LogFactory.getLog(Manifests.class);
	
	/**
     * Default singleton.
     */
    public static final MfMap DEFAULT = new Manifests();

    /**
     * Attributes retrieved.
     */
    private final transient Map<String, String> attributes;

    static {
        try {
            Manifests.DEFAULT.append(new ClasspathMfs());
        } catch (final IOException ex) {
        	logger.error("#load(): '%s' failed %[exception]s", ex);
        }
    }

    /**
     * Public ctor.
     * @since 1.0
     */
    public Manifests() {
        this(new HashMap<String, String>(0));
    }

    /**
     * Public ctor.
     * @param attrs Attributes to encapsulate
     * @since 1.0
     */
    public Manifests(final Map<String, String> attrs) {
        super();
        this.attributes = new ConcurrentHashMap<String, String>(attrs);
    }

    @Override
    public int size() {
        return this.attributes.size();
    }

    @Override
    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.attributes.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return this.attributes.containsValue(value);
    }

    @Override
    public String get(final Object key) {
        return this.attributes.get(key);
    }

    @Override
    public String put(final String key, final String value) {
        return this.attributes.put(key, value);
    }

    @Override
    public String remove(final Object key) {
        return this.attributes.remove(key);
    }

    @Override
    public void putAll(final Map<? extends String, ? extends String> attrs) {
        this.attributes.putAll(attrs);
    }

    @Override
    public void clear() {
        this.attributes.clear();
    }

    @Override
    public Set<String> keySet() {
        return this.attributes.keySet();
    }

    @Override
    public Collection<String> values() {
        return this.attributes.values();
    }

    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        return this.attributes.entrySet();
    }

    @Override
    public MfMap append(final Mfs streams) throws IOException {
        final long start = System.currentTimeMillis();
        final Collection<InputStream> list = streams.fetch();
        int saved = 0;
        int ignored = 0;
        for (final InputStream stream : list) {
            for (final Map.Entry<String, String> attr
                : Manifests.load(stream).entrySet()) {
                if (this.attributes.containsKey(attr.getKey())) {
                    ++ignored;
                } else {
                    this.attributes.put(attr.getKey(), attr.getValue());
                    ++saved;
                }
            }
        }
        logger.info("%d attributes loaded from %d stream(s) in %[ms]s, %d saved, %d ignored: %[list]s" + 
            this.attributes.size() + "---" + list.size() + "---" + 
            (System.currentTimeMillis() - start) + "---" + 
            saved + "---" + ignored + "---" + 
            new TreeSet<String>(this.attributes.keySet())
        );
        return this;
    }

    /**
     * Read one attribute available in one of {@code MANIFEST.MF} files.
     *
     * <p>If such a attribute doesn't exist {@link IllegalArgumentException}
     * will be thrown. If you're not sure whether the attribute is present or
     * not use {@link #exists(String)} beforehand.
     *
     * <p>The method is thread-safe.
     *
     * @param name Name of the attribute
     * @return The value of the attribute retrieved
     */
    public static String read(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("attribute can't be NULL");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("attribute can't be empty");
        }
        if (!Manifests.exists(name)) {
            throw new IllegalArgumentException(
            	("Attribute '%s' not found in MANIFEST.MF file(s) among %d other attribute(s): %[list]s" + 
                    name + "---" + 
                    Manifests.DEFAULT.size() + "---" + 
                    new TreeSet<String>(Manifests.DEFAULT.keySet())
                )
            );
        }
        return Manifests.DEFAULT.get(name);
    }

    /**
     * Check whether attribute exists in any of {@code MANIFEST.MF} files.
     *
     * <p>Use this method before {@link #read(String)} to check whether an
     * attribute exists, in order to avoid a runtime exception.
     *
     * <p>The method is thread-safe.
     *
     * @param name Name of the attribute to check
     * @return Returns {@code TRUE} if it exists, {@code FALSE} otherwise
     */
    public static boolean exists(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("attribute name can't be NULL");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("attribute name can't be empty");
        }
        return Manifests.DEFAULT.containsKey(name);
    }

    /**
     * Append attributes from the web application {@code MANIFEST.MF}.
     *
     * <p>The method is deprecated. Instead, use this code:
     *
     * <pre>Manifests.DEFAULT.append(new ServletMfs());</pre>
     *
     * @param ctx Servlet context
     * @see #Manifests()
     * @throws IOException If some I/O problem inside
     * @deprecated Use {@link #append(Mfs)} and {@link ServletMfs} instead
     */
    @Deprecated
    public static void append(final ServletContext ctx) throws IOException {
        Manifests.DEFAULT.append(new ServletMfs(ctx));
    }

    /**
     * Append attributes from the file.
     *
     * <p>The method is deprecated. Instead, use this code:
     *
     * <pre>Manifests.DEFAULT.append(new FilesMfs(file));</pre>
     *
     * @param file The file to load attributes from
     * @throws IOException If some I/O problem inside
     * @deprecated Use {@link #append(Mfs)} and {@link FilesMfs} instead
     */
    @Deprecated
    public static void append(final File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("file can't be NULL");
        }
        Manifests.DEFAULT.append(new FilesMfs(file));
    }

    /**
     * Append attributes from input stream.
     *
     * <p>The method is deprecated. Instead, use this code:
     *
     * <pre>Manifests.DEFAULT.append(new StreamsMfs(stream));</pre>
     *
     * @param stream Stream to use
     * @throws IOException If some I/O problem inside
     * @since 0.8
     * @deprecated Use {@link #append(Mfs)} and {@link StreamsMfs} instead
     */
    @Deprecated
    public static void append(final InputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("input stream can't be NULL");
        }
        Manifests.DEFAULT.append(new StreamsMfs(stream));
    }

    /**
     * Load attributes from input stream.
     *
     * <p>Inside the method we catch {@code RuntimeException} (which may look
     * suspicious) in order to protect our execution flow from expected (!)
     * exceptions from {@link Manifest#getMainAttributes()}. For some reason,
     * this JDK method doesn't throw checked exceptions if {@code MANIFEST.MF}
     * file format is broken. Instead, it throws a runtime exception (an
     * unchecked one), which we should catch in such an inconvenient way.
     *
     * @param stream Stream to load from
     * @return The attributes loaded
     * @throws IOException If some problem happens
     * @since 0.8
     */
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private static Map<String, String> load(final InputStream stream)
        throws IOException {
        final ConcurrentMap<String, String> props =
            new ConcurrentHashMap<String, String>(0);
        try {
            final Manifest manifest = new Manifest(stream);
            final Attributes attrs = manifest.getMainAttributes();
            for (final Object key : attrs.keySet()) {
                final String value = attrs.getValue(
                    Attributes.Name.class.cast(key)
                );
                props.put(key.toString(), value);
            }
            logger.debug("%d attribute(s) loaded %[list]s" + "---" + 
                props.size() + "---" + new TreeSet<String>(props.keySet())
            );
        // @checkstyle IllegalCatch (1 line)
        } catch (final RuntimeException ex) {
        	logger.error(Manifests.class + "---" + "#load(): failed %[exception]s", ex);
        } finally {
            stream.close();
        }
        return props;
    }
	
}
