package ru.javafiddle.core.ejb;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;
import ru.javafiddle.jpa.entity.File;
import ru.ncedu.dynamic.ByteArrayResource;
import ru.ncedu.dynamic.DynamicCompiler;
import ru.ncedu.dynamic.StringJavaFileObject;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by artyom on 15.12.15.
 */
@Stateless
@Named(value = "compileAndRunBean")
public class CompileAndRunBean extends DynamicCompiler {

    @PersistenceContext(name = "JFPersistenceUnit")
    EntityManager em;

    @EJB
    FileBean fileBean;


    public static final String DEFAULT_PACKAGE_PREFFIX = "";
    private static final Logger LOG = Logger.getLogger(CompileAndRunBean.class);

    public enum FileType {
        CLASS(8),
        INTERFACE(9),
        EXCEPTION(10),
        RUNNABLE(11),
        ENUM(12),
        ANNOTATION(13);

        private int type;

        FileType(int type) {
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public static boolean isFile(int type) {
            for (FileType fileType : FileType.values()) {
                if (fileType.getType() == type) {
                    return true;
                }
            }
            return false;
        }
    }

    private final int SOURCES = 3;



    public void init(ClassLoader parentLoader) throws Exception {
        super.init(parentLoader);
    }

    //!TODO
    public String compile(String projectHash) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(baos);
        synchronized (System.out) {
            PrintStream old = System.out;
            System.setOut(printStream);
            BasicConfigurator.configure();
            List<File> sources;
            //FileBean fileBean = new FileBean();
            sources = fileBean.getProjectFiles(projectHash);
            init(ClassLoader.getSystemClassLoader());
            List<SimpleJavaFileObject> userSources = constructResources(sources);
            String message = "";
            try {
                message = compileToClass(userSources);
            } catch (Exception e) {
                LOG.error("Exception, can't compile", e);
            }
            LOG.info(message);
            System.out.flush();
            System.setOut(old);
        }
        return baos.toString();
    }

    //!TODO
    public String run(String projectHash) {
        List<File> sources;
        //FileBean fileBean = new FileBean();
        sources = fileBean.getProjectFiles(projectHash);
        String mes = "";
        if (StringUtils.isEmpty(getMessage())) {
            Method mainMeth = findMain(sources, getClassLoader());
            String[] arguments = {};
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(baos);
                synchronized (System.out) {
                    PrintStream old = System.out;
                    System.setOut(printStream);
                    mainMeth.invoke(null, (Object) arguments);
                    System.out.flush();
                    System.setOut(old);
                }
                mes = baos.toString();
            } catch (InvocationTargetException | IllegalAccessException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return mes;
    }

    public Method findMain(List<File> sources, final ClassLoader clazzLoader) {
        int count = 0;
        Class clazz;
        Method mainMeth = null;
        for (int i = 0; i < sources.size(); i++) {
            File file = sources.get(i);
            int typeId = file.getType().getTypeId();
            if (FileType.isFile(typeId)) {
                String src = findSrc(sources);
                String packSource = constructPackage(file, src);
                String className = packSource + file.getFileName();
                try {
                    clazz = clazzLoader.loadClass(className);
                    Method[] meth = clazz.getDeclaredMethods();
                    int j = 0;
                    while (meth != null && j != meth.length) {
                        if (isMain(meth[j])) {
                            // we thought that there is only 1 "main" for the project
                            mainMeth = meth[j];
                        }
                        j++;
                    }
                } catch (ClassNotFoundException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        return mainMeth;
    }

    public boolean isMain(Method method) {
        if (method.getName().equals("main") && Modifier.isStatic(method.getModifiers())
                && Modifier.isPublic(method.getModifiers()) && method.getReturnType().equals(Void.TYPE)) {
            Type[] params = method.getGenericParameterTypes();
            if (params.length == 1) {
                if (params[0].getTypeName().equals("java.lang.String[]")) {
                    return true;
                }
            }
        }
        return false;
    }


    public String findSrc(List<File> sources) {
        String src = "";
        for (int i = 0; i < sources.size(); i++) {
            File file = sources.get(i);
            if (file.getType().getTypeId() == SOURCES) { //typeId = 3 - "sources"
                src = file.getPath();
            }
        }
        return src;
    }

    public List<SimpleJavaFileObject> constructResources(List<File> sources) {
        List<SimpleJavaFileObject> userSources = new ArrayList<>();
        for (int i = 0; i < sources.size(); i++) {
            File file = sources.get(i);
            int typeId = file.getType().getTypeId();
            if (FileType.isFile(typeId)) {
                byte[] userFByte = file.getData();
                String userFile = "";
                for (int j = 0; j < userFByte.length; j++) {
                    userFile += (char) userFByte[j];
                }
                String srcFolder = findSrc(sources);
                String pack = constructPackage(file, srcFolder);
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
        return userSources;
    }

    public String constructPackage(File file, String srcFolder) {
        String src = file.getPath();
        src = src.replaceFirst(srcFolder + "/", "");
        src = src.replace("/", ".");
        int pos = src.lastIndexOf(file.getFileName());
        String pack = src.substring(0, pos);
        return pack;
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
