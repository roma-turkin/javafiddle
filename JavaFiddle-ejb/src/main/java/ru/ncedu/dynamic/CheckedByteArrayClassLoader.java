/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.ncedu.dynamic;

import org.apache.log4j.Logger;

/**
 *
 * @author kharichkin
 */
public class CheckedByteArrayClassLoader extends ByteArrayClassLoader {
    
    private static final Logger LOG = Logger.getLogger(CheckedByteArrayClassLoader.class);

    public CheckedByteArrayClassLoader(ClassLoader parentLoader) throws Exception {
        super(parentLoader);
    }

    public void cleanUp() {
        ClassLoader parent = getParent();
        if(LOG.isTraceEnabled()){
                LOG.trace(this + ":parent=" + parent);
            }
        if (!(parent instanceof ByteArrayClassLoader)) {
            return;
        }
        ByteArrayClassLoader parentLoader = (ByteArrayClassLoader) parent;
        for (String klass : parentLoader.getLoadedClasses()) {
            if(LOG.isTraceEnabled()){
                LOG.trace("Removing class " + klass + " from classLoader " + this);
            }
            cache.remove(klass);
        }
        parentLoader.childLoader = this;
    }

//TODO: Implement checking classess accessibility    
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {       
        if(LOG.isTraceEnabled()){
                LOG.trace(this+": loadClass(String name):" + name);
            }
        return super.loadClass(name);
    }
    
}
