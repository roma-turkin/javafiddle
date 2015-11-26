package ru.ncedu.dynamic;

import java.io.*;
import javax.tools.*;
import javax.tools.JavaFileObject.Kind;
import org.apache.log4j.Logger;

public class DynamicClassFileManager extends
		ForwardingJavaFileManager{
    private static final Logger LOG = Logger.getLogger(DynamicClassFileManager.class);
	private ByteArrayClassLoader loader = null;
	public DynamicClassFileManager(StandardJavaFileManager mgr, ClassLoader loader) {  // It was not public
		super(mgr);
		try {
			/* 2 */this.loader = loader == null ? new ByteArrayClassLoader(loader) : new CheckedByteArrayClassLoader(loader);
		} catch (Exception ex) {
			LOG.error("", ex);
		}
	}

	@Override
	/* 3 */public JavaFileObject getJavaFileForOutput(Location location,
			String name, Kind kind, FileObject sibling) throws IOException {
		ByteArrayJavaFileObject co = new ByteArrayJavaFileObject(name, kind);
		loader.put(name, co);
		return co;
	}

	@Override
	/* 4 */public ClassLoader getClassLoader(Location location) {
		return loader;
	}
}