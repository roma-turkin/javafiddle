package ru.ncedu.dynamic;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.SecureClassLoader;
import java.util.*;
import org.apache.log4j.Logger;

public class ByteArrayClassLoader extends SecureClassLoader {

    private static final Logger LOG = Logger.getLogger(ByteArrayClassLoader.class);

    protected Map<String, ByteArrayJavaFileObject> cache = new HashMap<>();
    protected CheckedByteArrayClassLoader childLoader;

    protected Map<String, ByteArrayResource> resources = new HashMap<>();

    public ByteArrayClassLoader(ClassLoader loader) throws Exception {
        super(loader == null ? ByteArrayClassLoader.class.getClassLoader() : loader);
    }

    public void put(String name, ByteArrayResource obj) {
        resources.put(name, obj);
    }
    
    public void putAll(Map<String, ByteArrayResource> map){
        resources.putAll(map);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        ByteArrayResource resource = resources.get(name);
        if (resource != null) {
            return new ByteArrayInputStream(resource.contents);
        } else {
            return super.getResourceAsStream(name);
        }
    }

    public void put(String name, ByteArrayJavaFileObject obj) {
        ByteArrayJavaFileObject co = cache.get(name);
        if (co == null) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Putting to cache: " + name + "; " + obj);
            }
            cache.put(name, obj);
        }
    }

    public Set<String> getLoadedClasses() {
        return cache.keySet();
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        Class toReturn;
        if (LOG.isTraceEnabled()) {
            LOG.trace(this + ": loadClass(String name):" + name
                    + ";" + childLoader + ";" + cache.containsKey(name) + "; parent=" + getParent());
        }
        if (childLoader != null && childLoader.cache.containsKey(name)) {
            toReturn = childLoader.loadClass(name);
        } else {
            toReturn = super.loadClass(name);
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace(this + ": Loaded (" + name + ")=" + toReturn);
        }
        return toReturn;
    }

    public ClassLoader getChildClassLoader() {
        return childLoader;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
//            AccessController.checkPermission(new RuntimePermission("accessDeclaredMembers"));
        if (LOG.isTraceEnabled()) {
            LOG.trace(this + ": finding class:" + name);
        }
        Class<?> cls = null;
        try {
            ByteArrayJavaFileObject co = cache.get(name);
            if (co != null) {
                byte[] ba = co.getClassBytes();
                cls = super.defineClass(name, ba, 0, ba.length/*,
                 new CodeSource(new URL("http://ipccenter.ru/ByteArrayCL"), new Certificate[]{})*/);
            }
        } catch (Exception ex) {
            throw new ClassNotFoundException("Class name: " + name, ex);
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace(this + ": Found class:(" + name + ")=" + cls);
        }
        return cls;
    }
}
