package ru.javafiddle.execution;

import ru.ncedu.dynamic.ByteArrayResource;
import ru.ncedu.dynamic.DynamicClassFileManager;
import ru.ncedu.dynamic.DynamicCompiler;
import ru.ncedu.dynamic.StringJavaFileObject;

import javax.tools.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
/**
 * Created by nk on 21.11.2015.
 */
public class JFiddleCompiler extends DynamicCompiler{

    public static final String DEFAULT_PACKAGE_PREFFIX  = ""; // For user sources
    //   public static final String DIRECTORY_PREFIX = "";
    private static final Logger LOG = Logger.getLogger(JFiddleCompiler.class);






    public void init(ClassLoader parentLoader, Map<String, byte[]> libs) {
        if (libs == null) {
            try {
                super.init(null);
            } catch (Exception e) {
                LOG.error("Exception", e);
            }
        }
        else {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            List<File> listFiles = new ArrayList<File>();
            for (Map.Entry entry : libs.entrySet()) {
                String pathToLib = (String) entry.getKey();
                byte[] libFile = (byte[]) entry.getValue();
                File file = new File(pathToLib);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    LOG.error("FileNotFoundException", e);
                }
                try {
                    fos.write(libFile);
                    fos.close();
                } catch (IOException e) {
                    LOG.error("IOException", e);
                }
                listFiles.add(file);
            }

            DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
            StandardJavaFileManager fman = compiler.getStandardFileManager(null, null, null);
            try {
                fman.setLocation(StandardLocation.CLASS_PATH, listFiles);
            } catch (IOException e) {
                LOG.error("IOException", e);
            }
            JavaFileManager manager = new DynamicClassFileManager(fman, parentLoader);
            setCollector(collector);
            setCompiler(compiler);
            setManager(manager);
        }
    }


    public String compile(Map<String[], byte[]> sources) {
        List<SimpleJavaFileObject> userSources = new ArrayList<>();

        for(Map.Entry entry: sources.entrySet()) {
            String[] packName = (String[]) entry.getKey();
            byte[] userFByte = (byte[]) entry.getValue();
            String userFile = "";
            for(int i = 0; i < userFByte.length; i++) {
                userFile += (char)userFByte[i];
            }
            String pack = packName[0];
            String className = packName[1];
            SimpleJavaFileObject sjf = null;
            if (pack != null) {
                sjf = counstructResource(className, userFile, pack);
            }
            else {
                sjf = counstructResource(className, userFile);
            }
            userSources.add(sjf);
        }

        String message = "";

        try {
            message = compileToClass(userSources);
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
        LOG.info(message);
        return message;
    }

    public Method findMain(Map<String[], byte[]> sources) {
        ClassLoader clazzLoader = getClassLoader();
        Class clazz = null;
        Method mainMeth = null;
        for(Map.Entry entry: sources.entrySet()) {
            String[] packName = (String[]) entry.getKey();
            String pack = packName[0];
            String classNamePlusEnd = packName[1];
            if (classNamePlusEnd.contains(".java")) {
                String className = classNamePlusEnd.substring(0, classNamePlusEnd.length() - 5); // delete .java
                try {
                    clazz = clazzLoader.loadClass(pack + className);
                    Method[] meth = clazz.getDeclaredMethods();
                    int i = 0;
                    while (i != meth.length) {
                        if (meth[i].getName().equals("main")) {
                            mainMeth = meth[i];
                        }
                        i++;
                    }
                } catch (ClassNotFoundException e) {
                    LOG.error("ClassNotFoundException", e);
                } catch (NullPointerException e) {
                    LOG.error("NullPoiterException", e);
                }
            }
        }
        return mainMeth;
    }

    public String compileAndRun(Map<String[], byte[]> sources) throws Exception{
        String message = compile(sources);
        Method mainMeth = findMain(sources);
        String[] arguments = {};
        mainMeth.invoke(null, (Object) arguments);
        return message;
    }

    public void run(Map<String[], byte[]> sources) throws InvocationTargetException, IllegalAccessException {
        Method mainMeth = findMain(sources);
        String[] arguments = {};
        try {
            mainMeth.invoke(null, (Object) arguments);
        } catch (NullPointerException e) {
            LOG.error("NullPointerException", e);
        }
    }

    public SimpleJavaFileObject counstructResource(String className, String source){
        return counstructResource(className, source, DEFAULT_PACKAGE_PREFFIX);
    }

    public static SimpleJavaFileObject counstructResource(String className, String source, String packagePreffix){
        if(className.lastIndexOf(".java") < 0){
            return new ByteArrayResource(className, source.getBytes());
        } else {
            return new StringJavaFileObject(packagePreffix + className.substring(0, className.lastIndexOf(".java")), source);
        }
    }
}
