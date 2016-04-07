package ru.javafiddle.core.ejb;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import ru.javafiddle.jpa.entity.File;
import ru.ncedu.dynamic.ByteArrayResource;
import ru.ncedu.dynamic.DynamicCompiler;
import ru.ncedu.dynamic.StringJavaFileObject;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by artyom on 15.12.15.
 */
@Stateless
@Named(value = "compileAndRunBean")
public class CompileAndRunBean extends DynamicCompiler {

    @PersistenceContext
    EntityManager em;


    public static final String DEFAULT_PACKAGE_PREFFIX = "";
    private static final Logger LOG = Logger.getLogger(CompileAndRunBean.class);

    public void init(ClassLoader parentLoader) {
        try {
            super.init(parentLoader);
        } catch (Exception e) {
            LOG.error("Exception", e);
        }
    }

    //!TODO
    public String compile(String projectHash) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(printStream);
        BasicConfigurator.configure();
        List<File> sources;
        FileBean fileBean = new FileBean();
        sources = fileBean.getProjectFiles(projectHash);
        try {
            init(ClassLoader.getSystemClassLoader());
        } catch (NullPointerException e) {
            LOG.error("NullPointerException in getSystemClassLoader()", e);
        }
        List<SimpleJavaFileObject> userSources = new ArrayList<>();
        for (int i = 0; i < sources.size(); i++) {
            File file = sources.get(i);
            // typeId = 8 - "class", typeId = 9 - "interface", typeId = 10 - "exception"
            // typeId = 5 - "runnable", typeId = 11 - "enum", typeId = 12 - "annotation"
            int typeId = file.getType().getTypeId();
            if (typeId == 8 || typeId == 9 || typeId == 10 || typeId == 5 || typeId == 11 || typeId == 12) {
                byte[] userFByte = file.getData();
                String userFile = "";
                for (int j = 0; j < userFByte.length; j++) {
                    userFile += (char) userFByte[j];
                }
                String s = findSrc(sources);
                String src = file.getPath();
                src = src.replaceFirst(s + "/", "");
                src = src.replace("/", ".");
                int pos = src.lastIndexOf(file.getFileName());
                String pack = src.substring(0, pos);
                String className = file.getFileName() + ".java";
                SimpleJavaFileObject sjf = null;
                if (pack != null) {
                    sjf = counstructResource(className, userFile, pack);
                } else {
                    sjf = counstructResource(className, userFile);
                }
                userSources.add(sjf);
            }
        }

        String message = "";

        try {
            message = compileToClass(userSources);
        } catch (Exception e) {
            LOG.error("Exception, can't compile", e);
        }
        LOG.info(message);
        System.out.flush();
        System.setOut(old);
        return baos.toString();
    }

    //!TODO
    public String run(String projectHash) {
        List<File> sources;
        FileBean fileBean = new FileBean();
        sources = fileBean.getProjectFiles(projectHash);
        String mes = "";
        if (!getMessage().equals("")) {
            Method mainMeth = findMain(sources, getClassLoader());
            String[] arguments = {};
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(baos);
                PrintStream old = System.out;
                System.setOut(printStream);
                mainMeth.invoke(null, (Object) arguments);
                System.out.flush();
                System.setOut(old);
                mes = baos.toString();
            } catch (NullPointerException e) {
                LOG.error(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                LOG.error(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return mes;
    }

    public Method findMain(List<File> sources, ClassLoader clazzLoader) {
        int count = 0;
        Class clazz = null;
        Method mainMeth = null;
        for (int i = 0; i < sources.size(); i++) {
            File file = sources.get(i);
            // typeId = 8 - "class", typeId = 9 - "interface", typeId = 10 - "exception"
            // typeId = 5 - "runnable", typeId = 11 - "enum", typeId = 12 - "annotation"
            int typeId = file.getType().getTypeId();
            if (typeId == 8 || typeId == 9 || typeId == 10 || typeId == 5 || typeId == 11 || typeId == 12) {
                String src = findSrc(sources);
                String packSource = file.getPath().replaceFirst(src, "").replace("/", ".").replaceFirst(".", "");
//                String pack = "";
//                if (!packSource.equals("")) {
//                    pack = packSource + ".";
//                }
                String className = packSource;//pack + file.getFileName();
                try {
                    clazz = clazzLoader.loadClass(className);
                    Method[] meth = clazz.getDeclaredMethods();
                    int j = 0;
                    while (meth != null && j != meth.length) {
                        if (meth[j].getName().equals("main")) {
                            mainMeth = meth[j];
                            count++;
                        }
                        j++;
                    }
                } catch (ClassNotFoundException e) {
                    LOG.error("ClassNotFoundException", e);
                } catch (NullPointerException e) {
                    LOG.error("NullPointerException", e);
                }
            }
        }

        if (count != 1) {
            try {
                throw new Exception();
            } catch (Exception e) {
                LOG.error("Too many main functions", e);
            }
            return null;
        } else {
            return mainMeth;
        }
    }

    public String findSrc(List<File> sources) {
        String src = "";
        for (int i = 0; i < sources.size(); i++) {
            File file = sources.get(i);
            if (file.getType().getTypeId() == 3) { //typeId = 3 - "sources"
                src = file.getPath();
            }
        }
        return src;
    }

    public SimpleJavaFileObject counstructResource(String className, String source) {
        return counstructResource(className, source, DEFAULT_PACKAGE_PREFFIX);
    }

    public static SimpleJavaFileObject counstructResource(String className, String source, String packagePreffix) {
        if (className.lastIndexOf(".java") < 0) {
            return new ByteArrayResource(className, source.getBytes());
        } else {
            return new StringJavaFileObject(packagePreffix + className.substring(0, className.lastIndexOf(".java")), source);
        }
    }
}
